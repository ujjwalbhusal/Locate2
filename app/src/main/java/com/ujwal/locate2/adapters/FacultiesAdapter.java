package com.ujwal.locate2.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ujwal.locate2.R;
import com.ujwal.locate2.databinding.FacultiesAdapterListItemBinding;
import com.ujwal.locate2.view.ChatActivity;
import com.ujwal.locate2.view.ImagePreviewActivity;

import java.util.ArrayList;

public class FacultiesAdapter extends RecyclerView.Adapter<FacultiesAdapter.AllConnectionsViewHolder> {
    public Context context;
    private ArrayList<com.ujwal.locate2.models.Faculty> Faculty;

    public FacultiesAdapter(Context context, ArrayList<com.ujwal.locate2.models.Faculty> Faculty) {
        this.context = context;
        this.Faculty = Faculty;
    }

    @NonNull
    @Override
    public AllConnectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FacultiesAdapterListItemBinding allconnectionsListItemBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.faculties_adapter_list_item,parent,false);
        return new AllConnectionsViewHolder(allconnectionsListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AllConnectionsViewHolder holder, int position) {
        holder.allconnectionsListItemBinding.setFaculty(Faculty.get(position));
        if(Faculty.get(position).getImageURI()!=null) {
            Glide.with(context).load(Faculty.get(position).getImageURI()).into(holder.allconnectionsListItemBinding.imageView3);
        }
    }

    @Override
    public int getItemCount() {
        return Faculty==null?0:Faculty.size();
    }

    public class AllConnectionsViewHolder extends RecyclerView.ViewHolder
    {
        private FacultiesAdapterListItemBinding allconnectionsListItemBinding;
        public AllConnectionsViewHolder(@NonNull final FacultiesAdapterListItemBinding allconnectionsListItemBinding) {
            super(allconnectionsListItemBinding.getRoot());
            this.allconnectionsListItemBinding=allconnectionsListItemBinding;
            allconnectionsListItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();
                    Intent i=new Intent(context, ChatActivity.class);
                    i.putExtra("faculty",Faculty.get(pos));
                    context.startActivity(i);
                }
            });
            allconnectionsListItemBinding.imageView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String imageURI=Faculty.get(getAdapterPosition()).getImageURI();
                    if(imageURI!=null){
                        Intent i=new Intent(context, ImagePreviewActivity.class);
                        i.putExtra("imageURI",imageURI);
                        context.startActivity(i);
                    }

                }
            });
        }
    }
}
