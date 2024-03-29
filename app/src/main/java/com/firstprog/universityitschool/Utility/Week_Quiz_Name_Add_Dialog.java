package com.firstprog.universityitschool.Utility;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firstprog.universityitschool.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Week_Quiz_Name_Add_Dialog {

    private Context context;
    private Dialog subDialog;
    private TextView itemTitles;
    private EditText Quiz;
    Button submit;
    private LoadingDialog loading_Dialog, loading_Dialog2;
    private DatabaseReference databaseReference, newDatabaseReference, pushedPostRef;
    private FirebaseAuth firebaseAuth;

    public Week_Quiz_Name_Add_Dialog(Context context) {
        this.context = context;
    }

    //display editText view when clicking add button
    public void showDialog(String itemTitle) {
        subDialog = new Dialog(context);
        subDialog.setContentView(R.layout.activity_one_item_add_dialog);
        subDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        subDialog.setCanceledOnTouchOutside(true);

        itemTitles = subDialog.findViewById(R.id.itemTitle);
        itemTitles.setText(itemTitle);

        Quiz = subDialog.findViewById(R.id.addItem);

        subDialog.create();
        subDialog.show();
    }

    //return button id to week quiz Fragment
    public Button submit() {
        return submit = subDialog.findViewById(R.id.btnSubmitItem);
    }

    //upload newly added week to the firebase
    public void uploadToFirebase(String weekID, String batchNumber, String semesterNumber, String subjectNumber) {
        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            loading_Dialog = new LoadingDialog(context);
            String getQuiz = Quiz.getText().toString();

            if (valid(getQuiz)) {
                loading_Dialog.showDialog("Please Wait..");

                databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(batchNumber).child("batch_Sem").child(semesterNumber).child("semester_subject").child(subjectNumber).child("weeks");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String allSubID = ds.child("weekID").getValue().toString();

                            if (allSubID.equals(weekID)) {
                                newDatabaseReference = ds.child("week_quiz").getRef();
                                // Generate a reference to a new location and add some data using push()
                                pushedPostRef = newDatabaseReference.push();
                                // Get the unique ID generated by a push()
                                String postId = pushedPostRef.getKey();

                                newDatabaseReference.child(postId).child("quizMainChildID").setValue(postId);
                                newDatabaseReference.child(postId).child("quizID").setValue(getQuiz);

                                Quiz.setText(" ");
                                subDialog.dismiss();
                                loading_Dialog.HideDialog();

                                Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        throw error.toException();
                    }
                });

                subDialog.dismiss();
                loading_Dialog.HideDialog();
            }
        }
    }

    //display editText view with quiz details when clicking edit button
    public void showDialogWithDetails(String quiz, String itemTitle) {
        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            subDialog = new Dialog(context);
            subDialog.setContentView(R.layout.activity_one_item_add_dialog);
            subDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            subDialog.setCanceledOnTouchOutside(true);

            itemTitles = subDialog.findViewById(R.id.itemTitle);
            itemTitles.setText(itemTitle);

            //set batch number in preview
            Quiz = subDialog.findViewById(R.id.addItem);
            Quiz.setText(quiz);

            subDialog.create();
            subDialog.show();
        }
    }

    //update firebase quiz data when clicking edit button
    public void updateFirebaseDetails(String getBatchPreNumber, String getSemesterPreNumber, String getSubjectPreNumber,  String weekPreKey, String pVal) {

        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            loading_Dialog2 = new LoadingDialog(context);

            String newQuiz = Quiz.getText().toString();

            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(getBatchPreNumber).child("batch_Sem").child(getSemesterPreNumber).child("semester_subject").child(getSubjectPreNumber).child("weeks").child(weekPreKey).child("week_quiz");

            if (valid(newQuiz)) {
                loading_Dialog2.showDialog("Please Wait..");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String allPreVAL = ds.child("quizMainChildID").getValue().toString();

                            if (allPreVAL.equals(pVal)) {
                                ds.child("quizID").getRef().setValue(newQuiz).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        subDialog.dismiss();
                                        loading_Dialog2.HideDialog();
                                        Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        throw error.toException();
                    }
                });
                subDialog.dismiss();
                loading_Dialog2.HideDialog();
            }
        }
    }


    private boolean valid(String week) {
        if (week.isEmpty()) {
            Toast.makeText(context, "Quiz Number Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


}
