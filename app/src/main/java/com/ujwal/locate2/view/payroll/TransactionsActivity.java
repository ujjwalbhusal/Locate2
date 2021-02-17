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
import com.ujwal.locate2.models.txn_layout;
import com.ujwal.locate2.view.admins.StudentMainActivity;

public class TransactionsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcations);


        recyclerView = findViewById(R.id.txn_recycler);

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentuser = firebaseAuth.getCurrentUser();
        mainuser(currentuser);


    }

    private void mainuser(FirebaseUser cUser) {

        Query query = firebaseFirestore.collection("UserInfo").document("PaySlip")
                .collection(cUser.getUid());


        FirestoreRecyclerOptions<txn_layout> options = new FirestoreRecyclerOptions.Builder<txn_layout>()
                .setQuery(query,txn_layout.class)
                .build();


        adapter = new FirestoreRecyclerAdapter<txn_layout, txn_holder>(options) {
            @NonNull
            @Override
            public txn_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.txn_layout, parent, false);
                return new txn_holder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull txn_holder holder, int position, @NonNull txn_layout model) {

                holder.fullname.setText(model.getFullName());
                holder.netpay.setText(model.getNetPay());
                holder.date.setText(model.getPayDate());

            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }

    private class txn_holder extends RecyclerView.ViewHolder{

        private TextView fullname;
        private TextView netpay;
        private TextView date;


        public txn_holder(@NonNull View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.txn_fullname);
            netpay = itemView.findViewById(R.id.txn_netpay);
            date = itemView.findViewById(R.id.txn_date);

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
        startActivity(new Intent(TransactionsActivity.this, StudentMainActivity.class));
        finish();
    }
}
