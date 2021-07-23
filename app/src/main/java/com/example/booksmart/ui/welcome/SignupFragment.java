package com.example.booksmart.ui.welcome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.booksmart.Camera;
import com.example.booksmart.R;
import com.example.booksmart.WelcomeActivity;
import com.example.booksmart.data.Colleges;
import com.example.booksmart.models.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SignupFragment extends Fragment {

    public static final String TAG = "SignupFragment";
    public static final String CAMERA_FAILURE = "Picture wasn't taken!";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private static final int GET_FROM_GALLERY = 3;
    public static final int RESULT_OK = -1;
    public static final int IMAGE_PREVIEW_DIMENSION = 400;
    public static final String PHOTO_NAME_SUFFIX = "_profile_photo.jpg";
    public static final String NAME_KEY = "name";
    public static final String SCHOOL_KEY = "school";
    private static final String SIGN_UP_FAILURE = "Unable to create account for user!";
    private static final String ERROR_SAVING_IMAGE = "Could not save image uploaded. Please try again!";
    public static final String LOGIN_FAILURE = "Unable to login. ";
    public static final String EMPTY_FIELDS = "Oops, you forgot to fill in some fields!";
    private static final String USERNAME_TAKEN_MSG = "Sorry, that username is already taken.";
    public static final String EMAIL_TAKEN_MSG = "An account already exists for that email.";

    ImageView ivBack;
    EditText etName;
    AutoCompleteTextView tvSchool;
    ArrayAdapter<String> spinnerArrayAdapter;
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
    ProgressBar pb;
    Colleges colleges;
    List<String> collegesList;

    public SignupFragment() {}

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        ivBack = view.findViewById(R.id.ivSignupBack);
        etName = view.findViewById(R.id.etSignupName);
        tvSchool = view.findViewById(R.id.etSignupSchool);
        etEmail = view.findViewById(R.id.etSignupEmail);
        etUsername = view.findViewById(R.id.etSignupUsername);
        etPassword = view.findViewById(R.id.etSignupPassword);
        btnSelectPhoto = view.findViewById(R.id.btnSignupSelect);
        btnCapturePhoto = view.findViewById(R.id.btnSignupCapture);
        ivProfilePhoto = view.findViewById(R.id.ivProfileImagePreview);
        btnSignUp = view.findViewById(R.id.btnCreateAccount);
        pb = view.findViewById(R.id.pbLoadingSignup);

        colleges = new Colleges(getContext());
        collegesList = colleges.getColleges();
        camera = new Camera(getContext(), getActivity());
        photoFileName = PHOTO_NAME_SUFFIX;
        setAutoCompleteTextView();

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
                onSignUp();
            }
        });

        return view;
    }

    private void setAutoCompleteTextView() {
        spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, collegesList);
        tvSchool.setAdapter(spinnerArrayAdapter);
    }

    private void onSignUp(){
        pb.setVisibility(View.VISIBLE);

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String email = etEmail.getText().toString();
        String name = etName.getText().toString();
        String school = tvSchool.getText().toString();

        if (isAnyStringNullOrEmpty(username, password, name, email, school)) {
            pb.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), EMPTY_FIELDS, Toast.LENGTH_SHORT).show();
            return;
        }

        saveImageToParse();
    }

    private void saveImageToParse(){
        if (photoFile != null) {
            ParseFile photo = new ParseFile(photoFile);
            photo.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Toast.makeText(getContext(), ERROR_SAVING_IMAGE, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, ERROR_SAVING_IMAGE, e);
                        return;
                    }

                    signUpUser(photo);
                }
            });
        } else {
            signUpUser(null);
        }
    }

    private void signUpUser(ParseFile savedImage) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String email = etEmail.getText().toString();
        String name = etName.getText().toString();
        String school = tvSchool.getText().toString();

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put(NAME_KEY, name);
        user.put(SCHOOL_KEY, school);

        if (savedImage != null) {
            user.setImage(savedImage);
        }

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    pb.setVisibility(View.INVISIBLE);
                    switch (e.getCode()){
                        case ParseException.USERNAME_TAKEN:
                            Toast.makeText(getContext(), USERNAME_TAKEN_MSG, Toast.LENGTH_SHORT).show();
                            break;
                        case ParseException.EMAIL_TAKEN:
                            Toast.makeText(getContext(), EMAIL_TAKEN_MSG, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(getContext(), SIGN_UP_FAILURE,Toast.LENGTH_SHORT).show();
                            Log.e(TAG, e.getMessage(), e);
                            break;
                    }
                    return;
                }

                loginUser(username, password);
            }
        });
    }

    private void loginUser(String username, String password){
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                pb.setVisibility(View.INVISIBLE);
                if (e != null){
                    Toast.makeText(getContext(), LOGIN_FAILURE + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                ((WelcomeActivity) getActivity()).goMainActivity();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) { // User took image
                try {
                    selectedImage = BitmapFactory.decodeFile(camera.getPhotoFile().getAbsolutePath());
                    photoFile = camera.scaleImage(selectedImage, Camera.SCALE_WIDTH);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    public void goWelcomeFragment(){
        replaceFragment(new WelcomeFragment());
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out);
        transaction.replace(R.id.placeholder_activity_welcome, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static boolean isAnyStringNullOrEmpty(String... strings) {
        for (String s : strings)
            if (s == null || s.isEmpty())
                return true;
        return false;
    }
}