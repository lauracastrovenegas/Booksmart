package com.example.booksmart.ui.listings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.booksmart.R;
import com.example.booksmart.models.Listing;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListingFormFragment extends Fragment {

    public static final String TAG = "ListingFragmentForm";
    public static final String EMPTY_FIELD = "All fields must be complete!";
    public static final String NO_IMAGE = "There is no image!";
    public static final String SAVING_ERROR = "Error while saving";

    EditText etTitle;
    EditText etDescription;
    EditText etCourse;
    EditText etPrice;
    Button btnSelectPhoto;
    Button btnCapturePhoto;
    Button btnPost;
    ImageView ivCloseForm;
    ImageView ivImage;
    File photoFile;


    public ListingFormFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_listing_form, container, false);

        etTitle = view.findViewById(R.id.etListingTitle);
        etDescription = view.findViewById(R.id.etListingDescription);
        etCourse = view.findViewById(R.id.etListingCourse);
        etPrice = view.findViewById(R.id.etListingPrice);
        btnSelectPhoto = view.findViewById(R.id.btnListingSelect);
        btnCapturePhoto = view.findViewById(R.id.btnListingCapture);
        btnPost = view.findViewById(R.id.btnListingPost);
        ivCloseForm = view.findViewById(R.id.ivPostClose);
        ivImage = view.findViewById(R.id.ivListingImagePreview);

        btnCapturePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera();
            }
        });

        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPost();
            }
        });

        ivCloseForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goListingTimeline();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void onPost() {
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String price = etPrice.getText().toString();
        String course = etCourse.getText().toString();

        if (title.isEmpty() || description.isEmpty() || price.isEmpty() || course.isEmpty()){
            Toast.makeText(getContext(), EMPTY_FIELD, Toast.LENGTH_SHORT).show();
            return;
        }

        if (photoFile ==  null || ivImage.getDrawable() == null){
            Toast.makeText(getContext(), NO_IMAGE, Toast.LENGTH_SHORT).show();
            return;
        }

        ParseUser currentUser = ParseUser.getCurrentUser();
        /*ParseUser currentUser = new ParseUser();
        currentUser.put("username", "test_user");
        currentUser.put("password", "test_user");
        currentUser.put("name", "test user");*/
        saveListing(title, description, price, course, photoFile, currentUser);
    }

    private void saveListing(String title, String description, String price, String course, File photoFile, ParseUser currentUser) {
        Listing listing = new Listing();
        listing.setTitle(title);
        listing.setDescription(description);
        listing.setPrice(Integer.parseInt(price));
        listing.setCourse(course);
        listing.setImage(new ParseFile(photoFile));
        List<ParseFile> images = new ArrayList<>();
        images.add(new ParseFile((photoFile)));
        listing.setUser(currentUser);

        listing.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, SAVING_ERROR, e);
                    Toast.makeText(getContext(), SAVING_ERROR, Toast.LENGTH_SHORT).show();
                }

                etTitle.setText("");
                etDescription.setText("");
                etCourse.setText("");
                etPrice.setText("");
                ivImage.setImageResource(0);
                goListingTimeline();
            }
        });

    }

    private void onLaunchCamera() {
        //TODO
    }

    private void goListingTimeline(){
        Fragment fragment = new ListingsFragment();
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}