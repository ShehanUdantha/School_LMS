package com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Events;

import static android.view.View.GONE;

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

import com.firstprog.universityitschool.Adapters.SemData_Adapter;
import com.firstprog.universityitschool.Interfaces.BackPressedListener;
import com.firstprog.universityitschool.Interfaces.RecyclerViewInterface;
import com.firstprog.universityitschool.Model.SemesterModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Events_Semester_Fragment extends Fragment implements BackPressedListener, RecyclerViewInterface  {

    private Context context;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ArrayList<SemesterModel> list, filterList;
    private SearchView searchBar;
    private SemData_Adapter semesterData_adapter;
    public static BackPressedListener backListener;
    private String batchPreNumber;
    private TextView materialsText;
    private ImageView addIcon;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.default_screen_with_searchbar, container, false);

        context = view.getContext();

        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            Bundle bundle = this.getArguments();
            batchPreNumber = bundle.getString("BatchPreKey");

            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(batchPreNumber).child("batch_Sem");
            list = new ArrayList<>();

            materialsText = view.findViewById(R.id.defaultText);
            materialsText.setText("Semesters");

            addIcon = view.findViewById(R.id.defaultAddButton);
            addIcon.setVisibility(GONE);

            // Create RecyclerView
            recyclerView = view.findViewById(R.id.defaultRecView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            //arraylist set to the recycle view list
            semesterData_adapter = new SemData_Adapter(context, list, batchPreNumber,false, this);
            recyclerView.setAdapter(semesterData_adapter);

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

            //add firebase data models to arraylist
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                        SemesterModel semM = dataSnapshots.getValue(SemesterModel.class);
                        list.add(semM);
                    }
                    semesterData_adapter.notifyDataSetChanged();
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
            semesterData_adapter.setFilteredList(filterList);
        }
    }

    //open other fragment for clicked item
    @Override
    public void onItemClick(int Position, String value, String preValue) {

        Bundle bundle = new Bundle();
        bundle.putString("SemesterPreKey",preValue);
        bundle.putString("BatchPreKey",batchPreNumber);

        Event_Subject_Fragment subjects_fragment = new Event_Subject_Fragment();
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
        ft.replace(R.id.frameLayer, new Events_Fragment());
        ft.commit();
    }

}

