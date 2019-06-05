package com.xnt.baselib.pager;

import java.util.List;

/**
 * Created by yuhengye g10475 on 2018/6/14.
 **/
public interface DataAdapter<T> {

    boolean isEmpty();

    List<T> getData();

    void addAll(int position, List<T> data);

    void addAll(List<T> data);

    void refresh(List<T> data);

    void addItem(int position, T item);

    void addItem(T item);

    T removeItem(int position);

    T removeItem(T item);


}
