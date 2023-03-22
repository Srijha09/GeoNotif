package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ProfileFragment profileFragment = new ProfileFragment();
        BottomNavigationView bottomNavigationMenu = findViewById(R.id.BottomNavigationMenu);
        FirebaseAuth.getInstance().signOut();
        bottomNavigationMenu.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, profileFragment).commit();
                    return true;
                case R.id.friends:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, profileFragment).commit();
                    return true;
                default:
                    System.out.println(item.getTitle());
            }
            return false;
        });
    }
}