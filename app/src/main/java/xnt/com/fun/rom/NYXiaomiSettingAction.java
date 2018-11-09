package xnt.com.fun.rom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

class NYXiaomiSettingAction extends BaseSettingAction {
    private static final String ROM_MIUI_V5 = "V5";
    private static final String ROM_MIUI_V6 = "V6";
    private static final String ROM_MIUI_V7 = "V7";
    private static final String ROM_MIUI_V8 = "V8";

    private static String getMiuiVersion() {
        String propName = "ro.miui.ui.version.name";
        try {
            return RomUtil.getProp(propName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public boolean isThisRom() {
        return RomUtil.isMiui();
    }

    @Override
    public boolean openSetting(Activity activity) {
        String rom = getMiuiVersion();
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        if (ROM_MIUI_V5.equals(rom)) {
            Uri packageURI = Uri.parse("package:" + activity.getApplicationInfo().packageName);
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        } else if (ROM_MIUI_V6.equals(rom) || ROM_MIUI_V7.equals(rom)) {
            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", activity.getPackageName());
        } else if (ROM_MIUI_V8.equals(rom)) {
            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", activity.getPackageName());
        }
        return Rom.startSafely(activity, intent);
    }

    @Override
    public boolean openFloatWindowSetting(Activity context) {
        String versionName = getMiuiVersion();
        if (ROM_MIUI_V5.equals(versionName) || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) { //miui v5 的支持的android版本最高 4.x
            // http://www.romzj.com/list/search?keyword=MIUI%20V5#search_result
            String packageName = context.getPackageName();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", packageName, null);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (isIntentAvailable(intent, context) && Rom.startSafely(context, intent)) {
                return true;
            }
        }

        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        if (isIntentAvailable(intent, context) && Rom.startSafely(context, intent)) {
            return true;
        }
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        if (isIntentAvailable(intent, context) && Rom.startSafely(context, intent)) {
            return true;
        }

        intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setPackage("com.miui.securitycenter");
        intent.putExtra("extra_pkgname", context.getPackageName());
        if (isIntentAvailable(intent, context) && Rom.startSafely(context, intent)){
            return true;
        }

        return super.openFloatWindowSetting(context);
    }

    private static boolean isIntentAvailable(Intent intent, Context context) {
        if (intent == null) {
            return false;
        }
        return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }
}
