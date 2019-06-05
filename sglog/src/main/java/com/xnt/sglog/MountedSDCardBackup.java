package com.xnt.sglog;//
//package com.netease.huatian.common.log;
//
//import android.annotation.SuppressLint;
//import android.os.Build;
//import android.os.Build.VERSION;
//import android.os.Environment;
//import android.os.StatFs;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//
//public class MountedSDCardBackup
//{
//    private final static String TAG = MountedSDCardBackup.class.getSimpleName();
//
//    private ArrayList<String> mSDs = new ArrayList<String>();
//    private static MountedSDCardBackup mInstance;
//
//    public synchronized static MountedSDCardBackup getInstance()
//    {
//        if (null == mInstance)
//        {
//            mInstance = new MountedSDCardBackup();
//        }
//        return mInstance;
//    }
//
//    private MountedSDCardBackup()
//    {
//        detectSds();
//    }
//
//    private void detectSds()
//    {
//        String internalSddPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//
//        mSDs.clear();
//        mSDs.add(internalSddPath);
//
//        detectSDsFromMnt();
//        detectSDsFromVoldFstab();
//
//        mSDs.trimToSize();
//
//    }
//
//    private void detectSDsFromVoldFstab()
//    {
//        try
//        {
//            File voldFstab = new File("/etc/vold.fstab");
//            BufferedReader br = new BufferedReader(new FileReader(voldFstab));
//            String line = null;
//            while ((line = br.readLine()) != null)
//            {
//                if (line.startsWith("dev_mount"))
//                {
//                    String[] blocks = line.split(" ");
//                    if (blocks.length < 3)
//                    {
//                        continue;
//                    }
//                    String path = blocks[2];
//                    if (!mSDs.contains(path))
//                    {
//                        if (isValidPath(path))
//                        {
//                            mSDs.add(path);
//                        }
//                    }
//                }
//            }
//            br.close();
//        }
//        catch (IOException e)
//        {
//
//        }
//    }
//
//    private void detectSDsFromMnt()
//    {
//        String s = "";
//        try
//        {
//            Process process = new ProcessBuilder().command("mount").redirectErrorStream(true).start();
//            process.waitFor();
//            InputStream is = process.getInputStream();
//            byte[] buffer = new byte[1024];
//            while (is.read(buffer) != -1)
//            {
//                s = s + new String(buffer);
//            }
//            is.close();
//        }
//        catch (Exception e)
//        {
//
//        }
//
//        String[] lines = s.split("\n");
//
//        for (int i = 0; i < lines.length; i++)
//        {
//            if (-1 != lines[i].indexOf("/dev/block/vold") && -1 != lines[i].indexOf("vfat") && -1 != lines[i].indexOf("rw"))
//            {
//                String[] blocks = lines[i].split("\\s");
//                if (blocks.length < 2)
//                {
//                    continue;
//                }
//                String path = blocks[1];
//                if (!mSDs.contains(path) && path.startsWith("/mnt"))
//                {
//                    if (isValidPath(path))
//                    {
//                        mSDs.add(path);
//                    }
//                }
//            }
//        }
//    }
//
//    private boolean isValidPath(String path)
//    {
//        try
//        {
//            new StatFs(path); // 通过StatFs来判断是否是sd卡
//            File file = new File(path);
//            if (file.canWrite())
//            {
//                return true;
//            }
//        }
//        catch (Exception e)
//        {
//
//        }
//        return false;
//    }
//
//    public String getInternalSdPath()
//    {
//        return Environment.getDataDirectory().getAbsolutePath();
//    }
//
//    @SuppressLint("NewApi")
//    public synchronized String getExternalSdPath(boolean reDectect)
//    {
//        if (reDectect)
//        {
//            detectSds();
//        }
//
//        if (mSDs.size() > 1)
//        {
//            if (VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
//            {
//                if (Environment.isExternalStorageRemovable())
//                {
//                    if (isValidPath(mSDs.get(0)))
//                    {
//                        return mSDs.get(0);
//                    }
//                }
//            }
//            return mSDs.get(1);
//        }
//        else
//        {
//            return mSDs.get(0);
//        }
//    }
//
//    public boolean isExternalSdValid(){
//        return isValidPath(getExternalSdPath(false));
//    }
//
//    public int[] getExternalStorageSizeInfo() {
//        if (isExternalSdValid()) {
//            File sdcardDir = new File(getExternalSdPath(false));
//            StatFs sf = new StatFs(sdcardDir.getPath());
//            long blockSize = sf.getBlockSize();
//            long blockCount = sf.getBlockCount();
//            long availCount = sf.getAvailableBlocks();
//
//            int total = (int) ((blockCount * blockSize) / 1024) / 1024;
//            int free = (int) ((availCount * blockSize) / 1024) / 1024;
//
//            return new int[] {
//                    total, free
//            };
//
//        } else {
//            return null;
//        }
//    }
//
//    public synchronized ArrayList<String> getSDs(boolean reDectect)
//    {
//        if (reDectect)
//        {
//            detectSds();
//        }
//        return mSDs;
//    }
//
//    public synchronized String getBetterSDPath(boolean reDectect)
//    {
//        if (reDectect)
//        {
//            detectSds();
//        }
//
//        if (mSDs.size() <= 1)
//        {
//            StatFs sfInternal = new StatFs(mSDs.get(0));
//            long sizeInternal = (long)sfInternal.getBlockSize() * (long)sfInternal.getAvailableBlocks();
//            return mSDs.get(0);
//        }
//        else
//        {
//            String internal = mSDs.get(0);
//            StatFs sfInternal = new StatFs(internal);
//            long sizeInternal = (long)sfInternal.getBlockSize() * (long)sfInternal.getAvailableBlocks();
//
//            String external = mSDs.get(1);
//            StatFs sfExternal = new StatFs(external);
//            long sizeExternal = (long)sfExternal.getBlockSize() * (long)sfExternal.getAvailableBlocks();
//
//            return external;
//        }
//
//    }
//
//}
