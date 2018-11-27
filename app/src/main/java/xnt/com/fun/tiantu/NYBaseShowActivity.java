package xnt.com.fun.tiantu;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.sf.loglib.L;
import com.sf.utils.baseutil.SFToast;
import com.sf.utils.baseutil.SystemUIHelp;
import com.sf.utils.baseutil.UnitHelp;

import xnt.com.fun.R;
import xnt.com.fun.Utils;
import xnt.com.fun.ViewPagerTransformManger;
import xnt.com.fun.login.ThirdLoginActivity;
import xnt.com.fun.view.ExViewPager;

public class NYBaseShowActivity extends BaseActivity {

    protected ExViewPager mViewPager;

    private View mCommentView;
    private TextView mSendTv;
    private EditText mCommentEt;
    private TextView mPraiseTv;

    private SVGAParser mSvgaParser;
    private SVGAImageView mSvgaView;
    private GestureDetector mDetector;
    private ViewGroup mBottomBarGroup;
    protected TextView mBottomContentTv;


    private CommentViewHolder mViewHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    protected void initView(){
        this.mViewPager = (ExViewPager) findViewById(R.id.viewpager);
        try {
            this.mViewPager.setPageTransformer(true, ViewPagerTransformManger.getTransformRandom().clazz.newInstance());
        } catch (InstantiationException e) {
            L.error(TAG, e.getMessage());
        } catch (IllegalAccessException e) {
            L.error(TAG, e.getMessage());
        }
        this.mViewPager.setPageMargin(UnitHelp.dip2px(this, 5.0F));
        this.mViewPager.setPageMarginDrawable(R.drawable.black_shape);
        this.mViewPager.setAdapter(createAdapter());
        this.mSvgaView = findViewById(R.id.praise_animation_svga);

        //底部actionBar
        mBottomBarGroup = findViewById(R.id.bottom_show_bar_rl);
        mBottomContentTv = findViewById(R.id.pic_desc);
        mViewHolder = new CommentViewHolder(mBottomBarGroup);
        mViewHolder.writeCommentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditCommentView(getCurObjectId());
            }
        });
        mViewHolder.showCommentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentListDialog(getCurObjectId());
            }
        });
        mViewPager.setOnTouchViewPagerListener(new ExViewPager.ExViewPagerTouchListener() {
            @Override
            public void onTouch(MotionEvent event) {
                if(mDetector == null){
                    mDetector = new GestureDetector(NYBaseShowActivity.this,new GestureDetector.SimpleOnGestureListener(){
                        @Override
                        public boolean onDown(MotionEvent e) {
                            return true;
                        }

                        @Override
                        public boolean onSingleTapConfirmed(MotionEvent e) {
                            toggleBottomBar(mBottomBarGroup);
                            return true;
                        }

                        @Override
                        public boolean onDoubleTap(MotionEvent e) {
                            doSvgaAnimation("");
                            return true;
                        }
                    });
                }
                mDetector.onTouchEvent(event);
            }
        });

//
//        GestureView gestureView = (GestureView)findViewById(R.id.ny_beauty_gesture);
//        gestureView.setOnGestureListener();


        //评论
        mCommentView = findViewById(R.id.comment_view);
        mSendTv = (TextView) findViewById(R.id.comment_send_tv);
        mCommentEt = (EditText) findViewById(R.id.comment_et);
        mCommentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentView.setVisibility(View.GONE);
                mBottomBarGroup.setVisibility(View.VISIBLE);
                SystemUIHelp.hideSoftKeyboard(NYBaseShowActivity.this, mCommentEt);
            }
        });
        mPraiseTv = findViewById(R.id.pic_praise_tv);
        mPraiseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSvgaAnimation("");
                doPraise(getCurObjectId());
            }
        });
        mSendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mCommentEt.getText())) {
                    SFToast.showToast("请输入内容");
                    return;
                }
                SystemUIHelp.hideSoftKeyboard(NYBaseShowActivity.this, mCommentEt);
                String commentContent = mCommentEt.getText().toString();
                if(Utils.isLogin()) {
                    doPostComment(getCurObjectId(), commentContent);
                }else {
                    ThirdLoginActivity.toLogin(NYBaseShowActivity.this);
                }
            }
        });
    }

