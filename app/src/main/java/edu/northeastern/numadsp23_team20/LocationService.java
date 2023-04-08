package edu.northeastern.numadsp23_team20;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class LocationService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private PowerManager.WakeLock wakeLock;

    private LocationManager locationManager;

    DatabaseReference ref;
    GeoFire geoFire;
    GeoQuery geoQuery;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("LocationService - " + "Location service started");

        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseUser = mAuth.getCurrentUser();
        String userId = this.firebaseUser.getUid();
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/" + userId + "/locations");
        this.geoFire = new GeoFire(this.ref);
        ref.keepSynced(true);
        this.geoQuery = geoFire.queryAtLocation(new GeoLocation(0.0, 0.0), 0.3);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
//                System.out.println("Key: " + dataSnapshot.getKey());
                sendNotification(dataSnapshot.getKey(), location.latitude + "," + location.longitude);
            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //System.out.println("All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });

        startLocationUpdates();
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("LocationService-" + "Location service started");
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationServiceWakeLock");
        wakeLock.acquire();

        createNotificationChannelForService();
        createNotificationChannelForTasks();

        Notification notification = new NotificationCompat.Builder(this, "LocationServiceChannel")
                .setContentTitle("Location Service")
                .setContentText("Location updates are running...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        startLocationUpdates();
        return START_STICKY;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
//            System.out.println("LocationService" + "Latitude: " + latitude + " Longitude: " + longitude);

            geoQuery.setCenter(new GeoLocation(location.getLatitude(), location.getLongitude()));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
//            System.out.println("LocationService" + "Provider status changed: " + provider + " status: " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
//            System.out.println("LocationService" + "Provider enabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
//            System.out.println("LocationService" + "Provider disabled: " + provider);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        locationManager.removeUpdates(locationListener);
        stopForeground(true);
    }

    private void createNotificationChannelForService() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("LocationServiceChannel",
                    "Location Service Channel",
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotificationChannelForTasks() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("TaskNotificationChannel",
                    "Task Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String title, String text) {
//        System.out.println(title + "," + text);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "TaskNotificationChannel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        notificationManager.notify(random.nextInt(), builder.build());
    }
}

