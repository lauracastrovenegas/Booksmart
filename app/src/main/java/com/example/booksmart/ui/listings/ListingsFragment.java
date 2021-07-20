package com.example.booksmart.ui.listings;

import android.os.Bundle;
import android.renderscript.ScriptC;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.booksmart.BuildConfig;
import com.example.booksmart.helpers.EndlessRecyclerViewScrollListener;
import com.example.booksmart.helpers.ItemClickSupport;
import com.example.booksmart.R;
import com.example.booksmart.adapters.ListingAdapter;
import com.example.booksmart.models.Book;
import com.example.booksmart.models.Item;
import com.example.booksmart.models.Listing;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListingsFragment extends Fragment {

    public static final String TAG = "ListingsFragment";
    public static final int GRID_SPAN = 2;
    public static final int LISTING_LIMIT = 30;
    public static final String DESCENDING_ORDER_KEY = "createdAt";
    public static final String KEY_SCHOOL = "school";
    public static final String QUERY_ERROR = "Error getting listings";
    public static final String KEY = "detail_listing";
    public static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?fields=items(id,selfLink,volumeInfo,saleInfo)&printType=books&maxResults=" + String.valueOf(LISTING_LIMIT) + "&q=";
    public static final String DEFAULT_QUERY = "college+textbook";
    public static final String ITEMS_KEY = "items";

    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    List<Book> books;
    List<Item> items;
    RecyclerView rvListings;
    ListingAdapter adapter;
    GridLayoutManager gridLayoutManager;
    FloatingActionButton btnCompose;
    ProgressBar pb;
    RequestQueue queue;

    long startIndex;
    int skip;
    String currentUserSchool;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_listings, container, false);

        skip = 0;
        startIndex = 0;

        btnCompose = view.findViewById(R.id.btnAddListing);
        books = new ArrayList<>();
        items = new ArrayList<>();
        adapter = new ListingAdapter(getContext(), items);
        gridLayoutManager = new GridLayoutManager(getContext(), GRID_SPAN);
        rvListings = view.findViewById(R.id.rvListing);
        pb = view.findViewById(R.id.pbLoadingListings);

        rvListings.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);

        rvListings.setLayoutManager(gridLayoutManager);
        rvListings.setAdapter(adapter);

        queue = Volley.newRequestQueue(getContext());

        setEndlessScrollListener();

        setSwipeContainer(view);

        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goListingForm();
            }
        });

        onInitialLoad();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handles clicks for items in RecyclerView
        ItemClickSupport.addTo(rvListings).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Item item = items.get(position);
                        if (item.getType() == Item.TYPE_LISTING) {
                            String listing_id = ((Listing) item).getObjectId();

                            Fragment fragment = new ListingDetailFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(KEY, listing_id);
                            fragment.setArguments(bundle);

                            getFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in, R.anim.slide_out_left)
                                    .replace(R.id.nav_host_fragment_activity_main, fragment)
                                    .addToBackStack(null).commit();
                        }
                    }
                }
        );
    }

    public void onInitialLoad(){
        ParseUser user = ParseUser.getCurrentUser();

        user.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                currentUserSchool = user.getString(KEY_SCHOOL);
                queryListings(skip);
                fetchBooks(DEFAULT_QUERY, startIndex);
            }
        });
    }

    private void queryListings(int skipValue) {
        ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);
        query.include(Listing.KEY_USER);
        query.whereEqualTo(KEY_SCHOOL, currentUserSchool);
        query.setSkip(skipValue);
        query.setLimit(LISTING_LIMIT);
        query.addDescendingOrder(DESCENDING_ORDER_KEY);

        query.findInBackground(new FindCallback<Listing>() {

            @Override
            public void done(List<Listing> allListings, ParseException e) {
                if (e != null){
                    Log.e(TAG, QUERY_ERROR, e);
                    return;
                }

                Log.d(TAG, "queryListings()");

                if (skipValue == 0){
                    // CLEAR OUT old items before appending in the new ones for refresh
                    books.clear(); // temporary
                    items.clear();

                    items.addAll(allListings);
                    adapter.notifyDataSetChanged();

                    scrollListener.resetState();
                    swipeContainer.setRefreshing(false);
                    rvListings.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                } else {
                    items.addAll(allListings);
                    adapter.notifyDataSetChanged();
                }

                skip = skipValue + allListings.size();

                Log.d(TAG, "Total number of listings: " + String.valueOf(skip));
            }
        });
    }

    private void fetchBooks(String queryString, long start){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GOOGLE_BOOKS_URL + queryString + "&startIndex=" + String.valueOf(start), null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.d(TAG, "fetchBooks()");

                    books.addAll(Book.fromJsonArray(response.getJSONArray(ITEMS_KEY))); // temporary

                    items.addAll(Book.fromJsonArray(response.getJSONArray(ITEMS_KEY)));
                    startIndex = books.size();
                    adapter.notifyDataSetChanged();

                    Log.d(TAG, "Total number of books: " + String.valueOf(startIndex));

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString(), error);
            }
        });

        queue.add(jsonObjectRequest);
    }

    private void setEndlessScrollListener() {
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, String.valueOf(page));
                queryListings(skip);
                fetchBooks(DEFAULT_QUERY, startIndex);
            }
        };

        rvListings.addOnScrollListener(scrollListener);
    }

    private void setSwipeContainer(View view){
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryListings(0);
                fetchBooks(DEFAULT_QUERY, 0);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void goListingForm(){
        Fragment fragment = new ListingFormFragment();
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out_left);
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}