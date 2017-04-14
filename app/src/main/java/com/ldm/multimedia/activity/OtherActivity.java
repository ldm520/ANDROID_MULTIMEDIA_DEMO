package com.ldm.multimedia.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ldm.multimedia.BaseActivity;
import com.ldm.multimedia.R;

public class OtherActivity extends BaseActivity {
    private Button btn_muxer;
    private Button btn_more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        this.btn_muxer = (Button) findViewById(R.id.btn_muxer);
        this.btn_muxer.setOnClickListener(this);
        this.btn_more = (Button) findViewById(R.id.btn_more);
        this.btn_more.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_muxer:
                showActivity(this, MediamuxerActivity.class);
                break;
            case R.id.btn_more:
                showActivity(this, MoreMultiMdeiaApiActiivty.class);
                break;
        }
    }
}
