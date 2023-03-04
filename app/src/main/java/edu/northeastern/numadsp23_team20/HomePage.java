package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        BottomNavigationView bottomNavigationMenu = findViewById(R.id.BottomNavigationMenu);
        bottomNavigationMenu.setOnItemSelectedListener(item -> {
            System.out.println(item.getTitle());
            return true;
        });
    }
}