package com.xnt.sglog;

import android.content.Context;

import java.io.File;

public interface IUpload {

    void upload(Context context,String rootLogDir, File file);
}
