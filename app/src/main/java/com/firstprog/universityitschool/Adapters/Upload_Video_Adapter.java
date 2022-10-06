package com.firstprog.universityitschool.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firstprog.universityitschool.Interfaces.RecyclerViewInterface;
import com.firstprog.universityitschool.Model.VideoModel;
import com.firstprog.universityitschool.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class Upload_Video_Adapter extends RecyclerView.Adapter<Upload_Video_Adapter.ViewHolder> implements RecyclerViewInterface {

    private Context context;
    private ArrayList<VideoModel> vList;
    private DatabaseReference databaseReference;
    private String getBatchPreNumber, getSemesterPreNumber, getSubjectNumber, getWeekNumber;
    private RecyclerViewInterface recyclerViewInterface;

    public Upload_Video_Adapter(Context context, ArrayList<VideoModel> vList, String getBatchPreNumber, String getSemesterPreNumber, String getSubjectNumber, String getWeekNumber, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.vList = vList;
        this.getBatchPreNumber = getBatchPreNumber;
        this.getSemesterPreNumber = getSemesterPreNumber;
        this.getSubjectNumber = getSubjectNumber;
        this.getWeekNumber = getWeekNumber;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public void setFilteredList(ArrayList<VideoModel> filterList) {
        this.vList = filterList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public Upload_Video_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_selected_pdf_and_video, parent, false);

        return new Upload_Video_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Upload_Video_Adapter.ViewHolder holder, int position) {

        VideoModel videoModel = vList.get(position);
        holder.videoName.setText(videoModel.getVideoName());
        holder.videoDate.setText(videoModel.getVideoUploadDate());

        holder.preVAL = videoModel.getVideosMainChildID();
        holder.url = videoModel.getVideoUrl();

        holder.tapText.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return vList.size();
    }

    @Override
    public void onItemClick(int Position, String value, String preValue) {

    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView videoName, videoDate, tapText;
        String preVAL, url;
        ImageView deleteIcon, fileIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoName = itemView.findViewById(R.id.uploadFileName);
            videoDate = itemView.findViewById(R.id.uploadFileDate);
            tapText = itemView.findViewById(R.id.tapText);
            deleteIcon = itemView.findViewById(R.id.btnPDFDelete);
            fileIcon = itemView.findViewById(R.id.fileIcon);

            fileIcon.setImageResource(R.drawable.video_icon);

            //get item clicked position
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos, url, preVAL);
                        }
                    }
                }
            });

            //open action dialog to delete pdf details
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(getBatchPreNumber).child("batch_Sem").child(getSemesterPreNumber).child("semester_subject").child(getSubjectNumber).child("weeks").child(getWeekNumber).child("week_video");

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
