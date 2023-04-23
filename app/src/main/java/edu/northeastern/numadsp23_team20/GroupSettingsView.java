package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

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
        this.groupID = intent.getStringExtra("groupUUID");
        this.groupName = intent.getStringExtra("groupName");
        this.groupParticipants = intent.getStringArrayListExtra("groupParticipants");
        this.groupParticipantsNo = intent.getIntExtra("groupParticipantsNo", 1);
        // Set the group name as the text of the TextView
        TextView groupNameTextView = findViewById(R.id.groupName);
        groupNameTextView.setText(groupName);
        this.groupService = new GroupService();
        // Find the group with the matching name in groupsList
        this.group = new Group(groupName, groupParticipants);
        this.group.setUuid(groupID);
        this.edit = findViewById(R.id.donebttn);
        this.edit.setOnClickListener(v -> editGroupAlertDialog(group));
        Button addMember = findViewById(R.id.addmember_bttn);
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddMembersList.class);
                intent.putExtra("groupUUID", groupID);
                intent.putExtra("groupName", groupName);
                intent.putExtra("groupParticipantsNo", groupParticipantsNo);
                intent.putExtra("groupParticipants", groupParticipants);
                startActivity(intent);
            }
        });
        this.leaveBttn = findViewById(R.id.leave_bttn);
        this.leaveBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveGroupAlertDialog();
            }
        });
    }

    public void editGroupAlertDialog(Group group) {
        View view = getLayoutInflater().inflate(R.layout.editgroupname_dialog, null);
        EditText group_name = view.findViewById(R.id.editgroupname);
        group_name.setText(this.groupName);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(GroupSettingsView.this);
        alertDialog.setView(view);

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

            Group updatedGroup = group;
            updatedGroup.setGroupName(newGroupName);
            this.groupService.editGroupName(updatedGroup.getUuid(), updatedGroup.getGroupName());
            alert.dismiss();
        });
    }

    public void leaveGroupAlertDialog() {
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