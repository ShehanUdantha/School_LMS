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
import com.firstprog.universityitschool.Model.AssignmentsModel;
import com.firstprog.universityitschool.Model.PDFModel;
import com.firstprog.universityitschool.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class Week_Assignment_Adapter extends RecyclerView.Adapter<Week_Assignment_Adapter.ViewHolder> implements RecyclerViewInterface {


    private Context context;
    private ArrayList<AssignmentsModel> pdfList;
    private DatabaseReference databaseReference;
    private String getBatchPreNumber, getSemesterPreNumber, getSubjectNumber, getWeekNumber;
    private RecyclerViewInterface recyclerViewInterface;

    public Week_Assignment_Adapter(Context context, ArrayList<AssignmentsModel> pdfList, String getBatchPreNumber, String getSemesterPreNumber, String getSubjectNumber, String getWeekNumber, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.pdfList = pdfList;
        this.getBatchPreNumber = getBatchPreNumber;
        this.getSemesterPreNumber = getSemesterPreNumber;
        this.getSubjectNumber = getSubjectNumber;
        this.getWeekNumber = getWeekNumber;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public void setFilteredList(ArrayList<AssignmentsModel> filterList) {
        this.pdfList = filterList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public Week_Assignment_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_selected_pdf_and_video, parent, false);

        return new Week_Assignment_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Week_Assignment_Adapter.ViewHolder holder, int position) {

        AssignmentsModel pdfModel = pdfList.get(position);
        holder.pdfName.setText(pdfModel.getAssignmentName());
        holder.pdfDate.setText(pdfModel.getAssignmentUploadDate());

        holder.preVAL = pdfModel.getAssignmentsMainChildID();
        holder.url = pdfModel.getAssignmentUrl();
    }

    @Override
    public int getItemCount() {
        return pdfList.size();
    }

    @Override
    public void onItemClick(int Position, String value, String preValue) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView pdfName, pdfDate;
        String preVAL, url;
        ImageView deleteIcon, fileIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pdfName = itemView.findViewById(R.id.uploadFileName);
            pdfDate = itemView.findViewById(R.id.uploadFileDate);
            deleteIcon = itemView.findViewById(R.id.btnPDFDelete);
            fileIcon = itemView.findViewById(R.id.fileIcon);

            fileIcon.setImageResource(R.drawable.pdf);

            //get item clicked position
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos, url, preVAL);
                        }
                    }
                }
            });

            //open action dialog to delete pdf details
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(getBatchPreNumber).child("batch_Sem").child(getSemesterPreNumber).child("semester_subject").child(getSubjectNumber).child("weeks").child(getWeekNumber).child("week_assignments");

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
