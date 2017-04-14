package com.ldm.multimedia.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.ldm.multimedia.R;

/**
 * @author ldm
 * @description 图片操作工具类
 * @time 2017/4/10 14:47
 */
public class ImagePopupWindowHelper {
    private ISlectPicListener mPicListener;

    public PopupWindow mPopupWindow;

    public void setPicListener(ISlectPicListener mPicListener) {
        this.mPicListener = mPicListener;
    }

    private Activity mContext;

    public ImagePopupWindowHelper(Activity context) {
        this.mContext = context;
    }

    /**
     * 初始化PopupWindow
     */
    public PopupWindow initPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_select_pic, null);
        mPopupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 加上这个popupwindow中的ListView才可以接收点击事件
        mPopupWindow.setFocusable(true);
        mPopupWindow.setAnimationStyle(R.style.popwindow_exit_anim_style);
        // 控制popupwindow点击屏幕其他地方消失
        mPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.color.transparent));
        // 触摸popupwindow外部，popupwindow消失
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // popupwindow消失的时候恢复成原来的透明度
                mPicListener.onDismiss();
            }
        });
        layout.findViewById(R.id.take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                mPicListener.takePhoto();
            }
        });
        layout.findViewById(R.id.select_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                mPicListener.selectPic();
            }
        });
        layout.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                mPicListener.cancel();
            }
        });
        return mPopupWindow;
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        mContext.getWindow().setAttributes(lp);
        mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public interface ISlectPicListener {
        void onDismiss();

        //取消操作
        void cancel();

        //拍照
        void takePhoto();

        //从图库中选择
        void selectPic();
    }
}
