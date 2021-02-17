package com.ujwal.locate2.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ujwal.locate2.di.App;
import com.ujwal.locate2.models.Faculty;
import com.ujwal.locate2.utils.FirebaseUtils;
import com.ujwal.locate2.view.MapsActivity;
import com.ujwal.locate2.view.admins.StudentMainActivity;
import com.ujwal.locate2.viewmodel.FacultyViewModel;

import java.util.List;

public class LocationNotification extends LifecycleService {
    private FirebaseAuth firebaseAuth;
    Faculty faculty;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    String uuid;
    private FacultyViewModel facultyViewModel;
    NotificationManagerCompat notificationManager;
    int notificationId=2;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                Log.d("Auth State", "Auth State Changed");

            }
        };
        facultyViewModel=new FacultyViewModel(App.getApp());
        firebaseAuth.addAuthStateListener(authStateListener);
        firebaseDatabase = FirebaseUtils.getDatabase();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child("geocordinates").keepSynced(true);
            databaseReference.child("geocordinates").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() != 0) {
                        uuid = dataSnapshot.getKey();
                        facultyViewModel.getAllFaculties().observe(LocationNotification.this, new Observer<List<DataSnapshot>>() {
                            @Override
                            public void onChanged(List<DataSnapshot> dataSnapshots) {
                                for(DataSnapshot dataSnapshot1:dataSnapshots){
                                    if (uuid.equals(dataSnapshot1.getKey())) {
                                        if (isNetworkConnected()) {
                                            faculty = dataSnapshot1.getValue(Faculty.class);
                                            String name = (String) dataSnapshot1.child("name").getValue();
                                            showNotification(name, faculty);
                                        }
                                    }
                                }
                            }
                        });
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    if(notificationManager!=null) {
                            notificationManager.cancel(notificationId);
                    }

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    public int showNotification(String name, Faculty faculty)
    {
        int requestID = (int) System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence names ="Location Updates";
            String description = name+" is currently sharing realtime location.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL", names, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("faculty",faculty);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivities(getApplicationContext(), requestID,new Intent[]{new Intent(getApplicationContext(), StudentMainActivity.class),intent}, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "CHANNEL")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentTitle("Location Updates")
                .setVibrate(new long[]{500, 500})
                .setColorized(true)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentText(name+" is currently sharing realtime location.")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(notificationId, builder.build());
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
        return notificationId;

    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
