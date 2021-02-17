package com.ujwal.locate2.view.admins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ujwal.locate2.R;
import com.ujwal.locate2.databinding.ActivityLoginBinding;
import com.ujwal.locate2.models.Student;
import com.ujwal.locate2.utils.FirebaseUtils;
import com.ujwal.locate2.utils.ForgotPassword;
import com.ujwal.locate2.utils.VerifyActivity;
import com.ujwal.locate2.utils.authentication.LoginHandler;
import com.ujwal.locate2.view.LauncherActivity;
import com.ujwal.locate2.viewmodel.FacultyViewModel;
import com.ujwal.locate2.viewmodel.StudentViewModel;

import java.util.ArrayList;
import java.util.List;

public class StudentLogin extends AppCompatActivity {
    private Button signupButton;
    private Button loginButton;
    private ProgressBar progressBar;
    private EditText email;
    private EditText password;
    private TextView forgotpass;
    private ImageView imageView;
    private ActivityLoginBinding loginBinding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase= FirebaseUtils.getDatabase();
    private DatabaseReference databaseReference;
    private boolean alreadyRegister=false;
    private MaterialDialog materialDialog;
    private boolean isVerified=false;
    private List<DataSnapshot> allStudentsList = new ArrayList<>();
    private List<DataSnapshot> allFacultiesList = new ArrayList<>();
    private NestedScrollView scrollView;
    private StudentViewModel studentViewModel;
    private FacultyViewModel facultyViewModel;
    private  FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBinding= DataBindingUtil.setContentView(StudentLogin.this,R.layout.activity_login);
        getSupportActionBar().setTitle("Admin Login");
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser=firebaseAuth.getCurrentUser();
                Log.d("Auth State","Auth State Changed");

            }
        };
        imageView=loginBinding.imageViewSL;
        Glide.with(StudentLogin.this).load(R.drawable.realtime_hajiri).into(imageView);
        signupButton=loginBinding.signupbutton;
        progressBar=loginBinding.progressBar1;
        loginButton=loginBinding.loginButton;
        email=loginBinding.email;
        scrollView=loginBinding.scrollView;
        databaseReference=firebaseDatabase.getReference();
        password=loginBinding.password;
        forgotpass=loginBinding.textViewforgotstu;
//        studentViewModel = ViewModelProviders.of(StudentLogin.this).get(StudentViewModel.class);

        studentViewModel = new ViewModelProvider(StudentLogin.this).get(StudentViewModel.class);


//        facultyViewModel= ViewModelProviders.of(StudentLogin.this).get(FacultyViewModel.class);

        facultyViewModel = new ViewModelProvider(StudentLogin.this).get(FacultyViewModel.class);


        loginBinding.setClickHandlers(new LoginActivityClickHandlers(email.getText().toString().trim(),password.getText().toString().trim(), StudentLogin.this));

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

    public class LoginActivityClickHandlers extends LoginHandler {
        FirebaseAuth firebaseAuth;
        FirebaseUser firebaseUser;

        public LoginActivityClickHandlers(String email, String password, Context context) {
            super(email, password, context);
        }

        public void onLoginButtonClicked(View view) {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            setEmail(email.getText().toString().trim());
            setPassword(password.getText().toString().trim());
            if (confirmEmailPasswordInput()) {
                progressBar.setVisibility(View.VISIBLE);
                scrollView.smoothScrollTo(progressBar.getScrollX(), progressBar.getScrollY());
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                checkInDatabaseAndLogin();
            }
        }

        public void onSignUpButtonClicked(View view) {
            startActivity(new Intent(StudentLogin.this, StudentSignUp.class));

        }

        public void onForgotPasswordClicked(View view) {
            Intent t = new Intent(StudentLogin.this, ForgotPassword.class);
            t.putExtra("isuser", true);
            startActivity(t);
        }

        public void onLoginAsFacultyClicked(View view) {
            startActivity(new Intent(StudentLogin.this, LauncherActivity.class));
            StudentLogin.this.finish();
        }


        public void onLoginViaPhone(View view) {
            progressBar.setVisibility(View.VISIBLE);
            createDialogFirstForPhone();
            progressBar.setVisibility(View.GONE);
        }

        protected void signInWithPhoneAuthCredential(PhoneAuthCredential credential,String phoneNo) {
            showLoadingDialogue();
            alreadyRegister = false;
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            studentViewModel.getAllInstantStudentsList().thenAccept((List<DataSnapshot> list) -> {
                allStudentsList = list;
                if (allStudentsList.size() != 0) {
                    for (DataSnapshot d : allStudentsList) {
                        if (d.getValue().equals(phoneNo)) {
                            alreadyRegister=true;
                        }
                    }
                } else {
                    Toast.makeText(StudentLogin.this, "An error occurred! Please try again.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    hideLoadingMaterialDialogInstant();
                    return;
                }
                if(!alreadyRegister) {
                    Toast.makeText(StudentLogin.this, "Error! Invalid Credentials.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    hideLoadingMaterialDialogInstant();
                    return;
                }
                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(StudentLogin.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    hideLoadingMaterialDialogInstant();
                                    startActivity(new Intent(StudentLogin.this, StudentMainActivity.class));
                                    StudentLogin.this.finish();
                                } else {
                                    String message = "Error in verification!";
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        message = "Invalid code entered...";
                                    }
                                    Toast.makeText(StudentLogin.this, message, Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    hideLoadingMaterialDialogInstant();
                                }
                            }
                        });
            });
        }

        @Override
        protected void checkInDatabaseAndLogin() {
            isVerified=false;
            alreadyRegister=false;
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            facultyViewModel.getAllInstantFacultiesList().thenAccept((List<DataSnapshot> list) -> {
                allFacultiesList = list;
                if (allFacultiesList.size() != 0) {
                    for (DataSnapshot d : allFacultiesList) {
                        if (d.getValue().equals(email.getText().toString())) {
                            Toast.makeText(StudentLogin.this, "Error! Invalid Credentials.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            hideLoadingMaterialDialogInstant();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(StudentLogin.this, "An error occurred! Please try again.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    hideLoadingMaterialDialogInstant();
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(getEmail(), getPassword()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        hideLoadingMaterialDialogInstant();
                        Toast.makeText(StudentLogin.this, e.getMessage().trim(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser=firebaseAuth.getCurrentUser();
                            if(firebaseUser.isEmailVerified()) {
                                progressBar.setVisibility(View.GONE);
                                Intent i = new Intent(StudentLogin.this, StudentMainActivity.class);
                                startActivity(i);
                                StudentLogin.this.finish();
                            }
                            else {
                                progressBar.setVisibility(View.GONE);
                                Intent i = new Intent(StudentLogin.this, VerifyActivity.class);
                                Student student=new Student();
                                student.setEmail(email.getText().toString());
                                i.putExtra("student", student);
                                startActivity(i);
                                StudentLogin.this.finish();
                            }
                        }
                    }
                });
            });
        }

    }
}
