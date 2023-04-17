package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class GroupSettingsView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings_view);
        Intent intent = new Intent();
        String groupName = intent.getStringExtra("groupName");
        // Set the group name as the text of the TextView
        TextView groupNameTextView = findViewById(R.id.groupName);
        groupNameTextView.setText(groupName);
    }

}