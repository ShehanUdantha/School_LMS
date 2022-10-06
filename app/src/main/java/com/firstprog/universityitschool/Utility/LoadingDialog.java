package com.firstprog.universityitschool.Utility;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import com.firstprog.universityitschool.R;

public class LoadingDialog {
    Context context;
    Dialog dialog;

    public LoadingDialog(Context context) {
        this.context = context;
    }

    public void showDialog(String title) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        TextView textView = dialog.findViewById(R.id.textView);
        textView.setText(title);
        dialog.create();
        dialog.show();
    }

    public void HideDialog() {
        dialog.dismiss();
    }
}
