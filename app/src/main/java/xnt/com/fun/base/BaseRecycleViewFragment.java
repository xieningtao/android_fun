package xnt.com.fun.base;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.basesmartframe.baseevent.GlobalEvent;
import com.basesmartframe.basepull.PullType;
import com.basesmartframe.baseui.BaseFragment;
import com.sf.utils.baseutil.NetWorkManagerUtil;
import com.sflib.CustomView.newhttpview.HttpViewManager;
import com.sflib.reflection.core.SFIntegerMessage;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import xnt.com.fun.R;

public abstract class BaseRecycleViewFragment extends BaseFragment {
    protected PullLoadMoreRecyclerView mPullLoadMoreRv;
    private FrameLayout mHttpContainer;
    private HttpViewManager mHttpViewManager;
    private PullType mPullType;

    public BaseRecycleViewFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycle_view_fragment, (ViewGroup) null);
    }

    public PullLoadMoreRecyclerView getPullToRefreshListView() {
        return this.mPullLoadMoreRv;
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.initView(view, savedInstanceState);
        this.doRefresh();
    }

    private void initView(View view, Bundle savedInstanceState) {
        this.mHttpContainer = (FrameLayout) view.findViewById(R.id.http_container);
        this.mHttpViewManager = HttpViewManager.createManagerByDefault(this.getActivity(), this.mHttpContainer);
        this.mPullLoadMoreRv = (PullLoadMoreRecyclerView) view.findViewById(R.id.load_more_rv);
        mPullLoadMoreRv.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }

            @Override
            public void onLoadMore() {
                doLoadMore();
            }
        });
    }


    private boolean isListViewHasData() {
        RecyclerView recyclerView = mPullLoadMoreRv.getRecyclerView();
        if (recyclerView != null) {
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter != null) {
                return adapter.getItemCount() > 0;
            }
        }
        return false;
    }


    public void doLoadMore() {
        this.showHttpLoadingView();
        if (NetWorkManagerUtil.isNetworkAvailable()) {
            this.mPullType = PullType.LOADMORE;
            this.onLoadMore();
        } else {
            this.simpleFinishRefreshOrLoading(false);
        }

    }

    private void showHttpLoadingView() {
        boolean hasData = this.isListViewHasData();
        this.mHttpViewManager.showHttpLoadingView(hasData);
    }

    private void showHttpResultView() {
        boolean hasData = this.isListViewHasData();
        if (NetWorkManagerUtil.isNetworkAvailable()) {
            this.mHttpViewManager.showHttpViewNOData(hasData);
        } else {
            this.mHttpViewManager.showHttpViewNoNetwork(hasData);
        }

    }

    public void doRefresh() {
        if (NetWorkManagerUtil.isNetworkAvailable()) {
            this.showHttpLoadingView();
            this.mPullType = PullType.REFRESH;
            this.onRefresh();
        } else {
            this.onRefreshNoNetwork();
            simpleFinishRefreshOrLoading(false);
        }

    }

    protected void simpleFinishRefreshOrLoading(boolean hasMore) {
        mPullLoadMoreRv.setHasMore(hasMore);
        mPullLoadMoreRv.setPullLoadMoreCompleted();
        this.showHttpResultView();
    }

    @SFIntegerMessage(
            messageId = 105
    )
    public void onNetwokChange(GlobalEvent.NetworkEvent event) {
        if (event.hasNetwork && !this.isListViewHasData()) {
            this.doRefresh();
        }

    }

    protected abstract boolean onRefresh();

    protected boolean onRefreshNoNetwork() {
        return false;
    }

    protected abstract boolean onLoadMore();

}
