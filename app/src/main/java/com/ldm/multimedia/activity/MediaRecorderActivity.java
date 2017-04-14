package com.ldm.multimedia.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.ldm.multimedia.BaseActivity;
import com.ldm.multimedia.Constant;
import com.ldm.multimedia.R;
import com.ldm.multimedia.adapter.AudioAdapter;
import com.ldm.multimedia.model.FileBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * description：录音MediaRecorder
 * 作者：ldm
 * 时间：20172017/4/10 15:39
 * 邮箱：1786911211@qq.com
 */
public class MediaRecorderActivity extends BaseActivity {
    private Button start_tv;
    private ListView listView;
    //线程操作
    private ExecutorService mExecutorService;
    //录音API
    private MediaRecorder mMediaRecorder;
    //录音开始时间与结束时间
    private long startTime, endTime;
    //录音所保存的文件
    private File mAudioFile;
    //文件列表数据
    private List<FileBean> dataList;
    //录音文件数据列表适配器
    private AudioAdapter mAudioAdapter;
    //录音文件保存位置
    private String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio/";
    //当前是否正在播放
    private volatile boolean isPlaying;
    //播放音频文件API
    private MediaPlayer mediaPlayer;
    //使用Handler更新UI线程
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.RECORD_SUCCESS:
                    //录音成功，展示数据
                    if (null == mAudioAdapter) {
                        mAudioAdapter = new AudioAdapter(MediaRecorderActivity.this, dataList, R.layout.file_item_layout);
                    }
                    listView.setAdapter(mAudioAdapter);
                    break;
                //录音失败
                case Constant.RECORD_FAIL:
                    showToastMsg(getString(R.string.record_fail));
                    break;
                //录音时间太短
                case Constant.RECORD_TOO_SHORT:
                    showToastMsg(getString(R.string.time_too_short));
                    break;
                case Constant.PLAY_COMPLETION:
                    showToastMsg(getString(R.string.play_over));
                    break;
                case Constant.PLAY_ERROR:
                    showToastMsg(getString(R.string.play_error));
                    break;

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediarecorder);
        //录音及播放要使用单线程操作
        mExecutorService = Executors.newSingleThreadExecutor();
        dataList = new ArrayList<>();
        initViews();
        initEvents();
    }


    private void initViews() {
        this.start_tv = (Button) findViewById(R.id.start_tv);
        this.listView = (ListView) findViewById(R.id.listview);
    }


    private void initEvents() {
        //类似微信等应用按住说话进行录音，所以用OnTouch事件
        this.start_tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    //按下操作
                    case MotionEvent.ACTION_DOWN:
                        //安卓6.0以上录音相应权限处理
                        if (Build.VERSION.SDK_INT > 22) {
                            permissionForM();
                        } else {
                            startRecord();
                        }
                        break;
                    //松开操作
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        stopRecord();
                        break;
                }
                //对OnTouch事件做了处理，返回true
                return true;
            }
        });
        //点击播放对应的录音文件
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //使用MediaPlayer播放声音文件
                startPlay(dataList.get(i).getFile());
            }
        });
    }


    /**
     * @description 开始进行录音
     * @author ldm
     * @time 2017/2/9 9:18
     */
    private void startRecord() {
        start_tv.setText(R.string.stop_by_up);
        start_tv.setBackgroundResource(R.drawable.bg_gray_round);
        //异步任务执行录音操作
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                //播放前释放资源
                releaseRecorder();
                //执行录音操作
                recordOperation();
            }
        });
    }

    /**
     * @description 录音失败处理
     * @author ldm
     * @time 2017/2/9 9:35
     */
    private void recordFail() {
        mAudioFile = null;
        mHandler.sendEmptyMessage(Constant.RECORD_FAIL);
    }

    /**
     * @description 录音操作
     * @author ldm
     * @time 2017/2/9 9:34
     */
    private void recordOperation() {
        //创建MediaRecorder对象
        mMediaRecorder = new MediaRecorder();
        //创建录音文件,.m4a为MPEG-4音频标准的文件的扩展名
        mAudioFile = new File(mFilePath + System.currentTimeMillis() + ".m4a");
        //创建父文件夹
        mAudioFile.getParentFile().mkdirs();
        try {
            //创建文件
            mAudioFile.createNewFile();
            //配置mMediaRecorder相应参数
            //从麦克风采集声音数据
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置保存文件格式为MP4
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            //设置采样频率,44100是所有安卓设备都支持的频率,频率越高，音质越好，当然文件越大
            mMediaRecorder.setAudioSamplingRate(44100);
            //设置声音数据编码格式,音频通用格式是AAC
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            //设置编码频率
            mMediaRecorder.setAudioEncodingBitRate(96000);
            //设置录音保存的文件
            mMediaRecorder.setOutputFile(mAudioFile.getAbsolutePath());
            //开始录音
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            //记录开始录音时间
            startTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
            recordFail();
        }
    }


    /**
     * @description 结束录音操作
     * @author ldm
     * @time 2017/2/9 9:18
     */
    private void stopRecord() {
        start_tv.setText(R.string.speak_by_press);
        start_tv.setBackgroundResource(R.drawable.bg_white_round);
        //停止录音
        mMediaRecorder.stop();
        //记录停止时间
        endTime = System.currentTimeMillis();
        //录音时间处理，比如只有大于2秒的录音才算成功
        int time = (int) ((endTime - startTime) / 1000);
        if (time >= 3) {
            //录音成功,添加数据
            FileBean bean = new FileBean();
            bean.setFile(mAudioFile);
            bean.setFileLength(time);
            dataList.add(bean);
            //录音成功,发Message
            mHandler.sendEmptyMessage(Constant.RECORD_SUCCESS);
        } else {
            mAudioFile = null;
            mHandler.sendEmptyMessage(Constant.RECORD_TOO_SHORT);
        }
        //录音完成释放资源
        releaseRecorder();
    }

    /**
     * @description 翻放录音相关资源
     * @author ldm
     * @time 2017/2/9 9:33
     */
    private void releaseRecorder() {
        if (null != mMediaRecorder) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //页面销毁，线程要关闭
        mExecutorService.shutdownNow();
    }
    /*******6.0以上版本手机权限处理***************************/
    /**
     * @description 兼容手机6.0权限管理
     * @author ldm
     * @time 2016/5/24 14:59
     */
    private void permissionForM() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constant.PERMISSIONS_REQUEST_FOR_AUDIO);
        } else {
            startRecord();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == Constant.PERMISSIONS_REQUEST_FOR_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecord();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * @description 播放音频
     * @author ldm
     * @time 2017/2/9 16:54
     */
    private void playAudio(final File mFile) {
        if (null != mFile && !isPlaying) {
            isPlaying = true;
            mExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    startPlay(mFile);
                }
            });
        }
    }

    /**
     * @description 开始播放音频文件
     * @author ldm
     * @time 2017/2/9 16:56
     */
    private void startPlay(File mFile) {
        try {
            //初始化播放器
            mediaPlayer = new MediaPlayer();
            //设置播放音频数据文件
            mediaPlayer.setDataSource(mFile.getAbsolutePath());
            //设置播放监听事件
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //播放完成
                    playEndOrFail(true);
                }
            });
            //播放发生错误监听事件
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    playEndOrFail(false);
                    return true;
                }
            });
            //播放器音量配置
            mediaPlayer.setVolume(1, 1);
            //是否循环播放
            mediaPlayer.setLooping(false);
            //准备及播放
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            //播放失败正理
            playEndOrFail(false);
        }

    }

    /**
     * @description 停止播放或播放失败处理
     * @author ldm
     * @time 2017/2/9 16:58
     */
    private void playEndOrFail(boolean isEnd) {
        isPlaying = false;
        if (isEnd) {
            mHandler.sendEmptyMessage(Constant.PLAY_COMPLETION);
        } else {
            mHandler.sendEmptyMessage(Constant.PLAY_ERROR);
        }
        if (null != mediaPlayer) {
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnErrorListener(null);
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

