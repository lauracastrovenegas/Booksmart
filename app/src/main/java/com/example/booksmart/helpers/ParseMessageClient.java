package com.example.booksmart.helpers;

import android.content.Context;
import android.util.Log;

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

public abstract class ParseMessageClient {

    public static final String TAG = "ParseMessageClient";
    public static final String DESCENDING_ORDER_KEY = "createdAt";
    public static final String MESSAGES_KEY = "messages";
    public static final String USERS_KEY = "users";
    public static final String QUERY_ERROR = "Unable to fetch all conversations";
    private static final String LISTING_KEY = "listing";

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
                    Log.e(TAG, QUERY_ERROR, e);
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

                Log.i(TAG, conversation.toString());
                onNewConversationSaved(conversation);
            }
        });
    }

    protected abstract void onNewConversationSaved(Conversation conversation);

    public abstract void onAllConversationsFetched(List<Conversation> conversation);
}
