package com.xnt.sglog.prettylog;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.orhanobut.logger.LogStrategy;
import com.xnt.sglog.DateUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HTDiskLogStrategy implements LogStrategy {
    @NonNull
    private static Handler handler;
    private static HandlerThread logThread;
    private static DateFormat dateFormat = new SimpleDateFormat("MM-dd hh:mm:ss:SSS");
    private static HTDiskLogStrategy strategy = null;
    private final String DEFAULT_TAG = "HT";

    private HTDiskLogStrategy(@NonNull String folder, int maxFileSize) {
        logThread = new HandlerThread("logFileThread");
        logThread.start();
        handler = new WriteHandler(logThread.getLooper(), folder, maxFileSize);
    }

    public static void destroyLogThreadIfNeeded() {
        if (strategy != null && logThread != null && logThread.isAlive()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                logThread.quitSafely();
            } else {
                logThread.quit();
            }
            handler = null;
            logThread = null;
            strategy = null;
        }
    }

    public static synchronized HTDiskLogStrategy createInstance(@NonNull String folder, int maxFileSize) {
        destroyLogThreadIfNeeded();
        if (strategy == null) {
            strategy = new HTDiskLogStrategy(folder, maxFileSize);
        }
        return strategy;
    }

    @Override
    public void log(int priority, @Nullable String tag, @NonNull String message) {
        //打印logcat
        if (tag == null) {
            tag = DEFAULT_TAG;
        }
        //存文件
        // do nothing on the calling thread, simply pass the tag/msg to the background thread
        StringBuilder contentBuild = getContent(tag, message);
        handler.sendMessage(handler.obtainMessage(priority, contentBuild.toString()));
    }

    @NonNull
    private StringBuilder getContent(@NonNull String tag, @NonNull String message) {
        StringBuilder contentBuild = new StringBuilder();
        String time = dateFormat.format(new Date(System.currentTimeMillis()));
        contentBuild.append(time).append(android.os.Process.myPid()).append("-")
                .append(Thread.currentThread().getId()).append(" ").append(tag)
                .append(" : ").append(message);
        return contentBuild;
    }

    private static class WriteHandler extends Handler {

        @NonNull
        private final String folder;
        private final int maxFileSize;
        private final String fileNamePrefix;
        private int newFileCount;

        WriteHandler(@NonNull Looper looper, @NonNull String folder, int maxFileSize) {
            super(looper);
            this.folder = folder;
            this.maxFileSize = maxFileSize;
            fileNamePrefix = DateUtils.dateTimeFormat(Calendar.getInstance(), DateUtils.YYYY_MM_DD);
            newFileCount = 0;
        }

        @SuppressWarnings("checkstyle:emptyblock")
        @Override
        public void handleMessage(@NonNull Message msg) {
            String content = (String) msg.obj;

            FileWriter fileWriter = null;

            File logFile = getLogFile(folder);

            try {
                fileWriter = new FileWriter(logFile, true);

                writeLog(fileWriter, content);

                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                if (fileWriter != null) {
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e1) { /* fail silently */ }
                }
            }
        }

        /**
         * This is always called on a single background thread.
         * Implementing classes must ONLY write to the fileWriter and nothing more.
         * The abstract class takes care of everything else including close the stream and catching IOException
         *
         * @param fileWriter an instance of FileWriter already initialised to the correct file
         */
        private void writeLog(@NonNull FileWriter fileWriter, @NonNull String content) throws IOException {
            fileWriter.append(content);
        }

        private File createNewFileIfNeeded(@NonNull File newFile, @NonNull String fileName) {
            while (newFile.exists()) {
                newFileCount++;
                newFile = new File(folder, String.format("%s_%s.txt", fileName, newFileCount));
            }
            return newFile;
        }

        private File getLogFile(@NonNull String folderName) {

            File folder = new File(folderName);
            if (!folder.exists()) {
                //TODO: What if folder is not created, what happens then?
                folder.mkdirs();
            }

            if (!folder.exists()) {
                Log.e("LOG", "fail to create log directory");
                return null;
            }

            File existingFile = new File(folder, String.format("%s_%s.txt", fileNamePrefix, newFileCount));
            if (existingFile.exists() && existingFile.length() >= maxFileSize) {
                return createNewFileIfNeeded(existingFile, fileNamePrefix);
            } else {
                return existingFile;
            }
        }
    }
}
