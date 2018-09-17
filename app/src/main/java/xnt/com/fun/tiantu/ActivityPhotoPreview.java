package xnt.com.fun.tiantu;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.basesmartframe.pickphoto.ImageBean;
import com.basesmartframe.pickphoto.PickPhotosPreviewFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.L;
import com.sflib.CustomView.viewgroup.BaseLivePopAdapter;
import com.sflib.CustomView.viewgroup.LivePopView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import xnt.com.fun.R;
import xnt.com.fun.bean.NYCommentBean;
import xnt.com.fun.bean.PicGroup;
import xnt.com.fun.bean.PicList;

/**
 * Created by NetEase on 2016/10/13 0013.
 */
public class ActivityPhotoPreview extends BaseActivity implements PickPhotosPreviewFragment.OnPicSelectedListener {
    public static final String IMAGE_BEAN_LIST = "image_bean_list";
    public static final String IMAGE_GROUP_ID = "ImgGroupId";
    private String imageUrl[] = {
            "http://g.hiphotos.baidu.com/image/w%3D310/sign=40484034b71c8701d6b6b4e7177e9e6e/21a4462309f79052f619b9ee08f3d7ca7acbd5d8.jpg",
            "http://a.hiphotos.baidu.com/image/w%3D310/sign=b0fccc9b8518367aad8979dc1e728b68/3c6d55fbb2fb43166d8f7bc823a4462308f7d3eb.jpg",
            "http://d.hiphotos.baidu.com/image/w%3D310/sign=af0348abeff81a4c2632eac8e72b6029/caef76094b36acaf8ded6c2378d98d1000e99ce4.jpg"
    };
    private String content[] = {
            "aajfsdkjfksjk",
            "bbjflksjfks",
            "ccjskfjsdklfj"
    };
    private LivePopView mLivePopView;
    private String mImgGroupId;
    private int mNumber = -1;
    private int mCurPageIndex = 0;

    private List<NYCommentBean> commentBeenList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_photo_preview);
        Intent intent = getIntent();
        if (intent != null) {
            int position = intent.getIntExtra(PickPhotosPreviewFragment.INDEX, 0);
            mImgGroupId = intent.getStringExtra(IMAGE_GROUP_ID);
        }
        mLivePopView = (LivePopView) findViewById(R.id.live_popview);
        mLivePopView.setAdapter(new ImageCommentAdapter());


        initView();
        getAllPicByGroupId(mImgGroupId);
//        getComment(curImageMd5Value);

    }

    private void getAllPicByGroupId(String groupId) {
        BmobQuery<PicList> query = new BmobQuery<PicList>();
        PicGroup picGroup = new PicGroup();
        picGroup.setObjectId(groupId);
        query.addWhereEqualTo("PicGroupId", new BmobPointer(picGroup));
        //执行查询方法
        query.findObjects(new FindListener<PicList>() {
            @Override
            public void done(List<PicList> picLists, BmobException e) {
                if (e == null) {
                    PickPhotosPreviewFragment fragment = new PickPhotosPreviewFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(PickPhotosPreviewFragment.CAN_CHOOSE_IMAGE, false);
                    fragment.setArguments(bundle);
                    fragment.setOnPicSelectedListener(ActivityPhotoPreview.this);
                    PickPhotosPreviewFragment.setImageListData(transformPic(picLists));
                    getFragmentManager().beginTransaction().replace(R.id.photo_preview_fl, fragment).commitAllowingStateLoss();
                } else {
                    L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    private ArrayList<ImageBean> transformPic(List<PicList> picLists) {
        if (picLists == null) {
            return new ArrayList<>();
        }
        ArrayList<ImageBean> imageBeans = new ArrayList<>();
        for (int i = 0; i < picLists.size(); i++) {
            ImageBean bean = new ImageBean();
            bean.setPath(picLists.get(i).getImageUrl());
            imageBeans.add(bean);
        }
        return imageBeans;
    }

    private void sendComment(String imageUrlMd5, String comment) {

    }

    private void getComment(String imageUrlMd5) {

    }

    private void initView() {
        View sendView = findViewById(R.id.comment_send_tv);
        final EditText commentEt = (EditText) findViewById(R.id.comment_et);
//        sendView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              if(!TextUtils.isEmpty(commentEt.getText())) {
//                  String commentContent=commentEt.getText().toString();
//                  sendComment(curImageMd5Value,commentContent);
//              }
//            }
//        });
    }

    @Override
    public void onPicSelected(int index) {
        if (index != mCurPageIndex) {
            mCurPageIndex = index;
//            curImageMd5Value= mImgGroupId.get(index);
//            getComment(curImageMd5Value);

        }
    }

    class ImageCommentAdapter extends BaseLivePopAdapter {

        @Override
        public View getView(View rootView, int position) {
            if (rootView == null) {
                rootView = LayoutInflater.from(ActivityPhotoPreview.this).inflate(R.layout.item_pop_view, null);
            }
            NYCommentBean commentBean = commentBeenList.get(position);
            ImageView mPhotoIv = (ImageView) rootView.findViewById(R.id.photo_iv);
            ImageLoader.getInstance().displayImage(commentBean.getPhotoUrl(), mPhotoIv);
            TextView contentTv = (TextView) rootView.findViewById(R.id.comment_tv);
            contentTv.setText(commentBean.getComment());
            return rootView;
        }

        @Override
        public int getCount() {
            return commentBeenList.size();
        }
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            showSystemUI();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
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
