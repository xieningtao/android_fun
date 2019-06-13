package com.xnt.sglog;

import androidx.annotation.NonNull;
import android.util.Log;


import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.Logger;
import com.xnt.sglog.prettylog.StrategyFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * 引入一种新的日志格式，由于可能引起不适应的原因，两种日志之前做一个过渡切换
 * 设置开关进行日志格式个切换
 */
public class L {

    public static final int LOG_MAX_BYTES = 500 * 1024;

    private static final String TAG = "huatian";

    private static int LOG_LEVEL = 1;

    //<editor-fold desc="log level">
    public static final int LEVEL_VERBOSE = 0;

    public static final int LEVEL_DEBUG = 1;

    public static final int LEVEL_INFO = 2;

    public static final int LEVEL_WARN = 3;

    public static final int LEVEL_ERROR = 4;
    //</editor-fold>

    public static boolean enable = false;

    public static final void init(String externalStorageDirectory, LogStrategy logStrategy) {
//            FormatStrategy formatStrategy = StrategyFactory.getLogCatStrategy("HT");
        //log file
        //线上只打印文件
        if (BuildConfig.LOG_BUILD_TYPE.equals("RELEASE")) {
            FormatStrategy customFormatStrategy = StrategyFactory.getELKStrategy("HT", logStrategy);
            Logger.addLogAdapter(new AndroidLogAdapter(customFormatStrategy));
        } else if (BuildConfig.LOG_BUILD_TYPE.equals("DEBUG")) {//debug环境
            FormatStrategy logFormatStrategy = StrategyFactory.getLogCatStrategy("HT");
            Logger.addLogAdapter(new AndroidLogAdapter(logFormatStrategy));
        } else { //betatest环境打印console和文件
            //logcat
            FormatStrategy logFormatStrategy = StrategyFactory.getLogCatStrategy("HT");
            Logger.addLogAdapter(new AndroidLogAdapter(logFormatStrategy));
            //file
            FormatStrategy customFormatStrategy = StrategyFactory.getELKStrategy("HT", logStrategy);
            Logger.addLogAdapter(new AndroidLogAdapter(customFormatStrategy));
        }
    }

    public static void setEnable(boolean debug) {
        Log.d(TAG, "setEnable: " + debug);
        enable = debug;
    }

    public static void setLogLevel(int logLevel) {
        //线上环境只打印LEVEL_INFO及以上级别
        LOG_LEVEL = logLevel;
    }

    //<editor-fold desc="debugJson or xml">
    public static void debugJson(final Object tag, final String msg) {
        ThreadHelp.runInLogThread(new Runnable() {
            @Override
            public void run() {
                if (enable && LOG_LEVEL <= LEVEL_DEBUG) {
                    String tagName = getTagName(tag);
                    String content = getContent(msg);
                    Logger.t(tagName).json(content);
                }
            }
        });
    }

    public static void debugXml(final Object tag, final String msg) {
        ThreadHelp.runInLogThread(new Runnable() {
            @Override
            public void run() {
                if (enable && LOG_LEVEL <= LEVEL_DEBUG) {
                    String tagName = getTagName(tag);
                    String content = getContent(msg);
                    Logger.t(tagName).xml(content);
                }
            }
        });
    }
    //</editor-fold>

    //<editor-fold desc="日志打印">
    public static void v(Object tag, String m) {
        if (enable && LOG_LEVEL <= LEVEL_VERBOSE) {
            logHelper(tag, m, LEVEL_VERBOSE);
        }
    }

    public static void w(Object tag, String m) {
        if (enable && LOG_LEVEL <= LEVEL_WARN) {
            logHelper(tag, m, LEVEL_WARN);
        }
    }

    public static void d(Object tag, String m) {
        if (enable && LOG_LEVEL <= LEVEL_DEBUG) {
            logHelper(tag, m, LEVEL_DEBUG);
        }

    }

    public static void i(Object tag, String m) {
        if (enable && LOG_LEVEL <= LEVEL_INFO) {
            logHelper(tag, m, LEVEL_INFO);
        }

    }

    public static void e(Object tag, String m) {
        if (enable && LOG_LEVEL <= LEVEL_ERROR) {
            logHelper(tag, m, LEVEL_ERROR);
        }
    }

    public static void e(Object tag, String m, Throwable t) {
        if (enable && LOG_LEVEL <= LEVEL_ERROR) {
            logHelper(tag, m, LEVEL_ERROR);
        }
    }

    /**
     * 对于一些重要的日志，需要一定记录下来的
     *
     * @param tag
     * @param m
     */
    public static void f(Object tag, String m) {
        if (enable) {
            logHelper(tag, m, LEVEL_ERROR);
        }
    }


    //</editor-fold>


    //<editor-fold desc="log helper">

    private static void logHelper(final Object tag, final String msg, final int logLevel) {
        String tagName = getTagName(tag);
        String content = getContent(msg);
        if (logLevel == LEVEL_VERBOSE) {
            logVerbose(tagName, content);
        } else if (logLevel == LEVEL_DEBUG) {
            logDebug(tagName, content);
        } else if (logLevel == LEVEL_INFO) {
            logInfo(tagName, content);
        } else if (logLevel == LEVEL_WARN) {
            logWarning(tagName, content);
        } else if (logLevel == LEVEL_ERROR) {
            logError(tagName, content);
        } else {
            Log.e("LOG", "no such logLevel. logLevel: " + logLevel);
        }
    }

    private static void logError(String tagName, String content) {
        Logger.t(tagName).e(content);
    }

    private static void logWarning(String tagName, String content) {
        Logger.t(tagName).w(content);
    }

    private static void logInfo(String tagName, String content) {
        Logger.t(tagName).i(content);
    }

    private static void logDebug(String tagName, String content) {
        Logger.t(tagName).d(content);
    }

    private static void logVerbose(String tagName, String content) {
        Logger.t(tagName).v(content);
    }

    //</editor-fold>
    private static String getContent(String content) {
        return String.valueOf(content);
    }


    private static String getTagName(Object tag) {
        if (tag instanceof String) {
            return String.valueOf(tag);
        } else if (tag instanceof Class<?>) {
            return ((Class<?>) tag).getSimpleName();
        } else if (tag != null) {
            return getTagName(tag.getClass());
        } else {
            return TAG;
        }
    }

    private static void logToFile(final String tag, final String m) {
        if (FileLogger.LOG_TO_FILE) {
            ThreadHelp.runInLogThread(new Runnable() {
                @Override
                public void run() {
                    FileLogger.writeLine(tag, m);
                }
            });
        }
    }

    public static void exception(Throwable throwable) {
        if (enable && LOG_LEVEL <= LEVEL_ERROR) {
            String message = Throwable2String(throwable);
            L.e(LogType.EXCEPTION.mTag, message);
        }
    }

    /**
     * 忽略错误，用于应付 Sonar检查
     *
     * @param throwable 错误
     */
    public static void ignoreException(Throwable throwable) {
        // do nothing
    }

    @NonNull
    public static String Throwable2String(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(bos);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        return new String(bos.toByteArray());
    }

    /**
     * @param e
     */
    public static void throwExceptionIfDebug(Exception e) {
        if (BuildConfig.DEBUG) {//debug模式下便于发现问题
            throw new RuntimeException(e);
        } else {
            exception(e);
        }
    }
}
