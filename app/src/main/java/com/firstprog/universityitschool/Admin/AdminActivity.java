package com.firstprog.universityitschool.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firstprog.universityitschool.Admin.Fragment.Fragment_Chat;
import com.firstprog.universityitschool.Admin.Fragment.Fragment_Help;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Events.Event_Selected_Subject_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Events.Event_Subject_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Events.Events_Add_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Events.Events_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Events.Events_Semester_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Fragment_Home;
import com.firstprog.universityitschool.Admin.Fragment.Fragment_Settings;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.Material_Selected_Subject_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.Material_Subject_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.Materials_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.Materials_Semester_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData.Materials_Week_View_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData.Week_Assignments_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData.Week_Grades_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData.Week_Lessons_PDF_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData.Week_Lessons_Select_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData.Week_Lessons_Video_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData.Week_Notes_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData.Week_PDF_View;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData.Week_Quiz_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData.Week_Quiz_View_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Materials.WeekData.Week_Video_View;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Module.Modules_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Module.Selected_Lecturer_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Module.Semesters_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Module.Subjects_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.News.News_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Payments.Payment_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Payments.Payment_Semester_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Payments.Payment_View_Fragment;
import com.firstprog.universityitschool.Admin.Fragment.Home_Fragment.Users.User_Fragment;
import com.firstprog.universityitschool.Login.LoginActivity;
import com.firstprog.universityitschool.Model.UserModel;
import com.firstprog.universityitschool.Utility.CheckNetwork;
import com.firstprog.universityitschool.Utility.Preferences;
import com.firstprog.universityitschool.R;
import com.firstprog.universityitschool.Utility.LoadingDialog;
import com.firstprog.universityitschool.Utility.ProfileDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String uid;
    private DatabaseReference databaseReference;
    private Toolbar tool;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MenuItem menuItem;
    private View view;
    private CircleImageView circleImageView;
    private static final float END_SCALE = 0.7f;
    private LinearLayout contentView;
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
        setContentView(R.layout.activity_admin);

        initView();
        setSupportActionBar(tool);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationDrawer();

        //set home screen as default if nothing in saved
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayer, new Fragment_Home()).commit();
            navigationView.setCheckedItem(R.id.homeIcon);
        }

        sideBarItems();

        profileDialog = new ProfileDialog(AdminActivity.this);
        loading_Dialog = new LoadingDialog(AdminActivity.this);
        loading_Dialog2 = new LoadingDialog(AdminActivity.this);
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
                        Glide.with(AdminActivity.this)
                                .asBitmap()
                                .load(userModel.img)
                                .into(profilePicture);

                        //change tool bar profile icon
                        Glide.with(AdminActivity.this)
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
            if (AdminActivity.this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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
        if (!CheckNetwork.isConnected(AdminActivity.this)) {
            CheckNetwork.showNetworkDialog(AdminActivity.this);
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
                                    Toast.makeText(AdminActivity.this, "Profile Image Saved", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loading_Dialog.HideDialog();
                                    Toast.makeText(AdminActivity.this, "Failed to Uploaded " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AdminActivity.this, "Clicked Bell", Toast.LENGTH_SHORT).show();
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
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayer, new Fragment_Home()).commit();
                        break;
                    case R.id.chat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayer, new Fragment_Chat()).commit();
                        break;
                    case R.id.helpLine:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayer, new Fragment_Help()).commit();
                        break;
                    case R.id.busRoute:
                        Toast.makeText(AdminActivity.this, "Clicked bus", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayer, new Fragment_Settings()).commit();
                        break;
                    case R.id.rateUs:
                        Toast.makeText(AdminActivity.this, "Clicked rate", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.aboutUs:
                        Toast.makeText(AdminActivity.this, "Clicked about", Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(AdminActivity.this, LoginActivity.class));
            finish();
        }
    }

    public void signOutActivity() {
        Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AdminActivity.this, LoginActivity.class));
        FirebaseAuth.getInstance().signOut();
        Preferences.clearData(this);
        finish();
    }

    private void initView() {
        firebaseAuth = FirebaseAuth.getInstance();
        tool = findViewById(R.id.customToolbar);
        drawerLayout = findViewById(R.id.drawerLayer);
        navigationView = findViewById(R.id.nav_view);
        uid = firebaseAuth.getCurrentUser().getUid();
        contentView = findViewById(R.id.content);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance("https://itschool-auth-default-rtdb.firebaseio.com").getReference("users");
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        if(User_Fragment.backListener!=null){
            User_Fragment.backListener.onBackPressed();
        } else if(Modules_Fragment.backListener!=null){
            Modules_Fragment.backListener.onBackPressed();
        } else if(Semesters_Fragment.backListener!=null){
            Semesters_Fragment.backListener.onBackPressed();
        } else if(Subjects_Fragment.backListener!=null){
            Subjects_Fragment.backListener.onBackPressed();
        } else if(Selected_Lecturer_Fragment.backListener!=null){
            Selected_Lecturer_Fragment.backListener.onBackPressed();
        } else if(Materials_Fragment.backListener!=null){
            Materials_Fragment.backListener.onBackPressed();
        } else if(Materials_Semester_Fragment.backListener!=null){
            Materials_Semester_Fragment.backListener.onBackPressed();
        } else if(Material_Subject_Fragment.backListener!=null){
            Material_Subject_Fragment.backListener.onBackPressed();
        } else if(Material_Selected_Subject_Fragment.backListener!=null){
            Material_Selected_Subject_Fragment.backListener.onBackPressed();
        } else if(Materials_Week_View_Fragment.backListener!=null){
            Materials_Week_View_Fragment.backListener.onBackPressed();
        } else if(Week_Notes_Fragment.backListener!=null){
            Week_Notes_Fragment.backListener.onBackPressed();
        } else if(Week_Lessons_Select_Fragment.backListener!=null){
            Week_Lessons_Select_Fragment.backListener.onBackPressed();
        } else if(Week_Lessons_PDF_Fragment.backListener!=null){
            Week_Lessons_PDF_Fragment.backListener.onBackPressed();
        } else if(Week_PDF_View.backListener!=null){
            Week_PDF_View.backListener.onBackPressed();
        } else if(Week_Lessons_Video_Fragment.backListener!=null){
            Week_Lessons_Video_Fragment.backListener.onBackPressed();
        } else if(Week_Video_View.backListener!=null){
            Week_Video_View.backListener.onBackPressed();
        } else if(Week_Assignments_Fragment.backListener!=null){
            Week_Assignments_Fragment.backListener.onBackPressed();
        } else if(Week_Grades_Fragment.backListener!=null){
            Week_Grades_Fragment.backListener.onBackPressed();
        } else if(Week_Quiz_Fragment.backListener!=null){
            Week_Quiz_Fragment.backListener.onBackPressed();
        } else if(Week_Quiz_View_Fragment.backListener!=null){
            Week_Quiz_View_Fragment.backListener.onBackPressed();
        } else if(Events_Fragment.backListener!=null){
            Events_Fragment.backListener.onBackPressed();
        } else if(Events_Semester_Fragment.backListener!=null){
            Events_Semester_Fragment.backListener.onBackPressed();
        }  else if(Event_Subject_Fragment.backListener!=null){
            Event_Subject_Fragment.backListener.onBackPressed();
        }  else if(Event_Selected_Subject_Fragment.backListener!=null){
            Event_Selected_Subject_Fragment.backListener.onBackPressed();
        }  else if(Events_Add_Fragment.backListener!=null){
            Events_Add_Fragment.backListener.onBackPressed();
        }  else if(News_Fragment.backListener!=null){
            News_Fragment.backListener.onBackPressed();
        }   else if(Payment_Fragment.backListener!=null){
            Payment_Fragment.backListener.onBackPressed();
        }  else if(Payment_Semester_Fragment.backListener!=null){
            Payment_Semester_Fragment.backListener.onBackPressed();
        }   else if(Payment_View_Fragment.backListener!=null){
            Payment_View_Fragment.backListener.onBackPressed();
        } else {
            finish();
        }
    }
}

