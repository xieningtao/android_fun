package xnt.com.fun.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.basesmartframe.baseevent.GlobalEvent;
import com.basesmartframe.basepull.PullType;
import com.basesmartframe.baseui.BaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sf.utils.baseutil.NetWorkManagerUtil;
import com.sflib.CustomView.newhttpview.HttpViewManager;
import com.sflib.reflection.core.SFIntegerMessage;

import butterknife.BindView;
import butterknife.ButterKnife;
import xnt.com.fun.R;

public abstract class BaseRecycleViewFragment extends BaseFragment {

    @BindView(R.id.smart_refresh)
    protected SmartRefreshLayout mSmartRefresh;

    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    private FrameLayout mHttpContainer;
    private HttpViewManager mHttpViewManager;

    public BaseRecycleViewFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycle_view_fragment, (ViewGroup) null);
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        this.initView(view, savedInstanceState);
        this.doRefresh();
    }

    private void initView(View view, Bundle savedInstanceState) {
        this.mHttpContainer = (FrameLayout) view.findViewById(R.id.http_container);
        this.mHttpViewManager = HttpViewManager.createManagerByDefault(this.getActivity(), this.mHttpContainer);
        mSmartRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                doLoadMore();
            }
        });

        mSmartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                doRefresh();
            }
        });
    }


    private boolean isListViewHasData() {
        if (mRecyclerView != null) {
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter != null) {
                return adapter.getItemCount() > 0;
            }
        }
        return false;
    }


    public void doLoadMore() {
        this.showHttpLoadingView();
        if (NetWorkManagerUtil.isNetworkAvailable()) {
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
            this.onRefresh();
        } else {
            this.onRefreshNoNetwork();
            simpleFinishRefreshOrLoading(false);
        }

    }

    protected void simpleFinishRefreshOrLoading(boolean hasMore) {
        if(mSmartRefresh.getState() == RefreshState.Refreshing){
            mSmartRefresh.finishRefresh();
        }else {
            mSmartRefresh.finishLoadMore();
        }
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
