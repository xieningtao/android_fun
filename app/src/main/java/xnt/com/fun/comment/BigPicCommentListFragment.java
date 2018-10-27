package xnt.com.fun.comment;

import android.view.View;
import android.widget.AdapterView;

import com.basesmartframe.baseadapter.BaseAdapterHelper;

import xnt.com.fun.NYBasePullListFragment;

public class BigPicCommentListFragment extends NYBasePullListFragment<PicComment> {


    @Override
    protected boolean onRefresh() {
        return false;
    }

    @Override
    protected boolean onLoadMore() {
        return false;
    }

    @Override
    protected int[] getLayoutIds() {
        return new int[0];
    }

    @Override
    protected void bindView(BaseAdapterHelper baseAdapterHelper, int i, PicComment picComment) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
