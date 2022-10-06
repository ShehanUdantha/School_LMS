package com.firstprog.universityitschool.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firstprog.universityitschool.Interfaces.RecyclerViewInterface;
import com.firstprog.universityitschool.Model.QuizModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.Week_Quiz_Name_Add_Dialog;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Week_Quiz_Name_Adapter extends RecyclerView.Adapter<Week_Quiz_Name_Adapter.ViewHolder> implements RecyclerViewInterface {

    private Context context;
    private ArrayList<QuizModel> qList;
    private DatabaseReference databaseReference;
    private String getBatchPreNumber, getSemesterPreNumber, getSubjectNumber, getWeekNumber;
    private RecyclerViewInterface recyclerViewInterface;

    public Week_Quiz_Name_Adapter(Context context, ArrayList<QuizModel> qList, String getBatchPreNumber, String getSemesterPreNumber, String getSubjectNumber, String getWeekNumber, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.qList = qList;
        this.getBatchPreNumber = getBatchPreNumber;
        this.getSemesterPreNumber = getSemesterPreNumber;
        this.getSubjectNumber = getSubjectNumber;
        this.getWeekNumber = getWeekNumber;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public void setFilteredList(ArrayList<QuizModel> filterList) {
        this.qList = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Week_Quiz_Name_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_small_card_view, parent, false);

        return new Week_Quiz_Name_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Week_Quiz_Name_Adapter.ViewHolder holder, int position) {
        holder.itemName.setText("Quiz:");

        QuizModel quizModel = qList.get(position);
        holder.Quiz.setText(quizModel.getQuizID());
        holder.semSwitch.setVisibility(View.GONE);

        //for display details with editText when press edit icon
        holder.quizNumber = quizModel.getQuizID();
        holder.preVAL = quizModel.getQuizMainChildID();

    }

    @Override
    public int getItemCount() {
        return qList.size();
    }

    @Override
    public void onItemClick(int Position, String value, String preValue) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemName, Quiz;
        ImageView penIcon, deleteIcon;
        String quizNumber, preVAL;
        Switch semSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            Quiz = itemView.findViewById(R.id.viewItem);
            penIcon = itemView.findViewById(R.id.btnEdit2);
            deleteIcon = itemView.findViewById(R.id.btnDelete2);
            semSwitch = itemView.findViewById(R.id.semSwitch);

            //get item clicked position
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos, quizNumber, preVAL);
                        }
                    }
                }
            });

            //open dialog to edit quiz number
            penIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Week_Quiz_Name_Add_Dialog week_quiz_name_add_dialog = new Week_Quiz_Name_Add_Dialog(context);
                    week_quiz_name_add_dialog.showDialogWithDetails(quizNumber, "Update Quiz");

                    week_quiz_name_add_dialog.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            week_quiz_name_add_dialog.updateFirebaseDetails(getBatchPreNumber, getSemesterPreNumber, getSubjectNumber, getWeekNumber, preVAL);
                            notifyDataSetChanged();
                        }
                    });
                }
            });

            //open action dialog to delete quiz details
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(getBatchPreNumber).child("batch_Sem").child(getSemesterPreNumber).child("semester_subject").child(getSubjectNumber).child("weeks").child(getWeekNumber).child("week_quiz");

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
        }}
}
