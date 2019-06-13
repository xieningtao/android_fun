package com.xnt.baselib.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xnt.baselib.pager.DataAdapter;

import java.util.List;

abstract public class BaseRecyclerViewAdapter<T, VH extends BaseRecyclerViewHolder<T>> extends RecyclerView.Adapter<VH> implements DataAdapter<T> {

    private final IAdapterHelper<T> mAdapterHelper;

    /**
     * constructor of this adapter
     *
     * @param context <b>if null is passed, the method {@link #getContext()}
     *                and {@link #getLayoutInflater()} will return null.</b>
     *                so pay attention at this.
     */
    public BaseRecyclerViewAdapter(Context context) {
        mAdapterHelper = new AdapterHelperImpl<T>(context);
    }

    /**
     * the assigned position item in your data list, if position is invalid, null will be return
     * <p>
     * you should call <b>super.getItem()</b> first if you want to override this method in subclass
     *
     * @param position the assigned item position
     * @return the item in list, null return if position is invalid
     */
    protected T getItem(int position) {
        return mAdapterHelper.getItem(position);
    }

    /**
     * get the context, you can use this context to create view
     *
     * @return the context of you have been set before
     */
    protected final Context getContext() {
        return mAdapterHelper.getContext();
    }

    /**
     * if you use xml to create view, it will be useful
     *
     * @return the LayoutInflater you need to inflate xml,
     * if the context is null, then null return
     */
    protected final LayoutInflater getLayoutInflater() {
        return mAdapterHelper.getLayoutInflater();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * you should call <b>super.onBindViewHolder()</b> first if you want to override this method in subclass
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(VH holder, int position) {
        T itemData = getItem(position);
        holder.bind(position, itemData);
    }

    /**
     * {@inheritDoc}
     * <p>
     * you should call <b>super.getItemCount()</b> first if you want to override this method in subclass
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mAdapterHelper.getItemCount();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }


    @Override
    public void addAll(int position, List<T> data) {
        mAdapterHelper.addAll(position,data);
        notifyDataSetChanged();
    }

    @Override
    public void addAll(List<T> data) {
        mAdapterHelper.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void refresh(List<T> data) {
        mAdapterHelper.refresh(data);
        notifyDataSetChanged();
    }

    @Override
    public void addItem(int position, T item) {
        mAdapterHelper.addItem(position,item);
        notifyItemInserted(position);
    }

    @Override
    public void addItem(T item) {
        mAdapterHelper.addItem(item);
        notifyItemInserted(getItemCount()-1);
    }

    public T removeItem(int position) {
        T t = mAdapterHelper.removeItem(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, 1);
        return t;
    }

    public T removeItem(T item) {
        if(mAdapterHelper.getData() == null){
            return null;
        }
        int index = mAdapterHelper.getData().indexOf(item);
        if (index != -1) {
            return removeItem(index);
        }
        return null;
    }
}
