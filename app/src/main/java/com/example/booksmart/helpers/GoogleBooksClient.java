package com.example.booksmart.helpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.booksmart.models.Book;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GoogleBooksClient {

    public static final String TAG = "GoogleBooksClient";
    public static final int LISTING_LIMIT = 15;
    public static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?fields=items(id,volumeInfo,saleInfo)&printType=books&maxResults=" + String.valueOf(LISTING_LIMIT) + "&q=";
    public static final String DEFAULT_QUERY = "college+textbook";
    public static final String ITEMS_KEY = "items";

    RequestQueue queue;
    long startIndex;

    public GoogleBooksClient(Context context){
        queue = Volley.newRequestQueue(context);
        startIndex = 0;
    }

    public void fetchBooks(String queryString, long start){
        if (queryString.equals("")){
            queryString = DEFAULT_QUERY;
        } else {
            queryString = queryString.replace(' ', '+');
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GOOGLE_BOOKS_URL + queryString + "&startIndex=" + String.valueOf(start), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            List<Book> newBooks = Book.fromJsonArray(response.getJSONArray(ITEMS_KEY));
                            startIndex = start + newBooks.size() + 2;

                            onBooksFetched(newBooks);
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString(), error);
                onBooksFetched(new ArrayList<>());
            }
        });

        queue.add(jsonObjectRequest);
    }

    public void onBooksFetched(List<Book> newBooks) {}
}
