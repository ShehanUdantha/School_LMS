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

import com.firstprog.universityitschool.Model.NotesModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.NotesAddDialog;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Week_Notes_Adapter extends RecyclerView.Adapter<Week_Notes_Adapter.Week_Notes_DataViewHolder>{

    private Context context;
    private ArrayList<NotesModel> notesList;
    private DatabaseReference databaseReference;
    private String getBatchPreNumber, getSemesterPreNumber, getSubjectNumber, getWeekNumber;

    public Week_Notes_Adapter(Context context, ArrayList<NotesModel> notesList, String getBatchPreNumber, String getSemesterPreNumber, String getSubjectNumber, String getWeekNumber) {
        this.context = context;
        this.notesList = notesList;
        this.getBatchPreNumber = getBatchPreNumber;
        this.getSemesterPreNumber = getSemesterPreNumber;
        this.getSubjectNumber = getSubjectNumber;
        this.getWeekNumber = getWeekNumber;;
    }

    public void setFilteredList(ArrayList<NotesModel> filterList) {
        this.notesList = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Week_Notes_Adapter.Week_Notes_DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_weeks_notes, parent, false);

        return new Week_Notes_Adapter.Week_Notes_DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Week_Notes_Adapter.Week_Notes_DataViewHolder holder, int position) {

        NotesModel notesModel = notesList.get(position);
        holder.nTitle.setText(notesModel.getNotesTitle());
        holder.nSubTitle.setText(notesModel.getNotesSubtitle());
        holder.nNotes.setText(notesModel.getNote());
        holder.nDates.setText(notesModel.getNoteDate());

        if(notesModel.getNotesPriority() != null && notesModel.getNotesPriority().equalsIgnoreCase("1")){
            holder.nPriority.setImageResource(R.drawable.green_shape);
        } else if(notesModel.getNotesPriority() != null && notesModel.getNotesPriority().equalsIgnoreCase("2")){
            holder.nPriority.setImageResource(R.drawable.yellow_shape);
        } else if(notesModel.getNotesPriority() != null && notesModel.getNotesPriority().equalsIgnoreCase("3")){
            holder.nPriority.setImageResource(R.drawable.red_shape);
        } else {
            holder.nPriority.setImageResource(R.drawable.green_shape);
        }

        holder.preVAL = notesModel.getNotesMainChildID();
        holder.TEXTnTitle = notesModel.getNotesTitle();
        holder.TEXTnSubTitle = notesModel.getNotesSubtitle();
        holder.TEXTnNotes = notesModel.getNote();
        holder.TEXTnPriority = notesModel.getNotesPriority();


    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public class Week_Notes_DataViewHolder extends RecyclerView.ViewHolder{

        TextView nTitle, nSubTitle, nNotes, nDates;
        ImageView penIcon, deleteIcon, nPriority;
        String preVAL, TEXTnTitle, TEXTnSubTitle, TEXTnNotes, TEXTnPriority;

        public Week_Notes_DataViewHolder(@NonNull View itemView) {
            super(itemView);

            nTitle = itemView.findViewById(R.id.noteTitle);
            nSubTitle = itemView.findViewById(R.id.noteSubTitle);
            nNotes = itemView.findViewById(R.id.enterNote);
            nPriority = itemView.findViewById(R.id.noteShape);
            nDates = itemView.findViewById(R.id.submitTime);
            penIcon = itemView.findViewById(R.id.btnEdit4);
            deleteIcon = itemView.findViewById(R.id.btnDelete4);

            //open dialog to edit notes
            penIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NotesAddDialog notesAddDialog = new NotesAddDialog(context);
                    notesAddDialog.showDialogWithDetails(TEXTnTitle, TEXTnSubTitle, TEXTnNotes, TEXTnPriority);

                    notesAddDialog.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notesAddDialog.updateFirebaseDetails(getBatchPreNumber, getSemesterPreNumber, getSubjectNumber, getWeekNumber, preVAL);
                            notifyDataSetChanged();
                        }
                    });
                }
            });

            //open action dialog to delete notes details
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(getBatchPreNumber).child("batch_Sem").child(getSemesterPreNumber).child("semester_subject").child(getSubjectNumber).child("weeks").child(getWeekNumber).child("week_notes");

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
