package com.example.booksmart.ui.welcome;

import android.content.Intent;
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

import com.example.booksmart.MainActivity;
import com.example.booksmart.R;
import com.example.booksmart.WelcomeActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginFragment extends Fragment {

    private static final String TAG = "loginFragment";
    public static final String SUCCESS_MSG = "Welcome back!";
    public static final String NO_USERNAME_MSG = "Username field is empty.";
    public static final String NO_PASSWORD_MSG = "Password field is empty.";
    public static final String INVALID_LOGIN_MSG = "Invalid username/password";
    public static final String LOGIN_ISSUE_MSG = "Issue with login. Please try again.";
    public static final String EMPTY_STRING = "";

    ImageView ivBack;
    EditText etUsername;
    EditText etPassword;
    Button btnSignin;
    ProgressBar pb;

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
                loginUser(username, password);
            }
        });

        return view;
    }

    private void loginUser(String username, String password) {
        if (username.isEmpty() || username == null){
            pb.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), NO_USERNAME_MSG, Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty() || password == null){
            pb.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), NO_PASSWORD_MSG, Toast.LENGTH_SHORT).show();
        } else {
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    pb.setVisibility(View.INVISIBLE);

                    if (e != null) {
                        switch (e.getCode()) {
                            case ParseException.USERNAME_MISSING:
                                Toast.makeText(getContext(), NO_USERNAME_MSG, Toast.LENGTH_SHORT).show();
                                break;
                            case ParseException.PASSWORD_MISSING:
                                Toast.makeText(getContext(), NO_PASSWORD_MSG, Toast.LENGTH_SHORT).show();
                                break;
                            case ParseException.OBJECT_NOT_FOUND:
                                Toast.makeText(getContext(), INVALID_LOGIN_MSG, Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(getContext(), LOGIN_ISSUE_MSG, Toast.LENGTH_SHORT).show();
                                break;
                        }

                        Log.d(TAG, LOGIN_ISSUE_MSG + e.getMessage(), e);
                        return;
                    }

                    ((WelcomeActivity) getActivity()).goMainActivity();

                    Toast.makeText(getContext(), SUCCESS_MSG, Toast.LENGTH_SHORT).show();
                }
            });
        }
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
}