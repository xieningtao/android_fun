package xnt.com.fun.rom;

import android.app.Activity;
import android.content.Intent;

class NYQikuSettingAction extends BaseSettingAction {

    @Override
    public boolean isThisRom() {
        return RomUtil.isQiku();
    }

    @Override
    public boolean openFloatWindowSetting(Activity activity) {

        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$OverlaySettingsActivity");
        if (Rom.startSafely(activity, intent)) {
            return true;
        }

        intent.setClassName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
        if (Rom.startSafely(activity, intent)) {
            return true;
        }

        return super.openFloatWindowSetting(activity);
    }
}
