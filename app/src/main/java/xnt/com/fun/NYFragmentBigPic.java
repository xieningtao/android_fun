package xnt.com.fun;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.basesmartframe.baseadapter.BaseAdapterHelper;
import com.basesmartframe.bitmap.rounddrawable.RoundedImageView;
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
import xnt.com.fun.bean.CardPicGroup;
import xnt.com.fun.config.DisplayOptionConfig;
import xnt.com.fun.tiantu.ActivityPhotoPreview;
import xnt.com.fun.tiantu.NYPhotoShowActivity;

/**
 * Created by mac on 2018/6/2.
 */

public class NYFragmentBigPic extends NYBasePullListFragment<CardPicGroup> {
    private static final float mWH = 5.0f / 7.0f;
    private String mLatestTime;
    private List<CardPicGroup> mCardPicGroups = new ArrayList<>();
    private static final int PIC_PAGE_SIZE = 10;
    private int mPicWidth;
    private int mPicHeight;

    private String getLoadMoreTime() {//加载更多时间
        if (getDataSize() > 0) {
            CardPicGroup pairPicBean = getPullItem(getDataSize() - 1);
            if (pairPicBean != null) {
                return pairPicBean.getUpdatedAt();
            }
        }
        return DateFormatHelp.dateTimeFormat(Calendar.getInstance(), DateFormatHelp._YYYYMMDDHHMMSS);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Drawable drawable = getResources().getDrawable(R.drawable.ny_pic_divider);
        getPullToRefreshListView().getRefreshableView().setDivider(drawable);
        getPullToRefreshListView().getRefreshableView().setDividerHeight(UnitHelp.dip2px(getActivity(), 8));
        mPicWidth = Utils.getPicWidth(getActivity());
        mPicHeight = Utils.getPicHeight(mPicWidth, mWH);
    }

    private String getRefreshTime() {//刷新时间
        if (TextUtils.isEmpty(mLatestTime)) {
            mLatestTime = SpUtil.getString(getActivity(), "pic_latest_time");
            if (TextUtils.isEmpty(mLatestTime)) {
                mLatestTime = "2015-05-05 16:20:22";
            }
        } else {
            if (getDataSize() > 0) {
                CardPicGroup pairPicBean = getPullItem(0);
                if (pairPicBean != null) {
                    mLatestTime = pairPicBean.getUpdatedAt();
                }
            }
        }

        return mLatestTime;
    }

    @Override
    protected boolean onRefresh() {
        getPicByBmob(true);
        return false;
    }

    private void getPicByBmob(final boolean refresh) {
        BmobQuery<CardPicGroup> query = new BmobQuery<CardPicGroup>();
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(PIC_PAGE_SIZE);
        query.order("-updatedAt");
        String updateContent = refresh ? getRefreshTime() : getLoadMoreTime();
        Date updateData = DateFormatHelp.StrDateToCalendar(updateContent, DateFormatHelp._YYYYMMDDHHMMSS);
        if (refresh) { //refresh
            query.setSkip(0);
            query.addWhereGreaterThanOrEqualTo("updatedAt", new BmobDate(updateData));
        } else {//load more
//            query.setSkip()
            query.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(updateData));
        }



        //执行查询方法
        query.findObjects(new FindListener<CardPicGroup>() {
            @Override
            public void done(List<CardPicGroup> CardPicGroups, BmobException e) {
                if (e == null) {
                    //新加载出来的数据
                    List<CardPicGroup> diffCardPicGroups = removeExist(CardPicGroups);
                    if (diffCardPicGroups.size() % 2 != 0) {
                        diffCardPicGroups.remove(0);
                    }
                    Collections.sort(diffCardPicGroups, new Comparator<CardPicGroup>() {
                        @Override
                        public int compare(CardPicGroup lhs, CardPicGroup rhs) {
                            return -lhs.getUpdatedAt().compareTo(rhs.getUpdatedAt());
                        }
                    });
                    List<CardPicGroup> pairPicBeans = null;
                    //后续的刷新操作
                    if (refresh) {
                        mCardPicGroups.addAll(0, diffCardPicGroups);
                        if (diffCardPicGroups != null && diffCardPicGroups.size() > 0) {//保存当前刷新的时间
                            SpUtil.save(getActivity(), "pic_latest_time", mLatestTime);
                        }
                        if (mCardPicGroups.size() > PIC_PAGE_SIZE) {
                            List<CardPicGroup> tempCardPicGroups = new ArrayList<>(mCardPicGroups);
                            mCardPicGroups.clear();
                            mCardPicGroups.addAll(tempCardPicGroups.subList(0, PIC_PAGE_SIZE));
                        }
                        pairPicBeans = mCardPicGroups;
                    } else {//加载更多操作
                        mCardPicGroups.addAll(diffCardPicGroups);
                        pairPicBeans = diffCardPicGroups;
                    }
                    //去重复
                    if (refresh) {
                        finishRefreshOrLoading(pairPicBeans,0, true);
                    }else {
                        finishRefreshOrLoading(pairPicBeans, true);
                    }
                } else {
                    mCardPicGroups.clear();
                    finishRefreshOrLoading(null, false);
                    L.info("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    //一般只有第一个是重复的
    private List<CardPicGroup> removeExist(List<CardPicGroup> CardPicGroups) {
        List<CardPicGroup> diffNews = new ArrayList<>();
        if (CardPicGroups == null || CardPicGroups.size() == 0) {
            return diffNews;
        }
        int size = getDataSize();

        CardPicGroup CardPicGroup = null;
        for (int j = 0; j < CardPicGroups.size(); j++) {
            boolean exist = false;
            CardPicGroup = CardPicGroups.get(j);
            for (int i = 0; i < size; i++) {
                CardPicGroup news = getPullItem(i);
                if (CardPicGroup.equals(news)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                diffNews.add(CardPicGroup);
            }
        }
        return diffNews;
    }


    @Override
    protected boolean onLoadMore() {
        getPicByBmob(false);
        return false;
    }

    @Override
    protected int[] getLayoutIds() {
        return new int[]{R.layout.ny_big_pic_item};
    }

    @Override
    protected void bindView(BaseAdapterHelper help, int i, final CardPicGroup cardPicGroup) {
        RoundedImageView picLayout = help.getView(R.id.big_pic_iv);
        ViewGroup.LayoutParams picParams = picLayout.getLayoutParams();
        picParams.width = mPicWidth;
        picParams.height = mPicHeight;
        picLayout.setLayoutParams(picParams);
        help.setImageBuilder(R.id.big_pic_iv, cardPicGroup.imgUrl, DisplayOptionConfig.getDisplayOption(R.drawable.app_icon));
        help.setText(R.id.pic_desc_tv, cardPicGroup.imgDesc + "");
        help.setText(R.id.pic_label_tv, cardPicGroup.imgLabel);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CardPicGroup cardPicGroup = getPullItem(position - getHeadViewCount());
        Intent intent = new Intent(getActivity(), NYPhotoShowActivity.class);
        intent.putExtra(ActivityPhotoPreview.IMAGE_GROUP_ID, cardPicGroup.getObjectId());
        startActivity(intent);
    }
}
