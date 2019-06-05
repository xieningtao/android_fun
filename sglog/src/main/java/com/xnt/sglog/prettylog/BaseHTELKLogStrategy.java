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
import com.xnt.sglog.LogFileOperator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

abstract public class BaseHTELKLogStrategy implements LogStrategy {

    public interface OnLogPrepared{
        void onLogPrepared(long time, String elkDir);
    }
//    public static class ElkBasicParam{
//        private String
//    }
    private String userId;
    @NonNull
    private static WriteHandler handler;
    private static HandlerThread logThread;
    private static DateFormat dateFormat = new SimpleDateFormat("MM-dd hh:mm:ss:SSS");
    private static BaseHTELKLogStrategy strategy = null;
    public static final int FORCE_TO_NEW_FILE_CODE = 100;
    private OnLogPrepared mOnLogPrepared = null;
    private static final String DEFAULT_TAG = "HT";
    public BaseHTELKLogStrategy(@NonNull String folder, int maxFileSize) {
        logThread = new HandlerThread("logFileThread");
        logThread.start();
        handler = new BaseHTELKLogStrategy.WriteHandler(logThread.getLooper(), folder, maxFileSize);
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

    public static void flushLog(OnLogPrepared logPrepared,String beanStr){
        handler.setOnLogPreparedListener(logPrepared);
        handler.sendMessage(handler.obtainMessage(FORCE_TO_NEW_FILE_CODE, beanStr));
    }

    abstract public String transformLogTo(int priority,String tag,String message);
    @Override
    public void log(int priority, @Nullable String tag, @NonNull String message) {
        //打印logcat
        if (tag == null) {
            tag = DEFAULT_TAG;
        }
        //存文件
        // do nothing on the calling thread, simply pass the tag/msg to the background thread
        String beanStr = transformLogTo(priority,tag,message);
        handler.sendMessage(handler.obtainMessage(priority, beanStr));
    }

    private static class WriteHandler extends Handler {

        @NonNull
        private final String folder;
        private final int maxFileSize;
        private final String fileNamePrefix;
        private int newFileCount;
        private volatile OnLogPrepared logPrepared;


        WriteHandler(@NonNull Looper looper, @NonNull String folder, int maxFileSize) {
            super(looper);
            this.folder = folder;
            this.maxFileSize = maxFileSize;
            fileNamePrefix = DateUtils.dateTimeFormat(Calendar.getInstance(), DateUtils.YYYY_MM_DD);
            newFileCount = 0;
        }

        public void setOnLogPreparedListener(OnLogPrepared logPreparedListener){
            logPrepared = logPreparedListener;
        }

        @SuppressWarnings("checkstyle:emptyblock")
        @Override
        public void handleMessage(@NonNull Message msg) {
            int code = msg.what;
            final String content = (String) msg.obj;
            boolean forceToNewFile = false;
            if(code == FORCE_TO_NEW_FILE_CODE){
                forceToNewFile = true;
                Log.i("BaseHTELKLogStrategy","code: "+code+" content: "+content);
            }
            LogFileOperator.getInstance().writeLine(content,forceToNewFile);
            if(forceToNewFile && logPrepared != null){
                //当前时间点之前的文件都可以上传
                logPrepared.onLogPrepared(System.currentTimeMillis(), LogFileOperator.getInstance().getElkStorageDir());
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



        private File getLogFile(@NonNull String folderName,boolean force) {

            File folder = new File(folderName);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            if (!folder.exists()) {
                Log.e("LOG", "fail to create log directory");
                return null;
            }

            File existingFile = new File(folder, String.format("%s_%s.txt", fileNamePrefix, newFileCount));
            if (existingFile.exists() && existingFile.length() >= maxFileSize||force) {
                return createNewFileIfNeeded(existingFile, fileNamePrefix);
            } else {
                return existingFile;
            }
        }
    }
}
