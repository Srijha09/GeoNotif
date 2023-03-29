package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomePage extends AppCompatActivity {

    private Fragment tasksFragment;
    private Fragment groupsFragment;
    private Fragment friendsFragment;
    private Fragment profileFragment;

    private TaskService taskService = new TaskService();

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
        taskService.createTask(new Task("task1", "desc1",
                new LocationItem("target", 42.344847632021015, -71.09957277886753)));
        taskService.createTask(new Task("task2", "desc2",
                new LocationItem("sad", 42.344847632021015, -71.09957277886753)));
        taskService.createTask(new Task("task3", "desc33",
                new LocationItem("tasty", 42.344974507513825, -71.09819948797578)));
    }

    private void checkLocationPermissions() {
        boolean noFineLocationAccess = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean noCoarseLocationAccess = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        if (noFineLocationAccess && noCoarseLocationAccess) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    101);
        }
    }

    private Fragment getFragment(String itemTitle) {
        System.out.println(itemTitle);
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