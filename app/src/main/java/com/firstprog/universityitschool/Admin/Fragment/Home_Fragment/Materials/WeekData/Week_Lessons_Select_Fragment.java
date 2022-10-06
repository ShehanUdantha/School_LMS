package com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.firstprog.universityitschool.Interfaces.BackPressedListener;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;

public class Week_Lessons_Select_Fragment extends Fragment implements BackPressedListener {

    private RelativeLayout pdfCard, videoCard;
    private Context context;
    private String batchPreNumber, semesterPreNumber, subjectPreKey, weekPreKey, weekKey;
    public static BackPressedListener backListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_week_lessons, container, false);

        context = view.getContext();

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

            pdfCard = view.findViewById(R.id.pdfCard);
            pdfCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString("SemesterPreKey",semesterPreNumber);
                    bundle.putString("BatchPreKey",batchPreNumber);
                    bundle.putString("SubjectPreKey",subjectPreKey);
                    bundle.putString("WeekPreKey",weekPreKey);
                    bundle.putString("WeekKey",weekKey);

                    Week_Lessons_PDF_Fragment week_lessons_pdf_fragment = new Week_Lessons_PDF_Fragment();
                    week_lessons_pdf_fragment.setArguments(bundle);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayer, week_lessons_pdf_fragment);
                    ft.commit();
                }
            });

            videoCard = view.findViewById(R.id.videoFilesCard);
            videoCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString("SemesterPreKey",semesterPreNumber);
                    bundle.putString("BatchPreKey",batchPreNumber);
                    bundle.putString("SubjectPreKey",subjectPreKey);
                    bundle.putString("WeekPreKey",weekPreKey);
                    bundle.putString("WeekKey",weekKey);

                    Week_Lessons_Video_Fragment week_lessons_video_fragment = new Week_Lessons_Video_Fragment();
                    week_lessons_video_fragment.setArguments(bundle);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayer, week_lessons_video_fragment);
                    ft.commit();
                }
            });


        }

        return view;

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

        Materials_Week_View_Fragment materials_week_view_fragment = new Materials_Week_View_Fragment();
        materials_week_view_fragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayer, materials_week_view_fragment);
        ft.commit();

    }
}
