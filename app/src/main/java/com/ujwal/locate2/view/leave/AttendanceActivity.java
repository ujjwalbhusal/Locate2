package com.ujwal.locate2.view.leave;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ujwal.locate2.R;
import com.ujwal.locate2.models.LocationHelper;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AttendanceActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseAuth firebaseAuth;
//    FirebaseDatabase rootNode;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference locationReference;
    private TextView txt;
    Geocoder geocoder;
    List<Address> addressList;
    private final int REQUEST_CHECK_CODE = 8989;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        firebaseAuth = FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Button btn = findViewById(R.id.locbtn);
        txt = findViewById(R.id.loctxt);
        Button homeBtn = findViewById(R.id.signout);
//        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //get the location here

                        LocationRequest locationRequest = new LocationRequest()
                                .setFastestInterval(1500)
                                .setInterval(3000)
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                                .addLocationRequest(locationRequest);

                        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext()).checkLocationSettings(builder.build());

                        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                                try {
                                    task.getResult(ApiException.class);
                                } catch (ApiException e) {
                                    switch (e.getStatusCode()) {
                                        case LocationSettingsStatusCodes
                                                .RESOLUTION_REQUIRED:
                                            try {
                                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                                resolvableApiException.startResolutionForResult(AttendanceActivity.this, REQUEST_CHECK_CODE);

                                            } catch (IntentSender.SendIntentException | ClassCastException ex) {
                                                ex.printStackTrace();
                                            }
                                            break;

                                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                            Toast.makeText(AttendanceActivity.this, "Try Again", Toast.LENGTH_SHORT).show();

                                            break;
                                    }
                                    e.printStackTrace();
                                }
                            }
                        });

                        fusedLocationProviderClient.getLastLocation()
                                .addOnSuccessListener(new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {

                                        //english date time sample
                                        final String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                        final String currentDate = new SimpleDateFormat("YYYY-MM-dd", Locale.getDefault()).format(new Date());


                                        if (location != null) {

                                            //Get a reference to our posts
                                            firebaseDatabase = FirebaseDatabase.getInstance();
                                            locationReference = firebaseDatabase.getReference("attendance");
//                                            DatabaseReference userReference = rootNode.getReference("Users/" + firebaseAuth.getUid());
                                            DatabaseReference fullNameReference = firebaseDatabase.getReference("Users/" + firebaseAuth.getUid() + "/fullname");

                                            final String deviceDetails = Build.MANUFACTURER + " " + Build.MODEL;
                                            final Double lat = location.getLatitude();
                                            final Double lng = location.getLongitude();

                                            //Location name of lat lng

                                            geocoder = new Geocoder(AttendanceActivity.this, Locale.getDefault());
                                            try {
                                                addressList = geocoder.getFromLocation(lat, lng, 1);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            final String myAddress = addressList.get(0).getAddressLine(0);

                                            fullNameReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    final String fullName = dataSnapshot.getValue(String.class);
                                                    txt.setText("Hello! " + fullName + "\nYour location is " + myAddress);
                                                    Toast.makeText(AttendanceActivity.this, "Success, now redirecting to home", Toast.LENGTH_SHORT).show();
                                                    //Storing values from text field
                                                    LocationHelper locationHelper = new LocationHelper(myAddress, lat.toString(), lng.toString(), currentTime, currentDate, deviceDetails);
                                                    locationReference.child(firebaseAuth.getUid()).child(currentDate + " " + currentTime).setValue(locationHelper);
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    Toast.makeText(getApplicationContext(), "Error fetching data", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                });
                    } else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                    }
                }
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                finish();
            }
        });
    }
}