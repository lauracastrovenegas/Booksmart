package com.example.booksmart.ui.listings;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.example.booksmart.adapters.ListingAdapter;
import com.example.booksmart.models.Item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseUser;

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
    ListingAdapter adapter;
    GridLayoutManager gridLayoutManager;
    FloatingActionButton btnCompose;
    ProgressBar pb;
    TextView toolbarTitleSchool;
    TextView toolbarTitlePage;
    Boolean fragmentRecreated; // Indicates if fragment has just been created

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_listings, container, false);

        btnCompose = view.findViewById(R.id.btnAddListing);
        rvListings = view.findViewById(R.id.rvListing);
        pb = view.findViewById(R.id.pbLoadingListings);
        toolbarTitleSchool = view.findViewById(R.id.tvToolbarTitleSchool);
        toolbarTitlePage = view.findViewById(R.id.tvToolbarTitlePage);

        toolbarTitleSchool.setText(ParseUser.getCurrentUser().getString(KEY_SCHOOL));

        fragmentRecreated = true;

        gridLayoutManager = new GridLayoutManager(getContext(), GRID_SPAN);
        rvListings.setLayoutManager(gridLayoutManager);

        setViewModels();
        setEndlessScrollListener();
        setSwipeContainer(view);

        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goListingForm();
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
                        goDetailView();
                    }
                }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveRecyclerViewState();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveRecyclerViewState();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveRecyclerViewState();
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
                if (fragmentRecreated){
                    adapter = new ListingAdapter(getContext(), items);
                    rvListings.setAdapter(adapter);
                    scrollListener.resetState();

                    fragmentRecreated = false;
                    rvListings.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
                scrollListener.setLoading(false);
            }
        });

        // restore state of recycler view if any saved in ListingsViewModel
        if (listingsViewModel.getRecyclerViewState() != null){
            rvListings.getLayoutManager().onRestoreInstanceState(listingsViewModel.getRecyclerViewState());
        }
    }

    private void setEndlessScrollListener() {
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                scrollListener.setLoading(true);
                listingsViewModel.fetchMoreItems();
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