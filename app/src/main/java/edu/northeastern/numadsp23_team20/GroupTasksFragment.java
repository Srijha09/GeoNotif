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
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
import java.util.UUID;

public class GroupTasksFragment extends Fragment implements OnTaskItemClickListener{

    Context ctx;
    private MapView map;
    private IMapController mapController;
    private ActivityResultLauncher<Intent> addTaskActivityLaunch;
    private TaskService taskService;
    private List<Task> grouptaskList;
    private boolean loadingTasks;
    private ProgressBar tasksLoadingSpinner;
    private TextView noTasksTextView;
    private ScrollView tasksScrollView;
    private GroupTasksAdapter taskListAdapter;
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

        this.loadingTasks = true;
        this.ctx = getContext();
        Configuration.getInstance().load(this.ctx, PreferenceManager.getDefaultSharedPreferences(this.ctx));
        this.map = inflatedView.findViewById(R.id.TasksMapView);
        this.mapController = this.map.getController();
        this.configureMap();
        RecyclerView tasksRecyclerView = inflatedView.findViewById(R.id.TasksRecyclerView);
        this.tasksLoadingSpinner = inflatedView.findViewById(R.id.TasksLoadingSpinner);
        this.noTasksTextView = inflatedView.findViewById(R.id.NoTasksTextView);
        this.tasksScrollView = inflatedView.findViewById(R.id.TasksScrollView);
        this.grouptaskList = new ArrayList<>();
        this.taskService = new TaskService();
        LocationItem locationItem = new LocationItem("Fenway park", 42.3467, -71.0972);
        Task task1 = new Task("Fenway test task", "Fenway test task description",
                locationItem);
        UUID taskUuid = UUID.randomUUID();
        task1.setUuid(taskUuid.toString());
        grouptaskList.add(task1);

        this.taskListAdapter = new GroupTasksAdapter(this.grouptaskList, (OnTaskItemClickListener) this);
        tasksRecyclerView.setAdapter(taskListAdapter);
        tasksRecyclerView.setHasFixedSize(true);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this.ctx));
//        this.taskService.setTaskServiceListener(new TaskService.TaskServiceListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onTaskLoaded(Task task) {
//                if (task == null) {
//                    loadingTasks = false;
//                    tasksLoadingSpinner.setVisibility(View.INVISIBLE);
//                    noTasksTextView.setVisibility(View.VISIBLE);
//                    tasksScrollView.setVisibility(View.INVISIBLE);
//                    return;
//                }
//                if (loadingTasks) {
//                    loadingTasks = false;
//                    tasksLoadingSpinner.setVisibility(View.INVISIBLE);
//                    noTasksTextView.setVisibility(View.INVISIBLE);
//                    tasksScrollView.setVisibility(View.VISIBLE);
//                }
//                //setMapMarker(task);
//                grouptaskList.add(task);
//                taskListAdapter.notifyDataSetChanged();
//            }
//        });

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

    @SuppressLint("ClickableViewAccessibility")
    private void configureMap() {
        this.map.setTileSource(TileSourceFactory.MAPNIK);
        this.mapController.setZoom(15);
        this.map.setMultiTouchControls(true);
        this.map.setClickable(true);
    }


    @Override
    public void onTaskItemClick(int position) {
//        Task task = this.grouptaskList.get(position);
//        Intent intent = new Intent(getContext(), TaskView.class);
//        intent.putExtra("position", position);
//        intent.putExtra("taskTitle", task.getTaskName());
//        intent.putExtra("taskDescription", task.getDescription());
//        intent.putExtra("taskLocation", task.getLocation().getKey());
//        intent.putExtra("taskLatitude", task.getLocation().getLat());
//        intent.putExtra("taskLongitude", task.getLocation().getLon());
//        intent.putExtra("taskComplete", task.getIsComplete());
//        intent.putExtra("taskUUID", task.getUuid());
//        intent.putExtra("taskType", task.getTaskType());
//        intent.putExtra("taskTypeString", task.getTaskTypeString());
//        this.viewTaskActivityLaunch.launch(intent);
    }



}