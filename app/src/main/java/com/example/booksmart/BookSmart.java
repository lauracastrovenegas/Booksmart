package com.example.booksmart;

import android.app.Application;

import com.example.booksmart.models.Listing;
import com.example.booksmart.models.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class BookSmart extends Application {

    public static final String APP_ID = BuildConfig.APP_ID;
    public static final String CLIENT_KEY = BuildConfig.CLIENT_KEY;
    private static final String SERVER = BuildConfig.SERVER;

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Listing.class);
        ParseObject.registerSubclass(User.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APP_ID)
                .clientKey(CLIENT_KEY)
                .server(SERVER)
                .build()
        );
    }
}
