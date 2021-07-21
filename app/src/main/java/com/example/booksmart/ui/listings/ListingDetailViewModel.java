package com.example.booksmart.ui.listings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.booksmart.models.Item;

public class ListingDetailViewModel extends ViewModel {

    private MutableLiveData<Item> selected = new MutableLiveData<Item>();

    public void select(Item item){
        selected.setValue(item);
    }

    public LiveData<Item> getSelected() {
        return selected;
    }
}