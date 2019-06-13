package com.xnt.baselib.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.ViewGroup;

import com.xnt.baselib.pager.DataHandler;
import com.xnt.baselib.pager.DataType;

import java.util.List;

import static com.xnt.baselib.pager.DataType.getType;

public class BaseRefreshWrapAdapter<T, VH extends BaseRecyclerViewHolder<T>> extends BaseRecyclerViewAdapter<T, VH> implements DataHandler<T> {

    private BaseRecyclerViewAdapter<T, VH> mRealAdapter;
    private int pageSize;

    /**
     * constructor of this adapter
     *
     * @param context <b>if null is passed, the method {@link #getContext()}
     *                and {@link #getLayoutInflater()} will return null.</b>
     *                so pay attention at this.
     */
    public BaseRefreshWrapAdapter(Context context, BaseRecyclerViewAdapter<T, VH> realAdapter) {
        super(context);
        this.mRealAdapter = realAdapter;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public List<T> getData() {
        return null;
    }

    public void notifyData(boolean isRefresh, List<T> data) {
        int type = handleData(isRefresh, data);
        handleAdapter(type, data);
    }

    @Override
    public int handleData(boolean isRefresh, List<T> data) {
        return getType(isRefresh, pageSize, data, mRealAdapter.getData());
    }

    @Override
    public void handleAdapter(int type, List<T> data) {
        if (DataType.isRefreshType(type)) {
            mRealAdapter.refresh(data);
        } else {
            mRealAdapter.addAll(data);
        }
    }
}
