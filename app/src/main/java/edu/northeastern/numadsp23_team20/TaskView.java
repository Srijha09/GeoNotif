package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
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
        this.map = findViewById(R.id.MapView);
        this.customizeMap();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void customizeMap() {
        // Set map tiles
        this.map.setTileSource(TileSourceFactory.MAPNIK);
        IMapController mapController = this.map.getController();
        mapController.setZoom(18.8);
        // Set center
        GeoPoint centerPoint = new GeoPoint(42.3447, -71.0996);
        mapController.setCenter(centerPoint);
        // Set marker
        Marker marker = new Marker(this.map);
        marker.setPosition(centerPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.pin, null);
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
        Drawable dr = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap,
                (int) (18.0f * getResources().getDisplayMetrics().density),
                (int) (18.0f * getResources().getDisplayMetrics().density),
                true));
        marker.setIcon(dr);
        // Disable scroll and zoom
        this.map.setOnTouchListener((v, event) -> true);
        // Define radius circle
        double radiusInMeters = 100;
        GeoPoint circleCenter = marker.getPosition();
        Polygon circlePolygon = new Polygon();
        circlePolygon.setPoints(Polygon.pointsAsCircle(circleCenter, radiusInMeters));
        circlePolygon.getFillPaint().setColor(ContextCompat.getColor(
                this, R.color.map_radius_fill));
        circlePolygon.getOutlinePaint().setColor(ContextCompat.getColor(
                this, R.color.map_radius_outline));
        circlePolygon.getOutlinePaint().setStrokeWidth(0f);
        // Add layers to map
        this.map.getOverlays().add(circlePolygon);
        this.map.getOverlays().add(marker);
        // Refresh the map view to update the overlays
        this.map.invalidate();
    }
}