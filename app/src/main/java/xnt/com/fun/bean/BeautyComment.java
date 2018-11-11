package xnt.com.fun.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobPointer;

/**
 * Created by mac on 2018/6/2.
 */

public class BeautyComment extends BmobObject {
    public BmobPointer beautyId;
    public String content;
    public BmobPointer userId;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeautyComment that = (BeautyComment) o;
        return getObjectId().equals(that.getObjectId());
    }

    @Override
    public int hashCode() {
        return getObjectId().hashCode();
    }
}
