package com.xnt.baselib.pagestatus.pagestatus;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.xnt.baselib.R;


public abstract class AbsPageViewStatus implements IPageViewStatus {

    public static final int LAYOUT_EMPTY_VIEW = R.layout.layout_default_page_status_empty;
    public static final int LAYOUT_ERROR_VIEW = R.layout.layout_default_page_status_error;
    public static final int LAYOUT_LOADING_VIEW = R.layout.layout_default_page_status_loading;
    public static final int LAYOUT_RETRY_VIEW = R.layout.layout_default_page_status_retry;
    public static final int LAYOUT_SETTING_VIEW = R.layout.layout_default_page_status_setting;
    public static final int LAYOUT_UN_LOGIN_VIEW = R.layout.layout_default_page_status_un_login;
    public static final int LAYOUT_CONTENT_VIEW = R.layout.layout_default_page_status_content;
    public static final int LAYOUT_NETWORK_VIEW = R.layout.layout_default_page_status_network;

    protected Context mContext;
    protected LayoutInflater mLayoutInflater;

    protected View mLoadingView;
    protected View mEmptyView;
    protected View mErrorView;
    protected View mRetryView;
    protected View mSettingView;
    protected View mUnLoginView;
    protected View mNetworkView;
    protected View mContentView;

    public AbsPageViewStatus(@NonNull Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public AbsPageViewStatus(@NonNull Context context, int layoutRes) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mContentView = mLayoutInflater.inflate(layoutRes, null);
    }

