package com.xnt.sglog;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import java.io.File;

import static com.xnt.sglog.Constant.IDLE_SUFFIX;
import static com.xnt.sglog.Constant.UPLOADING_SUFFIX;

public class UploadElkHelper {

    static ILazyCreator<IUpload> mCreator = new ILazyCreator<IUpload>() {
        @Override
        protected IUpload create() {
            return new UploadElkImpl();
        }
    };

    private UploadElkHelper(){

    }

    public static void uploadElkFile(Context context,File file){
        mCreator.getInstance().upload(context,file);
    }

    public static void uploadIdleElkFile(Context context,String elkDir){
        if(TextUtils.isEmpty(elkDir)){
            return;
        }
        File dir = new File(elkDir);
        if (dir.exists()) {//获取当前目录下的所有文件
            File[] files = dir.listFiles(LogFileOperator.getInstance().getIdleFileNameFilter());
            for(File file:files) {
                UploadElkHelper.uploadElkFile(context,file);
            }
        }
    }

    public static void uploadUploadElkFile(Context context,String elkDir){
        if(TextUtils.isEmpty(elkDir)){
            return;
        }
        File dir = new File(elkDir);
        if (dir.exists()) {//获取当前目录下的所有文件
            File[] files = dir.listFiles(LogFileOperator.getInstance().getUploadFileNameFilter());
            for(File file:files) {
                UploadElkHelper.uploadElkFile(context,file);
            }
        }
    }

    static class UploadElkImpl implements IUpload{
        private final int START = 0;
        private final int UPLOADING = 1;
        private final int FINISH = 2;

        private final String TAG = "UploadElkImpl";

        @Override
        public void upload(Context context,File file) {
            if (file == null || !file.exists()) {
                Log.e(TAG,"method->upload file is null or empty");
                return;
            }
            final String fileName = file.getName();

            if(TextUtils.isEmpty(fileName)){
                Log.e(TAG,"method->upload fileName is empty");
                return;
            }
            Log.i(TAG,"method->upload before rename: "+fileName);
            String newFileName = "";
            //文件被标记为Idle,说明是可以上传的
            if(fileName.contains(IDLE_SUFFIX)) {
                // 为正在上传文件重命名
                newFileName = ElkHelper.remarkFile(file,IDLE_SUFFIX,UPLOADING_SUFFIX);
            }else if(fileName.contains(UPLOADING_SUFFIX)){
                newFileName = fileName;
            }
            Log.i(TAG,"method->upload after rename: "+newFileName);
            //只需要上传被标记为upload的文件
            if(!TextUtils.isEmpty(newFileName) && newFileName.contains(UPLOADING_SUFFIX)) {
//                Intent intent = new Intent(context, ElkUploadService.class);
//                intent.putExtra(ElkUploadService.ELK_FILE_NAME, newFileName);
//                context.startService(intent);
                //文件上传
            }
        }
    }

}
