package edu.northeastern.numadsp23_team20;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

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

import java.util.Arrays;
import java.util.List;

public class EditTask extends AppCompatActivity {

    ActivityResultLauncher<Intent> addressSearchActivity;
    MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        this.addressSearchActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(result.getData());
                    System.out.println(place.getName() + ", " + place.getLatLng());
                }
            }
        );

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        this.map = findViewById(R.id.EditTaskMapView);
        this.customizeMap();
    }

    public void onEditTaskUpdateButtonClick(View view) {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                fields).build(this);
        this.addressSearchActivity.launch(intent);
    }

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
        // this.map.setOnTouchListener((v, event) -> true);
        this.map.getOverlays().add(marker);
    }
}