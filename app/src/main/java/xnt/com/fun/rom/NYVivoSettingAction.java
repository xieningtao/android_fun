package xnt.com.fun.rom;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

class NYVivoSettingAction extends BaseSettingAction {
    @Override
    public boolean isThisRom() {
        return RomUtil.isVivo();
    }

    @Override
    public boolean openSetting(Activity activity) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("package", activity.getPackageName());
        ComponentName comp = new ComponentName("com.iqoo.secure", "com.iqoo.secure.MainActivity");
        intent.setComponent(comp);
        return Rom.startSafely(activity, intent);
    }

    @Override
    public boolean openFloatWindowSetting(Activity activity) {
        // 不支持直接到达悬浮窗设置页，只能到 i管家 首页
        Intent intent = new Intent("com.iqoo.secure");
        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.MainActivity");
        // com.iqoo.secure.ui.phoneoptimize.SoftwareManagerActivity
        // com.iqoo.secure.ui.phoneoptimize.FloatWindowManager
        if (Rom.startSafely(activity, intent)){
            return true;
        }

        return super.openFloatWindowSetting(activity);
    }
}
