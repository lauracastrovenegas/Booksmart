package com.example.booksmart.viewmodels;

import android.app.Application;
import android.os.Parcelable;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.booksmart.ItemRepository;
import com.example.booksmart.models.Book;
import com.example.booksmart.models.Item;
import com.example.booksmart.models.Listing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListingsViewModel extends AndroidViewModel {

    public static final String TAG = "ListingsViewModel";

    MutableLiveData<List<Item>> items;
    ItemRepository itemRepository;
    int index = -1;
    int top = -1;

    public ListingsViewModel(Application application) {
        super(application);

        items = new MutableLiveData<>();
        itemRepository = new ItemRepository(application){
            @Override
            public void onAllItemsFetched(List<Item> allItems) {
                Log.i(TAG, "onAllItemsFetched()");
                items.setValue(allItems);
            }
        };

    }

    public LiveData<List<Item>> getItems() {
        return items;
    }

    public Item getItem(int position){
        return items.getValue().get(position);
    }

    public void fetchItems(String query){
        itemRepository.fetchItems(0, 0, query);
    }

    public void fetchMoreItems(String query){
        itemRepository.fetchMoreItems(query);
    }

    public void resetList(){
        itemRepository.fetchItems(0, 0, "");
    }

    public void postListing(String title, String description, String price, String course, File photoFile){
        itemRepository.onPostListing(title, description, price, course, photoFile);
        setIndex(-1);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getIndex() {
        return index;
    }

    public int getTop() {
        return top;
    }
}