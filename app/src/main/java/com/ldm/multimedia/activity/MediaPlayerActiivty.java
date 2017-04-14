package com.ldm.multimedia.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.ldm.multimedia.BaseActivity;
import com.ldm.multimedia.R;

import java.io.File;

/**
 * description：使用MediaPlayer播放视频(音频也可以播放)
 * 作者：ldm
 * 时间：20172017/4/13 16:47
 * 邮箱：1786911211@qq.com
 */
public class MediaPlayerActiivty extends BaseActivity {
    /* 功能按钮 */
    private Button btn_play, btn_pause, btn_stop, btn_low, btn_height;
    /* SurfaceView */
    private SurfaceView mSurfaceView;
    /* 播放视频对象 */
    private MediaPlayer mediaPlayer;
    /* 系统声音 */
    private AudioManager audioManager;
    /* 记录播放位置 */
    private int position;
    private final static String SAVE_POSITION = "position";
    private String filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏显示
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mediaplayer);
        filePath = getIntent().getStringExtra("filePath");
        initViews();
        initListeners();
        mediaPlayer = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        setSurfaceView();
    }

    // 处理横竖屏切换
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            // 如果在播放的时候切换屏幕则保存当前观看的位置
            outState.putInt(SAVE_POSITION, mediaPlayer.getCurrentPosition());
        }
        super.onSaveInstanceState(outState);
    }


    // 横竖屏切换后的处理
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SAVE_POSITION)) {
            // 取得切换屏幕时保存的位置
            position = savedInstanceState.getInt(SAVE_POSITION);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }


    /* 实例化UI */
    private void initViews() {
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_low = (Button) findViewById(R.id.btn_low);
        btn_height = (Button) findViewById(R.id.btn_hight);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceVIew);
    }


    /* 为5个按钮设置监听 */
    private void initListeners() {
        btn_play.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_low.setOnClickListener(this);
        btn_height.setOnClickListener(this);
    }


    /**
     * @description 视频播放
     * @author ldm
     * @time 2017/4/13 16:47
     */
    private void playMedia() {
        //设置音视频资源（前提是文件存在）
        File file;
        if (!TextUtils.isEmpty(filePath)) {
            file = new File(filePath);
        } else {
            file = new File(Environment.getExternalStorageDirectory(), "test.3gp");
        }
        if (!file.exists()) {
            showToastMsg("多媒体资源文件存在！");
            return;
        }
        //初始化MediaPlayer
        mediaPlayer.reset();
        //设置声音流类型
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            //设置音视频资源
            mediaPlayer.setDataSource(file.getAbsolutePath());
            // 缓冲
            mediaPlayer.prepare();
            // 开始播放
            mediaPlayer.start();
            // 具体位置
            mediaPlayer.seekTo(position);
            // 视频输出到View
            mediaPlayer.setDisplay(mSurfaceView.getHolder());
            // 重置位置为0
            position = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description 对SurfaceView进行处理
     * @author ldm
     * @time 2017/4/13 16:50
     */
    private void setSurfaceView() {
        //设置SurfaceHolder类型
        mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 设置事件，回调函数
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (position > 0) {
                    playMedia();
                    position = 0;
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                //销毁时停止播放，释放资源。不做这个操作，即使退出，还是能听到视频的声音
                mediaPlayer.release();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:// 播放
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    return;
                } else {
                    playMedia();
                }
                break;
            case R.id.btn_pause:// 暂停
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    position = mediaPlayer.getCurrentPosition();
                    mediaPlayer.pause();
                } else {
                    return;
                }
                break;
            case R.id.btn_stop:// 停止
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    position = 0;
                } else {
                    return;
                }
                break;
            case R.id.btn_low:// 调小音量
                // 获取当前的音量
                int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                // 音量>0
                if (volume > 0) {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
                } else {
                    return;
                }
                break;
            case R.id.btn_hight:// 调大音量
                volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                // 音量小于最大音量时
                if (volume < audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
                }
                break;
        }
    }
}
