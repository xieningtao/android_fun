package com.xnt.sglog;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import static com.xnt.sglog.Constant.IDLE_SUFFIX;
import static com.xnt.sglog.Constant.UPLOADING_SUFFIX;
import static com.xnt.sglog.Constant.WRITE_SUFFIX;


/**
 * @author liangmingrui
 * 2018/6/4 16:49
 * 此类只负责收集elk日志写入文件，并不做文件上传功能，对外暴露文件上传的一些时机
 */
public class LogFileOperator {

    //<editor-fold desc="property">

    private static LogFileOperator instance = null;
    private final String TAG = "BaseHTELKLogStrategy";
    private final int SINGLE_LINE_LOG_LIMIT = 10 * 1024;
    private File currentElkFile;
    private BufferedWriter writer;
    private volatile int lineCounter = 0;
    private ElkOption elkOption = null;
    private int MAX_FILE_NUMBER = 8;
    private FilenameFilter mIdleFileNameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {//只含有IDLE标记的
            return !TextUtils.isEmpty(name) && name.contains(IDLE_SUFFIX);
        }
    };
    private FilenameFilter mUploadFileNameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {//只含有UPLOAD标记的
            return !TextUtils.isEmpty(name) && name.contains(UPLOADING_SUFFIX);
        }
    };

    private FilenameFilter mWritingFileNameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {//只含有WRTE标记的
            return !TextUtils.isEmpty(name) && name.contains(WRITE_SUFFIX);
        }
    };

    private Comparator mComparator = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            return o1.lastModified() > o2.lastModified() ? 1 : -1;
        }
    };
    //</editor-fold>

    //<editor-fold desc="public">

    private LogFileOperator() {

    }

    public static LogFileOperator getInstance() {
        if (instance == null) {
            synchronized (LogFileOperator.class) {
                if (instance == null) {
                    instance = new LogFileOperator();
                }
            }
        }
        return instance;
    }

    public FilenameFilter getIdleFileNameFilter() {
        return mIdleFileNameFilter;
    }

    public FilenameFilter getUploadFileNameFilter() {
        return mUploadFileNameFilter;
    }

    /**
     * 写入elk日志
     *
     * @param elkStr elk日志字符串
     */
    public synchronized void writeLine(String elkStr, boolean forceNewFile) {
        writeLine(elkStr, forceNewFile, null);
    }

    /**
     * 写入elk日志
     *
     * @param elkStr elk日志字符串
     */
    public synchronized void writeLine(String elkStr, boolean forceNewFile, OnElkFileFullListener onElkFileFull) {
        if (elkOption == null) {
            throw new IllegalArgumentException("elkOption must be init before log");
        }
        //某行日志太大，丢弃这条日志
        if (elkStr != null && elkStr.getBytes() != null && elkStr.getBytes().length > SINGLE_LINE_LOG_LIMIT) {
            elkStr = "this log is too long,size: " + elkStr.getBytes().length;
        }
        try {
            // 文件可能被删除
            if (writer == null || !currentElkFile.exists()) {
                makeNewElkFile(generateElkFile());
            }
            //暂时每一条日志都flush到文件,后续改成在某些条件下才flush用来提升性能
            writer.write(elkStr);
            writer.newLine();
            writer.flush();
            lineCounter++;
            //1、文件满了需要创建新文件；2、强制创建新文件
            if (lineCounter >= elkOption.getElkFileSize() || forceNewFile) {
                writer.close();
                writer = null;
                String newFileName = ElkHelper.remarkFile(elkOption.getElkStorageDir(),currentElkFile, WRITE_SUFFIX, IDLE_SUFFIX);
                if (onElkFileFull != null) {
                    onElkFileFull.onCurElkFileFull(elkOption.getElkStorageDir(), newFileName);
                }
                //检查日志是否满了，需要删除一些日志
                rmLogFileIfFull(elkOption.getElkStorageDir());
                makeNewElkFile(generateElkFile());
            }
        } catch (IOException e) {
            Log.e("LogFileOperator", "exception: " + e.getMessage());
        }
    }

    /**
     * 删除一定数量的日志
     *
     * @param folderName
     */
    private synchronized void rmLogFileIfFull(String folderName) {
        File folder = new File(folderName);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles(mIdleFileNameFilter);
            if (files != null && files.length >= MAX_FILE_NUMBER) {//超过一定的数量，删除一半的文件
                //对file进行顺序排序
                Arrays.sort(files, mComparator);
                int removedCount = 0;
                int reminderCount = 0;
                for (int i = 0; i < files.length; i++) {
                    Log.i(TAG, "method->rmLogFileIfFull fileName: " + files[i].getName());
                    files[i].delete();
                    removedCount++;
                    reminderCount = files.length - removedCount;
                    if (reminderCount <= MAX_FILE_NUMBER / 2) {
                        break;
                    }
                }
                Log.i(TAG, "removedCount: " + removedCount + " remiderCount: " + reminderCount);
            }
        }
    }

    /**
     * 获取当前elk日志行数
     *
     * @return elk日志行数
     */
    public int getLineCounter() {
        return lineCounter;
    }

    public String getElkStorageDir() {
        if (elkOption != null) {
            return elkOption.getElkStorageDir();
        } else {
            return "";
        }
    }

    //</editor-fold>

    //<editor-fold desc="private">

    /**
     * 做好文件上传前的准备
     *
     * @return 是否上传成功
     */
    public synchronized boolean startUploadElkFile(OnElkFilePreparedListener listener) {
        if (lineCounter <= 0 || writer == null) {
            return false;
        }
        boolean uploadResult = false;
        try {
            writer.close();
            writer = null;
            File dir = new File(elkOption.getElkStorageDir());
            if (dir.exists()) {//获取当前目录下的所有文件
                File[] files = dir.listFiles();
                if (listener != null) {
                    listener.onPreparedElkFiles(files);
                }
                uploadResult = true;
            }
            makeNewElkFile(generateElkFile());
        } catch (IOException e) {
            Log.e("LogFileOperator", "exception: " + e.getMessage());
        }
        return uploadResult;
    }

    /**
     * 在app初始化的时候调用
     *
     * @param option
     * @param listener
     */
    public synchronized void initAndConfigureElk(ElkOption option, OnElkFileScanListener listener) {
        if (option == null) {
            throw new IllegalArgumentException("option is null");
        }
        this.elkOption = option;
        try {
            File dir = new File(elkOption.getElkStorageDir());
            if (!dir.exists()) {
                dir.mkdirs();
            } else {
                //把之前的writing文件变成idle文件
                File[] mWriteFiles = dir.listFiles(mWritingFileNameFilter);
                for (File file : mWriteFiles) {
                    ElkHelper.remarkFile(elkOption.getElkStorageDir(),file, WRITE_SUFFIX, IDLE_SUFFIX);
                }

                //检查日志是否满了，需要删除一些日志
                rmLogFileIfFull(elkOption.getElkStorageDir());

                if (listener != null) {
                    listener.onScanElkFiles(elkOption.getElkStorageDir());
                }
            }
            makeNewElkFile(generateElkFile());
            initLineCounter();
        } catch (Exception e) {
            Log.e("LogFileOperator", "exception: " + e.getMessage());
        }
    }


    private void makeNewElkFile(File newElkFile) throws IOException {
        currentElkFile = newElkFile;
        File dir = currentElkFile.getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        if (!currentElkFile.exists()) {
            currentElkFile.createNewFile();
        }
        if (writer != null) {
            writer.close();
        }
        writer = new BufferedWriter(new FileWriter(currentElkFile, true));
        lineCounter = 0;
    }


    private File generateElkFile() {
        String date = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA)
                .format(new Date(System.currentTimeMillis()));
        return new File(elkOption.getElkStorageDir() + date + "_elk" + WRITE_SUFFIX);
    }

    private void initLineCounter() {
        lineCounter = 0;
        if (currentElkFile == null) {
            return;
        }
        LineNumberReader lnr = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(currentElkFile);
            lnr = new LineNumberReader(fileReader);
            long fileLength = currentElkFile.length();
            lnr.skip(fileLength);
            lineCounter = lnr.getLineNumber();
        } catch (IOException e) {
            L.exception(e);
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
                if (lnr != null) {
                    lnr.close();
                }
            } catch (IOException e) {
                Log.e("LogFileOperator", "exception: " + e.getMessage());
            }
        }
    }
    //</editor-fold>


    public interface OnElkFileFullListener {
        void onCurElkFileFull(String dir, String curFileName);
    }

    public interface OnElkFileScanListener {
        /**
         * 扫描现存的所有文件
         *
         * @param elkDir
         */
        void onScanElkFiles(String elkDir);
    }

    public interface OnElkFilePreparedListener {
        void onPreparedElkFiles(File[] files);
    }
}
