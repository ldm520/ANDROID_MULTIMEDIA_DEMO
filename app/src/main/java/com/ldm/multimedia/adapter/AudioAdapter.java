package com.ldm.multimedia.adapter;

import android.content.Context;

import com.ldm.multimedia.R;
import com.ldm.multimedia.model.FileBean;

import java.util.List;

public class AudioAdapter extends CommonAdapter<FileBean> {
    public AudioAdapter(Context mContext, List<FileBean> mDatas, int itemResId) {
        super(mContext, mDatas, itemResId);
    }

    @Override
    public void convert(ViewHolder holder, FileBean fileBean) {
        holder.setTextView(R.id.item_tv, "录音文件：" + fileBean.getFile().getAbsolutePath() + "\n录音时长：" + fileBean.getFileLength() + "s");
    }
}