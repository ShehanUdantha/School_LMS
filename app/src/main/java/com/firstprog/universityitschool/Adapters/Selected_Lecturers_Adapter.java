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

import com.bumptech.glide.Glide;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Module.Selected_Lecturer_Fragment;
import com.firstprog.universityitschool.Interfaces.RecyclerViewInterface;
import com.firstprog.universityitschool.Model.LecturerModel;
import com.firstprog.universityitschool.Model.UserModel;
import com.firstprog.universityitschool.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Selected_Lecturers_Adapter extends RecyclerView.Adapter<Selected_Lecturers_Adapter.SelectedDataViewHolder> {
    private Context context;
    private ArrayList<LecturerModel> slList;

    public Selected_Lecturers_Adapter(Context context, ArrayList<LecturerModel> slList) {
        this.context = context;
        this.slList = slList;
    }

    @NonNull
    @Override
    public Selected_Lecturers_Adapter.SelectedDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_selected_lecturer_view, parent, false);

        return new Selected_Lecturers_Adapter.SelectedDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Selected_Lecturers_Adapter.SelectedDataViewHolder holder, int position) {
        LecturerModel lecM = slList.get(position);

        holder.FullName.setText(lecM.getSelected_lecturersName());
        holder.Email.setText(lecM.getSelected_lecturersEmail());
        holder.Index.setText(lecM.getSelected_lecturersIndexNo());
        Glide.with(context)
                .asBitmap()
                .load(lecM.getSelected_lecturersPic())
                .into(holder.prof);

        holder.preVAL = lecM.getSelected_lecturersMainChildID();
        holder.lecPreVal = lecM.getSelected_lecturersID();
        holder.preSubVal = lecM.getSelected_subjectsID();

    }

    @Override
    public int getItemCount() {
        return slList.size();
    }

    public class SelectedDataViewHolder extends RecyclerView.ViewHolder {

        TextView FullName, Email, Index;
        ImageView prof, deleteIcon;
        String preVAL, lecPreVal, preSubVal;

        public SelectedDataViewHolder(@NonNull View itemView) {
            super(itemView);

            FullName = itemView.findViewById(R.id.viewDBLecName);
            Email = itemView.findViewById(R.id.viewDBLecEmail);
            Index = itemView.findViewById(R.id.viewDBLecIndex);
            prof = itemView.findViewById(R.id.viewLecProfilePicture);
            deleteIcon = itemView.findViewById(R.id.btnLecDelete);

            //open action dialog to delete semester details
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Selected_Lecturer_Fragment.btnDelete(preVAL, lecPreVal, preSubVal);
                }
            });
        }
    }
}
