package edu.northeastern.numadsp23_team20;

import static android.text.TextUtils.isEmpty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
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
    private Button leaveBttn;
    private GroupService groupService;
    private Group group;
    private String groupID;
    private String groupName;
    private Integer groupParticipantsNo;
    private ArrayList<String> groupParticipants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings_view);
        Intent intent = getIntent();
        groupID = intent.getStringExtra("groupUUID");
        String groupName = intent.getStringExtra("groupName");
        groupParticipants = intent.getStringArrayListExtra("groupParticipants");
        groupParticipantsNo = intent.getIntExtra("groupParticipantsNo", 1);
        System.out.println(groupParticipantsNo);
        // Set the group name as the text of the TextView
        TextView groupNameTextView = findViewById(R.id.groupName);
        groupNameTextView.setText(groupName);
        groupService = new GroupService();
        // Find the group with the matching name in groupsList
        this.group = new Group(groupName, groupParticipants);
        edit = findViewById(R.id.donebttn);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editGroupAlertDialog(group);
            }
        });
        Button addMember = findViewById(R.id.addmember_bttn);
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddMembersList.class);
                intent.putExtra("groupUUID", groupID);
                intent.putExtra("groupName", groupName);
                intent.putExtra("groupParticipantsNo", groupParticipantsNo);
                intent.putExtra("groupParticipants", groupParticipants);
                //intent.putExtra("loggedInUsername", GroupsAdapter.this.currentUser);
                startActivity(intent);
            }
        });
        leaveBttn = findViewById(R.id.leave_bttn);
        leaveBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveGroupAlertDialog();
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

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view1 -> {
            String newGroupName = group_name.getText().toString();
            TextView groupNameTextView = findViewById(R.id.groupName);
            groupNameTextView.setText(newGroupName);
//            if (groupNameChangedListener != null) {
//                groupNameChangedListener.onGroupNameChanged(newGroupName);
//            }
            Group updatedGroup = new Group(newGroupName, groupParticipants);
            group.setUuid(groupID);
            updatedGroup.setUuid(groupID);
            groupService.editGroup(group, updatedGroup);
            alert.dismiss();
        });
    }

    public void leaveGroupAlertDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Are you sure you want to leave this group?")
                .setIcon(R.drawable.warning)
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        groupService.leaveGroup(groupID);
                        finish();

                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();

    }


}