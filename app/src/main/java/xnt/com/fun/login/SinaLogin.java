package xnt.com.fun.login;

import android.app.Activity;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class SinaLogin implements LoginAction {

    @Override
    public boolean login(Activity activity, UMAuthListener listener) {
        if(!UMShareAPI.get(activity).isInstall(activity,SHARE_MEDIA.SINA)){
            return false;
        }
        UMShareAPI.get(activity).getPlatformInfo(activity, SHARE_MEDIA.SINA, listener);
        return true;
    }
}
