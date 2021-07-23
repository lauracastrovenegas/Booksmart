package com.example.booksmart.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.booksmart.models.Book;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Colleges {

    public static final String API_URL = "https://parseapi.back4app.com/classes/Usuniversitieslist_University?keys=name&order=name&limit=3202";
    private static final String TAG = "Colleges";

    List<String> colleges;
    Context context;

    public Colleges(Context context){
        this.context = context;
        colleges = new ArrayList<>();
        populateCollegeList();
    }

    public void populateCollegeList(){
        try {
            JSONObject obj = new JSONObject(readJSONFromAsset());
            JSONArray results = obj.getJSONArray("results");
            for (int i = 0; i < results.length(); i++){
                colleges.add(results.getJSONObject(i).getString("institution name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String readJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open("csvjson.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

    public List<String> getColleges() {
        return colleges;
    }
}
