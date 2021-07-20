package com.example.booksmart.ui.listings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.booksmart.R;
import com.example.booksmart.helpers.DeviceDimensionsHelper;
import com.example.booksmart.models.Item;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.example.booksmart.models.Listing;

import org.jetbrains.annotations.NotNull;

public class ListingDetailFragment extends Fragment {

    public static final String KEY = "detail_listing";
    public static final String FAIL_MSG = "Failed to retrieve listing";
    public static final String TAG = "ListingDetailFragment";
    
    ImageView ivImage;
    ImageView ivUserProfileImage;
    TextView tvTitle;
    TextView tvPrice;
    TextView tvUserUsername;
    ImageView ivClose;
    Button btnCourse;
    TextView tvDescription;
    Button btnMessageSeller;
    ProgressBar progressBar;

    public ListingDetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_listing_detail, container, false);

        ivImage = itemView.findViewById(R.id.ivListingImageDetail);
        ivUserProfileImage = itemView.findViewById(R.id.ivListingUserDetail);
        tvTitle = itemView.findViewById(R.id.tvListingTitleDetail);
        tvPrice = itemView.findViewById(R.id.tvListingPriceDetail);
        tvUserUsername = itemView.findViewById(R.id.tvListingUserDetail);
        btnCourse = itemView.findViewById(R.id.btnListingCourse);
        tvDescription = itemView.findViewById(R.id.tvListingDescription);
        btnMessageSeller = itemView.findViewById(R.id.btnMessageSeller);
        ivClose = itemView.findViewById(R.id.ivClose);
        progressBar = itemView.findViewById(R.id.pbListingDetail);

        progressBar.setVisibility(View.VISIBLE);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTimeline();
            }
        });

        return itemView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListingsViewModel listingsViewModel = new ViewModelProvider(requireActivity()).get(ListingsViewModel.class);
        listingsViewModel.getSelected().observe(getViewLifecycleOwner(), item -> {
            if (item.getType() == Item.TYPE_LISTING){
                ParseUser user = ((Listing) item).getParseUser("user");

                try {
                    user = user.fetchIfNeeded();
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }

                ParseFile image = ((Listing) item).getParseFile(Listing.KEY_IMAGE);
                if (image != null){
                    int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getContext());

                    Glide.with(getContext())
                            .load(image.getUrl())
                            .override(screenWidth,800)
                            .centerCrop()
                            .into(ivImage);
                }

                ParseFile profileImage = ((Listing) item).getParseUser(Listing.KEY_USER).getParseFile(Listing.KEY_IMAGE);
                if (profileImage != null){
                    Glide.with(getContext())
                            .load(profileImage.getUrl())
                            .circleCrop()
                            .into(ivUserProfileImage);
                }

                tvTitle.setText(((Listing) item).getString(Listing.KEY_TITLE));
                tvPrice.setText("$" + String.valueOf(((Listing) item).getInt(Listing.KEY_PRICE)));
                tvUserUsername.setText(((Listing) item).getParseUser(Listing.KEY_USER).getUsername());
                btnCourse.setText(((Listing) item).getString(Listing.KEY_COURSE));
                tvDescription.setText(((Listing) item).getString(Listing.KEY_DESCRIPTION));

                progressBar.setVisibility(View.INVISIBLE);
            } else {
                // TODO update the UI for google book item
            }
        });
    }

    private void goTimeline(){
        Fragment fragment = new ListingsFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out);
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}