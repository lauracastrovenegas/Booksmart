package com.example.booksmart.ui.listings;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.booksmart.Client;
import com.example.booksmart.MainActivity;
import com.example.booksmart.adapters.ListingAdapter;
import com.example.booksmart.models.Book;
import com.example.booksmart.models.Item;
import com.example.booksmart.models.Listing;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListingsViewModel extends AndroidViewModel {

    public static final String TAG = "ListingsFragmentViewModel";

    MutableLiveData<List<Item>> items;
    List<Item> itemArrayList;
    Client client;
    int skip;
    long startIndex;

    public ListingsViewModel(Application application) {
        super(application);

        itemArrayList = new ArrayList<>();
        items = new MutableLiveData<>();
        skip = 0;
        startIndex = 0;

        setClient(application);

        client.onInitialLoad();
    }

    private void setClient(Application application) {
        client = new Client(application.getBaseContext()) {
            @Override
            public void onDone(List<Item> fetchedItems) {
                itemArrayList.addAll(fetchedItems);
                items.setValue(itemArrayList);

                skip = client.getCurrentSkip();
                startIndex = client.getCurrentStart();
            }
        };
    }

    public LiveData<List<Item>> getItems() {
        return items;
    }

    public List<Item> getItemArrayList(){
        return itemArrayList;
    }

    public void fetchMoreItems(){
        client.fetchItems(skip, startIndex);
    }

    public void resetList(){
        itemArrayList.clear();
        skip = 0;
        startIndex = 0;
        fetchMoreItems();
    }
}