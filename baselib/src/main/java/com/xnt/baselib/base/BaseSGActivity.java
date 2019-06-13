package com.xnt.baselib.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xnt.baselib.EmptyEvent;
import com.xnt.baselib.utils.StatusBarTools;

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
