package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomePage extends AppCompatActivity {

    private Fragment tasksFragment;
    private Fragment groupsFragment;
    private Fragment friendsFragment;
    private Fragment profileFragment;

    private static final String TASKS_FRAGMENT_TAG = "TASKS_FRAGMENT_TAG";
    private static final String GROUPS_FRAGMENT_TAG = "GROUPS_FRAGMENT_TAG";
    private static final String FRIENDS_FRAGMENT_TAG = "FRIENDS_FRAGMENT_TAG";
    private static final String PROFILE_FRAGMENT_TAG = "PROFILE_FRAGMENT_TAG";
    private String currentFragmentTag;

    private GeoNotif settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        this.settings = (GeoNotif) getApplication();
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

        if (savedInstanceState != null) {
            // Restore the previously selected fragment
            currentFragmentTag = savedInstanceState.getString("FRAGMENT_TAG");
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(currentFragmentTag);
            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.BottomNavigationMenu, fragment, currentFragmentTag)
                        .commit();
            }
        }
        loadSharedPreferences();
        setSettings();
    }

    private void setSettings() {
        System.out.println(this.settings.getNotifSetting());
        System.out.println(this.settings.getNotifSetting().equalsIgnoreCase(GeoNotif.ENABLE_NOTIF_SETTING));
        if (this.settings.getNotifSetting().equalsIgnoreCase(GeoNotif.ENABLE_NOTIF_SETTING)) {
            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
        }
    }

    private void loadSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(GeoNotif.PREFERENCES, MODE_PRIVATE);
        String notifSetting = sharedPreferences.getString(GeoNotif.NOTIF_SETTING, GeoNotif.ENABLE_NOTIF_SETTING);
        this.settings.setNotifSetting(notifSetting);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the tag of the current fragment
        outState.putString("FRAGMENT_TAG", currentFragmentTag);
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
                this.currentFragmentTag = TASKS_FRAGMENT_TAG;
                return this.tasksFragment;
            case "Groups":
                this.currentFragmentTag = GROUPS_FRAGMENT_TAG;
                return this.groupsFragment;
            case "Friends":
                this.currentFragmentTag = FRIENDS_FRAGMENT_TAG;
                return this.friendsFragment;
            case "Profile":
                this.currentFragmentTag = PROFILE_FRAGMENT_TAG;
                return this.profileFragment;
        }
        return null;
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout, fragment).commit();
    }
}