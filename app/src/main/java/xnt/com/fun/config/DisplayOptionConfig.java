package xnt.com.fun.config;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Created by mac on 2018/6/2.
 */

public class DisplayOptionConfig {
    private static DisplayImageOptions globalOption;
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
}
