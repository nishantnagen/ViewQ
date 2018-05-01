package com.digipodium.viewq.viewq;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;



public class Splash_ScreenActivity extends AppCompatActivity {

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);

        StartAnimations();

        Handler handler = new Handler();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(Splash_ScreenActivity.this, LoginActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.bottom_to_up, R.anim.up_to_bottom);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
        anim.reset();
        ConstraintLayout l = findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);
    }
}