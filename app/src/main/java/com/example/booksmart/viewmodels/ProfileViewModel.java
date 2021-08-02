package com.example.booksmart.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.booksmart.ItemRepository;
import com.example.booksmart.ProfileRepository;
import com.example.booksmart.models.Favorite;
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
    MutableLiveData<List<Item>> favorites;
    ProfileRepository profileRepository;

    public ProfileViewModel(Application application) {
        super(application);

        profileRepository = new ProfileRepository(application.getBaseContext());
        listings = profileRepository.getListings();
        favorites = profileRepository.getFavorites();
    }

    public LiveData<List<Item>> getListings() {
        return listings;
    }

    public MutableLiveData<List<Item>> getFavorites() {
        return favorites;
    }

    public Item getListing(int position){
        return listings.getValue().get(position);
    }

    public Item getFavorite(int position){
        return favorites.getValue().get(position);
    }

    public void refreshFavorites() {
        profileRepository.refreshFavorites();
    }

    public void refreshListings() {
        profileRepository.refreshListings();
    }
}