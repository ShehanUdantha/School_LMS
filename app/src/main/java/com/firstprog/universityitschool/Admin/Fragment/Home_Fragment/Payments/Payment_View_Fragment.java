package com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Payments;

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

import com.firstprog.universityitschool.Adapters.Upload_PDF_Adapter;
import com.firstprog.universityitschool.Interfaces.BackPressedListener;
import com.firstprog.universityitschool.Interfaces.RecyclerViewInterface;
import com.firstprog.universityitschool.Model.PDFModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Payment_View_Fragment extends Fragment implements BackPressedListener, RecyclerViewInterface {

    private Context context;
    private String batchPreNumber, semesterPreNumber, subjectPreKey, weekPreKey, weekKey, fileName;
    private ImageView btnAdd;
    private TextView updateName;
    private RecyclerView recyclerView;
    private ArrayList<PDFModel> list, filterList;
    private Upload_PDF_Adapter upload_pdf_adapter;
    private DatabaseReference databaseReference;
    private SearchView searchBar;
    public static BackPressedListener backListener;

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
            weekPreKey = bundle.getString("WeekPreKey");
            weekKey = bundle.getString("WeekKey");

            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(batchPreNumber).child("batch_Sem").child(semesterPreNumber).child("semester_payments");

            list = new ArrayList<>();

            // Create RecyclerView
            recyclerView = view.findViewById(R.id.defaultRecView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            btnAdd = view.findViewById(R.id.defaultAddButton);
            btnAdd.setVisibility(GONE);

            updateName = view.findViewById(R.id.defaultText);
            updateName.setText("Payment Slips");

            //arraylist set to the recycle view list
            upload_pdf_adapter = new Upload_PDF_Adapter(context, list, batchPreNumber, semesterPreNumber, subjectPreKey, weekPreKey, false, this);
            recyclerView.setAdapter(upload_pdf_adapter);

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


            //add firebase data models to arraylist and display on recycle view
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                        PDFModel pdfM = dataSnapshots.getValue(PDFModel.class);
                        list.add(pdfM);
                    }
                    upload_pdf_adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        return view;
    }

    //The recycle view data filtered by searching notes title
    private void filterText(String newText) {

        filterList = new ArrayList<>();
        for (PDFModel pdfModel : list) {
            if (pdfModel.getPdfName().contains(newText)) {
                filterList.add(pdfModel);
            }
        }

        if (filterList.isEmpty()) {
            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
        } else {
            upload_pdf_adapter.setFilteredList(filterList);
        }
    }

    @Override
    public void onItemClick(int Position, String value, String preValue) {

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
        bundle.putString("BatchPreKey", batchPreNumber);

        Payment_Semester_Fragment semesters_fragment = new Payment_Semester_Fragment();
        semesters_fragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayer, semesters_fragment);
        ft.commit();
    }
}
