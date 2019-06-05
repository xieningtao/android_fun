package com.xnt.baselib.adapter;

import com.xnt.baselib.pager.DataAdapter;

import java.util.List;

public interface IDataList<T>  {
    /**
     * set the data list for adapter use
     *
     * @param list the list data for adapter
     * @see #getDataList()
     */
    void setDataList(List<? extends T> list);

    /**
     * get the data list you have call {@link #setDataList(List)} before
     *
     * @return the data list that bind for adapter for the moment,
     * if you did not call {@link #setDataList(List)} before, it will be null
     * @see #setDataList(List)
     */
    List<? extends T> getDataList();
}
