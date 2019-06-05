package com.xnt.baselib.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.xnt.baselib.EmptyEvent;
import com.xnt.baselib.utils.StatusBarTools;
import com.xnt.baselib.utils.SystemBarTintManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;

/**
 * 比较纯净的activity
 */
public class BaseSGActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EventBus
        EventBus.getDefault().register(this);
        initBasicTool();
    }

    protected void initBasicTool() {
        ButterKnife.bind(this);
        StatusBarTools.setTranslucentStatus(this, true);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEmptyEvent(EmptyEvent event) {

    }
}
