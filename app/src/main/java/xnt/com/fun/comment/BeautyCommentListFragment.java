package xnt.com.fun.comment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sf.loglib.L;
import com.sf.utils.baseutil.DateFormatHelp;
import com.sf.utils.baseutil.SFToast;
import com.sf.utils.baseutil.SystemUIHelp;
import com.sflib.reflection.core.SFBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import xnt.com.fun.FragmentUtils;
import xnt.com.fun.MessageId;
import xnt.com.fun.NYDateFormatHelper;
import xnt.com.fun.R;
import xnt.com.fun.Utils;
import xnt.com.fun.base.BaseRecycleViewFragment;
import xnt.com.fun.bean.Beauty;
import xnt.com.fun.bean.BeautyComment;
import xnt.com.fun.bean.NYBmobUser;
import xnt.com.fun.config.DisplayOptionConfig;
import xnt.com.fun.login.ThirdLoginActivity;

//评论对话框
public class BeautyCommentListFragment extends BaseRecycleViewFragment {
    public static final String BEAUTY_GROUP_ID = "beauty_group_id";
    private static final int PIC_PAGE_SIZE = 10;
    private List<BeautyComment> mBeautyComments = new ArrayList<>();
    private BeautyAdapter mAdapter;
    private String mLatestTime;

    private TextView mSendTv;
    private EditText mCommentEt;

    @Override
    protected boolean onRefresh() {
        getBeautyCommentList(true);
        return false;
    }

