package xnt.com.fun;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.basesmartframe.baseadapter.BaseAdapterHelper;
import com.sf.loglib.L;
import com.sf.utils.baseutil.DateFormatHelp;
import com.sf.utils.baseutil.SFToast;
import com.sf.utils.baseutil.SpUtil;

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
import xnt.com.fun.bean.News;

/**
 * Created by NetEase on 2016/10/9 0009.
 */
public class NYFragmentNews extends NYBasePullListFragment<News> {

    private final int PAGE_SIZE = 10;
    private String mLatestTime;
    private List<News> mNews = new ArrayList<>();
    private String getLoadMoreTime(){//加载更多时间
        if (getDataSize()>0) {
            News news = getPullItem(getDataSize() - 1);
            if (news != null ){
                return news.getUpdatedAt();
            }

        }
        return DateFormatHelp.dateTimeFormat(Calendar.getInstance(),DateFormatHelp._YYYYMMDDHHMMSS);
    }

    private String getRefreshTime(){//刷新时间
        if (TextUtils.isEmpty(mLatestTime)) {
            mLatestTime = SpUtil.getString(getActivity(), "news_latest_time");
            if (TextUtils.isEmpty(mLatestTime)){
                mLatestTime = "2015-05-05 16:20:22";
            }
        } else {
            if (getDataSize()>0) {
                News news = getPullItem(0);
                if (news != null){
                    mLatestTime = news.getUpdatedAt();
                }
            }
        }

        return mLatestTime;
    }
    @Override
    protected boolean onRefresh() {
        getNews(true);
        return false;
    }

    private void getNews(final boolean refresh) {
        BmobQuery<News> query = new BmobQuery<News>();
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(PAGE_SIZE);
//        query.order("updatedAt");
        String updateContent = refresh? getRefreshTime(): getLoadMoreTime();
        Date updateData = DateFormatHelp.StrDateToCalendar(updateContent,DateFormatHelp._YYYYMMDDHHMMSS);
        if (refresh) { //refresh
            query.addWhereGreaterThanOrEqualTo("updatedAt", new BmobDate(updateData));
        }else {//load more
            query.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(updateData));
        }
        //执行查询方法
        query.findObjects(new FindListener<News>() {
            @Override
            public void done(List<News> picGroups, BmobException e) {
                if (e == null) {
                    //新加载出来的数据
                    List<News> diffNews = removeExist(picGroups);
                    Collections.sort(diffNews, new Comparator<News>() {
                        @Override
                        public int compare(News lhs, News rhs) {
                            return -lhs.getUpdatedAt().compareTo(rhs.getUpdatedAt());
                        }
                    });
                    List<News> newBeans = null;
                    //后续的刷新操作
                    if (refresh){
                        mNews.addAll(0,diffNews);
                        if (diffNews != null && diffNews.size() > 0){//保存当前刷新的时间
                            SpUtil.save(getActivity(), "pic_latest_time",mLatestTime);
                        }
                        if (mNews.size() > PAGE_SIZE){
                            List<News> tempPicGroups = new ArrayList<>(mNews);
                            mNews.clear();
                            mNews.addAll(tempPicGroups.subList(0,PAGE_SIZE));
                        }
                        newBeans = mNews;
                    }else{//加载更多操作
                        mNews.addAll(diffNews);
                        newBeans = diffNews;
                    }
                    //去重复
                    finishRefreshOrLoading(newBeans, true);
                } else {
                    mNews.clear();
                    finishRefreshOrLoading(null,false);
                    L.info("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }
    //一般只有第一个是重复的
    private   List<News> removeExist(List<News> picGroups) {
        List<News> diffNews = new ArrayList<>();
        if (picGroups == null || picGroups.size() == 0) {
            return diffNews;
        }
        int size = getDataSize();

        News picGroup = null;
        for (int j = 0; j < picGroups.size(); j++) {
            boolean exist = false;
            picGroup = picGroups.get(j);
            for (int i = 0; i < size; i++) {
                News news = getPullItem(i);
                if (picGroup.equals(news)) {
                    exist = true;
                    break;
                }
            }
            if (!exist){
                diffNews.add(picGroup);
            }
        }
        return diffNews;
    }
    @Override
    protected boolean onLoadMore() {
        getNews(false);
        return false;
    }

    @Override
    protected int[] getLayoutIds() {
        return new int[]{R.layout.ny_news_item};
    }

    @Override
    protected void bindView(BaseAdapterHelper help, int position, News bean) {
        help.setImageBuilder(R.id.news_iv, bean.newsCoverUrl);
//        help.setText(R.id.news_label_tv, bean.getLabel());
        help.setText(R.id.news_title_tv, bean.newsTitle);
    }

    @Override
    protected boolean onRefreshNoNetwork() {
        SFToast.showToast(R.string.no_network);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int curPos = position - getHeadViewCount();
        News bean = getPullItem(curPos);
        Intent intent = new Intent(getActivity(), NYNewsDetailActivity.class);
        intent.putExtra(NYNewsDetailActivity.NEWS_ID, bean.newsId);
        startActivity(intent);
    }
}
