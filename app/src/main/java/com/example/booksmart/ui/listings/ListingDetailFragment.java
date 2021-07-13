package com.example.booksmart.ui.listings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.booksmart.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.example.booksmart.models.Listing;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListingDetailFragment extends Fragment {

    public static final String KEY = "detail_listing";
    public static final String FAIL_MSG = "Failed to retrieve listing";
    public static final String TAG = "ListingDetailFragment";
    private static final String DATE_FORMAT = "MMMM dd, yyyy";

    ParseObject listing;
    String listingId;
    ImageView ivImage;
    ImageView ivUserProfileImage;
    TextView tvTitle;
    TextView tvPrice;
    TextView tvUserUsername;
    ImageView ivClose;
    Button btnCourse;
    TextView tvDescription;
    Button btnMessageSeller;

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

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            listingId = bundle.getString(KEY); // Key
        }

        readObject(listingId);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ListingsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        return itemView;
    }

    public void readObject(String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Listing");
        query.include("description");
        query.include("image");
        query.include("createdAt");

        // The query will search for a ParseObject, given its objectId.
        // When the query finishes running, it will invoke the GetCallback
        // with either the object, or the exception thrown
        query.getInBackground(objectId, (object, e) -> {
            if (e == null) {

                listing = (Listing) object;

                ParseUser user = listing.getParseUser("user");

                try {
                    user = user.fetchIfNeeded();
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }

                /*Iterator<String> keys = user.keySet().iterator();

                while(keys.hasNext()) {
                    String key = keys.next();
                    Log.i(TAG, key);
                }*/

                Log.i(TAG, user.toString());
                tvTitle.setText(listing.getString(Listing.KEY_TITLE));
                tvPrice.setText("$" + String.valueOf(listing.getInt(Listing.KEY_PRICE)));
                tvUserUsername.setText(listing.getParseUser(Listing.KEY_USER).getUsername());
                btnCourse.setText(listing.getString(Listing.KEY_COURSE));
                tvDescription.setText(listing.getString(Listing.KEY_DESCRIPTION));

                ParseFile image = listing.getParseFile(Listing.KEY_IMAGE);
                if (image != null){
                    Glide.with(getContext())
                            .load(image.getUrl())
                            .override(500,500)
                            .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(50)))
                            .into(ivImage);
                }

                ParseFile profileImage = listing.getParseUser(Listing.KEY_USER).getParseFile(Listing.KEY_IMAGE);
                if (profileImage != null){
                    Glide.with(getContext())
                            .load(profileImage.getUrl())
                            .circleCrop()
                            .into(ivUserProfileImage);
                }
            } else {
                // something went wrong
                Log.e(TAG, FAIL_MSG, e);
            }
        });
    }
}