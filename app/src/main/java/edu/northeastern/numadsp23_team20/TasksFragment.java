package edu.northeastern.numadsp23_team20;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

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
import java.util.Arrays;
import java.util.List;

public class TasksFragment extends Fragment {

    private MapView map;
    private IMapController mapController;
    private List<List<Double>> taskLocations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_tasks, container, false);

        Context ctx = getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        this.map = inflatedView.findViewById(R.id.TasksMapView);
        this.mapController = this.map.getController();
        this.taskLocations = new ArrayList<>();
        this.taskLocations.add(Arrays.asList(42.3447, -71.0996));
        this.taskLocations.add(Arrays.asList(42.3398, -71.0892));
        this.taskLocations.add(Arrays.asList(42.3471, -71.0817));
        this.taskLocations.add(Arrays.asList(42.3473, -71.0971));
        this.configureMap();
        for (List<Double> latLong: this.taskLocations) {
            this.setMapMarker(latLong.get(0), latLong.get(1));
        }

        inflatedView.findViewById(R.id.AddTaskButton).setOnClickListener(view -> {
            this.onAddTaskButtonClick(view);
        });

        return inflatedView;
    }

    public void onAddTaskButtonClick(View view) {
        Intent intent = new Intent(getContext(), AddTask.class);
        this.startActivity(intent);
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
        Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.pin, null);
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
        Drawable dr = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap,
                (int) (18.0f * getResources().getDisplayMetrics().density),
                (int) (18.0f * getResources().getDisplayMetrics().density),
                true));
        mapMarker.setIcon(dr);
        return mapMarker;
    }

    private void setMapMarker(double latitude, double longitude) {
        GeoPoint markerPoint = new GeoPoint(latitude, longitude);
        this.mapController.setCenter(markerPoint);
        Marker mapMarker = this.getCustomizedMapMarker();
        mapMarker.setPosition(markerPoint);
        mapMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapMarker.setOnMarkerClickListener((marker, mapView) -> {
            Intent intent = new Intent(getContext(), TaskView.class);
            intent.putExtra("taskLatitude", latitude);
            intent.putExtra("taskLongitude", longitude);
            startActivity(intent);
            return true;
        });
        this.map.getOverlays().add(mapMarker);
    }
}