package com.firstprog.universityitschool.Utility;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import com.firstprog.universityitschool.Model.UserModel;
import com.firstprog.universityitschool.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserAddDialog {

    private Context userContext;
    private Dialog userDialog;
    Button submit;
    private EditText FirstName, LastName, Email, Index, Pass, ConPassword;
    private AutoCompleteTextView Role, Batch;
    ImageView prof, profEdit;
    private LoadingDialog loading_Dialog, loading_Dialog2;
    private Pattern pattern;
    private Matcher matcher;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference, databaseReferenceBatch;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private String defaultUrl, uid, adminEmail, adminPassword;
    private static final String EMAIL_REGEX = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";

    public UserAddDialog(Context userContext) {
        this.userContext = userContext;
    }

    //return button id to userFragment
    public Button submit() {
        return submit = userDialog.findViewById(R.id.btnSubmit);
    }

    //return image id to userFragment
    public ImageView editImage() {
        return profEdit = userDialog.findViewById(R.id.btnAddEdit);
    }

    public void updateImage(String updateImg) {
        Glide.with(userContext)
                .asBitmap()
                .load(updateImg)
                .into(prof);
    }

    public interface OnEmailCheckListener {
        void onSuccess(boolean isRegistered);
    }

    public void uploadToFirebase(Uri fillPath) {

        //check internet connection
        if (!CheckNetwork.isConnected(userContext)) {
            CheckNetwork.showNetworkDialog(userContext);
        } else {

            loading_Dialog = new LoadingDialog(userContext);

            String fName = FirstName.getText().toString();
            String lName = LastName.getText().toString();
            String role = Role.getText().toString();
            String personEmail = Email.getText().toString().replaceAll("\\s+$", "");
            String password = Pass.getText().toString();
            String index = Index.getText().toString();
            String batch = Batch.getText().toString();
            String comPassword = ConPassword.getText().toString();

            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("users");
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            defaultUrl = "https://firebasestorage.googleapis.com/v0/b/itschool-auth.appspot.com/o/users%2Fdefault%2Fprofile.png?alt=media&token=5f792bdd-0be2-4a18-a8da-f5ded5ccf1a2";


            //validate user inputs
            if (validator(personEmail, password, comPassword, fName, lName, role, index, batch)) {
                loading_Dialog.showDialog("Please Wait..");

                //check whether input email registered or not
                isCheckEmail(personEmail, new OnEmailCheckListener() {
                    @Override
                    public void onSuccess(boolean isRegistered) {

                        if (isRegistered) {
                            loading_Dialog.HideDialog();
                            Toast.makeText(userContext, "This Email all-ready Registered", Toast.LENGTH_SHORT).show();

                        } else {
                            //register user to firebase
                            firebaseAuth.createUserWithEmailAndPassword(personEmail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //store user details on created userid in users
                                        UserModel userModel = new UserModel(FirebaseAuth.getInstance().getCurrentUser().getUid(), fName, lName, personEmail, index, role, batch, defaultUrl, password, "0");
                                        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    //update user img url
                                                    if (fillPath != null) {
                                                        getImageUrl(firebaseAuth.getCurrentUser().getUid(), fillPath);
                                                    } else {
                                                        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("img").setValue(defaultUrl);
                                                    }

                                                    //get shared preferences
                                                    if (Preferences.getStatus(userContext)) {
                                                        uid = Preferences.getUid(userContext);
                                                        adminEmail = Preferences.getUEmail(userContext);
                                                        adminPassword = Preferences.getUPass(userContext);
                                                    }
                                                    //sign in again
                                                    /**because after registered the new user,
                                                     * current firebase user id change to that new firebase user id,
                                                     * so we need to change again it to previous firebase user id
                                                     */
                                                    firebaseAuth.signInWithEmailAndPassword(adminEmail, adminPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                                            if (task.isSuccessful()) {
                                                                databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                    }
                                                                });
                                                                Log.d(TAG, "onTestView: Again admin: " + firebaseAuth.getCurrentUser().getUid());
                                                            }
                                                        }
                                                    });
                                                    editTextClear();
                                                    HideDialog();
                                                    loading_Dialog.HideDialog();
                                                    Toast.makeText(userContext, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    loading_Dialog.HideDialog();
                                                    HideDialog();
                                                    Toast.makeText(userContext, "Failed to Register", Toast.LENGTH_SHORT).show();
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
                if (fName.isEmpty() && lName.isEmpty() && role.isEmpty() && personEmail.isEmpty() && password.isEmpty() && index.isEmpty() && batch.isEmpty())
                    Toast.makeText(userContext, "Sorry Check Information Again!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //check whether input email all-ready registered or not
    public void isCheckEmail(final String email, final OnEmailCheckListener listener) {
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

    //update firebase user data when clicking edit button
    public void updateFirebaseUserDetails(String userid) {

        //check internet connection
        if (!CheckNetwork.isConnected(userContext)) {
            CheckNetwork.showNetworkDialog(userContext);
        } else {

            loading_Dialog2 = new LoadingDialog(userContext);

            String fName = FirstName.getText().toString();
            String lName = LastName.getText().toString();
            String role = Role.getText().toString();
            String personEmail = Email.getText().toString();
            String index = Index.getText().toString();
            String batch = Batch.getText().toString();

            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("users");

            if (validator(personEmail, "password", "password", fName, lName, role, index, batch)) {
                loading_Dialog2.showDialog("Please Wait..");
                databaseReference.child(userid).child("firstName").setValue(fName);
                databaseReference.child(userid).child("lastName").setValue(lName);
                databaseReference.child(userid).child("email").setValue(personEmail);
                databaseReference.child(userid).child("role").setValue(role);
                databaseReference.child(userid).child("indexNo").setValue(index);
                databaseReference.child(userid).child("batch").setValue(batch);
                HideDialog();
                loading_Dialog2.HideDialog();
                Toast.makeText(userContext, "Update Successful", Toast.LENGTH_SHORT).show();

            } else {
                if (fName.isEmpty() && lName.isEmpty() && role.isEmpty() && personEmail.isEmpty() && index.isEmpty() && batch.isEmpty())
                    Toast.makeText(userContext, "Sorry Check Information Again!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //display editText view when clicking add button
    public void showDialog() {
        userDialog = new Dialog(userContext);
        userDialog.setContentView(R.layout.user_add_dialogbox);
        userDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        userDialog.setCanceledOnTouchOutside(true);

        databaseReferenceBatch = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("batch");


        FirstName = userDialog.findViewById(R.id.editFirstName);
        LastName = userDialog.findViewById(R.id.editLastName);
        Role = userDialog.findViewById(R.id.roleSpinner);
        Email = userDialog.findViewById(R.id.editEmail);
        Index = userDialog.findViewById(R.id.editIndexNo);
        Batch = userDialog.findViewById(R.id.filledSpinnerBatch);
        prof = userDialog.findViewById(R.id.profileAddPicture);
        Pass = userDialog.findViewById(R.id.editPassword);
        ConPassword = userDialog.findViewById(R.id.ConPassword);

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
        databaseReferenceBatch.addListenerForSingleValueEvent(valueEventListener);


        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(userContext, R.layout.drop_down_items, roleList);
        ArrayAdapter<String> batchAdapter = new ArrayAdapter<>(userContext, R.layout.drop_down_items, batchList);

        Role.setAdapter(roleAdapter);
        Batch.setAdapter(batchAdapter);

        userDialog.create();
        userDialog.show();
    }

    //display editText view with user details when clicking edit button
    public void showDialogWithDetails(String fName, String lName, String email, String role, String batch, String indexN, String img) {
        //check internet connection
        if (!CheckNetwork.isConnected(userContext)) {
            CheckNetwork.showNetworkDialog(userContext);
        } else {

            userDialog = new Dialog(userContext);
            userDialog.setContentView(R.layout.user_add_dialogbox);
            userDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            userDialog.setCanceledOnTouchOutside(true);

            //set user details in preview
            FirstName = userDialog.findViewById(R.id.editFirstName);
            FirstName.setText(fName);
            LastName = userDialog.findViewById(R.id.editLastName);
            LastName.setText(lName);
            Role = userDialog.findViewById(R.id.roleSpinner);
            Role.setText(role);
            Email = userDialog.findViewById(R.id.editEmail);
            Email.setText(email);
            Index = userDialog.findViewById(R.id.editIndexNo);
            Index.setText(indexN);
            Batch = userDialog.findViewById(R.id.filledSpinnerBatch);
            Batch.setText(batch);
            prof = userDialog.findViewById(R.id.profileAddPicture);
            Glide.with(userContext)
                    .asBitmap()
                    .load(img)
                    .into(prof);


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
            databaseReferenceBatch.addListenerForSingleValueEvent(valueEventListener);

            ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(userContext, R.layout.drop_down_items, roleList);
            ArrayAdapter<String> batchAdapter = new ArrayAdapter<>(userContext, R.layout.drop_down_items, batchList);

            Role.setAdapter(roleAdapter);
            Batch.setAdapter(batchAdapter);

            //hide some features
            ImageView btnAddEdit = userDialog.findViewById(R.id.btnAddEdit);
            btnAddEdit.setVisibility(View.GONE);

            EditText editPassword = userDialog.findViewById(R.id.editPassword);
            editPassword.setVisibility(View.GONE);

            EditText editConPassword = userDialog.findViewById(R.id.ConPassword);
            editConPassword.setVisibility(View.GONE);

            userDialog.create();
            userDialog.show();
        }
    }


    //validate user inputs
    private boolean validator(String personEmail, String password, String conPassword, String fName, String lName, String role, String index, String batch) {

        if (fName.isEmpty()) {
            Toast.makeText(userContext, "UserFirstName Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (lName.isEmpty()) {
            Toast.makeText(userContext, "UserLastName Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (personEmail.isEmpty()) {
            Toast.makeText(userContext, "UserEmail Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!validate(personEmail)) {
            Toast.makeText(userContext, "UserEmail Should be Valid Email Address", Toast.LENGTH_SHORT).show();
            return false;
        } else if (role.isEmpty()) {
            Toast.makeText(userContext, "UserRole Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (index.isEmpty()) {
            Toast.makeText(userContext, "UserIndexNo Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (batch.isEmpty()) {
            Toast.makeText(userContext, "UserBatch Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.isEmpty()) {
            Toast.makeText(userContext, "Password Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 6) {
            Toast.makeText(userContext, "Password Should Have Minimum 6 Characters!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (conPassword.isEmpty()) {
            Toast.makeText(userContext, "Confirm Password Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (conPassword.length() < 6) {
            Toast.makeText(userContext, "Confirm Password Should Have Minimum 6 Characters!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.equals(conPassword)) {
            Toast.makeText(userContext, "Confirm Password Should be Same!", Toast.LENGTH_SHORT).show();
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

    //selected img upload to the firebase and get firebase url and store it user img path according to user id
    public void getImageUrl(String userId, Uri fillPath) {
        StorageReference ref = storageReference.child("users").child(userId).child("images/" + UUID.randomUUID().toString());
        ref.putFile(fillPath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        //get url of uploaded img
                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                databaseReference.child(userId).child("img").setValue(task.getResult().toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }


    //clear editText after adding new user
    public void editTextClear() {
        FirstName.setText(" ");
        LastName.setText(" ");
        Role.setText(" ");
        Email.setText(" ");
        Pass.setText(" ");
        Index.setText(" ");
        Batch.setText(" ");
        ConPassword.setText(" ");
    }

    public void HideDialog() {
        userDialog.dismiss();
    }
}

