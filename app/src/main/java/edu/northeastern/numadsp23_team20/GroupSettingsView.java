package edu.northeastern.numadsp23_team20;

import static android.text.TextUtils.isEmpty;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupSettingsView extends AppCompatActivity {
    private Button edit;
    private GroupService groupService;
    private Group group;
    private String groupName;
    private int numParticipants;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings_view);
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        // Set the group name as the text of the TextView
        TextView groupNameTextView = findViewById(R.id.groupName);
        groupNameTextView.setText(groupName);
        groupService = new GroupService();
        GroupsFragment groupsFragment = new GroupsFragment();
        List<Group> groupsList = groupsFragment.getGroupsList();
        // Find the group with the matching name in groupsList
        System.out.println(groupsList);
        System.out.println(groupName);
        for (Group g : groupsList) {
            if (g.getGroupName().equals(groupName)) {
                group = g;
                numParticipants = g.getGroupParticipants().size();
                break;
            }
        }
        groupService = new GroupService();
        this.group = new Group(groupName, numParticipants);
        edit = findViewById(R.id.donebttn);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editGroupAlertDialog(group);
            }
        });
    }

    public void editGroupAlertDialog(Group group) {
        View view = getLayoutInflater().inflate(R.layout.editgroupname_dialog, null);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(GroupSettingsView.this);
        alertDialog.setView(view);
        EditText group_name = view.findViewById(R.id.editgroupname);
        alertDialog.setPositiveButton("DONE", (dialogInterface, i) -> {

        });
        alertDialog.setNegativeButton("CANCEL", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
        List<String> groupParticipants = new ArrayList<>();
        String currentUserUUID = groupService.getFirebaseUserUID();
        groupParticipants.add(currentUserUUID);

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view1 -> {
            String newGroupName = group_name.getText().toString().trim();
            if (newGroupName.isEmpty()) {
                group_name.setError("Group name cannot be empty");
            } else {
                // Create a new Group object with the updated group name
                Group updatedGroup = new Group(newGroupName, groupParticipants);
                GroupService groupService = new GroupService();
                groupService.editGroup(group, updatedGroup);
                alert.dismiss();
            }
        });
    }



}