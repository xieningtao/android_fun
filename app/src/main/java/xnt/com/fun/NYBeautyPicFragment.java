package xnt.com.fun;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.basesmartframe.bitmap.rounddrawable.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sf.loglib.L;
import com.sf.utils.baseutil.DateFormatHelp;
import com.sf.utils.baseutil.SpUtil;
import com.sf.utils.baseutil.UnitHelp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import xnt.com.fun.base.BaseRecycleViewFragment;
import xnt.com.fun.bean.Beauty;
import xnt.com.fun.config.DisplayOptionConfig;

public class NYBeautyPicFragment extends BaseRecycleViewFragment {
    private float mWH = 5.0f / 7.0f;
    private BeautyAdapter mAdapter;
    private String mLatestTime;
    private static final int PIC_PAGE_SIZE = 10;
    private List<Beauty> mBeauties = new ArrayList<>();
    private int mPicWidth;
    private int mPicHeight;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPicWidth = Utils.getPicWidth(getActivity())/2 - UnitHelp.dip2px(getActivity(),16);
        mPicHeight = Utils.getPicHeight(mPicWidth,mWH);
        mPullLoadMoreRv.setStaggeredGridLayout(2);
        mAdapter = new BeautyAdapter();
        mPullLoadMoreRv.setAdapter(mAdapter);
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

    private String getLoadMoreTime() {//加载更多时间
        if (mBeauties.size() > 0) {
            Beauty pairPicBean = mBeauties.get(mBeauties.size() - 1);
            if (pairPicBean != null) {
                return pairPicBean.getUpdatedAt();
            }
        }
        return DateFormatHelp.dateTimeFormat(Calendar.getInstance(), DateFormatHelp._YYYYMMDDHHMMSS);
    }

    private String getRefreshTime() {//刷新时间
        if (TextUtils.isEmpty(mLatestTime)) {
            mLatestTime = SpUtil.getString(getActivity(), "pic_latest_time");
            if (TextUtils.isEmpty(mLatestTime)) {
                mLatestTime = "2015-05-05 16:20:22";
            }
        } else {
            if (mBeauties.size() > 0) {
                Beauty pairPicBean = mBeauties.get(0);
                if (pairPicBean != null) {
                    mLatestTime = pairPicBean.getUpdatedAt();
                }
            }
        }

        return mLatestTime;
    }



    private void getPicByBmob(final boolean refresh) {
        BmobQuery<Beauty> query = new BmobQuery<Beauty>();
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(PIC_PAGE_SIZE);
        query.order("-updatedAt");
        String updateContent = refresh ? getRefreshTime() : getLoadMoreTime();
        Date updateData = DateFormatHelp.StrDateToCalendar(updateContent, DateFormatHelp._YYYYMMDDHHMMSS);
        if (refresh) { //refresh
            query.addWhereGreaterThanOrEqualTo("updatedAt", new BmobDate(updateData));
        } else {//load more
            query.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(updateData));
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
                        mBeauties.addAll(0, diffBeautys);
                        if (diffBeautys != null && diffBeautys.size() > 0) {//保存当前刷新的时间
                            SpUtil.save(getActivity(), "pic_latest_time", mLatestTime);
                        }
                        //只保留前面一页
                        if (mBeauties.size() > PIC_PAGE_SIZE) {
                            List<Beauty> tempBeautys = new ArrayList<>(mBeauties);
                            mBeauties.clear();
                            mBeauties.addAll(tempBeautys.subList(0, PIC_PAGE_SIZE));
                        }

                    }else {
                        mBeauties.addAll(diffBeautys);
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
        int size = mBeauties.size();

        Beauty Beauty = null;
        for (int j = 0; j < beauties.size(); j++) {
            boolean exist = false;
            Beauty = beauties.get(j);
            for (int i = 0; i < size; i++) {
                Beauty news = mBeauties.get(i);
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

    class BeautyViewHolder extends RecyclerView.ViewHolder{
        public RoundedImageView mPicIv;
        public TextView mPicLabel;
        public TextView mPicDesc;
        public BeautyViewHolder(View itemView) {
            super(itemView);
            mPicLabel = (TextView) itemView.findViewById(R.id.pic_label_tv);
            mPicDesc = (TextView) itemView.findViewById(R.id.pic_desc_tv);
            mPicIv = (RoundedImageView) itemView.findViewById(R.id.big_pic_iv);
        }
    }
    class BeautyAdapter extends RecyclerView.Adapter<BeautyViewHolder>{

        @Override
        public BeautyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View beautyPicView = LayoutInflater.from(getActivity()).inflate(R.layout.ny_beauty_item,null);
            return new BeautyViewHolder(beautyPicView);
        }

        @Override
        public void onBindViewHolder(BeautyViewHolder holder, int position) {
            ViewGroup.LayoutParams picParams = holder.mPicIv.getLayoutParams();
            picParams.width = mPicWidth;
            picParams.height = mPicHeight;
            holder.mPicIv.setLayoutParams(picParams);
            Beauty beauty = mBeauties.get(position);
            ImageLoader.getInstance().displayImage(beauty.imgUrl,holder.mPicIv, DisplayOptionConfig.getDisplayOption(R.drawable.app_icon));
            if (TextUtils.isEmpty(beauty.imgDesc)){
                holder.mPicDesc.setVisibility(View.GONE);
            }else {
                holder.mPicDesc.setVisibility(View.VISIBLE);
                holder.mPicDesc.setText(beauty.imgDesc + "");
            }
            holder.mPicLabel.setText(beauty.imgLabel);
        }

        @Override
        public int getItemCount() {
            return mBeauties.size();
        }
    }
}
