package xnt.com.fun;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.basesmartframe.baseadapter.BaseAdapterHelper;
import com.basesmartframe.basehttp.SFHttpClient;
import com.sf.httpclient.core.AjaxCallBack;
import com.sf.httpclient.core.AjaxParams;
import com.sf.utils.baseutil.GsonUtil;
import com.sf.utils.baseutil.SpUtil;

import xnt.com.fun.bean.NYVideoBean;
import xnt.com.fun.config.AppUrl;
import xnt.com.fun.httpparam.VideoResponse;

/**
 * Created by NetEase on 2016/10/9 0009.
 */
public class NYFragmentVideo extends NYBasePullListFragment<NYVideoBean> {

    private final int PAGE_SIZE=10;
    private String mLatestTime;

    private String getLastTime(){//加载更多时间
        if (getDataSize()>0) {
            NYVideoBean videoBean = getPullItem(getDataSize() - 1);
            if (videoBean != null){
                return  videoBean.timestamp;
            }
        }
        return "-1";
    }

    private String getLatestTime(){//刷新时间
        if (TextUtils.isEmpty(mLatestTime)) {
            mLatestTime = SpUtil.getString(getActivity(), "video_latest_time");
        }else {
            if (getDataSize()>0) {
                NYVideoBean videoBean = getPullItem(0);
                if (videoBean != null){
                    mLatestTime =  videoBean.timestamp;
                }
            }
        }
        return mLatestTime;
    }

    private void getVideos(final boolean refresh){
        AjaxParams params = new AjaxParams();
        params.put("timestamp",refresh?getLatestTime():getLastTime());
        params.put("page_size",String.valueOf(PAGE_SIZE));
        params.put("is_refresh",refresh ? String.valueOf(1) : String.valueOf(-1));
        SFHttpClient.get(AppUrl.GET_VIDEO, params, new AjaxCallBack<String>() {

            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                VideoResponse videoResponse = GsonUtil.parse(result,VideoResponse.class);
                if (videoResponse!=null){
                    finishRefreshOrLoading(videoResponse.videos,true);
                }else {
                    finishRefreshOrLoading(null,false);
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                finishRefreshOrLoading(null,false);
            }
        });
    }
    @Override
    protected boolean onRefresh() {
       getVideos(true);
        return false;
    }

    @Override
    protected boolean onLoadMore() {
        getVideos(false);
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
        position = position - getHeadViewCount();
        NYVideoBean videoBean = getPullItem(position);
        if (!TextUtils.isEmpty(videoBean.videoImgUrl)) {
            VideoPlayActivity.jump2VideoPlay(getActivity(), videoBean.videoUrl, videoBean.videoImgUrl);
        }
    }
}
