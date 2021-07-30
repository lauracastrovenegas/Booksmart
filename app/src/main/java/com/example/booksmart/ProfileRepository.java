package com.example.booksmart;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.booksmart.helpers.ParseClient;
import com.example.booksmart.models.Item;
import com.example.booksmart.models.Listing;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileRepository {

    Context context;
    ParseClient parseClient;
    MutableLiveData<List<Item>> listings;

    public ProfileRepository(Context context){
        this.context = context;
        listings = new MutableLiveData<>();

        setParseClient();

        parseClient.queryUserListings(ParseUser.getCurrentUser());
    }

    private void setParseClient() {
        parseClient = new ParseClient(context){
            @Override
            public void onQueryUserListingsDone(List<Listing> allListings, ParseException e) {
                List<Item> newListings = new ArrayList<>();
                newListings.addAll(allListings);
                listings.setValue(newListings);
            }

            @Override
            public void onListingSaved(Listing listing) {
                parseClient.queryUserListings(ParseUser.getCurrentUser());
            }
        };
    }

    public MutableLiveData<List<Item>> getListings() {
        return listings;
    }
}
