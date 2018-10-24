package xnt.com.fun.base;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.basesmartframe.baseevent.GlobalEvent;
import com.basesmartframe.basepull.PullType;
import com.basesmartframe.baseui.BaseFragment;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sf.utils.baseutil.NetWorkManagerUtil;
import com.sflib.CustomView.newhttpview.HttpViewManager;
import com.sflib.reflection.core.SFIntegerMessage;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import xnt.com.fun.R;

public abstract class BaseRecycleViewFragment<T,VH extends RecyclerView.ViewHolder> extends BaseFragment {
    protected PullLoadMoreRecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private FrameLayout mHttpContainer;
    private HttpViewManager mHttpViewManager;
    private PullType mPullType;
    private final int LOAD_MORE = 1;
    private LoadMoreViewHolder mLoadMoreViewHolder;
    private final List<T> data = new ArrayList();

    public BaseRecycleViewFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycle_view_fragment, (ViewGroup) null);
    }

    public PullLoadMoreRecyclerView getPullToRefreshListView() {
        return this.mRecyclerView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.initView(view, savedInstanceState);
        this.doRefresh();
    }

    private void initView(View view, Bundle savedInstanceState) {
        this.mHttpContainer = (FrameLayout) view.findViewById(R.id.http_container);
        this.mHttpViewManager = HttpViewManager.createManagerByDefault(this.getActivity(), this.mHttpContainer);
        this.data.clear();
        this.mRecyclerView = (PullLoadMoreRecyclerView) view.findViewById(R.id.load_more_rv);
        //瀑布流
        mRecyclerView.setStaggeredGridLayout(2);//参数为列数
        this.mRecyclerView.setAdapter(this.mAdapter);

        mRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
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

        return false;
    }


    public void doLoadMore() {
        this.showHttpLoadingView();
        if (NetWorkManagerUtil.isNetworkAvailable()) {
            this.mPullType = PullType.LOADMORE;
            this.onLoadMore();
        } else {
            this.simpleFinishRefreshOrLoading();
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
            simpleFinishRefreshOrLoading();
        }

    }

    protected void simpleFinishRefreshOrLoading() {
        mRecyclerView.setPullLoadMoreCompleted();
        this.showHttpResultView();
    }

    protected void finishRefreshOrLoading(List<T> data, int position, boolean isIncrement) {
        if (PullType.REFRESH == this.mPullType) {
            this.data.clear();
        }

        if (data != null && data.size() > 0) {
            this.data.addAll(position, data);
        }

        this.mAdapter.notifyDataSetChanged();
        this.showHttpResultView();
        this.simpleFinishRefreshOrLoading();
    }

    protected void finishRefreshOrLoading(List<T> data, boolean isIncrement) {
        this.finishRefreshOrLoading(data, this.getDataSize(), isIncrement);
    }

    @SFIntegerMessage(
            messageId = 105
    )
    public void onNetwokChange(GlobalEvent.NetworkEvent event) {
        if (event.hasNetwork && !this.isListViewHasData()) {
            this.doRefresh();
        }

    }


    protected int getViewType(int position) {
        return 0;
    }

    protected BaseAdapter WrapAdapterFactory(Bundle savedInstanceState, BaseAdapter adapter, PullToRefreshListView pullListView) {
        return adapter;
    }

    protected void onWrappAdapterCreated(BaseAdapter adapter, PullToRefreshListView pullListView) {
    }

    protected int getDataSize() {
        return this.data.size();
    }

    protected long getPullItemId(int position) {
        return this.mAdapter.getItemId(position);
    }

    protected abstract boolean onRefresh();

    protected boolean onRefreshNoNetwork() {
        return false;
    }

    protected abstract boolean onLoadMore();

    protected abstract int[] getLayoutIds();

    abstract protected  VH onCreateViewHolderOnFragment(ViewGroup viewGroup, int viewType);

    abstract protected void onBindViewHolderFragment(VH viewHolder,int postion);

    class LoadMoreViewHolder extends RecyclerView.ViewHolder{
        public TextView mLoadMoreTv;
        public ImageView mLoadMoreIv;
        public LoadMoreViewHolder(View itemView) {
            super(itemView);
        }

        public void showLoadMore(){

        }

        public void showFinishLoadMore(){

        }
    }
    abstract class BaseRecyclerViewAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == LOAD_MORE){
                View loadMoreView = LayoutInflater.from(getActivity()).inflate(R.layout.load_more_item,null);
                mLoadMoreViewHolder = new LoadMoreViewHolder(loadMoreView);
                return mLoadMoreViewHolder;
            }else {
                return onCreateViewHolderOnFragment(parent,viewType);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(getItemViewType(position) != LOAD_MORE){
                onBindViewHolderFragment((VH)holder,position);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(position == data.size()){
                return LOAD_MORE;
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return data.size() + 1;
        }
    }
}
