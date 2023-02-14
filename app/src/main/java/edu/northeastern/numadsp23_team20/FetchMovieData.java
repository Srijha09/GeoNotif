package edu.northeastern.numadsp23_team20;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class FetchMovieData implements Runnable {
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
            final String resp = convertStreamToString(inputStream);
            System.out.println(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
}
