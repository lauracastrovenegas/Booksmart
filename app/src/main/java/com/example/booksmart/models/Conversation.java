package com.example.booksmart.models;

import android.text.format.DateFormat;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ParseClassName("Conversation")
public class Conversation extends ParseObject {
    public static final String USERS_KEY = "users";
    public static final String LISTING_KEY = "listing";
    private static final String DATE_FORMAT = "MMMM dd, yyyy";

    public List<ParseUser> getUsers() {
        return (ArrayList) get(USERS_KEY);
    }

    public Listing getListing(){
        return (Listing) getParseObject(LISTING_KEY);
    }

    public void setUsers(ParseUser user1, ParseUser user2) {
        List<ParseObject> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        put(USERS_KEY, users);
    }

    public void setListing(Listing listing){
        put(LISTING_KEY, listing);
    }

    public String getCreatedAtDate(){
        return calculateTimeAgo(getCreatedAt());
    }

    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return DateFormat.format(DATE_FORMAT, createdAt).toString();
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }

}
