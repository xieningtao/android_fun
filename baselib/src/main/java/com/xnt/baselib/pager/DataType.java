package com.xnt.baselib.pager;

import java.util.List;

/**
 * Created by yuhengye g10475 on 2018/6/14.
 **/
public class DataType {

    public static final int REFRESH_NO_MORE_DATA = 1;//刷新前有内容，刷新后没有新的内容
    public static final int REFRESH_NO_DATA = 2;//刷新前后都没有内容
    public static final int REFRESH_DATA_SIZE_LESS = 3;//刷新结果小于请求条数
    public static final int REFRESH_DATA_SIZE_EQUAL = 4;//刷新结果大于或等于请求条数
    public static final int LOAD_NO_DATA = 5;//没有数据可加载
    public static final int LOAD_NO_MORE_DATA = 6;//没有更多的数据可加载，即结果小于请求条数
    public static final int LOAD_DATA_SIZE_EQUAL = 7;//加载结果大于或等于请求条数

    public static boolean isRefreshType(int type){
        return type == REFRESH_NO_MORE_DATA ||
                type == REFRESH_NO_DATA ||
                type == REFRESH_DATA_SIZE_LESS ||
                type == REFRESH_DATA_SIZE_EQUAL;
    }

    public static int getType(boolean isRefresh, int pageSize, List data, List currentData) {
        int status;
        if (isRefresh) {
            //下拉刷新
            if (data == null || data.isEmpty()) {
                //没有内容更新
                if (currentData == null || currentData.isEmpty()) {
                    status = REFRESH_NO_DATA;
                } else {
                    status = REFRESH_NO_MORE_DATA;
                }
            } else if (data.size() < pageSize) {
                //结果小于请求条数的一半
                status = REFRESH_DATA_SIZE_LESS;
            } else {
                //结果大于或等于请求条数
                status = REFRESH_DATA_SIZE_EQUAL;
            }
        } else {
            //上拉加载
            if (data == null || data.isEmpty()) {
                //没有数据可供加载
                status = LOAD_NO_DATA;
            } else {
                //成功加载更多
                if (data.size() < pageSize) {
                    //没有更多的数据可供加载
                    status = LOAD_NO_MORE_DATA;
                } else {
                    status = LOAD_DATA_SIZE_EQUAL;
                }
            }
        }
        return status;
    }

    public static boolean hasNewData(int type){
        return !(type == REFRESH_NO_MORE_DATA || type == REFRESH_NO_DATA || type == LOAD_NO_DATA);
    }

}
