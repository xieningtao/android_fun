package com.xnt.sglog;

import android.text.TextUtils;


import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * 常用时间的format
 *
 * @author xieningtao
 */
public class DateUtils {

    // refactor to enum
    public static final String _YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";
    public static final String _YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String _YYYYMMDDHHMMSS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String SLASH_YYYYMMDD = "yyyy/MM/dd";
    public static final String YYYYMMDD = "yyyyMMddHH";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String FULL_YYYYMMDDHHMMSS="yyyyMMddHHmmss";

    /**
     * general simple data format
     */
    public final static SimpleDateFormat _YYYYMMDDHHMM_FORMAT = new SimpleDateFormat(DateUtils._YYYYMMDDHHMM);
    public final static SimpleDateFormat _YYYYMMDDHHMMSS_FORMAT = new SimpleDateFormat(DateUtils._YYYYMMDDHHMMSS);
    public final static SimpleDateFormat _YYYYMMDDHHMMSS_SSS_FORMAT = new SimpleDateFormat(DateUtils._YYYYMMDDHHMMSS_SSS);
    public final static SimpleDateFormat SLASH_YYYYMMDD_FORMAT = new SimpleDateFormat(DateUtils.SLASH_YYYYMMDD);
    public final static SimpleDateFormat DATE_YYYY_MM_DD_FORMAT = new SimpleDateFormat(DateUtils.YYYY_MM_DD);

    public static String dateTimeFormat(Calendar calender, String format_str) {
        if (calender == null || TextUtils.isEmpty(format_str))
            return "";
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern(format_str);
        return format.format(calender.getTime());
    }

    public static Date StrDateToCalendar(String cotent, String format_str) {
        if (TextUtils.isEmpty(cotent) || TextUtils.isEmpty(format_str))
            return null;
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern(format_str);
        try {
            return format.parse(cotent);
        } catch (ParseException e) {
            L.e(DateUtils.class, e.getMessage());
            return null;
        }
    }

    public static String DateTimeToStr(Date date, String format_str) {
        if (date == null || TextUtils.isEmpty(format_str))
            return null;
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern(format_str);
        return format.format(date);
    }

    public static String strToFormatStr(String dateStr){
        if (dateStr == null)
            return null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmm");
            Date date = format.parse(dateStr);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            String formatStr = format1.format(date);
            return formatStr;
        }
        catch (Exception e) {
            L.exception(e);
            return null;
        }

    }

    public static String formatStr(String dateStr, String orginFormat, String destinFormat){
        if (dateStr == null)
            return "";
        try {
            SimpleDateFormat format = new SimpleDateFormat(orginFormat);
            Date date = format.parse(dateStr);
            SimpleDateFormat format1 = new SimpleDateFormat(destinFormat);
            String formatStr = format1.format(date);
            return formatStr;
        }
        catch (Exception e) {
            L.exception(e);
            return "";
        }

    }

    public static int parseInt(String string, int defaultValue){
        try {
            defaultValue = Integer.parseInt(string);
        } catch (Exception e) {
            L.exception(e);
        }
        return defaultValue;
    }

    public static String getLeftTime(String time) {
        //hh:mm:ss
        int second = parseInt(time, 0);
        int h = (second / 60 / 60);
        int m = (second - h * 60 * 60) / 60;
        int s = (second - h * 60 * 60 - m * 60);
        String times = String.format("%02d", h) + ":" + String.format("%02d", m) + ":" + String.format("%02d", s);
        return times;
    }


    public static long formatTimeLong(String formatTime) {
        int second = parseInt(formatTime, 0);
        int h = (second / 60 / 60);
        int m = (second - h * 60 * 60) / 60;
        int s = (second - h * 60 * 60 - m * 60);
        long totalSec = h * 3600 + m * 60 + s;
        return totalSec;
    }

    public static String fomateTimeString(long times) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式。
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(times);
        return hms;
    }

    public static boolean isSameDay(long time1, long time2) {
        if (Math.abs(time1 - time2) > 24 * 3600 * 1000) {
            return false;
        }

        if (time1 / (24 * 3600 * 1000) == time2 / (24 * 3600 * 1000)) {
            return true;
        }

        return false;
    }

    public static boolean isYestoday(long time) {
        long currentTime = System.currentTimeMillis();
        if (currentTime / (24 * 3600 * 1000) == time / (24 * 3600 * 1000) + 1) {
            return true;
        }
        return false;
    }

    public static long strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate.getTime();
    }



    public static String getDate(long time) {
        String date = "";
        try {
            long differ = System.currentTimeMillis() - time;
            if (differ <= 60000) {
                return "刚刚";
            }
            long between = (differ) / 1000;// 除以1000是为了转换成秒

            long day = between / (24 * 3600);
            long hour = between % (24 * 3600) / 3600;
            long minute = between % 3600 / 60;
            long second = between % 60;

            if (day >= 1) {
                date = DATE_YYYY_MM_DD_FORMAT.format(new Date(time));
            } else if (hour > 0 && day == 0) {
                date = hour + "小时前";
            } else if (minute > 0 && hour == 0 && day == 0) {
                date = minute + "分钟前";
            } else if (second > 0 && minute == 0 && hour == 0 && day == 0) {
                date = second + "秒前";
            }

        } catch (Exception e) {
           L.exception(e);
        }
        return date;
    }
}
