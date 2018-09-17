package xnt.com.fun;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.basesmartframe.baseadapter.BaseAdapterHelper;
import com.nostra13.universalimageloader.utils.L;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import xnt.com.fun.bean.Video;

/**
 * Created by NetEase on 2016/10/9 0009.
 */
public class NYFragmentVideo extends NYBasePullListFragment<Video> {

    private final int PAGE_SIZE = 10;
    private String mLatestTime;

//    private String getLastTime() {//加载更多时间
//        if (getDataSize() > 0) {
//            Video videoBean = getPullItem(getDataSize() - 1);
//            if (videoBean != null) {
//                return videoBean.timestamp;
//            }
//        }
//        return "-1";
//    }

//    private String getLatestTime() {//刷新时间
//        if (TextUtils.isEmpty(mLatestTime)) {
//            mLatestTime = SpUtil.getString(getActivity(), "video_latest_time");
//        } else {
//            if (getDataSize() > 0) {
//                Video videoBean = getPullItem(0);
//                if (videoBean != null) {
//                    mLatestTime = videoBean.timestamp;
//                }
//            }
//        }
//        return mLatestTime;
//    }

    private void getVideoByBmob(boolean refresh) {
        BmobQuery<Video> query = new BmobQuery<Video>();
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(PAGE_SIZE);
        //执行查询方法
        query.findObjects(new FindListener<Video>() {
            @Override
            public void done(List<Video> videos, BmobException e) {
                if (e == null) {
                    finishRefreshOrLoading(videos, true);
                } else {
                    finishRefreshOrLoading(null,false);
                    L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }



    @Override
    protected boolean onRefresh() {
        getVideoByBmob(true);
        return false;
    }

    @Override
    protected boolean onLoadMore() {
        getVideoByBmob(false);
        return false;
    }

    @Override
    protected int[] getLayoutIds() {
        return new int[]{R.layout.ny_video_item};
    }

    @Override
    protected void bindView(BaseAdapterHelper help, int position, Video bean) {
        help.setImageBuilder(R.id.video_iv, bean.videoCoverUrl);
        help.setText(R.id.video_title_tv, bean.videoName);
//        help.setText(R.id.ny_video_diggest_tv, bean.videoDescription);
//        help.setText(R.id.ny_video_label_tv, bean.videoLabel);
//        help.setText(R.id.ny_video_count_tv, bean.videoCount + "");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = position - getHeadViewCount();
        Video videoBean = getPullItem(position);
        if (!TextUtils.isEmpty(videoBean.videoCoverUrl)) {
            VideoPlayActivity.jump2VideoPlay(getActivity(), videoBean.videoUrl, videoBean.videoCoverUrl);
        }
    }
}
