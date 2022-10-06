package com.firstprog.universityitschool.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firstprog.universityitschool.Interfaces.RecyclerViewInterface;
import com.firstprog.universityitschool.Model.BatchModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.BatchAddDialog;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BatchData_Adapter extends RecyclerView.Adapter<BatchData_Adapter.BatchDataViewHolder> implements RecyclerViewInterface {

    private Context context;
    private ArrayList<BatchModel> batchList;
    private DatabaseReference databaseReference;
    private RecyclerViewInterface recyclerViewInterface;
    private boolean isTrue;

    public BatchData_Adapter(Context context, ArrayList<BatchModel> batchList, boolean isTrue, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.batchList = batchList;
        this.isTrue = isTrue;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public void setFilteredList(ArrayList<BatchModel> filterList) {
        this.batchList = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BatchData_Adapter.BatchDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_small_card_view, parent, false);

        return new BatchData_Adapter.BatchDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BatchData_Adapter.BatchDataViewHolder holder, int position) {
        holder.itemName.setText("Batch:");

        BatchModel batchModel = batchList.get(position);
        holder.Batch.setText(batchModel.batchID);

        //for display details with editText when press edit icon
        holder.batchNumber = batchModel.batchID;
        holder.preVAL = batchModel.mainChildVal;

        holder.semSwitch.setVisibility(View.GONE);

        if(isTrue != true){
            holder.penIcon.setVisibility(View.GONE);
            holder.deleteIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return batchList.size();
    }

    @Override
    public void onItemClick(int Position, String value, String preValue) {

    }


    public class BatchDataViewHolder extends RecyclerView.ViewHolder {

        TextView itemName, Batch;
        ImageView penIcon, deleteIcon;
        String batchNumber, preVAL;
        Switch semSwitch;

        public BatchDataViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.itemName);
            Batch = itemView.findViewById(R.id.viewItem);
            penIcon = itemView.findViewById(R.id.btnEdit2);
            deleteIcon = itemView.findViewById(R.id.btnDelete2);
            semSwitch = itemView.findViewById(R.id.semSwitch);

            //get item clicked position
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos, batchNumber, preVAL);
                        }
                    }
                }
            });

            //open dialog to edit batch number
            penIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BatchAddDialog batchAddDialog_ = new BatchAddDialog(context);
                    batchAddDialog_.showDialogWithDetails(batchNumber, "Update Batch");

                    batchAddDialog_.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            batchAddDialog_.updateFirebaseDetails(preVAL);
                        }
                    });
                }
            });

            //open action dialog to delete batch details
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch");

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
