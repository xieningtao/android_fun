package com.xnt.baselib.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sf.utils.baseutil.NetWorkManagerUtil;
import com.xnt.baselib.R;
import com.xnt.baselib.adapter.BaseRecyclerViewAdapter;
import com.xnt.baselib.adapter.BaseRefreshWrapAdapter;
import com.xnt.baselib.mvp.AbsPresenter;
import com.xnt.baselib.mvp.IView;
import com.xnt.baselib.pager.NumberPager;
import com.xnt.baselib.pager.Pager;

import java.util.List;

/**
 * 列表+分页
 *
 * @param <T>
 */
abstract public class BaseListSGActivity<T,V extends IView,P extends AbsPresenter<V>> extends BaseMVPSGActivity<V,P> {

    protected SmartRefreshLayout mRefreshLayout;
    protected RecyclerView mRecyclerView;
    private BaseRefreshWrapAdapter mWrapAdapter;
    private Pager mPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRefreshLayout = findViewById(R.id.smart_refresh);
        mRecyclerView = findViewById(R.id.recycler_view);
        BaseRecyclerViewAdapter adapter = createAdapter();
        mWrapAdapter = new BaseRefreshWrapAdapter(this, adapter);
        mRecyclerView.setAdapter(mWrapAdapter);
        mPager = createPager();
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                doRefresh(mPager);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                doLoadMore(mPager);
            }
        });
    }

    @Override
    int getChildContentLayoutRes() {
        return R.layout.layout_commom_recycler;
    }

    abstract void doRefresh(Pager pager);

    abstract void doLoadMore(Pager pager);

    abstract BaseRecyclerViewAdapter createAdapter();

    protected Pager createPager() {
        return new NumberPager(0);
    }

    private boolean isEmpty(List<T> data) {
        return data != null && !data.isEmpty();
    }

    /**
     * refresh,现有数据，加载的数据
     *
     * @param isRefresh
     * @param data
     */
    protected void handleStateView(boolean isRefresh, List<T> data) {
        if (isRefresh && isEmpty(data)) {
            if(NetWorkManagerUtil.isNetworkAvailable()){
                showEmptyStatus("暂时没有数据");
            }else {
                showNetworkError();
            }
        } else if (isRefresh && !isEmpty(data)) {
            //无需提醒

        } else if (!isRefresh && !isEmpty(mWrapAdapter.getData()) && isEmpty(data)) {
            //弹出toast提醒
        } else {
            //无需提醒
        }
    }

    /**
     * 成功的时候调用
     *
     * @param data
     */
    public void handleSuccessFullData(List<T> data) {
        boolean isRefresh = isRefreshing();
        boolean increasePage = !isRefresh && !isEmpty(data);
        mPager.handlePage(increasePage);
        mWrapAdapter.notifyData(isRefresh, data);
        handleStateView(isRefresh, data);
        resetRefreshView();
    }

    /**
     * 失败的时候调用
     *
     * @param msg
     */
    public void handleFailedData(String msg) {
        boolean isRefresh = isRefreshing();
        handleStateView(isRefresh, null);
        resetRefreshView();
    }

    protected boolean isRefreshing() {
        return mRefreshLayout != null && mRefreshLayout.getState() == RefreshState.Refreshing;
    }

    protected void resetRefreshView() {
        if (mRefreshLayout != null) {
            if (isRefreshing()) {
                mRefreshLayout.finishRefresh();
            } else {
                mRefreshLayout.finishLoadMore();
            }
        }
    }
}
