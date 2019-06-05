package com.xnt.baselib.utils;


import com.xnt.baselib.BuildConfig;

public class AssertUtils {

    public static void doAssert(boolean result, String assertMsg){
        if(BuildConfig.DEBUG && !result){
            throw new AssertionError(assertMsg);
        }
    }

    public static void doAssert(boolean result){
       doAssert(result,"");
    }
}
