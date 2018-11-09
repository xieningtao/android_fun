package xnt.com.fun.rom;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;

class NYSonySettingAction extends BaseSettingAction {
    private static final String MANUFACTURER_SONY = "SONY"; // 索尼

    @Override
    public boolean isThisRom() {
        return MANUFACTURER_SONY.equalsIgnoreCase(Build.MANUFACTURER);
    }

    @Override
    public boolean openSetting(Activity activity) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", activity.getPackageName());
        ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
        intent.setComponent(comp);
        if (Rom.startSafely(activity, intent)) {
            return true;
        }
        return super.openSetting(activity);
    }
}
