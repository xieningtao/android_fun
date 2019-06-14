package xnt.com.fun.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.sf.loglib.L;
import com.sf.utils.baseutil.NetWorkManagerUtil;
import com.sf.utils.baseutil.SFToast;
import com.sf.utils.baseutil.SpUtil;
import com.sflib.reflection.core.SFBus;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONObject;

import java.util.Map;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UpdateListener;
import xnt.com.fun.MessageId;
import xnt.com.fun.R;
import xnt.com.fun.Utils;
import xnt.com.fun.bean.NYBmobUser;
import xnt.com.fun.dialog.NYProgressDialog;

public class ThirdLoginActivity extends BaseActivity {
    private final String TAG = "ThirdLoginActivity";
    private Dialog mProgressDialog;
    private UMAuthListener umAuthListener = new UMAuthListener() {
        public void onStart(SHARE_MEDIA share_media) {
            L.info(TAG,"method->onStart");
            mProgressDialog.show();
        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            L.info(TAG, "method->onComplete map: " + map);
            if(map != null){
                String name = map.get("name");
                String avatarUrl = map.get("avatar_hd");
//                UserManager.getInstance().setNickName(name);
//                UserManager.getInstance().setAvatarUrl(avatarUrl);
                String accessToken = map.get("accessToken");
                String expiresIn = map.get("expires_in");
                String userId = map.get("uid");
                if(share_media == SHARE_MEDIA.SINA) {
                    NYBmobUser bmobUser = new NYBmobUser();
                    bmobUser.setAvatarUrl(avatarUrl);
                    bmobUser.setNick(name);
                    SpUtil.save(ThirdLoginActivity.this,LoginConstant.LOGIN_TYPE,LoginConstant.SINA);
                    registerOrLogin(BmobUser.BmobThirdUserAuth.SNS_TYPE_WEIBO,accessToken,expiresIn,userId,bmobUser);
                }else {
                    //TODO other plartform
                }
            }else {
                mProgressDialog.dismiss();
                SFToast.showToast(R.string.login_fail);
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            L.info(TAG, "method->onError throwable: " + throwable.getMessage());
            mProgressDialog.dismiss();
            SFToast.showToast(R.string.login_fail);
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            L.info(TAG, "method->onCancel i: " + i);
        }
    };

    public static void toLogin(Activity activity) {
        Intent intent = new Intent(activity, ThirdLoginActivity.class);
        activity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_login_activity);
        initView();
    }

    private void updateActionBar() {
        View rootView = getActionBar().getCustomView();
        rootView.setBackgroundColor(getResources().getColor(R.color.white));
        ImageView logoIv = (ImageView) rootView.findViewById(R.id.ny_logo);
        logoIv.setImageResource(R.drawable.app_icon);
        TextView titleTv = rootView.findViewById(R.id.ny_title_tv);
        titleTv.setText(R.string.login);
    }
    private void initView() {
        Utils.setActionBar(this);
        updateActionBar();
        mProgressDialog = NYProgressDialog.getProgressDialog(this,"正在登录中...");
//        UMShareConfig config = new UMShareConfig();
//        config.isNeedAuthOnGetUserInfo(true);
//        UMShareAPI.get(ThirdLoginActivity.this).setShareConfig(config);
        TextView weixinLoginTv = findViewById(R.id.weixin_tv);
        TextView sinaLoginTv = findViewById(R.id.sina_tv);

        weixinLoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetWorkManagerUtil.isNetworkAvailable()) {
                    WXLogin login = new WXLogin();
                    login.login(ThirdLoginActivity.this, umAuthListener);
                }else {
                    SFToast.showToast(R.string.net_unavailable);
                }
            }
        });

        sinaLoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetWorkManagerUtil.isNetworkAvailable()) {
                    SinaLogin login = new SinaLogin();
                    login.login(ThirdLoginActivity.this, umAuthListener);
                }else {
                    SFToast.showToast(R.string.net_unavailable);
                }
            }
        });
    }

    private void registerOrLogin(String snsType, String accessToken, String expiresIn, String userId, final NYBmobUser nyBmobUser){
        BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth(snsType,accessToken, expiresIn,userId);
        BmobUser.loginWithAuthData(authInfo, new LogInListener<JSONObject>() {
            @Override
            public void done(JSONObject userAuth,BmobException e) {
                mProgressDialog.dismiss();
                if(e == null){
                    updateUserInfo(nyBmobUser);
                }else {
                    SFToast.showToast(R.string.login_fail);
                }
            }
        });
    }

    private void updateUserInfo(NYBmobUser nyBmobUser){
        if(nyBmobUser == null){
            L.error(TAG,"method->updateUserInfo nyBmobUser is null");
            return;
        }
        BmobUser bmobUser = BmobUser.getCurrentUser(NYBmobUser.class);
        if(bmobUser != null){
            nyBmobUser.update(bmobUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e == null) {
                        SFBus.send(MessageId.USER_INFO_CHANGE);
                        ThirdLoginActivity.this.finish();
                    }else {
                        SFToast.showToast(R.string.login_fail);
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
