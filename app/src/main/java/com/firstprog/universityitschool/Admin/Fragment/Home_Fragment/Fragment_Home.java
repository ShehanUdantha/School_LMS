package com.firstprog.universityitschool.Admin.Fragment.Home_Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Events.Events_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.Materials_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Module.Modules_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.News.News_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Payments.Payment_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Users.User_Fragment;
import com.firstprog.universityitschool.R;

public class Fragment_Home extends Fragment {
    private RelativeLayout userCard, moduleCard, materialCard, eventsCard, newsCard, paymentCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        userCard = v.findViewById(R.id.userCard);
        moduleCard = v.findViewById(R.id.moduleCard);
        materialCard = v.findViewById(R.id.materialCard);
        eventsCard = v.findViewById(R.id.eventsCard);
        newsCard = v.findViewById(R.id.newsCard);
        paymentCard = v.findViewById(R.id.paymentCard);


        userCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayer, new User_Fragment());
                ft.commit();
            }
        });

        moduleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayer, new Modules_Fragment());
                ft.commit();
            }
        });

        materialCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayer, new Materials_Fragment());
                ft.commit();
            }
        });

        eventsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayer, new Events_Fragment());
                ft.commit();
            }
        });

        newsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayer, new News_Fragment());
                ft.commit();
            }
        });

        paymentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayer, new Payment_Fragment());
                ft.commit();
            }
        });

        return v;
    }

}
