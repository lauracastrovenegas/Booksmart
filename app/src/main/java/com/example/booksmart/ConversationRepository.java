package com.example.booksmart;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.booksmart.helpers.ParseMessageClient;
import com.example.booksmart.models.Conversation;

import java.util.List;

public class ConversationRepository {

    MutableLiveData<List<Conversation>> conversations;
    ParseMessageClient parseClient;
    Context context;

    public ConversationRepository (Application application){
        conversations = new MutableLiveData<>();
        context = application.getBaseContext();

        setClient();

        parseClient.queryAllConversations();
    }

    private void setClient() {
        parseClient = new ParseMessageClient(context) {
            @Override
            public void onAllConversationsFetched(List<Conversation> conversation) {
                conversations.setValue(conversation);
            }
        };
    }

    public MutableLiveData<List<Conversation>> getConversations() {
        return conversations;
    }

    public void addNewConversation(Conversation conversation){
        //parseClient.addConversation(conversation);
    }
}
