package com.example.booksmart.models;

import android.text.format.DateFormat;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Favorite")
public class Favorite extends ParseObject {
    public static final String USER_KEY = "user";
    public static final String LISTING_KEY = "listing";
    private static final String BOOK_ID_KEY = "bookId";
    private static final String TYPE_KEY = "type";

    public ParseUser getUser() {
        return getParseUser(USER_KEY);
    }

    public Listing getListing(){
        return (Listing) getParseObject(LISTING_KEY);
    }

    public String getBookId(){
        return getString(BOOK_ID_KEY);
    }

    public int getType(){
        return getInt(TYPE_KEY);
    }

    public void setUser(ParseUser user) {
        put(USER_KEY, user);
    }

    public void setListing(Listing listing){
        put(LISTING_KEY, listing);
    }

    public void setBookId(String bookId) {
       put(BOOK_ID_KEY, bookId);
    }

    public void setType(int typeCode){
        put(TYPE_KEY, typeCode);
    }
}
