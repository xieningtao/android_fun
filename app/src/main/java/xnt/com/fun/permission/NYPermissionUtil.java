package xnt.com.fun.permission;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import xnt.com.fun.R;
import xnt.com.fun.SFApp;

/**
 * Created by yuhengye g10475 on 2018/9/11.
 **/
public class NYPermissionUtil {

    public static ArrayList<String> getUngrantedPermissionList(@NonNull String... permissions){
        ArrayList<String> permissionList = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionList = new ArrayList<>();
            for (String permission : permissions) {
                if (!checkPermission(permission)) {
                    permissionList.add(permission);
                }
            }
        }
        return permissionList;
    }

    public static boolean checkPermission(String permission){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(SFApp.getApplication(), permission) == PackageManager.PERMISSION_GRANTED;
        }else{
            return PermissionChecker.checkSelfPermission(SFApp.getApplication(), permission) == PermissionChecker.PERMISSION_GRANTED;
        }
    }

    public static String getLocalRequestPermissionTip(boolean abandonRequest, String permissionName){
        if(abandonRequest){
            return SFApp.getApplication().getString(R.string.permission_ask_never, getLocalPermissionName(permissionName));
        } else {
            return SFApp.getApplication().getString(R.string.permission_ask_again, getLocalPermissionName(permissionName));
        }
    }

    private static String getLocalPermissionName(String permissionName){
        if(permissionName == null){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if(permissionName.contains(Manifest.permission.READ_PHONE_STATE)){
            appendName(sb, R.string.phone_permission);
        }
        if(permissionName.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            appendName(sb, R.string.storage_permission);
        }

        return sb.toString();
    }

    private static void appendName(StringBuilder sb, @StringRes int resId){
        if (sb.length() == 0) {
            sb.append(SFApp.getApplication().getString(resId));
        } else {
            sb.append(SFApp.getApplication().getString(R.string.divider)).append(SFApp.getApplication().getString(resId));
        }
    }

    public static void requestPermission(final Fragment fragment, final String permissionName, final @NonNull NYPermissionManager.PermissionAction<Boolean> action){
        requestPermission(fragment, permissionName, true, action);
    }

    public static void requestPermission(final Fragment fragment, final String permissionName, boolean checkPermission, final @NonNull NYPermissionManager.PermissionAction<Boolean> action){
        if(checkPermission && checkPermission(permissionName)){
            action.onPermissionResult(true);
            return;
        }
        new RxPermissions(fragment)
                .requestEach(permissionName)
                .subscribe(new Observer<Permission>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Permission permission) {
                        if (permission.granted) {
                            // `permission.name` is granted !
                            action.onPermissionResult(true);
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // Denied permission without ask never again
                            action.onPermissionResult(false);
                        } else {
                            // Denied permission with ask never again
                            // Need to go to the settings
                            new NYPermissionManager().showPermissionDialog(fragment.getActivity(), permissionName, action);
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

    public static void requestPermission(final FragmentActivity activity, final String permissionName, final @NonNull NYPermissionManager.PermissionAction<Boolean> action){
        requestPermission(activity, permissionName, true, action);
    }

    public static void requestPermission(final FragmentActivity activity, final String permissionName, boolean checkPermission, final @NonNull NYPermissionManager.PermissionAction<Boolean> action){
        if(checkPermission && checkPermission(permissionName)){
            action.onPermissionResult(true);
            return;
        }
        new RxPermissions(activity)
                .requestEach(permissionName)
                .subscribe(new Observer<Permission>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Permission permission) {
                        if (permission.granted) {
                            // `permission.name` is granted !
                            action.onPermissionResult(true);
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // Denied permission without ask never again
                            action.onPermissionResult(false);
                        } else {
                            // Denied permission with ask never again
                            // Need to go to the settings
                            new NYPermissionManager().showPermissionDialog(activity, permissionName, action);
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
}