    @Override
    protected boolean onLoadMore() {
        getBeautyCommentList(false);
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comment_dialog, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentUtils.removeViewWithSlideBottom(getActivity(), "beautyComment");
            }
        });
        view.findViewById(R.id.beauty_close_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentUtils.removeViewWithSlideBottom(getActivity(), "beautyComment");
            }
        });

        mSendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mCommentEt.getText())) {
                    SFToast.showToast("请输入内容");
                    return;
                }
                SystemUIHelp.hideSoftKeyboard(getActivity(), mCommentEt);
                String commentContent = mCommentEt.getText().toString();
                if(Utils.isLogin()) {
                    postComment(getBeautyId(), commentContent);
                }else {
                    ThirdLoginActivity.toLogin(getActivity());
                }
            }
        });
        mAdapter = new BeautyAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void increaseCommentNum(){
        final Beauty beauty = new Beauty();
        beauty.setObjectId(getBeautyId());
        beauty.increment("commentNum");
        beauty.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
            }
        });
    }

    private void postComment(String beautyId, String commentContent) {
        final BeautyComment beautyComment = new BeautyComment();
        final Beauty beauty = new Beauty();
        beauty.setObjectId(beautyId);
        beautyComment.beautyId = new BmobPointer(beauty);
        beautyComment.content = commentContent;
        beautyComment.userId = BmobUser.getCurrentUser(NYBmobUser.class);
        beautyComment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    mCommentEt.setText("");
                    mBeautyComments.add(0,beautyComment);
                    mAdapter.notifyDataSetChanged();
                    increaseCommentNum();
                    SFBus.send(MessageId.COMMENT_NUM_CHANGE);
                }
            }
        });
    }

    private String getRefreshTime() {//刷新时间
        if (TextUtils.isEmpty(mLatestTime)) {
            mLatestTime = "2015-05-05 16:20:22";
        } else {
            if (mBeautyComments.size() > 0) {
                BeautyComment picComment = mBeautyComments.get(0);
                if (picComment != null) {
                    mLatestTime = picComment.getUpdatedAt();
                }
            }
        }

        return mLatestTime;
    }

    private String getLoadMoreTime() {//加载更多时间
        if (mBeautyComments.size() > 0) {
            BeautyComment picComment = mBeautyComments.get(mBeautyComments.size() - 1);
            if (picComment != null) {
                return picComment.getUpdatedAt();
            }
        }
        return DateFormatHelp.dateTimeFormat(Calendar.getInstance(), DateFormatHelp._YYYYMMDDHHMMSS);
    }


    private String getBeautyId() {
        Bundle bundle = getArguments();
        return bundle.getString(BEAUTY_GROUP_ID);
    }


    private void getBeautyCommentList(final boolean refresh) {
        BmobQuery<BeautyComment> query = new BmobQuery<BeautyComment>();
        Beauty picGroup = new Beauty();
        picGroup.setObjectId(getBeautyId());
        query.addWhereEqualTo("beautyId", new BmobPointer(picGroup));
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
        query.findObjects(new FindListener<BeautyComment>() {
            @Override
            public void done(List<BeautyComment> picComments, BmobException e) {
                if (e == null) {
                    //新加载出来的数据
                    List<BeautyComment> diffBeauties = removeExist(picComments);

                    Collections.sort(diffBeauties, new Comparator<BeautyComment>() {
                        @Override
                        public int compare(BeautyComment lhs, BeautyComment rhs) {
                            return -lhs.getUpdatedAt().compareTo(rhs.getUpdatedAt());
                        }
                    });
                    boolean hasMore = true;
                    //后续的刷新操作
                    if (refresh) {
                        mBeautyComments.clear();
                        mBeautyComments.addAll(0, diffBeauties);
                    } else {//加载更多操作
                        mBeautyComments.addAll(diffBeauties);
                        hasMore = diffBeauties.size() != 0;
                    }
                    mAdapter.notifyDataSetChanged();
                    simpleFinishRefreshOrLoading(hasMore);
                } else {
                    simpleFinishRefreshOrLoading(false);
                    L.info("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    //一般只有第一个是重复的
    private List<BeautyComment> removeExist(List<BeautyComment> picComments) {
        List<BeautyComment> diffNews = new ArrayList<>();
        if (picComments == null || picComments.size() == 0) {
            return diffNews;
        }
        int size = mBeautyComments.size();

        BeautyComment BeautyComment = null;
        for (int j = 0; j < picComments.size(); j++) {
            boolean exist = false;
            BeautyComment = picComments.get(j);
            for (int i = 0; i < size; i++) {
                BeautyComment news = mBeautyComments.get(i);
                if (BeautyComment.equals(news)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                diffNews.add(BeautyComment);
            }
        }
        return diffNews;
    }


    class BeautyCommentViewHolder extends RecyclerView.ViewHolder {
        public ImageView mUserIv;
        public TextView mUserNameTv;
        public TextView mContentTv;
        public TextView mTimeTv;

        public BeautyCommentViewHolder(View itemView) {
            super(itemView);
            mUserIv = (ImageView) itemView.findViewById(R.id.pic_user_iv);
            mUserNameTv = (TextView) itemView.findViewById(R.id.pic_comment_name);
            mContentTv = (TextView) itemView.findViewById(R.id.pic_comment_content);
            mTimeTv = (TextView) itemView.findViewById(R.id.pic_comment_time);
        }
    }

    class BeautyAdapter extends RecyclerView.Adapter<BeautyCommentViewHolder> {

        @Override
        public BeautyCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View beautyPicView = LayoutInflater.from(getActivity()).inflate(R.layout.pic_comment_item_view, null);
            return new BeautyCommentViewHolder(beautyPicView);
        }

        @Override
        public void onBindViewHolder(BeautyCommentViewHolder holder, final int position) {
            BeautyComment beautyComment = mBeautyComments.get(position);
            holder.mContentTv.setText(beautyComment.content);
            if (beautyComment.userId != null) {
                holder.mUserNameTv.setText(beautyComment.userId.getNick());
                ImageLoader.getInstance().displayImage(beautyComment.userId.getAvatarUrl(), holder.mUserIv, DisplayOptionConfig.getDisplayOption(R.drawable.base_avatar_default_bg));
            }
            holder.mTimeTv.setText(NYDateFormatHelper.formatTime(beautyComment.getCreatedAt()));
        }

        @Override
        public int getItemCount() {
            return mBeautyComments.size();
        }
    }
}
