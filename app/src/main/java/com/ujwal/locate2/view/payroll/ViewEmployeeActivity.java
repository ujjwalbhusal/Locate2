package com.ujwal.locate2.view.payroll;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ujwal.locate2.R;
import com.ujwal.locate2.models.viewemp_info;


public class ViewEmployeeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_employee);

        recyclerView = findViewById(R.id.viewemp_recycler);



        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentuser = firebaseAuth.getCurrentUser();
        mainuser(currentuser);




    }

    private void mainuser(final FirebaseUser cUser) {

        Query query = firebaseFirestore.collection("UserInfo").document("AddEmp")
                .collection(cUser.getUid());



        FirestoreRecyclerOptions<viewemp_info> options = new FirestoreRecyclerOptions.Builder<viewemp_info>()
                .setQuery(query, viewemp_info.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<viewemp_info, viewemp_holder>(options) {
            @NonNull
            @Override
            public viewemp_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewemp_layout, parent, false);
                return new viewemp_holder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull viewemp_holder holder, int position, @NonNull viewemp_info model) {

                holder.fullname7.setText(model.getFullName());
                holder.ein7.setText(model.getEIN());
                holder.doj7.setText(model.getDOJ());
                holder.mobile.setText(model.getMobile());
                holder.email7.setText(model.getEmail());

            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);



    }



    private class viewemp_holder extends RecyclerView.ViewHolder{

        private TextView fullname7;
        private TextView ein7;
        private TextView doj7;
        private TextView mobile;
        private TextView email7;

        public viewemp_holder(@NonNull View itemView) {
            super(itemView);

            fullname7 = itemView.findViewById(R.id.viewemp_fullname);
            ein7 = itemView.findViewById(R.id.viewemp_ein);
            doj7 = itemView.findViewById(R.id.viewemp_doj);
            mobile = itemView.findViewById(R.id.viewemp_mobile);
            email7 = itemView.findViewById(R.id.viewemp_email);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ViewEmployeeActivity.this, PayrollDashboardActivity.class));
        finish();
    }
}