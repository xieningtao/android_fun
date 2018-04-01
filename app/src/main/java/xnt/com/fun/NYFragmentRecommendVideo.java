package xnt.com.fun;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;

import com.basesmartframe.baseadapter.BaseAdapterHelper;

import xnt.com.fun.bean.NYVideoBean;

/**
 * Created by NetEase on 2016/11/3 0003.
 */

public class NYFragmentRecommendVideo extends NYBasePullListFragment<NYVideoBean> {

    private final int PAGE_SIZE=10;
    public static interface OnVideoPlayItemClick{
        void onVideoPlayClick(String videoUrl, String coverUrl);
    }

    private OnVideoPlayItemClick mOnVideoPlayItemClick;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnVideoPlayItemClick){
            mOnVideoPlayItemClick= (OnVideoPlayItemClick) activity;
        }
    }

    private void doRequest(boolean refresh){

    }
    @Override
    protected boolean onRefresh() {
        doRequest(true);
        return true;
    }

    @Override
    protected boolean onLoadMore() {
        doRequest(false);
        return false;
    }

    @Override
    protected int[] getLayoutIds() {
        return new int[]{R.layout.ny_video_item};
    }

    @Override
    protected void bindView(BaseAdapterHelper help, int position, NYVideoBean bean) {
        help.setImageBuilder(R.id.video_iv, bean.videoImgUrl);
        help.setText(R.id.video_title_tv, bean.videoTitle);
        help.setText(R.id.ny_video_diggest_tv,bean.videoDescription);
        help.setText(R.id.ny_video_label_tv,bean.videoLabel);
        help.setText(R.id.ny_video_count_tv,bean.videoCount+"");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position=position-getHeadViewCount();
        NYVideoBean videoBean = getPullItem(position);
        mOnVideoPlayItemClick.onVideoPlayClick(videoBean.videoUrl,videoBean.videoImgUrl);
    }
}
