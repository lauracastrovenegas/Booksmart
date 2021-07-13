package com.example.booksmart.ui.listings;

import android.content.Intent;
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
import android.widget.Toast;

import com.example.booksmart.BuildConfig;
import com.example.booksmart.R;
import com.example.booksmart.models.Listing;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
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
    public static final int RESULT_OK           = -1;

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

        photoFileName = "photo.jpg";
        // TODO: uncomment when user implemented ->
        //photoFileName = ParseUser.getCurrentUser().getUsername() + LocalDate.now().toString() + "_photo.jpg";

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
        // create intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        photoFile = getPhotoFileUri(photoFileName);

        // wrap file object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // Load the taken image into a preview
                ivImage.setImageBitmap(takenImage);
            } else {
                Toast.makeText(getContext(), CAMERA_FAILURE, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, FAILURE_MSG);
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
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