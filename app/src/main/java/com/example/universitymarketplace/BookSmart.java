package com.example.universitymarketplace;

import android.app.Application;

import com.parse.Parse;

public class BookSmart extends Application {

    public static final String APP_ID = "6vGUdLaWSp7zQlmDSGEmeUnCsoYt06MBxvhX6osT";
    public static final String CLIENT_KEY = "KAx4fGemEK0wGFjKYkLWW9uAeqb2vAdAbF1LYxSQ";
    private static final String SERVER = "https://parseapi.back4app.com";

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APP_ID)
                .clientKey(CLIENT_KEY)
                .server(SERVER)
                .build()
        );
    }
}
