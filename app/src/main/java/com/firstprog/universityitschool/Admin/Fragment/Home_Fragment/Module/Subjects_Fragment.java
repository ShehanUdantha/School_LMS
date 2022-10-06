package com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Module;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firstprog.universityitschool.Adapters.SubjectData_Adapter;
import com.firstprog.universityitschool.Interfaces.BackPressedListener;
import com.firstprog.universityitschool.Interfaces.RecyclerViewInterface;
import com.firstprog.universityitschool.Model.SubjectModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.firstprog.universityitschool.Utility.SubjectAddDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Subjects_Fragment extends Fragment implements BackPressedListener, RecyclerViewInterface {

    private Context context;
    private String semesterNumber, semesterPreNumber, batchNumber, batchPreNumber;
    private TextView mUpdateText, mUpdateTextDB;
    private ArrayList<SubjectModel> list, filterList;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ImageView btnAdd;
    private SearchView searchBar;
    private SubjectData_Adapter subData_adapter;
    private SubjectAddDialog subjectAddDialog_;

    public static BackPressedListener backListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.default_screen_with_searchbar, container, false);

        context = view.getContext();

        mUpdateText = view.findViewById(R.id.defaultText);
        mUpdateText.setText("semester");

        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            Bundle bundle = this.getArguments();
            semesterNumber = bundle.getString("SemesterKey");
            semesterPreNumber = bundle.getString("SemesterPreKey");
            batchPreNumber = bundle.getString("BatchPreKey");
            batchNumber = bundle.getString("BatchKey");

            mUpdateTextDB = view.findViewById(R.id.defaultNumberText);
            mUpdateTextDB.setText(semesterNumber);

            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(batchPreNumber).child("batch_Sem").child(semesterPreNumber).child("semester_subject");
            list = new ArrayList<>();

            subjectAddDialog_ = new SubjectAddDialog(context);

            // Create RecyclerView
            recyclerView = view.findViewById(R.id.defaultRecView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            //arraylist set to the recycle view list
            subData_adapter = new SubjectData_Adapter(context, list, batchPreNumber, semesterPreNumber, true, this);
            recyclerView.setAdapter(subData_adapter);

            //search bar implement
            searchBar = view.findViewById(R.id.defaultSearchBar);
            searchBar.clearFocus();
            searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterText(newText);
                    return true;
                }
            });

            //batch add button implement
            btnAdd = view.findViewById(R.id.defaultAddButton);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subjectAddDialog_.showDialog("Add New Subject");

                    subjectAddDialog_.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            subjectAddDialog_.uploadToFirebase(semesterNumber, batchPreNumber, semesterPreNumber);
                        }
                    });
                }
            });

            //add firebase data models to arraylist and display on recycle view
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                        SubjectModel subM = dataSnapshots.getValue(SubjectModel.class);
                        list.add(subM);
                    }
                    subData_adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return view;
    }


    //The recycle view data filtered by searching subject numbers
    private void filterText(String newText) {

        filterList = new ArrayList<>();
        for (SubjectModel subjectModel : list) {
            if(subjectModel.getSubID().contains(newText)){
                filterList.add(subjectModel);
            }
        }

        if (filterList.isEmpty()) {
            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
        } else {
            subData_adapter.setFilteredList(filterList);
        }
    }

    @Override
    public void onItemClick(int Position, String value, String preValue) {
        //send selected item batch number to new fragment
        Bundle bundle = new Bundle();
        bundle.putString("SemesterKey",semesterNumber);
        bundle.putString("SemesterPreKey",semesterPreNumber);
        bundle.putString("BatchPreKey",batchPreNumber);
        bundle.putString("BatchKey",batchNumber);

        bundle.putString("SubjectKey",value);
        bundle.putString("SubjectPreKey",preValue);

        Selected_Lecturer_Fragment selected_lecturer_fragment = new Selected_Lecturer_Fragment();
        selected_lecturer_fragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayer, selected_lecturer_fragment);
        ft.commit();
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
        bundle.putString("BatchKey",batchNumber);
        bundle.putString("BatchPreKey",batchPreNumber);

        Semesters_Fragment semesters_fragment = new Semesters_Fragment();
        semesters_fragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayer, semesters_fragment);
        ft.commit();
    }
}
