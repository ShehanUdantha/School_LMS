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

import com.firstprog.universityitschool.Model.QuizAddModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.Week_Quiz_AddDialog;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Week_Quiz_View_Adapter extends RecyclerView.Adapter<Week_Quiz_View_Adapter.ViewHolder> {

    private Context context;
    private ArrayList<QuizAddModel> qList;
    private DatabaseReference databaseReference;
    private String getBatchPreNumber, getSemesterPreNumber, getSubjectNumber, getWeekNumber, getQuizNumber;

    public Week_Quiz_View_Adapter(Context context, ArrayList<QuizAddModel> qList, String getBatchPreNumber, String getSemesterPreNumber, String getSubjectNumber, String getWeekNumber, String getQuizNumber) {
        this.context = context;
        this.qList = qList;
        this.getBatchPreNumber = getBatchPreNumber;
        this.getSemesterPreNumber = getSemesterPreNumber;
        this.getSubjectNumber = getSubjectNumber;
        this.getWeekNumber = getWeekNumber;
        this.getQuizNumber = getQuizNumber;
    }

    public void setFilteredList(ArrayList<QuizAddModel> filterList) {
        this.qList = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Week_Quiz_View_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_quiz_ad_view, parent, false);

        return new Week_Quiz_View_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Week_Quiz_View_Adapter.ViewHolder holder, int position) {
        QuizAddModel quizAddModel = qList.get(position);
        holder.question_.setText(quizAddModel.getQuestion());
        holder.answer1.setText(quizAddModel.getAnswer_one());
        holder.answer2.setText(quizAddModel.getAnswer_two());
        holder.answer3.setText(quizAddModel.getAnswer_three());
        holder.answer4.setText(quizAddModel.getAnswer_four());
        holder.correct_.setText(quizAddModel.getCorrect_letter());

        //for display details with editText when press edit icon
        holder.preVAL = quizAddModel.getQuizNewMainChildID();
        holder.ques = quizAddModel.getQuestion();
        holder.a1 = quizAddModel.getAnswer_one();
        holder.a2 = quizAddModel.getAnswer_two();
        holder.a3 = quizAddModel.getAnswer_three();
        holder.a4 = quizAddModel.getAnswer_four();
        holder.corr = quizAddModel.getCorrect_letter();

    }

    @Override
    public int getItemCount() {
        return qList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView question_, answer1, answer2, answer3, answer4, correct_;
        ImageView penIcon, deleteIcon;
        String preVAL, ques, a1, a2, a3, a4, corr;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            question_ = itemView.findViewById(R.id.question_);
            answer1 = itemView.findViewById(R.id.answer1);
            answer2 = itemView.findViewById(R.id.answer2);
            answer3 = itemView.findViewById(R.id.answer3);
            answer4 = itemView.findViewById(R.id.answer4);
            correct_ = itemView.findViewById(R.id.correct_);
            penIcon = itemView.findViewById(R.id.btnEdit5);
            deleteIcon = itemView.findViewById(R.id.btnDelete5);

            //open dialog to edit quiz number
            penIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Week_Quiz_AddDialog week_quiz_addDialog = new Week_Quiz_AddDialog(context);
                    week_quiz_addDialog.showDialogWithDetails(ques, a1, a2, a3, a4, corr, "Update Quiz");

                    week_quiz_addDialog.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            week_quiz_addDialog.updateFirebaseDetails(getBatchPreNumber, getSemesterPreNumber, getSubjectNumber, getWeekNumber, getQuizNumber, preVAL);
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
                            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(getBatchPreNumber).child("batch_Sem").child(getSemesterPreNumber).child("semester_subject").child(getSubjectNumber).child("weeks").child(getWeekNumber).child("week_quiz").child(getQuizNumber).child("quizzes");

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
