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
import com.ujwal.locate2.databinding.StudentsAdapterListItemBinding;
import com.ujwal.locate2.models.Student;
import com.ujwal.locate2.view.ChatActivity;
import com.ujwal.locate2.view.ImagePreviewActivity;

import java.util.ArrayList;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.AllConnectionsViewHolder> {
    public Context context;
    private ArrayList<Student> students;

    public StudentsAdapter(Context context, ArrayList<Student> students) {
        this.context = context;
        this.students = students;
    }

    @NonNull
    @Override
    public AllConnectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StudentsAdapterListItemBinding StudentsAdapterListItemBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.students_adapter_list_item,parent,false);
        return new AllConnectionsViewHolder(StudentsAdapterListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AllConnectionsViewHolder holder, int position) {
        holder.StudentsAdapterListItemBinding.setStudent(students.get(position));
        if(students.get(position).getImageURI()!=null) {
            Glide.with(context).load(students.get(position).getImageURI()).into(holder.StudentsAdapterListItemBinding.imageView3);
        }
    }

    @Override
    public int getItemCount() {
        return students ==null?0: students.size();
    }

    public class AllConnectionsViewHolder extends RecyclerView.ViewHolder
    {
        private StudentsAdapterListItemBinding StudentsAdapterListItemBinding;
        public AllConnectionsViewHolder(@NonNull final StudentsAdapterListItemBinding StudentsAdapterListItemBinding) {
            super(StudentsAdapterListItemBinding.getRoot());
            this.StudentsAdapterListItemBinding=StudentsAdapterListItemBinding;
            StudentsAdapterListItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();
                    Intent i=new Intent(context, ChatActivity.class);
                    i.putExtra("student", students.get(pos));
                    context.startActivity(i);
                }
            });
            StudentsAdapterListItemBinding.imageView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String imageURI=students.get(getAdapterPosition()).getImageURI();
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
