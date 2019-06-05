package com.xnt.baselib.mvp;

public interface IView {
    void onStartLoading(String channel);

    void onFailed(String errorCode, String errorMsg, String channel);

    void onComplete(String channel);
}
