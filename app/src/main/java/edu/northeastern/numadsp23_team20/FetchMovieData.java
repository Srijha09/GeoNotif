package edu.northeastern.numadsp23_team20;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class FetchMovieData implements Runnable {

    private volatile JSONArray results;

    @Override
    public void run() {
        URL url = null;

        try {
            url = new URL("https://moviesdatabase.p.rapidapi.com/titles");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-RapidAPI-Key", "36c780a7bamshbd41900545a1fffp1579a1jsn8e009bed63f1");
            conn.setRequestProperty("X-RapidAPI-Host", "moviesdatabase.p.rapidapi.com");
            conn.setDoInput(true);
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            JSONObject jsonObject = null;

            String currentLine;
            try {
                while ((currentLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(currentLine);
                }
                JSONTokener jsonTokener = new JSONTokener(stringBuilder.toString());
                jsonObject = new JSONObject(jsonTokener);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.results = (JSONArray) jsonObject.get("results");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getResults() {
        return this.results;
    }
}
