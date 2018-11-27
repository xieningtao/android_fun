package xnt.com.fun.comment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.basesmartframe.baseadapter.BaseAdapterHelper;
import com.basesmartframe.baseui.BasePullListFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sf.loglib.L;
import com.sf.utils.baseutil.DateFormatHelp;
import com.sf.utils.baseutil.UnitHelp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import xnt.com.fun.NYDateFormatHelper;
import xnt.com.fun.R;
import xnt.com.fun.bean.CardPicGroup;
import xnt.com.fun.config.DisplayOptionConfig;

import static xnt.com.fun.NYFragmentBigPic.PIC_GROUP_ID;

public class BigPicCommentListFragment extends BasePullListFragment<PicComment> {

    private static final int PIC_PAGE_SIZE = 10;
    private List<PicComment> mPicComments = new ArrayList<>();
    private String mLatestTime;


    private String getRefreshTime() {//刷新时间
        if (TextUtils.isEmpty(mLatestTime)) {
            mLatestTime = "2015-05-05 16:20:22";
        } else {
            if (getDataSize() > 0) {
                PicComment picComment = getPullItem(0);
                if (picComment != null) {
                    mLatestTime = picComment.getUpdatedAt();
                }
            }
        }

        return mLatestTime;
    }

    private String getLoadMoreTime() {//加载更多时间
        if (getDataSize() > 0) {
            PicComment picComment = getPullItem(getDataSize() - 1);
            if (picComment != null) {
                return picComment.getUpdatedAt();
            }
        }
        return DateFormatHelp.dateTimeFormat(Calendar.getInstance(), DateFormatHelp._YYYYMMDDHHMMSS);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPullToRefreshListView().setBackgroundResource(R.color.ny_main_bg);
        getPullToRefreshListView().getRefreshableView().setDividerHeight(UnitHelp.dip2px(getActivity(), 0));
    }

    private String getTopicId() {
        Bundle bundle = getArguments();
        return bundle.getString(PIC_GROUP_ID);
    }

    @Override
    protected boolean onRefresh() {
        getPicCommentList(true);
        return false;
    }

    @Override
    protected boolean onLoadMore() {
        getPicCommentList(false);
        return false;
    }

    @Override
    protected int[] getLayoutIds() {
        return new int[]{R.layout.pic_comment_item_view};
    }

    @Override
    protected void bindView(BaseAdapterHelper helper, int i, PicComment picComment) {
        helper.setText(R.id.pic_comment_content, picComment.content);
        if (picComment.userId != null) {
            helper.setText(R.id.pic_comment_name, picComment.userId.getNick());
            ImageView avatarIv = helper.getView(R.id.pic_user_iv);
            ImageLoader.getInstance().displayImage(picComment.userId.getAvatarUrl(),avatarIv,DisplayOptionConfig.getDisplayOption(R.drawable.base_avatar_default_bg));
        }
        helper.setText(R.id.pic_comment_time, NYDateFormatHelper.formatTime(picComment.getCreatedAt()));
//        helper.setImageBitmap(R.id.pic_user_iv,R.drawable.app_icon);
        if (i == getDataSize() - 1) {//最后一个
            helper.setVisible(R.id.pic_comment_divider, View.GONE);
        } else {
            helper.setVisible(R.id.pic_comment_divider, View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    private void getPicCommentList(final boolean refresh) {
        BmobQuery<PicComment> query = new BmobQuery<PicComment>();
        CardPicGroup picGroup = new CardPicGroup();
        picGroup.setObjectId(getTopicId());
        query.addWhereEqualTo("topicId", new BmobPointer(picGroup));
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(PIC_PAGE_SIZE);
        query.order("-updatedAt");
        query.include("userId");
        String updateContent = refresh ? getRefreshTime() : getLoadMoreTime();
        Date updateData = DateFormatHelp.StrDateToCalendar(updateContent, DateFormatHelp._YYYYMMDDHHMMSS);
        if (refresh) { //refresh
            query.setSkip(0);
            query.addWhereGreaterThanOrEqualTo("updatedAt", new BmobDate(updateData));
        } else {//load more
            query.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(updateData));
        }


        //执行查询方法
        query.findObjects(new FindListener<PicComment>() {
            @Override
            public void done(List<PicComment> picComments, BmobException e) {
                if (e == null) {
                    //新加载出来的数据
                    List<PicComment> diffCardPicGroups = removeExist(picComments);

                    Collections.sort(diffCardPicGroups, new Comparator<PicComment>() {
                        @Override
                        public int compare(PicComment lhs, PicComment rhs) {
                            return -lhs.getUpdatedAt().compareTo(rhs.getUpdatedAt());
                        }
                    });
                    List<PicComment> pairPicBeans = null;
                    //后续的刷新操作
                    if (refresh) {
                        mPicComments.addAll(0, diffCardPicGroups);
                        if (mPicComments.size() > PIC_PAGE_SIZE) {
                            List<PicComment> tempCardPicGroups = new ArrayList<>(mPicComments);
                            mPicComments.clear();
                            mPicComments.addAll(tempCardPicGroups.subList(0, PIC_PAGE_SIZE));
                        }
                        pairPicBeans = mPicComments;
                    } else {//加载更多操作
                        mPicComments.addAll(diffCardPicGroups);
                        pairPicBeans = diffCardPicGroups;
                    }
                    //去重复
                    if (refresh) {
                        finishRefreshOrLoading(pairPicBeans, 0, true);
                    } else {
                        finishRefreshOrLoading(pairPicBeans, true);
                    }
                } else {
                    mPicComments.clear();
                    finishRefreshOrLoading(null, false);
                    L.info("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    //一般只有第一个是重复的
    private List<PicComment> removeExist(List<PicComment> picComments) {
        List<PicComment> diffNews = new ArrayList<>();
        if (picComments == null || picComments.size() == 0) {
            return diffNews;
        }
        int size = getDataSize();

        PicComment PicComment = null;
        for (int j = 0; j < picComments.size(); j++) {
            boolean exist = false;
            PicComment = picComments.get(j);
            for (int i = 0; i < size; i++) {
                PicComment news = getPullItem(i);
                if (PicComment.equals(news)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                diffNews.add(PicComment);
            }
        }
        return diffNews;
    }
}
