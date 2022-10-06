package com.firstprog.universityitschool.Utility;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventsAddDialog {

    private Context context;
    private Dialog subDialog;
    private TextView itemTitles, pickDate, pickTime;
    private EditText eTitle, eUrl, eDescription;
    private String priority, getETitle, getEUrl, getPickDate, getPickTime, getDescription, date, currentTime;
    private ImageView redShape_, greenShape_, yellowShape_;
    Button submit;
    private LoadingDialog loading_Dialog, loading_Dialog2;
    private DatabaseReference databaseReference, newDatabaseReference, pushedPostRef;
    private FirebaseAuth firebaseAuth;
    private DatePickerDialog datePickerDialog;
    private int hour, minute;
    private TimePickerDialog timePickerDialog;

    public EventsAddDialog(Context context) {

        this.context = context;
        currentTime = new SimpleDateFormat("hh.mm aa", Locale.getDefault()).format(new Date());
        date = getTodayDate();
    }

    //display editText view when clicking add button
    public void showDialog(String itemTitle) {
        subDialog = new Dialog(context);
        subDialog.setContentView(R.layout.activity_events_add_dialog);
        subDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        subDialog.setCanceledOnTouchOutside(true);

        itemTitles = subDialog.findViewById(R.id.EventsScreenTitle);
        itemTitles.setText(itemTitle);

        eTitle = subDialog.findViewById(R.id.eTitle);
        eUrl = subDialog.findViewById(R.id.eUrl);

        pickDate = subDialog.findViewById(R.id.pickDate);
        pickDate.setText(date);

        pickTime = subDialog.findViewById(R.id.pickTime);
        pickTime.setText(currentTime);

        eDescription = subDialog.findViewById(R.id.eDescription);

        redShape_ = subDialog.findViewById(R.id.redShape);
        greenShape_ = subDialog.findViewById(R.id.greenShape);
        yellowShape_ = subDialog.findViewById(R.id.yellowShape);

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

        subDialog.create();
        subDialog.show();
    }

    //return button id to week quiz Fragment
    public Button submit() {
        return submit = subDialog.findViewById(R.id.btnSubmitEvent);
    }

    public TextView pickDate() {
        return pickDate = subDialog.findViewById(R.id.pickDate);
    }

    public TextView pickTime() {
        return pickTime = subDialog.findViewById(R.id.pickTime);
    }

    // display today date as a default, when open the event add dialog
    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    // open a dialog to pickup the date
    public void DatePick() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                pickDate.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(context, dateSetListener, year, month, day);
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";
        //default
        return "JAN";
    }

    //pick current time
    public void TimePick() {

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                pickTime.setText(String.format(Locale.getDefault(), "%02d:%02d",hour, minute));
            }
        };

        timePickerDialog = new TimePickerDialog(context, onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    //upload newly added week to the firebase
    public void uploadToFirebase(String weekID, String batchNumber, String semesterNumber, String subjectNumber) {
        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            loading_Dialog = new LoadingDialog(context);

            getETitle = eTitle.getText().toString();
            getEUrl = eUrl.getText().toString();
            getPickDate = pickDate.getText().toString();
            getPickTime = pickTime.getText().toString();
            getDescription = eDescription.getText().toString();

            if (valid(getETitle, getEUrl, getPickDate, getPickTime)) {
                loading_Dialog.showDialog("Please Wait..");

                databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(batchNumber).child("batch_Sem").child(semesterNumber).child("semester_subject").child(subjectNumber).child("weeks");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String allSubID = ds.child("weekID").getValue().toString();

                            if (allSubID.equals(weekID)) {
                                newDatabaseReference = ds.child("week_events").getRef();
                                // Generate a reference to a new location and add some data using push()
                                pushedPostRef = newDatabaseReference.push();
                                // Get the unique ID generated by a push()
                                String postId = pushedPostRef.getKey();

                                newDatabaseReference.child(postId).child("eventMainChildID").setValue(postId);
                                newDatabaseReference.child(postId).child("eventTitle").setValue(getETitle);
                                newDatabaseReference.child(postId).child("eventUrl").setValue(getEUrl);
                                newDatabaseReference.child(postId).child("eventPriority").setValue(priority);
                                newDatabaseReference.child(postId).child("eventDate").setValue(getPickDate);
                                newDatabaseReference.child(postId).child("eventTime").setValue(getPickTime);
                                newDatabaseReference.child(postId).child("eventDescription").setValue(getDescription);

                                eTitle.setText(" ");
                                eDescription.setText(" ");
                                eUrl.setText(" ");

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
    public void showDialogWithDetails(String itemTitle, String title, String url, String priorities, String date, String time, String Description) {
        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            subDialog = new Dialog(context);
            subDialog.setContentView(R.layout.activity_events_add_dialog);
            subDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            subDialog.setCanceledOnTouchOutside(true);

            itemTitles = subDialog.findViewById(R.id.EventsScreenTitle);
            itemTitles.setText(itemTitle);

            //set batch number in preview
            eTitle = subDialog.findViewById(R.id.eTitle);
            eTitle.setText(title);
            eUrl = subDialog.findViewById(R.id.eUrl);
            eUrl.setText(url);
            pickDate = subDialog.findViewById(R.id.pickDate);
            pickDate.setText(date);
            pickTime = subDialog.findViewById(R.id.pickTime);
            pickTime.setText(time);
            eDescription = subDialog.findViewById(R.id.eDescription);
            eDescription.setText(Description);

            redShape_ = subDialog.findViewById(R.id.redShape);
            greenShape_ = subDialog.findViewById(R.id.greenShape);
            yellowShape_ = subDialog.findViewById(R.id.yellowShape);

            if (priorities.equals("1")) {
                greenShape_.setImageResource(R.drawable.ic_baseline_done_24);
                redShape_.setImageResource(0);
                yellowShape_.setImageResource(0);

            } else if (priorities.equals("2")) {
                yellowShape_.setImageResource(R.drawable.ic_baseline_done_24);
                redShape_.setImageResource(0);
                greenShape_.setImageResource(0);

            } else if (priorities.equals("3")) {
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

            subDialog.create();
            subDialog.show();
        }
    }

    //update firebase quiz data when clicking edit button
    public void updateFirebaseDetails(String getBatchPreNumber, String getSemesterPreNumber, String getSubjectPreNumber, String weekPreKey, String pVal) {

        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            loading_Dialog2 = new LoadingDialog(context);

            getETitle = eTitle.getText().toString();
            getEUrl = eUrl.getText().toString();
            getPickDate = pickDate.getText().toString();
            getPickTime = pickTime.getText().toString();
            getDescription = eDescription.getText().toString();

            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(getBatchPreNumber).child("batch_Sem").child(getSemesterPreNumber).child("semester_subject").child(getSubjectPreNumber).child("weeks").child(weekPreKey).child("week_events");

            if (valid(getETitle, getEUrl, getPickDate, getPickTime)) {
                loading_Dialog2.showDialog("Please Wait..");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String allPreVAL = ds.child("eventMainChildID").getValue().toString();

                            if (allPreVAL.equals(pVal)) {
                                ds.child("eventTitle").getRef().setValue(getETitle).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        subDialog.dismiss();
                                        loading_Dialog2.HideDialog();
                                        Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                ds.child("eventUrl").getRef().setValue(getEUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        subDialog.dismiss();
                                        loading_Dialog2.HideDialog();
                                        Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                ds.child("eventPriority").getRef().setValue(priority).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        subDialog.dismiss();
                                        loading_Dialog2.HideDialog();
                                        Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                ds.child("eventDate").getRef().setValue(getPickDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        subDialog.dismiss();
                                        loading_Dialog2.HideDialog();
                                        Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                ds.child("eventTime").getRef().setValue(getPickTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        subDialog.dismiss();
                                        loading_Dialog2.HideDialog();
                                        Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                ds.child("eventDescription").getRef().setValue(getDescription).addOnCompleteListener(new OnCompleteListener<Void>() {
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


    private boolean valid(String getETitle, String getEUrl, String getPickDate, String getPickTime) {
        if (getETitle.isEmpty()) {
            Toast.makeText(context, "Title Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (getEUrl.isEmpty()) {
            Toast.makeText(context, "Meeting Url Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (getPickDate.isEmpty()) {
            Toast.makeText(context, "Event date Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (getPickTime.isEmpty()) {
            Toast.makeText(context, "Event time Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


}
