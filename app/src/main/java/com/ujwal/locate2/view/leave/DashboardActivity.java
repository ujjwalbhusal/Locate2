package com.ujwal.locate2.view.leave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ujwal.locate2.R;
import com.ujwal.locate2.view.LauncherActivity;

public class DashboardActivity extends AppCompatActivity {

    CardView addAttendanceCard;
    CardView viewAttendanceCard;
    CardView leaveRequestCard;
    CardView logoutCard;

    TextView welcomeText;
    FirebaseAuth firebaseAuth;
//    FirebaseDatabase rootNode;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference locationReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        addAttendanceCard = findViewById(R.id.card1);
        viewAttendanceCard=findViewById(R.id.card_viewattendance);
        leaveRequestCard =findViewById(R.id.card_leave);
        logoutCard=findViewById(R.id.card_logout);

        welcomeText =  findViewById(R.id.txt_welcome);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
//          locationReference = rootNode.getReference("location");
//          DatabaseReference userReference = rootNode.getReference("Users/" + firebaseAuth.getUid());

        DatabaseReference databaseReference = firebaseDatabase.getReference("Users/" + firebaseAuth.getUid() + "/fullname");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String fullName = dataSnapshot.getValue(String.class);
                welcomeText.setText("Welcome! \n" + fullName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                welcomeText.setText("Welcome!");
            }
        });

//        thisActivity = getActivity();
        addAttendanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AttendanceActivity.class));
//                finish();

            }
        });

        viewAttendanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewAttendanceActivity.class));

            }
        });

        logoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LauncherActivity.class));
                finish();
            }
        });

        leaveRequestCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), RequestALeaveActivity.class));
//                finish();
            }
        });



    }
}