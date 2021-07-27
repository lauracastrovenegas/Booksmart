package com.example.booksmart.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.booksmart.models.Conversation;

import java.util.ArrayList;
import java.util.List;

public class ConversationsViewModel extends AndroidViewModel {

    public static final String TAG = "conversationViewModel";

    MutableLiveData<List<Conversation>> conversations;
    List<Conversation> conversationList;

    public ConversationsViewModel(Application application){
        super(application);

        conversationList = new ArrayList<>();
        conversations = new MutableLiveData<>();

        setRepository();
    }

    private void setRepository() {
        // TODO: Implement repository
    }

    public MutableLiveData<List<Conversation>> getConversations() {
        return conversations;
    }

    public List<Conversation> getConversationList() {
        return conversationList;
    }

    public Conversation getConversation(int position){
        return conversationList.get(position);
    }
}