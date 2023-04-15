package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

public class TaskView extends AppCompatActivity {

    MapView map;
    String taskName;
    Button markComplete;
    String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        this.map = findViewById(R.id.MapView);
        this.customizeMap();
        this.markComplete = findViewById(R.id.TaskDetailsCompleteButton);

        Intent intent = getIntent();
        this.taskName = intent.getExtras().getString("taskTitle");
        this.uuid = intent.getExtras().getString("taskUUID");
        ((TextView) findViewById(R.id.TaskTitleTextView)).setText(intent.getExtras().getString("taskTitle"));
        ((TextView) findViewById(R.id.TaskDetailsDescription)).setText(intent.getExtras().getString("taskDescription"));
        ((TextView) findViewById(R.id.TaskDetailsLocation)).setText("\uD83D\uDCCD " + intent.getExtras().getString("taskLocation"));

        if (intent.getExtras().getBoolean("taskComplete")) {
            ViewGroup layout = (ViewGroup) this.markComplete.getParent();
            layout.removeView(this.markComplete);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void customizeMap() {
        // Set map tiles
        this.map.setTileSource(TileSourceFactory.MAPNIK);
        IMapController mapController = this.map.getController();
        mapController.setZoom(18.8);
        // Set center
        Intent intent = getIntent();
        GeoPoint centerPoint = new GeoPoint(intent.getExtras().getDouble("taskLatitude"),
                intent.getExtras().getDouble("taskLongitude"));
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

    public void onTaskEditFloatingButtonClick(View view) {
        Intent thisIntent = getIntent();
        Intent intent = new Intent(this, EditTask.class);
        intent.putExtra("taskTitle", thisIntent.getExtras().getString("taskTitle"));
        intent.putExtra("taskDescription", thisIntent.getExtras().getString("taskDescription"));
        intent.putExtra("taskLocation", thisIntent.getExtras().getString("taskLocation"));
        intent.putExtra("taskLatitude", thisIntent.getExtras().getDouble("taskLatitude"));
        intent.putExtra("taskLongitude", thisIntent.getExtras().getDouble("taskLongitude"));
        intent.putExtra("taskComplete", thisIntent.getExtras().getBoolean("taskComplete"));
        intent.putExtra("taskUUID", thisIntent.getExtras().getString("taskUUID"));
        this.startActivity(intent);
    }

    public void onTaskDeleteFloatingButtonClick(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Are you sure you want to delete this task?")
                .setIcon(R.drawable.warning)
                .setPositiveButton("CONFIRM", (dialogInterface, whichButton) -> {
                    TaskService taskService = new TaskService();
                    taskService.deleteTask(this.uuid);
                    this.finish();
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}