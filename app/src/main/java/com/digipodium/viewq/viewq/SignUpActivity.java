package com.digipodium.viewq.viewq;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private EditText edt3;
    private EditText edt4;
    private EditText edt5;
    private Button btn4;
    private Button btn5;
    private FirebaseAuth mAuth;
    private CheckBox chck2;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        edt3 = findViewById(R.id.edt3);
        edt4 = findViewById(R.id.edt4);
        edt5 = findViewById(R.id.edt5);
        chck2 = findViewById(R.id.chck2);
        btn4 = findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chck2.isChecked()){
                }else{
                    Toast.makeText(getApplicationContext(), "accept our terms and conditions", Toast.LENGTH_SHORT).show();
                    return;
                }
                registerUser();
            }
        });
        btn5 = findViewById(R.id.btn5);
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent yo1 = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(yo1);
                overridePendingTransition(R.anim.fade_in,R.anim.shake);
                finish();
            }
        });
    }

    private void registerUser() {
        showDialog("registerning you in......please wait");
        String email = edt3.getText().toString();
        String password = edt4.getText().toString();
        String confirmpassword = edt5.getText().toString();
        if (email.isEmpty() || password.isEmpty() || confirmpassword.isEmpty()) {
            Toast.makeText(this, "fill all details", Toast.LENGTH_SHORT).show();
            hideDialog();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short,enter minimum 6 characters", Toast.LENGTH_SHORT).show();
            hideDialog();
            return;
        }
        if (confirmpassword.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short,enter minimum 6 characters", Toast.LENGTH_SHORT).show();
            hideDialog();
            return;
        }
        if (!password.equals(confirmpassword)){
            Toast.makeText(this, "password didn't matched", Toast.LENGTH_SHORT).show();
            hideDialog();
            return;
        }
                mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "sign up succesfull", Toast.LENGTH_SHORT).show();
                            updateUI(task.getResult().getUser());
                            hideDialog();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(SignUpActivity.this, "already registered with this email", Toast.LENGTH_SHORT).show();
                            hideDialog();
                        }
                    }
                });
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            if (!user.isEmailVerified()) {
                user.sendEmailVerification();
                Toast.makeText(this, "verification mail sent", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.fade_in,R.anim.shake);
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
}