package xnt.com.fun.rom;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

class NYLetvSettingAction extends BaseSettingAction {

    @Override
    public boolean isThisRom() {
        return super.isThisRom();
    }

    @Override
    public boolean openSetting(Activity activity) {

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", activity.getPackageName());
        ComponentName comp = new ComponentName("com.letv.android.letvsafe",
                "com.letv.android.letvsafe.PermissionAndApps");
        intent.setComponent(comp);
        if(Rom.startSafely(activity, intent)) {
            return true;
        }
        return super.openSetting(activity);
    }
}
