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
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.basesmartframe.dialoglib.DialogFactory;
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

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import xnt.com.fun.BuildConfig;
import xnt.com.fun.DialogHelper;
import xnt.com.fun.R;
import xnt.com.fun.ViewPagerTransformManger;
import xnt.com.fun.bean.CardPicBean;
import xnt.com.fun.bean.CardPicGroup;
import xnt.com.fun.share.NYShareView;

/**
 * Created by mac on 2018/6/2.
 */

public class NYPhotoShowActivity extends BaseActivity {


    protected ViewPager mViewPager;
    private Dialog mShareDialog;
    protected NYPhotoShowActivity.ViewPagerAdapter mAdapter;
    private List<CardPicBean> mCardPicBeans = new ArrayList<>();
    public static final String CARD_PIC_BEANS = "card_pic_beans";


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
        }
        this.mViewPager = (ViewPager) findViewById(R.id.viewpager);
        try {
            this.mViewPager.setPageTransformer(true, ViewPagerTransformManger.getTransformRandom().clazz.newInstance());
        } catch (InstantiationException e) {
            com.sf.loglib.L.error(TAG,e.getMessage());
        } catch (IllegalAccessException e) {
            com.sf.loglib.L.error(TAG,e.getMessage());
        }
//        this.mViewPager.setPageTransformer(true, new XTranslateTransform());
        this.mViewPager.setPageMargin(UnitHelp.dip2px(this, 5.0F));
        this.mViewPager.setPageMarginDrawable(R.drawable.black_shape);
        this.mAdapter = new NYPhotoShowActivity.ViewPagerAdapter(this);
        this.mViewPager.setAdapter(this.mAdapter);
        if (NetWorkManagerUtil.isNetworkAvailable()) {
            if (!TextUtils.isEmpty(imageGroupId)) {
                getAllPicByGroupId(imageGroupId);
            } else {
                //TODO
            }
        } else {
            //TODO
        }
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

    private void showUpdateDialog(final CardPicBean cardPicBean, final TextView bottomBarTv) {
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
                            bottomBarTv.setText(content);
                        }else{
                            SFToast.showToast("更新失败");
                        }
                    }
                });
            }
        });
    }

    private class ViewPagerAdapter extends PagerAdapter {
        private LayoutInflater mInflater;

        public ViewPagerAdapter(Context context) {
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
            final View bottomBar = view.findViewById(R.id.bottom_show_bar_rl);
            final TextView bottomBarTv = (TextView) view.findViewById(R.id.pic_desc);
            String desc = mCardPicBeans.get(position).imgDesc;
            if (!TextUtils.isEmpty(desc)) {
                bottomBar.setVisibility(View.VISIBLE);
                bottomBarTv.setText(desc);
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
            if(BuildConfig.SUPER_USER){
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showUpdateDialog(mCardPicBeans.get(position),bottomBarTv);
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
            CardPicBean bean = mCardPicBeans.get(position);
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
}
