package xnt.com.fun.tiantu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.basesmartframe.dialoglib.DialogFactory;
import com.basesmartframe.pickphoto.XTranslateTransform;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.L;
import com.sf.utils.baseutil.NetWorkManagerUtil;
import com.sf.utils.baseutil.SFToast;
import com.sf.utils.baseutil.UnitHelp;
import com.sflib.CustomView.baseview.EditTextClearDroidView;
import com.sflib.umenglib.share.DefaultShareAdapter;
import com.sflib.umenglib.share.DefaultUMengShareAction;
import com.sflib.umenglib.share.ShareContent;
import com.sflib.umenglib.share.UmengBuildHelper;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import xnt.com.fun.BeautyModel;
import xnt.com.fun.BuildConfig;
import xnt.com.fun.DialogHelper;
import xnt.com.fun.R;
import xnt.com.fun.bean.Beauty;
import xnt.com.fun.share.NYShareView;

public class NYBeautyShowActivity extends BaseActivity implements BeautyModel.OnDataChangeListener {


    //    private List<CardPicBean> mCardPicBeans = new ArrayList<>();
    public static final String BEAUTY_TOTAL_SIZE = "beauty_total_size";
    public static final String BEAUTY_CUR_POS = "beauty_cur_pos";
    public static final String NO_POSITION = "NO_POSITION";
    public static final String UNCHANGE = "UNCHANGE";
    protected ViewPager mViewPager;
    protected NYBeautyShowActivity.ViewPagerAdapter mAdapter;
    private Dialog mShareDialog;
    private int mTotalSize = 100;
    private int mCurPos = 0;
    private HashMap<Integer, Boolean> mTaskState = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ny_pic_show_activity);
        initViews();
        BeautyModel.getInstance().registerListener(this);
    }


    public void initViews() {
        Intent intent = this.getIntent();
        if (intent != null) {
            mTotalSize = intent.getIntExtra(BEAUTY_TOTAL_SIZE,0);
            mCurPos = intent.getIntExtra(BEAUTY_CUR_POS,0);
        }
        this.mViewPager = (ViewPager) findViewById(R.id.viewpager);
        this.mViewPager.setPageTransformer(true, new XTranslateTransform());
        this.mViewPager.setPageMargin(UnitHelp.dip2px(this, 5.0F));
        this.mViewPager.setPageMarginDrawable(R.drawable.black_shape);
        this.mAdapter = new NYBeautyShowActivity.ViewPagerAdapter(this);
        this.mViewPager.setAdapter(this.mAdapter);
        this.mViewPager.setCurrentItem(mCurPos);

    }


    private void getBeautyPic(final int position) {
        mTaskState.put(position, Boolean.TRUE);
        int indexId = getIndexId(position);
        BmobQuery<Beauty> query = new BmobQuery<>();
        query.addWhereEqualTo("indexId", indexId);
        //执行查询方法
        query.findObjects(new FindListener<Beauty>() {
            @Override
            public void done(List<Beauty> cardPicBeans, BmobException e) {
                if (e == null) {
                    BeautyModel.getInstance().addBeauties(position, cardPicBeans);
                } else {
                    L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
                mTaskState.put(position, Boolean.FALSE);
            }
        });
    }

    public void onDestroy() {
        BeautyModel.getInstance().unregisterListener(this);
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
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private class ViewPagerAdapter extends PagerAdapter {
        private LayoutInflater mInflater;

        public ViewPagerAdapter(Context context) {
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
            Beauty beauty = getBeauty(position);
            View view = this.mInflater.inflate(R.layout.ny_beauty_show_item, (ViewGroup) null);
            View progressView = view.findViewById(R.id.progress_bar);
            if (beauty == null) {
                view.setTag(NO_POSITION);
                progressView.setVisibility(View.VISIBLE);
            } else {
                view.setTag(UNCHANGE);
                progressView.setVisibility(View.GONE);
                final View bottomBar = view.findViewById(R.id.bottom_show_bar_rl);
                final TextView bottomBarTv = (TextView) view.findViewById(R.id.bottom_bar_tv);
                if (!TextUtils.isEmpty(beauty.imgDesc)) {
                    bottomBar.setVisibility(View.VISIBLE);
                    bottomBarTv.setText(beauty.imgDesc);
                } else {
                    bottomBar.setVisibility(View.GONE);
                }
                ImageView imageView = (ImageView) view.findViewById(R.id.photo_view);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bottomBar.getVisibility() != View.GONE) {
                            bottomBar.setVisibility(View.GONE);
                        } else {
                            bottomBar.setVisibility(View.VISIBLE);
                        }
                    }
                });
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

                            shareView.setShareContent(getShareAction(finalBeauty.imgDesc));
                            shareView.setShareAdapter(new DefaultShareAdapter());
                            shareDialogView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mShareDialog.dismiss();
                                }
                            });
                            mShareDialog = DialogFactory.getNoFloatingDimDialog(NYBeautyShowActivity.this, shareDialogView);
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

        @Override
        public int getItemPosition(Object object) {
            if(object instanceof View){
                View view = (View) object;
                String tag = (String) view.getTag();
                if(NO_POSITION.equals(tag)){
                    return POSITION_NONE;
                }else {
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
            } else if (!mTaskState.containsKey(position) || //没有运行过这个任务
                    mTaskState.get(position) == Boolean.FALSE) {//已经运行完成
                if (NetWorkManagerUtil.isNetworkAvailable()) {
                    getBeautyPic(position);
                } else {
                    SFToast.showToast(R.string.net_unavailable);
                }
            }
            return beauty;
        }
    }
}
