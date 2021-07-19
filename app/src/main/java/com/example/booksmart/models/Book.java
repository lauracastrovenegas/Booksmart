package com.example.booksmart.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Book implements Item {

    private static final String BOOK_ID = "id";
    public static final String TITLE_KEY = "title";
    public static final String AUTHORS_KEY = "authors";
    public static final String DESCRIPTION_KEY = "description";
    public static final String IMAGE_LINK_KEY = "imageLinks";
    public static final String IMAGE_KEY = "thumbnail";
    public static final String GOOGLE_LINK_KEY = "selfLink";
    public static final String VOLUME_INFO_KEY = "volumeInfo";
    public static final String SALE_INFO_KEY = "saleInfo";
    public static final String SALEABILITY_KEY = "saleability";
    public static final String NOT_FOR_SALE = "NOT_FOR_SALE";
    public static final String PRICE_KEY = "listPrice";
    public static final String PRICE_AMNT_KEY = "amount";

    String id;
    String title;
    JSONArray authors;
    String description;
    String imageLink;
    String googleLink;
    String saleability;
    String price;

    public static Book fromJson(JSONObject jsonObject) throws JSONException {
        Book book = new Book();
        book.id = jsonObject.getString(BOOK_ID);
        book.googleLink = jsonObject.getString(GOOGLE_LINK_KEY);
        JSONObject volumeInfo = jsonObject.getJSONObject(VOLUME_INFO_KEY);
        book.title = volumeInfo.getString(TITLE_KEY);
        try {
            book.authors = volumeInfo.getJSONArray(AUTHORS_KEY);
        } catch (JSONException e){
            book.authors = null;
        }
        try {
            book.description = volumeInfo.getString(DESCRIPTION_KEY);
        } catch (JSONException e){
            book.description = "";
        }
        try {
            book.imageLink = volumeInfo.getJSONObject(IMAGE_LINK_KEY).getString(IMAGE_KEY);
        } catch (JSONException e){
            book.imageLink = "";
        }
        JSONObject saleInfo = jsonObject.getJSONObject(SALE_INFO_KEY);
        book.saleability = saleInfo.getString(SALEABILITY_KEY);
        if (!book.saleability.equals(NOT_FOR_SALE)){
            try {
                book.price = saleInfo.getJSONObject(PRICE_KEY).getString(PRICE_AMNT_KEY);
            } catch (JSONException e){
                book.price = null;
            }
        } else {
            book.price = null;
        }

        return book;
    }

    public static List<Book> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            books.add(fromJson(jsonArray.getJSONObject(i)));
        }

        return books;
    }

    public String getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public List<JSONObject> getAuthors(){
        List<JSONObject> authorsList = new ArrayList<>();
        for (int i = 0; i < authors.length(); i++){
            try {
                authorsList.add(authors.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return authorsList;
    }

    public String getImage(){
        return imageLink;
    }

    public String getGoogleLink(){
        return googleLink;
    }

    public String getPrice(){
        return price;
    }

    public int getType(){
        return Item.TYPE_BOOK;
    }
}
