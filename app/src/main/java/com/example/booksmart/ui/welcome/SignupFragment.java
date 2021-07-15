package com.example.booksmart.ui.welcome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.booksmart.Camera;
import com.example.booksmart.R;
import com.example.booksmart.WelcomeActivity;
import com.example.booksmart.ui.listings.ListingsFragment;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;

public class SignupFragment extends Fragment {

    public static final String TAG = "SignupFragment";
    public static final String CAMERA_FAILURE = "Picture wasn't taken!";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private static final int GET_FROM_GALLERY = 3;
    public static final int RESULT_OK = -1;
    public static final int IMAGE_PREVIEW_DIMENSION = 400;
    public static final String DATA_KEY = "data";
    public static final String PHOTO_NAME_SUFFIX = "_profile_photo.jpg";

    ImageView ivBack;
    EditText etName;
    EditText etSchool;
    EditText etEmail;
    EditText etUsername;
    EditText etPassword;
    Button btnSelectPhoto;
    Button btnCapturePhoto;
    ImageView ivProfilePhoto;
    Button btnSignUp;
    Camera camera;
    Bitmap selectedImage;
    String photoFileName;
    File photoFile;

    public SignupFragment() {}

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        ivBack = view.findViewById(R.id.ivSignupBack);
        etName = view.findViewById(R.id.etSignupName);
        etSchool = view.findViewById(R.id.etSignupSchool);
        etEmail = view.findViewById(R.id.etSignupEmail);
        etUsername = view.findViewById(R.id.etSignupUsername);
        etPassword = view.findViewById(R.id.etSignupPassword);
        btnSelectPhoto = view.findViewById(R.id.btnSignupSelect);
        btnCapturePhoto = view.findViewById(R.id.btnSignupCapture);
        ivProfilePhoto = view.findViewById(R.id.ivProfileImagePreview);
        btnSignUp = view.findViewById(R.id.btnCreateAccount);

        camera = new Camera(getContext(), getActivity());
        photoFileName = PHOTO_NAME_SUFFIX;

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goWelcomeFragment();
            }
        });

        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.onOpenGallery();
            }
        });

        btnCapturePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.onLaunchCamera(photoFileName);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

        return view;
    }

    private void signUpUser() {
        //TODO
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) { // User took image
                selectedImage = BitmapFactory.decodeFile(camera.getPhotoFile().getAbsolutePath());
                photoFile = camera.getPhotoFile();
            } else if (requestCode == GET_FROM_GALLERY){ // User selected an image
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                } catch (FileNotFoundException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }
            }

            // Load the taken image into a preview
            setImageViewDimensions(IMAGE_PREVIEW_DIMENSION,IMAGE_PREVIEW_DIMENSION, ivProfilePhoto);
            ivProfilePhoto.setImageBitmap(selectedImage);
        } else {
            Toast.makeText(getContext(), CAMERA_FAILURE, Toast.LENGTH_SHORT).show();
        }
    }

    private void setImageViewDimensions(int width, int height, ImageView image){
        image.requestLayout();
        image.getLayoutParams().height = height;
        image.getLayoutParams().width = width;
    }

    private void goWelcomeFragment(){
        ((WelcomeActivity) getActivity()).replaceFragment(new WelcomeFragment());
    }
}