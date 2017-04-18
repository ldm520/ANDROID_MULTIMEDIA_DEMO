package com.ldm.multimedia.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ldm.multimedia.BaseActivity;
import com.ldm.multimedia.R;
import com.ldm.multimedia.adapter.MainAdapter;
import com.ldm.multimedia.utils.ImagePopupWindowHelper;

import java.io.File;

public class MainActivity extends BaseActivity implements ImagePopupWindowHelper.ISlectPicListener, MainAdapter.RvItemClickListener {
    private ImageView photo_iv;
    private RecyclerView recyclerView;
    /***
     * 使用照相机拍照获取图片
     */
    public static final int TACK_PHOTO = 1;
    /***
     * 使用相册中的图片
     */
    public static final int PICK_PHOTO = 2;
    private TextView takePhotoTv, pickPhotoTv, cancelTv;
    private ImagePopupWindowHelper mPicPopupHelper;
    private PopupWindow mPopupWindow;
    //是通过拍照还是通过选择图片
    private boolean isTake;
    private String picPath;
    private Uri photoUri;
    //剪裁图片
    private static final int CROP_PICTURE = 3;
    private String[] mDatas = {"图片部分", "音频部分", "视频部分", "其它部分"};
    private MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPicPopupHelper = new ImagePopupWindowHelper(this);
        mPicPopupHelper.setPicListener(this);
        mPopupWindow = mPicPopupHelper.initPopupWindow();
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mainAdapter = new MainAdapter(mDatas, this);
        //设置适配器
        recyclerView.setAdapter(mainAdapter);
        //设置垂直显示
        LinearLayoutManager ll = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(ll);
        this.photo_iv = (ImageView) findViewById(R.id.photo_iv);
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onDismiss() {
        if (null != mPopupWindow && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        mPicPopupHelper.backgroundAlpha(1f);
    }

    @Override
    public void cancel() {
        if (null != mPopupWindow && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        mPicPopupHelper.backgroundAlpha(1f);
    }

    @Override
    public void takePhoto() {
        isTake = true;
        if (Build.VERSION.SDK_INT > 22) {
            permissionForM();
        } else {
            byTakePhoto();
        }
    }

    @Override
    public void selectPic() {
        isTake = false;
        if (Build.VERSION.SDK_INT > 22) {
            permissionForM();
        } else {
            selectPhoto();
        }
    }

    /**
     * 拍照获取图片
     */
    private void byTakePhoto() {
        //执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"
/***
 * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
 * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
 * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
 */
            ContentValues values = new ContentValues();
            photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, TACK_PHOTO);
        } else {
            Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }

    /***
     * 从相册中取图片
     */
    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(intent, PICK_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //从相册取图片，有些手机有异常情况，请注意
            if (requestCode == PICK_PHOTO) {
                if (data == null) {
                    return;
                }
                photoUri = data.getData();
                if (photoUri == null) {
                    return;
                } else {
                    picPath = uriToFilePath(photoUri);
                    startPhotoZoom(photoUri, CROP_PICTURE);
                }
            } else if (requestCode == TACK_PHOTO) {
                String[] pojo = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
                if (cursor != null) {
                    int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
                    cursor.moveToFirst();
                    picPath = cursor.getString(columnIndex);
                    if (Build.VERSION.SDK_INT < 14) {
                        cursor.close();
                    }
                }
                if (picPath != null && (picPath.endsWith(".png") || picPath.endsWith(".PNG") || picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
                    photoUri = Uri.fromFile(new File(picPath));
                    startPhotoZoom(photoUri, CROP_PICTURE);
                }
            } else if (requestCode == CROP_PICTURE) {
                if (photoUri != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(picPath);
                    if (bitmap != null) {
                        photo_iv.setVisibility(View.VISIBLE);
                        photo_iv.setImageBitmap(bitmap);
                    }
                }

            }
        }
    }

    /**
     * @description 图片裁剪(当然可以不对图片进行裁剪)
     * @author ldm
     * @time 2017/4/10 15:06
     */
    private void startPhotoZoom(Uri uri, int requestCode) {
        int dp = 500;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);//输出是X方向的比例
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高，切忌不要再改动下列数字，会卡死
        intent.putExtra("outputX", 600);//输出X方向的像素
        intent.putExtra("outputY", 600);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);//设置为不返回数据
        startActivityForResult(intent, requestCode);
    }

    private String uriToFilePath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        return actualimagecursor.getString(actual_image_column_index);
    }

    /**
     * @description 兼容手机6.0权限管理
     * @author ldm
     * @time 2016/5/24 14:59
     */
    private void permissionForM() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_TAKE_PHOTO);
        } else {
            if (isTake) {
                byTakePhoto();
            } else {
                selectPhoto();
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_TAKE_PHOTO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isTake) {
                    byTakePhoto();
                } else {
                    selectPhoto();
                }
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static final int MY_PERMISSIONS_REQUEST_TAKE_PHOTO = 10;

    @Override
    public void onItemClick(View v, int position) {
        switch (position) {
            case 0:
                mPicPopupHelper.backgroundAlpha(0.5f);
                mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 50);// 显示
                break;
            case 1:
                showActivity(this, AudioActivity.class);
                break;
            case 2:
                showActivity(this, VideoActivity.class);
                break;
            case 3:
                showActivity(this, OtherActivity.class);
                break;
        }
    }
}
