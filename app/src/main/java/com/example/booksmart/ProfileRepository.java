package com.example.booksmart;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.booksmart.helpers.GoogleBooksClient;
import com.example.booksmart.helpers.ParseClient;
import com.example.booksmart.models.Book;
import com.example.booksmart.models.Favorite;
import com.example.booksmart.models.Item;
import com.example.booksmart.models.Listing;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileRepository {

    Context context;
    ParseClient parseClient;
    GoogleBooksClient googleClient;
    MutableLiveData<List<Item>> listings;
    MutableLiveData<List<Item>> favorites;
    List<Favorite> favoriteList;
    List<Item> items;

    public ProfileRepository(Context context){
        this.context = context;
        items = new ArrayList<>();
        listings = new MutableLiveData<>();
        favorites = new MutableLiveData<>();
        favoriteList = new ArrayList<>();

        setParseClient();
        setGoogleClient();

        parseClient.queryUserListings(ParseUser.getCurrentUser());
        parseClient.queryUserFavorites(ParseUser.getCurrentUser());
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
            public void onQueryUserFavoritesDone(List<Favorite> allFavorites, ParseException e) {
                favoriteList.clear();
                favoriteList.addAll(allFavorites);
                parseFavoritesToItems(favoriteList);
            }

            @Override
            public void onListingSaved(Listing listing) {
                refreshListings();
            }
        };
    }

    private void setGoogleClient(){
        googleClient = new GoogleBooksClient(context){
            @Override
            public void onBookFetched(Book book) {
                items.add(book);
                if (allItemsParsed()){
                    favorites.setValue(items);
                }
            }
        };
    }

    private void parseFavoritesToItems(List<Favorite> favorites) {
        items.clear();
        for (int i = 0; i < favorites.size(); i++){
            Favorite favorite = favorites.get(i);
            if (favorite.getType() == Item.TYPE_LISTING){
                items.add(favorite.getListing());
                if (allItemsParsed()){
                    this.favorites.setValue(items);
                }
            } else {
                googleClient.fetchBook(favorite.getBookId());
            }
        }
    }

    public MutableLiveData<List<Item>> getListings() {
        return listings;
    }

    public MutableLiveData<List<Item>> getFavorites() {
        return favorites;
    }

    public Boolean allItemsParsed(){
        return favoriteList.size() == items.size();
    }

    public void refreshFavorites() {
        parseClient.queryUserFavorites(ParseUser.getCurrentUser());
    }

    public void refreshListings() {
        parseClient.queryUserListings(ParseUser.getCurrentUser());
    }

    public List<Item> getItems() {
        return items;
    }
}
