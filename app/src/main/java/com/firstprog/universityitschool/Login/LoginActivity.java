package com.firstprog.universityitschool.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firstprog.universityitschool.Admin.AdminActivity;
import com.firstprog.universityitschool.MainActivity;
import com.firstprog.universityitschool.Model.UserModel;
import com.firstprog.universityitschool.Register.RegisterActivity;
import com.firstprog.universityitschool.Student.StudentActivity;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.firstprog.universityitschool.Utility.Preferences;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPass;
    private Button btnLogin;
    private TextView btnForget, btnRegister;
    private Switch loginSwitch;
    private Pattern pattern;
    private Matcher matcher;
    private String password, email;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private static final String EMAIL_REGEX = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";
    private LoadingDialog loading_Dialog;
    public String uid;
    public FirebaseUser user;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initView();

        //btn for home screen
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check internet connection
                if (!CheckNetwork.isConnected(LoginActivity.this)) {
                    CheckNetwork.showNetworkDialog(LoginActivity.this);
                } else {

                    //assign user inputs to specific variables
                    email = editTextEmail.getText().toString().replaceAll("\\s+$", "");
                    password = editTextPass.getText().toString();

                    loading_Dialog = new LoadingDialog(LoginActivity.this);

                    //check whether email and password filed inputs are correct or not
                    if (validator(email, password)) {
                        /**
                         * first check th email and password are matching or not
                         * if it match
                         *  open home screen
                         *  **/
                        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    uid = firebaseAuth.getCurrentUser().getUid();
                                    user = firebaseAuth.getCurrentUser();
                                    databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("users");
                                    loading_Dialog.showDialog("Please Wait..");

                                    if (uid != null) {
                                        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    UserModel userModel = dataSnapshot.getValue(UserModel.class);

                                                    if (user.isEmailVerified()) {
                                                        databaseReference.child(uid).child("token").setValue("1");

                                                        if (loginSwitch.isChecked()) {
                                                            if ((userModel.role).equals("admin") && (userModel.token).equals("1")) {
                                                                Preferences.setDataLogin(LoginActivity.this, true);
                                                                Preferences.setRole(LoginActivity.this, "admin");
                                                                Preferences.setUid(LoginActivity.this, uid);
                                                                Preferences.setUEmail(LoginActivity.this, email);
                                                                Preferences.setUPass(LoginActivity.this, password);
                                                                loading_Dialog.HideDialog();
                                                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                                                finish();
                                                            } else if ((userModel.role).equals("lecturer") && (userModel.token).equals("1")) {
                                                                Preferences.setDataLogin(LoginActivity.this, true);
                                                                Preferences.setRole(LoginActivity.this, "lecturer");
                                                                Preferences.setUid(LoginActivity.this, uid);
                                                                loading_Dialog.HideDialog();
                                                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                                finish();
                                                            } else if ((userModel.role).equals("student") && (userModel.token).equals("1")) {
                                                                Preferences.setDataLogin(LoginActivity.this, true);
                                                                Preferences.setRole(LoginActivity.this, "student");
                                                                Preferences.setUid(LoginActivity.this, uid);
                                                                loading_Dialog.HideDialog();
                                                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(LoginActivity.this, StudentActivity.class));
                                                                finish();
                                                            } else {
                                                                loading_Dialog.HideDialog();
                                                                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            //log to app without saving logging credentials
                                                            if ((userModel.role).equals("admin") && (userModel.token).equals("1")) {
                                                                Preferences.setDataLogin(LoginActivity.this, false);
                                                                Preferences.setUid(LoginActivity.this, uid);
                                                                Preferences.setUEmail(LoginActivity.this, email);
                                                                Preferences.setUPass(LoginActivity.this, password);
                                                                loading_Dialog.HideDialog();
                                                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                                                finish();
                                                            } else if ((userModel.role).equals("lecturer") && (userModel.token).equals("1")) {
                                                                Preferences.setDataLogin(LoginActivity.this, false);
                                                                Preferences.setUid(LoginActivity.this, uid);
                                                                loading_Dialog.HideDialog();
                                                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                                finish();
                                                            } else if ((userModel.role).equals("student") && (userModel.token).equals("1")) {
                                                                Preferences.setDataLogin(LoginActivity.this, false);
                                                                Preferences.setUid(LoginActivity.this, uid);
                                                                loading_Dialog.HideDialog();
                                                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(LoginActivity.this, StudentActivity.class));
                                                                finish();
                                                            } else {
                                                                loading_Dialog.HideDialog();
                                                                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    } else {
                                                        loading_Dialog.HideDialog();
                                                        emailVerificationDialog();
                                                    }

                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Email and Password does not match!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        if (email.isEmpty() && password.isEmpty())
                            Toast.makeText(LoginActivity.this, "Sorry Check Information Again!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //btn for forget screen
        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, PasswordResetActivity.class));
            }
        });

        //btn for signup screen
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void emailVerificationDialog() {
        dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.activity_verification_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        Button btnOk = dialog.findViewById(R.id.btnOk);
        dialog.create();
        dialog.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Failed due to: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    //check shared preference when opening a app
    @Override
    public void onStart() {
        super.onStart();
        if (Preferences.getStatus(LoginActivity.this)) {
            if (Preferences.getRole(LoginActivity.this).equals("admin")) {
                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                startActivity(intent);
                finish();
            } else if (Preferences.getRole(LoginActivity.this).equals("lecturer")) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (Preferences.getRole(LoginActivity.this).equals("student")) {
                Intent intent = new Intent(LoginActivity.this, StudentActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    //validate user inputs
    private boolean validator(String personEmail, String password) {
        if (personEmail.isEmpty()) {
            Toast.makeText(LoginActivity.this, "UserEmail Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!validate(personEmail)) {
            Toast.makeText(LoginActivity.this, "UserEmail Should be a Valid Email Address", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Password Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 6) {
            Toast.makeText(LoginActivity.this, "Password Should Have Minimum 6 Characters!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    //validate email address
    private boolean validate(final String email) {
        pattern = Pattern.compile(EMAIL_REGEX);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //assign variable to id
    private void initView() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPass = findViewById(R.id.editTextPass);
        btnLogin = findViewById(R.id.login);
        loginSwitch = findViewById(R.id.loginSwitch);
        btnForget = findViewById(R.id.btnForget);
        btnRegister = findViewById(R.id.btnRegister);
        pattern = Pattern.compile(EMAIL_REGEX);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}