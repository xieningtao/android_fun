package xnt.com.fun.login;

import android.app.Activity;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class WXLogin implements LoginAction {
    @Override
    public boolean login(Activity activity,UMAuthListener listener) {
//        IWXAPI wxapi = WXAPIFactory.createWXAPI(context, SFManifestUtil.getMetaValue(context, ShareConstant.WEI_XIN_APP_ID));
//        if (!wxapi.isWXAppInstalled()) {
//            // 请安装微信
//            return false;
//        }
//        wxapi.registerApp(SFManifestUtil.getMetaValue(context, ShareConstant.WEI_XIN_APP_ID));
//        final SendAuth.Req req = new SendAuth.Req();
//        req.scope = "snsapi_userinfo";
////        req.scope = "snsapi_base";
////        req.state = "wechat_sdk_demo_test";
//        req.state = "huatian";
//        wxapi.sendReq(req);
//        return true;
        if(!UMShareAPI.get(activity).isInstall(activity,SHARE_MEDIA.WEIXIN)){
            return false;
        }
        UMShareAPI.get(activity).getPlatformInfo(activity, SHARE_MEDIA.WEIXIN, listener);
        return true;
    }
}
