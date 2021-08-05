package com.example.booksmart;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.booksmart.helpers.ParseMessageClient;
import com.example.booksmart.models.Conversation;
import com.example.booksmart.models.Listing;
import com.example.booksmart.models.Message;
import com.example.booksmart.ui.MainActivity;

import java.util.List;

public class ConversationRepository {

    MutableLiveData<List<Conversation>> conversations;
    MutableLiveData<Boolean> notification;
    ParseMessageClient parseClient;
    Context context;

    public ConversationRepository (Application application){
        conversations = new MutableLiveData<>();
        notification = new MutableLiveData<>();
        context = application.getBaseContext();

        setClient();

        parseClient.queryAllConversations();
    }

    private void setClient() {
        parseClient = new ParseMessageClient(context) {
            @Override
            protected void onNewConversationSaved(Conversation conversation) {
                refreshConversations();
            }

            @Override
            public void onAllConversationsFetched(List<Conversation> conversation) {
                conversations.setValue(conversation);
            }

            @Override
            protected void onConversationsUpdated() {
                refreshConversations();
            }

            @Override
            protected void setNotification(Boolean isActive) {
                notification.setValue(isActive);
            }

            @Override
            protected void onConversationsRemoved() {
                refreshConversations();
            }
        };
    }

    public MutableLiveData<List<Conversation>> getConversations() {
        return conversations;
    }

    public void setMessageLiveQuery() {
        parseClient.setMessageLiveQuery();
    }

    public void refreshConversations(){
        parseClient.queryAllConversations();
    }

    public void setConversationLiveQuery() {
        parseClient.setConversationLiveQuery();
    }

    public MutableLiveData<Boolean> getNotification() {
        return notification;
    }

    public void setNotification(Boolean notification) {
        this.notification.setValue(notification);
    }
}
