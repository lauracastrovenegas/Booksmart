package com.example.booksmart;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

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

    Context context;
    Activity activity;
    RequestQueue queue;
    List<Item> items;
    int skip;
    long startIndex;
    String currentUserSchool;

    public Client(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
        queue = Volley.newRequestQueue(context);
        items = new ArrayList<>();
        skip = 0;
        startIndex = 0;
    }

    public void onInitialLoad(){
        ParseUser user = ParseUser.getCurrentUser();
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
                            startIndex = start + newBooks.size() + 1;

                            onDone(items);
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString(), error);
                error.printStackTrace();
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
}
