package com.digipodium.viewq.viewq;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int REQUEST_IMAGE_GET = 78;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDb;
    private FirebaseStorage mStorage;
    private String uid;
    private CircleImageView ivProfilePic;
    private boolean isImageUploaded = false;
    private Uri fullPhotoUri;
    private Uri imageUrl;
    private EditText etPhone;
    private EditText etPhone1;
    private EditText etPhone2;
    private EditText etPhone4;
    private EditText etPhone3;
    public TextView mTextMessage;
    public ImageView header_cover_image;
    public RelativeLayout profile_layout;
    public TextView user_profile_name;
    public TextView user_profile_short_bio;
    public ImageButton imgbtn_save;
    private Calendar mCalender;
    public TextView txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            finish();
        }
        uid = currentUser.getUid();

        mTextMessage = findViewById(R.id.message);
        header_cover_image = findViewById(R.id.header_cover_image);
        profile_layout = findViewById(R.id.profile_layout);
        user_profile_name = findViewById(R.id.user_profile_name);

        user_profile_short_bio = findViewById(R.id.user_profile_short_bio);

        imgbtn_save = findViewById(R.id.imgbtn_save);
        imgbtn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileInfo();
                Toast.makeText(ProfileActivity.this, "save successfull", Toast.LENGTH_SHORT).show();
                Intent yo5 = new Intent(ProfileActivity.this, CameraActivity.class);
                startActivity(yo5);
                finish();
            }
        });
        etPhone = findViewById(R.id.etPhone);
        etPhone1 = findViewById(R.id.etPhone1);
        mCalender = Calendar.getInstance();
        etPhone2 = findViewById(R.id.etPhone2);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalender.set(Calendar.YEAR, year);
                mCalender.set(Calendar.MONTH, month);
                mCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        etPhone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ProfileActivity.this, date, mCalender.get(Calendar.YEAR), mCalender.get(Calendar.MONTH),
                        mCalender.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        etPhone3 = findViewById(R.id.etPhone3);
        etPhone4 = findViewById(R.id.etPhone4);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mDb.getReference().child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);
                String Password = dataSnapshot.child("Password").getValue(String.class);
                String DOB = dataSnapshot.child("DOB").getValue(String.class);
                String mob_no = dataSnapshot.child("mob_no").getValue(String.class);
                String Address = dataSnapshot.child("Address").getValue(String.class);
                String url = dataSnapshot.child("pic").getValue(String.class);

                etPhone.setText(username);
                user_profile_name.setText(username);
                etPhone1.setText(Password);
                etPhone2.setText(DOB);
                etPhone3.setText(mob_no);
                etPhone4.setText(Address);
                Glide.with(ProfileActivity.this)
                        .load(url)
                        .into(ivProfilePic);

                if (currentUser.getEmail() != null) {
                    user_profile_short_bio.setText(currentUser.getEmail());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

       /* View headerView = navigationView.getHeaderView(0);
        TextView txtEmail = headerView.findViewById(R.id.txtEmail);
        TextView txtName = headerView.findViewById(R.id.txtName);
        CircleImageView ivProfilePic = headerView.findViewById(R.id.ivProfilePic1);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            txtEmail.setText(user.getEmail());
            updateHeader(user.getUid(), txtName, ivProfilePic);
        }*/
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etPhone2.setText(sdf.format(mCalender.getTime()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            fullPhotoUri = data.getData();
            Glide.with(this).load(fullPhotoUri).into(ivProfilePic);
            uploadToStorage();
        }
    }

    private void uploadToStorage() {
        mStorage.getReference().child(uid).putFile(fullPhotoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task != null) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult().getDownloadUrl();
                        if (downloadUrl != null) {
                            Toast.makeText(ProfileActivity.this, "image successfully uploaded", Toast.LENGTH_SHORT).show();
                            imageUrl = downloadUrl;

                            mDb.getReference().child("users").child(uid).child("pic").setValue(imageUrl.toString());
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (!user.isEmailVerified()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.log_out, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            Toast.makeText(this, "logout successful", Toast.LENGTH_SHORT).show();
            finish();
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void saveProfileInfo() {
        String username = etPhone.getText().toString();
        String Password = etPhone1.getText().toString();
        String DOB = etPhone2.getText().toString();
        String mob_no = etPhone3.getText().toString();
        String Address = etPhone4.getText().toString();
        if (username.isEmpty() || Password.isEmpty() || DOB.isEmpty() || mob_no.isEmpty() || Address.isEmpty()) {
            Toast.makeText(this, "fill all details before save", Toast.LENGTH_SHORT).show();

        }
        if (username.isEmpty()) {
            Toast.makeText(this, "fill username before save", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short,enter minimum 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (DOB.isEmpty()) {
            Toast.makeText(this, "fill DOB before save", Toast.LENGTH_SHORT).show();
            return;

        }
        if (mob_no.length() < 10) {
            Toast.makeText(this, "enter valid mob no.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Address.isEmpty()) {
            Toast.makeText(this, "fill Address before save", Toast.LENGTH_SHORT).show();
            return;
        }
        //length
        mDb.getReference().child("users").child(uid).child("username").setValue(username);
        mDb.getReference().child("users").child(uid).child("Password").setValue(Password);
        mDb.getReference().child("users").child(uid).child("DOB").setValue(DOB);
        mDb.getReference().child("users").child(uid).child("mob_no").setValue(mob_no);
        mDb.getReference().child("users").child(uid).child("Address").setValue(Address);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_camera:
                Intent i = new Intent(ProfileActivity.this, CameraActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.up_to_bottom2, R.anim.bottom_to_up);
                finish();
                break;
            case R.id.nav_profile:
                /**  Intent k = new Intent(ProfileActivity.this, ProfileActivity.class);
                 startActivity(k);
                 finish();**/
                break;
            case R.id.nav_setting:
                Intent l = new Intent(ProfileActivity.this, SettingActivity.class);
                startActivity(l);
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                finish();
                break;
            case R.id.nav_rating:
            {
                final String appPackageName = getPackageName();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id="+ appPackageName));
                if (intent.resolveActivity(getPackageManager())!=null){
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    startActivity(intent);
                }else {
                    Toast.makeText(this, "google play sore is not installed", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
