package com.xnt.baselib.pagestatus.pagestatus;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xnt.baselib.R;


public class PageViewStatusLayout extends FrameLayout implements IViewStatus {

    public static final int TYPE_VIEW_CONTENT = 0;
    public static final int TYPE_VIEW_RETRY = 1;
    public static final int TYPE_VIEW_ERROR = 2;
    public static final int TYPE_VIEW_LOADING = 3;
    public static final int TYPE_VIEW_SETTING = 4;
    public static final int TYPE_VIEW_UN_LOGIN = 5;
    public static final int TYPE_VIEW_EMPTY = 6;
    //网络
    public static final int TYPE_NETWORK_TRY = 7;

    private Context mContext;
    private AbsPageViewStatus mAbsPageViewStatus;
    private LayoutParams mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    private int mCurrentStatusType = TYPE_VIEW_CONTENT;

    private View mContentView;
    private View mRetryView;
    private View mErrorView;
    private View mLoadingView;
    private View mSettingView;
    private View mUnLoginView;
    private View mEmptyView;
    private View mNetworkView;


    public PageViewStatusLayout(@NonNull Context context) {
        this(context, null);
    }

    public PageViewStatusLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PageViewStatusLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }


    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        boolean isAddEmpty = false;
        if (child.getTag(R.id.tag_page_view_status) != null) {
            isAddEmpty = (boolean) child.getTag(R.id.tag_page_view_status);
        }

        if (!isAddEmpty) {
            if (getChildCount() > 0) {
                throw new IllegalStateException(
                        "StatusLayout can host only one direct child");
            }
        }

        super.addView(child, index, params);
    }

    public int getCurrentStatusType() {
        return mCurrentStatusType;
    }

    public PageViewStatusLayout setViewStatus(@NonNull AbsPageViewStatus absPageViewStatus) {
        this.mAbsPageViewStatus = absPageViewStatus;
        setContentView();
        setEmptyView();
        setErrorView();
        setLoadingView();
        setRetryView();
        setSettingView();
        setUnLoginView();
        setNetworkView();
        showContentStatusView();
        return this;
    }

    public PageViewStatusLayout setContentView() {
        removeView(mContentView);
        mContentView = this.mAbsPageViewStatus.getContentView();
        mContentView.setTag(R.id.tag_page_view_status, true);
        mContentView.setTag(R.id.tag_page_view_status_type, TYPE_VIEW_CONTENT);
        addView(mContentView, mLayoutParams);
        mContentView.setVisibility(GONE);
        return this;
    }


    public PageViewStatusLayout setRetryView() {
        removeView(mRetryView);
        mRetryView = this.mAbsPageViewStatus.getRetryView();
        mRetryView.setTag(R.id.tag_page_view_status, true);
        mRetryView.setTag(R.id.tag_page_view_status_type, TYPE_VIEW_RETRY);
        addView(mRetryView, mLayoutParams);
        mRetryView.setVisibility(GONE);
        return this;
    }

    public PageViewStatusLayout setEmptyView() {
        removeView(mEmptyView);
        mEmptyView = this.mAbsPageViewStatus.getEmptyView();
        mEmptyView.setTag(R.id.tag_page_view_status, true);
        mEmptyView.setTag(R.id.tag_page_view_status_type, TYPE_VIEW_EMPTY);
        addView(mEmptyView, mLayoutParams);
        mEmptyView.setVisibility(GONE);
        return this;
    }

    public PageViewStatusLayout setErrorView() {
        removeView(mErrorView);
        mErrorView = this.mAbsPageViewStatus.getErrorView();
        mErrorView.setTag(R.id.tag_page_view_status, true);
        mErrorView.setTag(R.id.tag_page_view_status_type, TYPE_VIEW_ERROR);
        addView(mErrorView, mLayoutParams);
        mErrorView.setVisibility(GONE);
        return this;
    }

    public PageViewStatusLayout setUnLoginView() {
        removeView(mUnLoginView);
        mUnLoginView = this.mAbsPageViewStatus.getUnLoginView();
        mUnLoginView.setTag(R.id.tag_page_view_status, true);
        mUnLoginView.setTag(R.id.tag_page_view_status_type, TYPE_VIEW_UN_LOGIN);
        addView(mUnLoginView, mLayoutParams);
        mUnLoginView.setVisibility(GONE);
        return this;
    }

    public PageViewStatusLayout setSettingView() {

        removeView(mSettingView);
        mSettingView = this.mAbsPageViewStatus.getSettingView();
        mSettingView.setTag(R.id.tag_page_view_status, true);
        mSettingView.setTag(R.id.tag_page_view_status_type, TYPE_VIEW_SETTING);
        addView(mSettingView, mLayoutParams);
        mSettingView.setVisibility(GONE);
        return this;
    }

    public PageViewStatusLayout setNetworkView() {
        removeView(mNetworkView);
        mNetworkView = this.mAbsPageViewStatus.getNetWorkView();
        mNetworkView.setTag(R.id.tag_page_view_status, true);
        mNetworkView.setTag(R.id.tag_page_view_status_type, TYPE_NETWORK_TRY);
        addView(mNetworkView, mLayoutParams);
        mNetworkView.setVisibility(GONE);
        return this;
    }

    public PageViewStatusLayout setLoadingView() {
        removeView(mLoadingView);
        mLoadingView = this.mAbsPageViewStatus.getLoadingView();
        mLoadingView.setTag(R.id.tag_page_view_status, true);
        mLoadingView.setTag(R.id.tag_page_view_status_type, TYPE_VIEW_LOADING);
        addView(mLoadingView, mLayoutParams);
        mLoadingView.setVisibility(GONE);
        return this;
    }

    public void show(int type) {
        mCurrentStatusType = type;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            int tmpType;
            if (getChildAt(i).getTag(R.id.tag_page_view_status_type) != null) {

                tmpType = (int) getChildAt(i).getTag(R.id.tag_page_view_status_type);
                if (tmpType == type) {
                    getChildAt(i).setVisibility(VISIBLE);
                } else {
                    getChildAt(i).setVisibility(GONE);
                }
            }
        }
    }


    @Override
    public void showContentStatusView() {
        show(TYPE_VIEW_CONTENT);
    }

    @Override
    public void showSettingStatusView() {
        show(TYPE_VIEW_SETTING);
    }

    @Override
    public void showLoadingStatusView() {
        show(TYPE_VIEW_LOADING);
    }

    @Override
    public void showLoadingStatusView(String loadingMsg) {
        show(TYPE_VIEW_LOADING);
    }

    @Override
    public void showErrorStatusView() {
        show(TYPE_VIEW_ERROR);
    }

    @Override
    public void showErrorStatusView(String errorMsg) {
        TextView textView = mErrorView.findViewById(R.id.layout_page_status_error_tv);
        if (null != textView && !TextUtils.isEmpty(errorMsg)) {
            textView.setText(errorMsg);
        }
        show(TYPE_VIEW_ERROR);

    }

    @Override
    public void showErrorStatusView(Drawable drawable) {
        ImageView imageView = mErrorView.findViewById(R.id.layout_page_status_error_iv);
        if (null != imageView && drawable != null) {
            imageView.setImageDrawable(drawable);
        }
        show(TYPE_VIEW_ERROR);
    }

    @Override
    public void showErrorStatusView(String errorMsg, Drawable drawable) {

        ImageView imageView = mErrorView.findViewById(R.id.layout_page_status_error_iv);
        TextView textView = mErrorView.findViewById(R.id.layout_page_status_error_tv);
        if (null != textView && !TextUtils.isEmpty(errorMsg)) {
            textView.setText(errorMsg);
        }

        if (null != imageView && drawable != null) {
            imageView.setImageDrawable(drawable);
        }

        show(TYPE_VIEW_ERROR);
    }

    @Override
    public void showEmptyStatusView() {
        show(TYPE_VIEW_EMPTY);
    }

    public void showNetworkView(){
        show(TYPE_NETWORK_TRY);
    }

    public void showNetworkView(OnClickListener listener){
        showNetworkView();
        mNetworkView.setOnClickListener(listener);
    }

    @Override
    public void showEmptyStatusView(String emptyMsg) {
        if (mEmptyView != null) {
            TextView textView = mEmptyView.findViewById(R.id.layout_page_status_empty_tv);
            if (textView != null && !TextUtils.isEmpty(emptyMsg)) {
                textView.setText(emptyMsg);
            }
        }
        show(TYPE_VIEW_EMPTY);
    }

    @Override
    public void showEmptyStatusView(Drawable drawable) {

    }

    @Override
    public void showEmptyStatusView(String emptyMsg, Drawable drawable) {

    }

    @Override
    public void showRetryStatusView() {
        show(TYPE_VIEW_RETRY);
    }

    @Override
    public void showRetryStatusView(String retryMsg) {

    }

    @Override
    public void showRetryStatusView(OnClickListener onClickListener) {

    }

    @Override
    public void showRetryStatusView(Drawable drawable) {

    }

    @Override
    public void showRetryStatusView(String retryMsg, Drawable drawable) {

    }

    @Override
    public void showRetryStatusView(String retryMsg, OnClickListener onClickListener) {

    }

    @Override
    public void showRetryStatusView(Drawable drawable, OnClickListener onClickListener) {

    }

    @Override
    public void showRetryStatusView(String retryMsg, Drawable drawable, OnClickListener onClickListener) {

    }

    @Override
    public void showUnLoginStatusView() {
        show(TYPE_VIEW_UN_LOGIN);
    }

    @Override
    public void showUnLoginStatusView(Drawable drawable) {

    }

    @Override
    public void showUnLoginStatusView(OnClickListener onClickListener) {

    }

    @Override
    public void showUnLoginStatusView(Drawable drawable, OnClickListener onClickListener) {

    }
}
