package com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.firstprog.universityitschool.Interfaces.BackPressedListener;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;


public class Week_Video_View extends Fragment implements BackPressedListener {

    private Context context;
    private VideoView videoView;
    private MediaController mediaController;
    private Uri uri;
    private String batchPreNumber, semesterPreNumber, subjectPreKey, weekPreKey, weekKey, videoUrl;
    public static BackPressedListener backListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_week_video_view, container, false);

        context = view.getContext();

        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            videoView = view.findViewById(R.id.videoView);
            mediaController = new MediaController(context);

            Bundle bundle = this.getArguments();
            semesterPreNumber = bundle.getString("SemesterPreKey");
            batchPreNumber = bundle.getString("BatchPreKey");
            subjectPreKey = bundle.getString("SubjectPreKey");
            weekPreKey = bundle.getString("WeekPreKey");
            weekKey = bundle.getString("WeekKey");
            videoUrl = bundle.getString("VideoUrl");

            uri = Uri.parse(videoUrl);
            videoView.setVideoURI(uri);

            mediaController.setMediaPlayer(videoView);
            videoView.setMediaController(mediaController);
            videoView.requestFocus();
            videoView.start();
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

        Week_Lessons_Video_Fragment week_lessons_video_fragment = new Week_Lessons_Video_Fragment();
        week_lessons_video_fragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayer, week_lessons_video_fragment);
        ft.commit();

    }
}
