package com.firstprog.universityitschool.Register;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firstprog.universityitschool.Login.LoginActivity;
import com.firstprog.universityitschool.Model.UserModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.firstprog.universityitschool.Utility.UserAddDialog;
import com.firstprog.universityitschool.Utility.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText FirstName2, LastName2, Email2, IndexNo2, Password2, confPassword2;
    private Button btnSubmit2;
    private AutoCompleteTextView Role2, Batch2;
    private String fName, lName, email, role, indexNum, batch, password, confirmPassword;
    private LoadingDialog loading_Dialog;
    private Pattern pattern;
    private Matcher matcher;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference, batchDatabaseReference;
    private String defaultUrl;
    private Dialog dialog;
    private FirebaseUser user;

    private static final String EMAIL_REGEX = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

        //setup firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("users");
        batchDatabaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch");
        defaultUrl = "https://firebasestorage.googleapis.com/v0/b/itschool-auth.appspot.com/o/users%2Fdefault%2Fprofile.png?alt=media&token=5f792bdd-0be2-4a18-a8da-f5ded5ccf1a2";

        //setup drop down menu items
        ArrayList<String> roleList = new ArrayList<>();
        roleList.add("admin");
        roleList.add("student");
        roleList.add("lecturer");


        ArrayList<String> batchList = new ArrayList<>();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String batchNumbers = ds.child("batchID").getValue(String.class);
                    batchList.add(batchNumbers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        batchDatabaseReference.addListenerForSingleValueEvent(valueEventListener);

        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, R.layout.drop_down_items, roleList);
        ArrayAdapter<String> batchAdapter = new ArrayAdapter<>(this, R.layout.drop_down_items, batchList);

        Role2.setAdapter(roleAdapter);
        Batch2.setAdapter(batchAdapter);

        //btn for submit user details to firebase
        btnSubmit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check internet connection
                if (!CheckNetwork.isConnected(RegisterActivity.this)) {
                    CheckNetwork.showNetworkDialog(RegisterActivity.this);
                } else {

                    //assign user inputs to specific variables
                    fName = FirstName2.getText().toString();
                    lName = LastName2.getText().toString();
                    email = Email2.getText().toString().replaceAll("\\s+$", "");
                    indexNum = IndexNo2.getText().toString();
                    password = Password2.getText().toString();
                    confirmPassword = confPassword2.getText().toString();
                    role = Role2.getText().toString();
                    batch = Batch2.getText().toString();

                    loading_Dialog = new LoadingDialog(RegisterActivity.this);

                    //check whether user inputs are correct or not
                    if (validator(email, password, confirmPassword, fName, lName, role, indexNum, batch)) {

                        loading_Dialog.showDialog("Please Wait..");

                        //check whether input email registered or not
                        isCheckEmail(email, new UserAddDialog.OnEmailCheckListener() {
                            @Override
                            public void onSuccess(boolean isRegistered) {

                                if (isRegistered) {
                                    loading_Dialog.HideDialog();
                                    Toast.makeText(RegisterActivity.this, "This Email all-ready Registered", Toast.LENGTH_SHORT).show();

                                } else {
                                    //register user to firebase
                                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                user = firebaseAuth.getCurrentUser();
                                                //store user details on created userid in users
                                                UserModel userModel = new UserModel(FirebaseAuth.getInstance().getCurrentUser().getUid(), fName, lName, email, indexNum, role, batch, defaultUrl, password, "0");
                                                databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            editTextClear();
                                                            loading_Dialog.HideDialog();
                                                            Toast.makeText(RegisterActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();

                                                            verifyDialog();
                                                        } else {
                                                            loading_Dialog.HideDialog();
                                                            Toast.makeText(RegisterActivity.this, "Failed to Register", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });

                                }

                            }
                        });

                    } else {
                        if (fName.isEmpty() && lName.isEmpty() && role.isEmpty() && email.isEmpty() && password.isEmpty() && confirmPassword.isEmpty() && indexNum.isEmpty() && batch.isEmpty())
                            Toast.makeText(RegisterActivity.this, "Sorry Check Information Again!", Toast.LENGTH_SHORT).show();
                    }

                }
            }

        });

    }


    private void verifyDialog() {
        dialog = new Dialog(RegisterActivity.this);
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
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Failed due to: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void initView() {
        FirstName2 = findViewById(R.id.editFirstName2);
        LastName2 = findViewById(R.id.editLastName2);
        Email2 = findViewById(R.id.editEmail2);
        Role2 = findViewById(R.id.filledSpinner);
        IndexNo2 = findViewById(R.id.editIndexNo2);
        Batch2 = findViewById(R.id.filledSpinnerBatch);
        Password2 = findViewById(R.id.editPassword2);
        confPassword2 = findViewById(R.id.confPassword2);
        btnSubmit2 = findViewById(R.id.btnSubmit2);
    }

    //check whether input email all-ready registered or not
    public void isCheckEmail(final String email, final UserAddDialog.OnEmailCheckListener listener) {
        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                boolean check = !(task.getResult().getSignInMethods().size() == 0);

                listener.onSuccess(check);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }


    //validate user inputs
    private boolean validator(String personEmail, String password, String conPassword, String fName, String lName, String role, String index, String batch) {

        if (fName.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "UserFirstName Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (lName.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "UserLastName Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (personEmail.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "UserEmail Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!validate(personEmail)) {
            Toast.makeText(RegisterActivity.this, "UserEmail Should be Valid Email Address", Toast.LENGTH_SHORT).show();
            return false;
        } else if (role.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "UserRole Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (index.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "UserIndexNo Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (batch.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "UserBatch Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Password Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (conPassword.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Confirm Password Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Password Should Have Minimum 6 Characters!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (conPassword.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Confirm Password Should Have Minimum 6 Characters!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.equals(conPassword)) {
            Toast.makeText(RegisterActivity.this, "Confirm Password Should be Same!", Toast.LENGTH_SHORT).show();
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

    //clear editText after adding new user
    public void editTextClear() {
        FirstName2.setText(" ");
        LastName2.setText(" ");
        Email2.setText(" ");
        IndexNo2.setText(" ");
        Batch2.setText(" ");
        Password2.setText(" ");
        confPassword2.setText(" ");
        Role2.setText(" ");
    }
}