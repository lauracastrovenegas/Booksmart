package com.example.booksmart;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.example.booksmart.models.Conversation;

import java.util.List;

public class ConversationRepository {

    MutableLiveData<List<Conversation>> conversations;
    // ParseConversationClient parseClient;

    public ConversationRepository (Application application){
        setClient();
        conversations = new MutableLiveData<>();
        // parseClient.getAllConversations();
    }

    private void setClient() {

    }

    public MutableLiveData<List<Conversation>> getConversations() {
        return conversations;
    }

    public void addNewConversation(Conversation conversation){
        //parseClient.addConversation(conversation);
    }
}
