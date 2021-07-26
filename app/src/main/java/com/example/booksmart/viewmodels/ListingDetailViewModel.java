package com.example.booksmart.viewmodels;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.booksmart.models.Item;
import com.example.booksmart.ui.profile.ProfileFragment;

public class ListingDetailViewModel extends ViewModel {

    private MutableLiveData<Item> selected = new MutableLiveData<Item>();
    private MutableLiveData<Fragment> previousFragment = new MutableLiveData<>();

    public void select(Item item){
        selected.setValue(item);
    }

    public LiveData<Item> getSelected() {
        return selected;
    }

    public void setPreviousFragment(Fragment fragment) {
        previousFragment.setValue(fragment);
    }

    public LiveData<Fragment> getPreviousFragment(){
        return previousFragment;
    }
}