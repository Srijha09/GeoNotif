package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomePage extends AppCompatActivity {

    private Fragment tasksFragment;
    private Fragment groupsFragment;
    private Fragment friendsFragment;
    private Fragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        this.checkLocationPermissions();
        this.tasksFragment = new TasksFragment();
        this.groupsFragment = new GroupsFragment();
        this.friendsFragment = new FriendsFragment();
        this.profileFragment = new ProfileFragment();
        BottomNavigationView bottomNavigationMenu = findViewById(R.id.BottomNavigationMenu);
        this.setFragment(this.tasksFragment);
        bottomNavigationMenu.setOnItemSelectedListener(item -> {
            this.setFragment(this.getFragment(item.getTitle().toString()));
            return true;
        });
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }

    private void checkLocationPermissions() {
        boolean noFineLocationAccess = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean noCoarseLocationAccess = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean noBackgroundLocationAccess = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED;
        if (noFineLocationAccess || noCoarseLocationAccess || noBackgroundLocationAccess) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    101);
        }
    }

    private Fragment getFragment(String itemTitle) {
        switch (itemTitle) {
            case "Tasks":
                return this.tasksFragment;
            case "Groups":
                return this.groupsFragment;
            case "Friends":
                return this.friendsFragment;
            case "Profile":
                return this.profileFragment;
        }
        return null;
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout, fragment).commit();
    }
}