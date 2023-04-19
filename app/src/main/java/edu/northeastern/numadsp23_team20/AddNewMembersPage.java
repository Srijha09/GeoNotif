package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class AddNewMembersPage extends AppCompatActivity {
    private ImageButton settings;
    private Button addNewMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);
        Intent intent = new Intent();
        String groupName = intent.getStringExtra("groupName");
        // Set the group name as the text of the TextView
        TextView groupNameTextView = findViewById(R.id.groupName);
        groupNameTextView.setText(groupName);
        addNewMembers = findViewById(R.id.addmember_bttn);
        addNewMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddMembersList.class);
                //intent.putExtra("groupName", groupName);
                startActivity(intent);
            }
        });

        settings = findViewById(R.id.settings_button);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a new intent to open the new activity
                Intent intent = new Intent(getApplicationContext(), GroupSettingsView.class);
                intent.putExtra("groupName", groupName);
                startActivity(intent);
            }
        });
    }
}