package xnt.com.fun.rom;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;

class NYLGSettingAction extends BaseSettingAction {
    private static final String MANUFACTURER_LG = "LG";

    @Override
    public boolean isThisRom() {
        return MANUFACTURER_LG.equalsIgnoreCase(Build.MANUFACTURER);
    }

    @Override
    public boolean openSetting(Activity activity) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", activity.getPackageName());
        ComponentName comp = new ComponentName("com.android.settings",
                "com.android.settings.Settings$AccessLockSummaryActivity");
        intent.setComponent(comp);
        if (Rom.startSafely(activity, intent)) {
            return true;
        }
        return super.openSetting(activity);
    }
}
