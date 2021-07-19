package com.example.booksmart.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Book {

    private static final String BOOK_ID = "id";
    public static final String TITLE_KEY = "title";
    public static final String SUBTITLE_KEY = "subtitle";
    public static final String AUTHORS_KEY = "authors";
    public static final String DESCRIPTION_KEY = "description";
    public static final String IMAGE_LINK_KEY = "imageLinks";
    public static final String IMAGE_KEY = "thumbnail";
    public static final String GOOGLE_LINK_KEY = "selfLink";
    public static final String VOLUME_INFO_KEY = "volumeInfo";

    String id;
    String title;
    String subtitle;
    List<String> authors;
    String description;
    String imageLink;
    String googleLink;

    public static Book fromJson(JSONObject jsonObject) throws JSONException {
        Book book = new Book();
        book.id = jsonObject.getString(BOOK_ID);
        book.googleLink = jsonObject.getString(GOOGLE_LINK_KEY);
        JSONObject volumeInfo = jsonObject.getJSONObject(VOLUME_INFO_KEY);
        book.title = volumeInfo.getString(TITLE_KEY);
        book.subtitle = volumeInfo.getString(SUBTITLE_KEY);
        book.authors = (List<String>) volumeInfo.get(AUTHORS_KEY);
        book.description = volumeInfo.getString(DESCRIPTION_KEY);
        book.imageLink = volumeInfo.getJSONObject(IMAGE_LINK_KEY).getString(IMAGE_KEY);

        return book;
    }

    public static List<Book> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            books.add(fromJson(jsonArray.getJSONObject(i)));
        }

        return books;
    }
}
