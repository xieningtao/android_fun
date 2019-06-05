package com.xnt.baselib.pager;

/**
 * Created by yuhengye g10475 on 2018/6/14.
 * 分页器
 **/
public abstract class Pager {

    abstract public Object getFirstPage();

    abstract public Object getNextPage();

    abstract public void handlePage(boolean increase);

    abstract public int getPageSize();

    public Object getPage(boolean isRefresh){
        if(isRefresh){
            return getFirstPage();
        }else{
            return getNextPage();
        }
    }

}
