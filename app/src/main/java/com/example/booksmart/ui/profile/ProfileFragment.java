package com.example.booksmart.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.booksmart.R;
import com.example.booksmart.ui.MainActivity;
import com.example.booksmart.ui.WelcomeActivity;
import com.example.booksmart.adapters.HorizontalItemAdapter;
import com.example.booksmart.helpers.ItemClickSupport;
import com.example.booksmart.models.Item;
import com.example.booksmart.ui.listings.ListingDetailFragment;
import com.example.booksmart.viewmodels.ConversationsViewModel;
import com.example.booksmart.viewmodels.ListingDetailViewModel;
import com.example.booksmart.viewmodels.ProfileViewModel;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String KEY_NAME = "name";
    public static final String KEY_SCHOOL = "school";
    public static final String KEY_IMAGE = "image";
    private static final String TAG = "ProfileFragment";

    ProfileViewModel profileViewModel;
    ListingDetailViewModel listingDetailViewModel;
    ConversationsViewModel conversationsViewModel;
    ParseUser user;
    TextView tvUsername;
    ImageView ivUserProfilePhoto;
    TextView tvUserName;
    TextView tvUserSchool;
    TextView tvNoListingText;
    TextView tvNoFavorites;
    ImageButton ibOptions;
    RecyclerView rvListings;
    RecyclerView rvFavorites;
    HorizontalItemAdapter listingAdapter;
    HorizontalItemAdapter favoriteAdapter;
    LinearLayoutManager listingsLayoutManager;
    LinearLayoutManager favoritesLayoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tvToolbarTitleUsername);
        ivUserProfilePhoto = view.findViewById(R.id.ivProfileUserPhoto);
        tvUserName = view.findViewById(R.id.tvProfileUserName);
        tvUserSchool = view.findViewById(R.id.tvProfileSchoolName);
        tvNoListingText = view.findViewById(R.id.tvNoListingsText);
        tvNoFavorites = view.findViewById(R.id.tvNoFavoritesText);
        ibOptions = view.findViewById(R.id.ibToolbarOptionsProfile);
        rvListings = view.findViewById(R.id.rvProfileListings);
        rvFavorites = view.findViewById(R.id.rvProfileFavorites);

        user = ParseUser.getCurrentUser();

        listingAdapter = new HorizontalItemAdapter(getContext(), new ArrayList<>());
        rvListings.setAdapter(listingAdapter);
        listingsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvListings.setLayoutManager(listingsLayoutManager);

        favoriteAdapter = new HorizontalItemAdapter(getContext(), new ArrayList<>());
        rvFavorites.setAdapter(favoriteAdapter);
        favoritesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvFavorites.setLayoutManager(favoritesLayoutManager);

        setViewModels();

        tvUsername.setText(user.getUsername());
        tvUserName.setText(user.getString(KEY_NAME));
        tvUserSchool.setText(user.getString(KEY_SCHOOL));

        ParseFile profileImage = user.getParseFile(KEY_IMAGE);
        if (profileImage != null){
            Glide.with(getContext())
                    .load(profileImage.getUrl())
                    .override(400, 400)
                    .circleCrop()
                    .into(ivUserProfilePhoto);
        }

        ibOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSettingsFragment();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Click listener for items in the 'my listings' recycler view
        ItemClickSupport.addTo(rvListings).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Item item = profileViewModel.getListing(position);
                        listingDetailViewModel.setPreviousFragment(new ProfileFragment());
                        listingDetailViewModel.select(item);
                        goDetailView();
                    }
                }
        );

        // Click listener for items in the 'saved items' recycler view
        ItemClickSupport.addTo(rvFavorites).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Item item = profileViewModel.getFavorite(position);
                        listingDetailViewModel.setPreviousFragment(new ProfileFragment());
                        listingDetailViewModel.select(item);
                        goDetailView();
                    }
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setViewModels(){
        listingDetailViewModel = new ViewModelProvider(requireActivity()).get(ListingDetailViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        conversationsViewModel = new ViewModelProvider(requireActivity()).get(ConversationsViewModel.class);

        // set observer for profile view model listings
        profileViewModel.getListings().observe(getViewLifecycleOwner(), new Observer<List<Item>>(){
            @Override
            public void onChanged(List<Item> items) {
                listingAdapter = new HorizontalItemAdapter(getContext(), items);
                rvListings.setAdapter(listingAdapter);
                if (items.size() == 0){
                    rvListings.setVisibility(View.GONE);
                    tvNoListingText.setVisibility(View.VISIBLE);
                }
            }
        });

        // set observer for profile view model favorites
        profileViewModel.getFavorites().observe(getViewLifecycleOwner(), new Observer<List<Item>>(){
            @Override
            public void onChanged(List<Item> items) {
                favoriteAdapter.clear();
                favoriteAdapter.addAll(items.subList(0, profileViewModel.getItems().size()));
                favoriteAdapter.notifyDataSetChanged();
                Log.i(TAG, String.valueOf(favoriteAdapter.getItemCount()));
                if (items.size() == 0){
                    rvFavorites.setVisibility(View.GONE);
                    tvNoFavorites.setVisibility(View.VISIBLE);
                }
            }
        });

        conversationsViewModel.getNotification().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                ((MainActivity) getActivity()).setNotification(aBoolean);
            }
        });
    }

    private void goDetailView(){
        Fragment fragment = new ListingDetailFragment();
        replaceFragment(fragment);
    }

    private void goSettingsFragment(){
        Fragment fragment = new SettingsFragment();
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