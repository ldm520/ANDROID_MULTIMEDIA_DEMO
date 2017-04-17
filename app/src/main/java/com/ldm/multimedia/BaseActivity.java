package com.ldm.multimedia;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.transition.Explode;
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
        getWindow().setExitTransition(new Explode());
        Intent in = new Intent(context, b);
        startActivity(in,
                ActivityOptions
                        .makeSceneTransitionAnimation(this).toBundle());
    }

    public void showActivity(Activity aty, Class<?> cls, Bundle extras) {
        getWindow().setExitTransition(new Explode());
        Intent intent = new Intent();
        intent.putExtras(extras);
        startActivity(intent,
                ActivityOptions
                        .makeSceneTransitionAnimation(this).toBundle());
    }

    protected void showToastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
