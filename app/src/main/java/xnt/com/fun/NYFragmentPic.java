package xnt.com.fun;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.basesmartframe.baseadapter.BaseAdapterHelper;
import com.basesmartframe.pickphoto.ImageBean;
import com.basesmartframe.pickphoto.PickPhotosPreviewFragment;
import com.sf.loglib.L;
import com.sf.utils.baseutil.DateFormatHelp;
import com.sf.utils.baseutil.SpUtil;
import com.sf.utils.baseutil.SystemUIWHHelp;
import com.sf.utils.baseutil.UnitHelp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import xnt.com.fun.bean.NYPairPicBean;
import xnt.com.fun.bean.PicGroup;
import xnt.com.fun.bean.PicList;
import xnt.com.fun.config.DisplayOptionConfig;
import xnt.com.fun.tiantu.ActivityPhotoPreview;

/**
 * Created by NetEase on 2016/10/9 0009.
 */
public class NYFragmentPic extends NYBasePullListFragment<NYPairPicBean> {

    private float mWH = 5.0f/7.0f;
    private int mPicWidth;
    private int mPicHeight;
    private String mLatestTime;
    private List<PicGroup> mPicGroups = new ArrayList<>();
    private static final int PIC_PAGE_SIZE = 4;
    private String getLoadMoreTime(){//加载更多时间
        if (getDataSize()>0) {
            NYPairPicBean pairPicBean = getPullItem(getDataSize() - 1);
            if (pairPicBean != null )
                if (pairPicBean.getRightBean() != null){
                return  pairPicBean.getRightBean().getUpdatedAt();
            }else if (pairPicBean.getLeftBean() != null){
                    return pairPicBean.getLeftBean().getUpdatedAt();
                }
        }
        return DateFormatHelp.dateTimeFormat(Calendar.getInstance(),DateFormatHelp._YYYYMMDDHHMMSS);
    }

    private String getRefreshTime(){//刷新时间
        if (TextUtils.isEmpty(mLatestTime)) {
            mLatestTime = SpUtil.getString(getActivity(), "pic_latest_time");
            if (TextUtils.isEmpty(mLatestTime)){
                mLatestTime = "2015-05-05 16:20:22";
            }
        } else {
            if (getDataSize()>0) {
                NYPairPicBean pairPicBean = getPullItem(0);
                if (pairPicBean != null && pairPicBean.getLeftBean() != null){
                    mLatestTime = pairPicBean.getLeftBean().getUpdatedAt();
                }
            }
        }
        //保存当前刷新的时间
        SpUtil.save(getActivity(), "pic_latest_time",mLatestTime);
        return mLatestTime;
    }
    @Override
    protected boolean onRefresh() {
        getPicByBmob(true);
        return false;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPicWidth = getPicWidth();
        mPicHeight = getPicHeight(mPicWidth,mWH);
    }

    private void getPicByBmob(final boolean refresh){
        BmobQuery<PicGroup> query = new BmobQuery<PicGroup>();
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(PIC_PAGE_SIZE);
//        query.order("updatedAt");
        String updateContent = refresh? getRefreshTime(): getLoadMoreTime();
        Date updateData = DateFormatHelp.StrDateToCalendar(updateContent,DateFormatHelp._YYYYMMDDHHMMSS);
        if (refresh) { //refresh
            query.addWhereGreaterThanOrEqualTo("updatedAt", new BmobDate(updateData));
        }else {//load more
            query.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(updateData));
        }


