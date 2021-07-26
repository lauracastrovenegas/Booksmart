package com.example.booksmart.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;
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
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class Client {

    public static final String TAG = "Listings Client";
    public static final int LISTING_LIMIT = 15;
    public static final String DESCENDING_ORDER_KEY = "createdAt";
    public static final String KEY_SCHOOL = "school";
    public static final String QUERY_ERROR = "Error getting listings";
    public static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?fields=items(id,volumeInfo,saleInfo)&printType=books&maxResults=" + String.valueOf(LISTING_LIMIT) + "&q=";
    public static final String DEFAULT_QUERY = "college+textbook";
    public static final String ITEMS_KEY = "items";
    public static final String SAVING_ERROR = "Error while saving";
    private static final String ERROR_SAVING_IMAGE = "Could not save image uploaded. Please try again!";

    Context context;
    RequestQueue queue;
    List<Item> items;
    ParseUser user;
    int listingSkip;
    int skip;
    long startIndex;
    String currentUserSchool;

    public Client(Context context){
        this.context = context;
        queue = Volley.newRequestQueue(context);
        items = new ArrayList<>();
        skip = 0;
        startIndex = 0;
    }

    public void onInitialLoad(){
        user = ParseUser.getCurrentUser();
        user.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                currentUserSchool = user.getString(KEY_SCHOOL);
                fetchItems(0, 0);
            }
        });
    }

    public void fetchItems(int skipValue, long startIndexValue){
        items.clear();
        queryListings(skipValue, startIndexValue);
    }

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
                        onDone(newList);
                    }
                });
            }
        });
    }

    public void queryListings(int skipValue, long startIndexValue) {
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
                    fetchBooks(DEFAULT_QUERY, startIndexValue);
                    return;
                }

                if (skipValue == 0){
                    items.clear();
                    startIndex = 0;
                }

                items.addAll(allListings);
                skip = skipValue + allListings.size();
                fetchBooks(DEFAULT_QUERY, startIndexValue);
            }
        });
    }

    private void fetchBooks(String queryString, long start){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GOOGLE_BOOKS_URL + queryString + "&startIndex=" + String.valueOf(start), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            List<Book> newBooks = Book.fromJsonArray(response.getJSONArray(ITEMS_KEY));
                            items.addAll(newBooks);
                            startIndex = start + newBooks.size() + 2;

                            onDone(items);
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString(), error);
                onDone(items);
            }
        });

        queue.add(jsonObjectRequest);
    }

    public int getCurrentSkip(){
        return skip;
    }

    public long getCurrentStart(){
        return startIndex;
    }

    public abstract void onDone(List<Item> items);

    public void onPostListing(String title, String description, String price, String course, File photoFile){
        saveImageToParse(title, description, price, course, photoFile);
    }

    private void saveImageToParse(String title, String description, String price, String course, File photoFile){
        ParseFile photo = new ParseFile(photoFile);
        photo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Toast.makeText(context, ERROR_SAVING_IMAGE, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.getMessage(), e);
                    return;
                }

                saveListing(title, description, price, course, photo, user);
            }
        });
    }

    private void saveListing(String title, String description, String price, String course, ParseFile photoFile, ParseUser currentUser) {
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
}
