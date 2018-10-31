package xnt.com.fun;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.basesmartframe.baseadapter.BaseAdapterHelper;
import com.basesmartframe.bitmap.rounddrawable.RoundedImageView;
import com.basesmartframe.dialoglib.DialogFactory;
import com.sf.loglib.L;
import com.sf.utils.baseutil.DateFormatHelp;
import com.sf.utils.baseutil.SFToast;
import com.sf.utils.baseutil.SpUtil;
import com.sf.utils.baseutil.SystemUIHelp;
import com.sf.utils.baseutil.UnitHelp;
import com.sflib.CustomView.KeyBoardFrameLayout;
import com.sflib.CustomView.baseview.EditTextClearDroidView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import xnt.com.fun.bean.CardPicBean;
import xnt.com.fun.bean.CardPicGroup;
import xnt.com.fun.comment.PicComment;
import xnt.com.fun.config.DisplayOptionConfig;
import xnt.com.fun.tiantu.ActivityPhotoPreview;
import xnt.com.fun.tiantu.NYPhotoShowActivity;

/**
 * Created by mac on 2018/6/2.
 */

public class NYFragmentBigPic extends NYBasePullListFragment<CardPicGroup> {
    private static final float mWH = 5.0f / 7.0f;
    private static final int PIC_PAGE_SIZE = 10;
    private String mLatestTime;
    private List<CardPicGroup> mCardPicGroups = new ArrayList<>();
    private int mPicWidth;
    private int mPicHeight;
    private View mCommentView;
    private KeyBoardFrameLayout mKeyBoardView;
    private Button mSendBt;
    private EditText mCommentEt;
    private String curPicGroupId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recommend_fragment, null);
    }

    private String getLoadMoreTime() {//加载更多时间
        if (getDataSize() > 0) {
            CardPicGroup pairPicBean = getPullItem(getDataSize() - 1);
            if (pairPicBean != null) {
                return pairPicBean.getUpdatedAt();
            }
        }
        return DateFormatHelp.dateTimeFormat(Calendar.getInstance(), DateFormatHelp._YYYYMMDDHHMMSS);
    }


    private void deletePicBy(String groupId) {
        BmobQuery<CardPicBean> query = new BmobQuery<>();
        final CardPicGroup picGroup = new CardPicGroup();
        picGroup.setObjectId(groupId);
        query.addWhereEqualTo("PicGroupId", new BmobPointer(picGroup));
        //执行查询方法
        query.findObjects(new FindListener<CardPicBean>() {
            @Override
            public void done(final List<CardPicBean> cardPicBeans, BmobException e) {
                if (e == null) {
                    deleteSubPics(cardPicBeans, picGroup);
                } else {
                    L.error(TAG, "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    private void deleteSubPics(List<CardPicBean> cardPicBeans, final CardPicGroup picGroup) {
        final List<BmobObject> deleteBeans = new ArrayList<>();
        deleteBeans.addAll(cardPicBeans);
        new BmobBatch().deleteBatch(deleteBeans).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                boolean removeSuccessful = true;
                if(e==null){
                    for(int i=0;i<list.size();i++){
                        BatchResult result = list.get(i);
                        BmobException ex =result.getError();
                        if(ex==null){
                            L.info(TAG,"第"+i+"个数据批量删除成功");
                        }else{
                            removeSuccessful = false;
                            L.info(TAG,"第"+i+"个数据批量删除失败："+ex.getMessage()+","+ex.getErrorCode());
                        }
                    }

                }else{
                    removeSuccessful = false;
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
                if(!removeSuccessful) {
                    SFToast.showToast("子图删除失败");
                }else {
                    SFToast.showToast("子图删除成功");
                    deleteGroup(picGroup);
                }
            }
        });
    }

    private void deleteGroup(CardPicGroup picGroup) {
        List<BmobObject> deleteGroups = new ArrayList<>();
        deleteGroups.add(picGroup);
        new BmobBatch().deleteBatch(deleteGroups).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                boolean removeSuccessful = true;
                if(e != null){
                    removeSuccessful = false;
                }
                if(!removeSuccessful) {
                    SFToast.showToast("----->首图删除失败");
                }else {
                    SFToast.showToast("----->首图删除成功");
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Drawable drawable = getResources().getDrawable(R.drawable.ny_pic_divider);
        getPullToRefreshListView().getRefreshableView().setDivider(drawable);
        getPullToRefreshListView().getRefreshableView().setDividerHeight(UnitHelp.dip2px(getActivity(), 8));
        getPullToRefreshListView().getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View superUserView = layoutInflater.inflate(R.layout.super_user_operate_dialog,null);
                final Dialog operationDialog = DialogFactory.getNoTitleDialog(getActivity(),superUserView);
                operationDialog.show();
                final CardPicGroup cardPicGroup = getPullItem(position - getHeadViewCount());
                superUserView.findViewById(R.id.remove_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletePicBy(cardPicGroup.getObjectId());
                        operationDialog.dismiss();
                    }
                });
                editContent(position, superUserView, operationDialog);

                return true;
            }
        });
        mCommentView = view.findViewById(R.id.comment_view);
        mSendBt = (Button) view.findViewById(R.id.comment_send_tv);
        mCommentEt = (EditText) view.findViewById(R.id.comment_et);
        mKeyBoardView = (KeyBoardFrameLayout) view.findViewById(R.id.keyboard_fl);
        mKeyBoardView.setOnKeyBoardListener(new KeyBoardFrameLayout.onKeyBoardListener() {
            @Override
            public void onKeyboardVisible(boolean b) {
                if(b){
                    mCommentView.setVisibility(View.VISIBLE);
                }else {
                    mCommentView.setVisibility(View.GONE);
                }
            }
        });
        mCommentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUIHelp.hideSoftKeyboard(getActivity(),mCommentEt);
            }
        });
        mSendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mCommentEt.getText())){
                    SFToast.showToast("请输入内容");
                    return;
                }
                SystemUIHelp.hideSoftKeyboard(getActivity(),mCommentEt);
                String commentContent = mCommentEt.getText().toString();
                postComment(curPicGroupId,commentContent,null);
            }
        });
        mPicWidth = Utils.getPicWidth(getActivity());
        mPicHeight = Utils.getPicHeight(mPicWidth, mWH);
    }

    private void editContent(final int position, View superUserView, final Dialog operationDialog) {
        superUserView.findViewById(R.id.edit_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operationDialog.dismiss();
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View superUserView = layoutInflater.inflate(R.layout.super_user_edit_dialog,null);
                final Dialog editDialog = DialogFactory.getNoTitleDialog(getActivity(),superUserView);
                editDialog.show();
                final EditTextClearDroidView droidView = (EditTextClearDroidView) superUserView.findViewById(R.id.edit_view);
                final CardPicGroup cardPicGroup = getPullItem(position - getHeadViewCount());
                superUserView.findViewById(R.id.modify_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.isEmpty(droidView.getEditText().getText())){
                            SFToast.showToast(getString(R.string.input_word));
                            return;
                        }
                        editDialog.dismiss();
                        String content = droidView.getEditText().getText().toString();
                        cardPicGroup.imgDesc = content;
                        cardPicGroup.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                   SFToast.showToast("更新成功");
                                }else{
                                    SFToast.showToast("更新失败");
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void postComment(String picGroupId,String commentContent,String userId){
        PicComment picComment = new PicComment();
        CardPicGroup picGroup = new CardPicGroup();
        picGroup.setObjectId(picGroupId);
        picComment.topicId = new BmobPointer(picGroup);
        picComment.content = commentContent;
        if(TextUtils.isEmpty(userId)){
            picComment.userId = null;
        }else {
            BmobUser user = new BmobUser();
            user.setObjectId(userId);
            picComment.userId=new BmobPointer(user);
        }
        picComment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e == null){
                    SFToast.showToast("发表成功");
                }else {
                    SFToast.showToast("发表失败");
                }
            }
        });
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
                        finishRefreshOrLoading(pairPicBeans, 0, true);
                    } else {
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
        help.setOnClickListener(R.id.write_comment_iv, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUIHelp.showSoftKeyboard(getActivity(),mCommentEt);
                curPicGroupId = cardPicGroup.getObjectId();
            }
        });
        help.setOnClickListener(R.id.comment_tv, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void showKeyBoard() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CardPicGroup cardPicGroup = getPullItem(position - getHeadViewCount());
        Intent intent = new Intent(getActivity(), NYPhotoShowActivity.class);
        intent.putExtra(ActivityPhotoPreview.IMAGE_GROUP_ID, cardPicGroup.getObjectId());
        startActivity(intent);
    }

}
