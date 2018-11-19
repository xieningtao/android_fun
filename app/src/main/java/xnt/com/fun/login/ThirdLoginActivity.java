package xnt.com.fun.login;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.basesmartframe.baseui.BaseActivity;
import com.nostra13.universalimageloader.utils.L;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import xnt.com.fun.R;
import xnt.com.fun.Utils;

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

    private void initActionBar() {
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
        View actionView = LayoutInflater.from(this).inflate(R.layout.ny_home_title, null);
        getActionBar().setCustomView(actionView, params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toolbar parent = (Toolbar) actionView.getParent();
            parent.setContentInsetsAbsolute(0, 0);
        }
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
