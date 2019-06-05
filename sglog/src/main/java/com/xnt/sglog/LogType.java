package com.xnt.sglog;

/**
 * Created by NetEase on 2016/9/13 0013.
 */
enum LogType {
    EXCEPTION("EXCEPTION", L.LEVEL_ERROR);
    public final String mTag;
    public final int mLevel;

    private LogType(String tag, int level) {
        this.mLevel = level;
        this.mTag = tag;
    }
}
