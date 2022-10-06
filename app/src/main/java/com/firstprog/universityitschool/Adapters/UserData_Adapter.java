package com.firstprog.universityitschool.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firstprog.universityitschool.Model.UserModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.UserAddDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserData_Adapter extends RecyclerView.Adapter<UserData_Adapter.UserDataViewHolder> {

    private Context context;
    private ArrayList<UserModel> userList;
    private DatabaseReference databaseReference;

    public UserData_Adapter(Context context, ArrayList<UserModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    public void setFilteredList(ArrayList<UserModel> filterList) {
        this.userList = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserData_Adapter.UserDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_userdata_view, parent, false);

        return new UserDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserData_Adapter.UserDataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UserModel user = userList.get(position);

        holder.FullName.setText(user.firstName + " " + user.lastName);
        holder.Email.setText(user.email);
        holder.Index.setText(user.indexNo);
        holder.Batch.setText(user.batch);
        holder.Role.setText(user.role);
        Glide.with(context)
                .asBitmap()
                .load(user.img)
                .into(holder.prof);

        //for display details with editText when press edit icon
        holder.fName = user.firstName;
        holder.lName = user.lastName;
        holder.email = user.email;
        holder.role = user.role;
        holder.indexN = user.indexNo;
        holder.batch = user.batch;
        holder.img = user.img;
        holder.userid = user.uid;

        if (userList.get(position).isExpanded()) {
            TransitionManager.beginDelayedTransition(holder.parent);
            holder.expandedRelativeLayout.setVisibility(View.VISIBLE);
            holder.downArrow.setVisibility(View.GONE);
            holder.deleteIcon.setVisibility(View.VISIBLE);
            holder.penIcon.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserDataViewHolder extends RecyclerView.ViewHolder {
        TextView FullName, Role, Email, Index, Batch;
        ImageView prof, penIcon, deleteIcon, upArrow, downArrow;
        CardView parent;
        RelativeLayout expandedRelativeLayout;
        String userid, fName, lName, email, role, batch, indexN, img;


        public UserDataViewHolder(@NonNull View itemView) {
            super(itemView);

            FullName = itemView.findViewById(R.id.viewDBName);
            Role = itemView.findViewById(R.id.viewDBRole);
            Email = itemView.findViewById(R.id.viewDBEmail);
            Index = itemView.findViewById(R.id.viewDBIndex);
            Batch = itemView.findViewById(R.id.viewDBBatch);
            prof = itemView.findViewById(R.id.viewProfilePicture);
            penIcon = itemView.findViewById(R.id.btnEdit);
            deleteIcon = itemView.findViewById(R.id.btnDelete);
            upArrow = itemView.findViewById(R.id.btnUpArrow);
            downArrow = itemView.findViewById(R.id.btnDownArrow);
            parent = itemView.findViewById(R.id.parent);
            expandedRelativeLayout = itemView.findViewById(R.id.expanded);

            //card expanded and close buttons
            downArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserModel um = new UserModel();
                    um = userList.get(getAdapterPosition());
                    um.setExpanded(!um.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            upArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserModel umm = new UserModel();
                    umm = userList.get(getAdapterPosition());
                    umm.setExpanded(!umm.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            //open dialog to edit user details
            penIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserAddDialog userAddDialog = new UserAddDialog(context);
                    userAddDialog.showDialogWithDetails(fName, lName, email, role, batch, indexN, img);

                    userAddDialog.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userAddDialog.updateFirebaseUserDetails(userid);
                        }
                    });
                }
            });

            //open action dialog to delete user details
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure?");
                    builder.setMessage("Deleting this account will result in completely removing this account details from the system!");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("users");

                            databaseReference.child(userid).removeValue(new DatabaseReference.CompletionListener() {
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
