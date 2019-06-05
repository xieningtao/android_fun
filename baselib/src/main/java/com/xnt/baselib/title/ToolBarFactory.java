package com.xnt.baselib.title;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xnt.baselib.R;

public class ToolBarFactory implements TitleFactory {
    @Override
    public View createTitleView(LayoutInflater inflater, ViewGroup parent) {
        View titleView = inflater.inflate(R.layout.base_toolbar, null);
        return titleView;
    }
}
