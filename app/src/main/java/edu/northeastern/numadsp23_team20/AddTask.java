package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class AddTask extends AppCompatActivity {

    private MapView map;
    private IMapController mapController;
    private FusedLocationProviderClient fusedLocationClient;
    private GeoPoint currentUserLocation;
    private double taskLatitude;
    private double taskLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        this.map = findViewById(R.id.AddTaskMapView);
        this.mapController = this.map.getController();
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        this.getCurrentUserLocation();
    }

    public void onAddTaskFindLocationButton(View view) {
        findViewById(R.id.AddTaskLocationValue).setVisibility(View.VISIBLE);
    }

    public void onAddTaskSubmitButton(View view) {
        String taskTitle = ((TextView) findViewById(R.id.AddTaskTitleValue)).getText().toString();
        String taskDescription = ((TextView) findViewById(R.id.AddTaskDescriptionValue)).getText().toString();
        LocationItem location = new LocationItem("Boylston", this.taskLatitude, this.taskLongitude);
        Task task = new Task(taskTitle, taskDescription, location);
        TaskService taskService = new TaskService();
        taskService.createTask(task);
    }

    private void getCurrentUserLocation() {
        boolean noFineLocationAccess = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean noCoarseLocationAccess = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        if (noFineLocationAccess && noCoarseLocationAccess) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    101);
        }
        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, location -> {
                if (location != null) {
                    this.currentUserLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                    this.taskLatitude = location.getLatitude();
                    this.taskLongitude = location.getLongitude();
                    this.configureMap();
                }
            });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void configureMap() {
        this.map.setTileSource(TileSourceFactory.MAPNIK);
        this.mapController.setZoom(16);
        this.map.setMultiTouchControls(true);
        this.map.setClickable(true);
        System.out.println(this.currentUserLocation);
        this.mapController.setCenter(this.currentUserLocation);
    }
}