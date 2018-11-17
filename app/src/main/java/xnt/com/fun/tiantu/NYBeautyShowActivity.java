package xnt.com.fun.tiantu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sfchat.media.MediaPlayManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sf.loglib.L;
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
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import xnt.com.fun.BeautyModel;
import xnt.com.fun.BuildConfig;
import xnt.com.fun.DialogHelper;
import xnt.com.fun.FragmentUtils;
import xnt.com.fun.R;
import xnt.com.fun.bean.Beauty;
import xnt.com.fun.bean.BeautyComment;
import xnt.com.fun.bean.Music;
import xnt.com.fun.comment.BeautyCommentListFragment;
import xnt.com.fun.share.NYShareView;

public class NYBeautyShowActivity extends NYBaseShowActivity implements BeautyModel.OnDataChangeListener {


    //    private List<CardPicBean> mCardPicBeans = new ArrayList<>();
    public static final String BEAUTY_TOTAL_SIZE = "beauty_total_size";
    public static final String BEAUTY_CUR_POS = "beauty_cur_pos";
    public static final String NO_POSITION = "NO_POSITION";
    public static final String UNCHANGE = "UNCHANGE";
    private BeautyPagerAdapter mAdapter;
    private Dialog mShareDialog;
    private ImageView mMusicIv;
    private HashMap<Integer, TaskState> mTaskState = new HashMap<>();
    private List<Music> mMusics = new ArrayList<>();
    private int mCurMusicIndex = 0;
    private MusicAnimationTask mTask;
    private int mTotalSize = 100;
    private int mCurPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ny_beauty_show_activity);
        initViews();
        BeautyModel.getInstance().registerListener(this);
    }

    @Override
    protected PagerAdapter createAdapter() {
        mAdapter = new BeautyPagerAdapter(this);
        return mAdapter;
    }

    @Override
    protected String getCurObjectId() {
        Beauty beauty = mAdapter.getBeauty(getCurrentItem());
        return beauty.getObjectId();
    }

    @Override
    protected void showCommentListDialog(String objectId) {
            Bundle bundle = new Bundle();
            bundle.putString(BeautyCommentListFragment.BEAUTY_GROUP_ID,objectId);
            FragmentUtils.showViewWithSlideBottom(NYBeautyShowActivity.this,
                    R.id.dialog_container,
                    BeautyCommentListFragment.class,
                    "beautyComment",bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    protected void initViews() {
        Intent intent = this.getIntent();
        if (intent != null) {
            mTotalSize = intent.getIntExtra(BEAUTY_TOTAL_SIZE, 0);
            mCurPos = intent.getIntExtra(BEAUTY_CUR_POS, 0);
        }
        super.initView();
        mViewPager.setCurrentItem(mCurPos);
        mMusicIv = (ImageView) findViewById(R.id.music_iv);
        //animation
        mTask = new MusicAnimationTask();
        mTask.execute();
        //play music

        if (NetWorkManagerUtil.isNetworkAvailable()) {
//            String musicUrl="http://m10.music.126.net/20181103114753/972537392dfbd4e09ac276b74429755e/ymusic/8750/cd5e/9bb5/b241fd490c144103ccd9daff7b8fef98.mp3";
            String musicUrl = "http://m10.music.126.net/20181103122118/82e99bd2d8b577011086196c9a80338f/ymusic/b692/683f/66d7/effb1c0cd4eaa906378deb16a7f89a26.mp3";
            MediaPlayManager.getInstance().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                }
            });
            MediaPlayManager.getInstance().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //加载下一首歌
                    mCurMusicIndex++;
                    String url = getMusicUrl();
                    if (!TextUtils.isEmpty(url)) {
                        MediaPlayManager.getInstance().startPlay(url);
                    }
                }
            });
            MediaPlayManager.getInstance().setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    SFToast.showToast("音乐播放出错");
                    return true;
                }
            });
            MediaPlayManager.getInstance().createMediaPlay();
            getMusic("pure");
        }
    }

    @Override
    protected void doPraise(String curObjectId) {
        super.doPraise(curObjectId);
    }

    @Override
    protected void doPostComment(String curObjectId, String commentContent, String userId) {
        postComment(curObjectId,commentContent,userId);
    }

    private void postComment(String beautyId, String commentContent, String userId) {
        final BeautyComment picComment = new BeautyComment();
        Beauty beauty = new Beauty();
        beauty.setObjectId(beautyId);
        picComment.beautyId = new BmobPointer(beauty);
        picComment.content = commentContent;
        if (TextUtils.isEmpty(userId)) {
            picComment.userId = null;
        } else {
            BmobUser user = new BmobUser();
            user.setObjectId(userId);
            picComment.userId = new BmobPointer(user);
        }

        picComment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                handleCommentResult(e);
            }
        });
    }

    private void getMusic(String tag) {
        BmobQuery<Music> query = new BmobQuery<>();
        query.addWhereEqualTo("tag", tag);
        query.setLimit(10);
        //执行查询方法
        query.findObjects(new FindListener<Music>() {
            @Override
            public void done(List<Music> musics, BmobException e) {
                if (e == null) {
                    mMusics.addAll(musics);
                    if (NetWorkManagerUtil.isNetworkAvailable()) {
                        String musicUrl = getMusicUrl();
                        if (!TextUtils.isEmpty(musicUrl)) {
                            MediaPlayManager.getInstance().startPlay(musicUrl);
                        }
                    }
                } else {
                    L.info("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    @Nullable
    private String getMusicUrl() {
        if (mMusics.size() <= mCurMusicIndex || mCurMusicIndex < 0) {
            return "";
        }
        String musicUrl = mMusics.get(mCurMusicIndex).musicUrl;
        if (TextUtils.isEmpty(musicUrl)) {
            BmobFile musicFile = mMusics.get(mCurMusicIndex).musicFile;
            if (musicFile != null) {
                musicUrl = musicFile.getUrl();
            }
        }
        return musicUrl;
    }

    public int getCurrentItem() {
        int curr = mViewPager.getCurrentItem();
//        if(this.mAdapter != null && this.mAdapter.getCount() > 1) {
//            if(curr == 0) {
//                curr = mAdapter.getCount() - 1;
//            } else if(curr == mAdapter.getCount() - 1) {
//                curr = 0;
//            }
//        }
        return curr;
    }

    private void getBeautyPic(final int position) {
        mTaskState.put(position, TaskState.START);
        int indexId = getIndexId(position);
        BmobQuery<Beauty> query = new BmobQuery<>();
        query.addWhereEqualTo("indexId", indexId);
        //执行查询方法
        query.findObjects(new FindListener<Beauty>() {
            @Override
            public void done(List<Beauty> cardPicBeans, BmobException e) {
                if (e == null) {
                    mTaskState.put(position, TaskState.SUCCESS);
                    BeautyModel.getInstance().addBeauties(position, cardPicBeans);
                } else {
                    mTaskState.put(position, TaskState.FAIL);
                    mAdapter.notifyDataSetChanged();
                    L.info("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }

            }
        });
    }

    public void onDestroy() {
        MediaPlayManager.getInstance().destroyPlayer();
        BeautyModel.getInstance().unregisterListener(this);
        mTask.cancel(false);
        super.onDestroy();

    }




    private void showUpdateDialog(final Beauty beauty, final TextView bottomBarTv) {
        LayoutInflater layoutInflater = LayoutInflater.from(NYBeautyShowActivity.this);
        View editContentView = layoutInflater.inflate(R.layout.super_user_edit_dialog, null);
        final Dialog editDialog = DialogHelper.getNoTitleDialog(NYBeautyShowActivity.this, editContentView);
        editDialog.show();
        final EditTextClearDroidView droidView = (EditTextClearDroidView) editContentView.findViewById(R.id.edit_view);
        droidView.getEditText().setText(beauty.imgDesc);
        editContentView.findViewById(R.id.modify_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(droidView.getEditText().getText())) {
                    SFToast.showToast(getString(R.string.input_word));
                    return;
                }
                editDialog.dismiss();
                final String content = droidView.getEditText().getText().toString();
                beauty.imgDesc = content;
                beauty.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            SFToast.showToast("更新成功");
                            bottomBarTv.setText(content);
                        } else {
                            SFToast.showToast("更新失败");
                        }
                    }
                });
            }
        });
    }

    private int getIndexId(int position) {
        return mTotalSize - position;
    }

    @Override
    public void onDataChange() {
        mAdapter.notifyDataSetChanged();
    }

    private ShareAction getShareAction(String content) {
        String title = "M拍";
        String url = "https://xieningtao.github.io/";
        String imgUrl = "https://raw.githubusercontent.com/xieningtao/documents/master/icon/app_icon.png";
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);
        ShareContent shareContent = new ShareContent.ShareContentBuilder()
                .setTitle(title)
                .setContent(content)
                .setUrl(url)
                .setImage_url(imgUrl)
                .setBitmap(bitmap)
                .build();
        UMImage thumb = new UMImage(NYBeautyShowActivity.this, imgUrl);
        UMWeb linker = UmengBuildHelper.getUMWeb(url, title, content, thumb);
        return DefaultUMengShareAction.getLinkAction(NYBeautyShowActivity.this, linker);
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






    private class BeautyPagerAdapter extends PagerAdapter {
        private LayoutInflater mInflater;

        public BeautyPagerAdapter(Context context) {
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return mTotalSize;
        }

        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == (View) object;
        }

        public Object instantiateItem(View container, final int position) {
            final Beauty beauty = getBeauty(position);
            View view = this.mInflater.inflate(R.layout.ny_beauty_show_item, (ViewGroup) null);
            View progressView = view.findViewById(R.id.progress_bar);
            View noDataView = view.findViewById(R.id.no_data_view);
            if (beauty == null) {
                if (mTaskState.containsKey(position) && mTaskState.get(position) == TaskState.FAIL) {
                    view.setTag(UNCHANGE);
                    noDataView.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.GONE);
                } else {
                    view.setTag(NO_POSITION);
                    progressView.setVisibility(View.VISIBLE);
                    noDataView.setVisibility(View.GONE);
                }
            } else {
                view.setTag(UNCHANGE);
                progressView.setVisibility(View.GONE);
                noDataView.setVisibility(View.GONE);
                final View bottomBar = view.findViewById(R.id.bottom_show_bar_rl);
                final TextView bottomBarTv = (TextView) view.findViewById(R.id.pic_desc);
//                if (!TextUtils.isEmpty(beauty.imgDesc)) {
//                    bottomBar.setVisibility(View.VISIBLE);
//                    bottomBarTv.setText(beauty.imgDesc);
//                } else {
//                    bottomBar.setVisibility(View.GONE);
//                }

                ImageView imageView = (ImageView) view.findViewById(R.id.photo_view);
//                imageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });
                if (BuildConfig.SUPER_USER) {
                    imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            showUpdateDialog(BeautyModel.getInstance().getBeauty(position), bottomBarTv);
                            return true;
                        }
                    });
                }
                ImageView shareIv = (ImageView) view.findViewById(R.id.view_share_iv);
                final Beauty finalBeauty = beauty;
                shareIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mShareDialog == null) {
                            View shareDialogView = LayoutInflater.from(NYBeautyShowActivity.this).inflate(R.layout.ny_share_dialog, null);
                            NYShareView shareView = (NYShareView) shareDialogView.findViewById(R.id.share_view);
                            shareDialogView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mShareDialog.dismiss();
                                }
                            });
                            shareView.setShareContent(getShareAction(finalBeauty.imgDesc));
                            shareView.setShareAdapter(new DefaultShareAdapter());
                            shareDialogView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mShareDialog.dismiss();
                                }
                            });
                            mShareDialog = DialogHelper.getNoTitleDialog(NYBeautyShowActivity.this, shareDialogView);
                        }
                        if (!mShareDialog.isShowing()) {
                            mShareDialog.show();
                        }
                    }
                });
                ImageLoader.getInstance().displayImage(beauty.imgUrl, imageView);
            }
            ((ViewPager) container).addView(view);
            return view;
        }


        private void getBeautyCommentList(String beautyId, final ViewGroup commentContainer) {
            BmobQuery<BeautyComment> query = new BmobQuery<>();
            Beauty picGroup = new Beauty();
            picGroup.setObjectId(beautyId);
            query.addWhereEqualTo("beautyId", new BmobPointer(picGroup));
            query.setLimit(1);
            query.order("-updatedAt");
            //执行查询方法
            query.findObjects(new FindListener<BeautyComment>() {
                @Override
                public void done(List<BeautyComment> picComments, BmobException e) {
                    if (e == null && picComments != null && picComments.size() > 0) {
                        TextView commentContentTv = commentContainer.findViewById(R.id.pic_comment_content);
                        TextView commentNameTv = commentContainer.findViewById(R.id.pic_comment_name);
                        commentContentTv.setText(picComments.get(0).content);
                        commentNameTv.setText("随机");
                    } else {
                        L.info("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    }
                }
            });
        }


        @Override
        public int getItemPosition(Object object) {
            if (object instanceof View) {
                View view = (View) object;
                String tag = (String) view.getTag();
                if (NO_POSITION.equals(tag)) {
                    return POSITION_NONE;
                } else {
                    return POSITION_UNCHANGED;
                }
            }
            return super.getItemPosition(object);
        }

        @Nullable
        private Beauty getBeauty(int position) {
            Beauty beauty = null;
            if (BeautyModel.getInstance().getBeautySize() > position) {
                beauty = BeautyModel.getInstance().getBeauty(position);
            } else if (!mTaskState.containsKey(position)) {//没运行的
                if (NetWorkManagerUtil.isNetworkAvailable()) {
                    getBeautyPic(position);
                } else {
                    SFToast.showToast(R.string.net_unavailable);
                }
            }
            return beauty;
        }
    }

    class MusicAnimationTask extends AsyncTask<Void, Void, Animation> {

        @Override
        protected Animation doInBackground(Void... voids) {
            RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(NYBeautyShowActivity.this, R.anim.music_rotate);
            return rotateAnimation;

        }

        @Override
        protected void onPostExecute(Animation animation) {
            super.onPostExecute(animation);
            if (!NYBeautyShowActivity.this.isFinishing()) {
                mMusicIv.setVisibility(View.VISIBLE);
                mMusicIv.startAnimation(animation);
            }
        }
    }

}
