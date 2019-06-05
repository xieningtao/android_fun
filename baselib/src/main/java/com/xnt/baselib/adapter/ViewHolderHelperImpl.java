package com.xnt.baselib.adapter;

public class ViewHolderHelperImpl<T> implements IViewHolderHelper<T> {

    private int mPosition = IViewHolderHelper.INVALID_POSITION;
    private T mBindData;

    @Override
    public void bind(int position, T t) {
        mPosition = position;
        mBindData = t;
    }

    @Override
    public int getBindPosition() {
        return mPosition;
    }

    @Override
    public T getBindData() {
        return mBindData;
    }
}
