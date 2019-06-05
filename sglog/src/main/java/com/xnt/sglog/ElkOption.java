package com.xnt.sglog;

import android.support.annotation.NonNull;
import android.text.TextUtils;

public class ElkOption {
    private static final int UPLOAD_SIZE = 1000;
    private String elkStorageDir;
    private int elkFileSize;

    public ElkOption(@NonNull String elkStorageDir, int elkFileSize) {
        if(TextUtils.isEmpty(elkStorageDir)){
            throw new IllegalArgumentException("elkStorageDir is illegal");
        } else {
            this.elkStorageDir = elkStorageDir;
        }

        //default size
        if(elkFileSize <=0){
            this.elkFileSize = UPLOAD_SIZE;
        }else {
            this.elkFileSize = elkFileSize;
        }
    }

    public String getElkStorageDir() {
        return elkStorageDir;
    }

    public int getElkFileSize() {
        return elkFileSize;
    }

    public static class ElkBuilder{
        private String elkStorageDir;
        private int elkFileSize;

        public ElkBuilder setElkStorageBase(String elkStorageDir){
            this.elkStorageDir = elkStorageDir;
            return this;
        }
        public ElkBuilder setElkFileSize(int elkFileSize){
            this.elkFileSize = elkFileSize;
            return this;
        }

        public ElkOption build(){
            return new ElkOption(this.elkStorageDir,this.elkFileSize);
        }
    }
}
