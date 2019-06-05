package com.xnt.baselib.pager;

import java.util.List;

import static com.xnt.baselib.pager.DataType.getType;

/**
 * Created by yuhengye g10475 on 2018/6/14.
 **/
public class DefaultDataHandler<T> implements DataHandler<T> {

    DataAdapter<T> adapter;
    int pageSize;

    public DefaultDataHandler(DataAdapter<T> adapter, Pager pager) {
        this(adapter, pager.getPageSize());
    }

    public DefaultDataHandler(DataAdapter<T> adapter, int pageSize) {
        this.adapter = adapter;
        this.pageSize = pageSize;
    }

    @Override
    public int handleData(boolean isRefresh, List<T> data) {
        return getType(isRefresh, pageSize, data, adapter.getData());
    }

    @Override
    public void handleAdapter(int type, List<T> data) {
        if (DataType.isRefreshType(type)) {
            adapter.refresh(data);
        } else {
            adapter.addAll(data);
        }
    }


}
