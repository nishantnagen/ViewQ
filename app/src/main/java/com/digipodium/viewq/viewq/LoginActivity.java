package com.digipodium.viewq.viewq;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class LoginActivity extends AppCompatActivity {

    public boolean isFirstStart;

    private EditText edt1;
    private EditText edt2;
    private Button btn3;
    private Button btn1;
    private FirebaseAuth mAuth;
    private Button btn2;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginuser();
            }
        });
        btn2 = findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent yo = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(yo);
                overridePendingTransition(R.anim.fade_in,R.anim.shake);
                finish();
            }
        });
        btn3 = findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent yo6 = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(yo6);
                overridePendingTransition(R.anim.right_enter, R.anim.right_out);
                finish();
            }
        });
        edt1 = findViewById(R.id.edt1);
        edt2 = findViewById(R.id.edt2);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                isFirstStart = getSharedPreferences.getBoolean("firstStart", true);

                if (isFirstStart) {

                    Intent i = new Intent(LoginActivity.this, SliderIntro.class);
                    overridePendingTransition(R.anim.bottom_to_up, R.anim.up_to_bottom);
                    startActivity(i);
                    SharedPreferences.Editor e = getSharedPreferences.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                }
            }
        });
        t.start();
    }

    private void loginuser() {
        showDialog("logging you in..... please wait...");
        String email = edt1.getText().toString();
        String password = edt2.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "invalid credential", Toast.LENGTH_SHORT).show();
            hideDialog();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            hideDialog();
                            if (task.isSuccessful()) {
                                updateUI(task.getResult().getUser());
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
 
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            if (!user.isEmailVerified()) {

            } else {
                Intent i = new Intent(LoginActivity.this, CameraActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.up_to_bottom2, R.anim.bottom_to_up);
                finish();
            }
        }
    }

    public void showDialog(String message) {
        dialog = new ProgressDialog(this);
        dialog.setMessage(message);
        dialog.show();
    }

    public void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        updateUI(currentUser);

    }
}


