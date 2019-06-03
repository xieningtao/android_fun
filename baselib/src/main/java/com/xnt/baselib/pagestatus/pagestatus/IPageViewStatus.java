package com.xnt.baselib.pagestatus.pagestatus;

import android.view.View;

public interface IPageViewStatus {

    int getLoadingViewLayoutId();

    int getErrorViewLayoutId();

    int getEmptyViewLayoutId();

    int getRetryViewLayoutId();

    int getSettingViewLayoutId();

    int getContentViewLayoutId();

    int getUnLoginViewLayoutId();


    View initLoadingView();

    View initErrorView();

    View initEmptyView();

    View initRetryView();

    View initSettingView();

    View initContentView();

    View initUnLoginView();

}

