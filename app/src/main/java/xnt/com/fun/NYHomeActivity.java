package xnt.com.fun;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseSFTabActivity;
import com.sf.loglib.L;

import cdc.sed.yff.nm.sp.SpotListener;
import cdc.sed.yff.nm.sp.SpotManager;

/**
 * Created by NetEase on 2016/10/9 0009.
 */
public class NYHomeActivity extends BaseSFTabActivity {


    public static void toHomeActivity(Activity activity){
        Intent intent = new Intent(activity,NYHomeActivity.class);
        activity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTabWidget().setDividerDrawable(null);
        getTabWidget().setBackgroundResource(R.drawable.ny_home_bottom_layer);
        setTabAdapter(new FragmentTabAdapter());
        SpotManager.getInstance(this).showSlideableSpot(this,
                new SpotListener() {
                    @Override
                    public void onShowSuccess() {
                        L.info(TAG,"onShowSuccess ");
                    }

                    @Override
                    public void onShowFailed(int i) {
                        L.info(TAG,"onShowFailed i: "+i);
                    }

                    @Override
                    public void onSpotClosed() {

                    }

                    @Override
                    public void onSpotClicked(boolean b) {

                    }
                });
    }

    @Override
    public void onTabChanged(String tabId) {

    }

    public class FragmentTabAdapter implements BaseSFTabActivity.BaseTabSpecAdapter {

        @Override
        public int getCount() {
            return 4;
        }

        //refactor it later
        @Override
        public TabHost.TabSpec getTabSpec(final TabHost tabHost, int index, LayoutInflater layoutInflater) {
            switch (index) {
                case 0:
                    View tabView0 = layoutInflater.inflate(R.layout.tab_item, null);
                    ImageView tabIv0 = (ImageView) tabView0.findViewById(R.id.tab_iv);
                    tabIv0.setImageResource(R.drawable.ny_home_selector);
                    TextView tabTv0 = (TextView) tabView0.findViewById(R.id.tab_tv);
                    tabTv0.setText(R.string.news);
                    TabHost.TabSpec tabSpec0 = tabHost.newTabSpec("index" + index).setIndicator(tabView0);
                    return tabSpec0;
                case 1:
                    View tabView1 = layoutInflater.inflate(R.layout.tab_item, null);
                    ImageView tabIv1 = (ImageView) tabView1.findViewById(R.id.tab_iv);
                    tabIv1.setImageResource(R.drawable.ny_beauty_selector);
                    TextView tabTv1 = (TextView) tabView1.findViewById(R.id.tab_tv);
                    tabTv1.setText(R.string.new_beauty);
                    TabHost.TabSpec tabSpec1 = tabHost.newTabSpec("index" + index).setIndicator(tabView1);
                    return tabSpec1;
                case 2:
                    View tabView2 = layoutInflater.inflate(R.layout.tab_item, null);
                    ImageView tabIv2 = (ImageView) tabView2.findViewById(R.id.tab_iv);
                    tabIv2.setImageResource(R.drawable.ny_discover_selector);
                    TextView tabTv2 = (TextView) tabView2.findViewById(R.id.tab_tv);
                    tabTv2.setText(R.string.beauty);
                    TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("index" + index).setIndicator(tabView2);
                    return tabSpec2;
                case 3:
                    View tabView3 = layoutInflater.inflate(R.layout.tab_item, null);
                    ImageView tabIv3 = (ImageView) tabView3.findViewById(R.id.tab_iv);
                    tabIv3.setImageResource(R.drawable.ny_profile_selector);
                    TextView tabTv3 = (TextView) tabView3.findViewById(R.id.tab_tv);
                    tabTv3.setText(R.string.profile);
                    TabHost.TabSpec tabSpec3 = tabHost.newTabSpec("index" + index).setIndicator(tabView3);
                    return tabSpec3;
            }
            return null;
        }

        @Override
        public Class<? extends Fragment> getFragmentClass(int index) {
            switch (index) {
                case 0:
                    return NYFragmentBigPic.class;
                case 1:
//                    return NYNewBeautyPic.class;
                    return NYBeautyPicFragment.class;
                case 2:
                    return NYFragmentNews.class;
                case 3:
                    return NYFragmentProfile.class;
                case 4:
                    return NYFragmentVideo.class;
            }
            return null;
        }

        @Override
        public Bundle getBundle(int index) {
            return null;
        }
    }

    private int mBackPressCount = 0;
    private long mBackPressTime = 0;

    @Override
    protected void onPause() {
        super.onPause();
        // 插屏广告
        SpotManager.getInstance(this).onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 插屏广告
        SpotManager.getInstance(this).onStop();
    }

    @Override
    public void onBackPressed() {

        // 如果有需要，可以点击后退关闭插播广告。
        if (SpotManager.getInstance(this).isSlideableSpotShowing()) {
            SpotManager.getInstance(this).hideSlideableSpot();
            return;
        }
        if (SpotManager.getInstance(this).isSpotShowing()) {
            SpotManager.getInstance(this).hideSpot();
            return;
        }

            mBackPressCount++;
            if (mBackPressCount == 0 && mBackPressTime == 0) {
                mBackPressTime = System.currentTimeMillis();
            } else {
                long curTime = System.currentTimeMillis();
                if (curTime - mBackPressTime < 2 * 1000 && mBackPressCount == 2) {
                    SpotManager.getInstance(this).onAppExit();
                    System.exit(0);
                    return;
                } else {
                    mBackPressCount = 0;
                    mBackPressTime = 0;
                }
            }
//            FansStationManager.getInstance(this).onAppExit();
            super.onBackPressed();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 插屏广告
        SpotManager.getInstance(this).onDestroy();
    }
}
