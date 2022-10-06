package com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
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
import com.firstprog.universityitschool.Utility.PDFAddDialog;
import com.firstprog.universityitschool.Utility.LoadingDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Week_Lessons_PDF_Fragment extends Fragment implements BackPressedListener, RecyclerViewInterface {

    private Context context;
    private String batchPreNumber, semesterPreNumber, subjectPreKey, weekPreKey, weekKey, fileName;
    private ImageView btnAdd;
    private TextView updateName;
    private RecyclerView recyclerView;
    private ArrayList<PDFModel> list, filterList;
    private Upload_PDF_Adapter upload_pdf_adapter;
    private Uri filePath;
    private DatabaseReference databaseReference;
    private PDFAddDialog pdfAddDialog;
    private SearchView searchBar;
    private final int PICK_PDF_REQUEST = 100;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 100;
    private LoadingDialog loading_Dialog;
    public static BackPressedListener backListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.default_screen_with_searchbar, container, false);

        context = view.getContext();
        loading_Dialog = new LoadingDialog(context);

        updateName = view.findViewById(R.id.defaultText);
        updateName.setText("upload pdf");

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

            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(batchPreNumber).child("batch_Sem").child(semesterPreNumber).child("semester_subject").child(subjectPreKey).child("weeks").child(weekPreKey).child("week_pdf");

            list = new ArrayList<>();

            pdfAddDialog = new PDFAddDialog(context);

            // Create RecyclerView
            recyclerView = view.findViewById(R.id.defaultRecView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            //arraylist set to the recycle view list
            upload_pdf_adapter = new Upload_PDF_Adapter(context, list, batchPreNumber, semesterPreNumber, subjectPreKey, weekPreKey, true, this);
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


            btnAdd = view.findViewById(R.id.defaultAddButton);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pdfAddDialog.showDialog();

                    pdfAddDialog.uploadAreaView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkPermissionForReadExternalStorage();
                        }
                    });

                    pdfAddDialog.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pdfAddDialog.uploadPDFtoFirebase(fileName, filePath, weekKey, batchPreNumber, semesterPreNumber, subjectPreKey, weekPreKey);
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
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF Files"), PICK_PDF_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            if (filePath != null) {
                fileName = getFileName(filePath);
                pdfAddDialog.updateFileName(fileName);
            }
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri data) {
        String result = null;
        if(data.getScheme().equals("content")){
            Cursor cursor = getContext().getContentResolver().query(data,null,null,null, null);
            try {
                if(cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if(result == null){
            result = data.getPath();
            int cut = result.lastIndexOf("/");
            if(cut != -1){
                result = result.substring(cut + 1);
            }
        }

        return result;
    }

    //The recycle view data filtered by searching notes title
    private void filterText(String newText) {

        filterList = new ArrayList<>();
        for (PDFModel pdfModel : list) {
            if(pdfModel.getPdfName().contains(newText)){
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

        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {
            loading_Dialog.showDialog("Please Wait..");

            Bundle bundle = new Bundle();
            bundle.putString("SemesterPreKey", semesterPreNumber);
            bundle.putString("BatchPreKey", batchPreNumber);
            bundle.putString("SubjectPreKey", subjectPreKey);
            bundle.putString("WeekPreKey", weekPreKey);
            bundle.putString("WeekKey", weekKey);
            bundle.putString("PDFUrl", value);
            bundle.putString("Value", "pdf");

            Week_PDF_View week_pdf_view = new Week_PDF_View();
            week_pdf_view.setArguments(bundle);

            loading_Dialog.HideDialog();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayer, week_pdf_view);
            ft.commit();
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

        Week_Lessons_Select_Fragment week_lessons_select_fragment = new Week_Lessons_Select_Fragment();
        week_lessons_select_fragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayer, week_lessons_select_fragment);
        ft.commit();

    }

}
