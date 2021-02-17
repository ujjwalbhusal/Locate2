package com.ujwal.locate2.view.payroll;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ujwal.locate2.R;
import com.ujwal.locate2.view.admins.StudentMainActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddEmployeeActivity extends AppCompatActivity {

    TextView doj5;
    Button submit_btn5;
    EditText ein5, fullname5, mobile5, email5;
    ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);


        doj5 = findViewById(R.id.doj_ui5);
        ein5 = findViewById(R.id.ein_ui5);
        fullname5 = findViewById(R.id.fullname_ui5);
        mobile5 = findViewById(R.id.mobile_input_ui5);
        email5 = findViewById(R.id.email_input_ui5);
        submit_btn5 = findViewById(R.id.submit_btn_ui5);
        progressBar = findViewById(R.id.progressBar_ui5);


        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentuser = firebaseAuth.getCurrentUser();
        mainuser(currentuser);




        //===================================CALENDER===================================

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);




        doj5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddEmployeeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        String date = dayOfMonth+"/"+month+"/"+year;
                        doj5.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });


    }

    private void mainuser(final FirebaseUser cUser) {


        submit_btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String ein = ein5.getText().toString();
                String fullname = fullname5.getText().toString();
                String mobile = mobile5.getText().toString();
                String email = email5.getText().toString();
                String doj = doj5.getText().toString();

                Map<String, Object> addemp = new HashMap<>();

                addemp.put("EIN",ein);
                addemp.put("FullName",fullname);
                addemp.put("Mobile",mobile);
                addemp.put("Email",email);
                addemp.put("DOJ",doj);



                progressBar.setVisibility(View.VISIBLE);


                firebaseFirestore.collection("UserInfo").document("AddEmp")
                        .collection(cUser.getUid()).add(addemp).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Toast.makeText(AddEmployeeActivity.this, "Employee Added !", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), PayrollDashboardActivity.class));
                        finish();
                        progressBar.setVisibility(View.GONE);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddEmployeeActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), PayrollDashboardActivity.class));
                        finish();
                    }
                });



            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddEmployeeActivity.this, StudentMainActivity.class));
        finish();
    }
}