//    public void increasePraise(int increment){
//        mPraiseCount+=increment;
//    }
//
//    public void increaseComment(int increment){
//        mCommentCount+=increment;
//    }
//
//    public int getPraiseCount(){
//        return mPraiseCount;
//    }
//
//    public int getCommentCount(){
//        return mCommentCount;
//    }
    public void updateCommentNum(String text){
        mViewHolder.showCommentNumTv.setText(text);
    }

    public void updatePraiseNum(String text){
        mViewHolder.praiseView.setText(text);
    }

    public void updatePraiseState(boolean selected){
        mViewHolder.praiseView.setSelected(selected);
    }

    public void updateDescContent(String text){
        mViewHolder.descView.setText(text);
    }
    protected void doPostComment(String curObjectId,String commentContent){

    }

    protected void doPraise(String curObjectId){

    }



    protected String getCurObjectId(){
        return "";
    }

    protected PagerAdapter createAdapter(){
        return null;
    }
    private void doSvgaAnimation(final String channel) {
        if (mSvgaParser == null) {
            mSvgaParser = new SVGAParser(this);
        }
        mSvgaParser.parse("praise.svga", new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NonNull SVGAVideoEntity videoItem) {
                final SVGADrawable drawable = new SVGADrawable(videoItem);
//                drawable.setBounds(0,0,DpAndPxUtils.dip2px(150),DpAndPxUtils.dip2px(150));
                mSvgaView.setImageDrawable(drawable);
                mSvgaView.setLoops(1);
                mSvgaView.setCallback(new SVGACallback() {
                    @Override
                    public void onPause() {
                        L.info(TAG, "svga onPause");
                    }

                    @Override
                    public void onFinished() {
                        L.info(TAG, "svga onFinish");
                    }

                    @Override
                    public void onRepeat() {
                        L.info(TAG, "svga onRepeat");
                    }

                    @Override
                    public void onStep(int frame, double percentage) {
                        L.info(TAG, "svga onStep frame: " + frame + " percentage: " + percentage);
                    }
                });
                mSvgaView.startAnimation();

//                updatePraise(channel);

            }

            @Override
            public void onError() {
                SFToast.showToast("播放svga失败");
            }
        });
    }


    protected void showCommentListDialog(String objectId){

    }
    private void showEditCommentView(String objectId) {
        mCommentView.setVisibility(View.VISIBLE);
        mBottomBarGroup.setVisibility(View.GONE);
        mCommentEt.requestFocus();
        mCommentView.post(new Runnable() {
            @Override
            public void run() {
                SystemUIHelp.showSoftKeyboard(NYBaseShowActivity.this, mCommentEt);
            }
        });
    }

    protected void showDesc(boolean show){
        if(show){
            mViewHolder.descView.setVisibility(View.VISIBLE);
        }else {
            mViewHolder.descView.setVisibility(View.GONE);
        }
    }

    protected void showBottomBar(boolean show){
        if(show) {
            mBottomBarGroup.setVisibility(View.VISIBLE);
        }else {
            mBottomBarGroup.setVisibility(View.GONE);
        }
    }

    protected void handleCommentResult(Throwable e) {
        if (e == null) {
            SFToast.showToast("发表成功");
            mBottomBarGroup.setVisibility(View.VISIBLE);
            mCommentView.setVisibility(View.GONE);
            mCommentEt.setText("");
        } else {
            SFToast.showToast("发表失败");
        }
    }

    private void toggleBottomBar(final View bottomBar) {
        if (bottomBar.getVisibility() != View.GONE) {
            YoYo.with(Techniques.SlideOutDown).withListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    bottomBar.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    bottomBar.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).duration(500).playOn(bottomBar);
        } else {
            bottomBar.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.SlideInUp).duration(500).playOn(bottomBar);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSvgaView.pauseAnimation();
    }

    public void onDestroy() {
        mSvgaView.stopAnimation();
        super.onDestroy();

    }
}
