package edu.northeastern.numadsp23_team20;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
    private ValueEventListener valueEventListener;
    private GroupServiceTaskCreateListener groupServiceTaskCreateListener;
    private GroupServiceReadParticipantsListener groupServiceReadParticipantsListener;

    public GroupService() {
        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseUser = mAuth.getCurrentUser();
    }

    public void setGroupServiceListener(GroupServiceListener groupServiceListener) {
        this.groupServiceListener = groupServiceListener;
    }

    public void setGroupServiceTaskCreateListener(GroupServiceTaskCreateListener groupServiceTaskCreateListener) {
        this.groupServiceTaskCreateListener = groupServiceTaskCreateListener;
    }

    public void setGroupServiceReadParticipantsListener(GroupServiceReadParticipantsListener groupServiceReadParticipantsListener) {
        this.groupServiceReadParticipantsListener = groupServiceReadParticipantsListener;
    }

    public String getFirebaseUserUID() {
        return firebaseUser.getUid();
    }

    public void createGroup(Group group) {
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Groups/"
                + group.getUuid());
        this.ref.setValue(group);
        String currentUserUUID = firebaseUser.getUid();
        group.getGroupParticipants().add(currentUserUUID);
        for (String participantUUID : group.getGroupParticipants()) {
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

    public void editGroupName(String groupUUID, String updatedGroupName) {
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Groups/" + groupUUID);
        this.ref.child("groupName").setValue(updatedGroupName);
    }

    public void leaveGroup(String groupID) {
        String userId = this.firebaseUser.getUid();
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("GeoNotif/Groups/" + groupID);
        groupsRef.child("groupParticipants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> groupParticipants = new ArrayList<>();
                groupParticipants = (List<String>) dataSnapshot.getValue();
                // Handle the case where there are no group participants
                if (groupParticipants == null || groupParticipants.isEmpty()) {
                    groupParticipants = new ArrayList<>();
                }
                // Find the index of the participant to remove
                int indexToRemove = groupParticipants.indexOf(userId);
                // Remove the participant from the list
                if (indexToRemove >= 0) {
                    groupParticipants.remove(indexToRemove);
                }
                // Update the group participants list in the database
                groupsRef.child("groupParticipants").setValue(groupParticipants);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
        DatabaseReference userGroupref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId);
        userGroupref.child("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> groupUUIDs = new ArrayList<>();
                groupUUIDs = (List<String>) dataSnapshot.getValue();
                // Handle the case where there are no group participants
                if (groupUUIDs == null || groupUUIDs.isEmpty()) {
                    groupUUIDs = new ArrayList<>();
                }
                // Find the index of the participant to remove
                int indexToRemove = groupUUIDs.indexOf(groupID);
                // Remove the participant from the list
                if (indexToRemove >= 0) {
                    groupUUIDs.remove(indexToRemove);
                }
                // Update the group participants list in the database
                userGroupref.child("Groups").setValue(groupUUIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    public void addTaskToGroup(String groupUuid, Task task) {
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Groups/"
                + groupUuid + "/Tasks");
        String newTaskKey = this.ref.push().getKey();
        this.ref.child(newTaskKey).setValue(task.getUuid());
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
                        groupParticipants = (ArrayList<String>) groupChildren.getValue();
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
                groupServiceTaskCreateListener.onTaskCreated(task.getUuid());
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
                for (String groupUUID : groupUUIDs) {
                    DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference(
                            "GeoNotif/Groups/" + groupUUID);
                    groupRef.get().addOnCompleteListener(group -> {
                        if (!group.isSuccessful()) {
                            Log.e("firebase", "Error getting data", group.getException());
                        } else {
                            Group g = group.getResult().getValue(Group.class);
                            groupServiceListener.onUserGroupLoaded(g);
//                            for (DataSnapshot groupDetails : group.getResult().getChildren()) {
//                                if (groupDetails.getKey().equals("groupName")) {
//                                    Group g = group.getResult().getValue(Group.class);
//                                    groupServiceListener.onUserGroupLoaded(g);
//                                }
//                            }
                        }
                    });
                }
            }
        });
    }

    public void readParticipantsForGroup(String groupUUID) {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference(
                "GeoNotif/Groups/" + groupUUID + "/groupParticipants");
        groupRef.get().addOnCompleteListener(group -> {
            if (!group.isSuccessful()) {
                Log.e("firebase", "Error getting data", group.getException());
            } else {
                List<String> participantUUIDs = (List<String>) group.getResult().getValue();
                for (String participantUUID : participantUUIDs) {
                    DatabaseReference participantRef = FirebaseDatabase.getInstance().getReference(
                            "GeoNotif/Users/" + participantUUID);
                    participantRef.get().addOnCompleteListener(participant -> {
                        if (!participant.isSuccessful()) {
                            Log.e("firebase", "Error getting data", participant.getException());
                        } else {
                            for (DataSnapshot userDetails : participant.getResult().getChildren()) {
                                if (userDetails.getKey().equals("fullname")) {
                                    groupServiceReadParticipantsListener.onParticipantRead(userDetails.getValue().toString());
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public interface GroupServiceListener {
        void onUserGroupLoaded(Group group);

        void onGroupCreated(Group group);
    }

    public interface GroupServiceTaskCreateListener {
        void onTaskCreated(String taskUUID);
    }

    public interface GroupServiceReadParticipantsListener {
        void onParticipantRead(String participant);
    }
}