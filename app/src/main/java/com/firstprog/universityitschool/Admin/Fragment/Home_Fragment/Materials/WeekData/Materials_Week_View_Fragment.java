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

import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.Material_Selected_Subject_Fragment;
import com.firstprog.universityitschool.Interfaces.BackPressedListener;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;

public class Materials_Week_View_Fragment extends Fragment implements BackPressedListener {

    private RelativeLayout notesCard, lessonsCard, assignmentCard, quizCard, gradeCard;
    private Context context;
    private String batchPreNumber, semesterPreNumber, subjectPreKey, weekPreKey, weekKey;
    public static BackPressedListener backListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_material_selected_subject_week, container, false);

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

            notesCard = view.findViewById(R.id.notesCard);
            notesCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString("SemesterPreKey",semesterPreNumber);
                    bundle.putString("BatchPreKey",batchPreNumber);
                    bundle.putString("SubjectPreKey",subjectPreKey);
                    bundle.putString("WeekPreKey",weekPreKey);
                    bundle.putString("WeekKey",weekKey);

                    Week_Notes_Fragment week_notes_fragment = new Week_Notes_Fragment();
                    week_notes_fragment.setArguments(bundle);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayer, week_notes_fragment);
                    ft.commit();
                }
            });

            lessonsCard = view.findViewById(R.id.lessonsCard);
            lessonsCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString("SemesterPreKey",semesterPreNumber);
                    bundle.putString("BatchPreKey",batchPreNumber);
                    bundle.putString("SubjectPreKey",subjectPreKey);
                    bundle.putString("WeekPreKey",weekPreKey);
                    bundle.putString("WeekKey",weekKey);

                    Week_Lessons_Select_Fragment week_lessons_select_fragment = new Week_Lessons_Select_Fragment();
                    week_lessons_select_fragment.setArguments(bundle);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayer, week_lessons_select_fragment);
                    ft.commit();
                }
            });

            assignmentCard = view.findViewById(R.id.assignmentCard);
            assignmentCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString("SemesterPreKey",semesterPreNumber);
                    bundle.putString("BatchPreKey",batchPreNumber);
                    bundle.putString("SubjectPreKey",subjectPreKey);
                    bundle.putString("WeekPreKey",weekPreKey);
                    bundle.putString("WeekKey",weekKey);

                    Week_Assignments_Fragment week_assignments_fragment = new Week_Assignments_Fragment();
                    week_assignments_fragment.setArguments(bundle);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayer, week_assignments_fragment);
                    ft.commit();
                }
            });

            quizCard = view.findViewById(R.id.quizCard);
            quizCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString("SemesterPreKey",semesterPreNumber);
                    bundle.putString("BatchPreKey",batchPreNumber);
                    bundle.putString("SubjectPreKey",subjectPreKey);
                    bundle.putString("WeekPreKey",weekPreKey);
                    bundle.putString("WeekKey",weekKey);

                    Week_Quiz_Fragment week_quiz_fragment = new Week_Quiz_Fragment();
                    week_quiz_fragment.setArguments(bundle);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayer, week_quiz_fragment);
                    ft.commit();
                }
            });

            gradeCard = view.findViewById(R.id.gradeCard);
            gradeCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString("SemesterPreKey",semesterPreNumber);
                    bundle.putString("BatchPreKey",batchPreNumber);
                    bundle.putString("SubjectPreKey",subjectPreKey);
                    bundle.putString("WeekPreKey",weekPreKey);
                    bundle.putString("WeekKey",weekKey);

                    Week_Grades_Fragment week_grades_fragment = new Week_Grades_Fragment();
                    week_grades_fragment.setArguments(bundle);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayer, week_grades_fragment);
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

        Material_Selected_Subject_Fragment material_selected_subject_fragment = new Material_Selected_Subject_Fragment();
        material_selected_subject_fragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayer, material_selected_subject_fragment);
        ft.commit();

    }
}
