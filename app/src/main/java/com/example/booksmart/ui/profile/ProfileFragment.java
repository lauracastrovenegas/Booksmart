package com.example.booksmart.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.booksmart.MainActivity;
import com.example.booksmart.R;
import com.example.booksmart.WelcomeActivity;
import com.example.booksmart.adapters.ListingAdapter;
import com.example.booksmart.databinding.FragmentProfileBinding;
import com.example.booksmart.models.Item;
import com.example.booksmart.ui.listings.ListingsViewModel;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;

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
    RecyclerView rvListings;
    RecyclerView rvResources;
    ListingAdapter listingAdapter;
    LinearLayoutManager listingsLayoutManager;
    LinearLayoutManager resourcesLayoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnLogout = view.findViewById(R.id.btnLogout);
        tvUsername = view.findViewById(R.id.tvToolbarTitleUsername);
        ivUserProfilePhoto = view.findViewById(R.id.ivProfileUserPhoto);
        tvUserName = view.findViewById(R.id.tvProfileUserName);
        tvUserSchool = view.findViewById(R.id.tvProfileSchoolName);
        rvListings = view.findViewById(R.id.rvProfileListings);
        rvResources = view.findViewById(R.id.rvProfileResources);

        user = ParseUser.getCurrentUser();
        listingAdapter = new ListingAdapter(getContext(), new ArrayList<Item>());
        listingsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvListings.setAdapter(listingAdapter);
        rvListings.setLayoutManager(listingsLayoutManager);

        resourcesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvResources.setLayoutManager(resourcesLayoutManager);

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

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void goWelcomeActivity(){
        Intent intent = new Intent(getContext(), WelcomeActivity.class);
        getActivity().finish();
        startActivity(intent);
    }
}