package com.xnt.baselib.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;

import com.xnt.baselib.pager.DataAdapter;

import java.util.List;

public class AdapterHelperImpl<T> implements IAdapterHelper<T> {

    private static final String LOG_TAG = "AdapterHelperImpl";

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;

    private List<T> mListData;

    public AdapterHelperImpl(Context context) {
        mContext = context;

        if (context == null) {
            Log.w(LOG_TAG, "context is null, so LayoutInflater is null");
            mLayoutInflater = null;
            return;
        }

        mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    @Override
    public T getItem(int position) {
        int count = getItemCount();

        if (count == 0) {
            return null;
        }

        if (position >= 0 && position < count) {
            return mListData.get(position);
        }

        return null;
    }

    @Override
    public int getItemCount() {
        if (mListData == null) {
            return 0;
        }

        return mListData.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public List<T> getData() {
        return mListData;
    }


    public void addAll(int position, List<T> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        mListData.addAll(position, data);
    }

    @Override
    public void addAll(List<T> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        mListData.addAll(data);
    }

    public void refresh(List<T> data) {
        if (data == null) {
            mListData.clear();
        } else {
            mListData = data;
        }
    }

    public boolean isItemDragEnabled(int position) {
        return !isIndexOutOfBounds(position);
    }

    public boolean isIndexOutOfBounds(int position) {
        return 0 > position || position >= mListData.size();
    }

    public void addItem(int position, T item) {
        mListData.add(position, item);
    }

    public void addItem(T item) {
        mListData.add(item);
    }


    public T removeItem(int position) {
        if (isIndexOutOfBounds(position)) {
            return null;
        }
        return mListData.remove(position);
    }

    public T removeItem(T item) {
        int index = mListData.indexOf(item);
        if (index != -1) {
            return removeItem(index);
        }
        return null;
    }
}
