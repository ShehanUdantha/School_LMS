package com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.News;

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
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firstprog.universityitschool.Adapters.NewsAdapter;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Fragment_Home;
import com.firstprog.universityitschool.Interfaces.BackPressedListener;
import com.firstprog.universityitschool.Model.NewsModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.firstprog.universityitschool.Utility.NewsAddDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class News_Fragment extends Fragment implements BackPressedListener {

    private Context context;
    private ImageView addIcon;
    private TextView textView;
    private NewsAddDialog newsAddDialog;
    private ArrayList<NewsModel> list, filterList;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private SearchView searchBar;
    private Uri filePath;
    private final int PICK_IMG_REQUEST = 100;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 100;
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
            textView = view.findViewById(R.id.defaultText);
            textView.setText("News");

            newsAddDialog = new NewsAddDialog(context);

            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("news");

            list = new ArrayList<>();

            // Create RecyclerView
            recyclerView = view.findViewById(R.id.defaultRecView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            newsAdapter = new NewsAdapter(context,list);
            recyclerView.setAdapter(newsAdapter);

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

            addIcon = view.findViewById(R.id.defaultAddButton);
            addIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newsAddDialog.showDialog();

                    newsAddDialog.imageSelectionView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkPermissionForReadExternalStorage();
                        }
                    });

                    newsAddDialog.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            newsAddDialog.uploadToFirebase(filePath);
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
                        NewsModel newsM = dataSnapshots.getValue(NewsModel.class);
                        list.add(newsM);
                    }
                    newsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        return view;
    }

    //get storage access permissions
    public void checkPermissionForReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openDocuments();
            } else {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);
            }

        } else {
            openDocuments();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "permission Granted", Toast.LENGTH_SHORT).show();
                openDocuments();
            } else {
                Toast.makeText(context, "permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //select pdf method
    private void openDocuments(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select video Files"), PICK_IMG_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMG_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            if (filePath != null) {
                newsAddDialog.updateImage(filePath.toString());
            }
        }
    }

    //The recycle view data filtered by searching notes title
    private void filterText(String newText) {

        filterList = new ArrayList<>();
        for (NewsModel newsModel : list) {
            if(newsModel.getNewsTitle().contains(newText)){
                filterList.add(newsModel);
            }
        }

        if (filterList.isEmpty()) {
            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
        } else {
            newsAdapter.setFilteredList(filterList);
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
