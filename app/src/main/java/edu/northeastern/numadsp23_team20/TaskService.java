package edu.northeastern.numadsp23_team20;

import android.annotation.SuppressLint;
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
import java.util.HashSet;
import java.util.List;

public class TaskService {

    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private TaskServiceListener taskServiceListener;
    private TaskServiceDeleteListener taskServiceDeleteListener;
    private TaskServiceCreateListener taskServiceCreateListener;

    private DatabaseReference ref;
    private GeoFire geoFire;
    private ValueEventListener valueEventListener;

    public TaskService() {
        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseUser = mAuth.getCurrentUser();
        this.taskServiceListener = null;
        this.taskServiceDeleteListener = null;
        this.taskServiceCreateListener = null;
    }

    public void setTaskServiceListener(TaskServiceListener taskServiceListener) {
        this.taskServiceListener = taskServiceListener;
    }

    public void setTaskServiceDeleteListener(TaskServiceDeleteListener taskServiceDeleteListener) {
        this.taskServiceDeleteListener = taskServiceDeleteListener;
    }

    public void setTaskServiceCreateListener(TaskServiceCreateListener taskServiceCreateListener) {
        this.taskServiceCreateListener = taskServiceCreateListener;
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
                List<String> taskUUIDs = new ArrayList<>();
                taskUUIDs = (List<String>) snapshot.getValue();
                if (taskUUIDs == null || taskUUIDs.isEmpty()) {
                    taskUUIDs = new ArrayList<>();
                }
                addUserTaskList(taskUUIDs, task.getUuid());
                taskServiceCreateListener.onTaskCreated(task.getUuid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //adding new group Tasks.
    public void createGroupTask(Task task, String groupID, ArrayList<String> groupParticipants) {
        //String userId = this.firebaseUser.getUid();
        //adding the location for each group user
        for(String userId : groupParticipants) {
            this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Locations");
            this.geoFire = new GeoFire(this.ref);
            this.geoFire.setLocation(task.getUuid(), new GeoLocation(
                    task.getLocation().getLat(), task.getLocation().getLon()));
        }
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Tasks/" + task.getUuid());
        this.ref.setValue(task);
        //adding new tasks to a group
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Groups/" + groupID + "/Tasks");
        this.valueEventListener = this.ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> taskUUIDs = new ArrayList<>();
                taskUUIDs = (List<String>) snapshot.getValue();
                if (taskUUIDs == null || taskUUIDs.isEmpty()) {
                    taskUUIDs = new ArrayList<>();
                }
                addGroupTaskList(taskUUIDs, task.getUuid(), groupID);
                taskServiceCreateListener.onTaskCreated(task.getUuid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        for(String userid : groupParticipants) {
            this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userid + "/Tasks");
            this.valueEventListener = this.ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<String> taskUUIDs = new ArrayList<>();
                    taskUUIDs = (List<String>) snapshot.getValue();
                    if (taskUUIDs == null || taskUUIDs.isEmpty()) {
                        taskUUIDs = new ArrayList<>();
                    }
                    addUserGroupTaskList(taskUUIDs, task.getUuid(), userid);
                    taskServiceCreateListener.onTaskCreated(task.getUuid());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    public void addUserTaskList(List<String> tasks, String uuid) {
        tasks.add(uuid);
        List<String> newTasks = new ArrayList<>(new HashSet<>(tasks));
        String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Tasks");
        this.ref.setValue(newTasks);
        this.ref.removeEventListener(this.valueEventListener);
    }

    //to add group task uuids to each user in the group
    public void addUserGroupTaskList(List<String> tasks, String uuid, String userID) {
        tasks.add(uuid);
        List<String> newTasks = new ArrayList<>(new HashSet<>(tasks));
        //String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userID + "/Tasks");
        this.ref.setValue(newTasks);
        this.ref.removeEventListener(this.valueEventListener);
    }

    // add the group task uuids to each group
    public void addGroupTaskList(List<String> tasks, String uuid, String groupID) {
        tasks.add(uuid);
        List<String> newTasks = new ArrayList<>(new HashSet<>(tasks));
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Groups/" + groupID + "/Tasks");
        this.ref.setValue(newTasks);
        this.ref.removeEventListener(this.valueEventListener);
    }

    public void readTask(String taskUUID) {
        String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference(
                "GeoNotif/Tasks/" + taskUUID);
        this.ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Task t = task.getResult().getValue(Task.class);
                taskServiceListener.onTaskLoaded(t);
            }
        });
    }

    public void readTasks() {
        String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Tasks");
        this.ref.get().addOnCompleteListener(tasks -> {
            if (!tasks.isSuccessful()) {
                Log.e("firebase", "Error getting data", tasks.getException());
            } else {
                if (!tasks.getResult().hasChildren()) {
                    taskServiceListener.onTaskLoaded((Task) null);
                }
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
    }

    public void editTask(Task task, Task updatedTask) {
        String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Tasks/" + updatedTask.getUuid());
        this.ref.setValue(updatedTask);
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Locations");
        this.geoFire = new GeoFire(this.ref);
        this.geoFire.setLocation(updatedTask.getUuid(), new GeoLocation(
                updatedTask.getLocation().getLat(), updatedTask.getLocation().getLon()));
    }

    public void editGroupTask(Task task, Task updatedTask, String groupId, ArrayList<String> groupParticipants) {
        //String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Groups/Tasks/" + updatedTask.getUuid());
        this.ref.setValue(updatedTask);
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Tasks/" + updatedTask.getUuid());
        this.ref.setValue(updatedTask);
        for(String userId:groupParticipants) {
            this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Locations");
            this.geoFire = new GeoFire(this.ref);
            this.geoFire.setLocation(updatedTask.getUuid(), new GeoLocation(
                    updatedTask.getLocation().getLat(), updatedTask.getLocation().getLon()));
        }
    }



    public void removeUserTaskList(List<String> tasks, String uuid) {
        tasks.remove(uuid);
        String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Tasks");
        this.ref.setValue(tasks);
        this.ref.removeEventListener(this.valueEventListener);
    }

    //remove a task uuid from Groups Node
    public void removeGroupTaskList(List<String> tasks, String uuid, String groupID) {
        tasks.remove(uuid);
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Groups/" + groupID + "/Tasks");
        this.ref.setValue(tasks);
        this.ref.removeEventListener(this.valueEventListener);
    }

    //remove task uuid from each user in the group
    public void removeUserGroupTaskList(List<String> tasks, String uuid, String userID) {
        tasks.remove(uuid);
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userID + "/Tasks");
        this.ref.setValue(tasks);
        this.ref.removeEventListener(this.valueEventListener);
    }

    public void deleteTask(String taskUUID) {
        String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Tasks/" + taskUUID);
        this.ref.removeValue();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Locations/"
                + taskUUID);
        this.ref.removeValue();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userId + "/Tasks");
        this.valueEventListener = this.ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> taskUUIDs = new ArrayList<>();
                taskUUIDs = (List<String>) snapshot.getValue();
                if (taskUUIDs == null || taskUUIDs.isEmpty()) {
                    taskUUIDs = new ArrayList<>();
                }
                removeUserTaskList(taskUUIDs, taskUUID);
                taskServiceDeleteListener.onTaskDeleted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void deleteGroupTask(String taskUUID, String groupID, ArrayList<String> groupParticipants) {
        //String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Tasks/" + taskUUID);
        this.ref.removeValue();
        for (String userid:groupParticipants) {
            this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userid + "/Locations/"
                    + taskUUID);
            this.ref.removeValue();
        }

        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Groups/" + groupID + "/Tasks");
        this.valueEventListener = this.ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> taskUUIDs = new ArrayList<>();
                taskUUIDs = (List<String>) snapshot.getValue();
                if (taskUUIDs == null || taskUUIDs.isEmpty()) {
                    taskUUIDs = new ArrayList<>();
                }
                removeGroupTaskList(taskUUIDs, taskUUID, groupID);
                taskServiceDeleteListener.onTaskDeleted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        for (String userid:groupParticipants) {
            this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/" + userid + "/Tasks");
            this.valueEventListener = this.ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<String> taskUUIDs = new ArrayList<>();
                    taskUUIDs = (List<String>) snapshot.getValue();
                    if (taskUUIDs == null || taskUUIDs.isEmpty()) {
                        taskUUIDs = new ArrayList<>();
                    }
                    removeUserGroupTaskList(taskUUIDs, taskUUID,userid);
                    taskServiceDeleteListener.onTaskDeleted();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public interface TaskServiceCreateListener {
        void onTaskCreated(String taskUUID);
    }

    public interface TaskServiceListener {
        void onTaskLoaded(Task task);

        @SuppressLint("NotifyDataSetChanged")
        void onTaskLoaded(com.google.android.gms.tasks.Task task);
    }

    public interface TaskServiceDeleteListener {
        void onTaskDeleted();
    }
}
