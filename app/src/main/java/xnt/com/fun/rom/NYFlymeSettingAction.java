package xnt.com.fun.rom;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;


class NYFlymeSettingAction extends BaseSettingAction {

    @Override
    public boolean isThisRom() {
        return RomUtil.isFlyme();
    }

    @Override
    public boolean openSetting(Activity activity) {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", activity.getPackageName());
        ComponentName comp = new ComponentName("com.meizu.safe", "com.meizu.safe.SecurityMainActivity");
        intent.setComponent(comp);
        return Rom.startSafely(activity, intent);
    }

    @Override
    public boolean openFloatWindowSetting(Activity activity) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
        intent.putExtra("packageName", activity.getPackageName());
        if (Rom.startSafely(activity, intent)){
            return true;
        }

        return super.openFloatWindowSetting(activity);
    }
}
