package com.example.booksmart.helpers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.booksmart.R;
import com.example.booksmart.models.Book;
import com.example.booksmart.models.Favorite;
import com.example.booksmart.models.Item;
import com.example.booksmart.models.Listing;
import com.example.booksmart.models.User;
import com.example.booksmart.ui.WelcomeActivity;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ParseClient {

    public static final String TAG = "Parse Client";
    public static final int LISTING_LIMIT = 15;
    public static final String DESCENDING_ORDER_KEY = "createdAt";
    public static final String KEY_SCHOOL = "school";
    public static final String QUERY_ERROR = "Error getting listings";
    public static final String SAVING_ERROR = "Error while saving";
    private static final String ERROR_SAVING_IMAGE = "Could not save image uploaded. Please try again!";
    public static final String NAME_KEY = "name";
    private static final String SIGN_UP_FAILURE = "Unable to create account for user!";
    public static final String LOGIN_FAILURE = "Unable to login: ";
    private static final String USERNAME_TAKEN_MSG = "Sorry, that username is already taken.";
    public static final String EMAIL_TAKEN_MSG = "An account already exists for that email.";
    private static final String TITLE_KEY = "title";

    Context context;
    ParseUser user;
    String currentUserSchool;

    public ParseClient(Context context){
        this.context = context;
    }

    // Sign up new user -> log in new user
    public void signUpUser(ParseFile savedImage, String username, String password, String email, String name, String school) {
        Log.i(TAG, "signUpUser()");
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put(NAME_KEY, name);
        user.put(KEY_SCHOOL, school);

        if (savedImage != null) {
            user.setImage(savedImage);
        }

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    switch (e.getCode()){
                        case ParseException.USERNAME_TAKEN:
                            Toast.makeText(context, USERNAME_TAKEN_MSG, Toast.LENGTH_SHORT).show();
                            break;
                        case ParseException.EMAIL_TAKEN:
                            Toast.makeText(context, EMAIL_TAKEN_MSG, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(context, SIGN_UP_FAILURE,Toast.LENGTH_SHORT).show();
                            Log.e(TAG, e.getMessage(), e);
                            break;
                    }
                    onSignUpUnsuccessful();
                    return;
                }

                loginUser(username, password);
            }
        });
    }

    // Log in User
    public void loginUser(String username, String password){
        Log.i(TAG, "loginUser()");
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null){
                    Toast.makeText(context, LOGIN_FAILURE + e.getMessage(), Toast.LENGTH_SHORT).show();
                    onLogInUnsuccessful();
                    return;
                }

                onUserLoggedIn();
            }
        });
    }

    // get current user and set school
    public void getCurrentUser(){
        Log.i(TAG, "getCurrentUser()");
        user = ParseUser.getCurrentUser();
        user.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                currentUserSchool = user.getString(KEY_SCHOOL);
                onUserFetched(user);
            }
        });
    }

    // Query posts for a specific user
    public void queryUserListings(ParseUser user) {
        user.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);
                query.include(Listing.KEY_USER);
                query.whereEqualTo(Listing.KEY_USER, user);
                query.addDescendingOrder(DESCENDING_ORDER_KEY);

                query.findInBackground(new FindCallback<Listing>() {

                    @Override
                    public void done(List<Listing> allListings, ParseException e) {
                        if (e != null){
                            Log.e(TAG, QUERY_ERROR, e);
                            return;
                        }

                        onQueryUserListingsDone(allListings, e);
                    }
                });
            }
        });
    }

    // Query all listings from skipValue to skipValue + LISTING_LIMIT
    public void queryListings(int skipValue, String queryString) {
        user = ParseUser.getCurrentUser();
        try {
            user.fetchIfNeeded();
            currentUserSchool = user.getString(KEY_SCHOOL);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);
        query.include(Listing.KEY_USER);
        query.whereEqualTo(KEY_SCHOOL, currentUserSchool);
        query.whereEqualTo(Listing.KEY_SOLD, false);
        if (!queryString.equals("")){
            query.whereContains(TITLE_KEY, queryString);
        }
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

                onQueryListingsDone(allListings, queryString, e);
            }
        });
    }

    // Save listing object to parse database
    public void saveListing(String title, String description, String price, String course, ParseFile photoFile, ParseUser currentUser) {
        Listing listing = new Listing();
        listing.setTitle(title);
        listing.setDescription(description);
        listing.setPrice(Integer.parseInt(price));
        listing.setCourse(course);
        listing.setImage(photoFile);
        listing.setUser(currentUser);
        listing.setSchool(currentUser.getString(KEY_SCHOOL));

        listing.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, SAVING_ERROR, e);
                    Toast.makeText(context, SAVING_ERROR, Toast.LENGTH_SHORT).show();
                }

                onListingSaved(listing);
            }
        });
    }

    // Save a File as a ParseFile
    public void saveImageToParse(File photoFile){
        Log.i(TAG, "saveImageToParse()");
        if (photoFile != null) {
            ParseFile photo = new ParseFile(photoFile);
            photo.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Toast.makeText(context, ERROR_SAVING_IMAGE, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, ERROR_SAVING_IMAGE, e);
                        return;
                    }

                    onParseImageSaved(photo);
                }
            });
        } else {
            onParseImageSaved(null);
        }
    }

    public void checkItemFavorite(Item item){
        ParseQuery query = ParseQuery.getQuery(Favorite.class);
        query.whereEqualTo(Favorite.TYPE_KEY, item.getType());
        if (item.getType() == Item.TYPE_LISTING){
            query.whereEqualTo(Favorite.LISTING_KEY, item);
        } else {
            query.whereEqualTo(Favorite.BOOK_ID_KEY, ((Book) item).getId());
        }
        query.getFirstInBackground(new GetCallback<Favorite>(){
            public void done(Favorite favorite, ParseException e){
                if (e == null){ // favorite exists
                    onItemFavorite(favorite);
                } else { // favorite does not exist
                    if(e.getCode() != ParseException.OBJECT_NOT_FOUND){
                        Log.e(TAG, e.getMessage(), e);
                    }

                    onItemFavorite(null);
                }
            }
        });
    }

    public void queryUserFavorites(ParseUser currentUser) {
        ParseQuery query = ParseQuery.getQuery(Favorite.class);
        query.include(Favorite.LISTING_KEY);
        query.include(Listing.KEY_USER);
        query.whereEqualTo(Favorite.USER_KEY, currentUser);
        query.addDescendingOrder(DESCENDING_ORDER_KEY);
        query.findInBackground(new FindCallback<Favorite>() {

            @Override
            public void done(List<Favorite> favorites, ParseException e) {
                if (e != null){
                    Log.e(TAG, QUERY_ERROR, e);
                    return;
                }

                onQueryUserFavoritesDone(favorites, e);
            }
        });
    }

    public void saveFavorite(Favorite favorite){
        favorite.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                onFavoritesUpdated();
            }
        });
    }

    public void removeFavorite(Favorite favorite){
        favorite.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                onFavoritesUpdated();
            }
        });
    }

    public void removeFavorites(Listing listing){
        ParseQuery query = ParseQuery.getQuery(Favorite.class);
        query.whereEqualTo(Favorite.LISTING_KEY, listing);
        query.findInBackground(new FindCallback<Favorite>() {
            @Override
            public void done(List<Favorite> favorites, ParseException e) {
                for(int i = 0; i < favorites.size(); i++){
                    favorites.get(i).deleteInBackground();
                }

                onFavoritesUpdated();
            }
        });
    }

    public void markAsSold(Listing listing){
        listing.setSold(true);
        listing.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Toast.makeText(context, "Failed to mark listing as sold. Please try again", Toast.LENGTH_SHORT).show();
                    return;
                }

                onListingUpdated();
            }
        });
    }

    public void onListingUpdated() {}

    public void onFavoritesUpdated() {}

    public void onQueryUserFavoritesDone(List<Favorite> favorites, ParseException e) { }

    public void onItemFavorite(Favorite favorite) {}

    public void onUserLoggedIn(){};

    public void onLogInUnsuccessful() {}

    public void onSignUpUnsuccessful() {};

    public void onUserFetched(ParseUser user){};

    public void onQueryListingsDone(List<Listing> items, String query, ParseException e){};

    public void onQueryUserListingsDone(List<Listing> allListings, ParseException e){};

    public void onListingSaved(Listing listing){};

    public void onParseImageSaved(ParseFile image){};
}
