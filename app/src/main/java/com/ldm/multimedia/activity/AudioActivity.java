package com.ldm.multimedia.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ldm.multimedia.BaseActivity;
import com.ldm.multimedia.R;

/**
 * @author ldm
 * @description 音频部分界面
 * @time 2017/4/14 11:32
 */
public class AudioActivity extends BaseActivity {
    private Button btn_am;
    private Button btn_mr;
    private Button btn_ar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        this.btn_am = (Button) findViewById(R.id.btn_am);
        this.btn_am.setOnClickListener(this);
        this.btn_mr = (Button) findViewById(R.id.btn_mr);
        this.btn_mr.setOnClickListener(this);
        this.btn_ar = (Button) findViewById(R.id.btn_ar);
        this.btn_ar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_am:
                showActivity(this, AudioManagerActivity.class);
                break;
            case R.id.btn_mr:
                showActivity(this, MediaRecorderActivity.class);
                break;
            case R.id.btn_ar:
                showActivity(this, AudioRecordActivity.class);
                break;
        }

    }
}
