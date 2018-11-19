package xnt.com.fun;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.sf.loglib.L;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.analytics.MobclickAgent;

import cdc.sed.yff.AdManager;
import cdc.sed.yff.nm.sp.SplashViewSettings;
import cdc.sed.yff.nm.sp.SpotManager;
import cdc.sed.yff.nm.sp.SpotRequestListener;
import xnt.com.fun.config.NYBMobConfig;

/**
 * Created by mac on 2018/6/2.
 */

public class SplashActivity extends FragmentActivity {

    private final String TAG = "SplashActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        AdManager.getInstance(this).init(NYBMobConfig.YOU_MI_ID, NYBMobConfig.YOU_MI_KEY, true);
//        FansStationManager.getInstance(this).onAppLaunch();
        SpotManager.getInstance(this).requestSpot(new SpotRequestListener() {
            @Override
            public void onRequestSuccess() {
                L.info(TAG, "spot onRequestSuccess");
            }

            @Override
            public void onRequestFailed(int i) {
                L.error(TAG, "spot onRequestFailed result: " + i);
            }
        });
        SplashViewSettings splashViewSettings = new SplashViewSettings();
        splashViewSettings.setTargetClass(NYHomeActivity.class);
        ViewGroup splashContainer = findViewById(R.id.splash_container);
        splashViewSettings.setSplashViewContainer(splashContainer);
        SpotManager.getInstance(this).showSplash(this,
                splashViewSettings, null);
//        ThreadHelp.runInMain(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(SplashActivity.this,NYHomeActivity.class);
//                SplashActivity.this.startActivity(intent);
//                SplashActivity.this.finish();
//            }
//        },2000);
    }

    private void getNecessaryPermission(){
        final RxPermissions rxPermissions = new RxPermissions(this);
//        rxPermissions.requestEach()
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 开屏展示界面的 onDestroy() 回调方法中调用
        SpotManager.getInstance(this).onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
}
