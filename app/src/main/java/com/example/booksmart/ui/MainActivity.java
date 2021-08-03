package com.example.booksmart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.booksmart.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    public static final int REMOVE_REQUEST = 202;
    BadgeDrawable badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navView.setItemIconTintList(null);
        NavigationUI.setupWithNavController(navView, navController);

        badge = navView.getOrCreateBadge(R.id.navigation_conversation);
        badge.setBackgroundColor(getResources().getColor(R.color.orange_500));
        badge.setVisible(false);

        // prevent toolbar from hiding when keyboard opens
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void setNotification(Boolean visible){
        badge.setVisible(visible);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            for (Fragment childFragment : fragment.getChildFragmentManager().getFragments()){
                childFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

}