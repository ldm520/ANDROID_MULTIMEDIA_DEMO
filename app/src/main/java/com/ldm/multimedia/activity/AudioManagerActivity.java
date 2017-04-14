package com.ldm.multimedia.activity;

import android.app.Service;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ldm.multimedia.BaseActivity;
import com.ldm.multimedia.R;

/**
 * description：AudioManager音量管理
 * 作者：ldm
 * 时间：20172017/4/10 15:39
 * 邮箱：1786911211@qq.com
 */
public class AudioManagerActivity extends BaseActivity {
    private TextView type_tv;
    private TextView volunm_tv;
    private Button btn_up;
    private Button btn_down;
    private Button btn_silence;
    //声音管理类AudioManager
    private AudioManager mAudioManager;
    //当前音量
    private int currentVolume;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiomanager);
        //获得系统的音频对象
        mAudioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        initViews();
        initDatas();
    }

    private void initDatas() {
        //声音模式如 NORMAL（普通）, RINGTONE（铃声）, orIN_CALL（通话）
        int mode = mAudioManager.getMode();
        String modeStr = null;
        switch (mode) {
            case AudioManager.MODE_NORMAL:
                modeStr = "普通模式";
                break;
            case AudioManager.MODE_RINGTONE:
                modeStr = "铃声模式";
                break;
            case AudioManager.MODE_IN_CALL:
                modeStr = "通话模式";
                break;
        }
        type_tv.setText("当前声音模式：" + modeStr);
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volunm_tv.setText("当前音量：" + currentVolume);
    }

    private void initViews() {
        this.type_tv = (TextView) findViewById(R.id.type_tv);
        this.volunm_tv = (TextView) findViewById(R.id.volunm_tv);
        this.btn_up = (Button) findViewById(R.id.btn_up);
        this.btn_up.setOnClickListener(this);
        this.btn_down = (Button) findViewById(R.id.btn_down);
        this.btn_down.setOnClickListener(this);
        this.btn_silence = (Button) findViewById(R.id.btn_silence);
        this.btn_silence.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_up://调高音量
                int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                //如果当前音量小于最大音量，则可以调高
                if (currentVolume < max) {
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 1);
                }
                currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                volunm_tv.setText("当前音量：" + currentVolume);
                break;
            case R.id.btn_down:
                //如果当前音量小于最大于0，则可以调低
                if (currentVolume > 0) {
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 1);
                }
                currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                volunm_tv.setText("当前音量：" + currentVolume);
                break;
            case R.id.btn_silence:
                //只是设置铃声为静音
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                //设置麦克风静音
                mAudioManager.setMicrophoneMute(true);
                break;
        }
    }
    /**
     * 其它常用API
     * //通话音量
     int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL );
     int current = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL );
     //系统音量
     int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM );
     current = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM );
     //铃声音量
     max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_RING);
     current = mAudioManager.getStreamVolume(AudioManager.STREAM_RING );
     //音乐音量
     max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC );
     current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC );
     */
}

