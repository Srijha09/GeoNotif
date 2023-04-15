package edu.northeastern.numadsp23_team20;

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
import java.util.UUID;

public class TaskService {

    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private TaskServiceListener taskServiceListener;

    private DatabaseReference ref;
    private GeoFire geoFire;

    public TaskService() {
        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseUser = mAuth.getCurrentUser();
        this.taskServiceListener = null;
    }

    public void setTaskServiceListener(TaskServiceListener taskServiceListener) {
        this.taskServiceListener = taskServiceListener;
    }

    public void createTask(Task task) {
        String userId = this.firebaseUser.getUid();
        UUID uuid = UUID.randomUUID();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Locations");
        this.geoFire = new GeoFire(this.ref);
        this.geoFire.setLocation(uuid.toString(), new GeoLocation(
                task.getLocation().getLat(), task.getLocation().getLon()));
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Tasks/" + uuid.toString());
        this.ref.setValue(task);
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId);
//        this.ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User currentUser = snapshot.getValue(User.class);
//                currentUser.addTask(uuid.toString());
//                DatabaseReference r = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId);
//                r.setValue(currentUser);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    public void readTasks() {
        String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Tasks");
        this.ref.get().addOnCompleteListener(tasks -> {
            if (!tasks.isSuccessful()) {
                Log.e("firebase", "Error getting data", tasks.getException());
            } else {
                List<Task> tasksList = new ArrayList<>();
                for (DataSnapshot item : tasks.getResult().getChildren()) {
                    Task task = new Task();
                    task.setTaskName(item.child("taskName").getValue().toString());
                    task.setDescription(item.child("description").getValue().toString());
                    String key = "";
                    double lat = 0.0;
                    double lon = 0.0;
                    for (DataSnapshot locationDetail : item.child("location").getChildren()) {
                        if (locationDetail.getKey().equals("key")) {
                            key = locationDetail.getValue().toString();
                        }
                        if (locationDetail.getKey().equals("lat")) {
                            lat = (double) locationDetail.getValue();
                        }
                        if (locationDetail.getKey().equals("lon")) {
                            lon = (double) locationDetail.getValue();
                        }
                        LocationItem locationItem = new LocationItem(key, lat, lon);
                        task.setLocation(locationItem);
                    }
                    task.setIsComplete((Boolean) item.child("isComplete").getValue());
                    tasksList.add(task);
                }
                taskServiceListener.onTasksLoaded(tasksList);
            }
        });
    }

    public void editTask(Task task, Task updatedTask) {
        String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Tasks/" + task.getTaskName());
        this.ref.setValue(updatedTask);
    }

    public void deleteTask(String taskName) {
        String userId = this.firebaseUser.getUid();
        System.out.println(taskName);
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/" + userId + "/tasks/"
                + taskName);
        this.ref.removeValue();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/" + userId + "/locations/"
                + taskName);
        this.ref.removeValue();
    }

    public interface TaskServiceListener {
        void onTasksLoaded(List<Task> tasks);
    }
}
