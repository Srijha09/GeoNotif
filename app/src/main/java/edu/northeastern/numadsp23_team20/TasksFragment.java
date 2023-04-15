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

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TasksFragment extends Fragment implements OnTaskItemClickListener {

    Context ctx;
    private MapView map;
    private IMapController mapController;
    private ActivityResultLauncher<Intent> addTaskActivityLaunch;
    private TaskService taskService;
    private List<Task> taskList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_tasks, container, false);
        this.ctx = getContext();
        Configuration.getInstance().load(this.ctx, PreferenceManager.getDefaultSharedPreferences(this.ctx));
        this.map = inflatedView.findViewById(R.id.TasksMapView);
        this.mapController = this.map.getController();
        this.configureMap();
        RecyclerView tasksRecyclerView = inflatedView.findViewById(R.id.TasksRecyclerView);
        this.taskList = new ArrayList<>();
        this.taskService = new TaskService();
        TaskListAdapter taskListAdapter = new TaskListAdapter(this.taskList, this);
        tasksRecyclerView.setAdapter(taskListAdapter);
        tasksRecyclerView.setHasFixedSize(true);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this.ctx));
        this.taskService.setTaskServiceListener(new TaskService.TaskServiceListener() {
            @Override
            public void onTaskLoaded(Task task) {
                setMapMarker(task);
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
                            this.taskService.readTasks();
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
//        List<String> groupParticipants = new ArrayList<>();
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
    }

    @SuppressLint("ClickableViewAccessibility")
    private void configureMap() {
        this.map.setTileSource(TileSourceFactory.MAPNIK);
        this.mapController.setZoom(3);
        this.map.setMultiTouchControls(true);
        this.map.setClickable(true);
    }

    private Marker getCustomizedMapMarker() {
        Marker mapMarker = new Marker(this.map);
        Drawable pin_drawable = ResourcesCompat.getDrawable(this.ctx.getResources(), R.drawable.pin, null);
        ;
        Bitmap bitmap = ((BitmapDrawable) pin_drawable).getBitmap();
        Drawable dr = new BitmapDrawable(this.ctx.getResources(), Bitmap.createScaledBitmap(bitmap,
                (int) (18.0f * this.ctx.getResources().getDisplayMetrics().density),
                (int) (18.0f * this.ctx.getResources().getDisplayMetrics().density),
                true));
        mapMarker.setIcon(dr);
        return mapMarker;
    }

    private void setMapMarker(Task task) {
        GeoPoint markerPoint = new GeoPoint(task.getLocation().getLat(), task.getLocation().getLon());
        this.mapController.setCenter(markerPoint);
        Marker mapMarker = this.getCustomizedMapMarker();
        mapMarker.setPosition(markerPoint);
        mapMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapMarker.setOnMarkerClickListener((marker, mapView) -> {
            Intent intent = new Intent(getContext(), TaskView.class);
            intent.putExtra("taskTitle", task.getTaskName());
            intent.putExtra("taskDescription", task.getDescription());
            intent.putExtra("taskLocation", task.getLocation().getKey());
            intent.putExtra("taskLatitude", task.getLocation().getLat());
            intent.putExtra("taskLongitude", task.getLocation().getLon());
            intent.putExtra("taskComplete", task.getIsComplete());
            intent.putExtra("taskUUID", task.getUuid());
            startActivity(intent);
            return true;
        });
        this.map.getOverlays().add(mapMarker);
    }

    @Override
    public void onTaskItemClick(int position) {
        Task task = this.taskList.get(position);
        Intent intent = new Intent(getContext(), TaskView.class);
        intent.putExtra("taskTitle", task.getTaskName());
        intent.putExtra("taskDescription", task.getDescription());
        intent.putExtra("taskLocation", task.getLocation().getKey());
        intent.putExtra("taskLatitude", task.getLocation().getLat());
        intent.putExtra("taskLongitude", task.getLocation().getLon());
        intent.putExtra("taskComplete", task.getIsComplete());
        intent.putExtra("taskUUID", task.getUuid());
        startActivity(intent);
    }
}