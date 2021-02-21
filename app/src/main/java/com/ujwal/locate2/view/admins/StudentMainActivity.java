package com.ujwal.locate2.view.admins;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.ujwal.locate2.R;
import com.ujwal.locate2.adapters.FacultiesAdapter;
import com.ujwal.locate2.view.CalendarActivity;
import com.ujwal.locate2.models.Faculty;
import com.ujwal.locate2.models.Student;
import com.ujwal.locate2.view.payroll.AddEmployeeActivity;
import com.ujwal.locate2.view.payroll.PayslipActivity;
import com.ujwal.locate2.view.payroll.TransactionsActivity;
import com.ujwal.locate2.view.payroll.ViewEmployeeActivity;
import com.ujwal.locate2.services.ChatNotification;
import com.ujwal.locate2.services.LocationNotification;
import com.ujwal.locate2.utils.FirebaseUtils;
import com.ujwal.locate2.view.ImagePreviewActivity;
import com.ujwal.locate2.view.ToolsActivity;
import com.ujwal.locate2.viewmodel.FacultyViewModel;
import com.ujwal.locate2.viewmodel.StudentViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class StudentMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView emailnav;
    TextView namenav;
    boolean doubleBackToExitPressedOnce = false;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    FirebaseUser firebaseUser;
    Student student;
    private ProgressBar progressBarNavMenu;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FacultyViewModel facultyViewModel;
    private StudentViewModel studentViewModel;
    FirebaseAuth.AuthStateListener authStateListener;
    private String TAG="StudentMainActivity";
    private ArrayList<Faculty> faculties=new ArrayList<>();
    String uuid;
    private NavigationView navigationView;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    FacultiesAdapter allConnectionsAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Connected Employees");
        progressBar=findViewById(R.id.progressBarHome);
        mFirebaseDatabase = FirebaseUtils.getDatabase();
        studentViewModel= new ViewModelProvider(StudentMainActivity.this).get(StudentViewModel.class);
        facultyViewModel= new ViewModelProvider(StudentMainActivity.this).get(FacultyViewModel.class);

        myRef = mFirebaseDatabase.getReference();
        myRef.child("faculties").keepSynced(true);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(StudentMainActivity.this));
        allConnectionsAdapter=new FacultiesAdapter(StudentMainActivity.this,faculties);
        recyclerView.setAdapter(allConnectionsAdapter);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser=firebaseAuth.getCurrentUser();
                Log.d("Auth State","Auth State Changed");
            }
        };
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(StudentMainActivity.this,DividerItemDecoration.VERTICAL));
        swipeRefreshLayout=findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.DKGRAY, Color.RED,Color.GREEN,Color.MAGENTA,Color.BLACK,Color.CYAN);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        firebaseUser.reload();
                        mFirebaseDatabase.goOffline();
                        mFirebaseDatabase.goOnline();
                        myRef.child("students").keepSynced(true);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },4000);

            }
        });
        loadDataFromDatabase();
        loadNavigationMenu();
        Intent l=new Intent(StudentMainActivity.this, LocationNotification.class);
        Intent o=new Intent(StudentMainActivity.this, ChatNotification.class);
        startService(l);
        startService(o);
    }

    private void loadDataFromDatabase(){
        facultyViewModel.getAllFaculties().observe(StudentMainActivity.this, new Observer<List<DataSnapshot>>() {
            @Override
            public void onChanged(List<DataSnapshot> dataSnapshots) {
                faculties.clear();
                for(DataSnapshot dataSnapshot:dataSnapshots){
                    showData(dataSnapshot);
                    progressBar.setVisibility(View.INVISIBLE);
                    allConnectionsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void loadNavigationMenu(){
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        studentViewModel.getAllStudents().observe(StudentMainActivity.this, new Observer<List<DataSnapshot>>() {
            @Override
            public void onChanged(List<DataSnapshot> dataSnapshots) {
                for (DataSnapshot dataSnapshot : dataSnapshots) {
                    try {
                        if (dataSnapshot.getKey().equals(firebaseUser.getUid())) {
                            student = dataSnapshot.getValue(Student.class);
                            View headerView = navigationView.getHeaderView(0);
                            progressBarNavMenu=headerView.findViewById(R.id.progressBarNavMenu);
                            progressBarNavMenu.getIndeterminateDrawable().setColorFilter(getColor(R.color.gradient_4_start), PorterDuff.Mode.MULTIPLY);
                            TextView email = (TextView) headerView.findViewById(R.id.emailnav);
                            if (student.getEmail() == null) {
                                email.setText(student.getPhoneno());
                            } else {
                                email.setText(student.getEmail());
                            }
                            TextView name = headerView.findViewById(R.id.namenav);
                            name.setText(student.getName());
                            ImageView imageView = headerView.findViewById(R.id.imageViewMe);
                            if (student.getImageURI()!=null) {
                                progressBarNavMenu.setVisibility(View.VISIBLE);
                                Glide.with(StudentMainActivity.this).load(student.getImageURI()).listener(requestListener()).into(imageView);
                            }
                            profilePictureClickListener(imageView);
                        }
                    } catch (Exception e) {
                        Log.d("NavMenu", e.getMessage());
                    }
                }
            }
        });
    }

    private void showData(DataSnapshot dataSnapshot) {
        Faculty u = new Faculty();
        uuid = dataSnapshot.getKey();
        if (firebaseUser != null) {
            if (!uuid.equals(firebaseUser.getUid())) {
                try {
                    u.setName((dataSnapshot.getValue(Faculty.class).getName()));
                    u.setUuid(uuid);
                    u.setImageURI(dataSnapshot.getValue(Faculty.class).getImageURI());
                    u.setCollege(dataSnapshot.getValue(Faculty.class).getCollege());
                    u.setPhoneno(dataSnapshot.getValue(Faculty.class).getPhoneno());
                    u.setEmail(dataSnapshot.getValue(Faculty.class).getEmail());
                    u.setDepartment(dataSnapshot.getValue(Faculty.class).getDepartment());
                    u.setEmployeeid(dataSnapshot.getValue(Faculty.class).getEmployeeid());
                } catch (Exception e) {
                    Log.d("showDataStudent", e.getMessage());
                }
                faculties.add(u);
            }
        }
    }

    @Override
    protected void onResume() {
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        loadNavigationMenu();
        super.onResume();
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



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.tools) {
            Intent intent=new Intent(StudentMainActivity.this, ToolsActivity.class);
            intent.putExtra("student",student);
            startActivity(intent);

        } else if (id == R.id.connections) {
//           Intent intent = new Intent(StudentMainActivity.this, AllConnections.class);
            Intent intent = new Intent(getApplicationContext(), StudentMainActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_calendar) {
            Intent intent = new Intent(StudentMainActivity.this, CalendarActivity.class);
            startActivity(intent);
        }

       else if (id == R.id.nav_payroll) {
           Intent intent=new Intent(StudentMainActivity.this, AddEmployeeActivity.class);
           startActivity(intent);

       }else if (id == R.id.nav_payroll3) {
           Intent intent=new Intent(StudentMainActivity.this, ViewEmployeeActivity.class);
           startActivity(intent);

       }else if (id == R.id.nav_payroll1) {
           Intent intent=new Intent(StudentMainActivity.this, PayslipActivity.class);
           startActivity(intent);

       }else if (id == R.id.nav_payroll2) {
           Intent intent=new Intent(StudentMainActivity.this, TransactionsActivity.class);
           startActivity(intent);

       }
        /*if (id == R.id.messages) {
        } else if (id == R.id.requests) {

        }*/
       else if (id == R.id.signout) {
            if(authStateListener!=null)
            {
                firebaseAuth.removeAuthStateListener(authStateListener);
            }
            Intent l=new Intent(StudentMainActivity.this,LocationNotification.class);
            Intent o=new Intent(StudentMainActivity.this, ChatNotification.class);
            getApplicationContext().stopService(l);
            getApplicationContext().stopService(o);
            firebaseAuth.signOut();
            loadLauncherActivity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadLauncherActivity()
    {
        startActivity(new Intent(StudentMainActivity.this, StudentLogin.class));
        StudentMainActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit",Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    public void profilePictureClickListener(ImageView imageView){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(student.getImageURI()!=null){
                    Intent i=new Intent(StudentMainActivity.this, ImagePreviewActivity.class);
                    i.putExtra("imageURI",student.getImageURI());
                    startActivity(i);
                }
            }
        });
    }

    public RequestListener<Drawable> requestListener(){
        return new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                progressBarNavMenu.setVisibility(View.GONE);
                Toast.makeText(StudentMainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                progressBarNavMenu.setVisibility(View.GONE);
                return false;
            }
        };
    }

}
