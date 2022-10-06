package com.firstprog.universityitschool.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.firstprog.universityitschool.Utility.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private Button reset;
    private FirebaseAuth firebaseAuth;
    private Pattern pattern;
    private Matcher matcher;
    private static final String EMAIL_REGEX = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";
    private String email;
    private LoadingDialog loading_Dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_password_reset);

        initView();
        loading_Dialog = new LoadingDialog(PasswordResetActivity.this);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check internet connection
                if (!CheckNetwork.isConnected(PasswordResetActivity.this)) {
                    CheckNetwork.showNetworkDialog(PasswordResetActivity.this);
                } else {
                    email = editTextEmail.getText().toString().replaceAll("\\s+$", "");
                    if (email.isEmpty()) {
                        Toast.makeText(PasswordResetActivity.this, "UserEmail Cannot be Empty!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (validate(email)) {
                            loading_Dialog.showDialog("Please Wait..");
                            resetPassword(email);
                        } else {
                            Toast.makeText(PasswordResetActivity.this, "UserEmail Should be Valid Email Address", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void resetPassword(String email) {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loading_Dialog.HideDialog();
                    Toast.makeText(PasswordResetActivity.this, "Please Check Your Inbox for Password Reset Link!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PasswordResetActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
    }

    private void initView() {
        editTextEmail = findViewById(R.id.editTextEmail);
        reset = findViewById(R.id.reset);
        pattern = Pattern.compile(EMAIL_REGEX);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    //validate email address
    private boolean validate(final String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}