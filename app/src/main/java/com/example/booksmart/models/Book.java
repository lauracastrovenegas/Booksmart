package com.example.booksmart.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Book implements Item {

    public static final String TAG = "Book.class";
    private static final String BOOK_ID = "id";
    public static final String TITLE_KEY = "title";
    public static final String AUTHORS_KEY = "authors";
    public static final String DESCRIPTION_KEY = "description";
    public static final String IMAGE_LINK_KEY = "imageLinks";
    public static final String IMAGE_KEY = "thumbnail";
    public static final String GOOGLE_LINK_KEY = "previewLink";
    public static final String VOLUME_INFO_KEY = "volumeInfo";
    public static final String SALE_INFO_KEY = "saleInfo";
    public static final String SALEABILITY_KEY = "saleability";
    public static final String NOT_FOR_SALE = "NOT_FOR_SALE";
    public static final String PRICE_KEY = "listPrice";
    public static final String PRICE_AMNT_KEY = "amount";
    public static final String USER_NAME = "Google Books";
    public static final String INDUSTRY_ID_KEY = "industryIdentifiers";
    public static final String TYPE_KEY = "type";
    public static final String IDENTIFIER_KEY = "identifier";

    String id;
    String title;
    JSONArray authors;
    String description;
    String imageLink;
    String googleLink;
    String amazonLink;
    String barnesNoblesLink;
    String booksMillionLink;
    String saleability;
    String price;
    String ISBN;

    public static Book fromJson(JSONObject jsonObject) throws JSONException {
        Book book = new Book();
        book.id = jsonObject.getString(BOOK_ID);
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
            if (!book.imageLink.isEmpty()) {
                book.imageLink = book.imageLink.substring(0, 4) + "s" + book.imageLink.substring(4);
            }
        } catch (JSONException e){
            book.imageLink = "";
        }
        try {
            JSONArray industryIdentifiers = volumeInfo.getJSONArray(INDUSTRY_ID_KEY);
            book.ISBN = "";
            if (industryIdentifiers.length() != 0){
                for (int i = 0; i < industryIdentifiers.length(); i++){
                    JSONObject identifier = industryIdentifiers.getJSONObject(i);
                    if (identifier.getString(TYPE_KEY).equals("ISBN_13")){
                        book.ISBN = identifier.getString(IDENTIFIER_KEY);
                    }
                }
            }
        } catch (JSONException e){
            book.ISBN = "";
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

        book.googleLink = "https://www.google.com/books/edition/" + book.title.replace(" ","_") + "/" + book.id + "?hl=en&kptab=getbook";
        if (!book.ISBN.equals("")){
            book.amazonLink = "https://www.amazon.com/s?k=" + book.ISBN + "&i=stripbooks&linkCode=qs";
            book.barnesNoblesLink = "https://www.barnesandnoble.com/w/" + book.title.replace(" ","-") + (book.getAuthors().size() > 0 ? "-" + book.getAuthors().get(0).replace(" ", "-") : "") + "?ean=" + book.ISBN;
            book.booksMillionLink = "https://www.booksamillion.com/search?query=" + book.ISBN;
        } else {
            book.amazonLink = "";
            book.barnesNoblesLink = "";
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

    public List<String> getAuthors(){
        List<String> authorsList = new ArrayList<>();
        if (authors != null) {
            for (int i = 0; i < authors.length(); i++) {
                try {
                    authorsList.add(authors.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    public String getAmazonLink() {
        return amazonLink;
    }

    public String getBarnesNoblesLink() {
        return barnesNoblesLink;
    }

    public String getBooksMillionLink() {
        return booksMillionLink;
    }

    public String getPrice(){
        if (price != null) {
            return "$" + price;
        }

        return null;
    }

    public String getUserName(){
        return USER_NAME;
    }

    public int getType(){
        return Item.TYPE_BOOK;
    }
}
