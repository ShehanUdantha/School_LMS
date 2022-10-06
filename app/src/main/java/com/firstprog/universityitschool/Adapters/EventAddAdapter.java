package com.firstprog.universityitschool.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firstprog.universityitschool.Interfaces.RecyclerViewInterface;
import com.firstprog.universityitschool.Model.EventsModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.EventsAddDialog;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class EventAddAdapter extends RecyclerView.Adapter<EventAddAdapter.ViewHolder> implements RecyclerViewInterface {

    private Context context;
    private ArrayList<EventsModel> eveList;
    private RecyclerViewInterface recyclerViewInterface;
    private DatabaseReference databaseReference;
    private String getBatchPreNumber, getSemesterPreNumber, getSubjectNumber, getWeekNumber;

    public EventAddAdapter(Context context, ArrayList<EventsModel> eveList, RecyclerViewInterface recyclerViewInterface, String getBatchPreNumber, String getSemesterPreNumber, String getSubjectNumber, String getWeekNumber) {
        this.context = context;
        this.eveList = eveList;
        this.recyclerViewInterface = recyclerViewInterface;
        this.getBatchPreNumber = getBatchPreNumber;
        this.getSemesterPreNumber = getSemesterPreNumber;
        this.getSubjectNumber = getSubjectNumber;
        this.getWeekNumber = getWeekNumber;
    }

    public void setFilteredList(ArrayList<EventsModel> filterList) {
        this.eveList = filterList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public EventAddAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_event_view, parent, false);

        return new EventAddAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAddAdapter.ViewHolder holder, int position) {
        EventsModel eventsModel = eveList.get(position);

        if (eventsModel.getEventDate() != null) {
            String splitDate[] = eventsModel.getEventDate().split(" ");
            holder.dayView.setText(splitDate[1]);
            holder.monthView.setText(splitDate[0]);
        }

        holder.evenTitle.setText(eventsModel.getEventTitle());
        holder.evenTime.setText(eventsModel.getEventTime());
        holder.evenDescription.setText(eventsModel.getEventDescription());

        if (eventsModel.getEventPriority() != null && eventsModel.getEventPriority().equalsIgnoreCase("1")) {
            holder.eveShape.setImageResource(R.drawable.green_shape);
        } else if (eventsModel.getEventPriority() != null && eventsModel.getEventPriority().equalsIgnoreCase("2")) {
            holder.eveShape.setImageResource(R.drawable.yellow_shape);
        } else if (eventsModel.getEventPriority() != null && eventsModel.getEventPriority().equalsIgnoreCase("3")) {
            holder.eveShape.setImageResource(R.drawable.red_shape);
        } else {
            holder.eveShape.setImageResource(R.drawable.green_shape);
        }

        holder.preVAL = eventsModel.getEventMainChildID();
        holder.eDayView = eventsModel.getEventDate();
        holder.eEvenTitle = eventsModel.getEventTitle();
        holder.eEvenTime = eventsModel.getEventTime();
        holder.eEvenDescription = eventsModel.getEventDescription();
        holder.ePriority = eventsModel.getEventPriority();
        holder.eUrl = eventsModel.getEventUrl();

    }

    @Override
    public int getItemCount() {
        return eveList.size();
    }

    @Override
    public void onItemClick(int Position, String value, String preValue) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView dayView, monthView, evenTitle, evenTime, evenDescription;
        ImageView penIcon, deleteIcon, eveShape;
        Button btnEvenUrl;
        String preVAL, eDayView, eEvenTitle, eEvenTime, eEvenDescription, ePriority, eUrl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dayView = itemView.findViewById(R.id.dayView);
            monthView = itemView.findViewById(R.id.monthView);
            evenTitle = itemView.findViewById(R.id.evenTitle);
            evenTime = itemView.findViewById(R.id.evenTime);
            evenDescription = itemView.findViewById(R.id.evenDescription);
            eveShape = itemView.findViewById(R.id.eveShape);
            penIcon = itemView.findViewById(R.id.btnEdit8);
            deleteIcon = itemView.findViewById(R.id.btnDelete8);
            btnEvenUrl = itemView.findViewById(R.id.btnEvenUrl);

            //get item clicked position
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos, "", preVAL);
                        }
                    }
                }
            });

            //open dialog to edit notes
            penIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventsAddDialog eventsAddDialog = new EventsAddDialog(context);
                    eventsAddDialog.showDialogWithDetails("Update Event", eEvenTitle, eUrl, ePriority, eDayView, eEvenTime, eEvenDescription);

                    eventsAddDialog.pickDate().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eventsAddDialog. DatePick();
                        }
                    });

                    eventsAddDialog.pickTime().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eventsAddDialog.TimePick();
                        }
                    });

                    eventsAddDialog.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eventsAddDialog.updateFirebaseDetails(getBatchPreNumber, getSemesterPreNumber, getSubjectNumber, getWeekNumber, preVAL);
                            notifyDataSetChanged();
                        }
                    });
                }
            });

            //open action dialog to delete notes details
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch").child(getBatchPreNumber).child("batch_Sem").child(getSemesterPreNumber).child("semester_subject").child(getSubjectNumber).child("weeks").child(getWeekNumber).child("week_events");

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

            //open zoom meeting
            btnEvenUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(eUrl));
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
