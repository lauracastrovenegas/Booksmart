package com.example.booksmart.ui.listings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.booksmart.Client;
import com.example.booksmart.helpers.EndlessRecyclerViewScrollListener;
import com.example.booksmart.helpers.ItemClickSupport;
import com.example.booksmart.R;
import com.example.booksmart.adapters.ListingAdapter;
import com.example.booksmart.models.Item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ListingsFragment extends Fragment {

    public static final String TAG = "ListingsFragment";
    public static final int GRID_SPAN = 2;

    ListingDetailViewModel listingDetailViewModel;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    List<Item> items;
    RecyclerView rvListings;
    ListingAdapter adapter;
    GridLayoutManager gridLayoutManager;
    FloatingActionButton btnCompose;
    ProgressBar pb;
    Client client;

    long startIndex;
    int skip;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_listings, container, false);

        btnCompose = view.findViewById(R.id.btnAddListing);
        rvListings = view.findViewById(R.id.rvListing);
        pb = view.findViewById(R.id.pbLoadingListings);

        skip = 0;
        startIndex = 0;
        listingDetailViewModel = new ViewModelProvider(requireActivity()).get(ListingDetailViewModel.class);
        items = new ArrayList<>();
        adapter = new ListingAdapter(getContext(), items);
        gridLayoutManager = new GridLayoutManager(getContext(), GRID_SPAN);
        rvListings.setLayoutManager(gridLayoutManager);
        rvListings.setAdapter(adapter);

        setEndlessScrollListener();
        setSwipeContainer(view);
        setClient();

        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goListingForm();
            }
        });

        rvListings.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        scrollListener.setLoading(true);

        client.onInitialLoad();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ItemClickSupport.addTo(rvListings).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Item item = items.get(position);
                        listingDetailViewModel.select(item);
                        goDetailView();
                    }
                }
        );
    }

    private void setClient(){
        client = new Client(getContext(), getActivity()) {
            @Override
            public void onDone(List<Item> itemList) {
                items.addAll(itemList);
                adapter.notifyDataSetChanged();

                if (skip == 0){
                    scrollListener.resetState();
                    rvListings.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                }

                skip = client.getCurrentSkip();
                startIndex = client.getCurrentStart();
                scrollListener.setLoading(false);
                swipeContainer.setRefreshing(false);
            }
        };
    }

    private void setEndlessScrollListener() {
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                scrollListener.setLoading(true);
                client.fetchItems(skip, startIndex);
            }
        };

        rvListings.addOnScrollListener(scrollListener);
    }

    private void setSwipeContainer(View view){
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scrollListener.setLoading(true);
                items.clear();
                client.fetchItems(0,0);
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