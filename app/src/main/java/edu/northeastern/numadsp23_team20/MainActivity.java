package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
    }

    public void onAboutButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, About.class);
        this.startActivity(intent);
    }

    public void onAtYourServiceButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, AtYourService.class);
        this.startActivity(intent);
    }

    public void onStickItToEmClick(View view) {
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        this.startActivity(intent);
    }

    public void onGeoNotifClick(View view) {

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(MainActivity.this, HomePage.class);
            this.startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            this.startActivity(intent);
        }
    }
}