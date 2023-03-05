package edu.northeastern.numadsp23_team20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class ChooseChat extends AppCompatActivity {
    MyAdapter myAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    String currUsername;
    Intent intent;
    private String[] titles= new String[]{"Chats","Stickers"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_chat);
        tabLayout = findViewById(R.id.tabs);
        viewPager2 = findViewById(R.id.view_pager);
        myAdapter = new MyAdapter(this);
        viewPager2.setAdapter(myAdapter);
        new TabLayoutMediator(tabLayout,viewPager2,((tab, position) -> tab.setText(titles[position]))).attach();
        intent = getIntent();
        currUsername = intent.getStringExtra("username");
        System.out.println(currUsername);
    }

    public Bundle getMyData() {
        Bundle bundle = new Bundle();
        bundle.putString("current_user", currUsername);
        return bundle;
    }
}