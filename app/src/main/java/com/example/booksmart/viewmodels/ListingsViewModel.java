package com.example.booksmart.viewmodels;

import android.app.Application;
import android.os.Parcelable;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.booksmart.ItemRepository;
import com.example.booksmart.models.Item;
import com.example.booksmart.models.Listing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListingsViewModel extends AndroidViewModel {

    public static final String TAG = "ListingsViewModel";

    MutableLiveData<List<Item>> items;
    List<Item> itemArrayList;
    ItemRepository itemRepository;
    int skip;
    long startIndex;
    Parcelable recyclerViewState;

    public ListingsViewModel(Application application) {
        super(application);

        itemArrayList = new ArrayList<>();
        items = new MutableLiveData<>();
        skip = 0;
        startIndex = 0;

        setClient(application);

        itemRepository.onInitialLoad();
    }

    private void setClient(Application application) {
        itemRepository = new ItemRepository(application.getBaseContext()) {
            @Override
            public void onAllItemsFetched(List<Item> fetchedItems) {
                itemArrayList.addAll(fetchedItems);
                items.setValue(itemArrayList);

                skip = itemRepository.getCurrentSkip();
                startIndex = itemRepository.getCurrentStart();
            }

            @Override
            public void listingSaved(Listing listing) {
                itemArrayList.add(0, listing);
                items.setValue(itemArrayList);
            }
        };
    }

    public LiveData<List<Item>> getItems() {
        return items;
    }

    public Item getItem(int position){
        return itemArrayList.get(position);
    }

    public List<Item> getItemArrayList(){
        return itemArrayList;
    }

    public void fetchMoreItems(){
        itemRepository.fetchItems(skip, startIndex);
    }

    public void resetList(){
        itemArrayList.clear();
        skip = 0;
        startIndex = 0;
        fetchMoreItems();
    }

    public void postListing(String title, String description, String price, String course, File photoFile){
        itemRepository.onPostListing(title, description, price, course, photoFile);
    }

    public void setRecyclerViewState(Parcelable recyclerViewState) {
        this.recyclerViewState = recyclerViewState;
    }

    public Parcelable getRecyclerViewState() {
        return recyclerViewState;
    }
}