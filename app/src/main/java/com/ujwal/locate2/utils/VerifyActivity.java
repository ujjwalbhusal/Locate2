package com.ujwal.locate2.utils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ujwal.locate2.R;
import com.ujwal.locate2.models.Faculty;
import com.ujwal.locate2.models.Student;
import com.ujwal.locate2.view.LauncherActivity;
import com.ujwal.locate2.view.employees.FacultyMainActivity;
import com.ujwal.locate2.view.admins.StudentMainActivity;


public class VerifyActivity extends AppCompatActivity {
    TextView verifytext;
    Button ok;
    Button resend;
    Button Cancel;
    Student student;
    boolean isuser;
    Faculty faculty;
    String email;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase=FirebaseUtils.getDatabase();
    DatabaseReference databaseReference;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser=firebaseAuth.getCurrentUser();
                Log.d("Auth State","Auth State Changed");

            }
        };
        ok=findViewById(R.id.ok);
        databaseReference=firebaseDatabase.getReference();
        final Intent i=getIntent();
        firebaseUser.sendEmailVerification();
        student =i.getParcelableExtra("student");
        try {
            if (student != null) {
                isuser = true;
                email = student.getEmail();
            } else {
                faculty = i.getParcelableExtra("faculty");
                isuser = false;
                email = faculty.getEmail();
            }
        }
        catch (Exception e){
            Log.d("Verify",e.getMessage());
        }
        verifytext=findViewById(R.id.textverify);
        verifytext.setText("A verification link is sent to \'"+email+"\'. Please click on the link to verify it.\nAfter verifying, tap on \'OK\' button to continue.\nTap on \'Cancel\' button to register again if you entered your credentials wrong.");
        resend=findViewById(R.id.buttonresendverify);
        Cancel=findViewById(R.id.buttoncancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    firebaseUser.reload();
                    if (firebaseUser.isEmailVerified()) {
                        if (isuser) {
                            startActivity(new Intent(VerifyActivity.this, StudentMainActivity.class));
                            VerifyActivity.this.finish();
                        } else {
                            startActivity(new Intent(VerifyActivity.this, FacultyMainActivity.class));
                            VerifyActivity.this.finish();
                        }
                    } else {
                        Toast.makeText(VerifyActivity.this, "Error! Email isn't yet verified.", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.d("Verify",e.getMessage());
                }
            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                try{
                if(firebaseUser.isEmailVerified()) {
                    Snackbar.make(v,"Email already Verified!",Snackbar.LENGTH_SHORT).show();
                }
                else {
                    firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Snackbar.make(v,"Verification Link sent!",Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }catch (Exception e){
                    Log.d("Verify",e.getMessage());
                }
            }

        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    firebaseAuth.signOut();
                    startActivity(new Intent(VerifyActivity.this, LauncherActivity.class));
                    VerifyActivity.this.finish();

            }catch (Exception e){
                    Log.d("Verify",e.getMessage());
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!=null)
        {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
