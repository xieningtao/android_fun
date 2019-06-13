package com.xnt.baselib.base;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.xnt.baselib.R;
import com.xnt.baselib.pagestatus.pagestatus.AbsPageViewStatus;
import com.xnt.baselib.pagestatus.pagestatus.DefaultPageViewStatus;
import com.xnt.baselib.pagestatus.pagestatus.PageViewStatusHelper;
import com.xnt.baselib.pagestatus.pagestatus.PageViewStatusLayout;
import com.xnt.baselib.title.TitleFactory;

/**
 * 采用自定义titlebar，或者是toolBar
 * 同时定义了各种不同状态的view
 */
abstract public class BaseCommonSGActivity extends BaseSGActivity {

    protected FrameLayout mTitleContainer;
    protected PageViewStatusLayout mPageViewStatusLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common_container);
        mTitleContainer = findViewById(R.id.layout_common_title_bar_container);
        TitleFactory factory = getTitleBarFactory();
        if (factory != null) {
            View titleView = factory.createTitleView(LayoutInflater.from(this), mTitleContainer);
            mTitleContainer.addView(titleView);
            initTitleBar(titleView);
        } else {
            mTitleContainer.setVisibility(View.GONE);
        }
        mPageViewStatusLayout = findViewById(R.id.layout_common_content_container);
        int layoutRes = getChildContentLayoutRes();
        View contentRootView = LayoutInflater.from(this).inflate(layoutRes, null);
        mPageViewStatusLayout.setViewStatus(getPageViewStatus(contentRootView));
    }

    /**
     * 子类需要重写此函数，来初始化title
     *
     * @param barView
     */
    protected void initTitleBar(View barView) {
        if (barView instanceof Toolbar) {
            Toolbar toolbar = (Toolbar) barView;
            setSupportActionBar(toolbar);
        }
    }

    @LayoutRes
    abstract int getChildContentLayoutRes();

    abstract protected TitleFactory getTitleBarFactory();

    protected AbsPageViewStatus getPageViewStatus(View rootView) {
        return new DefaultPageViewStatus(this, rootView);
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
        super.onDestroy();
    }


    //<editor-fold desc="各种状态view">
    public void showEmptyStatus(String msg) {
        PageViewStatusHelper.showEmptyStatus(mPageViewStatusLayout, msg);
    }

    public void showNetworkError() {
        showNetworkError(null);
    }

    public void showNetworkError(View.OnClickListener listener) {
        PageViewStatusHelper.showNetworkError(mPageViewStatusLayout, listener);
    }

    public void showLoadingStatusView() {
        PageViewStatusHelper.showLoadingStatusView(mPageViewStatusLayout);
    }

    public void showErrorStatusView(String msg) {
        PageViewStatusHelper.showErrorStatusView(mPageViewStatusLayout, msg);
    }

    public void showRetryStatusView(String msg, View.OnClickListener listener) {
        PageViewStatusHelper.showRetryStatusView(mPageViewStatusLayout, msg, listener);
    }

    public void showContentStatusView() {
        PageViewStatusHelper.showContentStatusView(mPageViewStatusLayout);
    }
    //</editor-fold>
}
