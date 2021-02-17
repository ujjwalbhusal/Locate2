package com.ujwal.locate2.view.leave;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ujwal.locate2.R;
import com.ujwal.locate2.adapters.ViewAttendanceAdapter;
import com.ujwal.locate2.models.LocationHelper;

public class ViewAttendanceByEmployeeActivity extends AppCompatActivity {
    RecyclerView attendanceRv;
    FirebaseAuth firebaseAuth;
    DatabaseReference myRef;
    ViewAttendanceAdapter viewAttendanceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance_by_employee);

        firebaseAuth = FirebaseAuth.getInstance();
        attendanceRv = (RecyclerView) findViewById(R.id.attendance_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        attendanceRv.setLayoutManager(linearLayoutManager);
//        attendanceRv.setLayoutManager(new LinearLayoutManager(this));
        myRef = FirebaseDatabase.getInstance().getReference().child("attendance");
        FirebaseRecyclerOptions<LocationHelper> options =
                new FirebaseRecyclerOptions.Builder<LocationHelper>()
                        .setQuery(myRef.child(firebaseAuth.getUid()), LocationHelper.class)
                        .build();

        viewAttendanceAdapter = new ViewAttendanceAdapter(options);
        attendanceRv.setAdapter(viewAttendanceAdapter);
    }
}