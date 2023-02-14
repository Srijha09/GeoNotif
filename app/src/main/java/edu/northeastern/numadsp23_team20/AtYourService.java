package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AtYourService extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_at_your_service);
    }

    public void onSearchButtonClick(View view) {
        FetchMovieData fetchMovieData = new FetchMovieData();
        Thread runnableThread = new Thread(fetchMovieData);
        runnableThread.start();
        try {
            runnableThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONArray results = fetchMovieData.getResults();
        for (int i = 0; i < results.length(); i++) {
            JSONObject objects = null;
            try {
                objects = results.getJSONObject(i);
                System.out.println(objects);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}