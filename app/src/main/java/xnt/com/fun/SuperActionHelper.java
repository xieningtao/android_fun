package xnt.com.fun;

import android.util.Log;

import com.sf.loglib.L;
import com.sf.utils.baseutil.SFToast;

import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;

public class SuperActionHelper {
    public static final String TAG = "SuperActionHelper";
    public static void deleteByGroupId(List<BmobObject> subBeans, final List<BmobObject> groupBeans) {
        if(subBeans == null || subBeans.size() ==0 ){
            return;
        }
        new BmobBatch().deleteBatch(subBeans).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                boolean removeSuccessful = true;
                if(e==null){
                    for(int i=0;i<list.size();i++){
                        BatchResult result = list.get(i);
                        BmobException ex =result.getError();
                        if(ex==null){
                            L.info(TAG,"第"+i+"个数据批量删除成功");
                        }else{
                            removeSuccessful = false;
                            L.info(TAG,"第"+i+"个数据批量删除失败："+ex.getMessage()+","+ex.getErrorCode());
                        }
                    }

                }else{
                    removeSuccessful = false;
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
                if(!removeSuccessful) {
                    SFToast.showToast("sub-object删除失败");
                }else {
                    SFToast.showToast("sub-object子图删除成功");
                    deleteById(groupBeans);
                }
            }
        });
    }

    public static void deleteById(List<BmobObject> deleteBeans){
        if(deleteBeans == null || deleteBeans.size() == 0){
            return;
        }
        new BmobBatch().deleteBatch(deleteBeans).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                boolean removeSuccessful = true;
                if(e != null){
                    removeSuccessful = false;
                }
                if(!removeSuccessful) {
                    SFToast.showToast("删除失败");
                }else {
                    SFToast.showToast("删除成功");
                }
            }
        });
    }


    public static interface SuperUpdateListener{
        void onDone(String content);
    }

}
