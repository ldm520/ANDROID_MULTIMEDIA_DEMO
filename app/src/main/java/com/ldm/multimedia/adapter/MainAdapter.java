package com.ldm.multimedia.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ldm.multimedia.R;

/**
 * description：
 * 作者：ldm
 * 时间：20172017/4/17 16:45
 * 邮箱：1786911211@qq.com
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private String[] mDatas;
    private RvItemClickListener itemClickListener;

    public MainAdapter(String[] mDatas, RvItemClickListener itemClickListener) {
        this.mDatas = mDatas;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item, null);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mTextView.setText(mDatas[position]);
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != itemClickListener) {
                    itemClickListener.onItemClick(holder.mTextView, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.main_item_tv);
        }
    }

    public interface RvItemClickListener {
        void onItemClick(View view, int position);
    }
}
