package xnt.com.fun.rom;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;

class NYHuaWeiSettingAction extends BaseSettingAction {
    private final static String HUAWEI_PACKAGE = "com.huawei.systemmanager";

    /**
     * 获取 emui 版本号
     * @return
     */
    public static double getEmuiVersion() {
        try {
            String emuiVersion = RomUtil.getProp("ro.build.version.emui");
            String version = emuiVersion.substring(emuiVersion.indexOf("_") + 1);
            return Double.parseDouble(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 4.0;
    }

    @Override
    public boolean isThisRom() {
        return RomUtil.isEmui();
    }

    @Override
    public boolean openSetting(Activity activity) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", activity.getPackageName());
        ComponentName comp = new ComponentName("com.huawei.systemmanager",
                "com.huawei.permissionmanager.ui.MainActivity");
        intent.setComponent(comp);
        return Rom.startSafely(activity, intent);
    }

    @Override
    public boolean openFloatWindowSetting(Activity context) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setClassName(HUAWEI_PACKAGE, "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");//悬浮窗管理页面
            if (Rom.startSafely(context, intent)) {
                return true;
            }
        }
        // Huawei Honor P6|4.4.4|3.0
        intent.setClassName(HUAWEI_PACKAGE, "com.huawei.notificationmanager.ui.NotificationManagmentActivity");
        intent.putExtra("showTabsNumber", 1);
        if (Rom.startSafely(context, intent)) {
            return true;
        }
        intent.setClassName(HUAWEI_PACKAGE, "com.huawei.permissionmanager.ui.MainActivity");
        if (Rom.startSafely(context, intent)) {
            return true;
        }
        intent.setClassName("com.Android.settings", "com.android.settings.permission.TabItem");//权限管理页面 android4.4
        if (Rom.startSafely(context, intent)) {
            return true;
        }

        return super.openFloatWindowSetting(context);
    }
}
