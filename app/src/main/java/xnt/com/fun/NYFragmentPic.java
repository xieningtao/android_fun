package xnt.com.fun;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.basesmartframe.baseadapter.BaseAdapterHelper;
import com.basesmartframe.basehttp.SFHttpClient;
import com.basesmartframe.pickphoto.ImageBean;
import com.basesmartframe.pickphoto.PickPhotosPreviewFragment;
import com.sf.httpclient.core.AjaxCallBack;
import com.sf.httpclient.core.AjaxParams;
import com.sf.utils.baseutil.GsonUtil;
import com.sf.utils.baseutil.SpUtil;

import java.util.ArrayList;
import java.util.List;

import xnt.com.fun.bean.NYPairPicBean;
import xnt.com.fun.bean.NYPicBean;
import xnt.com.fun.bean.NYPicCollectionBean;
import xnt.com.fun.bean.NYPicCoverBean;
import xnt.com.fun.bean.NYPicListBean;
import xnt.com.fun.config.AppUrl;
import xnt.com.fun.tiantu.ActivityPhotoPreview;

/**
 * Created by NetEase on 2016/10/9 0009.
 */
public class NYFragmentPic extends NYBasePullListFragment<NYPairPicBean> {

    private String mLatestTime;
    @Override
    protected boolean onRefresh() {
        getPic(true);
        return false;
    }

    private String getLastTime(){//加载更多时间
        if (getDataSize()>0) {
            NYPairPicBean pairPicBean = getPullItem(getDataSize() - 1);
            if (pairPicBean != null && pairPicBean.getmLeftBean() != null){
                 return  pairPicBean.getmLeftBean().timestamp;
            }
        }
        return "-1";
    }

    private String getLatestTime(){//刷新时间
        if (TextUtils.isEmpty(mLatestTime)) {
            mLatestTime = SpUtil.getString(getActivity(), "pic_latest_time");
        }else {
            if (getDataSize()>0) {
                NYPairPicBean pairPicBean = getPullItem(0);
                if (pairPicBean != null && pairPicBean.getmLeftBean() != null){
                    mLatestTime = pairPicBean.getmLeftBean().timestamp;
                }
            }
        }
        return mLatestTime;
    }
    private void getPic(boolean refresh){
        AjaxParams params = new AjaxParams();
        params.put("timestamp",refresh ? getLatestTime() : getLastTime());
        params.put("page_size","20");
        params.put("is_refresh",refresh ? String.valueOf(1) : String.valueOf(-1));
        SFHttpClient.get(AppUrl.GET_PIC,params, new AjaxCallBack<String>() {

            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                NYPicCollectionBean picCollectionBeans = GsonUtil.parse(result,NYPicCollectionBean.class);
                if (picCollectionBeans!=null) {
                    List<NYPairPicBean> pairPicBeans = single2Pair(picCollectionBeans.imgs);
                    if (pairPicBeans != null) {
                        finishRefreshOrLoading(pairPicBeans, true);
                    } else {
                        finishRefreshOrLoading(null, false);
                    }
                }else {
                    finishRefreshOrLoading(null, false);
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                finishRefreshOrLoading(null,false);
            }
        });
    }
    private List<NYPairPicBean> single2Pair(List<NYPicBean> picBeanList){
        List<NYPairPicBean> pairPicBeanList=new ArrayList<>();
        if(picBeanList==null||picBeanList.isEmpty()){
            return pairPicBeanList;
        }
        int index=0;
        int size=picBeanList.size();
        while (index<size){
            NYPairPicBean pairPicBean=new NYPairPicBean();
            NYPicBean leftPicBean=picBeanList.get(index);
            pairPicBean.setLeftBean(leftPicBean);
            index++;
            if(index<size) {
                NYPicBean rightPicBean = picBeanList.get(index);
                pairPicBean.setmRightBean(rightPicBean);
                index++;
            }
            pairPicBeanList.add(pairPicBean);
        }
        return pairPicBeanList;
    }

    @Override
    protected boolean onLoadMore() {
        getPic(false);
        return false;
    }

    @Override
    protected int[] getLayoutIds() {
        return new int[]{R.layout.ny_topic_item};
    }

    @Override
    protected void bindView(BaseAdapterHelper help, int position, final NYPairPicBean bean) {
        final NYPicBean leftBean=bean.getmLeftBean();
        if(leftBean!=null) {
            NYPicCoverBean coverBean=leftBean.cover;
            help.setVisible(R.id.left_rl,View.VISIBLE);
            if(coverBean!=null) {
                help.setImageBuilder(R.id.pic_first_iv, coverBean.imgUrl);
                help.setText(R.id.pic_number_first_tv, coverBean.number + "");
                help.setText(R.id.pic_label_first_tv, coverBean.imgLabel);
                help.getView(R.id.left_rl).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ActivityPhotoPreview.class);
                        intent.putExtra(PickPhotosPreviewFragment.INDEX, 0);
                        intent.putStringArrayListExtra(ActivityPhotoPreview.IMAGE_MD5,getImageMd5Values(leftBean.picListBean));
                        intent.putExtra(ActivityPhotoPreview.IMAGE_BEAN_LIST, tianTuImageList2ImageBean(leftBean.picListBean));
                        startActivity(intent);
                    }
                });
            }else {
                help.setVisible(R.id.left_rl,View.INVISIBLE);
            }
        }else {
            help.setVisible(R.id.left_rl,View.INVISIBLE);
        }

        final NYPicBean rightBean=bean.getRightBean();
        if(rightBean!=null) {
            help.setVisible(R.id.right_rl,View.VISIBLE);
            NYPicCoverBean coverBean=rightBean.cover;
            if(coverBean!=null) {
                help.setImageBuilder(R.id.pic_second_iv, coverBean.imgUrl);
                help.setText(R.id.pic_number_second_tv, coverBean.number + "");
                help.setText(R.id.pic_label_second_tv, coverBean.imgLabel);
                help.getView(R.id.right_rl).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ActivityPhotoPreview.class);
                        intent.putExtra(PickPhotosPreviewFragment.INDEX, 0);
                        intent.putStringArrayListExtra(ActivityPhotoPreview.IMAGE_MD5,getImageMd5Values(rightBean.picListBean));
                        intent.putExtra(ActivityPhotoPreview.IMAGE_BEAN_LIST, tianTuImageList2ImageBean(rightBean.picListBean));
                        startActivity(intent);
                    }
                });
            }else {
                help.setVisible(R.id.right_rl,View.INVISIBLE);
            }
        }else {
            help.setVisible(R.id.right_rl,View.INVISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


    }

    private ArrayList<String> getImageMd5Values(List<NYPicListBean> picListBeen){
        ArrayList<String> imageMd5Values=new ArrayList<>();
        if(picListBeen==null||picListBeen.isEmpty()){
            return imageMd5Values;
        }
        for (NYPicListBean imageBean : picListBeen) {
            imageMd5Values.add(imageBean.getImageUrlMd5());
        }
        return imageMd5Values;

    }
    private ArrayList<ImageBean> tianTuImageList2ImageBean(List<NYPicListBean> picListBeen) {
        ArrayList<ImageBean> imageBeanList = new ArrayList<>();
        if(picListBeen==null||picListBeen.isEmpty()){
            return imageBeanList;
        }
        for (NYPicListBean imageBean : picListBeen) {
            ImageBean newImageBean = new ImageBean();
            newImageBean.setPath(imageBean.getImageUrl());
            imageBeanList.add(newImageBean);
        }
        return imageBeanList;
    }
}
