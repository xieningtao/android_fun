package com.xnt.baselib.pagestatus.pagestatus;

import android.graphics.drawable.Drawable;
import android.view.View;

public interface IViewStatus {

    void showContentStatusView();


    void showSettingStatusView();


    void showLoadingStatusView();

    void showLoadingStatusView(String loadingMsg);


    void showErrorStatusView();

    void showErrorStatusView(String errorMsg);

    void showErrorStatusView(Drawable drawable);

    void showErrorStatusView(String errorMsg, Drawable drawable);


    void showEmptyStatusView();

    void showEmptyStatusView(String emptyMsg);

    void showEmptyStatusView(Drawable drawable);

    void showEmptyStatusView(String emptyMsg, Drawable drawable);


    void showRetryStatusView();

    void showRetryStatusView(String retryMsg);

    void showRetryStatusView(View.OnClickListener onClickListener);

    void showRetryStatusView(Drawable drawable);

    void showRetryStatusView(String retryMsg, Drawable drawable);

    void showRetryStatusView(String retryMsg, View.OnClickListener onClickListener);

    void showRetryStatusView(Drawable drawable, View.OnClickListener onClickListener);

    void showRetryStatusView(String retryMsg, Drawable drawable, View.OnClickListener onClickListener);


    void showUnLoginStatusView();

    void showUnLoginStatusView(Drawable drawable);

    void showUnLoginStatusView(View.OnClickListener onClickListener);

    void showUnLoginStatusView(Drawable drawable, View.OnClickListener onClickListener);


}
