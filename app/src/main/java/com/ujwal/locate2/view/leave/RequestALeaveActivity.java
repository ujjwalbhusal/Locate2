package com.ujwal.locate2.view.leave;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ujwal.locate2.R;
import com.ujwal.locate2.models.LeaveHelper;

import java.util.Date;
import java.util.Locale;

public class RequestALeaveActivity extends AppCompatActivity {


    TextView leaveDescTxt;
    Button requestLeaveBtn;
    Button homeBtn;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_a_leave);

        leaveDescTxt = findViewById(R.id.leavedesc);
        requestLeaveBtn=findViewById(R.id.btnrequestleave);
        homeBtn=findViewById(R.id.btnhome);

        firebaseAuth= FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("attendance");

        requestLeaveBtn.setOnClickListener(v -> databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String date = new SimpleDateFormat("YYYY-MM-dd", Locale.getDefault()).format(new Date());
                String desc = leaveDescTxt.getText().toString();

                LeaveHelper leaveHelper=new LeaveHelper(desc,date,time,true);
                databaseReference.child(firebaseAuth.getUid()).child(date+" "+time).setValue(leaveHelper);
                Toast.makeText(RequestALeaveActivity.this, "Success, now redirecting to home", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error fetching data", Toast.LENGTH_LONG).show();
            }
        }));

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                finish();
            }
        });


//        LocationHelper locationHelper = new LocationHelper(myAddress, lat.toString(), lng.toString(), currentTime, currentDate, deviceDetails);
//        locationReference.child(firebaseAuth.getUid()).child(currentDate + " " + currentTime).setValue(locationHelper);

    }
}