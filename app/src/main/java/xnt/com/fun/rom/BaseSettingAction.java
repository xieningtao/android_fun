package xnt.com.fun.rom;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import xnt.com.fun.SFApp;


class BaseSettingAction implements Rom.RomType, Rom.RomAction {

    @Override
    public boolean isThisRom() {
        return false;
    }

    @Override
    public boolean openSetting(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + SFApp.gContext.getPackageName()));
//        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        return Rom.startSafely(activity, intent);
    }

    @Override
    public boolean openFloatWindowSetting(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23 && activity != null) {
            Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION");
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            return Rom.startSafely(activity, intent);
        }
        return false;
    }
}
