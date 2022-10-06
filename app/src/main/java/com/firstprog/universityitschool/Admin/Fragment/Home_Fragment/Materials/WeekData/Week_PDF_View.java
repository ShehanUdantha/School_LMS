package com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.firstprog.universityitschool.Interfaces.BackPressedListener;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Week_PDF_View extends Fragment implements BackPressedListener {

    private Context context;
    private WebView webView;
    private ProgressBar progressBar;
    private String batchPreNumber, semesterPreNumber, subjectPreKey, weekPreKey, weekKey, pdfUrl, value;
    public static BackPressedListener backListener;

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_week_pdf_viwer, container, false);

        context = view.getContext();

        //check internet connection
        if (!CheckNetwork.isConnected(context)) {
            CheckNetwork.showNetworkDialog(context);
        } else {

            progressBar = view.findViewById(R.id.progressBar2);
            progressBar.setVisibility(View.VISIBLE);

            Bundle bundle = this.getArguments();
            semesterPreNumber = bundle.getString("SemesterPreKey");
            batchPreNumber = bundle.getString("BatchPreKey");
            subjectPreKey = bundle.getString("SubjectPreKey");
            weekPreKey = bundle.getString("WeekPreKey");
            weekKey = bundle.getString("WeekKey");
            pdfUrl = bundle.getString("PDFUrl");
            value = bundle.getString("Value");

            webView = view.findViewById(R.id.pdfWebView);

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.setWebChromeClient(new WebChromeClient());
            webView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    webView.loadUrl("javascript:(function() { " +
                            "document.querySelector('[role=\"toolbar\"]').remove();})()");
                    progressBar.setVisibility(View.GONE);
                }
            });
            //"https://docs.google.com/viewerng/viewer?embedded=true&url="
            try {
                webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + URLEncoder.encode(pdfUrl, "ISO-8859-1"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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
        bundle.putString("SemesterPreKey", semesterPreNumber);
        bundle.putString("BatchPreKey", batchPreNumber);
        bundle.putString("SubjectPreKey", subjectPreKey);
        bundle.putString("WeekPreKey", weekPreKey);
        bundle.putString("WeekKey", weekKey);

        if (value.equals("pdf")) {
            Week_Lessons_PDF_Fragment week_lessons_pdf_fragment = new Week_Lessons_PDF_Fragment();
            week_lessons_pdf_fragment.setArguments(bundle);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayer, week_lessons_pdf_fragment);
            ft.commit();

        } else if (value.equals("grade")) {
            Week_Grades_Fragment week_grades_fragment = new Week_Grades_Fragment();
            week_grades_fragment.setArguments(bundle);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayer, week_grades_fragment);
            ft.commit();

        }

    }
}
