package com.ldm.multimedia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

/**
 * description：
 * 作者：ldm
 * 时间：20172017/4/10 14:19
 * 邮箱：1786911211@qq.com
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {
    protected void showActivity(Context context, Class b) {
        Intent in = new Intent(context, b);
        startActivity(in);
    }

    public void showActivity(Activity aty, Class<?> cls, Bundle extras) {
        Intent intent = new Intent();
        intent.putExtras(extras);
        intent.setClass(aty, cls);
        aty.startActivity(intent);
    }

    protected void showToastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
