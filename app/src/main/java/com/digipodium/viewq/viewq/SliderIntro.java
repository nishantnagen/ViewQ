package com.digipodium.viewq.viewq;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;


public class SliderIntro extends AppIntro {


    @Override
    public void init(Bundle savedInstanceState) {

        //adding the three slides for introduction app you can ad as many you needed
        addSlide(AppIntroSampleSlider.newInstance(R.layout.app_intro1));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.app_intro2));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.app_intro3));

        // Show and Hide Skip and Done buttons
        showStatusBar(false);
        showSkipButton(false);

        // Turn vibration on and set intensity
        // You will need to add VIBRATE permission in Manifest file
        setVibrate(true);
        setVibrateIntensity(80);

        //Add animation to the intro slider
        setDepthAnimation();
    }

    @Override
    public void onSkipPressed() {
        // Do something here when users click or tap on Skip button.
        Toast.makeText(getApplicationContext(),
                getString(R.string.library_appintro_licenseId), Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        overridePendingTransition(R.anim.up_to_bottom, R.anim.bottom_to_up);
        startActivity(i);
    }

    @Override
    public void onNextPressed() {
        overridePendingTransition(R.anim.up_to_bottom, R.anim.bottom_to_up);
        // Do something here when users click or tap on Next button.
    }

    @Override
    public void onDonePressed() {
        overridePendingTransition(R.anim.up_to_bottom, R.anim.bottom_to_up);
        // Do something here when users click or tap tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged() {
        overridePendingTransition(R.anim.up_to_bottom, R.anim.bottom_to_up);
        // Do something here when slide is changed
    }
}