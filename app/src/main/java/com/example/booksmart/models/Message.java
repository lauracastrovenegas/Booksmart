package com.example.booksmart.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String USER_KEY = "user";
    public static final String BODY_KEY = "body";

    public String getUserId() {
        return getParseUser(USER_KEY).getObjectId();
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public void setUser(ParseUser user) {
        put(USER_KEY, user);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }
}
