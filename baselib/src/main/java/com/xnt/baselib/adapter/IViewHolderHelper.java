package com.xnt.baselib.adapter;

public interface IViewHolderHelper<T> {

    int INVALID_POSITION = -1;

    /**
     * bind the current item view with position and item data
     * @param position the current view item position, it is for view item ui use in most of case.
     * @param t the current bind data, it is for list view item ui use in most of case
     */
    void bind(int position, T t);

    /**
     * get the current item bind position
     * @return the current list view item position, it is for list view item ui use in most of case
     */
    int getBindPosition();

    /**
     * get the current item bind data
     * @return the current bind data, it is for list view item ui use in most of case
     */
    T getBindData();
}
