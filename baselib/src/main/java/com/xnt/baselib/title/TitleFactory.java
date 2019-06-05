package com.xnt.baselib.title;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface TitleFactory {
    View createTitleView(LayoutInflater inflater, ViewGroup parent);
}
