package xnt.com.fun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.basesmartframe.baseadapter.BaseAdapterHelper;
import com.basesmartframe.basehttp.SFHttpClient;
import com.basesmartframe.pickphoto.ActivityFragmentContainer;
import com.sf.httpclient.core.AjaxCallBack;
import com.sf.utils.baseutil.SFToast;

import xnt.com.fun.bean.NYNewsBean;
import xnt.com.fun.config.AppUrl;

/**
 * Created by NetEase on 2016/10/9 0009.
 */
public class NYFragmentNews extends NYBasePullListFragment<NYNewsBean> {

    private final int PAGE_SIZE = 10;

    @Override
    protected boolean onRefresh() {
        getNews(true);
        return false;
    }

    private void getNews(boolean refresh) {

        SFHttpClient.get(AppUrl.GET_ARTICLE, new AjaxCallBack<String>() {

            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                finishRefreshOrLoading(null,false);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                finishRefreshOrLoading(null,false);
            }
        });
    }

    @Override
    protected boolean onLoadMore() {
        getNews(false);
        return false;
    }

    @Override
    protected int[] getLayoutIds() {
        return new int[]{R.layout.ny_news_item};
    }

    @Override
    protected void bindView(BaseAdapterHelper help, int position, NYNewsBean bean) {
        help.setImageBuilder(R.id.news_iv, bean.getImageUrl());
        help.setText(R.id.news_label_tv, bean.getLabel());
        help.setText(R.id.news_title_tv, bean.getTitle());
    }

    @Override
    protected boolean onRefreshNoNetwork() {
        SFToast.showToast(R.string.no_network);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int curPos = position - getHeadViewCount();
        NYNewsBean bean = getPullItem(curPos);
        Intent intent = new Intent(getActivity(), ActivityFragmentContainer.class);
        intent.putExtra(ActivityFragmentContainer.FRAGMENT_CLASS_NAME, NYFragmentNewsDetail.class.getName());
        Bundle bundle = new Bundle();
        bundle.putString(NYFragmentNewsDetail.NEWS_ID, bean.getId());
//        bundle.putString(NYFragmentNewsDetail.NEWS_ID, bean.getId());
        intent.putExtra(ActivityFragmentContainer.BUNDLE_CONTAINER, bundle);
        startActivity(intent);
    }
}
