package xnt.com.fun.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobPointer;

/**
 * Created by mac on 2018/6/2.
 */

public class CardPicGroup extends BmobObject {
    public String imgUrl;
    public String imgLabel;
    public String imgDesc;

    public int commentNum;
    public int praiseNum;

    public String latestCommentContent;
    public BmobPointer latestUserId;
}
