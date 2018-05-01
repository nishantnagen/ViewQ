package com.digipodium.viewq.viewq;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class ThumbnailActivity extends AppCompatActivity {

    private LinearLayout layout1;
    private LinearLayout layout2;
    private LinearLayout layout3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumbnail);

        final Uri imageUri = getIntent().getData();

        layout1 = findViewById(R.id.layout1);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ThumbnailActivity.this, ObjectDetectionActivity.class);
                i.setData(imageUri);
                startActivity(i);
            }
        });
        layout2 = findViewById(R.id.layout2);
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ThumbnailActivity.this, FaceDetectionActivity.class);
                i.setData(imageUri);
                startActivity(i);
            }
        });

        layout3 = findViewById(R.id.layout3);
        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ThumbnailActivity.this, TextDetectionActivity.class);
                i.setData(imageUri);
                startActivity(i);
            }
        });
    }
}

