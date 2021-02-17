package com.ujwal.locate2.view.employees;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.ujwal.locate2.R;
import com.ujwal.locate2.adapters.StudentsAdapter;
import com.ujwal.locate2.view.CalendarActivity;
import com.ujwal.locate2.view.leave.DashboardActivity;
import com.ujwal.locate2.view.leave.RequestALeaveActivity;
import com.ujwal.locate2.models.Faculty;
import com.ujwal.locate2.models.Student;
import com.ujwal.locate2.services.ChatNotification;
import com.ujwal.locate2.services.GetLocation;
import com.ujwal.locate2.utils.FirebaseUtils;
import com.ujwal.locate2.view.ImagePreviewActivity;
import com.ujwal.locate2.view.ToolsActivity;
import com.ujwal.locate2.viewmodel.FacultyViewModel;
import com.ujwal.locate2.viewmodel.StudentViewModel;

import java.util.ArrayList;
import java.util.List;

public class FacultyMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean doubleBackToExitPressedOnce = false;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    FirebaseUser firebaseUser;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private String TAG="MainActivityFaculty";
    private ArrayList<Student> students=new ArrayList<>();
    String uuid;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    View parent;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    private Faculty faculty;
    StudentsAdapter studentsAdapter;
    FacultyViewModel facultyViewModel;
    StudentViewModel studentViewModel;
    NavigationView navigationView;
    private ProgressBar progressBarNavMenu;
    private  FirebaseAuth.AuthStateListener authStateListener;
    FloatingActionButton floatingActionButton;
    public static int l=0;
    private SearchView searchView;
    private int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Connected Admins");
        progressBar=findViewById(R.id.progressBarHome);
        parent=findViewById(android.R.id.content);
        mFirebaseDatabase = FirebaseUtils.getDatabase();
        myRef = mFirebaseDatabase.getReference();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
//        facultyViewModel= ViewModelProviders.of(FacultyMainActivity.this).get(FacultyViewModel.class);
        facultyViewModel = new ViewModelProvider(FacultyMainActivity.this).get(FacultyViewModel.class);
