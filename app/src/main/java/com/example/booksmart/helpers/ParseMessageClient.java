package com.example.booksmart.helpers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;
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

    public void getConversation(Listing listing){
        ParseQuery query = ParseQuery.getQuery(Conversation.class);
        query.include(ParseMessageClient.LISTING_KEY);
        query.include(ParseMessageClient.USERS_KEY);
        query.whereEqualTo(ParseMessageClient.LISTING_KEY, listing);
        query.whereEqualTo(ParseMessageClient.USERS_KEY, ParseUser.getCurrentUser());

        query.getFirstInBackground(new GetCallback<Conversation>(){
            public void done(Conversation conversation, ParseException e){
                if (e == null){ // conversation already exists
                    onConversationFetched(conversation);
                } else { // conversation does not exist -> start new conversation
                    if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                        Conversation newConversation = new Conversation();
                        newConversation.setUsers(listing.getUser(), ParseUser.getCurrentUser());
                        newConversation.setListing(listing);
                        onConversationFetched(newConversation);
                    } else {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }
        });
    }

    public void saveMessage(Message message){
        // Check if conversation exists
        Conversation conversation = message.getConversation();
        conversation.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e != null){
                    if (e.getCode() == ParseException.OBJECT_NOT_FOUND){
                        // Conversation does not exists - save it first
                        conversation.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e != null){
                                    Log.e(TAG, e.getMessage(), e);
                                }

                                // save message after saving new conversation
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
                        });
                    } else {
                        Log.e(TAG, e.getMessage(), e);
                    }
                    return;
                }

                // Conversation exists - save message
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

    public void setMessageLiveQuery(){
        ParseLiveQueryClient parseLiveQueryClient = null;

        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("wss://booksmartapp.b4a.io/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ParseQuery query = ParseQuery.getQuery(Message.class);
        query.include(CONVO_KEY);
        query.whereNotEqualTo(USER_KEY, ParseUser.getCurrentUser());

        // Connect to Parse server
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(query);

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Message>() {
            @Override
            public void onEvent(ParseQuery<Message> query, final Message message) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Log.i(TAG, "new Message" + message.getBody());
                        Conversation conversation =  message.getConversation();
                        try {
                            conversation.fetchIfNeeded();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (conversation.getUsers().get(0).getObjectId().equals(ParseUser.getCurrentUser().getObjectId()) || conversation.getUsers().get(1).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                            conversation.setUnread(true);
                            conversation.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    onNewMessageFound(message);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    public void setConversationLiveQuery(){
        ParseLiveQueryClient parseLiveQueryClient = null;

        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("wss://booksmartapp.b4a.io/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ParseQuery query = ParseQuery.getQuery(Conversation.class);

        // Connect to Parse server
        SubscriptionHandling<Conversation> subscriptionHandling = parseLiveQueryClient.subscribe(query);

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.DELETE, new SubscriptionHandling.HandleEventCallback<Conversation>() {
            @Override
            public void onEvent(ParseQuery<Conversation> query, final Conversation conversation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        if (conversation.getUsers().get(0).getObjectId().equals(ParseUser.getCurrentUser().getObjectId()) || conversation.getUsers().get(1).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                            onConversationsRemoved();
                        }
                    }
                });
            }
        });
    }

    public void removeConversations(Listing listing){
        ParseQuery query = ParseQuery.getQuery(Conversation.class);
        query.whereEqualTo(LISTING_KEY, listing);
        query.findInBackground(new FindCallback<Conversation>(){
            @Override
            public void done(List<Conversation> conversations, ParseException e) {
                for (int i = 0; i < conversations.size(); i++){
                    clearMessages(conversations.get(i));
                    conversations.get(i).deleteInBackground();
                }

                onConversationsRemoved();
            }
        });
    }

    public void clearMessages(Conversation conversation){
        ParseQuery innerQuery = ParseQuery.getQuery(Message.class);
        innerQuery.whereEqualTo(Message.CONVO_KEY, conversation);
        innerQuery.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, ParseException e) {
                for (int i = 0; i < messages.size(); i++){
                    messages.get(i).deleteInBackground();
                }
            }
        });
    }

    protected void onConversationsRemoved() {}

    protected void onNewMessageFound(Message message) {}

    protected void onMessageFetched(Message message){};

    protected void onAllMessagesFetched(List<Message> messages){};

    protected void onMessageSaved(Message message){};

    protected void onNewConversationSaved(Conversation conversation){};

    protected void onAllConversationsFetched(List<Conversation> conversation){};

    protected void onConversationFetched(Conversation conversation) {};


}