        //执行查询方法
        query.findObjects(new FindListener<PicGroup>() {
            @Override
            public void done(List<PicGroup> picGroups, BmobException e) {
                if (e == null) {
                    //新加载出来的数据
                    List<PicGroup> diffPicGroups = removeExist(picGroups);
                    Collections.sort(diffPicGroups, new Comparator<PicGroup>() {
                        @Override
                        public int compare(PicGroup lhs, PicGroup rhs) {
                            return -lhs.getUpdatedAt().compareTo(rhs.getUpdatedAt());
                        }
                    });
                    List<NYPairPicBean> pairPicBeans = null;
                    //后续的刷新操作
                    if (refresh){
                        mPicGroups.addAll(0,diffPicGroups);
                        if (mPicGroups.size() > PIC_PAGE_SIZE){
                            List<PicGroup> tempPicGroups = new ArrayList<>(mPicGroups);
                            mPicGroups.clear();
                            mPicGroups.addAll(tempPicGroups.subList(0,PIC_PAGE_SIZE));
                        }
                        pairPicBeans = single2Pair(mPicGroups);
                    }else{//加载更多操作
                        mPicGroups.addAll(diffPicGroups);
                        pairPicBeans = single2Pair(diffPicGroups);
                    }
                    //去重复
                    finishRefreshOrLoading(pairPicBeans, true);
                } else {
                    mPicGroups.clear();
                    finishRefreshOrLoading(null,false);
                    L.info("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    //一般只有第一个是重复的
    private  List<PicGroup> removeExist(List<PicGroup> picGroups) {
        List<PicGroup> diffPicGroup = new ArrayList<>();
        if (picGroups == null || picGroups.size() == 0) {
            return diffPicGroup;
        }
        int size = getDataSize();

        PicGroup picGroup = null;
        for (int j = 0; j < picGroups.size(); j++) {
            boolean exist = false;
            picGroup = picGroups.get(j);
            for (int i = 0; i < size; i++) {
                NYPairPicBean pairPicBean = getPullItem(i);
                if (picGroup.equals(pairPicBean.getLeftBean()) || picGroup.equals(pairPicBean.getRightBean())) {
                    exist = true;
                    break;
                }
            }
            if (!exist){
                diffPicGroup.add(picGroup);
            }
        }
        if (diffPicGroup.size()%2 != 0){
            diffPicGroup.remove(0);
        }
        return diffPicGroup;
    }
    private List<NYPairPicBean> single2Pair(List<PicGroup> picBeanList){
        List<NYPairPicBean> pairPicBeanList=new ArrayList<>();
        if(picBeanList==null||picBeanList.isEmpty()){
            return pairPicBeanList;
        }
        int index=0;
        int size=picBeanList.size();
        while (index<size){
            NYPairPicBean pairPicBean=new NYPairPicBean();
            PicGroup leftPicBean=picBeanList.get(index);
            pairPicBean.setLeftBean(leftPicBean);
            index++;
            if(index<size) {
                PicGroup rightPicBean = picBeanList.get(index);
                pairPicBean.setRightBean(rightPicBean);
                index++;
            }
            pairPicBeanList.add(pairPicBean);
        }
        return pairPicBeanList;
    }

    @Override
    protected boolean onLoadMore() {
        getPicByBmob(false);
        return false;
    }

    @Override
    protected int[] getLayoutIds() {
        return new int[]{R.layout.ny_topic_item};
    }

    private int getPicWidth(){
        int screenWidth = SystemUIWHHelp.getScreenRealWidth(getActivity());
        int reminderWidth = screenWidth - UnitHelp.dip2px(getActivity(),4*2+4*2);
        return reminderWidth/2;
    }

    private int getPicHeight(int width,float ratio){
        return (int) (width/ratio);
    }


    @Override
    protected void bindView(BaseAdapterHelper help, int position, final NYPairPicBean bean) {
        final PicGroup leftBean=bean.getLeftBean();
        RelativeLayout leftRl = help.getView(R.id.left_rl);
        RelativeLayout rightRl = help.getView(R.id.right_rl);
        ViewGroup.LayoutParams leftParams = leftRl.getLayoutParams();
        ViewGroup.LayoutParams rightParams = rightRl.getLayoutParams();
        leftParams.width = rightParams.width = mPicWidth;
        leftParams.height = rightParams.height = mPicHeight;
        leftRl.setLayoutParams(leftParams);
        rightRl.setLayoutParams(rightParams);
        if(leftBean!=null) {
            final PicGroup coverBean=leftBean;
            help.setVisible(R.id.left_rl,View.VISIBLE);
            if(coverBean!=null) {
                help.setImageBuilder(R.id.pic_first_iv, coverBean.imgUrl, DisplayOptionConfig.getDisplayOption(R.drawable.app_icon));
                help.setText(R.id.pic_number_first_tv, coverBean.number + "");
                help.setText(R.id.pic_label_first_tv, coverBean.imgLabel);
                help.getView(R.id.left_rl).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ActivityPhotoPreview.class);
                        intent.putExtra(PickPhotosPreviewFragment.INDEX, 0);
                        intent.putExtra(ActivityPhotoPreview.IMAGE_GROUP_ID,coverBean.getObjectId());
                        startActivity(intent);
                    }
                });
            }else {
                help.setVisible(R.id.left_rl,View.INVISIBLE);
            }
        }else {
            help.setVisible(R.id.left_rl,View.INVISIBLE);
        }

        final PicGroup rightBean=bean.getRightBean();
        if(rightBean!=null) {
            help.setVisible(R.id.right_rl,View.VISIBLE);
            final PicGroup coverBean=rightBean;
            if(coverBean!=null) {
                help.setImageBuilder(R.id.pic_second_iv, coverBean.imgUrl);
                help.setText(R.id.pic_number_second_tv, coverBean.number + "");
                help.setText(R.id.pic_label_second_tv, coverBean.imgLabel);
                help.getView(R.id.right_rl).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ActivityPhotoPreview.class);
                        intent.putExtra(PickPhotosPreviewFragment.INDEX, 0);
                        intent.putExtra(ActivityPhotoPreview.IMAGE_GROUP_ID,coverBean.getObjectId());
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

    private ArrayList<String> getImageMd5Values(List<PicList> picListBeen){
        ArrayList<String> imageMd5Values=new ArrayList<>();
        if(picListBeen==null||picListBeen.isEmpty()){
            return imageMd5Values;
        }
        for (PicList imageBean : picListBeen) {
            imageMd5Values.add(imageBean.getImageUrlMd5());
        }
        return imageMd5Values;

    }
    private ArrayList<ImageBean> tianTuImageList2ImageBean(List<PicList> picListBeen) {
        ArrayList<ImageBean> imageBeanList = new ArrayList<>();
        if(picListBeen==null||picListBeen.isEmpty()){
            return imageBeanList;
        }
        for (PicList imageBean : picListBeen) {
            ImageBean newImageBean = new ImageBean();
            newImageBean.setPath(imageBean.getImageUrl());
            imageBeanList.add(newImageBean);
        }
        return imageBeanList;
    }
}
