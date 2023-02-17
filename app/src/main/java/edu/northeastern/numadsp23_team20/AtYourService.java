package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AtYourService extends AppCompatActivity {

    private List<Movie> movieList;
    private MovieAdapter movieAdapter;
    private RecyclerView movieRecyclerView;
    private JSONArray movieData;
    private AtomicBoolean searchComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_at_your_service);

        this.movieList = new ArrayList<>();
        this.movieAdapter = new MovieAdapter(this.movieList, this);
        this.movieRecyclerView = findViewById(R.id.MovieRecyclerView);
        this.movieRecyclerView.setHasFixedSize(true);
        this.movieRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.movieRecyclerView.setAdapter(this.movieAdapter);
    }

    public void onSearchButtonClick(View view) {
        this.movieData = new JSONArray();
        this.searchComplete = new AtomicBoolean(false);
        FetchMovieData fetchMovieData = new FetchMovieData();
        Thread runnableThread = new Thread(fetchMovieData);
        runnableThread.start();
        while (!this.searchComplete.get()) {
            Log.d("LOG", "Searching...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class FetchMovieData implements Runnable {
        @Override
        public void run() {
            try {
                URL url = new URL("https://moviesdatabase.p.rapidapi.com/titles");
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
                movieData = (JSONArray) jsonObject.get("results");
                searchComplete.set(true);
                JSONObject objects;
                JSONObject titleText;
                JSONObject releaseYear;
                for (int i = 0; i < movieData.length(); i++) {
                    try {
                        objects = movieData.getJSONObject(i);
                        System.out.println(objects);
                        titleText = (JSONObject) objects.get("titleText");
                        releaseYear = (JSONObject) objects.get("releaseYear");
                        movieList.add(new Movie(
                                titleText.get("text").toString(),
                                releaseYear.get("year").toString()
                        ));
                        movieAdapter.notifyItemInserted(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }
}