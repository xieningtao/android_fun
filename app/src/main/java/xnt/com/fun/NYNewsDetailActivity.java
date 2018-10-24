package xnt.com.fun;

import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.basesmartframe.baseui.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sf.loglib.L;
import com.sf.utils.baseutil.NetWorkManagerUtil;
import com.sflib.CustomView.newhttpview.HttpViewManager;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import xnt.com.fun.bean.StyleDetailItem;
import xnt.com.fun.bean.StyleNews;

import static xnt.com.fun.NYFragmentBigPic.mWH;

public class NYNewsDetailActivity extends BaseActivity {
    private RecyclerView mDetailRv;
    private TextView mTitleTv;
    private FrameLayout mErrorFl;
    private HttpViewManager mHttpViewManager;
    private List<StyleDetailItem> mDetailItems = new ArrayList<>();
    private NewsDetailAdapter mDetailAdapter = new NewsDetailAdapter();
    public static final String TITLE = "title";
    public static final String NEWS_ID = "news_id";


    private int mPicWidth;
    private int mPicHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        initActionBar();
        initView();
    }

    private void initActionBar() {
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
        View actionView = LayoutInflater.from(this).inflate(R.layout.ny_home_title,null);
        getActionBar().setCustomView(actionView,params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toolbar parent = (Toolbar) actionView.getParent();
            parent.setContentInsetsAbsolute(0, 0);
        }
    }

    private void initView(){
        mDetailRv = (RecyclerView) findViewById(R.id.news_detail_rv);
        mTitleTv = (TextView) findViewById(R.id.news_detail_title_tv);
        mErrorFl = (FrameLayout) findViewById(R.id.content_container);
        mDetailRv.setAdapter(mDetailAdapter);
        mHttpViewManager = HttpViewManager.createManagerByDefault(this, mErrorFl);
        mPicWidth = Utils.getPicWidth(this);
        mPicHeight = Utils.getPicHeight(mPicWidth, mWH);
        if (getIntent() != null) {
            String title = getIntent().getStringExtra(TITLE);
            mTitleTv.setText(title);
            String newsId = getIntent().getStringExtra(NEWS_ID);
            if (NetWorkManagerUtil.isNetworkAvailable()) {
                mHttpViewManager.showHttpLoadingView(false);
                getDetails(newsId);
            } else {
                mHttpViewManager.showHttpViewNoNetwork(false);
            }
        }
    }

    private void getDetails(String newsId){
        BmobQuery<StyleDetailItem> query = new BmobQuery<StyleDetailItem>();
        StyleNews styleNews = new StyleNews();
        styleNews.setObjectId(newsId);
        query.addWhereEqualTo("newsId", new BmobPointer(styleNews));
        //执行查询方法
        query.findObjects(new FindListener<StyleDetailItem>() {
            @Override
            public void done(List<StyleDetailItem> items, BmobException e) {
                if (e == null) {
                    mDetailItems.addAll(items);
                    mDetailAdapter.notifyDataSetChanged();
                    mHttpViewManager.dismissAllHttpView();
                } else {
                    mHttpViewManager.showHttpViewNOData(false);
                    L.info("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    class  NewsDetailViewHolder extends RecyclerView.ViewHolder{
        public ImageView mPicIv;
        public TextView mNewsTv;

        public NewsDetailViewHolder(View itemView) {
            super(itemView);
            mPicIv = (ImageView) itemView.findViewById(R.id.news_detail_iv);
            mNewsTv = (TextView) itemView.findViewById(R.id.news_detail_desc_tv);
        }
    }
    class NewsDetailAdapter extends RecyclerView.Adapter<NewsDetailViewHolder>{

        @Override
        public NewsDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View newsDetailView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_detail,null);
            return new NewsDetailViewHolder(newsDetailView);
        }

        @Override
        public void onBindViewHolder(NewsDetailViewHolder holder, int position) {
            StyleDetailItem item = mDetailItems.get(position);
            if(!TextUtils.isEmpty(item.imgDesc)) {
                holder.mNewsTv.setVisibility(View.VISIBLE);
                holder.mNewsTv.setText(item.imgDesc);
            }else {
                holder.mNewsTv.setVisibility(View.GONE);
            }
            ViewGroup.LayoutParams picParams = holder.mPicIv.getLayoutParams();
            picParams.width = mPicWidth;
            picParams.height = mPicHeight;
            holder.mPicIv.setLayoutParams(picParams);
            ImageLoader.getInstance().displayImage(item.imageUrl,holder.mPicIv);
        }

        @Override
        public int getItemCount() {
            return mDetailItems.size();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
