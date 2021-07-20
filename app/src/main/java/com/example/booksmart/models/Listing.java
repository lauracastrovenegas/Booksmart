package com.example.booksmart.models;

import android.text.format.DateFormat;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

@ParseClassName("Listing")
public class Listing extends ParseObject implements Item {

    public static final String KEY_USER = "user";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PRICE = "price";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_ALL_IMAGES = "images";
    public static final String KEY_COURSE = "course";
    public static final String KEY_SCHOOL = "school";

    private static final String DATE_FORMAT = "MMMM dd, yyyy";

    public String getId(){
        return getObjectId();
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public String getTitle(){
        return getString(KEY_TITLE);
    }

    public void setTitle(String title){
        put(KEY_TITLE, title);
    }

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public int getPrice(){
        return getInt(KEY_PRICE);
    }

    public void setPrice(int price){
        put(KEY_PRICE, price);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image){
        put(KEY_IMAGE, image);
    }

    public List<ParseFile> getAllImages(){
        return getList(KEY_ALL_IMAGES);
    }

    public void addImage(ParseFile image){
        add(KEY_ALL_IMAGES, image);
    }

    public void addAllImages(List<ParseFile> images){
        for (int i = 0; i < images.size(); i++){
            add(KEY_ALL_IMAGES, images.get(i));
        }
    }

    public String getCourse(){
        return getString(KEY_COURSE);
    }

    public void setCourse(String course){
        put(KEY_COURSE, course);
    }

    public String getSchool(){
        return getString(KEY_SCHOOL);
    }

    public void setSchool(String school){
        put(KEY_SCHOOL, school);
    }

    public String getCreatedAtDate(Listing listing){
        return calculateTimeAgo(listing.getCreatedAt());
    }

    public int getType(){
        return Item.TYPE_LISTING;
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
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
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
