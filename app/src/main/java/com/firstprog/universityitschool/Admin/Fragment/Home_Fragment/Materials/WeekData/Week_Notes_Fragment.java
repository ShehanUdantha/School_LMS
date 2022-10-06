package com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firstprog.universityitschool.Adapters.Week_Notes_Adapter;
import com.firstprog.universityitschool.Interfaces.BackPressedListener;
import com.firstprog.universityitschool.Model.NotesModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.firstprog.universityitschool.Utility.NotesAddDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Week_Notes_Fragment extends Fragment implements BackPressedListener {

    private Context context;
    private String batchPreNumber, semesterPreNumber, subjectPreKey, weekPreKey, weekKey;
    private ImageView btnAdd;
    private TextView mText;
    private ArrayList<NotesModel> list, filterList;
    private NotesAddDialog notesAddDialog;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private SearchView searchBar;
    private Week_Notes_Adapter week_notes_adapter;
    public static BackPressedListener backListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.default_screen_with_searchbar, container, false);

        context = view.getContext();

        mText = view.findViewById(R.id.defaultText);
        mText.setText("notes");

        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            Bundle bundle = this.getArguments();
            semesterPreNumber = bundle.getString("SemesterPreKey");
            batchPreNumber = bundle.getString("BatchPreKey");
            subjectPreKey = bundle.getString("SubjectPreKey");
            weekPreKey = bundle.getString("WeekPreKey");
            weekKey = bundle.getString("WeekKey");

            notesAddDialog = new NotesAddDialog(context);

            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(batchPreNumber).child("batch_Sem").child(semesterPreNumber).child("semester_subject").child(subjectPreKey).child("weeks").child(weekPreKey).child("week_notes");

            list = new ArrayList<>();

            // Create RecyclerView
            recyclerView = view.findViewById(R.id.defaultRecView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            //arraylist set to the recycle view list
            week_notes_adapter = new Week_Notes_Adapter(context, list, batchPreNumber, semesterPreNumber, subjectPreKey, weekPreKey);
            recyclerView.setAdapter(week_notes_adapter);

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
                    notesAddDialog.showDialog();

                    notesAddDialog.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notesAddDialog.NotesUploadToFirebase(weekKey, batchPreNumber, semesterPreNumber, subjectPreKey);
                        }
                    });
                }
            });
        }

        //add firebase data models to arraylist and display on recycle view
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                    NotesModel notesM = dataSnapshots.getValue(NotesModel.class);
                    list.add(notesM);
                }
                week_notes_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    //The recycle view data filtered by searching notes title
    private void filterText(String newText) {

        filterList = new ArrayList<>();
        for (NotesModel notesModel : list) {
            if(notesModel.getNotesTitle().contains(newText)){
                filterList.add(notesModel);
            }
        }

        if (filterList.isEmpty()) {
            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
        } else {
            week_notes_adapter.setFilteredList(filterList);
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
        bundle.putString("SubjectPreKey",subjectPreKey);
        bundle.putString("WeekPreKey",weekPreKey);
        bundle.putString("WeekKey",weekKey);

        Materials_Week_View_Fragment materials_week_view_fragment = new Materials_Week_View_Fragment();
        materials_week_view_fragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayer, materials_week_view_fragment);
        ft.commit();

    }
}