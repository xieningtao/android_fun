package com.xnt.sglog;

import android.os.Environment;


import java.io.File;

public class Constant {

    //<editor-fold desc="elk配置">
//    public static final String ELK_STORAGE_BASE_DIR = MountedSDCard.getInstance().getExternalSdPath(AppUtil.getApplication());
    //debug和betatest的包都会放在debug目录，release的包会放在elk_file文件下
//    public static final String ELK_STORAGE_DIR = BuildConfig.DEBUG ?
//            ELK_STORAGE_BASE_DIR + "/elk_file/debug/" : ELK_STORAGE_BASE_DIR + "/elk_file/";
    //elk只支持小于1m文件上传
    public static final int ELK_FILE_MAX_SIZE = 2500;

    public static final String UPLOADING_SUFFIX = "-uploading";//需要上传的文件
    public static final String IDLE_SUFFIX = "-idle";//没有被操作的文件
    public static final String WRITE_SUFFIX = "-writing";//正在写的文件
    //</editor-fold>

}