//        studentViewModel=ViewModelProviders.of(FacultyMainActivity.this).get(StudentViewModel.class);
        studentViewModel = new ViewModelProvider(FacultyMainActivity.this).get(StudentViewModel.class);

        myRef.child("students").keepSynced(true);
        floatingActionButton=findViewById(R.id.floatingActionButton);
        floatingActionButton.setVisibility(View.VISIBLE);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(FacultyMainActivity.this,DividerItemDecoration.VERTICAL));
        swipeRefreshLayout=findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.DKGRAY, Color.RED,Color.GREEN,Color.MAGENTA,Color.BLACK,Color.CYAN);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser=firebaseAuth.getCurrentUser();
                Log.d("Auth State","Auth State Changed");

            }
        };
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        firebaseUser.reload();
                        mFirebaseDatabase.goOffline();
                        mFirebaseDatabase.goOnline();
                        myRef.child("faculties").keepSynced(true);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },4000);

            }
        });
        loadListFromDatabase();
        loadNavigationMenu();
        recyclerView.setLayoutManager(new LinearLayoutManager(FacultyMainActivity.this));
        studentsAdapter =new StudentsAdapter(FacultyMainActivity.this, students);
        recyclerView.setAdapter(studentsAdapter);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GetLocation.runservice==1)
                {
                   l=0;
                    Intent x= new Intent(FacultyMainActivity.this, GetLocation.class);
                    getApplicationContext().stopService(x);
                    Snackbar.make(v,"Location Hidden",Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                        buildAlertMessageNoGps();
                    }
                    else{
                        checkUserPermission();
                    }
                }
            }
        });
        Intent o=new Intent(FacultyMainActivity.this, ChatNotification.class);
        startService(o);
    }

    private void loadNavigationMenu(){
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        firebaseUser=firebaseAuth.getCurrentUser();
        View headerView = navigationView.getHeaderView(0);
        TextView email = (TextView) headerView.findViewById(R.id.emailnav);
        email.setText(firebaseUser.getEmail());
        TextView name=headerView.findViewById(R.id.namenav);
        name.setText(firebaseUser.getDisplayName());
        navigationView.getMenu().getItem(0).setChecked(true);
        facultyViewModel.getAllFaculties().observe(FacultyMainActivity.this, new Observer<List<DataSnapshot>>() {
            @Override
            public void onChanged(List<DataSnapshot> dataSnapshots) {
                for(DataSnapshot dataSnapshot:dataSnapshots){
                    try {
                        if (dataSnapshot.getKey().equals(firebaseUser.getUid())) {
                            faculty = dataSnapshot.getValue(Faculty.class);
                            View headerView = navigationView.getHeaderView(0);
                            progressBarNavMenu=headerView.findViewById(R.id.progressBarNavMenu);
                            progressBarNavMenu.getIndeterminateDrawable().setColorFilter(getColor(R.color.gradient_4_start), PorterDuff.Mode.MULTIPLY);
                            TextView email = (TextView) headerView.findViewById(R.id.emailnav);
                            if (faculty.getEmail()==null) {
                                email.setText(faculty.getPhoneno());
                            } else {
                                email.setText(faculty.getEmail());
                            }
                            TextView name = headerView.findViewById(R.id.namenav);
                            name.setText(faculty.getName());
                            ImageView imageView = headerView.findViewById(R.id.imageViewMe);
                            if (faculty.getImageURI()!=null) {
                                progressBarNavMenu.setVisibility(View.VISIBLE);
                                Glide.with(FacultyMainActivity.this).load(faculty.getImageURI()).listener(requestListener()).into(imageView);
                            }
                            profilePictureClickListener(imageView);
                        }
                    }
                    catch(Exception e)
                    {
                        Log.d("NavMenu", e.getMessage());
                    }
                }
            }});
    }

    private void loadListFromDatabase(){
        studentViewModel.getAllStudents().observe(FacultyMainActivity.this, new Observer<List<DataSnapshot>>() {
            @Override
            public void onChanged(List<DataSnapshot> dataSnapshots) {
                students.clear();
                for(DataSnapshot dataSnapshot:dataSnapshots){
                    showData(dataSnapshot);
                    progressBar.setVisibility(View.INVISIBLE);
                    studentsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void showData(DataSnapshot dataSnapshot){
        Student fc=new Student();
        uuid= dataSnapshot.getKey();
        if(firebaseUser!=null) {
            if (!uuid.equals(firebaseUser.getUid())) {
                try {
                    fc.setName((dataSnapshot.getValue(Student.class).getName()));
                    fc.setUuid(uuid);
                    fc.setImageURI(dataSnapshot.getValue(Student.class).getImageURI());
                    fc.setCollege(dataSnapshot.getValue(Student.class).getCollege());
                    fc.setDepartment(dataSnapshot.getValue(Student.class).getDepartment());
                    fc.setPhoneno(dataSnapshot.getValue(Student.class).getPhoneno());
                    fc.setEnno(dataSnapshot.getValue(Student.class).getEnno());
                    fc.setEmail(dataSnapshot.getValue(Student.class).getEmail());
                } catch (Exception e) {
                    Log.d("showDataFaculty", e.getMessage());
                }
                students.add(fc);
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.tools) {
            Intent intent=new Intent(FacultyMainActivity.this, ToolsActivity.class);
            intent.putExtra("faculty",faculty);
            startActivity(intent);

        } else if (id == R.id.connections) {
//            Intent intent = new Intent(FacultyMainActivity.this, FacultyMainActivity.class);
            Intent intent = new Intent(getApplicationContext(), FacultyMainActivity.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_dash) {
            Intent intent=new Intent(FacultyMainActivity.this, DashboardActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_calendar) {
            Intent intent=new Intent(FacultyMainActivity.this, CalendarActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_leave_management) {
            Intent intent=new Intent(FacultyMainActivity.this, RequestALeaveActivity.class);
            startActivity(intent);

        }
         /*   else if (id == R.id.messages) {
        } else if (id == R.id.requests) {
        } */
         else if (id == R.id.signout) {
            Intent o=new Intent(FacultyMainActivity.this, ChatNotification.class);
            getApplicationContext().stopService(o);
            if (GetLocation.runservice == 1) {
                GetLocation.notificationManager.cancel(2);
                Intent x = new Intent(getApplicationContext(), GetLocation.class);
                getApplicationContext().stopService(x);
            }
            firebaseAuth.signOut();
            loadLauncherActivity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadLauncherActivity()
    {
        startActivity(new Intent(FacultyMainActivity.this, FacultyLogin.class));
        FacultyMainActivity.this.finish();
    }

    public void profilePictureClickListener(ImageView imageView){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(faculty.getImageURI()!=null){
                    Intent i=new Intent(FacultyMainActivity.this, ImagePreviewActivity.class);
                    i.putExtra("imageURI",faculty.getImageURI());
                    startActivity(i);
                }
            }
        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    l=1;
                    Snackbar.make(parent,"Location Visible",Snackbar.LENGTH_SHORT).show();
                    Intent x= new Intent(FacultyMainActivity.this, GetLocation.class);
                    startService(x);
                } else {
                    Toast.makeText(FacultyMainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    checkUserPermission();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    private void checkUserPermission() {
        if (Build.VERSION.SDK_INT >= 28) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                                {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            }
            else
            {
                l=1;
                Snackbar.make(parent,"Location Visible",Snackbar.LENGTH_SHORT).show();
                Intent x= new Intent(FacultyMainActivity.this, GetLocation.class);
                startService(x);
            }
        }
        else{
            l=1;
            Snackbar.make(parent,"Location Visible",Snackbar.LENGTH_SHORT).show();
            Intent x= new Intent(FacultyMainActivity.this, GetLocation.class);
            startService(x);
        }
    }


    void buildAlertMessageNoGps() {
        final AlertDialog.Builder builders = new AlertDialog.Builder(this);
        builders.setMessage("Your GPS seems to be disabled or isn't set to \'High Accuracy\'. Do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Toast.makeText(FacultyMainActivity.this,"Error! Turn on GPS! ",Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builders.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        try {
            myRef.child("geocordinates").child(faculty.getUuid()).removeValue();
        }
        catch(Exception e)
        {
            Log.d("TAG",e.getMessage());
        }
            if (GetLocation.runservice == 1) {
                GetLocation.notificationManager.cancel(2);
                Intent x = new Intent(getApplicationContext(), GetLocation.class);
                getApplicationContext().stopService(x);
            }

        super.onDestroy();
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

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view,menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        searchView =(SearchView) menuItem.getActionView();
        search(searchView);
        return super.onCreateOptionsMenu(menu);
    }*/

    public RequestListener<Drawable> requestListener(){
        return new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                progressBarNavMenu.setVisibility(View.GONE);
                Toast.makeText(FacultyMainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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
