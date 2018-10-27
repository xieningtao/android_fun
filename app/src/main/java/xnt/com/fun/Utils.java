package xnt.com.fun;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toolbar;

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

    public static void setActionBar(Activity activity) {
        if (activity == null) {
            return;
        }
        activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
        View actionView = LayoutInflater.from(activity).inflate(R.layout.ny_home_title, null);
        activity.getActionBar().setCustomView(actionView, params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toolbar parent = (Toolbar) actionView.getParent();
            parent.setContentInsetsAbsolute(0, 0);
        }
        actionView.setBackgroundColor(activity.getResources().getColor(R.color.white));
        ImageView logoIv = (ImageView) actionView.findViewById(R.id.ny_logo);
        logoIv.setImageResource(R.drawable.app_icon);
    }
}
