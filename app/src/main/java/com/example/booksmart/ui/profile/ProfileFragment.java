package com.example.booksmart.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.booksmart.R;
import com.example.booksmart.WelcomeActivity;
import com.example.booksmart.adapters.HorizontalItemAdapter;
import com.example.booksmart.models.Item;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String KEY_NAME = "name";
    public static final String KEY_SCHOOL = "school";
    public static final String KEY_IMAGE = "image";

    ProfileViewModel profileViewModel;
    ParseUser user;
    Button btnLogout;
    TextView tvUsername;
    ImageView ivUserProfilePhoto;
    TextView tvUserName;
    TextView tvUserSchool;
    TextView tvNoListingText;
    ImageButton ibOptions;
    RecyclerView rvListings;
    HorizontalItemAdapter listingAdapter;
    LinearLayoutManager listingsLayoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnLogout = view.findViewById(R.id.btnLogout);
        tvUsername = view.findViewById(R.id.tvToolbarTitleUsername);
        ivUserProfilePhoto = view.findViewById(R.id.ivProfileUserPhoto);
        tvUserName = view.findViewById(R.id.tvProfileUserName);
        tvUserSchool = view.findViewById(R.id.tvProfileSchoolName);
        tvNoListingText = view.findViewById(R.id.tvNoListingsText);
        ibOptions = view.findViewById(R.id.ibToolbarOptionsProfile);
        rvListings = view.findViewById(R.id.rvProfileListings);

        user = ParseUser.getCurrentUser();

        listingAdapter = new HorizontalItemAdapter(getContext(), new ArrayList<>());
        rvListings.setAdapter(listingAdapter);
        listingsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvListings.setLayoutManager(listingsLayoutManager);

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

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                goWelcomeActivity();
            }
        });

        ibOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (btnLogout.getVisibility()){
                    case View.INVISIBLE:
                        btnLogout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        btnLogout.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setViewModels(){
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        // set observer for profile view model
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
    }

    public void goWelcomeActivity(){
        Intent intent = new Intent(getContext(), WelcomeActivity.class);
        getActivity().finish();
        startActivity(intent);
    }
}