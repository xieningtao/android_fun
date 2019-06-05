package com.xnt.baselib.pager;

import java.util.List;

/**
 * Created by yuhengye g10475 on 2018/6/14.
 **/
public interface DataHandler<T> {
    /**
     * 返回属于哪种类型
     * @param isRefresh
     * @param data
     * @return @link DataType
     */
    int handleData(boolean isRefresh, List<T> data);

    /**
     * 添加视图到适配器
     * @param type
     * @param data
     */
    void handleAdapter(int type, List<T> data);
}
