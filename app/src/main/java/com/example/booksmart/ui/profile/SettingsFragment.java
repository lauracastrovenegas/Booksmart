package com.example.booksmart.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.booksmart.R;
import com.example.booksmart.ui.WelcomeActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class SettingsFragment extends Fragment {

    ImageView ivClose;
    Button btnLogout;
    ImageView ivUserProfileImage;
    TextView tvUserName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        ivClose = view.findViewById(R.id.ivCloseSettings);
        btnLogout = view.findViewById(R.id.btnLogout);
        ivUserProfileImage = view.findViewById(R.id.profileCircleImageView);
        tvUserName = view.findViewById(R.id.usernameTextView);

        ParseUser user = ParseUser.getCurrentUser();
        try {
            user = user.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tvUserName.setText(user.getString("name"));
        ParseFile image = user.getParseFile("image");
        Glide.with(getContext())
                .load(image.getUrl())
                .circleCrop()
                .into(ivUserProfileImage);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goProfileFragment();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                goWelcomeActivity();
            }
        });

        return view;
    }

    public void goWelcomeActivity(){
        Intent intent = new Intent(getContext(), WelcomeActivity.class);
        getActivity().finish();
        startActivity(intent);
    }

    private void goProfileFragment(){
        Fragment fragment = new ProfileFragment();
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out);
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}