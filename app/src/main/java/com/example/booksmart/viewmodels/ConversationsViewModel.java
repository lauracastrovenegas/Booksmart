package com.example.booksmart.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.booksmart.ConversationRepository;
import com.example.booksmart.models.Conversation;
import com.example.booksmart.models.Listing;

import java.util.ArrayList;
import java.util.List;

public class ConversationsViewModel extends AndroidViewModel {

    public static final String TAG = "conversationViewModel";

    MutableLiveData<List<Conversation>> conversations;
    ConversationRepository repository;

    public ConversationsViewModel(Application application){
        super(application);

        repository = new ConversationRepository(application);
        conversations = repository.getConversations();
    }

    public MutableLiveData<List<Conversation>> getConversations() {
        return conversations;
    }

    public void addNewConversation(Conversation conversation){
        repository.addNewConversation(conversation);
    }

    public Conversation getConversation(int position){
        return conversations.getValue().get(position);
    }
}