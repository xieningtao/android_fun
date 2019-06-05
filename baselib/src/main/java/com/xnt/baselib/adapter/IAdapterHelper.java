package com.xnt.baselib.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import com.xnt.baselib.pager.DataAdapter;

import java.util.List;

public interface IAdapterHelper<T> extends DataAdapter<T> {
    /**
     * get the context, you can use this context to create view
     *
     * @return the context of you have been set before
     */
    Context getContext();

    /**
     * if you use xml to create view, it will be useful
     *
     * @return the LayoutInflater you need to inflate xml,
     * if the context is null, then null return
     */
    LayoutInflater getLayoutInflater();

    /**
     * the assigned position item in your data list, if position is invalid, null will be return
     *
     * @param position the assigned item position
     * @return the item in list, null return if position is invalid
     * @see #setDataList(List)
     */
    T getItem(int position);

    /**
     * get the list size
     *
     * @return the list size, 0 return if the list is null
     * @see #setDataList(List)
     */
    int getItemCount();
}
