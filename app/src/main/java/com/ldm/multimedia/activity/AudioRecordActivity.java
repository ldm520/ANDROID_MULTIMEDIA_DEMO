package com.ldm.multimedia.activity;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ldm.multimedia.BaseActivity;
import com.ldm.multimedia.R;
import com.ldm.multimedia.utils.FileUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * description：使用AudioRecord录音,AudioTrack播放pcm声音数据流
 * 作者：ldm
 * 时间：20172017/4/14 11:01
 * 邮箱：1786911211@qq.com
 * 参考博客 ：http://blog.csdn.net/chenjie19891104/article/details/6333553
 */
public class AudioRecordActivity extends BaseActivity {
    private TextView stateView;
    private Button btnStart, btnStop, btnPlay, btnFinish;
    private RecordTask recorder;
    private PlayTask player;
    private File audioFile;
    //标记
    private boolean isRecording = true, isPlaying = false;
    //录制频率，单位hz.这里的值注意了，写的不好，可能实例化AudioRecord对象的时候，会出错。
    private int frequence = 8000;
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiorecord);
        initViews();
        //在这里我们创建一个文件，用于保存录制内容
        audioFile = FileUtil.getSaveFile(getPackageName()
                        + File.separator + "video" + File.separator,
                System.currentTimeMillis() + ".pcm");
    }

    private void initViews() {
        stateView = (TextView) this.findViewById(R.id.view_state);
        stateView.setText("准备开始");
        btnStart = (Button) this.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);
        btnStop = (Button) this.findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(this);
        btnPlay = (Button) this.findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(this);
        btnFinish = (Button) this.findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(this);
        btnFinish.setText("停止播放");
        btnStop.setEnabled(false);
        btnPlay.setEnabled(false);
        btnFinish.setEnabled(false);
    }


    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_start:
                //这里启动录制任务
                recorder = new RecordTask();
                recorder.execute();
                break;
            case R.id.btn_stop:
                //停止录制
                this.isRecording = false;
                break;
            case R.id.btn_play:
                player = new PlayTask();
                player.execute();
                break;
            case R.id.btn_finish:
                //完成播放
                this.isPlaying = false;
                break;

        }
    }

    class RecordTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            isRecording = true;
            try {
                //开通输出流到指定的文件
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(audioFile)));
                //根据定义好的几个配置，来获取合适的缓冲大小
                int bufferSize = AudioRecord.getMinBufferSize(frequence, channelConfig, audioEncoding);
                //实例化AudioRecord
                AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, frequence, channelConfig, audioEncoding, bufferSize);
                //定义缓冲
                short[] buffer = new short[bufferSize];
                //开始录制
                record.startRecording();
                int r = 0; //存储录制进度
                //定义循环，根据isRecording的值来判断是否继续录制
                while (isRecording) {
                    //从bufferSize中读取字节，返回读取的short个数
                    int bufferReadResult = record.read(buffer, 0, buffer.length);
                    //循环将buffer中的音频数据写入到OutputStream中
                    for (int i = 0; i < bufferReadResult; i++) {
                        dos.writeShort(buffer[i]);
                    }
                    publishProgress(new Integer(r)); //向UI线程报告当前进度
                    r++; //自增进度值
                }
                //录制结束
                record.stop();
                dos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        //该方法在UI线程中被执行
        protected void onProgressUpdate(Integer... progress) {
            stateView.setText("正在录音中...");
        }

        protected void onPostExecute(Void result) {
            btnStop.setEnabled(false);
            btnStart.setEnabled(true);
            btnPlay.setEnabled(true);
            btnFinish.setEnabled(false);
        }

        protected void onPreExecute() {
            //stateView.setText("正在录制");
            btnStart.setEnabled(false);
            btnPlay.setEnabled(false);
            btnFinish.setEnabled(false);
            btnStop.setEnabled(true);
        }

    }

    class PlayTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            isPlaying = true;
            int bufferSize = AudioTrack.getMinBufferSize(frequence, channelConfig, audioEncoding);
            short[] buffer = new short[bufferSize / 4];
            try {
                //定义输入流，将音频写入到AudioTrack类中，实现播放
                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(audioFile)));
                //实例AudioTrack
                AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, frequence, channelConfig, audioEncoding, bufferSize, AudioTrack.MODE_STREAM);
                //开始播放
                track.play();
                //由于AudioTrack播放的是流，所以，我们需要一边播放一边读取
                while (isPlaying && dis.available() > 0) {
                    int i = 0;
                    while (dis.available() > 0 && i < buffer.length) {
                        buffer[i] = dis.readShort();
                        i++;
                    }
                    //然后将数据写入到AudioTrack中
                    track.write(buffer, 0, buffer.length);

                }

                //播放结束
                track.stop();
                dis.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            btnPlay.setEnabled(true);
            btnFinish.setEnabled(false);
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
        }

        protected void onPreExecute() {

            //stateView.setText("正在播放");
            btnStart.setEnabled(false);
            btnStop.setEnabled(false);
            btnPlay.setEnabled(false);
            btnFinish.setEnabled(true);
        }

    }
}
