package com.ujwal.locate2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.ujwal.locate2.R;
import com.ujwal.locate2.models.viewemp_info;

public class viewemp_adapter extends FirestoreRecyclerAdapter<viewemp_info, viewemp_adapter.viewemp_holder> {



    public viewemp_adapter(@NonNull FirestoreRecyclerOptions<viewemp_info> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull viewemp_holder holder, int position, @NonNull viewemp_info model) {
        holder.tvfullname.setText(model.getFullName());
        holder.tvein.setText(String.valueOf(model.getEIN()));
        holder.tvdoj.setText(model.getDOJ());
        holder.tvmoblie.setText(model.getMobile());
        holder.tvemail.setText(model.getEmail());
    }

    @NonNull
    @Override
    public viewemp_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewemp_layout, parent, false);
        return new viewemp_holder(v);
    }

    class viewemp_holder extends RecyclerView.ViewHolder{

        TextView tvfullname;
        TextView tvein;
        TextView tvdoj;
        TextView tvmoblie;
        TextView tvemail;

        public viewemp_holder(@NonNull View itemView) {
            super(itemView);

            tvfullname = itemView.findViewById(R.id.viewemp_fullname);
            tvein = itemView.findViewById(R.id.viewemp_ein);
            tvdoj = itemView.findViewById(R.id.viewemp_doj);
            tvmoblie = itemView.findViewById(R.id.viewemp_mobile);
            tvemail = itemView.findViewById(R.id.viewemp_email);


        }
    }

}
