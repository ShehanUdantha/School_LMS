package com.firstprog.universityitschool.Utility;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firstprog.universityitschool.Admin.AdminActivity;
import com.firstprog.universityitschool.R;

public class ProfileDialog {

    private Context context;
    private Dialog dialog;
    Button logout;
    TextView FullName, Role, Email, Index, Batch;
    ImageView prof, profEdit;

    public ProfileDialog(Context context) {
        this.context = context;
    }

    public Button logOut() {
        return logout = dialog.findViewById(R.id.btnLogout);
    }

    public ImageView editImage() {
        return profEdit = dialog.findViewById(R.id.btnEdit);
    }

    public void updateImage(String updateImg) {
        Glide.with(context)
                .asBitmap()
                .load(updateImg)
                .into(prof);
    }

    public void showDialog(String name, String role, String email, String index, String batch, String img) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.profile_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        FullName = dialog.findViewById(R.id.personDBName);
        Role = dialog.findViewById(R.id.personRole);
        Email = dialog.findViewById(R.id.personDBEmail);
        Index = dialog.findViewById(R.id.personDBIndex);
        Batch = dialog.findViewById(R.id.personDBBatch);
        prof = dialog.findViewById(R.id.profilePicture);

        FullName.setText(name);
        Role.setText(role);
        Email.setText(email);
        Index.setText(index);
        Batch.setText(batch);

        Glide.with(context)
                .asBitmap()
                .load(img)
                .into(prof);

        dialog.create();
        dialog.show();
    }
}
