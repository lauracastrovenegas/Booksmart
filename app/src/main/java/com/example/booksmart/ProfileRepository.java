package com.example.booksmart;

import android.content.Context;

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
                favoriteList = allFavorites;
                parseFavoritesToItems(allFavorites);
            }

            @Override
            public void onListingSaved(Listing listing) {
                parseClient.queryUserListings(ParseUser.getCurrentUser());
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
        items = new ArrayList<>();
        for (int i = 0; i < favorites.size(); i++){
            Favorite favorite = favorites.get(i);
            if (favorite.getType() == Item.TYPE_LISTING){
                items.add(favorite.getListing());
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
}
