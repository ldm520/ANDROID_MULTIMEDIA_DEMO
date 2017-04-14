package com.ldm.multimedia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ldm.multimedia.BaseActivity;
import com.ldm.multimedia.R;

public class ImageActivity extends BaseActivity {
    private Button btn_image;
    private Button btn_audio;
    private Button btn_video;
    private Button btn_other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.btn_image = (Button) findViewById(R.id.btn_image);
        this.btn_image.setOnClickListener(this);
        this.btn_audio = (Button) findViewById(R.id.btn_audio);
        this.btn_audio.setOnClickListener(this);
        this.btn_video = (Button) findViewById(R.id.btn_video);
        this.btn_video.setOnClickListener(this);
        this.btn_other = (Button) findViewById(R.id.btn_other);
        this.btn_other.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_image:
                Intent in = new Intent(this, ImageActivity.class);
                break;
            case R.id.btn_audio:
                break;
            case R.id.btn_video:
                break;
            case R.id.btn_other:
                break;


        }

    }
}
