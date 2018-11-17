package xnt.com.fun.tiantu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.basesmartframe.dialoglib.DialogFactory;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.L;
import com.sf.utils.baseutil.NetWorkManagerUtil;
import com.sf.utils.baseutil.SFToast;
import com.sflib.CustomView.baseview.EditTextClearDroidView;
import com.sflib.umenglib.share.DefaultShareAdapter;
import com.sflib.umenglib.share.DefaultUMengShareAction;
import com.sflib.umenglib.share.ShareContent;
import com.sflib.umenglib.share.UmengBuildHelper;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import xnt.com.fun.BuildConfig;
import xnt.com.fun.DialogHelper;
import xnt.com.fun.FragmentHelper;
import xnt.com.fun.NYFragmentContainerActivity;
import xnt.com.fun.R;
import xnt.com.fun.bean.CardPicBean;
import xnt.com.fun.bean.CardPicGroup;
import xnt.com.fun.bean.NYBmobUser;
import xnt.com.fun.comment.BigPicCommentListFragment;
import xnt.com.fun.comment.PicComment;
import xnt.com.fun.share.NYShareView;

import static xnt.com.fun.NYFragmentBigPic.PIC_GROUP_ID;

/**
 * Created by mac on 2018/6/2.
 */

public class NYPhotoShowActivity extends NYBaseShowActivity {


    private Dialog mShareDialog;
    protected PhotoPagerAdapter mAdapter;
    private List<CardPicBean> mCardPicBeans = new ArrayList<>();
    public static final String CARD_PIC_BEANS = "card_pic_beans";

    private String curPicGroupId;


