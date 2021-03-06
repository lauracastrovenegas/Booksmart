package com.example.booksmart.models;

import android.text.format.DateFormat;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String USER_KEY = "user";
    public static final String BODY_KEY = "body";
    public static final String CONVO_KEY = "conversation";
    private static final String DATE_FORMAT = "MMMM dd, yyyy";
    private static final String TIME_FORMAT = "hh:mm";

    public ParseUser getUser() {
        return getParseUser(USER_KEY);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public Conversation getConversation(){
        return (Conversation) getParseObject(CONVO_KEY);
    }

    public void setUser(ParseUser user) {
        put(USER_KEY, user);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }

    public void setConversation(Conversation conversation){
        put(CONVO_KEY, conversation);
    }

    public String getCreatedAtDate(){
        return calculateTimeAgo(getCreatedAt());
    }

    public String getCreatedAtTime(){
        return calculateTimeAgoMinutes(getCreatedAt());
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

    public static String calculateTimeAgoMinutes(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < 24 * HOUR_MILLIS) {
                return DateFormat.format(TIME_FORMAT, createdAt).toString();
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
