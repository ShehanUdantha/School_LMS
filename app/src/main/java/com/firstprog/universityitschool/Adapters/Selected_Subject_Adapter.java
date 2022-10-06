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
import com.firstprog.universityitschool.Model.WeekModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.WeekAddDialog;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Selected_Subject_Adapter extends RecyclerView.Adapter<Selected_Subject_Adapter.Material_Selected_SubjectDataViewHolder> implements RecyclerViewInterface {

    private Context context;
    private ArrayList<WeekModel> weekList;
    private DatabaseReference databaseReference;
    private String getBatchPreNumber, getSemesterPreNumber, getSubjectPreNumber;
    private RecyclerViewInterface recyclerViewInterface;
    private boolean isTrue;

    public Selected_Subject_Adapter(Context context, ArrayList<WeekModel> weekList, String getBatchPreNumber, String getSemesterPreNumber, String getSubjectPreNumber, boolean isTrue, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.weekList = weekList;
        this.getBatchPreNumber = getBatchPreNumber;
        this.getSemesterPreNumber = getSemesterPreNumber;
        this.recyclerViewInterface = recyclerViewInterface;
        this.getSubjectPreNumber = getSubjectPreNumber;
        this.isTrue = isTrue;
    }

    public void setFilteredList(ArrayList<WeekModel> filterList) {
        this.weekList = filterList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public Selected_Subject_Adapter.Material_Selected_SubjectDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_small_card_view, parent, false);

        return new Selected_Subject_Adapter.Material_Selected_SubjectDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Selected_Subject_Adapter.Material_Selected_SubjectDataViewHolder holder, int position) {

        holder.itemName.setText("Week:");

        WeekModel weekModel = weekList.get(position);
        holder.Week.setText(weekModel.getWeekID());

        holder.weekSwitch.setChecked(weekModel.isWeekCurrent());

        if(isTrue != true){
            holder.penIcon.setVisibility(View.GONE);
            holder.deleteIcon.setVisibility(View.GONE);
            holder.weekSwitch.setVisibility(View.GONE);
        }

        //for display details with editText when press edit icon
        holder.weekNumber = weekModel.getWeekID();
        holder.preVAL = weekModel.getWeekMainChildID();
    }

    @Override
    public int getItemCount() {
        return weekList.size();
    }

    @Override
    public void onItemClick(int Position, String value, String preValue) {

    }

    public class Material_Selected_SubjectDataViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, Week;
        ImageView penIcon, deleteIcon;
        String weekNumber, preVAL;
        Switch weekSwitch;

        public Material_Selected_SubjectDataViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.itemName);
            Week = itemView.findViewById(R.id.viewItem);
            penIcon = itemView.findViewById(R.id.btnEdit2);
            deleteIcon = itemView.findViewById(R.id.btnDelete2);
            weekSwitch = itemView.findViewById(R.id.semSwitch);

            //get item clicked position
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos, weekNumber, preVAL);
                        }
                    }
                }
            });

            //semester switch setup
            weekSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    WeekAddDialog weekAddDialog = new WeekAddDialog(context);

                    if (buttonView.isPressed()) {
                        weekAddDialog.updateSelectedWeek(getBatchPreNumber, getSemesterPreNumber, getSubjectPreNumber, preVAL, isChecked);
                    } else {}
                }
            });

            //open dialog to edit week number
            penIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WeekAddDialog weekAddDialog = new WeekAddDialog(context);
                    weekAddDialog.showDialogWithDetails(weekNumber, "Update Week");

                    weekAddDialog.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            weekAddDialog.updateFirebaseDetails(getBatchPreNumber, getSemesterPreNumber, getSubjectPreNumber, preVAL);
                            notifyDataSetChanged();
                        }
                    });
                }
            });

            //open action dialog to delete subject details
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(getBatchPreNumber).child("batch_Sem").child(getSemesterPreNumber).child("semester_subject").child(getSubjectPreNumber).child("weeks");

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