    public static final String COMMENT_COUNT= "comment_count";
    public static final String PRAISE_COUNT= "praise_count";
    private int mCommentCount = 0;
    private int mPraiseCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ny_pic_show_activity);
        initViews();
    }


    public void initViews() {
        Intent intent = this.getIntent();
        String imageGroupId = null;
        if (intent != null) {
            imageGroupId = intent.getStringExtra(ActivityPhotoPreview.IMAGE_GROUP_ID);
            curPicGroupId = imageGroupId;
        }
        super.initView();
        if (NetWorkManagerUtil.isNetworkAvailable()) {
            if (!TextUtils.isEmpty(imageGroupId)) {
                getAllPicByGroupId(imageGroupId);
            }
        } else {
            SFToast.showToast(R.string.net_unavailable);
        }
    }

    @Override
    protected String getCurObjectId() {
        return curPicGroupId;
    }


    @Override
    protected void showCommentListDialog(String objectId) {
        Bundle bundle = new Bundle();
        bundle.putString(PIC_GROUP_ID, objectId);
        Intent intent = FragmentHelper.getStartIntent(NYPhotoShowActivity.this, BigPicCommentListFragment.class,
                bundle, null, NYFragmentContainerActivity.class);
        intent.putExtra(NYFragmentContainerActivity.CONTAINER_TITLE, "评论");
        startActivity(intent);
    }

    @Override
    protected PagerAdapter createAdapter() {
        mAdapter = new PhotoPagerAdapter(this);
        return mAdapter;
    }

    @Override
    protected void doPostComment(String curObjectId, String commentContent, String userId) {
        updateLatestComment(curObjectId, commentContent, null);
    }

    @Override
    protected void doPraise(String curObjectId) {
        CardPicBean picBean = mCardPicBeans.get(mViewPager.getCurrentItem());
        if(picBean.isPraised){
            SFToast.showToast(R.string.already_praise);
            return;
        }
        doIncreasePraiseNum(curPicGroupId);
    }

    private void doIncreasePraiseNum(String picGroupId){
        final CardPicGroup picGroup = new CardPicGroup();
        picGroup.setObjectId(picGroupId);
        picGroup.increment("praiseNum", 1);
        picGroup.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                   CardPicBean picBean = mCardPicBeans.get(mViewPager.getCurrentItem());
                   picBean.isPraised = true;
//                   increasePraise(1);
                    mPraiseCount++;
                   updatePraiseNum(String.valueOf(mPraiseCount));
                }
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

        Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                com.sf.loglib.L.info(TAG, "subscribe call thread: " + Thread.currentThread().getName());
                String result = picGroup.updateSync();
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        }).flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                com.sf.loglib.L.info(TAG, "flatMap call thread: " + Thread.currentThread().getName());
                return postComment(picGroupId, commentContent, null);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        handleCommentResult(null);
                        mCommentCount++;
                        updateCommentNum(String.valueOf(mCommentCount));
                        com.sf.loglib.L.info(TAG, "onCompleted thread: " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        handleCommentResult(throwable);
                        com.sf.loglib.L.info(TAG, "onError thread: " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onNext(Object o) {
                        com.sf.loglib.L.info(TAG, "onNext thread: " + Thread.currentThread().getName());
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
                com.sf.loglib.L.info(TAG,"postComment call thread: "+Thread.currentThread().getName());
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


    private void getAllPicByGroupId(String groupId) {
        BmobQuery<CardPicBean> query = new BmobQuery<>();
        CardPicGroup picGroup = new CardPicGroup();
        picGroup.setObjectId(groupId);
        query.addWhereEqualTo("PicGroupId", new BmobPointer(picGroup));
        //执行查询方法
        query.findObjects(new FindListener<CardPicBean>() {
            @Override
            public void done(List<CardPicBean> cardPicBeans, BmobException e) {
                if (e == null) {
                    mCardPicBeans.addAll(cardPicBeans);
                    mAdapter.notifyDataSetChanged();
                } else {
                    L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private void showUpdateDialog(final CardPicBean cardPicBean) {
        LayoutInflater layoutInflater = LayoutInflater.from(NYPhotoShowActivity.this);
        View editContentView = layoutInflater.inflate(R.layout.super_user_edit_dialog,null);
        final Dialog editDialog = DialogHelper.getNoTitleDialog(NYPhotoShowActivity.this,editContentView);
        editDialog.show();
        final EditTextClearDroidView droidView = (EditTextClearDroidView) editContentView.findViewById(R.id.edit_view);
        droidView.getEditText().setText(cardPicBean.imgDesc);
        editContentView.findViewById(R.id.modify_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(droidView.getEditText().getText())){
                    SFToast.showToast(getString(R.string.input_word));
                    return;
                }
                editDialog.dismiss();
                final String content = droidView.getEditText().getText().toString();
                cardPicBean.imgDesc = content;
                cardPicBean.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            SFToast.showToast("更新成功");
                            mBottomContentTv.setText(content);
                        }else{
                            SFToast.showToast("更新失败");
                        }
                    }
                });
            }
        });
    }

    private class PhotoPagerAdapter extends PagerAdapter {
        private LayoutInflater mInflater;

        public PhotoPagerAdapter(Context context) {
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return mCardPicBeans.size();
        }

        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == (View) object;
        }

        public Object instantiateItem(View container, final int position) {
            View view = this.mInflater.inflate(R.layout.ny_pic_show_item, (ViewGroup) null);

            ImageView imageView = (ImageView) view.findViewById(R.id.photo_view);

            if(BuildConfig.SUPER_USER){
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showUpdateDialog(mCardPicBeans.get(position));
                        return true;
                    }
                });
            }
            ImageView shareIv = (ImageView) view.findViewById(R.id.view_share_iv);
            shareIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mShareDialog == null) {
                        View shareDialogView = LayoutInflater.from(NYPhotoShowActivity.this).inflate(R.layout.ny_share_dialog, null);
                        NYShareView shareView = (NYShareView) shareDialogView.findViewById(R.id.share_view);

                        String desc = mCardPicBeans.get(position).imgDesc;
                        shareView.setShareContent(getShareAction(desc));
                        shareView.setShareAdapter(new DefaultShareAdapter());
                        shareDialogView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mShareDialog.dismiss();
                            }
                        });
                        mShareDialog = DialogFactory.getNoFloatingDimDialog(NYPhotoShowActivity.this, shareDialogView);
                    }
                    if (!mShareDialog.isShowing()) {
                        mShareDialog.show();
                    }
                }
            });
            final CardPicBean bean = mCardPicBeans.get(position);

            ImageLoader.getInstance().displayImage(bean.imageUrl, imageView);
            ((ViewPager) container).addView(view);
            return view;
        }

    }

    private ShareAction getShareAction(String content){
        String title = "M拍";
        String url = "https://xieningtao.github.io/";
        String imgUrl="https://raw.githubusercontent.com/xieningtao/documents/master/icon/app_icon.png";
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.app_icon);
        ShareContent shareContent = new ShareContent.ShareContentBuilder()
                .setTitle(title)
                .setContent(content)
                .setUrl(url)
                .setImage_url(imgUrl)
                .setBitmap(bitmap)
                .build();
        UMImage thumb = new UMImage(NYPhotoShowActivity.this,imgUrl);
        UMWeb linker = UmengBuildHelper.getUMWeb(url,title,content,thumb);
        return DefaultUMengShareAction.getLinkAction(NYPhotoShowActivity.this,linker);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
    }
}
