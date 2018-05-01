package com.digipodium.viewq.viewq;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SettingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private FirebaseAuth mAuth;
    private EditText etLocation;
    private SharedPreferences setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        etLocation = findViewById(R.id.editFolder);
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        String folder = setting.getString("location", "viewQ");
        etLocation.setText(folder);
        Button btn11 = findViewById(R.id.btn11);
        btn11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "logout successfull", Toast.LENGTH_SHORT).show();
                    finish();
                    logout();
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
            String location = etLocation.getText().toString();
            if (location.isEmpty()){
                location="viewQ";
            }
            setting.edit().putString("folder", location).apply();
            super.onBackPressed();
        }
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            // Handle navigation view item clicks here.
            int id=item.getItemId();
            switch(id) {
                case R.id.nav_camera:
                    Intent i = new Intent(SettingActivity.this, CameraActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.up_to_bottom2, R.anim.bottom_to_up);
                    finish();
                    break;
                case R.id.nav_profile:
                    Intent k = new Intent(SettingActivity.this, ProfileActivity.class);
                    startActivity(k);
                    overridePendingTransition(R.anim.fade_in, R.anim.bottom_to_up);
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
               case R.id.nav_setting:
                    break;
            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
