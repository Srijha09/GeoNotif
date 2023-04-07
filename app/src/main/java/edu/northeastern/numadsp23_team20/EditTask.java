package edu.northeastern.numadsp23_team20;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

public class EditTask extends AppCompatActivity {

    private ActivityResultLauncher<Intent> addressSearchActivity;
    private TextView editTaskLocationValue;
    private MapView map;
    private IMapController mapController;
    private Marker mapMarker;
    private Task task;

    private TextView editTaskTitleValue;
    private TextView editTaskDescriptionValue;
    private String taskLocation;
    private double taskLatitude;
    private double taskLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        this.editTaskLocationValue = findViewById(R.id.EditTaskLocationValue);
        // Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        this.addressSearchActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(result.getData());
                    this.taskLocation = place.getName();
                    this.taskLatitude = place.getLatLng().latitude;
                    this.taskLongitude = place.getLatLng().longitude;
                    setMapMarker(this.taskLatitude, this.taskLongitude);
                    editTaskLocationValue.setText("\uD83D\uDCCD " + place.getName());

                }
            }
        );

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        this.map = findViewById(R.id.EditTaskMapView);
        this.mapController = this.map.getController();
        this.mapMarker = new Marker(this.map);
        Intent intent = this.getIntent();
        String taskTitle = intent.getExtras().getString("taskTitle");
        String taskDescription = intent.getExtras().getString("taskDescription");
        this.taskLocation = intent.getExtras().getString("taskLocation");
        this.taskLatitude = intent.getExtras().getDouble("taskLatitude");
        this.taskLongitude = intent.getExtras().getDouble("taskLongitude");
        LocationItem locationItem = new LocationItem(this.taskLocation, this.taskLatitude, this.taskLongitude);
        this.task = new Task(taskTitle, taskDescription, locationItem);
        this.configureMap();
        this.customizeMapMarker();
        this.setMapMarker(this.taskLatitude, this.taskLongitude);
        this.editTaskTitleValue = findViewById(R.id.EditTaskTitleValue);
        this.editTaskTitleValue.setText(taskTitle);
        this.editTaskDescriptionValue = findViewById(R.id.EditTaskDescriptionValue);
        this.editTaskDescriptionValue.setText(taskDescription);
        ((TextView) findViewById(R.id.EditTaskLocationValue)).setText("\uD83D\uDCCD " + taskLocation);
    }

    public void onEditTaskUpdateButtonClick(View view) {
        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG,
                Place.Field.ADDRESS_COMPONENTS);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                fields).build(this);
        this.addressSearchActivity.launch(intent);
    }

    public void onEditTaskCancelButtonClick(View view) {
        this.finish();
    }

    public void onEditTaskSubmitButtonClick(View view) {
        new AlertDialog.Builder(this)
            .setMessage("Are you sure you want to edit this task?")
            .setPositiveButton("CONFIRM", (dialogInterface, whichButton) -> {
                LocationItem locationItem = new LocationItem(
                        this.taskLocation, this.taskLatitude, this.taskLongitude
                );
                Task updatedTask = new Task(this.editTaskTitleValue.getText().toString(),
                        this.editTaskDescriptionValue.getText().toString(), locationItem);
                TaskService taskService = new TaskService();
                taskService.editTask(this.task, updatedTask);
                this.finish();
            })
            .setNegativeButton(android.R.string.no, null)
            .show();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void configureMap() {
        this.map.setTileSource(TileSourceFactory.MAPNIK);
        this.mapController.setZoom(18.8);
        this.map.setOnTouchListener((v, event) -> true);
    }

    private void customizeMapMarker() {
        Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.pin, null);
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
        Drawable dr = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap,
                (int) (18.0f * getResources().getDisplayMetrics().density),
                (int) (18.0f * getResources().getDisplayMetrics().density),
                true));
        this.mapMarker.setIcon(dr);
    }

    private void setMapMarker(double latitude, double longitude) {
        this.map.invalidate();
        this.mapMarker.remove(this.map);
        GeoPoint markerPoint = new GeoPoint(latitude, longitude);
        this.mapController.setCenter(markerPoint);
        this.mapMarker.setPosition(markerPoint);
        this.mapMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        this.map.getOverlays().add(this.mapMarker);
    }
}