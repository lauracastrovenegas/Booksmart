package com.example.booksmart;

import android.app.Application;

import com.example.booksmart.models.Conversation;
import com.example.booksmart.models.Favorite;
import com.example.booksmart.models.Listing;
import com.example.booksmart.models.Message;
import com.example.booksmart.models.User;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import java.util.ArrayList;

public class BookSmart extends Application {

    public static final String APP_ID = BuildConfig.APP_ID;
    public static final String CLIENT_KEY = BuildConfig.CLIENT_KEY;
    private static final String SERVER = BuildConfig.SERVER;

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Listing.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Conversation.class);
        ParseObject.registerSubclass(Favorite.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APP_ID)
                .clientKey(CLIENT_KEY)
                .server(SERVER)
                .build()
        );

        ArrayList<String> channels = new ArrayList<>();
        channels.add("News");

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", BuildConfig.SENDER_ID);
        installation.put("channels", channels);
        installation.saveInBackground();
    }
}
