package com.example.booksmart.ui.listings;

import android.app.Application;
import android.os.Parcelable;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.booksmart.Client;
import com.example.booksmart.models.Item;
import java.util.ArrayList;
import java.util.List;

public class ListingsViewModel extends AndroidViewModel {

    public static final String TAG = "ListingsFragmentViewModel";

    MutableLiveData<List<Item>> items;
    List<Item> itemArrayList;
    Client client;
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

    public Item getItem(int position){
        return itemArrayList.get(position);
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

    public void setRecyclerViewState(Parcelable recyclerViewState) {
        this.recyclerViewState = recyclerViewState;
    }

    public Parcelable getRecyclerViewState() {
        return recyclerViewState;
    }
}