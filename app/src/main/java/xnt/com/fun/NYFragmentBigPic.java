package xnt.com.fun;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.basesmartframe.baseadapter.BaseAdapterHelper;
import com.basesmartframe.data.ViewBind;
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

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import xnt.com.fun.bean.CardPicBean;
import xnt.com.fun.bean.CardPicGroup;
import xnt.com.fun.bean.NYBmobUser;
import xnt.com.fun.comment.BigPicCommentListFragment;
import xnt.com.fun.comment.PicComment;
import xnt.com.fun.config.DisplayOptionConfig;
import xnt.com.fun.tiantu.ActivityPhotoPreview;
import xnt.com.fun.tiantu.NYPhotoShowActivity;



/**
 * Created by mac on 2018/6/2.
 */

public class NYFragmentBigPic extends NYBasePullListFragment<CardPicGroup> {
    public static final String PIC_GROUP_ID = "pic_group_id";
    private static final float mWH = 5.0f / 7.0f;
    private static final int PIC_PAGE_SIZE = 10;
    private String mLatestTime;
    private List<CardPicGroup> mCardPicGroups = new ArrayList<>();
    private int mPicWidth;
    private int mPicHeight;
    private View mCommentView;
    private KeyBoardFrameLayout mKeyBoardView;
    private TextView mSendTv;
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


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Drawable drawable = getResources().getDrawable(R.drawable.ny_pic_divider);
        getPullToRefreshListView().getRefreshableView().setDivider(drawable);
        getPullToRefreshListView().getRefreshableView().setDividerHeight(UnitHelp.dip2px(getActivity(), 8));
        mCommentView = view.findViewById(R.id.comment_view);
        mSendTv = (TextView) view.findViewById(R.id.comment_send_tv);
        mCommentEt = (EditText) view.findViewById(R.id.comment_et);
        mKeyBoardView = (KeyBoardFrameLayout) view.findViewById(R.id.keyboard_fl);
        mKeyBoardView.setOnKeyBoardListener(new KeyBoardFrameLayout.onKeyBoardListener() {
            @Override
            public void onKeyboardVisible(boolean b) {
//                if(b){
//                    mCommentView.setVisibility(View.VISIBLE);
//                }else {
//                    mCommentView.setVisibility(View.GONE);
//                }
            }
        });
        mCommentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentView.setVisibility(View.GONE);
                SystemUIHelp.hideSoftKeyboard(getActivity(), mCommentEt);
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
                updateLatestComment(curPicGroupId, commentContent, null);
            }
        });
        mPicWidth = Utils.getBigPicWidth(getActivity());
        mPicHeight = Utils.getPicHeight(mPicWidth, mWH);
    }

    private void doSuperOperation(final CardPicGroup cardPicGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View superUserView = layoutInflater.inflate(R.layout.super_user_operate_dialog, null);
        final Dialog operationDialog = DialogHelper.getNoTitleDialog(getActivity(), superUserView);
        operationDialog.show();
        superUserView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operationDialog.dismiss();
            }
        });
        superUserView.findViewById(R.id.remove_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePicBy(cardPicGroup.getObjectId());
                operationDialog.dismiss();
            }
        });
        superUserView.findViewById(R.id.edit_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operationDialog.dismiss();
                updateContent(cardPicGroup);
            }
        });
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
                    //TODO，可以试一试全部加到一个列表里面进行删除
                    List<BmobObject> subObjects = new ArrayList<>();
                    subObjects.addAll(cardPicBeans);
                    List<BmobObject> groupObjets = new ArrayList<>();
                    groupObjets.add(picGroup);
                    SuperActionHelper.deleteByGroupId(subObjects, groupObjets);
                } else {
                    L.error(TAG, "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    private void updateContent(final CardPicGroup cardPicGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View editContentView = layoutInflater.inflate(R.layout.super_user_edit_dialog, null);
        final Dialog editDialog = DialogHelper.getNoTitleDialog(getActivity(), editContentView);
        editDialog.show();
        final EditTextClearDroidView droidView = (EditTextClearDroidView) editContentView.findViewById(R.id.edit_view);
        droidView.getEditText().setText(cardPicGroup.imgDesc);
        editContentView.findViewById(R.id.modify_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(droidView.getEditText().getText())) {
                    SFToast.showToast(getString(R.string.input_word));
                    return;
                }
                editDialog.dismiss();
                String content = droidView.getEditText().getText().toString();
                cardPicGroup.imgDesc = content;
                cardPicGroup.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            SFToast.showToast("更新成功");
                        } else {
                            SFToast.showToast("更新失败");
                        }
                    }
                });
            }
        });
    }


    private void updateLatestComment(final String picGroupId, final String commentContent, String userId) {
        final CardPicGroup picGroup = new CardPicGroup();
        picGroup.setObjectId(picGroupId);
        picGroup.latestCommentContent = commentContent;
        if (!TextUtils.isEmpty(userId)) {
            NYBmobUser user = new NYBmobUser();
            user.setObjectId(userId);
            picGroup.latestUserId = new BmobPointer(user);
        }
        picGroup.increment("commentNum", 1);
        mCommentView.setVisibility(View.GONE);

        Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                L.info(TAG, "subscribe call thread: " + Thread.currentThread().getName());
                String result = picGroup.updateSync();
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        }).flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                L.info(TAG, "flatMap call thread: " + Thread.currentThread().getName());
                return postComment(picGroupId, commentContent, null);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        L.info(TAG, "onCompleted thread: " + Thread.currentThread().getName());
                        SFToast.showToast("发表成功");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        L.info(TAG, "onError thread: " + Thread.currentThread().getName());
                        SFToast.showToast("发表失败");
                    }

                    @Override
                    public void onNext(Object o) {
                        L.info(TAG, "onNext thread: " + Thread.currentThread().getName());
                    }
                });
    }

    private Observable<String> postComment(String picGroupId, String commentContent, String userId) {
        final PicComment picComment = new PicComment();
        CardPicGroup picGroup = new CardPicGroup();
        picGroup.setObjectId(picGroupId);
        picComment.topicId = new BmobPointer(picGroup);
        picComment.content = commentContent;
        if (TextUtils.isEmpty(userId)) {
            picComment.userId = null;
        } else {
            BmobUser user = new BmobUser();
            user.setObjectId(userId);
            picComment.userId = new BmobPointer(user);
        }

        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                L.info(TAG,"postComment call thread: "+Thread.currentThread().getName());
                String result = picComment.saveSync();
                subscriber.onNext(result);
                if(TextUtils.isEmpty(result)){
                    subscriber.onError(null);
                }else {
                    subscriber.onCompleted();
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
        query.order("-createdAt");
        String updateContent = refresh ? getRefreshTime() : getLoadMoreTime();
        Date updateData = DateFormatHelp.StrDateToCalendar(updateContent, DateFormatHelp._YYYYMMDDHHMMSS);
        if (refresh) { //refresh
            query.setSkip(0);
            query.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(updateData));
        } else {//load more
//            query.setSkip()
            query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(updateData));
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
        ImageView picLayout = help.getView(R.id.big_pic_iv);
        ViewGroup.LayoutParams picParams = picLayout.getLayoutParams();
        picParams.width = mPicWidth;
        picParams.height = mPicHeight;
        picLayout.setLayoutParams(picParams);
        ViewBind.displayImage(cardPicGroup.imgUrl, (ImageView) help.getView(R.id.big_pic_iv), DisplayOptionConfig.getDefaultDisplayOption());
        help.setText(R.id.pic_desc_tv, cardPicGroup.imgDesc + "");
        help.setText(R.id.pic_time_tv, NYDateFormatHelper.formatTime(cardPicGroup.getCreatedAt()));
        help.setOnClickListener(R.id.write_comment_rl, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentView.setVisibility(View.VISIBLE);
                mCommentEt.requestFocus();
                mCommentView.post(new Runnable() {
                    @Override
                    public void run() {
                        SystemUIHelp.showSoftKeyboard(getActivity(), mCommentEt);
                    }
                });
                curPicGroupId = cardPicGroup.getObjectId();
            }
        });
        help.setOnClickListener(R.id.comment_rl, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(PIC_GROUP_ID, cardPicGroup.getObjectId());
                Intent intent = FragmentHelper.getStartIntent(getActivity(), BigPicCommentListFragment.class,
                        bundle, null, NYFragmentContainerActivity.class);
                intent.putExtra(NYFragmentContainerActivity.CONTAINER_TITLE, "评论");
                startActivity(intent);
            }
        });

        help.setOnClickListener(R.id.big_pic_iv, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NYPhotoShowActivity.class);
                intent.putExtra(ActivityPhotoPreview.IMAGE_GROUP_ID, cardPicGroup.getObjectId());
                intent.putExtra(ActivityPhotoPreview.IMAGE_GROUP_ID, cardPicGroup.getObjectId());
                intent.putExtra(ActivityPhotoPreview.IMAGE_GROUP_ID, cardPicGroup.getObjectId());
                startActivity(intent);
            }
        });

        //comment
        if(TextUtils.isEmpty(cardPicGroup.latestCommentContent)){
            help.setVisible(R.id.pic_comment_view,View.GONE);
        }else {
            help.setVisible(R.id.pic_comment_view,View.VISIBLE);
            help.setText(R.id.pic_comment_name,"随机");
            help.setText(R.id.pic_comment_content,cardPicGroup.latestCommentContent);
        }
        help.setText(R.id.comment_tv,String.valueOf(cardPicGroup.commentNum));
        if (BuildConfig.SUPER_USER) {
            help.setOnLongClickListener(R.id.big_pic_iv, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    doSuperOperation(cardPicGroup);
                    return true;
                }
            });
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}
