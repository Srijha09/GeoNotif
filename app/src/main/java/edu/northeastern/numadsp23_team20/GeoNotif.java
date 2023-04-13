package edu.northeastern.numadsp23_team20;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class GeoNotif extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
