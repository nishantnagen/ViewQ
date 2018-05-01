package com.digipodium.viewq.viewq;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Camera.AutoFocusCallback;

import com.bumptech.glide.Glide;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.EasyPermissions;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class CameraActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {


    Glide glide;
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar = null;
    private boolean isLighOn = false;
    private Button btnflashlight;
    private FirebaseAuth mAuth;
    private SurfaceHolder mHolder;
    private Camera mCamera;

    private String TAG = "Camera Activity";
    private CameraPreview mPreview;
    private final int REQUEST_PERMISSION_STATE = 1;

    private CircleImageView capturedPic;
    private Camera.Parameters parameters;
    private String folder;
    private FrameLayout preview;
    private File pictureFile;
    private boolean isPermitted = false;
    private SharedPreferences setting;

    @Override
    protected void onStop() {
        super.onStop();

        if (mCamera != null) {
            mCamera.release();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mAuth = FirebaseAuth.getInstance();
        mCamera = getCameraInstance();

        boolean state = EasyPermissions.hasPermissions(this,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.VIBRATE);
        if (!state) {
            EasyPermissions.requestPermissions(this, "Camera Permissions", 23,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.VIBRATE);
        } else {
            isPermitted = true;
            if (!checkCameraHardware(this)) {
                Toast.makeText(this, "this app is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
        }


    /*    View headerView = navigationView.getHeaderView(0);
        TextView txtEmail = headerView.findViewById(R.id.txtEmail);
        TextView txtName = headerView.findViewById(R.id.txtName);
        CircleImageView ivProfilePic = headerView.findViewById(R.id.ivProfilePic1);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            txtEmail.setText(user.getEmail());
            updateHeader(user.getUid(), txtName, ivProfilePic);
        }*/
    }

  /*  private void updateHeader(String uid, final TextView txtName, final CircleImageView ivProfilePic) {
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);
                String url = dataSnapshot.child("pic").getValue(String.class);

                txtName.setText(username);
                Glide.with(CameraActivity.this)
                        .load(url)
                        .into(ivProfilePic);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
*/
    @Override
    protected void onResume() {
        super.onResume();
        if (isPermitted) {
            preview = findViewById(R.id.camera_preview);
            capturedPic = findViewById(R.id.capturedPic);
            capturedPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pictureFile != null) {
                        Uri uri = Uri.fromFile(pictureFile);
                        Intent i = new Intent(CameraActivity.this, ThumbnailActivity.class);
                        i.setData(uri);
                        startActivity(i);
                    }
                }
            });
            btnflashlight = findViewById(R.id.buttonFlashlight);
            btnflashlight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    if (isLighOn) {

                        Log.i("info", "torch is turn off!");

                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(parameters);
                        mCamera.startPreview();
                        isLighOn = false;

                    } else {

                        Log.i("info", "torch is turn on!");

                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

                        mCamera.setParameters(parameters);
                        mCamera.startPreview();
                        isLighOn = true;
                    }
                }
            });

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            camera_code();
        } else {
            Toast.makeText(this, "restart app", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            mPreview = null;
        }
    }

    private void camera_code() {
        mCamera = getCameraInstance();

        mPreview = new CameraPreview(this, mCamera);
        preview.addView(mPreview);

        final Camera.PictureCallback mPicture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (pictureFile == null) {
                    Toast.makeText(CameraActivity.this, "error saving image", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error creating media file, check storage permissions: ");
                    return;

                }
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    Toast.makeText(CameraActivity.this, "success", Toast.LENGTH_SHORT).show();
                    Glide.with(CameraActivity.this).load(Uri.fromFile(pictureFile)).into(capturedPic);

                    mCamera.startPreview();
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());//} catch (IOException e) {
                }
            }
        };

        Button button_capture = findViewById(R.id.button_capture);
        button_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    mCamera.takePicture(null, null, mPicture);

                } catch (Exception e) {
                    Toast.makeText(CameraActivity.this, "check logcat", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        isPermitted = true;
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        isPermitted = false;

    }

    class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {


        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            mCamera.setDisplayOrientation(90);

            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mCamera != null) {
                mCamera.stopPreview();

                mCamera.release();
                mCamera = null;
            }

        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

            mCamera.autoFocus(new AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        camera.cancelAutoFocus();//  Only add to this sentence ， Automatic focus
                        doAutoFocus();
                    }
                }
            });
        }
    }

    private File getOutputMediaFile(int typeImage) {
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        String f = setting.getString("location", "viewQ");
        folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile() + "/" + f;
        new File(folder).mkdir();
        File file = new File(folder, "IMG" + System.currentTimeMillis() + ".jpg");
        //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,pictureFile);
        // File image_path="your image path";
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        return file;
    }

    private void doAutoFocus() {
        parameters = mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(parameters);
        mCamera.autoFocus(new AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    camera.cancelAutoFocus();//  Only add to this sentence ， Automatic focus 。
                    if (!Build.MODEL.equals("KORIDY H30")) {
                        parameters = camera.getParameters();
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1 Continuous focus
                        camera.setParameters(parameters);
                    } else {
                        parameters = camera.getParameters();
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        camera.setParameters(parameters);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        switch (requestCode) {
            case REQUEST_PERMISSION_STATE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(CameraActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CameraActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.log_out, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            logout();
            Toast.makeText(this, "logout successful", Toast.LENGTH_SHORT).show();
            finish();
        }
        return super.onOptionsItemSelected(item);
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
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_camera:
                /**  Intent i = new Intent(CameraActivity.this, CameraActivity.class);
                 startActivity(i);
                 overridePendingTransition(R.anim.up_to_bottom2, R.anim.bottom_to_up);
                 finish();**/
                break;
            case R.id.nav_profile:
                Intent k = new Intent(CameraActivity.this, ProfileActivity.class);
                startActivity(k);
                overridePendingTransition(R.anim.fade_in, R.anim.bottom_to_up);
                break;
            case R.id.nav_setting:
                Intent l = new Intent(CameraActivity.this, SettingActivity.class);
                startActivity(l);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.nav_rating: {
                final String appPackageName = getPackageName();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + appPackageName));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "google play sore is not installed", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}