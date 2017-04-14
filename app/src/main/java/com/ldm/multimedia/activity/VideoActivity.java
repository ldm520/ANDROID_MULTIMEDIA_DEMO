package com.ldm.multimedia.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.ldm.multimedia.BaseActivity;
import com.ldm.multimedia.R;

import java.io.File;
 /**
      * @description 视频部分界面
      * @author ldm
      * @time  2017/4/14 11:33
      * @param
   */
public class VideoActivity extends BaseActivity {
    private Button action_btn;
    private Button videoview_btn;
    private Button mediaPlayer_btn;
    private Button record_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        this.action_btn = (Button) findViewById(R.id.action_btn);
        this.action_btn.setOnClickListener(this);
        this.videoview_btn = (Button) findViewById(R.id.videoview_btn);
        this.videoview_btn.setOnClickListener(this);
        this.mediaPlayer_btn = (Button) findViewById(R.id.mediaPlayer_btn);
        this.mediaPlayer_btn.setOnClickListener(this);
        this.record_btn = (Button) findViewById(R.id.record_btn);
        this.record_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_btn://使用手机系统播放器播放视频
                //视频路径，当然也可以是网络视频url
                String filePath = Environment.getExternalStorageDirectory().getPath() + "/ldm_test.mp4";
                //首先要确保手SD卡下有ldm_test.mp4视频文件
                if (!new File(filePath).exists()) {
                    showToastMsg("视频文件不存在");
                    return;
                }
                Uri uri = Uri.parse(filePath);
                //调用系统自带的播放器
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "video/mp4");
                startActivity(intent);
                break;
            case R.id.videoview_btn:
                showActivity(this, VideoViewActiivty.class);
                break;
            case R.id.mediaPlayer_btn:
                showActivity(this, MediaPlayerActiivty.class);
                break;
            case R.id.record_btn:
                showActivity(this,RecordVideoActivity.class);
                break;


        }

    }
}
