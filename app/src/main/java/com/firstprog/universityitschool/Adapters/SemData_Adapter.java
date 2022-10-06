package com.firstprog.universityitschool.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firstprog.universityitschool.Interfaces.RecyclerViewInterface;
import com.firstprog.universityitschool.Model.SemesterModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.SemAddDialog;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SemData_Adapter extends RecyclerView.Adapter<SemData_Adapter.SemDataViewHolder> implements RecyclerViewInterface {

    private Context context;
    private ArrayList<SemesterModel> semList;
    private DatabaseReference databaseReference;
    private RecyclerViewInterface recyclerViewInterface;
    private String getBatchPreNumber;
    private boolean isTrue;

    public SemData_Adapter(Context context, ArrayList<SemesterModel> semList, String getBatchPreNumber, boolean isTrue, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.semList = semList;
        this.recyclerViewInterface = recyclerViewInterface;
        this.getBatchPreNumber = getBatchPreNumber;
        this.isTrue = isTrue;
    }

    public void setFilteredList(ArrayList<SemesterModel> filterList) {
        this.semList = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SemData_Adapter.SemDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_small_card_view, parent, false);

        return new SemData_Adapter.SemDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SemData_Adapter.SemDataViewHolder holder, int position) {

        holder.itemName.setText("Semester:");

        SemesterModel semesterModel = semList.get(position);
        holder.Semester.setText(semesterModel.getSemID());

        holder.semSwitch.setChecked(semesterModel.isCurrent());

        if(isTrue != true){
            holder.penIcon.setVisibility(View.GONE);
            holder.semSwitch.setVisibility(View.GONE);
            holder.deleteIcon.setVisibility(View.GONE);
        }

        //for display details with editText when press edit icon
        holder.semNumber = semesterModel.getSemID();
        holder.preVAL = semesterModel.getSemMainChildID();

    }

    @Override
    public int getItemCount() {
        return semList.size();
    }

    @Override
    public void onItemClick(int Position, String value, String preValue) {

    }


    public class SemDataViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, Semester;
        ImageView penIcon, deleteIcon;
        String semNumber, preVAL;
        Switch semSwitch;

        public SemDataViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            Semester = itemView.findViewById(R.id.viewItem);
            penIcon = itemView.findViewById(R.id.btnEdit2);
            deleteIcon = itemView.findViewById(R.id.btnDelete2);
            semSwitch = itemView.findViewById(R.id.semSwitch);

            //get item clicked position
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos, semNumber, preVAL);
                        }
                    }
                }
            });

            //semester switch setup
            semSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SemAddDialog semAddDialogs = new SemAddDialog(context);

                    if (buttonView.isPressed()) {
                        semAddDialogs.updateSelectedSemester(getBatchPreNumber, preVAL, isChecked);
                    } else {}
                }
            });

            //open dialog to edit semester number
            penIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SemAddDialog semAddDialog_ = new SemAddDialog(context);
                    semAddDialog_.showDialogWithDetails(semNumber, "Update Semester");

                    semAddDialog_.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            semAddDialog_.updateFirebaseDetails(getBatchPreNumber, preVAL);
                        }
                    });
                }
            });

            //open action dialog to delete semester details
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(getBatchPreNumber).child("batch_Sem");

                            databaseReference.child(preVAL).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseReference == null) {
                                        Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Delete Successful", Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }
    }
}
