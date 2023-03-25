package edu.northeastern.numadsp23_team20;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TaskService {

    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    private DatabaseReference ref;
    private GeoFire geoFire;

    public TaskService() {
        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseUser = mAuth.getCurrentUser();
    }

    public void createTask(Task task) {
        String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/" + userId + "/locations");
        this.geoFire = new GeoFire(this.ref);
        this.geoFire.setLocation(task.getLocation().getKey(), new GeoLocation(
                task.getLocation().getLat(), task.getLocation().getLon()));

        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/" + userId + "/tasks");
        this.ref.setValue(task);
    }

}
