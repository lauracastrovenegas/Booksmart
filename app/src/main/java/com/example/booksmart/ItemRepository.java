package com.example.booksmart;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.booksmart.helpers.GoogleBooksClient;
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

public class ItemRepository {

    public static final String TAG = "Listings Client";
    public static final int LISTING_LIMIT = 15;
    public static final String KEY_SCHOOL = "school";
    public static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?fields=items(id,volumeInfo,saleInfo)&printType=books&maxResults=" + String.valueOf(LISTING_LIMIT) + "&q=";
    public static final String DEFAULT_QUERY = "college+textbook";
    public static final String ITEMS_KEY = "items";

    Context context;
    GoogleBooksClient googleClient;
    ParseClient parseClient;
    MutableLiveData<List<Item>> items;
    List<Item> itemList;
    int skip;
    long startIndex;
    String title;
    String description;
    String price;
    String course;

    public ItemRepository(Context context){
        this.context = context;
        setParseClient();
        setGoogleClient();
        itemList = new ArrayList<>();
        items = new MutableLiveData<>();
        skip = 0;
        startIndex = 0;

        fetchItems(0, 0, "");
    }

    private void setGoogleClient() {
        googleClient = new GoogleBooksClient(context){
            @Override
            public void onBooksFetched(List<Book> newBooks) {
                Log.i(TAG, "onBooksFetched()");
                startIndex = startIndex + newBooks.size();
                //Log.i(TAG, newBooks.get(0).getTitle());
                itemList.addAll(newBooks);
                items.setValue(itemList);
                //Log.i(TAG, ((Book) items.getValue().get(0)).getTitle());
                Log.i(TAG, String.valueOf(itemList.size()));
                onAllItemsFetched(itemList);
            }
        };
    }

    private void setParseClient() {
        parseClient = new ParseClient(context) {
            @Override
            public void onQueryListingsDone(List<Listing> allListings, String queryString, ParseException e) {
                Log.i(TAG, "onQueryListingsDone()");
                if (e != null){
                    Log.e(TAG, QUERY_ERROR, e);
                    googleClient.fetchBooks(queryString, startIndex);
                    return;
                }

                itemList.addAll(allListings);
                skip = skip + allListings.size();

                googleClient.fetchBooks(queryString, startIndex);
            }

            @Override
            public void onParseImageSaved(ParseFile image) {
                parseClient.saveListing(title, description, price, course, image, ParseUser.getCurrentUser());
            }

            @Override
            public void onListingSaved(Listing listing) {
                fetchItems(0, 0, "");
            }
        };
    }

    public MutableLiveData<List<Item>> getItems() {
        return items;
    }

    public void fetchItems(int skipValue, long startIndexValue, String query){
        Log.i(TAG, "fetchItems()");
        skip = skipValue;
        startIndex = startIndexValue;
        if (skip == 0 && startIndexValue == 0){
            itemList.clear();
        }
        parseClient.queryListings(skipValue, query);
    }

    public void fetchMoreItems(String query){
        Log.i(TAG, "fetchMoreItems()");
        fetchItems(skip, startIndex, query);
    }

    public void onPostListing(String title, String description, String price, String course, File photoFile){
        this.title = title;
        this.description = description;
        this.price = price;
        this.course = course;
        parseClient.saveImageToParse(photoFile);
    }

    public void onAllItemsFetched(List<Item> items){}
}
