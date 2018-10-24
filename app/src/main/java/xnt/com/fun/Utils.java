package xnt.com.fun;

import android.app.Activity;

import com.sf.utils.baseutil.SystemUIWHHelp;
import com.sf.utils.baseutil.UnitHelp;

public class Utils {

    public static int getPicWidth(Activity activity) {
        int screenWidth = SystemUIWHHelp.getScreenRealWidth(activity);
        int reminderWidth = screenWidth - UnitHelp.dip2px(activity, 8 + 8);
        return reminderWidth;
    }

    public static int getPicHeight(int width, float ratio) {
        return (int) (width / ratio);
    }
}
