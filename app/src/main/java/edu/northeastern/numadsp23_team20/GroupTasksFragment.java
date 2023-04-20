package edu.northeastern.numadsp23_team20;
import com.google.android.gms.tasks.OnCompleteListener;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.tasks.Task;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.api.IMapController;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

public class GroupTasksFragment extends Fragment implements GroupSettingsView.GroupNameChangedListener{

    Context ctx;
    private MapView map;
    private IMapController mapController;
    private ActivityResultLauncher<Intent> addTaskActivityLaunch;
    private TaskService taskService;
    private List<Task> taskList;
    private ImageButton settings;
    private String groupName;

    static FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    private static ArrayList<edu.northeastern.numadsp23_team20.Task> groupTasks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        groupTasks = new ArrayList<edu.northeastern.numadsp23_team20.Task>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inflatedView = inflater.inflate(R.layout.fragment_group_tasks, container, false);
        assert getArguments() != null;
        String groupId = getArguments().getString("groupUUID");
        groupName = getArguments().getString("groupName");
        ArrayList<String> groupParticipants = getArguments().getStringArrayList("groupParticipants");
        Integer groupParticipantsNo = getArguments().getInt("groupParticipantsNo");
        // Set the group name as the text of the TextView
        TextView groupNameTextView = inflatedView.findViewById(R.id.groupName);
        groupNameTextView.setText(groupName);
        settings = inflatedView.findViewById(R.id.settings_button);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a new intent to open the new activity
                Intent intent = new Intent(getContext(), GroupSettingsView.class);
                intent.putExtra("groupUUID", groupId);
                intent.putExtra("groupName", groupName);
                intent.putExtra("groupParticipantsNo", groupParticipantsNo);
                intent.putExtra("groupParticipants", groupParticipants);
                startActivity(intent);
            }
        });
        //query all the tasks
        //fetch the tasks where the group name is the current group name
        String newGroupName = "Group task: " + groupName;
        DatabaseReference userFriendsRef = FirebaseDatabase.getInstance().getReference("GeoNotif/Tasks/");
        userFriendsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> userFriends) {
                if (!userFriends.isSuccessful()) {
                    Log.e("firebase", "Error getting data", userFriends.getException());
                } else {
                    for (DataSnapshot childSnapshot : userFriends.getResult().getChildren()) {
                        String groupName = childSnapshot.child("taskTypeString").getValue(String.class);
                        if (groupName.equals(newGroupName)) {
                            String description = childSnapshot.child("description").getValue(String.class);
                            Boolean isComplete = childSnapshot.child("isComplete").getValue(Boolean.class);
                            String taskName = childSnapshot.child("taskName").getValue(String.class);
                            String uuid = childSnapshot.child("uuid").getValue(String.class);

                            String locationKey = childSnapshot.child("location").child("key").getValue(String.class);
                            double locationLat = childSnapshot.child("location").child("lat").getValue(Double.class);
                            double locationLon = childSnapshot.child("location").child("lon").getValue(Double.class);
                            LocationItem location = new LocationItem(locationKey,locationLat, locationLon);

                            edu.northeastern.numadsp23_team20.Task addTask =  new edu.northeastern.numadsp23_team20.Task(taskName, description, location, uuid, isComplete);
                            groupTasks.add(addTask);
                        }
                    }

                }
            }
        });

        return inflatedView;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GroupSettingsView) {
            ((GroupSettingsView) context).setGroupNameChangedListener(this);
        }
    }

    @Override
    public void onGroupNameChanged(String newGroupName) {
        this.groupName = newGroupName;
    }



}