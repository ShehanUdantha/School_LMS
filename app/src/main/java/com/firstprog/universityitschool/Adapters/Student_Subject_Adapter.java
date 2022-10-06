package com.firstprog.universityitschool.Adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firstprog.universityitschool.Interfaces.RecyclerViewInterface;
import com.firstprog.universityitschool.Model.SubjectModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.SubjectAddDialog;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Student_Subject_Adapter extends RecyclerView.Adapter<Student_Subject_Adapter.SubjectDataViewHolder> implements RecyclerViewInterface {
    private Context context;
    private ArrayList<SubjectModel> subList;
    private RecyclerViewInterface recyclerViewInterface;

    public Student_Subject_Adapter(Context context, ArrayList<SubjectModel> subList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.subList = subList;
        this.recyclerViewInterface = recyclerViewInterface;
    }


    @NonNull
    @Override
    public Student_Subject_Adapter.SubjectDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_student_subjects, parent, false);

        return new Student_Subject_Adapter.SubjectDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Student_Subject_Adapter.SubjectDataViewHolder holder, int position) {

        SubjectModel subjectModels = subList.get(position);
        holder.Subjects.setText(subjectModels.getSubID());

        holder.subNumber = subjectModels.getSubID();
        holder.preVAL = subjectModels.getSubMainChildID();
    }

    @Override
    public int getItemCount() {
        return subList.size();
    }

    @Override
    public void onItemClick(int Position, String value, String preValue) {

    }


    public class SubjectDataViewHolder extends RecyclerView.ViewHolder{

        String subNumber, preVAL;
        TextView Subjects;


        public SubjectDataViewHolder(@NonNull View itemView) {
            super(itemView);

            Subjects = itemView.findViewById(R.id.subject_Name);

            //get item clicked position
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos, subNumber, preVAL);
                        }
                    }
                }
            });

        }
    }
}

