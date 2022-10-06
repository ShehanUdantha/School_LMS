package com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials;

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

import com.firstprog.universityitschool.Adapters.Selected_Subject_Adapter;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData.Materials_Week_View_Fragment;
import com.firstprog.universityitschool.Interfaces.BackPressedListener;
import com.firstprog.universityitschool.Interfaces.RecyclerViewInterface;
import com.firstprog.universityitschool.Model.WeekModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.firstprog.universityitschool.Utility.WeekAddDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Material_Selected_Subject_Fragment extends Fragment implements BackPressedListener, RecyclerViewInterface {

    private Context context;
    private ArrayList<WeekModel> list, filterList;
    private String batchPreNumber, semesterPreNumber, subjectPreKey, subjectKey;
    public static BackPressedListener backListener;
    private DatabaseReference databaseReference;
    private SearchView searchBar;
    private RecyclerView recyclerView;
    private ImageView btnAdd;
    private TextView weeTextView;
    private Selected_Subject_Adapter material_selected_subject_adapter;
    private WeekAddDialog weekAddDialog;

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
            semesterPreNumber = bundle.getString("SemesterPreKey");
            batchPreNumber = bundle.getString("BatchPreKey");
            subjectPreKey = bundle.getString("SubjectPreKey");
            subjectKey = bundle.getString("SubjectKey");

            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(batchPreNumber).child("batch_Sem").child(semesterPreNumber).child("semester_subject").child(subjectPreKey).child("weeks");
            list = new ArrayList<>();

            weekAddDialog = new WeekAddDialog(context);

            weeTextView = view.findViewById(R.id.defaultText);
            weeTextView.setText("Weeks");

            // Create RecyclerView
            recyclerView = view.findViewById(R.id.defaultRecView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            //arraylist set to the recycle view list
            material_selected_subject_adapter = new Selected_Subject_Adapter(context, list, batchPreNumber, semesterPreNumber, subjectPreKey, true, this);
            recyclerView.setAdapter(material_selected_subject_adapter);

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
                    weekAddDialog.showDialog("Add New Week");

                    weekAddDialog.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            weekAddDialog.uploadToFirebase(batchPreNumber, semesterPreNumber, subjectKey);
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
                        WeekModel weekM = dataSnapshots.getValue(WeekModel.class);
                        list.add(weekM);
                    }
                    material_selected_subject_adapter.notifyDataSetChanged();
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
        for (WeekModel weekModel : list) {
            if (weekModel.getWeekID().contains(newText)) {
                filterList.add(weekModel);
            }
        }

        if (filterList.isEmpty()) {
            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
        } else {
            material_selected_subject_adapter.setFilteredList(filterList);
        }
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
        bundle.putString("SemesterPreKey",semesterPreNumber);
        bundle.putString("BatchPreKey",batchPreNumber);

        Material_Subject_Fragment material_subject_fragment = new Material_Subject_Fragment();
        material_subject_fragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayer, material_subject_fragment);
        ft.commit();

    }

    @Override
    public void onItemClick(int Position, String value, String preValue) {

        //send selected item batch number to new fragment
        Bundle bundle = new Bundle();
        bundle.putString("SemesterPreKey",semesterPreNumber);
        bundle.putString("BatchPreKey",batchPreNumber);
        bundle.putString("SubjectPreKey",subjectPreKey);
        bundle.putString("WeekPreKey",preValue);
        bundle.putString("WeekKey",value);

        Materials_Week_View_Fragment materials_week_view_fragment = new Materials_Week_View_Fragment();
        materials_week_view_fragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayer, materials_week_view_fragment);
        ft.commit();

    }
}
