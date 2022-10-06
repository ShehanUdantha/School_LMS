package com.firstprog.universityitschool.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firstprog.universityitschool.Login.LoginActivity;
import com.firstprog.universityitschool.Model.UserModel;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Student.Fragment.Stu_Chat_Fragment;
import com.firstprog.universityitschool.Student.Fragment.Stu_Home_Fragment;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.firstprog.universityitschool.Utility.LoadingDialog;
import com.firstprog.universityitschool.Utility.Preferences;
import com.firstprog.universityitschool.Utility.ProfileDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String uid;
    private DatabaseReference databaseReference;
    private Toolbar tool;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MenuItem menuItem;
    private View view;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private CircleImageView circleImageView;
    private static final float END_SCALE = 0.7f;
    private ConstraintLayout contentView;
    private ProfileDialog profileDialog;
    private String name, role, email, index, batch, img;
    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 100;
    private LoadingDialog loading_Dialog, loading_Dialog2;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        initView();
        setSupportActionBar(tool);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationDrawer();

        //set home screen as default if nothing in saved
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.middleFrame, new Stu_Home_Fragment()).commit();
            navigationView.setCheckedItem(R.id.homeIcon);
        }

        sideBarItems();

        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        profileDialog = new ProfileDialog(StudentActivity.this);
        loading_Dialog = new LoadingDialog(StudentActivity.this);
        loading_Dialog2 = new LoadingDialog(StudentActivity.this);
    }

    private void navigationDrawer() {
        //Setup drawer on and close
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, tool, R.string.open_menu, R.string.close_menu);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //add animation to drawer
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Setup ToolBar
        getMenuInflater().inflate(R.menu.top_nav, menu);

        menuItem = menu.findItem(R.id.profileIcon);
        view = MenuItemCompat.getActionView(menuItem);

        circleImageView = view.findViewById(R.id.profilePic);

        //when clicked profile icon of tool bar, open user profile data dialog
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileDialog.showDialog(name, role, email, index, batch, img);
                //Log.d(TAG,"onTestView " + name);
                profileDialog.editImage().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkPermissionForReadExternalStorage();
                    }
                });
                profileDialog.logOut().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signOutActivity();
                    }
                });
            }
        });
        getUserData();
        return super.onCreateOptionsMenu(menu);
    }

    private void getUserData() {
        //Retrieve data from firebase related to user id
        if (uid != null) {
            loading_Dialog2.showDialog("Loading..");
            databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);

                        //change navigation profile view data
                        View headerView = navigationView.getHeaderView(0);

                        TextView first_Name = headerView.findViewById(R.id.first_Name);
                        first_Name.setText(userModel.firstName);

                        TextView second_Name = headerView.findViewById(R.id.second_Name);
                        second_Name.setText(userModel.lastName);

                        TextView index_No = headerView.findViewById(R.id.index_No);
                        index_No.setText(userModel.indexNo);

                        ImageView profilePicture = headerView.findViewById(R.id.profilePicture);
                        Glide.with(StudentActivity.this)
                                .asBitmap()
                                .load(userModel.img)
                                .into(profilePicture);

                        //change tool bar profile icon
                        Glide.with(StudentActivity.this)
                                .asBitmap()
                                .load(userModel.img)
                                .into(circleImageView);

                        name = userModel.firstName + " " + userModel.lastName;
                        email = userModel.email;
                        index = userModel.indexNo;
                        role = userModel.role;
                        batch = userModel.batch;
                        img = userModel.img;

                        loading_Dialog2.HideDialog();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    //get storage access permissions
    public void checkPermissionForReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (StudentActivity.this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                SelectImage();
            } else {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);
            }

        } else {
            SelectImage();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "permission Granted", Toast.LENGTH_SHORT).show();
                SelectImage();
            } else {
                Toast.makeText(this, "permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Select Image method
    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //check internet connection
        if (!CheckNetwork.isConnected(StudentActivity.this)) {
            CheckNetwork.showNetworkDialog(StudentActivity.this);
        } else {
            // checking request code and result code
            // if request code is PICK_IMAGE_REQUEST and
            // resultCode is RESULT_OK
            // then set image in the image view
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

                // Get the Uri of data
                filePath = data.getData();
                if (filePath != null) {
                    profileDialog.updateImage(filePath.toString());
                    loading_Dialog.showDialog("Please Wait..");
                    //upload image into firebase
                    StorageReference ref = storageReference.child("users").child(uid).child("images/" + UUID.randomUUID().toString());
                    ref.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    //get url of uploaded img
                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            String fileLink = task.getResult().toString();
                                            //updated new url with previous url
                                            databaseReference.child(uid).child("img").setValue(fileLink);
                                        }
                                    });

                                    loading_Dialog.HideDialog();
                                    Toast.makeText(StudentActivity.this, "Profile Image Saved", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loading_Dialog.HideDialog();
                                    Toast.makeText(StudentActivity.this, "Failed to Uploaded " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        }
    }

    //toolbar item selection
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bell:
                Toast.makeText(StudentActivity.this, "Clicked Bell", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //sidebar item selection
    private void sideBarItems() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeIcon:
                        getSupportFragmentManager().beginTransaction().replace(R.id.middleFrame, new Stu_Home_Fragment()).commit();
                        break;
                    case R.id.chat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.middleFrame, new Stu_Chat_Fragment()).commit();
                        break;
                    case R.id.helpLine:
                        break;
                    case R.id.busRoute:
                        Toast.makeText(StudentActivity.this, "Clicked bus", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.settings:
                        break;
                    case R.id.rateUs:
                        Toast.makeText(StudentActivity.this, "Clicked rate", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.aboutUs:
                        Toast.makeText(StudentActivity.this, "Clicked about", Toast.LENGTH_SHORT).show();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(StudentActivity.this, LoginActivity.class));
            finish();
        }
    }

    public void signOutActivity() {
        Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(StudentActivity.this, LoginActivity.class));
        FirebaseAuth.getInstance().signOut();
        Preferences.clearData(this);
        finish();
    }

    private void initView() {
        firebaseAuth = FirebaseAuth.getInstance();
        tool = findViewById(R.id.customToolbarStudent);
        drawerLayout = findViewById(R.id.drawerLayerStudent);
        navigationView = findViewById(R.id.nav_view_student);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        navController = Navigation.findNavController(this, R.id.middleFrame);

        uid = firebaseAuth.getCurrentUser().getUid();
        contentView = findViewById(R.id.content2);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("users");
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

}