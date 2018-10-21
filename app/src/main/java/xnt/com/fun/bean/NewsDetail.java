package xnt.com.fun.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by NetEase on 2016/10/10 0010.
 */
public class NewsDetail extends BmobObject{

    public String newsId;
    public BmobFile newsDetail;
    public String newEtraUrl;
}
