package edu.northeastern.numadsp23_team20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

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

    private Handler handler = new Handler();
    private Button searchButton;
    private ProgressBar searchingSpinner;
    private List<Movie> movieList;
    private MovieAdapter movieAdapter;
    private RecyclerView movieRecyclerView;
    private JSONArray movieData;
    private AtomicBoolean searchComplete;

    private boolean isLoading = false;
    private String next = "titles";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_at_your_service);

        this.searchButton = (Button) findViewById(R.id.searchButton);
        this.searchingSpinner = (ProgressBar) findViewById(R.id.SearchingSpinner);
        this.movieList = new ArrayList<>();
        this.movieAdapter = new MovieAdapter(this.movieList, this);
        this.movieRecyclerView = findViewById(R.id.MovieRecyclerView);
        this.movieRecyclerView.setHasFixedSize(true);
        this.movieRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.movieRecyclerView.setAdapter(this.movieAdapter);

        initScrollListener();
    }

    private void initScrollListener() {
        this.movieRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading) {
                    if (linearLayoutManager != null
                            && linearLayoutManager.findLastCompletelyVisibleItemPosition()
                            == movieList.size() - 1) {
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
        this.movieList.add(null);
        this.movieAdapter.notifyItemInserted(this.movieList.size() - 1);
        handler.postDelayed(() -> {
            movieList.remove(movieList.size() - 1);
            int scrollPosition = movieList.size();
            movieAdapter.notifyItemRemoved(scrollPosition);
            this.searchComplete = new AtomicBoolean(false);
            FetchMovieData fetchMovieData = new FetchMovieData(this.next);
            Thread runnableThread = new Thread(fetchMovieData);
            runnableThread.start();
        }, 2000);
    }

    public void onSearchButtonClick(View view) {
        this.searchButton.setVisibility(View.INVISIBLE);
        this.searchingSpinner.setVisibility(View.VISIBLE);
        this.movieData = new JSONArray();
        this.searchComplete = new AtomicBoolean(false);
        FetchMovieData fetchMovieData = new FetchMovieData(this.next);
        Thread runnableThread = new Thread(fetchMovieData);
        runnableThread.start();
    }

    class FetchMovieData implements Runnable {

        String searchString = "";
        String nextSearchString = "";

        public FetchMovieData(String searchString) {
            this.searchString = searchString;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                URL url = new URL("https://moviesdatabase.p.rapidapi.com/" + this.searchString);
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
                System.out.println(jsonObject);
                nextSearchString = jsonObject.get("next").toString().substring(1);
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // movieAdapter.notifyItemRangeInserted(0, movieData.length());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                movieAdapter.notifyDataSetChanged();
                searchButton.setVisibility(View.VISIBLE);
                searchingSpinner.setVisibility(View.INVISIBLE);
                next = this.nextSearchString;
                isLoading = false;
            });
        }
    }
}