package com.example.booksmart.ui.listings;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.booksmart.helpers.EndlessRecyclerViewScrollListener;
import com.example.booksmart.helpers.ItemClickSupport;
import com.example.booksmart.R;
import com.example.booksmart.adapters.ItemAdapter;
import com.example.booksmart.models.Book;
import com.example.booksmart.models.Item;
import com.example.booksmart.viewmodels.ListingDetailViewModel;
import com.example.booksmart.viewmodels.ListingsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ListingsFragment extends Fragment {

    public static final String TAG = "ListingsFragment";
    public static final int GRID_SPAN = 2;
    public static final String KEY_SCHOOL = "school";

    ListingsViewModel listingsViewModel;
    ListingDetailViewModel listingDetailViewModel;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    RecyclerView rvListings;
    ItemAdapter adapter;
    GridLayoutManager gridLayoutManager;
    FloatingActionButton btnCompose;
    ProgressBar pb;
    TextView toolbarTitleSchool;
    Boolean listRefreshed; // Indicates if fragment has just been created
    SearchView searchView;
    String currentSearchQuery;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_listings, container, false);

        btnCompose = view.findViewById(R.id.btnAddListing);
        rvListings = view.findViewById(R.id.rvListing);
        pb = view.findViewById(R.id.pbLoadingListings);
        toolbarTitleSchool = view.findViewById(R.id.tvToolbarTitleSchool);
        searchView = view.findViewById(R.id.svToolbarSearch);

        toolbarTitleSchool.setText(ParseUser.getCurrentUser().getString(KEY_SCHOOL));
        listRefreshed = true;
        currentSearchQuery = "";

        gridLayoutManager = new GridLayoutManager(getContext(), GRID_SPAN);
        rvListings.setLayoutManager(gridLayoutManager);

        adapter = new ItemAdapter(getContext(), new ArrayList<>());
        rvListings.setAdapter(adapter);

        setViewModels();
        setEndlessScrollListener();
        setSwipeContainer(view);

        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goListingForm();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query;
                listRefreshed = true;
                listingsViewModel.fetchItems(currentSearchQuery);
                rvListings.setVisibility(View.INVISIBLE);
                pb.setVisibility(View.VISIBLE);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        rvListings.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Click listener for items in the recycler view
        ItemClickSupport.addTo(rvListings).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        saveRecyclerViewState();
                        Item item = listingsViewModel.getItem(position);
                        listingDetailViewModel.select(item);
                        listingDetailViewModel.setPreviousFragment(new ListingsFragment());
                        goDetailView();
                    }
                }
        );
    }

    private void setViewModels(){
        listingDetailViewModel = new ViewModelProvider(requireActivity()).get(ListingDetailViewModel.class);
        listingsViewModel = new ViewModelProvider(requireActivity()).get(ListingsViewModel.class);

        // set observer for listings view model
        listingsViewModel.getItems().observe(getViewLifecycleOwner(), new Observer<List<Item>>(){
            @Override
            public void onChanged(List<Item> items) {
                // only instantiate adapter and set adapter for rv right after the fragment has been created
                // every time after that initial set up, just notify the adapter
                Log.i(TAG, "onChanged");
                Log.i(TAG, String.valueOf(listRefreshed));
                if (listRefreshed){
                    rvListings.scrollToPosition(0);
                    listRefreshed = false;
                }

                adapter.clear();
                adapter.addAll(items);
                adapter.notifyDataSetChanged();
                Log.i(TAG, String.valueOf(items.size()));
                scrollListener.setLoading(false);
                rvListings.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
            }
        });
    }

    private void setEndlessScrollListener() {
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore()");
                scrollListener.setLoading(true);
                listRefreshed = false;
                listingsViewModel.fetchMoreItems(currentSearchQuery);
            }
        };

        rvListings.addOnScrollListener(scrollListener);
        scrollListener.setLoading(false);
    }

    private void setSwipeContainer(View view){
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listingsViewModel.resetList();
                swipeContainer.setRefreshing(false);
                scrollListener.resetState();
                scrollListener.setLoading(false);
                listingsViewModel.setRecyclerViewState(null);
                listRefreshed = true;
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void saveRecyclerViewState(){
        Parcelable recylerViewState = rvListings.getLayoutManager().onSaveInstanceState();
        listingsViewModel.setRecyclerViewState(recylerViewState);
    }

    private void goListingForm(){
        Fragment fragment = new ListingFormFragment();
        replaceFragment(fragment);
    }

    private void goDetailView(){
        Fragment fragment = new ListingDetailFragment();
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