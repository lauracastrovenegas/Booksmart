package com.example.booksmart.ui.chat;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.booksmart.R;
import com.example.booksmart.viewmodels.ConversationsViewModel;

public class ConversationsFragment extends Fragment {

    private ConversationsViewModel mViewModel;

    public static ConversationsFragment newInstance() {
        return new ConversationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversations, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ConversationsViewModel.class);
        // TODO: Use the ViewModel
    }

}