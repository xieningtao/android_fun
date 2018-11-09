package xnt.com.fun.rom;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

import static xnt.com.fun.rom.Rom.startSafely;

class NYOppoSettingAction extends BaseSettingAction {
    @Override
    public boolean isThisRom() {
        return RomUtil.isOppo();
    }

    @Override
    public boolean openSetting(Activity activity) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", activity.getPackageName());
        ComponentName comp = new ComponentName("com.coloros.safecenter",
                "com.coloros.safecenter.permission.singlepage.PermissionSinglePageActivity");
        intent.setComponent(comp);
        return startSafely(activity, intent);
    }

    @Override
    public boolean openFloatWindowSetting(Activity activity) {
        Intent intent = new Intent();
        intent.putExtra("packageName", activity.getPackageName());
        // OPPO A53|5.1.1|2.1
        intent.setAction("com.oppo.safe");
        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.floatwindow.FloatWindowListActivity");
        if (startSafely(activity, intent)) {
            return true;
        }

        // OPPO R7s|4.4.4|2.1
        intent.setAction("com.color.safecenter");
        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.floatwindow.FloatWindowListActivity");
        if (startSafely(activity, intent)) {
            return true;
        }

        intent.setAction("com.coloros.safecenter");
        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.permission.floatwindow.FloatWindowListActivity");
        if (startSafely(activity, intent)) {
            return true;
        }


        intent.setAction("com.coloros.safecenter");
        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");
        if (startSafely(activity, intent)) {
            return true;
        }

        intent.setAction("com.color.safecenter");
        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
        if (startSafely(activity, intent)) {
            return true;
        }

        return super.openFloatWindowSetting(activity);
    }
}
