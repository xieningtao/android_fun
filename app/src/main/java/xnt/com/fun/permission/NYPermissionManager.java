package xnt.com.fun.permission;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sf.loglib.L;

import xnt.com.fun.R;
import xnt.com.fun.SFApp;
import xnt.com.fun.rom.Rom;


/**
 * Created by g8876 on 2018/1/17.
 */
//后续其他的权限可以重构到这个地方来，比如说悬浮窗权限，语音权限
public class NYPermissionManager {
    private static final String TAG = "NYPermissionManager";
    private Application.ActivityLifecycleCallbacks mCallbacks;
    private static String[] recordAudioNeedCheckBufferList = new String[]{"HuaweiP7-L09", "HONORCAM-AL00",
            "HUAWEICUN-AL00"};
    public static interface PermissionAction<T>{
        void onPermissionResult(T result);
    }
    public void doAction(Activity activity) {
        Rom.romAction().openSetting(activity);
    }


    public  boolean checkCameraPermissionByCamera() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return NYPermissionUtil.checkPermission(Manifest.permission.CAMERA);
        }
        boolean isOk = true;
        Camera camera = null;
        try {
            camera = Camera.open(0);
            camera.setDisplayOrientation(90);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            isOk = false;
        }
        if (isOk) {
            camera.release();
        }
        return isOk;
    }

    private  boolean doCheckRecordAudioDirectly() {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            return NYPermissionUtil.checkPermission(Manifest.permission.RECORD_AUDIO);
//        }

        // 直接通过录音的方式判断是否有麦克风权限
        AudioRecord record = null;
        try {
            int bufferSize = AudioRecord.getMinBufferSize(22050, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            record = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize);
            record.startRecording();
            int recordingState = record.getRecordingState();
            if (recordingState == AudioRecord.RECORDSTATE_STOPPED) {
                return false;
            }
            // 有些机型需要通过检查录音文件大小来辨别
            if (!needToCheckBuffer()) {
                return true;
            }
            byte[] buffer = new byte[bufferSize];
            int result = record.read(buffer, 0, buffer.length);
            return result > 0 && !isBytesAllZero(buffer);
        } catch (Exception e) {
            L.error(TAG,"exception: "+e.getMessage());
            return false;
        } finally {
            if (record != null) {
                record.release();
            }
        }
    }

    private  boolean needToCheckBuffer() {
        String id = Build.ID;
        for (String listId : recordAudioNeedCheckBufferList) {
            if (TextUtils.equals(listId, id)) {
                return true;
            }
        }
        return false;
    }

    private  boolean isBytesAllZero(byte[] bytes) {
        for (byte b : bytes) {
            if (b != 0) {
                return false;
            }
        }
        return true;
    }

    public void showPermissionDialog(final Activity permissionActivity, @NonNull final String permissionName, final PermissionAction<Boolean> action) {
        if (permissionActivity == null || permissionActivity.isFinishing()) {
            return;
        }
        final Dialog permissionDialog = new Dialog(permissionActivity);
        View permissionView = LayoutInflater.from(permissionActivity).inflate(R.layout.ny_base_dialog,null);
        String permissionHintText;
        switch (permissionName) {
            case Manifest.permission.CAMERA:
                permissionHintText = permissionActivity.getString(R.string.camera_permission_tip);
                break;
            case Manifest.permission.RECORD_AUDIO:
                permissionHintText = permissionActivity.getString(R.string.audio_permission_tip);
                break;
            case Manifest.permission.ACCESS_COARSE_LOCATION:
            case Manifest.permission.ACCESS_FINE_LOCATION:
                permissionHintText = permissionActivity.getString(R.string.location_permission_tip);
                break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                permissionHintText = permissionActivity.getString(R.string.storage_permission_tip);
                break;
            default:
                permissionHintText = "";
        }
        ((TextView) permissionView.findViewById(R.id.message)).setText(permissionHintText);
        ((TextView) permissionView.findViewById(R.id.dialog_sure_tv)).setText(R.string.go_setting);
        ((TextView) permissionView.findViewById(R.id.dialog_cancel_tv)).setText(R.string.cancel);
        permissionView.findViewById(R.id.dialog_sure_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + SFApp.gContext.getPackageName()));
                    permissionActivity.startActivity(intent);
                } catch (ActivityNotFoundException ignore) {
                    L.error(TAG,"exception: "+ignore.getMessage());
                }
            }
        });
        permissionView.findViewById(R.id.dialog_cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (action != null) {
                    action.onPermissionResult(false);
                }
                permissionDialog.dismiss();
            }
        });
        permissionDialog.setContentView(permissionView);
        permissionDialog.setCancelable(false);
        permissionDialog.setCanceledOnTouchOutside(false);
        permissionDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mCallbacks != null) {
                    SFApp.getApplication().unregisterActivityLifecycleCallbacks(mCallbacks);
                    mCallbacks = null;
                }
            }
        });
        permissionDialog.show();
        mCallbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (permissionActivity == activity) {
                    if (permissionDialog.isShowing()) {
                        boolean success;
                        switch (permissionName) {
                            case Manifest.permission.CAMERA:
                                success = checkCameraPermissionByCamera();
                                break;
                            case Manifest.permission.RECORD_AUDIO:
                                success = doCheckRecordAudioDirectly();
                                break;
                            case Manifest.permission.ACCESS_COARSE_LOCATION:
                            case Manifest.permission.ACCESS_FINE_LOCATION:
                                success = NYPermissionUtil.checkPermission(permissionName);
                                break;
                            default:
                                success = NYPermissionUtil.checkPermission(permissionName);
                                break;
                        }
                        if (success) {
                            if (action != null) {
                                action.onPermissionResult(true);
                            }
                            permissionDialog.dismiss();
                        }
                    }
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (activity == permissionActivity && mCallbacks != null) {
                    SFApp.getApplication().unregisterActivityLifecycleCallbacks(mCallbacks);
                    mCallbacks = null;
                }
            }
        };
        SFApp.getApplication().registerActivityLifecycleCallbacks(mCallbacks);
    }
}
