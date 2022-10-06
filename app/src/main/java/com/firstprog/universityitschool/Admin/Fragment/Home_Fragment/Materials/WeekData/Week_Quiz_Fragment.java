package com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData;
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

import com.firstprog.universityitschool.Adapters.Week_Quiz_Name_Adapter;
import com.firstprog.universityitschool.Interfaces.BackPressedListener;
import com.firstprog.universityitschool.Interfaces.RecyclerViewInterface;
import com.firstprog.universityitschool.Model.QuizModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.firstprog.universityitschool.Utility.Week_Quiz_Name_Add_Dialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Week_Quiz_Fragment extends Fragment implements BackPressedListener, RecyclerViewInterface {

    private Context context;
    private String batchPreNumber, semesterPreNumber, subjectPreKey, weekPreKey, weekKey;
    private ImageView btnAdd;
    private TextView updateName;
    private RecyclerView recyclerView;
    private Week_Quiz_Name_Adapter quiz_name_adapter;
    private ArrayList<QuizModel> list, filterList;
    private SearchView searchBar;
    private DatabaseReference databaseReference;
    private Week_Quiz_Name_Add_Dialog week_quiz_name_add_dialog;
    public static BackPressedListener backListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.default_screen_with_searchbar, container, false);

        context = view.getContext();

        updateName = view.findViewById(R.id.defaultText);
        updateName.setText("add quizzes");

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

            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(batchPreNumber).child("batch_Sem").child(semesterPreNumber).child("semester_subject").child(subjectPreKey).child("weeks").child(weekPreKey).child("week_quiz");

            list = new ArrayList<>();
            week_quiz_name_add_dialog = new Week_Quiz_Name_Add_Dialog(context);

            // Create RecyclerView
            recyclerView = view.findViewById(R.id.defaultRecView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            //arraylist set to the recycle view list
            quiz_name_adapter = new Week_Quiz_Name_Adapter(context, list, batchPreNumber, semesterPreNumber, subjectPreKey, weekPreKey,this);
            recyclerView.setAdapter(quiz_name_adapter);

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
                    week_quiz_name_add_dialog.showDialog("Add New Quiz");

                    week_quiz_name_add_dialog.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            week_quiz_name_add_dialog.uploadToFirebase(weekKey, batchPreNumber, semesterPreNumber, subjectPreKey);
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
                        QuizModel qM = dataSnapshots.getValue(QuizModel.class);
                        list.add(qM);
                    }
                    quiz_name_adapter.notifyDataSetChanged();
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
        for (QuizModel quizModel : list) {
            if (quizModel.getQuizID().contains(newText)) {
                filterList.add(quizModel);
            }
        }

        if (filterList.isEmpty()) {
            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
        } else {
            quiz_name_adapter.setFilteredList(filterList);
        }
    }


    @Override
    public void onItemClick(int Position, String value, String preValue) {
        Bundle bundle = new Bundle();
        bundle.putString("SemesterPreKey",semesterPreNumber);
        bundle.putString("BatchPreKey",batchPreNumber);
        bundle.putString("SubjectPreKey",subjectPreKey);
        bundle.putString("WeekPreKey",weekPreKey);
        bundle.putString("WeekKey",weekKey);
        bundle.putString("QuizPreKey",preValue);
        bundle.putString("QuizKey",value);

        Week_Quiz_View_Fragment week_quiz_view_fragment = new Week_Quiz_View_Fragment();
        week_quiz_view_fragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayer, week_quiz_view_fragment);
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
