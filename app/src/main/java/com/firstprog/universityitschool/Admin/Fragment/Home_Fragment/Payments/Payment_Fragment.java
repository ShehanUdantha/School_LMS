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

import com.firstprog.universityitschool.Adapters.BatchData_Adapter;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Fragment_Home;
import com.firstprog.universityitschool.Interfaces.BackPressedListener;
import com.firstprog.universityitschool.Interfaces.RecyclerViewInterface;
import com.firstprog.universityitschool.Model.BatchModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Payment_Fragment extends Fragment implements BackPressedListener, RecyclerViewInterface {

    private Context context;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ArrayList<BatchModel> list, filterList;
    private SearchView searchBar;
    private BatchData_Adapter batchData_adapter;
    private TextView materialsText;
    private ImageView addIcon;
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
            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch");
            list = new ArrayList<>();

            materialsText = view.findViewById(R.id.defaultText);
            materialsText.setText("Batches");

            addIcon = view.findViewById(R.id.defaultAddButton);
            addIcon.setVisibility(GONE);

            // Create RecyclerView
            recyclerView = view.findViewById(R.id.defaultRecView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            //arraylist set to the recycle view list
            batchData_adapter = new BatchData_Adapter(context, list, false, this);
            recyclerView.setAdapter(batchData_adapter);

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
                        BatchModel batchM = dataSnapshots.getValue(BatchModel.class);
                        list.add(batchM);
                    }
                    batchData_adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        return view;
    }

    //The recycle view data filtered by searching batch numbers
    private void filterText(String newText) {

        filterList = new ArrayList<>();
        for (BatchModel batchModel : list) {
            if(batchModel.getBatchID().contains(newText)){
                filterList.add(batchModel);
            }
        }

        if (filterList.isEmpty()) {
            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
        } else {
            batchData_adapter.setFilteredList(filterList);
        }
    }

    //open other fragment for clicked item
    @Override
    public void onItemClick(int Position, String value, String preValue) {

        //send selected item batch number to new fragment
        Bundle bundle = new Bundle();
        bundle.putString("BatchPreKey",preValue);

        Payment_Semester_Fragment semesters_fragment = new Payment_Semester_Fragment();
        semesters_fragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayer, semesters_fragment);
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
        ft.replace(R.id.frameLayer, new Fragment_Home());
        ft.commit();
    }
}
