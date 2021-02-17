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

public class PayslipActivity extends AppCompatActivity {

    TextView pay_date6;
    EditText fullname6,payable_days6,basicsal6,bonus6,absent6,epf6,health_ins6,esi6,child_sup6,net_pay6;
    Button submit6;
    private FirebaseFirestore firebaseFirestore;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payslip);


        fullname6 = findViewById(R.id.fullname_ui6);
        pay_date6 = findViewById(R.id.pay_date_ui6);
        payable_days6 = findViewById(R.id.payable_days_ui6);
        basicsal6 = findViewById(R.id.basic_salary_ui6);
        bonus6 = findViewById(R.id.bonus_ui6);
        absent6 = findViewById(R.id.absent_ui6);
        epf6 = findViewById(R.id.epf_ui6);
        health_ins6 = findViewById(R.id.health_insurance_ui6);
        esi6 = findViewById(R.id.esi_ui6);
        child_sup6 = findViewById(R.id.child_support_ui6);
        net_pay6 = findViewById(R.id.net_pay_ui6);
        submit6 = findViewById(R.id.submit_btn_ui6);
        progressBar = findViewById(R.id.progressBar_ui6);



        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        mainuser(currentUser);









        //---------------------------------Calender-----------------------------------------//

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        pay_date6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        PayslipActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        String date = dayOfMonth+"/"+month+"/"+year;
                        pay_date6.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
    }

    private void mainuser(final FirebaseUser currentUser) {

        submit6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ein = fullname6.getText().toString().trim();
                String paydate = pay_date6.getText().toString().trim();
                String payabledays = payable_days6.getText().toString().trim();
                String basicsalary = basicsal6.getText().toString().trim();
                String absent = absent6.getText().toString().trim();
                String bonus = bonus6.getText().toString().trim();
                String epf = epf6.getText().toString().trim();
                String healthins = health_ins6.getText().toString().trim();
                String esi = esi6.getText().toString().trim();
                String childsup = child_sup6.getText().toString().trim();
                final String netpay = net_pay6.getText().toString().trim();


                Map<String, Object> payslip = new HashMap<>();


                payslip.put("FullName",ein);
                payslip.put("PayDate",paydate);
                payslip.put("PayableDays",payabledays);
                payslip.put("BasicSalary",basicsalary);
                payslip.put("Absent",absent);
                payslip.put("Bonus",bonus);
                payslip.put("ePF",epf);
                payslip.put("HealthIns",healthins);
                payslip.put("ESI",esi);
                payslip.put("ChildSupp",childsup);
                payslip.put("NetPay",netpay);



                progressBar.setVisibility(View.VISIBLE);




                firebaseFirestore.collection("UserInfo").document("PaySlip")
                        .collection(currentUser.getUid()).add(payslip).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Toast.makeText(PayslipActivity.this, "PaySlip Generated !", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), PayrollDashboardActivity.class));
                        progressBar.setVisibility(View.GONE);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PayslipActivity.this, "Error !", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), StudentMainActivity.class));
                        finish();

                    }
                });


            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PayslipActivity.this, StudentMainActivity.class));
        finish();
    }
}
