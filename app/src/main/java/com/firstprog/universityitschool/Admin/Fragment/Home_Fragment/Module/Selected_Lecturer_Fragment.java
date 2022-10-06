package com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Module;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firstprog.universityitschool.Adapters.Selected_Lecturers_Adapter;
import com.firstprog.universityitschool.Interfaces.BackPressedListener;
import com.firstprog.universityitschool.Model.LecturerModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.firstprog.universityitschool.Utility.LoadingDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Selected_Lecturer_Fragment extends Fragment implements BackPressedListener {

    private Context context;
    private static Context context2;
    private String semesterNumber, batchNumber, SubjectKey, subMainChildID, subBatchID, subSemesterID, lecIndex;
    private static String batchPreNumber, semesterPreNumber, SubjectPreKey;
    private TextView slTextDB;
    private ArrayList<LecturerModel> list;
    private DatabaseReference databaseReference, databaseReference2, databaseReference3, databaseReference4, databaseReference5, newDatabaseReference, pushedPostRef;
    private static DatabaseReference databaseReference6, databaseReference7;
    private RecyclerView recyclerView;
    private static Selected_Lecturers_Adapter selected_lecturers_adapter;
    private AutoCompleteTextView SelectedLecturers;
    private Button btnAdd;
    private LoadingDialog loading_Dialog;
    public static BackPressedListener backListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_module_lecturer_selection, container, false);

        context = view.getContext();
        context2 = view.getContext();

        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            Bundle bundle = this.getArguments();
            semesterNumber = bundle.getString("SemesterKey");
            semesterPreNumber = bundle.getString("SemesterPreKey");
            batchPreNumber = bundle.getString("BatchPreKey");
            batchNumber = bundle.getString("BatchKey");
            SubjectKey = bundle.getString("SubjectKey");
            SubjectPreKey = bundle.getString("SubjectPreKey");

            slTextDB = view.findViewById(R.id.slTextDB);
            slTextDB.setText(SubjectKey);

            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("users");
            databaseReference2 = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(batchPreNumber).child("batch_Sem").child(semesterPreNumber).child("semester_subject");
            databaseReference3 = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(batchPreNumber).child("batch_Sem").child(semesterPreNumber).child("semester_subject").child(SubjectPreKey).child("selected_lecturers");
            databaseReference4 = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("lecturers");

            list = new ArrayList<>();

            loading_Dialog = new LoadingDialog(context);

            // Create RecyclerView
            recyclerView = view.findViewById(R.id.selectedLecturerUpdateView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            //arraylist set to the recycle view list
            selected_lecturers_adapter = new Selected_Lecturers_Adapter(context, list);
            recyclerView.setAdapter(selected_lecturers_adapter);

            //get list of all lecturers index no and display it in dropdown menu
            ArrayList<String> lecList = new ArrayList<>();

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        if (ds.child("role").getValue(String.class).equals("lecturer")) {
                            String lecturers = ds.child("indexNo").getValue(String.class);
                            lecList.add(lecturers);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            databaseReference.addListenerForSingleValueEvent(valueEventListener);

            ArrayAdapter<String> lecAdapter = new ArrayAdapter<>(context, R.layout.drop_down_items, lecList);

            SelectedLecturers = view.findViewById(R.id.slSpinner);
            SelectedLecturers.setAdapter(lecAdapter);


            //lecturer add button implement
            btnAdd = view.findViewById(R.id.btnAdd3);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //check internet connection
                    if (!CheckNetwork.isConnected(context)) {
                        CheckNetwork.showNetworkDialog(context);
                    } else {
                        lecIndex = SelectedLecturers.getText().toString();

                        if (valid(lecIndex)) {
                            loading_Dialog.showDialog("Please Wait..");

                            databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    for (DataSnapshot dataSnap : snapshot.getChildren()) {
                                        String allSubID = dataSnap.child("subID").getValue().toString();

                                        if (allSubID.equals(SubjectKey)) {

                                            newDatabaseReference = dataSnap.child("selected_lecturers").getRef();
                                            // Generate a reference to a new location and add some data using push()
                                            pushedPostRef = newDatabaseReference.push();
                                            // Get the unique ID generated by a push()
                                            String postId = pushedPostRef.getKey();

                                            subMainChildID = dataSnap.child("subMainChildID").getValue(String.class);
                                            subBatchID = dataSnap.child("bID").getValue(String.class);
                                            subSemesterID = dataSnap.child("sID").getValue(String.class);

                                            newDatabaseReference.child(postId).child("selected_lecturersMainChildID").setValue(postId);

                                            // Get lecturer details from selected lecturer index number
                                            ValueEventListener valueEventListener2 = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                                        if (ds.child("role").getValue(String.class).equals("lecturer")) {
                                                            if (ds.child("indexNo").getValue(String.class).equals(lecIndex)) {

                                                                newDatabaseReference.child(postId).child("selected_lecturersID").setValue(ds.child("uid").getValue(String.class));
                                                                newDatabaseReference.child(postId).child("selected_subjectsID").setValue(subMainChildID);
                                                                newDatabaseReference.child(postId).child("selected_lecturersName").setValue(ds.child("firstName").getValue(String.class) + " " + ds.child("lastName").getValue(String.class));
                                                                newDatabaseReference.child(postId).child("selected_lecturersEmail").setValue(ds.child("email").getValue(String.class));
                                                                newDatabaseReference.child(postId).child("selected_lecturersIndexNo").setValue(ds.child("indexNo").getValue(String.class));
                                                                newDatabaseReference.child(postId).child("selected_lecturersPic").setValue(ds.child("img").getValue(String.class));

                                                                // store values in new path called lecturers
                                                                databaseReference4.child(ds.child("uid").getValue(String.class)).child("selected_lecturersID").setValue(ds.child("uid").getValue(String.class));

                                                                databaseReference5 = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("lecturers").child(ds.child("uid").getValue(String.class)).child("selected_subject");
                                                                databaseReference5.child(subMainChildID).child("selected_subjectsID").setValue(subMainChildID);
                                                                databaseReference5.child(subMainChildID).child("selected_subjectsBatchID").setValue(subBatchID);
                                                                databaseReference5.child(subMainChildID).child("selected_subjectsSemesterID").setValue(subSemesterID);

                                                                loading_Dialog.HideDialog();
                                                                Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            };
                                            databaseReference.addListenerForSingleValueEvent(valueEventListener2);

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    throw error.toException();
                                }
                            });
                        }
                    }
                }

            });

            //add firebase data models to arraylist and display on recycle view
            databaseReference3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                        LecturerModel lecUserM = dataSnapshots.getValue(LecturerModel.class);
                        list.add(lecUserM);
                    }
                    selected_lecturers_adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return view;
    }

    // button for delete selected lecturers
    public static void btnDelete(String preVAL, String lecPreVal, String preSubVal) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context2);
        builder.setTitle("Are you sure?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                databaseReference6 = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(batchPreNumber).child("batch_Sem").child(semesterPreNumber).child("semester_subject").child(SubjectPreKey).child("selected_lecturers");

                databaseReference6.child(preVAL).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseReference == null) {
                            Toast.makeText(context2, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context2, "Delete Successful", Toast.LENGTH_SHORT).show();
                            selected_lecturers_adapter.notifyDataSetChanged();
                        }
                    }
                });

                databaseReference7 = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("lecturers").child(lecPreVal).child("selected_subject");

                databaseReference7.child(preSubVal).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseReference == null) {
                            Toast.makeText(context2, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
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

    private boolean valid(String lecIndex) {
        if (lecIndex.isEmpty()) {
            Toast.makeText(context, "Selection Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        backListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        backListener = this;
    }

    @Override
    public void onBackPressed() {

        Bundle bundle = new Bundle();
        bundle.putString("SemesterKey", semesterNumber);
        bundle.putString("SemesterPreKey", semesterPreNumber);
        bundle.putString("BatchPreKey", batchPreNumber);
        bundle.putString("BatchKey", batchNumber);

        Subjects_Fragment subjects_fragment = new Subjects_Fragment();
        subjects_fragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayer, subjects_fragment);
        ft.commit();
    }
}
