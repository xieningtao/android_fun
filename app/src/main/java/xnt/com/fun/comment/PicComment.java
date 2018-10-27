package xnt.com.fun.comment;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobPointer;

public class PicComment extends BmobObject {
    public BmobPointer topicId;
    public String content;
    public BmobPointer userId;
}
