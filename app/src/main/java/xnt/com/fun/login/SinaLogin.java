package xnt.com.fun.login;

import android.app.Activity;

import com.sf.utils.baseutil.SFToast;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import xnt.com.fun.R;

public class SinaLogin implements LoginAction {

    @Override
    public boolean login(Activity activity, UMAuthListener listener) {
        if(!UMShareAPI.get(activity).isInstall(activity,SHARE_MEDIA.SINA)){
            SFToast.showToast(R.string.install_sina_app_tip);
            return false;
        }
        UMShareAPI.get(activity).getPlatformInfo(activity, SHARE_MEDIA.SINA, listener);
        return true;
    }
}
