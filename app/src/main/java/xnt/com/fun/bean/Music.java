package xnt.com.fun.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by mac on 2018/6/2.
 */

public class Music extends BmobObject {
    public String tag;
    public String musicTitle;
    public BmobFile musicFile;
    public String musicUrl;
}
