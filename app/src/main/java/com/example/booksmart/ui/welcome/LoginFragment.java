package com.example.booksmart.ui.welcome;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.booksmart.R;
import com.example.booksmart.helpers.ParseClient;
import com.example.booksmart.models.Item;
import com.example.booksmart.models.Listing;
import com.example.booksmart.ui.WelcomeActivity;
import com.parse.LogInCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class LoginFragment extends Fragment {

    private static final String TAG = "loginFragment";
    public static final String SUCCESS_MSG = "Welcome back!";
    public static final String NO_USERNAME_MSG = "Username field is empty.";
    public static final String NO_PASSWORD_MSG = "Password field is empty.";

    ImageView ivBack;
    EditText etUsername;
    EditText etPassword;
    Button btnSignin;
    ProgressBar pb;
    ParseClient parseClient;

    public LoginFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ivBack = view.findViewById(R.id.ivLoginBack);
        etUsername = view.findViewById(R.id.etLoginUsername);
        etPassword = view.findViewById(R.id.etLoginPassword);
        btnSignin = view.findViewById(R.id.btnSignIn);
        pb = view.findViewById(R.id.pbLoadingLogin);

        setParseClient();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goWelcomeFragment();
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                onLogin(username, password);
            }
        });

        return view;
    }

    private void onLogin(String username, String password) {
        if (username.isEmpty() || username == null){
            pb.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), NO_USERNAME_MSG, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty() || password == null){
            pb.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), NO_PASSWORD_MSG, Toast.LENGTH_SHORT).show();
            return;
        }

        parseClient.loginUser(username, password);
    }

    private void setParseClient() {
        parseClient = new ParseClient(getContext()) {
            @Override
            public void onUserLoggedIn() {
                pb.setVisibility(View.INVISIBLE);
                ((WelcomeActivity) getActivity()).goMainActivity();
                Toast.makeText(getContext(), SUCCESS_MSG, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserFetched(ParseUser user) {}

            @Override
            protected void onQueryUserListingsDone(List<Listing> allListings, ParseException e) {

            }

            @Override
            public void onListingSaved(Listing listing) {}

            @Override
            public void onQueryListingsDone(List<Listing> items, ParseException e) {}

            @Override
            public void onParseImageSaved(ParseFile image) {}
        };
    }

    private void goWelcomeFragment(){
        replaceFragment(new WelcomeFragment());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out);
        transaction.replace(R.id.placeholder_activity_welcome, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}