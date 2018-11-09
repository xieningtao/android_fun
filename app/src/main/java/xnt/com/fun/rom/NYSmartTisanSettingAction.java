package xnt.com.fun.rom;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;

class NYSmartTisanSettingAction extends BaseSettingAction {

    @Override
    public boolean isThisRom() {
        return RomUtil.isSmartisan();
    }

    @Override
    public boolean openFloatWindowSetting(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 锤子 坚果|5.1.1|2.5.3
            Intent intent = new Intent("com.smartisanos.security.action.SWITCHED_PERMISSIONS_NEW");
            intent.setClassName("com.smartisanos.security", "com.smartisanos.security.SwitchedPermissions");
            intent.putExtra("index", 17); // 不同版本会不一样
            return Rom.startSafely(activity, intent);
        } else {
            // 锤子 坚果|4.4.4|2.1.2
            Intent intent = new Intent("com.smartisanos.security.action.SWITCHED_PERMISSIONS");
            intent.setClassName("com.smartisanos.security", "com.smartisanos.security.SwitchedPermissions");
            intent.putExtra("permission", new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW});

            //        Intent intent = new Intent("com.smartisanos.security.action.MAIN");
            //        intent.setClassName("com.smartisanos.security", "com.smartisanos.security.MainActivity");
            //        return Rom.startSafely(activity, intent);
        }
        return super.openFloatWindowSetting(activity);
    }
}
