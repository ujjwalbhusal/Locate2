package com.ujwal.locate2.services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ujwal.locate2.view.employees.FacultyMainActivity;
import com.ujwal.locate2.viewmodel.FacultyViewModel;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class GetLocation extends Service {
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 5 * 1000;  /* 5 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    FirebaseDatabase firebaseDatabase;
    int notificationId=2;
    FacultyViewModel facultyViewModel;
    public static int runservice=0;
    private FirebaseAuth.AuthStateListener authStateListener;
    public static NotificationManagerCompat notificationManager;
    public static NotificationCompat.Builder builder;




    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                Log.d("Auth State", "Auth State Changed");

            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
        firebaseDatabase= FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference();
        runservice=1;
            getLastLocation();
            startLocationUpdates();
            showNotification();
    }


    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
                getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                // do work here
                                // GPS location can be null if GPS is switched off
                                if (locationResult.getLastLocation() != null) {
                                    if(locationResult.getLastLocation().hasAccuracy())
                                    {
                                        if(locationResult.getLastLocation().getAccuracy()<10)
                                        {
                                            onLocationChanged(locationResult.getLastLocation());

                                        }
                                    }

                                }
                            }
                        },
                        Looper.myLooper());
            }

    public void onLocationChanged(Location location) {
        if(FacultyMainActivity.l==1) {
            // You can now create a LatLng Object for use with maps
            reference.child("geocordinates").child(firebaseUser.getUid()).setValue(location);
            //reference.child(("geocordinates")).child(firebaseUser.getUid()).child("latitude").setValue(location.getLatitude());
            //reference.child("geocordinates").child(firebaseUser.getUid()).child("longitude").setValue(location.getLongitude());
            //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    public void showNotification() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        {
            CharSequence name ="Location Updates";
            String description = "Your realtime location is currently shared.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, FacultyMainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
             builder = new NotificationCompat.Builder(getApplicationContext(), "CHANNEL")
                    .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                    .setContentTitle("Location Updates")
                    .setContentText("Your realtime location is currently shared.")
                    .setColorized(true)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                     .setVibrate(new long[]{500, 500})
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
         notificationManager = NotificationManagerCompat.from(getApplicationContext());
            notificationManager.notify(notificationId, builder.build());
    }
    @Override
    public void onDestroy() {
        FacultyMainActivity.l=0;
        runservice=0;
        notificationManager.cancel(notificationId);
        if(authStateListener!=null)
        {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        reference.child("geocordinates").child(firebaseUser.getUid()).removeValue();
        super.onDestroy();
    }

}

