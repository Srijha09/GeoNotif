package edu.northeastern.numadsp23_team20;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class EditTask extends AppCompatActivity {

    ActivityResultLauncher<Intent> addressSearchActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

//        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
//        this.addressSearchActivity = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
//                    Place place = Autocomplete.getPlaceFromIntent(result.getData());
//                    System.out.println(place.getName() + ", " + place.getLatLng());
//                }
//            }
//        );
    }

    public void onAddressSearch(View view) {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                fields).build(this);
        this.addressSearchActivity.launch(intent);
    }
}