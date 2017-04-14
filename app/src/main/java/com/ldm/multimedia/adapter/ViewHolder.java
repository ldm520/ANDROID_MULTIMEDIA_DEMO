package com.ldm.multimedia.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {
    /**
     * SparseArray是android里为<Interger,Object>这样的HashMap<Integer,Oject>
     * 而专门写的类,目的是提高效率，其核心是折半查找函数（binarySearch）。
     * 在Android中，当我们需要定义    HashMap<Integer, E> hashMap =
     * new HashMap<Integer, E>()时我们可以使用如下的方式来取得更好的性能.
     * SparseArray<E> sparseArray = new SparseArray<E>();
     */
    private SparseArray<View> mViews;
    private int mPosition;// 这个参数在我们防止数据错乱加载时可用到
    private View mConvertView;
    private Context mContext;

    public ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mPosition = position;
        this.mContext = context;
        mViews = new SparseArray<>();
        mConvertView = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    /**
     * 获取到ViewHolder
     *
     * @description：
     * @author ldm
     * @date 2015-10-13 上午10:08:41
     */
    public static ViewHolder getHolder(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        } else {
            ViewHolder hoder = (ViewHolder) convertView.getTag();
            hoder.mPosition = position;
            return hoder;
        }
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 通过id获取控件
     *
     * @description：
     * @author ldm
     * @date 2015-10-12 下午8:01:46
     */
    public <T extends View> T getViewById(int resId) {
        View view = mViews.get(resId);
        if (view == null) {
            view = mConvertView.findViewById(resId);
            mViews.put(resId, view);
        }
        return (T) view;
    }


    /**
     * 设置TextView数据
     *
     * @description：
     * @author ldm
     * @date 2015-10-13 上午9:32:49
     */
    public ViewHolder setTextView(int resId, String text) {
        TextView tv = getViewById(resId);
        tv.setText(text);
        return this;
    }

    public ViewHolder setTextView(int resId, int textId) {
        TextView tv = getViewById(resId);
        tv.setText(textId);
        return this;
    }

    /**
     * 为ImageView设置数据
     *
     * @description：
     * @author ldm
     * @date 2015-10-13 上午9:34:14
     */
    public ViewHolder setImageView(int resId, int picId) {
        ImageView iv = getViewById(resId);
        iv.setImageResource(picId);
        return this;
    }

    //设置CheckBox
    public ViewHolder setCheckBox(int resId, boolean isChecked) {
        CheckBox cb = getViewById(resId);
        cb.setChecked(isChecked);
        return this;
    }
}