package edu.northeastern.numadsp23_team20;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class GroupService {
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;

    public GroupService() {
        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseUser = mAuth.getCurrentUser();
    }

    public void createGroup(Group group) {
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Groups/"
                + group.getGroupName());
        this.ref.setValue(group);
    }

    public void editGroup(Group group, Group updatedGroup) {
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Groups/" + group.getGroupName());
        this.ref.setValue(updatedGroup);
    }

    public void addTaskToGroup(String groupName, Task task) {
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Groups/"
                + groupName);
        this.ref.get().addOnCompleteListener(group -> {
            if (!group.isSuccessful()) {
                Log.e("firebase", "Error getting data", group.getException());
            } else {
                for (DataSnapshot groupChildren : group.getResult().getChildren()) {
                    List<String> groupParticipants = new ArrayList<>();
                    if (groupChildren.getKey().equals("groupParticipants")) {
                        groupParticipants = (ArrayList) groupChildren.getValue();
                    }
                    for (String userId : groupParticipants) {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(
                                "GeoNotif/" + userId + "/tasks/" + task.getTaskName());
                        userRef.setValue(task);
                    }
                }
            }
        });
    }
}