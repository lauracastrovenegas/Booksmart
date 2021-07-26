package com.example.booksmart.helpers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.booksmart.models.Book;
import com.example.booksmart.models.Item;
import com.example.booksmart.models.Listing;
import com.example.booksmart.models.User;
import com.example.booksmart.ui.WelcomeActivity;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class ParseClient {

    public static final String TAG = "Parse Client";
    public static final int LISTING_LIMIT = 15;
    public static final String DESCENDING_ORDER_KEY = "createdAt";
    public static final String KEY_SCHOOL = "school";
    public static final String QUERY_ERROR = "Error getting listings";
    public static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?fields=items(id,volumeInfo,saleInfo)&printType=books&maxResults=" + String.valueOf(LISTING_LIMIT) + "&q=";
    public static final String DEFAULT_QUERY = "college+textbook";
    public static final String ITEMS_KEY = "items";
    public static final String SAVING_ERROR = "Error while saving";
    private static final String ERROR_SAVING_IMAGE = "Could not save image uploaded. Please try again!";
    public static final String NAME_KEY = "name";
    private static final String SIGN_UP_FAILURE = "Unable to create account for user!";
    public static final String LOGIN_FAILURE = "Unable to login. ";
    public static final String EMPTY_FIELDS = "Oops, you forgot to fill in some fields!";
    private static final String USERNAME_TAKEN_MSG = "Sorry, that username is already taken.";
    public static final String EMAIL_TAKEN_MSG = "An account already exists for that email.";

    Context context;
    List<Item> items;
    ParseUser user;
    int listingSkip;
    int skip;
    String currentUserSchool;

    public ParseClient(Context context){
        this.context = context;
        items = new ArrayList<>();
        skip = 0;
    }

    public void signUpUser(ParseFile savedImage, String username, String password, String email, String name, String school) {
        Log.i(TAG, "signUpUser()");
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put(NAME_KEY, name);
        user.put(KEY_SCHOOL, school);

        if (savedImage != null) {
            user.setImage(savedImage);
        }

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    switch (e.getCode()){
                        case ParseException.USERNAME_TAKEN:
                            Toast.makeText(context, USERNAME_TAKEN_MSG, Toast.LENGTH_SHORT).show();
                            break;
                        case ParseException.EMAIL_TAKEN:
                            Toast.makeText(context, EMAIL_TAKEN_MSG, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(context, SIGN_UP_FAILURE,Toast.LENGTH_SHORT).show();
                            Log.e(TAG, e.getMessage(), e);
                            break;
                    }
                    return;
                }

                loginUser(username, password);
            }
        });
    }

    public void loginUser(String username, String password){
        Log.i(TAG, "loginUser()");
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null){
                    Toast.makeText(context, LOGIN_FAILURE + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                onUserLoggedIn();
            }
        });
    }

    public abstract void onUserLoggedIn();

    public void getUserSchool(int skipValue, long startIndexValue){
        user = ParseUser.getCurrentUser();
        user.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                currentUserSchool = user.getString(KEY_SCHOOL);
                onUserFetched(user);
            }
        });
    }

    public abstract void onUserFetched(ParseUser user);

    public void queryUserListings(int skipValue, ParseUser user) {
        user.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);
                query.include(Listing.KEY_USER);
                query.whereEqualTo(Listing.KEY_USER, user);
                query.setSkip(skipValue);
                query.addDescendingOrder(DESCENDING_ORDER_KEY);

                query.findInBackground(new FindCallback<Listing>() {

                    @Override
                    public void done(List<Listing> allListings, ParseException e) {
                        if (e != null){
                            Log.e(TAG, QUERY_ERROR, e);
                            return;
                        }

                        listingSkip = skipValue + allListings.size();
                        List<Item> newList = new ArrayList<>();
                        newList.addAll(allListings);
                        onQueryListingsDone(newList);
                    }
                });
            }
        });
    }

    public void queryListings(int skipValue) {
        ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);
        query.include(Listing.KEY_USER);
        query.whereEqualTo(KEY_SCHOOL, currentUserSchool);
        query.setSkip(skipValue);
        query.setLimit(LISTING_LIMIT);
        query.addDescendingOrder(DESCENDING_ORDER_KEY);

        query.findInBackground(new FindCallback<Listing>() {

            @Override
            public void done(List<Listing> allListings, ParseException e) {
                if (e != null){
                    Log.e(TAG, QUERY_ERROR, e);
                    return;
                }

                if (skipValue == 0){
                    items.clear();
                }

                items.addAll(allListings);
                skip = skipValue + allListings.size();
            }
        });
    }

    public int getCurrentSkip(){
        return skip;
    }

    public void saveImageToParse(File photoFile){
        Log.i(TAG, "saveImageToParse()");
        if (photoFile != null) {
            ParseFile photo = new ParseFile(photoFile);
            photo.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Toast.makeText(context, ERROR_SAVING_IMAGE, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, ERROR_SAVING_IMAGE, e);
                        return;
                    }

                    onParseImageSaved(photo);
                }
            });
        } else {
            onParseImageSaved(null);
        }
    }

    public void saveListing(String title, String description, String price, String course, ParseFile photoFile, ParseUser currentUser) {
        Listing listing = new Listing();
        listing.setTitle(title);
        listing.setDescription(description);
        listing.setPrice(Integer.parseInt(price));
        listing.setCourse(course);
        listing.setImage(photoFile);
        listing.setUser(currentUser);
        listing.setSchool(currentUser.getString(KEY_SCHOOL));

        listing.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, SAVING_ERROR, e);
                    Toast.makeText(context, SAVING_ERROR, Toast.LENGTH_SHORT).show();
                }

                onListingSaved(listing);
            }
        });
    }

    public abstract void onListingSaved(Listing listing);

    public abstract void onQueryListingsDone(List<Item> items);

    public abstract void onParseImageSaved(ParseFile image);
}