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

import com.firstprog.universityitschool.Adapters.Week_Quiz_View_Adapter;
import com.firstprog.universityitschool.Interfaces.BackPressedListener;
import com.firstprog.universityitschool.Model.QuizAddModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.firstprog.universityitschool.Utility.Week_Quiz_AddDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Week_Quiz_View_Fragment extends Fragment implements BackPressedListener {

    private TextView mText, mNText;
    private Context context;
    private String batchPreNumber, semesterPreNumber, subjectPreKey, weekPreKey, weekKey, QuizPreKey, QuizKey;
    private ImageView btnAdd;
    private Week_Quiz_AddDialog week_quiz_addDialog;
    private DatabaseReference databaseReference;
    private ArrayList<QuizAddModel> list, filterList;
    private SearchView searchBar;
    private RecyclerView recyclerView;
    private Week_Quiz_View_Adapter week_quiz_view_adapter;
    public static BackPressedListener backListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.default_screen_with_searchbar,container,false);

        context = view.getContext();

        week_quiz_addDialog = new Week_Quiz_AddDialog(context);

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
            QuizPreKey = bundle.getString("QuizPreKey");
            QuizKey = bundle.getString("QuizKey");

            mText = view.findViewById(R.id.defaultText);
            mText.setText("Quiz");

            mNText = view.findViewById(R.id.defaultNumberText);
            mNText.setText(QuizKey);

            btnAdd = view.findViewById(R.id.defaultAddButton);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    week_quiz_addDialog.showDialog("Add New Quiz");

                    week_quiz_addDialog.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            week_quiz_addDialog.uploadToFirebase(QuizKey, batchPreNumber, semesterPreNumber, subjectPreKey, weekPreKey);
                        }
                    });
                }
            });

            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(batchPreNumber).child("batch_Sem").child(semesterPreNumber).child("semester_subject").child(subjectPreKey).child("weeks").child(weekPreKey).child("week_quiz").child(QuizPreKey).child("quizzes");

            list = new ArrayList<>();

            // Create RecyclerView
            recyclerView = view.findViewById(R.id.defaultRecView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            //arraylist set to the recycle view list
            week_quiz_view_adapter = new Week_Quiz_View_Adapter(context, list, batchPreNumber, semesterPreNumber, subjectPreKey, weekPreKey, QuizPreKey);
            recyclerView.setAdapter(week_quiz_view_adapter);

            //add firebase data models to arraylist and display on recycle view
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                        QuizAddModel qM = dataSnapshots.getValue(QuizAddModel.class);
                        list.add(qM);
                    }
                    week_quiz_view_adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

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

        }

        return view;
    }

    //The recycle view data filtered by searching semester numbers
    private void filterText(String newText) {

        filterList = new ArrayList<>();
        for (QuizAddModel quizModel : list) {
            if (quizModel.getQuestion().contains(newText)) {
                filterList.add(quizModel);
            }
        }

        if (filterList.isEmpty()) {
            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
        } else {
            week_quiz_view_adapter.setFilteredList(filterList);
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

        Week_Quiz_Fragment week_quiz_fragment = new Week_Quiz_Fragment();
        week_quiz_fragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayer, week_quiz_fragment);
        ft.commit();

    }
}
