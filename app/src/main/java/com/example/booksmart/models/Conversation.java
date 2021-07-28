package com.example.booksmart.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ParseClassName("Conversation")
public class Conversation extends ParseObject {
    public static final String USERS_KEY = "users";
    public static final String MESSAGES_KEY = "messages";
    public static final String LISTING_KEY = "listing";
    private static final String NAME_KEY = "name";

    public List<ParseUser> getUsers() {
        return (ArrayList) get(USERS_KEY);
    }

    public List<Message> getMessages() {
        return (ArrayList) get(MESSAGES_KEY);
    }

    public Listing getListing(){
        return (Listing) getParseObject(LISTING_KEY);
    }

    public Message getLastMessage(){
        if (getMessages().isEmpty()){
            return null;
        }

        return getMessages().get(0);
    }

    public void setUsers(ParseUser user1, ParseUser user2) {
        List<ParseObject> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        put(USERS_KEY, users);
    }

    public void setMessages(List<Message> messages) {
        put(MESSAGES_KEY, messages);
    }

    public void setListing(Listing listing){
        put(LISTING_KEY, listing);
    }

}
