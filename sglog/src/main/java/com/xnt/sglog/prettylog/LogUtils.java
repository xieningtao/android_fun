package com.xnt.sglog.prettylog;


import com.xnt.sglog.Constant;
import com.xnt.sglog.L;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

public class LogUtils {

    /**
     * 5m
     */
    private static final long MAX_LOG_SIZE = 1024 * 1024 * 5;


    public synchronized static void cleanExceedLogs(String rootLogDir) {
        //日志文件夹
        File logDir = new File(rootLogDir);
        if (logDir != null && logDir.exists() && logDir.isDirectory()) {
            List<File> logFiles = Arrays.asList(logDir.listFiles());
            Collections.sort(logFiles, new Comparator<File>() {
                @Override
                public int compare(File file, File t1) {
                    long fileOneTime = file.lastModified();
                    long fileTwoTime = t1.lastModified();
                    return (int) (fileOneTime - fileTwoTime);
                }
            });
            long totalSize = 0;
            if (logFiles != null && logFiles.size() > 0) {
                totalSize = getRemainderSize(logFiles);
                if (totalSize >= MAX_LOG_SIZE) {
                    Queue<File> fileQueue = new ArrayDeque<>(logFiles);
                    while (!fileQueue.isEmpty() && totalSize >= MAX_LOG_SIZE) {
                        File willRemovedFile = fileQueue.poll();
                        totalSize -= willRemovedFile.length();
                        willRemovedFile.delete();
                    }
                }
            }
        }
    }

    private static long getRemainderSize(List<File> logFiles) {
        long totalSize = 0;
        for (int i = 0; i < logFiles.size(); i++) {
            long size = logFiles.get(i).length();
            totalSize += size;
        }
        return totalSize;
    }
}
