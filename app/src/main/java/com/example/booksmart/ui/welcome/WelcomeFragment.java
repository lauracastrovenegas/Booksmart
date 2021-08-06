package com.example.booksmart.ui.welcome;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.example.booksmart.R;

public class WelcomeFragment extends Fragment {

    Button btnLogin;
    Button btnSignup;

    public WelcomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        Window window = getActivity().getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.purple_500));

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
        replaceFragment(new LoginFragment());
    }

    private void goSignupFragment(){
        replaceFragment(new SignupFragment());
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out_left);
        transaction.replace(R.id.placeholder_activity_welcome, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}