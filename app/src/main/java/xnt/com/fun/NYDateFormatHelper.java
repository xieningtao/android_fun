package xnt.com.fun;

import com.sf.utils.baseutil.DateFormatHelp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NYDateFormatHelper {
    private static final long TIME_UNIT_ONE_MINUTE = 1000 * 60;
    private static final long TIME_UNIT_ONE_HOUR = TIME_UNIT_ONE_MINUTE * 60;
    private static final long TIME_UNIT_ONE_DAY = TIME_UNIT_ONE_HOUR * 24;

    public static final SimpleDateFormat MM_DD_Format = new SimpleDateFormat("MM-dd");
    public static final SimpleDateFormat DAY_Date_Format = new SimpleDateFormat("yyyy-MM-dd");

    public static final String formatTime(String dateStr) {
        Date myDate =  DateFormatHelp.StrDateToCalendar(dateStr,DateFormatHelp._YYYYMMDDHHMM);
        long createdTime = myDate.getTime();
        long currentTime = System.currentTimeMillis();
        long distance = currentTime - createdTime;
        String timeText;
        if (distance < TIME_UNIT_ONE_MINUTE) {
            timeText = "刚刚";
        } else if (distance < TIME_UNIT_ONE_HOUR) {
            timeText = String.format("%d分钟前", distance / TIME_UNIT_ONE_MINUTE);
        } else if (createdTime >= getTodayBefore(0)) {
            timeText = String.format("%d小时前", distance / TIME_UNIT_ONE_HOUR);
        } else if (createdTime >= getTodayBefore(1)) {
            timeText = "昨天";
        } else if (createdTime >= getYearZero()) {
            Date date = new Date(createdTime);
            timeText = MM_DD_Format.format(date);
        } else {
            Date date = new Date(createdTime);
            timeText = DAY_Date_Format.format(date);
        }
        return timeText;
    }

    public static long getYearZero() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getTodayBefore(int day) {
        Calendar calendar = Calendar.getInstance();
        if (day != 0) {
            calendar.add(Calendar.DAY_OF_MONTH, -day);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
