package com.example.booksmart;

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
import com.example.booksmart.helpers.ParseClient;
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

public abstract class ItemRepository {

    public static final String TAG = "Listings Client";
    public static final int LISTING_LIMIT = 15;
    public static final String KEY_SCHOOL = "school";
    public static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?fields=items(id,volumeInfo,saleInfo)&printType=books&maxResults=" + String.valueOf(LISTING_LIMIT) + "&q=";
    public static final String DEFAULT_QUERY = "college+textbook";
    public static final String ITEMS_KEY = "items";

    Context context;
    RequestQueue queue;
    ParseClient parseClient;
    List<Item> items;
    ParseUser currentUser;
    int listingSkip;
    int skip;
    long startIndex;
    String userSchool;
    String title;
    String description;
    String price;
    String course;
    Boolean isInitialFetch;

    public ItemRepository(Context context){
        this.context = context;
        setParseClient();
        queue = Volley.newRequestQueue(context);
        items = new ArrayList<>();
        skip = 0;
        startIndex = 0;
        isInitialFetch = false;
    }

    private void setParseClient() {
        parseClient = new ParseClient(context) {
            @Override
            public void onUserLoggedIn() {}

            @Override
            public void onUserFetched(ParseUser user) {
                Log.i(TAG, "onUserFetched()");
                currentUser = user;
                userSchool = user.getString(KEY_SCHOOL);
                if (isInitialFetch){
                    fetchItems(0,0);
                }
            }

            @Override
            public void onQueryUserListingsDone(List<Listing> allListings, ParseException e) {
                listingSkip = listingSkip + allListings.size();
                List<Item> newList = new ArrayList<>();
                newList.addAll(allListings);
                onAllItemsFetched(newList);
            }

            @Override
            public void onQueryListingsDone(List<Listing> allListings, ParseException e) {
                Log.i(TAG, "onQueryListingsDone()");
                if (e != null){
                    Log.e(TAG, QUERY_ERROR, e);
                    fetchBooks(DEFAULT_QUERY, startIndex);
                    return;
                }

                if (skip == 0){
                    items.clear();
                    startIndex = 0;
                }

                items.addAll(allListings);
                skip = skip + allListings.size();

                fetchBooks(DEFAULT_QUERY, startIndex);
            }

            @Override
            public void onParseImageSaved(ParseFile image) {
                parseClient.saveListing(title, description, price, course, image, ParseUser.getCurrentUser());
            }

            @Override
            public void onListingSaved(Listing listing) {
                listingSaved(listing);
            }
        };
    }

    public void onInitialLoad(){
        Log.i(TAG, "onInitialLoad()");
        isInitialFetch = true;
        parseClient.getCurrentUser();
    }

    public void fetchItems(int skipValue, long startIndexValue){
        Log.i(TAG, "fetchItems()");
        items.clear();
        startIndex = startIndexValue;
        parseClient.queryListings(skipValue);
    }

    public void queryUserListings(int skipValue, ParseUser user) {
        parseClient.queryUserListings(skipValue, user);
    }

    private void fetchBooks(String queryString, long start){
        Log.i(TAG, "fetchBooks()");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GOOGLE_BOOKS_URL + queryString + "&startIndex=" + String.valueOf(start), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            List<Book> newBooks = Book.fromJsonArray(response.getJSONArray(ITEMS_KEY));
                            items.addAll(newBooks);
                            startIndex = start + newBooks.size() + 2;

                            onAllItemsFetched(items);
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString(), error);
                onAllItemsFetched(items);
            }
        });

        queue.add(jsonObjectRequest);
    }

    public void onPostListing(String title, String description, String price, String course, File photoFile){
        this.title = title;
        this.description = description;
        this.price = price;
        this.course = course;
        parseClient.saveImageToParse(photoFile);
    }

    public int getCurrentSkip(){
        return skip;
    }

    public long getCurrentStart(){
        return startIndex;
    }

    public abstract void onAllItemsFetched(List<Item> items);

    public abstract void listingSaved(Listing listing);
}
