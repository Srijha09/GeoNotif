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
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

public class GroupTasksFragment extends Fragment implements OnTaskItemClickListener {

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

        View inflatedView = inflater.inflate(R.layout.fragment_group_tasks, container, false);
        assert getArguments() != null;
        String groupName = getArguments().getString("groupName");
        // Set the group name as the text of the TextView
        TextView groupNameTextView = inflatedView.findViewById(R.id.groupName);
        groupNameTextView.setText(groupName);


        this.ctx = getContext();
        Configuration.getInstance().load(this.ctx, PreferenceManager.getDefaultSharedPreferences(this.ctx));
        this.map = inflatedView.findViewById(R.id.TasksMapView);
        this.mapController = this.map.getController();
        this.configureMap();
        RecyclerView tasksRecyclerView = inflatedView.findViewById(R.id.TasksRecyclerView);
        this.taskService = new TaskService();
//        this.taskService.setTaskServiceListener(tasks -> {
//            this.taskList = tasks;
//            for (Task task: tasks) {
//                this.setMapMarker(task);
//            }
//            TaskListAdapter taskListAdapter = new TaskListAdapter(tasks, this);
//            tasksRecyclerView.setAdapter(taskListAdapter);
//            tasksRecyclerView.setHasFixedSize(true);
//            tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this.ctx));
//        });
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
    }

    @SuppressLint("ClickableViewAccessibility")
    private void configureMap() {
        this.map.setTileSource(TileSourceFactory.MAPNIK);
        this.mapController.setZoom(16);
        this.map.setMultiTouchControls(true);
        this.map.setClickable(true);
    }

    private Marker getCustomizedMapMarker() {
        Marker mapMarker = new Marker(this.map);
        Drawable pin_drawable = ResourcesCompat.getDrawable(this.ctx.getResources(), R.drawable.pin, null);;
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
            startActivity(intent);
            return true;
        });
        this.map.getOverlays().add(mapMarker);
    }

    @Override
    public void onTaskItemClick(int position) {
        System.out.println("Clicked " + position);
        Task task = this.taskList.get(position);
        Intent intent = new Intent(getContext(), TaskView.class);
        intent.putExtra("taskTitle", task.getTaskName());
        intent.putExtra("taskDescription", task.getDescription());
        intent.putExtra("taskLocation", task.getLocation().getKey());
        intent.putExtra("taskLatitude", task.getLocation().getLat());
        intent.putExtra("taskLongitude", task.getLocation().getLon());
        startActivity(intent);
    }
}