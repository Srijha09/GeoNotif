package edu.northeastern.numadsp23_team20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TasksFragment extends Fragment implements OnTaskItemClickListener {

    Context ctx;
    private MapView map;
    private IMapController mapController;
    private ActivityResultLauncher<Intent> addTaskActivityLaunch;
    private ActivityResultLauncher<Intent> viewTaskActivityLaunch;
    private TaskService taskService;
    private List<Task> taskList;
    private boolean loadingTasks;
    private ProgressBar tasksLoadingSpinner;
    private TextView noTasksTextView;
    private ScrollView tasksScrollView;
    private TaskListAdapter taskListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.loadingTasks = true;
        View inflatedView = inflater.inflate(R.layout.fragment_tasks, container, false);
        this.ctx = getContext();
        Configuration.getInstance().load(this.ctx, PreferenceManager.getDefaultSharedPreferences(this.ctx));
        this.map = inflatedView.findViewById(R.id.TasksMapView);
        this.mapController = this.map.getController();
        this.configureMap();
        RecyclerView tasksRecyclerView = inflatedView.findViewById(R.id.TasksRecyclerView);
        this.tasksLoadingSpinner = inflatedView.findViewById(R.id.TasksLoadingSpinner);
        this.noTasksTextView = inflatedView.findViewById(R.id.NoTasksTextView);
        this.tasksScrollView = inflatedView.findViewById(R.id.TasksScrollView);
        this.taskList = new ArrayList<>();
        this.taskService = new TaskService();
        this.taskListAdapter = new TaskListAdapter(this.taskList, this);
        tasksRecyclerView.setAdapter(taskListAdapter);
        tasksRecyclerView.setHasFixedSize(true);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this.ctx));
        this.taskService.setTaskServiceListener(new TaskService.TaskServiceListener() {
            @Override
            public void onTaskLoaded(Task task) {
                if (task == null) {
                    loadingTasks = false;
                    tasksLoadingSpinner.setVisibility(View.INVISIBLE);
                    noTasksTextView.setVisibility(View.VISIBLE);
                    tasksScrollView.setVisibility(View.INVISIBLE);
                    return;
                }
                if (loadingTasks) {
                    loadingTasks = false;
                    tasksLoadingSpinner.setVisibility(View.INVISIBLE);
                    noTasksTextView.setVisibility(View.INVISIBLE);
                    tasksScrollView.setVisibility(View.VISIBLE);
                }
                //setMapMarker(task);
                taskList.add(task);
                taskListAdapter.notifyDataSetChanged();
            }
        });
        this.taskService.readTasks();
        this.addTaskActivityLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bundle intentExtras = data.getExtras();
                        if (intentExtras.getBoolean("NewTask")) {
                            taskService.readTask(intentExtras.getString("TaskUUID"));
                            loadingTasks = false;
                            tasksLoadingSpinner.setVisibility(View.INVISIBLE);
                            noTasksTextView.setVisibility(View.INVISIBLE);
                            tasksScrollView.setVisibility(View.VISIBLE);
                        }
                    }
                });
        this.viewTaskActivityLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bundle intentExtras = data.getExtras();
                        if (intentExtras.getBoolean("DeletedTask")) {
                            taskList.remove(intentExtras.getInt("DeletedTaskPosition"));
                            taskListAdapter.notifyDataSetChanged();
                            loadingTasks = false;
                            tasksLoadingSpinner.setVisibility(View.INVISIBLE);
                            noTasksTextView.setVisibility(View.VISIBLE);
                            tasksScrollView.setVisibility(View.INVISIBLE);
                        } else if (intentExtras.getBoolean("EditedTask")) {
                            taskList.get(intentExtras.getInt("EditedTaskPosition")).setIsComplete(true);
                            taskListAdapter.notifyItemChanged(intentExtras.getInt("EditedTaskPosition"));
                            loadingTasks = false;
                            tasksLoadingSpinner.setVisibility(View.INVISIBLE);
                            noTasksTextView.setVisibility(View.INVISIBLE);
                            tasksScrollView.setVisibility(View.VISIBLE);
                        }
                    }
                });
        inflatedView.findViewById(R.id.AddTaskButton).setOnClickListener(this::onAddTaskButtonClick);
        return inflatedView;
    }

    public void onAddTaskButtonClick(View view) {
        Intent intent = new Intent(getContext(), AddTask.class);
        this.addTaskActivityLaunch.launch(intent);

        // Reference to create group and add task to group

//        GroupService groupService = new GroupService();
//        ArrayList<String> groupParticipants = new ArrayList<>();
//        groupParticipants.add("0807XrhHnbYCitusNryNuiFhaRB2");
//        groupParticipants.add("MCusNMtwr7hNW7e5xg6Un4tEpfu2");
//        Group group = new Group("Group 1", groupParticipants);
//        UUID groupUuid = UUID.randomUUID();
//        group.setUuid(groupUuid.toString());
//        groupService.createGroup(group);
//        LocationItem locationItem = new LocationItem("Fenway park", 42.3467, -71.0972);
//        Task task = new Task("Fenway test task", "Fenway test task description",
//                locationItem);
//        UUID taskUuid = UUID.randomUUID();
//        task.setUuid(taskUuid.toString());
//        groupService.addTaskToGroup(groupUuid.toString(), task);

        // Reference to get friends of user
//        FriendService friendService = new FriendService();
//        friendService.readUserFriends();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void configureMap() {
        this.map.setTileSource(TileSourceFactory.MAPNIK);
        this.mapController.setZoom(15);
        this.map.setMultiTouchControls(true);
        this.map.setClickable(true);
    }

