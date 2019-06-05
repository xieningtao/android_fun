package com.xnt.baselib.title;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xnt.baselib.R;

public class DefaultBarFactory implements TitleFactory {
    @Override
    public View createTitleView(LayoutInflater inflater, ViewGroup parent) {
        View titleView = inflater.inflate(R.layout.layout_default_title_bar, null);
        titleView.findViewById(R.id.title_bar_back_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getContext() instanceof AppCompatActivity) {
                    AppCompatActivity appCompatActivity = (AppCompatActivity) v.getContext();
                    appCompatActivity.finish();
                }
            }
        });
        return titleView;
    }
}
