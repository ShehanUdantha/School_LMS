package com.firstprog.universityitschool.Student.Fragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firstprog.universityitschool.Adapters.Student_Subject_Adapter;
import com.firstprog.universityitschool.Interfaces.BackPressedListener;
import com.firstprog.universityitschool.Interfaces.RecyclerViewInterface;
import com.firstprog.universityitschool.Model.BatchModel;
import com.firstprog.universityitschool.Model.SemesterModel;
import com.firstprog.universityitschool.Model.SubjectModel;
import com.firstprog.universityitschool.Model.UserModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Stu_Home_Fragment extends Fragment implements RecyclerViewInterface {

    private Context context;
    private ShimmerFrameLayout mShimmerViewContainer;
    private RecyclerView studentSubRec;
    private String uid, batch, batchChild, semester;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference, databaseReference2;
    private RecyclerView recyclerView;
    private Student_Subject_Adapter subData_adapter;
    private ArrayList<SubjectModel> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stu_home, container, false);

        context = view.getContext();
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        studentSubRec = view.findViewById(R.id.studentSubRec);

        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            mShimmerViewContainer.startShimmer();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                mShimmerViewContainer.stopShimmer();
                mShimmerViewContainer.setVisibility(View.GONE);
                studentSubRec.setVisibility(View.VISIBLE);
            }, 3000);

            list = new ArrayList<>();

            firebaseAuth = FirebaseAuth.getInstance();
            uid = firebaseAuth.getCurrentUser().getUid();

            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("users");
            getUserData();
            databaseReference2 = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch");
            displaySubjects();
            //mcloudorganization@gmail.com

            // Create RecyclerView
            recyclerView = view.findViewById(R.id.studentSubRec);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            //arraylist set to the recycle view list
            subData_adapter = new Student_Subject_Adapter(context, list,this);
            recyclerView.setAdapter(subData_adapter);
        }

        return view;
    }


    private void displaySubjects() {

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {

                for (DataSnapshot dataSnapshots : snap.getChildren()) {
                    BatchModel batchModel = dataSnapshots.getValue(BatchModel.class);
                    if (batchModel.getBatchID().equalsIgnoreCase(batch)) {
                        batchChild = batchModel.getMainChildVal();
                        Log.d(TAG, "onTestView b " + batchChild);

                        databaseReference2.child(batchChild).child("batch_Sem").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot data_Snapshots : snapshot.getChildren()) {
                                    SemesterModel semesterModel = data_Snapshots.getValue(SemesterModel.class);
                                    if (semesterModel.isCurrent() == true) {
                                        semester = semesterModel.getSemMainChildID();
                                        Log.d(TAG, "onTestView s " + semester);

                                        databaseReference2.child(batchChild).child("batch_Sem").child(semester).child("semester_subject").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snap_shot) {
                                                list.clear();
                                                for (DataSnapshot dataSnapshots : snap_shot.getChildren()) {
                                                    SubjectModel subM = dataSnapshots.getValue(SubjectModel.class);
                                                    list.add(subM);
                                                }
                                                subData_adapter.notifyDataSetChanged();

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserData() {
        //Retrieve data from firebase related to user id
        if (uid != null) {
            databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        batch = userModel.getBatch();
                        Log.d(TAG, "onTestView u " + batch);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmer();
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmer();
        super.onPause();
    }

    @Override
    public void onItemClick(int Position, String value, String preValue) {

    }
}
