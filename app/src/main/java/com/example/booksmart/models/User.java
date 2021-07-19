package com.example.booksmart.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser {

    public static final String IMAGE_KEY = "image";

    public void setImage(ParseFile parseFile){
        put(IMAGE_KEY, parseFile);
    }
}