//    private Marker getCustomizedMapMarker() {
//        Marker mapMarker = new Marker(this.map);
//        Drawable pin_drawable = ResourcesCompat.getDrawable(this.ctx.getResources(), R.drawable.pin, null);
//        // assert pin_drawable != null;
//        Bitmap bitmap = ((BitmapDrawable) pin_drawable).getBitmap();
//        Drawable dr = new BitmapDrawable(this.ctx.getResources(), Bitmap.createScaledBitmap(bitmap,
//                (int) (18.0f * this.ctx.getResources().getDisplayMetrics().density),
//                (int) (18.0f * this.ctx.getResources().getDisplayMetrics().density),
//                true));
//        mapMarker.setIcon(dr);
//        return mapMarker;
//    }

//    private void setMapMarker(Task task) {
//        GeoPoint markerPoint = new GeoPoint(task.getLocation().getLat(), task.getLocation().getLon());
//        this.mapController.setCenter(markerPoint);
//        Marker mapMarker = this.getCustomizedMapMarker();
//        mapMarker.setPosition(markerPoint);
//        mapMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//        mapMarker.setOnMarkerClickListener((marker, mapView) -> {
//            Intent intent = new Intent(getContext(), TaskView.class);
//            intent.putExtra("taskTitle", task.getTaskName());
//            intent.putExtra("taskDescription", task.getDescription());
//            intent.putExtra("taskLocation", task.getLocation().getKey());
//            intent.putExtra("taskLatitude", task.getLocation().getLat());
//            intent.putExtra("taskLongitude", task.getLocation().getLon());
//            intent.putExtra("taskComplete", task.getIsComplete());
//            intent.putExtra("taskUUID", task.getUuid());
//            intent.putExtra("taskType", task.getTaskType());
//            intent.putExtra("taskTypeString", task.getTaskTypeString());
//            startActivity(intent);
//            return true;
//        });
//        this.map.getOverlays().add(mapMarker);
//    }

    @Override
    public void onTaskItemClick(int position) {
        Task task = this.taskList.get(position);
        Intent intent = new Intent(getContext(), TaskView.class);
        intent.putExtra("position", position);
        intent.putExtra("taskTitle", task.getTaskName());
        intent.putExtra("taskDescription", task.getDescription());
        intent.putExtra("taskLocation", task.getLocation().getKey());
        intent.putExtra("taskLatitude", task.getLocation().getLat());
        intent.putExtra("taskLongitude", task.getLocation().getLon());
        intent.putExtra("taskComplete", task.getIsComplete());
        intent.putExtra("taskUUID", task.getUuid());
        intent.putExtra("taskType", task.getTaskType());
        intent.putExtra("taskTypeString", task.getTaskTypeString());
        this.viewTaskActivityLaunch.launch(intent);
    }
}