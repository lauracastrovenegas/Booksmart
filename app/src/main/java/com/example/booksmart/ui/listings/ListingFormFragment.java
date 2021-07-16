package com.example.booksmart.ui.listings;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.booksmart.BuildConfig;
import com.example.booksmart.Camera;
import com.example.booksmart.R;
import com.example.booksmart.helpers.BitmapScaler;
import com.example.booksmart.models.Listing;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListingFormFragment extends Fragment {

    public static final String TAG = "ListingFragmentForm";
    public static final String EMPTY_FIELD = "All fields must be complete!";
    public static final String NO_IMAGE = "There is no image!";
    public static final String SAVING_ERROR = "Error while saving";
    public static final String SUCCESS_MSG = "Success!";
    public static final String FAILURE_MSG = "Failure: ";
    public static final String CAMERA_FAILURE = "Picture wasn't taken!";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private static final int GET_FROM_GALLERY = 3;
    public static final int RESULT_OK = -1;
    public static final int IMAGE_PREVIEW_DIMENSION = 400;
    public static final String DATA_KEY = "data";
    public static final String PHOTO_NAME_SUFFIX = "_photo.jpg";
    private static final String ERROR_SAVING_IMAGE = "Could not save image to parse";
    public static final String BLANK = "";

    public String photoFileName;
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
    Camera camera;
    Bitmap selectedImage;
    String title;
    String description;
    String price;
    String course;
    ParseUser currentUser;
    ProgressBar pb;

    public ListingFormFragment() {}

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        pb = view.findViewById(R.id.pbLoading);

        currentUser = ParseUser.getCurrentUser();
        camera = new Camera(getContext(), getActivity());
        photoFileName = ParseUser.getCurrentUser().getUsername() + LocalDate.now().toString() + PHOTO_NAME_SUFFIX;

        btnCapturePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.onLaunchCamera(photoFileName);
            }
        });

        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.onOpenGallery();
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
        title = etTitle.getText().toString();
        description = etDescription.getText().toString();
        price = etPrice.getText().toString();
        course = etCourse.getText().toString();

        if (title.isEmpty() || description.isEmpty() || price.isEmpty() || course.isEmpty()){
            Toast.makeText(getContext(), EMPTY_FIELD, Toast.LENGTH_SHORT).show();
            return;
        }

        if (photoFile == null || ivImage.getDrawable() == null){
            Toast.makeText(getContext(), NO_IMAGE, Toast.LENGTH_SHORT).show();
            return;
        }

        saveImageToParse();
    }

    private void saveImageToParse(){
        pb.setVisibility(ProgressBar.VISIBLE);

        ParseFile photo = new ParseFile(photoFile);
        photo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, ERROR_SAVING_IMAGE, e);
                } else {
                    saveListing(title, description, price, course, photo, currentUser);
                }
            }
        });
    }

    private void saveListing(String title, String description, String price, String course, ParseFile photoFile, ParseUser currentUser) {
        Listing listing = new Listing();
        listing.setTitle(title);
        listing.setDescription(description);
        listing.setPrice(Integer.parseInt(price));
        listing.setCourse(course);
        listing.setImage(photoFile);
        listing.setUser(currentUser);

        listing.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, SAVING_ERROR, e);
                    Toast.makeText(getContext(), SAVING_ERROR, Toast.LENGTH_SHORT).show();
                }

                etTitle.setText(BLANK);
                etDescription.setText(BLANK);
                etCourse.setText(BLANK);
                etPrice.setText(BLANK);
                ivImage.setImageResource(0);
                pb.setVisibility(ProgressBar.INVISIBLE);
                goListingTimeline();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) { // User took image
                selectedImage = BitmapFactory.decodeFile(camera.getPhotoFile().getAbsolutePath());
                photoFile = camera.getPhotoFile();
            } else if (requestCode == GET_FROM_GALLERY){ // User selected an image
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                    photoFile = camera.scaleImage(selectedImage, Camera.SCALE_WIDTH);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }
            }

            // Load the taken image into a preview
            setImageViewDimensions(IMAGE_PREVIEW_DIMENSION,IMAGE_PREVIEW_DIMENSION, ivImage);
            ivImage.setImageBitmap(selectedImage);
        } else {
            Toast.makeText(getContext(), CAMERA_FAILURE, Toast.LENGTH_SHORT).show();
        }
    }

    private void goListingTimeline(){
        Fragment fragment = new ListingsFragment();
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out);
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setImageViewDimensions(int width, int height, ImageView image){
        image.requestLayout();
        image.getLayoutParams().height = height;
        image.getLayoutParams().width = width;
    }
}