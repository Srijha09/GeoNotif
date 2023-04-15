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

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskService {

    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private TaskServiceListener taskServiceListener;

    private DatabaseReference ref;
    private GeoFire geoFire;
    private ValueEventListener valueEventListener;

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
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Locations");
        this.geoFire = new GeoFire(this.ref);
        this.geoFire.setLocation(task.getUuid(), new GeoLocation(
                task.getLocation().getLat(), task.getLocation().getLon()));
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Tasks/" + task.getUuid());
        this.ref.setValue(task);
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Tasks");
        this.valueEventListener = this.ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("-----------" + snapshot.getValue());
                List<String> taskUUIDs = new ArrayList<>();
                taskUUIDs = (List<String>) snapshot.getValue();
                if (taskUUIDs == null || taskUUIDs.isEmpty()) {
                    taskUUIDs = new ArrayList<>();
                }
                updateUserTaskList(taskUUIDs, task.getUuid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateUserTaskList(List<String> tasks, String uuid) {
        System.out.println(tasks);
        tasks.add(uuid);
        String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Tasks");
        this.ref.setValue(tasks);
        this.ref.removeEventListener(this.valueEventListener);
    }

    public void readTasks() {
        String userId = this.firebaseUser.getUid();
//        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Tasks");
//        this.ref.get().addOnCompleteListener(tasks -> {
//            if (!tasks.isSuccessful()) {
//                Log.e("firebase", "Error getting data", tasks.getException());
//            } else {
//                List<Task> tasksList = new ArrayList<>();
//                for (DataSnapshot item : tasks.getResult().getChildren()) {
//                    Task task = new Task();
//                    task.setTaskName(item.child("taskName").getValue().toString());
//                    task.setDescription(item.child("description").getValue().toString());
//                    String key = "";
//                    double lat = 0.0;
//                    double lon = 0.0;
//                    for (DataSnapshot locationDetail : item.child("location").getChildren()) {
//                        if (locationDetail.getKey().equals("key")) {
//                            key = locationDetail.getValue().toString();
//                        }
//                        if (locationDetail.getKey().equals("lat")) {
//                            lat = (double) locationDetail.getValue();
//                        }
//                        if (locationDetail.getKey().equals("lon")) {
//                            lon = (double) locationDetail.getValue();
//                        }
//                        LocationItem locationItem = new LocationItem(key, lat, lon);
//                        task.setLocation(locationItem);
//                    }
//                    task.setIsComplete((Boolean) item.child("isComplete").getValue());
//                    tasksList.add(task);
//                }
//                taskServiceListener.onTasksLoaded(tasksList);
//            }
//        });
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Tasks");
        this.ref.get().addOnCompleteListener(tasks -> {
            if (!tasks.isSuccessful()) {
                Log.e("firebase", "Error getting data", tasks.getException());
            } else {
                System.out.println(tasks.getResult().getValue());
                for (DataSnapshot item : tasks.getResult().getChildren()) {
                    String taskUUID = item.getValue().toString();
                    DatabaseReference readRef = FirebaseDatabase.getInstance().getReference("GeoNotif/Tasks/" + taskUUID);
                    readRef.get().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            Task t = task.getResult().getValue(Task.class);
                            taskServiceListener.onTaskLoaded(t);
                        }
                    });
                }
            }
        });
//        this.valueEventListener = this.ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                System.out.println("-----------" + snapshot.getValue());
//                List<String> taskUUIDs = new ArrayList<>();
//                taskUUIDs = (List<String>) snapshot.getValue();
//                if (taskUUIDs == null || taskUUIDs.isEmpty()) {
//
//                } else {
//                    for (String uuid : taskUUIDs) {
//                        DatabaseReference readRef = FirebaseDatabase.getInstance().getReference("GeoNotif/Tasks/" + uuid);
//                        readRef.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                Task t = snapshot.getValue(Task.class);
//                                System.out.println(t.toString());
//                                tasksList.add(t);
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//                    }
//                    System.out.println(tasksList.size());
//                    taskServiceListener.onTasksLoaded(tasksList);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    public void editTask(Task task, Task updatedTask) {
        String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Tasks/" + task.getUuid());
        this.ref.setValue(updatedTask);
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Locations");
        this.geoFire = new GeoFire(this.ref);
        this.geoFire.setLocation(updatedTask.getUuid(), new GeoLocation(
                updatedTask.getLocation().getLat(), updatedTask.getLocation().getLon()));
    }

    public void deleteTask(String taskUUID) {
        String userId = this.firebaseUser.getUid();
        System.out.println(taskUUID);
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Tasks/" + taskUUID);
        this.ref.removeValue();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Locations/"
                + taskUUID);
        this.ref.removeValue();
    }

    public interface TaskServiceListener {
        void onTaskLoaded(Task task);
    }
}
