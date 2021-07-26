package com.example.booksmart.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.booksmart.ItemRepository;
import com.example.booksmart.models.Item;
import com.example.booksmart.models.Listing;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    public static final String TAG = "ProfileViewModel";

    MutableLiveData<List<Item>> listings;
    List<Item> listingsArrayList;
    ItemRepository itemRepository;
    int listingsSkip;

    public ProfileViewModel(Application application) {
        super(application);

        listings = new MutableLiveData<>();
        listingsArrayList = new ArrayList<>();
        listingsSkip = 0;

        setClient(application);

        itemRepository.queryUserListings(listingsSkip, ParseUser.getCurrentUser());
    }

    private void setClient(Application application) {
        itemRepository = new ItemRepository(application.getBaseContext()) {
            @Override
            public void onAllItemsFetched(List<Item> items) {
                listingsArrayList.addAll(items);
                listings.setValue(listingsArrayList);
                Log.i(TAG, listingsArrayList.toString());
            }

            @Override
            public void listingSaved(Listing listing) {}
        };
    }

    public LiveData<List<Item>> getListings() {
        return listings;
    }

    public Item getListing(int position){
        return listingsArrayList.get(position);
    }

    public List<Item> getListingsArrayList(){
        return listingsArrayList;
    }

    public void addToListings(String title, String description, String price, String course, File photoFile) {
        Listing listing = new Listing();
        listing.setTitle(title);
        listing.setDescription(description);
        listing.setPrice(Integer.parseInt(price));
        listing.setCourse(course);
        listing.setUser(ParseUser.getCurrentUser());
        ParseFile image = new ParseFile(photoFile);
        image.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                listing.setImage(image);
                listingsArrayList.add(0, listing);
            }
        });
    }
}