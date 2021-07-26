package com.example.booksmart.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Conversation")
public class Conversation extends ParseObject {
    public static final String USERS_KEY = "users";
    public static final String MESSAGES_KEY = "messages";
    public static final String LISTING_KEY = "listing";
    private static final String NAME_KEY = "name";

    public ParseUser[] getUsers() {
        return (ParseUser[]) get(USERS_KEY);
    }

    public Message[] getMessages() {
        return (Message[]) get(MESSAGES_KEY);
    }

    public Listing getListing(){
        return (Listing) getParseObject(LISTING_KEY);
    }

    public String getPreview(){
        Message lastMessage = getMessages()[0];
        String messageBody = lastMessage.getBody();
        String userName = lastMessage.getUser().getString(NAME_KEY);
        if (userName.equals(ParseUser.getCurrentUser().getString(NAME_KEY))){
            return "You: " + messageBody;
        }

        return userName + ": " + messageBody;
    }

    public void setUsers(ParseUser user1, ParseUser user2) {
        ParseUser[] users = new ParseUser[2];
        users[0] = user1;
        users[1] = user2;
        put(USERS_KEY, users);
    }

    public void setMessages(Message[] messages) {
        put(MESSAGES_KEY, messages);
    }

    public void setListing(Listing listing){
        put(LISTING_KEY, listing);
    }

}
