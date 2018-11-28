package xnt.com.fun;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

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

public class SplashActivity extends FragmentActivity {

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
