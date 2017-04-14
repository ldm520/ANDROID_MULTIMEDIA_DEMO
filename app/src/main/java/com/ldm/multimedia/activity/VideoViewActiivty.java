package com.ldm.multimedia.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.ldm.multimedia.BaseActivity;
import com.ldm.multimedia.R;

/**
 * description：使用VideoView播放视频
 * 作者：ldm
 * 时间：20172017/4/13 15:34
 * 邮箱：1786911211@qq.com
 */
public class VideoViewActiivty extends BaseActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    //视频播放控件VideoView
    private VideoView videoview;
    //视频路径
    private static final String videoPath = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);
        initViews();
    }

    private void initViews() {
        this.videoview = (VideoView) findViewById(R.id.videoview);
        //设备系统样式的播放控件器（通常项目中是自定义）
        videoview.setMediaController(new MediaController(this));
        //设置播放准备（缓冲监听）
        videoview.setOnPreparedListener(this);
        //设置播放完成监听
        videoview.setOnCompletionListener(this);
        //设置播放发生错误监听
        videoview.setOnErrorListener(this);
        //加载uri所对应的视频
        videoview.setVideoURI(Uri.parse(videoPath));
        //加载视频文件路径
        // videoview.setVideoPath(videoPath);
        videoview.start();
        videoview.requestFocus();
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * @description 播放完成处理
     * @author ldm
     * @time 2017/4/13 15:43
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //播放完成处理
    }

    /**
     * @description 播放发生错误处理
     * @author ldm
     * @time 2017/4/13 15:43
     */
    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        //错误提示或处理
        return false;
    }

    /**
     * @description 播放缓冲完成监听
     * @author ldm
     * @time 2017/4/13 15:44
     */
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //取消加载框等操作
    }
}
