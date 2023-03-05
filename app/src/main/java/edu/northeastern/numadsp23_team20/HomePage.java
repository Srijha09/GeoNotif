package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePage extends AppCompatActivity {

    private Fragment tasksFragment;
    private Fragment groupsFragment;
    private Fragment friendsFragment;
    private Fragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        this.tasksFragment = new TasksFragment();
        this.groupsFragment = new GroupsFragment();
        this.friendsFragment = new FriendsFragment();
        this.settingsFragment = new SettingsFragment();
        BottomNavigationView bottomNavigationMenu = findViewById(R.id.BottomNavigationMenu);
        this.setFragment(this.tasksFragment);
        bottomNavigationMenu.setOnItemSelectedListener(item -> {
            this.setFragment(this.getFragment(item.getTitle().toString()));
            return true;
        });
    }

    private Fragment getFragment(String itemTitle) {
        System.out.println(itemTitle);
        switch (itemTitle) {
            case "Tasks": return this.tasksFragment;
            case "Groups": return this.groupsFragment;
            case "Friends": return this.friendsFragment;
            case "Settings": return this.settingsFragment;
        }
        return null;
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout,fragment).commit();
    }
}