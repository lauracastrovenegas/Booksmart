package com.example.booksmart.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser {
    public void setImage(ParseFile parseFile){
        put("image", parseFile);
    }
}
