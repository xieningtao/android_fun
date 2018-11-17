package xnt.com.fun.config;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import xnt.com.fun.R;

/**
 * Created by mac on 2018/6/2.
 */

public class DisplayOptionConfig {
    private static DisplayImageOptions globalOption;
    private static DisplayImageOptions defaultDisplayOption;
    public static DisplayImageOptions getDisplayOption(int resId){
        if (globalOption == null) {
            globalOption = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .showImageOnLoading(resId)
                    .showImageForEmptyUri(resId)
                    .showImageOnFail(resId)
                    .build();
        }
        return globalOption;
    }
    public static DisplayImageOptions getDefaultDisplayOption(){
        if (defaultDisplayOption == null) {
            defaultDisplayOption = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .showImageOnLoading(R.drawable.app_default_loading)
                    .showImageForEmptyUri(R.drawable.app_default_loading)
                    .showImageOnFail(R.drawable.app_default_loading)
                    .build();
        }
        return defaultDisplayOption;
    }
}
