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

public class SubjectData_Adapter extends RecyclerView.Adapter<SubjectData_Adapter.SubjectDataViewHolder> implements RecyclerViewInterface {
    private Context context;
    private ArrayList<SubjectModel> subList;
    private DatabaseReference databaseReference;
    private String getBatchPreNumber, getSemesterPreNumber;
    private RecyclerViewInterface recyclerViewInterface;
    private boolean isTrue;

    public SubjectData_Adapter(Context context, ArrayList<SubjectModel> subList, String getBatchPreNumber, String getSemesterPreNumber, boolean isTrue, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.subList = subList;
        this.getBatchPreNumber = getBatchPreNumber;
        this.getSemesterPreNumber = getSemesterPreNumber;
        this.isTrue = isTrue;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public void setFilteredList(ArrayList<SubjectModel> filterList) {
        this.subList = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubjectData_Adapter.SubjectDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_subject_view_card, parent, false);

        return new SubjectData_Adapter.SubjectDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectData_Adapter.SubjectDataViewHolder holder, int position) {

        SubjectModel subjectModels = subList.get(position);
        holder.Subjects.setText(subjectModels.getSubID());

        //for display details with editText when press edit icon
        holder.subNumber = subjectModels.getSubID();
        holder.preVAL = subjectModels.getSubMainChildID();

        if(isTrue != true){
            holder.penIcon.setVisibility(View.GONE);
            holder.deleteIcon.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return subList.size();
    }

    @Override
    public void onItemClick(int Position, String value, String preValue) {

    }


    public class SubjectDataViewHolder extends RecyclerView.ViewHolder{

        TextView Subjects;
        ImageView penIcon, deleteIcon;
        String subNumber, preVAL;

        public SubjectDataViewHolder(@NonNull View itemView) {
            super(itemView);
            Subjects = itemView.findViewById(R.id.subjectName);
            penIcon = itemView.findViewById(R.id.btnEdit3);
            deleteIcon = itemView.findViewById(R.id.btnDelete3);

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

            //open dialog to edit subject number
            penIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SubjectAddDialog subjectAddDialog_ = new SubjectAddDialog(context);
                    subjectAddDialog_.showDialogWithDetails(subNumber, "Update Subject");

                    subjectAddDialog_.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            subjectAddDialog_.updateFirebaseDetails(getBatchPreNumber, getSemesterPreNumber, preVAL);
                            notifyDataSetChanged();
                        }
                    });
                }
            });

            //open action dialog to delete subject details
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(getBatchPreNumber).child("batch_Sem").child(getSemesterPreNumber).child("semester_subject");

                            databaseReference.child(preVAL).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseReference == null) {
                                        Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Delete Successful", Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });

        }
    }
}