    public AbsPageViewStatus(@NonNull Context context, View contentView) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mContentView = contentView;
    }

    public View getNetWorkView() {
        if (null != mLayoutInflater && null == mNetworkView) {

            if (checkResourceID(getLayoutNetworkViewLayoutId())) {
                mNetworkView = mLayoutInflater.inflate(getLayoutNetworkViewLayoutId(), null);
                if (null == mNetworkView) {
                    mNetworkView = initNetworkView();
                }
            } else {
                mNetworkView = initNetworkView();
            }
        }

        if (null == mNetworkView) {
            mNetworkView = mLayoutInflater.inflate(LAYOUT_NETWORK_VIEW, null);
        }

        return mNetworkView;
    }

    public View getContentView() {

        if (null != mLayoutInflater && null == mContentView) {

            if (checkResourceID(getContentViewLayoutId())) {
                mContentView = mLayoutInflater.inflate(getContentViewLayoutId(), null);
                if (null == mContentView) {
                    mContentView = initContentView();
                }
            } else {
                mContentView = initContentView();
            }
        }

        if (null == mContentView) {
            mContentView = mLayoutInflater.inflate(LAYOUT_CONTENT_VIEW, null);
        }

        return mContentView;
    }

    public void setContentView(View contentView) {
        this.mContentView = contentView;
    }

    public View getLoadingView() {
        if (null != mLayoutInflater && null == mLoadingView) {
            if (checkResourceID(getLoadingViewLayoutId())) {
                mLoadingView = mLayoutInflater.inflate(getLoadingViewLayoutId(), null);
                if (null == mLoadingView) {
                    mLoadingView = initLoadingView();
                }
            } else {
                mLoadingView = initLoadingView();
            }
        }

        if (mLoadingView == null) {
            mLoadingView = mLayoutInflater.inflate(LAYOUT_LOADING_VIEW, null);
        }
        return mLoadingView;
    }

    public void setLoadingView(View loadingView) {
        this.mLoadingView = loadingView;
    }

    public View getEmptyView() {
        if (null == mLayoutInflater && null != mEmptyView) {
            if (checkResourceID(getEmptyViewLayoutId())) {
                mEmptyView = mLayoutInflater.inflate(getEmptyViewLayoutId(), null);
                if (null == mEmptyView) {
                    mEmptyView = initEmptyView();
                }
            } else {
                mEmptyView = initEmptyView();
            }
        }

        if (null == mEmptyView) {
            mEmptyView = mLayoutInflater.inflate(LAYOUT_EMPTY_VIEW, null);
        }

        return mEmptyView;
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }

    public View getErrorView() {
        if (null == mLayoutInflater && null != mErrorView) {
            if (checkResourceID(getErrorViewLayoutId())) {
                mErrorView = mLayoutInflater.inflate(getErrorViewLayoutId(), null);
                if (null == mErrorView) {
                    mErrorView = initErrorView();
                }
            } else {
                mErrorView = initErrorView();
            }
        }

        if (null == mErrorView) {
            mErrorView = mLayoutInflater.inflate(LAYOUT_ERROR_VIEW, null);
        }

        return mErrorView;
    }

    public void setErrorView(View errorView) {
        this.mErrorView = errorView;
    }

    public View getRetryView() {

        if (null == mRetryView && null != mLayoutInflater) {
            if (checkResourceID(getRetryViewLayoutId())) {
                mRetryView = mLayoutInflater.inflate(getRetryViewLayoutId(), null);
                if (null == mRetryView) {
                    mRetryView = initRetryView();
                }
            } else {
                mRetryView = initRetryView();
            }
        }

        if (null == mRetryView) {
            mRetryView = mLayoutInflater.inflate(LAYOUT_RETRY_VIEW, null);
        }

        return mRetryView;
    }

    public void setRetryView(View retryView) {
        this.mRetryView = retryView;
    }

    public View getSettingView() {

        if (null == mSettingView && null != mLayoutInflater) {
            if (checkResourceID(getSettingViewLayoutId())) {
                mRetryView = mLayoutInflater.inflate(getSettingViewLayoutId(), null);
                if (null == mSettingView) {
                    mSettingView = initSettingView();
                }
            } else {
                mSettingView = initSettingView();
            }
        }

        if (null == mSettingView) {
            mSettingView = mLayoutInflater.inflate(LAYOUT_SETTING_VIEW, null);
        }

        return mSettingView;
    }

    public void setSettingView(View settingView) {
        this.mSettingView = settingView;
    }

    public View getUnLoginView() {
        if (null == mUnLoginView && null != mLayoutInflater) {
            if (checkResourceID(getUnLoginViewLayoutId())) {
                mUnLoginView = mLayoutInflater.inflate(getUnLoginViewLayoutId(), null);
                if (null == mUnLoginView) {
                    mUnLoginView = initUnLoginView();
                }
            } else {
                mUnLoginView = initUnLoginView();
            }
        }

        if (null == mUnLoginView) {
            mUnLoginView = mLayoutInflater.inflate(LAYOUT_UN_LOGIN_VIEW, null);
        }
        return mUnLoginView;
    }

    public void setUnloginView(View unLoginView) {
        this.mUnLoginView = unLoginView;
    }


    public int getLayoutNetworkViewLayoutId() {
        return 0;
    }

    @Override
    public int getLoadingViewLayoutId() {
        return 0;
    }

    @Override
    public int getErrorViewLayoutId() {
        return 0;
    }

    @Override
    public int getEmptyViewLayoutId() {
        return 0;
    }

    @Override
    public int getRetryViewLayoutId() {
        return 0;
    }

    @Override
    public int getSettingViewLayoutId() {
        return 0;
    }

    @Override
    public int getContentViewLayoutId() {
        return 0;
    }

    @Override
    public int getUnLoginViewLayoutId() {
        return 0;
    }

    @Override
    public View initLoadingView() {
        return null;
    }

    @Override
    public View initErrorView() {
        return null;
    }

    @Override
    public View initEmptyView() {
        return null;
    }

    @Override
    public View initRetryView() {
        return null;
    }

    @Override
    public View initSettingView() {
        return null;
    }

    @Override
    public View initContentView() {
        return null;
    }

    public View initNetworkView() {
        return null;
    }

    @Override
    public View initUnLoginView() {
        return null;
    }

    private boolean checkResourceID(int layoutId) {
        return (layoutId >>> 24) >= 2;
    }
}
