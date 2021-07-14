package com.example.booksmart.ui.welcome;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.booksmart.MainActivity;
import com.example.booksmart.R;
import com.example.booksmart.WelcomeActivity;
import com.example.booksmart.ui.listings.ListingsFragment;

public class WelcomeFragment extends Fragment {

    Button btnLogin;
    Button btnSignup;

    public WelcomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        btnLogin = view.findViewById(R.id.btnLogin);
        btnSignup = view.findViewById(R.id.btnSignup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               goLoginFragment();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSignupFragment();
            }
        });

        return view;
    }

    private void goLoginFragment(){
        ((WelcomeActivity) getActivity()).replaceFragment(new LoginFragment());
    }

    private void goSignupFragment(){
        ((WelcomeActivity) getActivity()).replaceFragment(new SignupFragment());
    }


}