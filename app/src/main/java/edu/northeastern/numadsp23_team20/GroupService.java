package edu.northeastern.numadsp23_team20;

import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupService {
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private GroupServiceListener groupServiceListener;

    public GroupService() {
        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseUser = mAuth.getCurrentUser();
    }

    public void setGroupServiceListener(GroupServiceListener groupServiceListener) {
        this.groupServiceListener = groupServiceListener;
    }

    public void createGroup(Group group) {
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Groups/"
                + group.getUuid());
        this.ref.setValue(group);
        for (String participantUUID: group.getGroupParticipants()) {
            DatabaseReference userGroupsRef = FirebaseDatabase.getInstance().getReference(
                    "GeoNotif/Users/" + participantUUID + "/Groups");
            userGroupsRef.get().addOnCompleteListener(userGroups -> {
                if (!userGroups.isSuccessful()) {
                    Log.e("firebase", "Error getting data", userGroups.getException());
                } else {
                    List<String> groupUUIDs = (List<String>) userGroups.getResult().getValue();
                    if (groupUUIDs == null || groupUUIDs.isEmpty()) {
                        groupUUIDs = new ArrayList<>();
                    }
                    groupUUIDs.add(group.getUuid());
                    userGroupsRef.setValue(groupUUIDs);
                }
            });
        }
    }

    public void editGroup(Group group, Group updatedGroup) {
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Groups/" + group.getGroupName());
        this.ref.setValue(updatedGroup);
    }

    public void addTaskToGroup(String groupUuid, Task task) {
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Groups/"
                + groupUuid);
        this.ref.get().addOnCompleteListener(group -> {
            if (!group.isSuccessful()) {
                Log.e("firebase", "Error getting data", group.getException());
            } else {
                DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference("GeoNotif/Tasks/" + task.getUuid());
                taskRef.setValue(task);
                for (DataSnapshot groupChildren : group.getResult().getChildren()) {
                    List<String> groupParticipants = new ArrayList<>();
                    if (groupChildren.getKey().equals("groupParticipants")) {
                        groupParticipants = (ArrayList) groupChildren.getValue();
                    }
                    for (String userId : groupParticipants) {
                        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Locations");
                        GeoFire geoFire = new GeoFire(locationRef);
                        geoFire.setLocation(task.getUuid(), new GeoLocation(task.getLocation().getLat(), task.getLocation().getLon()));
                        DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Tasks");
                        userTasksRef.get().addOnCompleteListener(tasks -> {
                            if (!tasks.isSuccessful()) {
                                Log.e("firebase", "Error getting data", tasks.getException());
                            } else {
                                List<String> taskUUIDs = (List<String>) tasks.getResult().getValue();
                                if (taskUUIDs == null || taskUUIDs.isEmpty()) {
                                    taskUUIDs = new ArrayList<>();
                                }
                                taskUUIDs.add(task.getUuid());
                                userTasksRef.setValue(taskUUIDs);
                            }
                        });
                    }
                }
            }
        });
    }

    public void readGroupsForUser() {
        String userUUID = this.firebaseUser.getUid();
        DatabaseReference userGroupsRef = FirebaseDatabase.getInstance().getReference(
                "GeoNotif/Users/" + userUUID + "/Groups");
        userGroupsRef.get().addOnCompleteListener(userGroups -> {
            if (!userGroups.isSuccessful()) {
                Log.e("firebase", "Error getting data", userGroups.getException());
            } else {
                List<String> groupUUIDs = (List<String>) userGroups.getResult().getValue();
                groupServiceListener.onUserGroupsLoaded(groupUUIDs);
            }
        });
    }

    public interface GroupServiceListener {
        void onUserGroupsLoaded(List<String> groups);
    }
}