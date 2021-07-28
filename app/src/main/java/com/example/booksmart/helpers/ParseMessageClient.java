package com.example.booksmart.helpers;

import android.content.Context;
import android.util.Log;

import com.example.booksmart.models.Conversation;
import com.example.booksmart.models.Listing;
import com.example.booksmart.models.Message;
import com.parse.FindCallback;
import com.parse.GetCallback;
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
        query.addDescendingOrder(DESCENDING_ORDER_KEY);

        query.findInBackground(new FindCallback<Conversation>(){
            @Override
            public void done(List<Conversation> conversations, ParseException e) {
                if (e != null){
                    Log.e(TAG, QUERY_ERROR, e);
                    return;
                }

                List<Conversation> userConversations = new ArrayList<>();
                for (int i = 0; i < conversations.size(); i++){
                    List<ParseUser> users = conversations.get(i).getUsers();
                    if (ParseUser.getCurrentUser().getObjectId().equals(users.get(0).getObjectId()) || ParseUser.getCurrentUser().getObjectId().equals(users.get(1).getObjectId())){
                        userConversations.add(conversations.get(i));
                    }
                }
                onAllConversationsFetched(userConversations);
            }
        });

    }

    public void saveNewConversation(ParseUser user, Listing listing){
        Conversation conversation = new Conversation();
        conversation.setUsers(user, ParseUser.getCurrentUser());
        conversation.setListing(listing);

        Message message1 = new Message();
        message1.setBody("This is a message");
        message1.setUser(user);

        Message message2 = new Message();
        message2.setBody("This is another message");
        message2.setUser(ParseUser.getCurrentUser());

        List<Message> messages = new ArrayList<>();
        messages.add(message2);
        messages.add(message1);


        conversation.setMessages(messages);

        conversation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
    }

    public abstract void onAllConversationsFetched(List<Conversation> conversation);
}
