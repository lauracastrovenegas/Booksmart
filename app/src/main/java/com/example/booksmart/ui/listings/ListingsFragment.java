package com.example.booksmart.ui.listings;

import android.os.Bundle;
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

import com.example.booksmart.helpers.EndlessRecyclerViewScrollListener;
import com.example.booksmart.helpers.ItemClickSupport;
import com.example.booksmart.R;
import com.example.booksmart.adapters.ListingAdapter;
import com.example.booksmart.models.Listing;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ListingsFragment extends Fragment {

    public static final String TAG = "ListingsFragment";
    public static final int GRID_SPAN = 2;
    public static final int LISTING_LIMIT = 20;
    public static final String DESCENDING_ORDER_KEY = "createdAt";
    public static final String KEY_SCHOOL = "school";
    public static final String QUERY_ERROR = "Error getting listings";
    public static final String KEY = "detail_listing";

    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    List<Listing> listings;
    RecyclerView rvListings;
    ListingAdapter adapter;
    GridLayoutManager gridLayoutManager;
    FloatingActionButton btnCompose;
    ProgressBar pb;

    private int skip;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_listings, container, false);

        skip = 0;

        btnCompose = view.findViewById(R.id.btnAddListing);
        listings = new ArrayList<>();
        adapter = new ListingAdapter(getContext(), listings);
        gridLayoutManager = new GridLayoutManager(getContext(), GRID_SPAN);
        rvListings = view.findViewById(R.id.rvListing);
        pb = view.findViewById(R.id.pbLoadingListings);

        rvListings.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);

        rvListings.setLayoutManager(gridLayoutManager);
        rvListings.setAdapter(adapter);

        setEndlessScrollListener();

        setSwipeContainer(view);

        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goListingForm();
            }
        });

        queryListings();

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
                        Listing listing = listings.get(position);
                        String listing_id = listing.getObjectId();

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
        );
    }

    private void queryListings() {
        ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);
        query.include(Listing.KEY_USER);
        // TODO: Add following when user login set up:
        //  query.whereEqualTo(KEY_SCHOOL, ParseUser.getCurrentUser().get("school"));
        query.setLimit(LISTING_LIMIT);
        query.addDescendingOrder(DESCENDING_ORDER_KEY);
        query.findInBackground(new FindCallback<Listing>() {

            @Override
            public void done(List<Listing> allListings, ParseException e) {
                if (e != null){
                    Log.e(TAG, QUERY_ERROR, e);
                    return;
                }

                // CLEAR OUT old items before appending in the new ones for refresh
                listings.clear();
                listings.addAll(allListings);
                adapter.notifyDataSetChanged();
                skip = listings.size() - 1;

                scrollListener.resetState();
                swipeContainer.setRefreshing(false);
                rvListings.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
            }
        });
    }

    // TODO fix double query at beginning
    private void queryMoreListings(){
        ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);
        query.include(Listing.KEY_USER);
        // TODO: Add following when user login set up:
        //  query.whereEqualTo(KEY_SCHOOL, ParseUser.getCurrentUser().get("school"));
        query.setSkip(skip);
        query.setLimit(LISTING_LIMIT);
        query.addDescendingOrder(DESCENDING_ORDER_KEY);
        query.findInBackground(new FindCallback<Listing>() {

            @Override
            public void done(List<Listing> allListings, ParseException e) {
                if (e != null){
                    Log.e(TAG, QUERY_ERROR, e);
                    return;
                }

                listings.addAll(allListings);
                adapter.notifyDataSetChanged();
                skip = listings.size() - 1;
            }
        });
    }

    private void setEndlessScrollListener() {
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, String.valueOf(page));
                queryMoreListings();
            }
        };

        rvListings.addOnScrollListener(scrollListener);
    }

    private void setSwipeContainer(View view){
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryListings();
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