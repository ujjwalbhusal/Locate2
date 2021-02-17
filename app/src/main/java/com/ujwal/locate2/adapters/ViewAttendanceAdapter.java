package com.ujwal.locate2.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.ujwal.locate2.R;
import com.ujwal.locate2.models.LocationHelper;


public class ViewAttendanceAdapter extends FirebaseRecyclerAdapter<LocationHelper, ViewAttendanceAdapter.AttendanceViewHolder> {

    Boolean isLeave;
    public ViewAttendanceAdapter(@NonNull FirebaseRecyclerOptions<LocationHelper> options) {

        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position, @NonNull LocationHelper model) {



        if (model.getAddress() != null) {
            holder.viewAddress.setText(model.getAddress());
            holder.leaveCard.setCardBackgroundColor(Color.parseColor("#ffffff"));
            holder.viewAddress.setTextSize(18);
            holder.viewAddress.setTypeface(null, Typeface.NORMAL);


        }
        if (model.getAddress()==null) {
            holder.viewAddress.setText("Leave");

            if(holder.viewAddress.getText()=="Leave"){
                holder.leaveCard.setCardBackgroundColor(Color.parseColor("#ffbb33"));
                holder.viewAddress.setTextSize(22);
                holder.viewAddress.setTypeface(null, Typeface.BOLD_ITALIC);

            }


        }

        holder.viewDate.setText(model.getDate());
        holder.viewTime.setText(model.getTime());

    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_view_single_row, parent, false);
        return new AttendanceViewHolder(view);
    }


    class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView viewAddress;
        TextView viewDate;
        TextView viewTime;
        CardView leaveCard;


        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);


            leaveCard = (CardView) itemView.findViewById(R.id.leavecard);
            viewAddress = (TextView) itemView.findViewById(R.id.txt_viewaddress);
            viewDate = (TextView) itemView.findViewById(R.id.txt_viewdate);
            viewTime = (TextView) itemView.findViewById(R.id.txt_viewtime);

        }
    }
}
