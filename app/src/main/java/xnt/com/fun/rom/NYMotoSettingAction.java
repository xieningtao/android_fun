package xnt.com.fun.rom;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

class NYMotoSettingAction extends BaseSettingAction {
    @Override
    public boolean isThisRom() {
        return RomUtil.isMoto();
    }

    @Override
    public boolean openSetting(Activity activity) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("package", activity.getPackageName());
        ComponentName comp = new ComponentName("com.zui.safecenter",
                "com.lenovo.safecenter.MainTab.LeSafeMainActivity");
        intent.setComponent(comp);
        return Rom.startSafely(activity, intent);
    }

    @Override
    public boolean openFloatWindowSetting(Activity activity) {
        Intent intent = new Intent();
        intent.setClassName("com.zui.safecenter", "com.shenqi.xuipermissionmanager.XuiPermissionManager");
        if (Rom.startSafely(activity, intent)){
            return true;
        }

        return super.openFloatWindowSetting(activity);
    }
}
