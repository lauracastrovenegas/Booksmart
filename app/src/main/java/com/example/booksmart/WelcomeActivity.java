package com.example.booksmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.example.booksmart.ui.welcome.LoginFragment;
import com.example.booksmart.ui.welcome.SignupFragment;
import com.example.booksmart.ui.welcome.WelcomeFragment;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class WelcomeActivity extends AppCompatActivity {

    public static final String TAG = "Welcome Activity!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if (ParseUser.getCurrentUser() != null){
            goMainActivity();
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.placeholder_activity_welcome, new WelcomeFragment());
        ft.commit();
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void goMainActivity(){
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}