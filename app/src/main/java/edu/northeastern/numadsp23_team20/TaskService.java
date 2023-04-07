package edu.northeastern.numadsp23_team20;

import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

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
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/" + userId + "/locations");
        this.geoFire = new GeoFire(this.ref);
        this.geoFire.setLocation(task.getLocation().getKey(), new GeoLocation(
                task.getLocation().getLat(), task.getLocation().getLon()));

        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/" + userId + "/tasks/"
                + task.getTaskName());
        this.ref.setValue(task);
    }

    public void readTasks() {
        String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/" + userId + "/tasks");
        this.ref.get().addOnCompleteListener(tasks -> {
            if (!tasks.isSuccessful()) {
                Log.e("firebase", "Error getting data", tasks.getException());
            }
            else {
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
                    tasksList.add(task);
                }
                taskServiceListener.onTasksLoaded(tasksList);
            }
        });
    }

    public void editTask(Task task, Task updatedTask) {
        String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/" + userId + "/tasks/"
                + task.getTaskName());
        this.ref.setValue(updatedTask);
    }

    public void deleteTask(String taskName) {
        String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/" + userId + "/tasks/"
                + taskName);
        this.ref.removeValue();
    }

    public interface TaskServiceListener {
        void onTasksLoaded(List<Task> tasks);
    }
}
