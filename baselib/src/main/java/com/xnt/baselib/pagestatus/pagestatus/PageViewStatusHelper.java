package com.xnt.baselib.pagestatus.pagestatus;

import android.view.View;

public class PageViewStatusHelper {

    public static void showEmptyStatus(PageViewStatusLayout layout,String msg){
        if(layout != null){
            layout.showEmptyStatusView(msg);
        }
    }

    public static void showNetworkError(PageViewStatusLayout layout){
        showNetworkError(layout,null);
    }

    public static void showNetworkError(PageViewStatusLayout layout,View.OnClickListener listener){
        if(layout != null){
            layout.showNetworkView(listener);
        }
    }

    public static void showLoadingStatusView(PageViewStatusLayout layout){
        if(layout != null){
            layout.showLoadingStatusView();
        }
    }

    public static void showErrorStatusView(PageViewStatusLayout layout,String msg){
        if(layout != null){
            layout.showErrorStatusView(msg);
        }
    }

    public static void showRetryStatusView(PageViewStatusLayout layout,String msg, View.OnClickListener listener){
        if(layout != null){
            layout.showRetryStatusView(msg,listener);
        }
    }

    public static void showContentStatusView(PageViewStatusLayout layout){
        if(layout != null){
            layout.showContentStatusView();
        }
    }
}
