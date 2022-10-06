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

import com.bumptech.glide.Glide;
import com.firstprog.universityitschool.Interfaces.RecyclerViewInterface;
import com.firstprog.universityitschool.Model.BatchModel;
import com.firstprog.universityitschool.Model.NewsModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.BatchAddDialog;
import com.firstprog.universityitschool.Utility.NewsAddDialog;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<NewsModel> newsList;
    private DatabaseReference databaseReference;

    public NewsAdapter(Context context, ArrayList<NewsModel> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    public void setFilteredList(ArrayList<NewsModel> filterList) {
        this.newsList = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_news, parent, false);

        return new NewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
        NewsModel newsModel = newsList.get(position);
        holder.title.setText(newsModel.getNewsTitle());
        holder.news.setText(newsModel.getNews());

        Glide.with(context)
                .asBitmap()
                .load(newsModel.getNewsUrl())
                .into(holder.newsImage);


        holder.preVal = newsModel.getNewsMainChildName();
        holder.sNews = newsModel.getNews();
        holder.sTitle = newsModel.getNewsTitle();
        holder.sUrl = newsModel.getNewsUrl();

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, news;
        ImageView newsImage, penIcon, deleteIcon;
        String preVal, sTitle, sNews, sUrl;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            title = itemView.findViewById(R.id.newsTitle);
            news = itemView.findViewById(R.id.theNews);
            newsImage = itemView.findViewById(R.id.newsImg);
            penIcon = itemView.findViewById(R.id.btnEdit9);
            deleteIcon = itemView.findViewById(R.id.btnDelete9);

            //open dialog to edit batch number
            penIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewsAddDialog newsAddDialog = new NewsAddDialog(context);
                    newsAddDialog.showDialogWithDetails(sUrl,sTitle,sNews);

                    newsAddDialog.submit().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            newsAddDialog.updateFirebaseDetails(preVal);
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
                            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("news");

                            databaseReference.child(preVal).removeValue(new DatabaseReference.CompletionListener() {
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
