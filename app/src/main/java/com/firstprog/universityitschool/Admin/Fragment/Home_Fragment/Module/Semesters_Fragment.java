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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firstprog.universityitschool.Adapters.SemData_Adapter;
import com.firstprog.universityitschool.Interfaces.BackPressedListener;
import com.firstprog.universityitschool.Interfaces.RecyclerViewInterface;
import com.firstprog.universityitschool.Model.SemesterModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.firstprog.universityitschool.Utility.SemAddDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Semesters_Fragment extends Fragment implements BackPressedListener, RecyclerViewInterface {

    private Context context;
    private String batchNumber, batchPreNumber;
    private TextView mUpdateText, mUpdateTextDB;
    private ArrayList<SemesterModel> list, filterList;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private SemData_Adapter semData_adapter;
    private ImageView btnAdd;
    private SemAddDialog semAddDialogs;
    private SearchView searchBar;

    public static BackPressedListener backListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.default_screen_with_searchbar, container, false);
        context = view.getContext();

        mUpdateText = view.findViewById(R.id.defaultText);
        mUpdateText.setText("batch");

        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            Bundle bundle = this.getArguments();
            batchNumber = bundle.getString("BatchKey");
            batchPreNumber = bundle.getString("BatchPreKey");

            mUpdateTextDB = view.findViewById(R.id.defaultNumberText);
            mUpdateTextDB.setText(batchNumber);

            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(batchPreNumber).child("batch_Sem");
            list = new ArrayList<>();

            semAddDialogs = new SemAddDialog(context);

            // Create RecyclerView
            recyclerView = view.findViewById(R.id.defaultRecView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            //arraylist set to the recycle view list
            semData_adapter = new SemData_Adapter(context, list, batchPreNumber, true, this);
            recyclerView.setAdapter(semData_adapter);

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
                    semAddDialogs.showDialog("Add New Semester");

                    semAddDialogs.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            semAddDialogs.uploadToFirebase(batchNumber);
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
                        SemesterModel semM = dataSnapshots.getValue(SemesterModel.class);
                        list.add(semM);
                    }
                    semData_adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        return view;
    }

    //The recycle view data filtered by searching semester numbers
    private void filterText(String newText) {

        filterList = new ArrayList<>();
        for (SemesterModel semesterModel : list) {
            if(semesterModel.getSemID().contains(newText)){
                filterList.add(semesterModel);
            }
        }

        if (filterList.isEmpty()) {
            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
        } else {
            semData_adapter.setFilteredList(filterList);
        }
    }


    @Override
    public void onItemClick(int Position, String value, String preValue) {
        //send selected item batch number to new fragment
        Bundle bundle = new Bundle();
        bundle.putString("SemesterKey",value);
        bundle.putString("SemesterPreKey",preValue);
        bundle.putString("BatchPreKey",batchPreNumber);
        bundle.putString("BatchKey",batchNumber);

        Subjects_Fragment subjects_fragment = new Subjects_Fragment();
        subjects_fragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayer, subjects_fragment);
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
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayer, new Modules_Fragment());
        ft.commit();
    }
}
