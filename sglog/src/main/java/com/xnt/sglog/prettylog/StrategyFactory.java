package com.xnt.sglog.prettylog;

import android.support.annotation.NonNull;

import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.LogcatLogStrategy;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.xnt.sglog.DateUtils;

import java.util.Date;

/**
 * 创建多种日志格式
 */
public class StrategyFactory {

    public static FormatStrategy getLogCatStrategy(String globalTag){
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag(globalTag)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .logStrategy(new LogcatLogStrategy())
                .build();
        return formatStrategy;
    }

    public static FormatStrategy getCustomStrategy(@NonNull String globalTag, @NonNull String folder, int maxSize){
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag(globalTag)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .logStrategy(HTDiskLogStrategy.createInstance(folder,maxSize))
                .build();
        return formatStrategy;
    }

    public static FormatStrategy getELKStrategy(@NonNull String globalTag, LogStrategy logStrategy){
        PlainFormatStrategy formatStrategy = PlainFormatStrategy.newBuilder()
                .tag(globalTag)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .logStrategy(logStrategy)
                .build();
        return formatStrategy;
    }

    public static FormatStrategy getTxtStrategy(@NonNull String globalTag, @NonNull String folder, int maxSize){
        TxtFormatStrategy formatStrategy = TxtFormatStrategy.newBuilder()
                .date(new Date())
                .dateFormat(DateUtils._YYYYMMDDHHMMSS_SSS_FORMAT)
                .tag(globalTag)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .logStrategy(HTDiskLogStrategy.createInstance(folder,maxSize))
                .build();
        return formatStrategy;
    }
}
