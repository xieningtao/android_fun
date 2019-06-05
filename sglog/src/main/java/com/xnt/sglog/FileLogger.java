package com.xnt.sglog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

class FileLogger {

    public static final boolean LOG_TO_FILE = L.enable;
    public static BufferedWriter mWriter;

    public static synchronized void init(String rootDir) {
        if (!LOG_TO_FILE) {
            return;
        }
        try {
            File dir = new File(rootDir + "/log/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
            String logName = rootDir + "/log/" + date + ".txt";
            mWriter = new BufferedWriter(new FileWriter(logName, true));
        } catch (Exception e) {
            L.exception(e);
        }
    }

    public static synchronized void writeLine(String tag, String text) {
        if (mWriter == null) {
            return;
        }
        try {
            String time = new SimpleDateFormat("MM-dd hh:mm:ss:SSS").format(new Date(System.currentTimeMillis()));
            text = time + " >>> pi: " + android.os.Process.myPid() +" "+ tag + " : " + text;
            mWriter.write(text);
            mWriter.newLine();
            mWriter.flush();
        } catch (IOException e) {
            L.exception(e);
        }
    }

    public static synchronized void close() {
        if (mWriter != null) {
            try {
                mWriter.close();
            } catch (IOException e) {
            }
            mWriter = null;
        }
    }

    public static void appendFile(String filePath, String content) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(filePath, true);
            writer.append(content).append('\n').append('\r');
        } catch (IOException e) {L.exception(e);} finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    L.exception(e);
                }
            }
        }
    }
}
