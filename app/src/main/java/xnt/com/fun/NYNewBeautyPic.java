package xnt.com.fun;

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
import com.sf.utils.baseutil.SystemUIWHHelp;
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
import xnt.com.fun.bean.Beauty;
import xnt.com.fun.config.DisplayOptionConfig;

/**
 * Created by mac on 2018/6/2.
 */

public class NYNewBeautyPic extends NYBasePullListFragment<Beauty> {
    private float mWH = 5.0f / 7.0f;
    private String mLatestTime;
    private List<Beauty> mBeautys = new ArrayList<>();
    private static final int PIC_PAGE_SIZE = 10;
    private int mPicWidth;
    private int mPicHeight;

    private String getLoadMoreTime() {//加载更多时间
        if (getDataSize() > 0) {
            Beauty pairPicBean = getPullItem(getDataSize() - 1);
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
        mPicWidth = getPicWidth();
        mPicHeight = getPicHeight(mPicWidth, mWH);
    }

    private String getRefreshTime() {//刷新时间
        if (TextUtils.isEmpty(mLatestTime)) {
            mLatestTime = SpUtil.getString(getActivity(), "pic_latest_time");
            if (TextUtils.isEmpty(mLatestTime)) {
                mLatestTime = "2015-05-05 16:20:22";
            }
        } else {
            if (getDataSize() > 0) {
                Beauty pairPicBean = getPullItem(0);
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
        BmobQuery<Beauty> query = new BmobQuery<Beauty>();
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(PIC_PAGE_SIZE);
//        query.order("updatedAt");
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
                    if (diffBeautys.size() % 2 != 0) {
                        diffBeautys.remove(0);
                    }
                    Collections.sort(diffBeautys, new Comparator<Beauty>() {
                        @Override
                        public int compare(Beauty lhs, Beauty rhs) {
                            return -lhs.getUpdatedAt().compareTo(rhs.getUpdatedAt());
                        }
                    });
                    List<Beauty> pairPicBeans = null;
                    //后续的刷新操作
                    if (refresh) {
                        mBeautys.addAll(0, diffBeautys);
                        if (diffBeautys != null && diffBeautys.size() > 0) {//保存当前刷新的时间
                            SpUtil.save(getActivity(), "pic_latest_time", mLatestTime);
                        }
                        if (mBeautys.size() > PIC_PAGE_SIZE) {
                            List<Beauty> tempBeautys = new ArrayList<>(mBeautys);
                            mBeautys.clear();
                            mBeautys.addAll(tempBeautys.subList(0, PIC_PAGE_SIZE));
                        }
                        pairPicBeans = mBeautys;
                    } else {//加载更多操作
                        mBeautys.addAll(diffBeautys);
                        pairPicBeans = diffBeautys;
                    }
                    //去重复
                    if (refresh) {
                        finishRefreshOrLoading(pairPicBeans,0, true);
                    }else {
                        finishRefreshOrLoading(pairPicBeans, true);
                    }
                } else {
                    mBeautys.clear();
                    finishRefreshOrLoading(null, false);
                    L.info("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    //一般只有第一个是重复的
    private List<Beauty> removeExist(List<Beauty> Beautys) {
        List<Beauty> diffNews = new ArrayList<>();
        if (Beautys == null || Beautys.size() == 0) {
            return diffNews;
        }
        int size = getDataSize();

        Beauty Beauty = null;
        for (int j = 0; j < Beautys.size(); j++) {
            boolean exist = false;
            Beauty = Beautys.get(j);
            for (int i = 0; i < size; i++) {
                Beauty news = getPullItem(i);
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
    protected boolean onLoadMore() {
        getPicByBmob(false);
        return false;
    }

    private int getPicWidth() {
        int screenWidth = SystemUIWHHelp.getScreenRealWidth(getActivity());
        int reminderWidth = screenWidth - UnitHelp.dip2px(getActivity(), 8 + 8);
        return reminderWidth;
    }

    private int getPicHeight(int width, float ratio) {
        return (int) (width / ratio);
    }

    @Override
    protected int[] getLayoutIds() {
        return new int[]{R.layout.ny_big_pic_item};
    }

    @Override
    protected void bindView(BaseAdapterHelper help, int i, final Beauty beauty) {
        RoundedImageView picLayout = help.getView(R.id.big_pic_iv);
        ViewGroup.LayoutParams picParams = picLayout.getLayoutParams();
        picParams.width = mPicWidth;
        picParams.height = mPicHeight;
        picLayout.setLayoutParams(picParams);
        help.setImageBuilder(R.id.big_pic_iv, beauty.imgUrl, DisplayOptionConfig.getDisplayOption(R.drawable.app_icon));
        if (TextUtils.isEmpty(beauty.imgDesc)){
            help.getView(R.id.pic_desc_tv).setVisibility(View.GONE);
        }else {
            help.getView(R.id.pic_desc_tv).setVisibility(View.VISIBLE);
            help.setText(R.id.pic_desc_tv, beauty.imgDesc + "");
        }
        help.setText(R.id.pic_label_tv, beauty.imgLabel);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      
    }
}
