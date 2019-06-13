package com.xnt.sglog;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.Locale;

public class ElkHelper {
    private static final String TAG = "ElkHelper";

    // 上报elk日志需要转义的字符集，否则会上报失败
    public static final char[] ELK_ESCAPE_CHARS = new char[]{'%'};

    /**
     * 把字符串中某些字符转义为ascii字符
     *
     * @param source 源字符串
     * @param chars  需要转义的字符
     * @return 转义后的字符串
     */
    public static String replaceCharsByAscii(String source, char[] chars) {
        if (source == null || chars == null) {
            return "";
        }
        String result = source;
        for (char c : chars) {
            result = result.replace(c + "", getCharAsciiStr(c));
        }
        return result;
    }

    private static String getCharAsciiStr(char c) {
        int i = (int) c;
        return String.format(Locale.CHINA, "\\u%04x", i);
    }

    public static File markFile(String rootLogPath, File file, String markLabel) {
        if (file == null || !file.exists()) {
            return file;
        }
        final File uploadFile = new File(rootLogPath, file.getName() + markLabel);
        file.renameTo(uploadFile);
        return file;
    }

    public static String remarkFile(String rootLogPath, File file, String markLabel, String remarkLabel) {
        if (file == null || !file.exists()) {
            return "";
        }
        if (!TextUtils.isEmpty(file.getName()) && file.getName().contains(markLabel)) {
            String remarkFileName = file.getName().replace(markLabel, remarkLabel);
            final File uploadFile = new File(rootLogPath, remarkFileName);
            boolean result = file.renameTo(uploadFile);
            Log.i(TAG, "method->remarkFile result: " + result + " file state: " + file.exists() + " uploadFile state: " + uploadFile.exists());
            return uploadFile.getName();
        }
        return "";
    }
}
