package xnt.com.fun.comment;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobPointer;

public class PicComment extends BmobObject {
    public BmobPointer topicId;
    public String content;
    public BmobPointer userId;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PicComment that = (PicComment) o;
        return getObjectId().equals(that.getObjectId());
    }

    @Override
    public int hashCode() {
      return getObjectId().hashCode();
    }
}
