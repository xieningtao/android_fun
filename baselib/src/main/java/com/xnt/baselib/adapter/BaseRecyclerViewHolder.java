package com.xnt.baselib.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

abstract public class BaseRecyclerViewHolder<T> extends RecyclerView.ViewHolder {

    private final IViewHolderHelper<T> mViewHolderHelper;

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);

        mViewHolderHelper = new ViewHolderHelperImpl<T>();
    }

    /**
     * bind the current item view with position and item data
     * <p>
     * you should call <b>super.bind()</b> first if you want to override this method in subclass
     *
     * @param position the current view item position, it is for view item ui use in most of case.
     * @param t        the current bind data, it is for list view item ui use in most of case
     */
    public void bind(int position, T t) {
        mViewHolderHelper.bind(position, t);
        onBind(t);
    }

    /**
     * get the current item bind data
     * <p>
     * you should call <b>super.getBindData()</b> first if you want to override this method in subclass
     *
     * @return the current bind data, it is for list view item ui use in most of case
     */
    protected T getBindData() {
        return mViewHolderHelper.getBindData();
    }

    /**
     * get the current item bind position
     * <p>
     * you should call <b>super.getBindPosition()</b> first if you want to override this method in subclass
     *
     * @return the current list view item position, it is for list view item ui use in most of case
     */
    protected int getBindPosition() {
        return mViewHolderHelper.getBindPosition();
    }

    /**
     * do actual bind work in this method, e.g. tvxxx.setText(t.xxx);
     *
     * @param t the current bind data
     */
    protected abstract void onBind(T t);
}
