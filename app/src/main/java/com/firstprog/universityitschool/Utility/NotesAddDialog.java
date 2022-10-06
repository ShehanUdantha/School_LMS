package com.firstprog.universityitschool.Utility;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotesAddDialog {

    private Context context;
    private Dialog notesDialog;
    private TextView NoteScreenTitle;
    private ImageView redShape_, greenShape_, yellowShape_;
    private EditText noteTitle, noteSubtitle, note;
    Button submit;
    private String priority, getTitle, getSubtitle, getNote;
    private LoadingDialog loading_Dialog, loading_Dialog2;
    private DatabaseReference databaseReference, newDatabaseReference, pushedPostRef;
    private FirebaseAuth firebaseAuth;


    public NotesAddDialog(Context context) {
        this.context = context;
    }

    //display editText view when clicking add button
    public void showDialog() {
        notesDialog = new Dialog(context);
        notesDialog.setContentView(R.layout.notes_add_dialog);
        notesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        notesDialog.setCanceledOnTouchOutside(true);

        noteTitle = notesDialog.findViewById(R.id.nTitle);
        noteSubtitle = notesDialog.findViewById(R.id.nSubtitle);
        note = notesDialog.findViewById(R.id.nNotes);

        redShape_ = notesDialog.findViewById(R.id.redShape);
        greenShape_ = notesDialog.findViewById(R.id.greenShape);
        yellowShape_ = notesDialog.findViewById(R.id.yellowShape);

        greenShape_.setImageResource(R.drawable.ic_baseline_done_24);
        priority = "1";

        greenShape_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                greenShape_.setImageResource(R.drawable.ic_baseline_done_24);
                redShape_.setImageResource(0);
                yellowShape_.setImageResource(0);
                priority = "1";
            }
        });

        yellowShape_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yellowShape_.setImageResource(R.drawable.ic_baseline_done_24);
                greenShape_.setImageResource(0);
                redShape_.setImageResource(0);
                priority = "2";
            }
        });

        redShape_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redShape_.setImageResource(R.drawable.ic_baseline_done_24);
                greenShape_.setImageResource(0);
                yellowShape_.setImageResource(0);
                priority = "3";
            }
        });

        notesDialog.create();
        notesDialog.show();
    }

    //return button id to week notes Fragment
    public Button submit() {
        return submit = notesDialog.findViewById(R.id.btnSubmitNote);
    }

    //upload newly added notes to the firebase
    public void NotesUploadToFirebase(String weekID, String batchNumber, String semesterNumber, String subjectNumber) {
        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            loading_Dialog = new LoadingDialog(context);
            getTitle = noteTitle.getText().toString();
            getSubtitle = noteSubtitle.getText().toString();
            getNote = note.getText().toString();

            if (valid(getTitle, getNote)) {
                loading_Dialog.showDialog("Please Wait..");

                databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(batchNumber).child("batch_Sem").child(semesterNumber).child("semester_subject").child(subjectNumber).child("weeks");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String allSemID = ds.child("weekID").getValue().toString();

                            if (allSemID.equals(weekID)) {
                                newDatabaseReference = ds.child("week_notes").getRef();
                                // Generate a reference to a new location and add some data using push()
                                pushedPostRef = newDatabaseReference.push();
                                // Get the unique ID generated by a push()
                                String postId = pushedPostRef.getKey();

                                newDatabaseReference.child(postId).child("notesMainChildID").setValue(postId);
                                newDatabaseReference.child(postId).child("notesTitle").setValue(getTitle);
                                newDatabaseReference.child(postId).child("notesSubtitle").setValue(getSubtitle);
                                newDatabaseReference.child(postId).child("notesPriority").setValue(priority);
                                newDatabaseReference.child(postId).child("note").setValue(getNote);

                                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                newDatabaseReference.child(postId).child("noteDate").setValue(date);

                                noteSubtitle.setText(" ");
                                noteTitle.setText(" ");
                                note.setText(" ");
                                greenShape_.setImageResource(R.drawable.ic_baseline_done_24);

                                notesDialog.dismiss();
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
                notesDialog.dismiss();
                loading_Dialog.HideDialog();
            }
        }
    }

    //display editText view with notes details when clicking edit button
    public void showDialogWithDetails(String title, String subtitle, String notes, String priorities) {
        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            notesDialog = new Dialog(context);
            notesDialog.setContentView(R.layout.notes_add_dialog);
            notesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            notesDialog.setCanceledOnTouchOutside(true);

            NoteScreenTitle = notesDialog.findViewById(R.id.NoteScreenTitle);
            NoteScreenTitle.setText("Update Note");

            redShape_ = notesDialog.findViewById(R.id.redShape);
            greenShape_ = notesDialog.findViewById(R.id.greenShape);
            yellowShape_ = notesDialog.findViewById(R.id.yellowShape);

            //set notes details in preview
            noteTitle = notesDialog.findViewById(R.id.nTitle);
            noteTitle.setText(title);
            noteSubtitle = notesDialog.findViewById(R.id.nSubtitle);
            noteSubtitle.setText(subtitle);
            note = notesDialog.findViewById(R.id.nNotes);
            note.setText(notes);

            if(priorities.equals("1")){
                greenShape_.setImageResource(R.drawable.ic_baseline_done_24);
                redShape_.setImageResource(0);
                yellowShape_.setImageResource(0);

            } else if(priorities.equals("2")){
                yellowShape_.setImageResource(R.drawable.ic_baseline_done_24);
                redShape_.setImageResource(0);
                greenShape_.setImageResource(0);

            } else if(priorities.equals("3")){
                redShape_.setImageResource(R.drawable.ic_baseline_done_24);
                greenShape_.setImageResource(0);
                yellowShape_.setImageResource(0);

            }

            priority = priorities;

            greenShape_.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    greenShape_.setImageResource(R.drawable.ic_baseline_done_24);
                    redShape_.setImageResource(0);
                    yellowShape_.setImageResource(0);
                    priority = "1";
                }
            });

            yellowShape_.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    yellowShape_.setImageResource(R.drawable.ic_baseline_done_24);
                    greenShape_.setImageResource(0);
                    redShape_.setImageResource(0);
                    priority = "2";
                }
            });

            redShape_.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    redShape_.setImageResource(R.drawable.ic_baseline_done_24);
                    greenShape_.setImageResource(0);
                    yellowShape_.setImageResource(0);
                    priority = "3";
                }
            });


            notesDialog.create();
            notesDialog.show();
        }
    }


    //update firebase notes data when clicking edit button
    public void updateFirebaseDetails(String getBatchPreNumber, String getSemesterPreNumber, String getSubjectPreNumber, String getWeekPreNumber, String pVal) {

        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            loading_Dialog2 = new LoadingDialog(context);

            getTitle = noteTitle.getText().toString();
            getSubtitle = noteSubtitle.getText().toString();
            getNote = note.getText().toString();

            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(getBatchPreNumber).child("batch_Sem").child(getSemesterPreNumber).child("semester_subject").child(getSubjectPreNumber).child("weeks").child(getWeekPreNumber).child("week_notes");

            if (valid(getTitle, getNote)) {
                loading_Dialog2.showDialog("Please Wait..");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String allPreVAL = ds.child("notesMainChildID").getValue().toString();

                            if (allPreVAL.equals(pVal)) {
                                ds.child("notesTitle").getRef().setValue(getTitle).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        notesDialog.dismiss();
                                        loading_Dialog2.HideDialog();
                                        Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                ds.child("notesSubtitle").getRef().setValue(getSubtitle).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        notesDialog.dismiss();
                                        loading_Dialog2.HideDialog();
                                        Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                ds.child("note").getRef().setValue(getNote).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        notesDialog.dismiss();
                                        loading_Dialog2.HideDialog();
                                        Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                ds.child("notesPriority").getRef().setValue(priority).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        notesDialog.dismiss();
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
                notesDialog.dismiss();
                loading_Dialog2.HideDialog();
            }
        }

    }

    private boolean valid(String Title, String Note) {
        if (Title.isEmpty()) {
            Toast.makeText(context, "Title Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Note.isEmpty()) {
            Toast.makeText(context, "Note Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
