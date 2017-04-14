package com.ldm.multimedia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {
    protected Context mContext;
	protected List<T> mDatas;
	protected LayoutInflater mInflater;
	protected int itemResId;

	public CommonAdapter(Context mContext, List<T> mDatas, int itemResId) {
		this.mContext = mContext;
		this.mDatas = mDatas;
		this.itemResId = itemResId;
		if(mContext!=null)
		   mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = ViewHolder.getHolder(mContext, convertView, parent,
				itemResId, position);
		convert(holder, getItem(position));
		return holder.getConvertView();
	}

	/**
	 * 数据处理方法，我们写的ListView对应adapter主要是实现这个方法功能
	 * 
	 * @description：
	 * @author ldm
	 * @date 2015-10-13 上午10:13:06
	 */
	public abstract void convert(ViewHolder holder, T t);

	protected void showMsg(int resId) {
		Toast.makeText(mContext, mContext.getString(resId), Toast.LENGTH_SHORT)
				.show();
	}
}