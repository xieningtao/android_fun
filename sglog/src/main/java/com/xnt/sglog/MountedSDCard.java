
package com.xnt.sglog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.StatFs;

import java.io.File;

public class MountedSDCard
{

    private static MountedSDCard mInstance;

    public synchronized static MountedSDCard getInstance()
    {
        if (null == mInstance)
        {
            mInstance = new MountedSDCard();
        }
        return mInstance;
    }

    private MountedSDCard() {
    }


    private boolean isValidPath(String path)
    {
        try
        {
            new StatFs(path); // 通过StatFs来判断是否是sd卡
            File file = new File(path);
            if (file.canWrite())
            {
                return true;
            }
        }
        catch (Exception e)
        {

        }
        return false;
    }

    @SuppressLint("NewApi")
    public synchronized String getExternalSdPath(Context context) {
        File file = context.getExternalCacheDir();
        if(file == null){
            file = context.getCacheDir();
        }
        return file.getAbsolutePath();
    }
    

}
