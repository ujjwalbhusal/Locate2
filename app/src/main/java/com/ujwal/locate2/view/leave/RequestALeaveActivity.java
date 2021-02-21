package com.ujwal.locate2.view.leave;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.ujwal.locate2.view.payroll.AddEmployeeActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RequestALeaveActivity extends AppCompatActivity {


    TextView leaveDescTxt;
    Button requestLeaveBtn;
    Button homeBtn;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    EditText leavefrom, leaveto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_a_leave);

        leaveDescTxt = findViewById(R.id.leavedesc);
        requestLeaveBtn=findViewById(R.id.btnrequestleave);
        homeBtn=findViewById(R.id.btnhome);
        leavefrom=findViewById(R.id.leavefrom);
        leaveto = findViewById(R.id.leaveto);

        firebaseAuth= FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("attendance");

        //===================================CALENDER===================================

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        leavefrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        RequestALeaveActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        String date = dayOfMonth+"/"+month+"/"+year;
                        leavefrom.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();

            }
        });


        leaveto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        RequestALeaveActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        String date = dayOfMonth+"/"+month+"/"+year;
                        leaveto.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();

            }
        });

        requestLeaveBtn.setOnClickListener(v -> databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String date = new SimpleDateFormat("YYYY-MM-dd", Locale.getDefault()).format(new Date());
                String desc = leaveDescTxt.getText().toString();
                String from = leavefrom.getText().toString();
                String to = leaveto.getText().toString();


                LeaveHelper leaveHelper=new LeaveHelper(desc, date, time, from, to , true);
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