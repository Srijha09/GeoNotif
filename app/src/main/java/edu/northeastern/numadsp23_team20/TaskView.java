package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

public class TaskView extends AppCompatActivity {

    MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        this.map = findViewById(R.id.map);
        this.customizeMap();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void customizeMap() {
        // Set map tiles
        this.map.setTileSource(TileSourceFactory.MAPNIK);
        IMapController mapController = this.map.getController();
        mapController.setZoom(18.8);
        GeoPoint centerPoint = new GeoPoint(42.3447, -71.0996);
        mapController.setCenter(centerPoint);
        // Set marker on map
        Marker startMarker = new Marker(this.map);
        startMarker.setPosition(centerPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        this.map.getOverlays().add(startMarker);
        // Disable scroll and zoom
        this.map.setOnTouchListener((v, event) -> true);
        double radiusInMeters = 50;
        // Define the circle's center point
        GeoPoint circleCenter = startMarker.getPosition();
        // Define the circle's radius in terms of latitude degrees and longitude degrees
        double radiusInDegreesLatitude = radiusInMeters / 111319.9;
        double radiusInDegreesLongitude = radiusInMeters / (111319.9 * Math.cos(
                Math.toRadians(circleCenter.getLatitude())));
        // Define the circle's bounding box
        BoundingBox circleBoundingBox = new BoundingBox(
                circleCenter.getLatitude() + radiusInDegreesLatitude,
                circleCenter.getLongitude() + radiusInDegreesLongitude,
                circleCenter.getLatitude() - radiusInDegreesLatitude,
                circleCenter.getLongitude() - radiusInDegreesLongitude
        );
        // Create a new Polygon object representing the circle
        Polygon circlePolygon = new Polygon();
        circlePolygon.setPoints(Polygon.pointsAsCircle(circleCenter, radiusInMeters));
        circlePolygon.getFillPaint().setColor(Color.parseColor("#500000FF"));
        circlePolygon.getOutlinePaint().setColor(Color.parseColor("#FF0000"));
        circlePolygon.getOutlinePaint().setStrokeWidth(0f);
        // Add the circle to the map
        this.map.getOverlayManager().add(circlePolygon);
        // Refresh the map view to update the overlays
        this.map.invalidate();
    }
}