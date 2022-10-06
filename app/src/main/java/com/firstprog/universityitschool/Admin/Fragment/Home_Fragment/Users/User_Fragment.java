package com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Users;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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

import com.firstprog.universityitschool.Adapters.UserData_Adapter;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Fragment_Home;
import com.firstprog.universityitschool.Interfaces.BackPressedListener;
import com.firstprog.universityitschool.Model.UserModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.firstprog.universityitschool.Utility.UserAddDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class User_Fragment extends Fragment implements BackPressedListener{
    private RecyclerView recyclerView;
    private ArrayList<UserModel> list, filterList;
    private DatabaseReference databaseReference;
    private UserData_Adapter userData_adapter;
    private ImageView btnAdd;
    private TextView mainText;
    private UserAddDialog userAddDialog;
    private final int PICK_IMAGE_REQUEST = 100;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 100;
    private Context context;
    private Uri filePath;
    private SearchView searchBar;
    public static BackPressedListener backListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.default_screen_with_searchbar, container, false);
        context = view.getContext();

        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("users");
            list = new ArrayList<>();

            userAddDialog = new UserAddDialog(context);

            mainText = view.findViewById(R.id.defaultText);
            mainText.setText("Users");

            // Create RecyclerView
            recyclerView = view.findViewById(R.id.defaultRecView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            //arraylist set to the recycle view list
            userData_adapter = new UserData_Adapter(context, list);
            recyclerView.setAdapter(userData_adapter);

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

            //user add button implement
            btnAdd = view.findViewById(R.id.defaultAddButton);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userAddDialog.showDialog();
                    userAddDialog.editImage().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkPermissionForReadExternalStorage();
                        }
                    });

                    userAddDialog.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userAddDialog.uploadToFirebase(filePath);
                        }
                    });
                }
            });


            //add firebase data models to arraylist
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        UserModel userM = dataSnapshot1.getValue(UserModel.class);
                        list.add(userM);
                    }
                    userData_adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return view;
    }


    //The recycle view data filtered by searching email address
    private void filterText(String newText) {

        filterList = new ArrayList<>();
        for (UserModel userEmail : list) {
            if (userEmail.getEmail().toLowerCase().contains(newText.toLowerCase())) {
                filterList.add(userEmail);
            }
        }

        if (filterList.isEmpty()) {
            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
        } else {
            userData_adapter.setFilteredList(filterList);
        }
    }


    //get storage access permissions
    public void checkPermissionForReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                SelectImage();
            } else {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);
            }

        } else {
            SelectImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "permission Granted", Toast.LENGTH_SHORT).show();
                SelectImage();
            } else {
                Toast.makeText(context, "permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Select Image method
    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            if (filePath != null) {
                userAddDialog.updateImage(filePath.toString());
            }
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
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayer, new Fragment_Home());
        ft.commit();
    }
}

