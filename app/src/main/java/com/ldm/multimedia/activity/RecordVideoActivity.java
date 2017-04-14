package com.ldm.multimedia.activity;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.ldm.multimedia.BaseActivity;
import com.ldm.multimedia.R;
import com.ldm.multimedia.utils.FileUtil;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * description：视频录制类
 * 作者：ldm
 * 时间：20172017/4/13 17:13
 * 邮箱：1786911211@qq.com
 */
public class RecordVideoActivity extends BaseActivity implements SurfaceHolder.Callback {
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceHolder holder;
    //录制视频
    private Button video_record_btn;
    //是否已经在录制
    private boolean isRecording = false;
    //音视频录制API:MediaRecorder
    private MediaRecorder mRecorder;
    //相机
    private Camera mCamera = null;
    //相机的尺寸
    private Camera.Size mSize = null;
    //默认后置摄像头
    private int mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
    //手机旋转对应的调整角度
    private static final SparseIntArray orientations = new SparseIntArray();
    //视频文件
    private File file;

    static {
        orientations.append(Surface.ROTATION_0, 90);
        orientations.append(Surface.ROTATION_90, 0);
        orientations.append(Surface.ROTATION_180, 270);
        orientations.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 设置竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 选择支持半透明模式
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_video_recorder);
        initViews();
    }


    private void initViews() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        video_record_btn = (Button) findViewById(R.id.video_record_btn);
        video_record_btn.setOnClickListener(this);
        holder = mSurfaceView.getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        //保持屏幕常亮
        holder.setKeepScreenOn(true);
        holder.addCallback(this);
    }

    /**
     * @description 设置相机
     * @author ldm
     * @time 2017/4/13 17:23
     */
    private void initCamera() {
        //如果是双摄像头
        if (Camera.getNumberOfCameras() == 2) {
            try {
                mCamera = Camera.open(mCameraFacing);
            } catch (Exception e) {
                mCamera = Camera.open();
                e.printStackTrace();
            }
        } else {
            mCamera = Camera.open();
        }

        CameraSizeComparator sizeComparator = new CameraSizeComparator();
        Camera.Parameters parameters = mCamera.getParameters();

        if (mSize == null) {
            //选择合适的预览尺寸
            List<Camera.Size> vSizeList = parameters.getSupportedPreviewSizes();
            //对尺寸进行排序
            Collections.sort(vSizeList, sizeComparator);
            for (int num = 0; num < vSizeList.size(); num++) {
                Camera.Size size = vSizeList.get(num);
                if (size.width >= 800 && size.height >= 480) {
                    this.mSize = size;
                    break;
                }
            }
            mSize = vSizeList.get(0);
            List<String> focusModesList = parameters.getSupportedFocusModes();
            //增加对聚焦模式的判断
            if (focusModesList.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else if (focusModesList.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            mCamera.setParameters(parameters);
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int orientation = orientations.get(rotation);
        mCamera.setDisplayOrientation(orientation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT > 22) {
            permissionForM();
        } else {
            initCamera();
        }
    }

    @Override
    public void onPause() {
        releaseCamera();
        super.onPause();
    }

    /**
     * @description 开始录制
     * @author ldm
     * @time 2017/4/13 17:24
     */
    private void startRecord() {

        if (mRecorder == null) {
            mRecorder = new MediaRecorder(); // 创建MediaRecorder
        }
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.unlock();
            mRecorder.setCamera(mCamera);
        }
        try {
            // 设置音频采集方式
            mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            //设置视频的采集方式
            mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            //设置文件的输出格式
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//aac_adif， aac_adts， output_format_rtp_avp， output_format_mpeg2ts ，webm
            //设置audio的编码格式
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //设置video的编码格式
            mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            //设置录制的视频编码比特率
            mRecorder.setVideoEncodingBitRate(1024 * 1024);
            //设置录制的视频帧率,注意文档的说明:
            mRecorder.setVideoFrameRate(30);
            //设置要捕获的视频的宽度和高度
            mSurfaceHolder.setFixedSize(320, 240);//最高只能设置640x480
            mRecorder.setVideoSize(320, 240);//最高只能设置640x480
            //设置记录会话的最大持续时间（毫秒）
            mRecorder.setMaxDuration(60 * 1000);
            mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            file = FileUtil.getSaveFile(getPackageName()
                            + File.separator + "video" + File.separator,
                    System.currentTimeMillis() + ".mp4");
            //设置输出文件的路径
            mRecorder.setOutputFile(file.getAbsolutePath());
            //准备录制
            mRecorder.prepare();
            //开始录制
            mRecorder.start();
            isRecording = true;
            video_record_btn.setText("停止");
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }
    }

    /**
     * 停止录制
     */
    private void stopRecord() {
        try {
            //停止录制
            mRecorder.stop();
            //重置
            mRecorder.reset();
//            video_record_btn.setText("开始");
        } catch (Exception e) {
            e.printStackTrace();
        }
        isRecording = false;
        Bundle b = new Bundle();
        b.putString("filePath", file.getAbsolutePath());
        showActivity(RecordVideoActivity.this, MediaPlayerActiivty.class, b);
    }

    /**
     * 释放MediaRecorder
     */
    private void releaseMediaRecorder() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mCamera.unlock();
                mCamera.release();
            }
        } catch (RuntimeException e) {
        } finally {
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
        mSurfaceHolder = holder;
        if (mCamera == null) {
            return;
        }
        try {
            //设置显示
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            releaseCamera();
            finish();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // surfaceDestroyed的时候同时对象设置为null
        if (isRecording && mCamera != null) {
            mCamera.lock();
        }
        mSurfaceView = null;
        mSurfaceHolder = null;
        releaseMediaRecorder();
        releaseCamera();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.video_record_btn) {
            if (!isRecording) {
                startRecord();
            } else {
                stopRecord();
            }
        }

    }

    private class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
                //按升序排列
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }

    }

    /**
     * @description 兼容手机6.0权限管理(Camera权限)
     * @author ldm
     * @time 2016/5/24 14:59
     */
    private void permissionForM() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            initCamera();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCamera();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 10;
}
