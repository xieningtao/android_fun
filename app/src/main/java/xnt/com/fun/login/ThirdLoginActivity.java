package xnt.com.fun.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.nostra13.universalimageloader.utils.L;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import xnt.com.fun.R;

public class ThirdLoginActivity extends BaseActivity {
    private final String TAG = "ThirdLoginActivity";
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
            L.i(TAG,"method->onStart");
        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            L.i(TAG, "method->onComplete map: " + map);
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            L.i(TAG, "method->onError throwable: " + throwable.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            L.i(TAG, "method->onCancel i: " + i);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_login_activity);
        initView();
    }

    private void initView() {
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMShareAPI.get(ThirdLoginActivity.this).setShareConfig(config);
        TextView weixinLoginTv = findViewById(R.id.weixin_tv);
        TextView sinaLoginTv = findViewById(R.id.sina_tv);

        weixinLoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WXLogin login = new WXLogin();
                login.login(ThirdLoginActivity.this, umAuthListener);
            }
        });

        sinaLoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SinaLogin login = new SinaLogin();
                login.login(ThirdLoginActivity.this, umAuthListener);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
