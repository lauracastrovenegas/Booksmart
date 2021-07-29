package com.example.booksmart.helpers;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.booksmart.models.Conversation;
import com.example.booksmart.models.Listing;
import com.example.booksmart.models.Message;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ParseMessageClient {

    public static final String TAG = "ParseMessageClient";
    public static final String DESCENDING_ORDER_KEY = "createdAt";
    public static final String MESSAGES_KEY = "messages";
    public static final String USERS_KEY = "users";
    public static final String CONVO_QUERY_ERROR = "Unable to fetch all conversations";
    public static final String LISTING_KEY = "listing";
    private static final String USER_KEY = "user";
    private static final String CONVO_KEY = "conversation";

    Context context;

    public ParseMessageClient(Context context){
        this.context = context;
    }

    public void queryAllConversations(){
        ParseQuery query = ParseQuery.getQuery(Conversation.class);
        query.include(MESSAGES_KEY);
        query.include(LISTING_KEY);
        query.include(USERS_KEY);
        query.whereEqualTo(USERS_KEY, ParseUser.getCurrentUser());
        query.addDescendingOrder(DESCENDING_ORDER_KEY);

        query.findInBackground(new FindCallback<Conversation>(){
            @Override
            public void done(List<Conversation> conversations, ParseException e) {
                if (e != null){
                    Log.e(TAG, CONVO_QUERY_ERROR, e);
                    return;
                }

                onAllConversationsFetched(conversations);
            }
        });

    }

    public void saveNewConversation(Conversation conversation){
        conversation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, e.getMessage(), e);
                }

                onNewConversationSaved(conversation);
            }
        });
    }

    public void saveMessage(Message message){
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, e.getMessage(), e);
                }

                onMessageSaved(message);
            }
        });
    }

    public void getMessages(Conversation conversation) {
        ParseQuery query = ParseQuery.getQuery(Message.class);
        query.include(USER_KEY);
        query.include(CONVO_KEY);
        query.whereEqualTo(CONVO_KEY, conversation);
        query.addDescendingOrder(DESCENDING_ORDER_KEY);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, ParseException e) {
                if (e != null){
                    if (e.getCode() == ParseException.OTHER_CAUSE){
                        onAllMessagesFetched(null);
                    } else {
                        Log.e(TAG, e.getMessage(), e);
                    }
                    return;
                }

                onAllMessagesFetched(messages);
            }
        });
    }

    public void getLastMessage(Conversation conversation) {
        ParseQuery query = ParseQuery.getQuery(Message.class);
        query.whereEqualTo(CONVO_KEY, conversation);
        query.addDescendingOrder(DESCENDING_ORDER_KEY);

        query.getFirstInBackground(new GetCallback<Message>() {
            @Override
            public void done(Message message, ParseException e) {
                if (e != null){
                    if (e.getCode() == ParseException.OBJECT_NOT_FOUND){
                        onMessageFetched(null);
                    } else {
                        Log.e(TAG, e.getMessage(), e);
                    }
                    return;
                }

                onMessageFetched(message);
            }
        });
    }

    protected void onMessageFetched(Message message){};

    protected void onAllMessagesFetched(List<Message> messages){};

    protected void onMessageSaved(Message message){};

    protected void onNewConversationSaved(Conversation conversation){};

    protected void onAllConversationsFetched(List<Conversation> conversation){};


}
