package xnt.com.fun;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.sf.loglib.L;
import com.sflib.reflection.core.ThreadHelp;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import cdc.sed.yff.nm.sp.SpotManager;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import xnt.com.fun.permission.NYPermissionUtil;

/**
 * Created by mac on 2018/6/2.
 */

public class SplashActivity extends FragmentActivity implements SplashADListener {

    private final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
//        AdManager.getInstance(this).init(NYBMobConfig.YOU_MI_ID, NYBMobConfig.YOU_MI_KEY, true);
////        FansStationManager.getInstance(this).onAppLaunch();
//        SpotManager.getInstance(this).requestSpot(new SpotRequestListener() {
//            @Override
//            public void onRequestSuccess() {
//                L.info(TAG, "spot onRequestSuccess");
//            }
//        },2000);

//        fetchSplashAD(this, container, skipView, Constants.APPID, Constants.SplashPosID, this, 0);
//
//            @Override
//            public void onRequestFailed(int i) {
//                L.error(TAG, "spot onRequestFailed result: " + i);
//            }
//        });
//        SplashViewSettings splashViewSettings = new SplashViewSettings();
//        splashViewSettings.setTargetClass(NYHomeActivity.class);
//        ViewGroup splashContainer = findViewById(R.id.splash_container);
//        splashViewSettings.setSplashViewContainer(splashContainer);
//        SpotManager.getInstance(this).showSplash(this,
//                splashViewSettings, null);
        getNecessaryPermission();
    }

    private void toNext() {
        ThreadHelp.runInMain(new Runnable() {
            @Override
            public void run() {
                NYHomeActivity.toHomeActivity(SplashActivity.this);
                SplashActivity.this.finish();
            }
        }, 500);
    }

    private void getNecessaryPermission() {
        final RxPermissions rxPermissions = new RxPermissions(this);
        String permissions[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> ungrantedPermission = NYPermissionUtil.getUngrantedPermissionList(permissions);
        if (ungrantedPermission == null || ungrantedPermission.isEmpty()) {
            toNext();
            return;
        }
        rxPermissions.requestEach(ungrantedPermission.toArray(new String[ungrantedPermission.size()]))
                .subscribe(new Observer<Permission>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Permission permission) {
                        String permissionName = permission.name;
                        boolean granted = permission.granted;
                        if (!granted) {
                            boolean permissionReadPhone = NYPermissionUtil.checkPermission(Manifest.permission.READ_PHONE_STATE);
                            if (permissionReadPhone) {
                                permissionName = permissionName.replace(Manifest.permission.READ_PHONE_STATE, "");
                            }
                            boolean permissionWrite = NYPermissionUtil.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            if (permissionWrite) {
                                permissionName = permissionName.replace(Manifest.permission.WRITE_EXTERNAL_STORAGE, "");
                            }
                            if (permissionReadPhone && permissionWrite) {
                                granted = true;
                            }
                        }

                        //必要权限通过后
                        if (granted) {
                            // `permission.name` is granted !
                            toNext();
                        } else {
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
    public void onPointerCaptureChanged(boolean hasCapture) {

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


    /**
     * 拉取开屏广告，开屏广告的构造方法有3种，详细说明请参考开发者文档。
     *
     * @param activity      展示广告的 activity
     * @param adContainer   展示广告的大容器
     * @param skipContainer 自定义的跳过按钮：传入该 view 给 SDK 后，SDK 会自动给它绑定点击跳过事件。SkipView 的样式可以由开发者自由定制，其尺寸限制请参考 activity_splash.xml 或下面的注意事项。
     * @param appId         应用 ID
     * @param posId         广告位 ID
     * @param adListener    广告状态监听器
     * @param fetchDelay    拉取广告的超时时长：即开屏广告从请求到展示所花的最大时长（并不是指广告曝光时长）取值范围[3000, 5000]，设为0表示使用广点通 SDK 默认的超时时长。
     */
    private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer,
                               String appId, String posId, SplashADListener adListener, int fetchDelay) {
//        splashAD = new SplashAD(activity, adContainer, skipContainer, appId, posId, adListener, fetchDelay);
    }

    @Override
    public void onADPresent() {
        Log.i("AD_DEMO", "SplashADPresent");
//        splashHolder.setVisibility(View.INVISIBLE); // 广告展示后一定要把预设的开屏图片隐藏起来
    }

    @Override
    public void onADDismissed() {
        L.info("AD_DEMO", "SplashADDismissed");
        toHome();
    }

    private void toHome() {
        Intent intent = new Intent(SplashActivity.this, NYHomeActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }


    @Override
    public void onNoAD(AdError error) {
        L.info("AD_DEMO", String.format("LoadSplashADFail, eCode=%d, errorMsg=%s", error.getErrorCode(), error.getErrorMsg()));
        toHome();
    }

    @Override
    public void onADClicked() {
        L.info("AD_DEMO", "SplashADClicked");
    }

    /**
     * 倒计时回调，返回广告还将被展示的剩余时间。
     * 通过这个接口，开发者可以自行决定是否显示倒计时提示，或者还剩几秒的时候显示倒计时
     *
     * @param millisUntilFinished 剩余毫秒数
     */
    @Override
    public void onADTick(long millisUntilFinished) {
        Log.i("AD_DEMO", "SplashADTick " + millisUntilFinished + "ms");
//        skipView.setText(String.format(SKIP_TEXT,
//                Math.round(millisUntilFinished / 1000f)));
    }

    @Override
    public void onADExposure() {
        Log.i("AD_DEMO", "SplashADExposure");
    }

    //防止用户返回键退出 APP
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
