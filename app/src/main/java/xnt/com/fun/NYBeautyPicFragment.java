package xnt.com.fun;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sf.loglib.L;
import com.sf.utils.baseutil.SpUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import xnt.com.fun.base.BaseRecycleViewFragment;
import xnt.com.fun.bean.Beauty;
import xnt.com.fun.config.DisplayOptionConfig;
import xnt.com.fun.tiantu.NYBeautyShowActivity;

public class NYBeautyPicFragment extends BaseRecycleViewFragment implements BeautyModel.OnDataChangeListener {
    private float mWH = 5.0f / 7.0f;
    private BeautyAdapter mAdapter;
    private int mLatestIndexId = -1;
    private static final int PIC_PAGE_SIZE = 10;
//    private List<Beauty> mBeauties = new ArrayList<>();
    private int mPicWidth;
    private int mPicHeight;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPicWidth = Utils.getBeautyPicWidth(getActivity())/2;
        mPicHeight = Utils.getPicHeight(mPicWidth,mWH);
        mPullLoadMoreRv.setStaggeredGridLayout(2);
        mAdapter = new BeautyAdapter();
        mPullLoadMoreRv.setAdapter(mAdapter);
        BeautyModel.getInstance().registerListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BeautyModel.getInstance().unregisterListener(this);
    }

    @Override
    protected boolean onRefresh() {
        getPicByBmob(true);
        return false;
    }

    @Override
    protected boolean onLoadMore() {
        getPicByBmob(false);
        return false;
    }

    private int getRefreshIndexId() {//刷新时间
        if (mLatestIndexId == -1) {
            mLatestIndexId = SpUtil.getInt(getActivity(), "pic_latest_index");
        } else {
            if (BeautyModel.getInstance().getBeautySize() > 0) {
                Beauty pairPicBean = BeautyModel.getInstance().getBeauty(0);
                if (pairPicBean != null) {
                    mLatestIndexId = pairPicBean.indexId;
                }
            }
        }
        return mLatestIndexId;
    }

    private int getLoadMoreIndexId() {//加载更多时间
        if (BeautyModel.getInstance().getBeautySize() > 0) {
            Beauty pairPicBean = BeautyModel.getInstance().getBeauty(BeautyModel.getInstance().getBeautySize() - 1);
            if (pairPicBean != null) {
                return pairPicBean.indexId;
            }
        }
        return 100;
    }



    private void getPicByBmob(final boolean refresh) {
        BmobQuery<Beauty> query = new BmobQuery<Beauty>();
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(PIC_PAGE_SIZE);
        query.order("-indexId");
//        String updateContent = refresh ? getRefreshTime() : getLoadMoreTime();
        int indexId = refresh ? getRefreshIndexId() : getLoadMoreIndexId();
//        Date updateData = DateFormatHelp.StrDateToCalendar(updateContent, DateFormatHelp._YYYYMMDDHHMMSS);
//        if (refresh) { //refresh
//            query.addWhereGreaterThanOrEqualTo("indexId", new BmobDate(updateData));
//        } else {//load more
//            query.addWhereLessThanOrEqualTo("indexId", new BmobDate(updateData));
//        }
        if (refresh) { //refresh
            query.addWhereGreaterThanOrEqualTo("indexId", indexId);
        } else {//load more
            query.addWhereLessThanOrEqualTo("indexId", indexId);
        }


        //执行查询方法
        query.findObjects(new FindListener<Beauty>() {
            @Override
            public void done(List<Beauty> Beautys, BmobException e) {
                if (e == null) {
                    //新加载出来的数据
                    List<Beauty> diffBeautys = removeExist(Beautys);
                    Collections.sort(diffBeautys, new Comparator<Beauty>() {
                        @Override
                        public int compare(Beauty lhs, Beauty rhs) {
                            return -lhs.getUpdatedAt().compareTo(rhs.getUpdatedAt());
                        }
                    });
                    //后续的刷新操作
                    if (refresh) {
                        BeautyModel.getInstance().addBeauties(0, diffBeautys);
                        if (diffBeautys != null && diffBeautys.size() > 0) {//保存当前刷新的时间
//                            SpUtil.save(getActivity(), "pic_latest_time", mLatestTime);
                            SpUtil.save(getActivity(),"pic_latest_index",mLatestIndexId);
                        }
                        //只保留前面一页
                        if (BeautyModel.getInstance().getBeautySize() > PIC_PAGE_SIZE) {
                            List<Beauty> tempBeautys = new ArrayList<>(BeautyModel.getInstance().getBeauties());
                            BeautyModel.getInstance().clear();
                            BeautyModel.getInstance().addBeauties(tempBeautys.subList(0, PIC_PAGE_SIZE));
                        }

                    }else {
                        BeautyModel.getInstance().addBeauties(diffBeautys);
                    }
                    mAdapter.notifyDataSetChanged();
                    boolean hasMore = true;
                    if(!refresh) {
                        hasMore = diffBeautys.size() != 0;
                    }
                   simpleFinishRefreshOrLoading(hasMore);
                } else {
                    simpleFinishRefreshOrLoading(false);
                    L.info("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    //一般只有第一个是重复的
    private List<Beauty> removeExist(List<Beauty> beauties) {
        List<Beauty> diffNews = new ArrayList<>();
        if (beauties == null || beauties.size() == 0) {
            return diffNews;
        }
        int size = BeautyModel.getInstance().getBeautySize();

        Beauty Beauty = null;
        for (int j = 0; j < beauties.size(); j++) {
            boolean exist = false;
            Beauty = beauties.get(j);
            for (int i = 0; i < size; i++) {
                Beauty news = BeautyModel.getInstance().getBeauty(i);
                if (Beauty.equals(news)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                diffNews.add(Beauty);
            }
        }
        return diffNews;
    }

    @Override
    public void onDataChange() {
        mAdapter.notifyDataSetChanged();
    }

    class BeautyViewHolder extends RecyclerView.ViewHolder{
        public ImageView mPicIv;
        public TextView mPicLabel;
        public TextView mPicDesc;
        public BeautyViewHolder(View itemView) {
            super(itemView);
            mPicLabel = (TextView) itemView.findViewById(R.id.pic_label_tv);
            mPicDesc = (TextView) itemView.findViewById(R.id.pic_desc_tv);
            mPicIv = (ImageView) itemView.findViewById(R.id.big_pic_iv);
        }
    }
    class BeautyAdapter extends RecyclerView.Adapter<BeautyViewHolder>{

        @Override
        public BeautyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View beautyPicView = LayoutInflater.from(getActivity()).inflate(R.layout.ny_beauty_item,null);
            return new BeautyViewHolder(beautyPicView);
        }

        @Override
        public void onBindViewHolder(BeautyViewHolder holder, final int position) {
            ViewGroup.LayoutParams picParams = holder.mPicIv.getLayoutParams();
            picParams.width = mPicWidth;
            picParams.height = mPicHeight;
            holder.mPicIv.setLayoutParams(picParams);
            Beauty beauty = BeautyModel.getInstance().getBeauty(position);
            ImageLoader.getInstance().displayImage(beauty.imgUrl,holder.mPicIv, DisplayOptionConfig.getDefaultDisplayOption());
            if (TextUtils.isEmpty(beauty.imgDesc)){
                holder.mPicDesc.setVisibility(View.GONE);
            }else {
                holder.mPicDesc.setVisibility(View.VISIBLE);
                holder.mPicDesc.setText(beauty.imgDesc + "");
            }
            holder.mPicLabel.setText(beauty.imgLabel);
            holder.mPicIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), NYBeautyShowActivity.class);
                    //第一个数据的id
                    int indexId = BeautyModel.getInstance().getBeauty(0).indexId;
                    intent.putExtra(NYBeautyShowActivity.BEAUTY_TOTAL_SIZE,indexId);
                    intent.putExtra(NYBeautyShowActivity.BEAUTY_CUR_POS,position);
                    getActivity().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return BeautyModel.getInstance().getBeautySize();
        }
    }
}
