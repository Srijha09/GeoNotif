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

    public GroupService() {
        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseUser = mAuth.getCurrentUser();
    }

    public void createGroup(Group group) {
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Groups/"
                + group.getUuid());
        this.ref.setValue(group);
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
}