package xnt.com.fun.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by mac on 2018/6/2.
 */

public class CardPicGroup extends BmobObject {
    public String imgUrl;
    public String imgLabel;
    public String imgDesc;

    public Integer commentNum;
    public Integer praiseNum;

    public String latestCommentContent;
    public NYBmobUser latestUserId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CardPicGroup that = (CardPicGroup) o;

        if (commentNum != that.commentNum) return false;
        if (praiseNum != that.praiseNum) return false;
        if (imgUrl != null ? !imgUrl.equals(that.imgUrl) : that.imgUrl != null) return false;
        if (imgLabel != null ? !imgLabel.equals(that.imgLabel) : that.imgLabel != null)
            return false;
        if (imgDesc != null ? !imgDesc.equals(that.imgDesc) : that.imgDesc != null) return false;
        return latestCommentContent != null ? latestCommentContent.equals(that.latestCommentContent) : that.latestCommentContent == null;
    }

    @Override
    public int hashCode() {
        int result = imgUrl != null ? imgUrl.hashCode() : 0;
        result = 31 * result + (imgLabel != null ? imgLabel.hashCode() : 0);
        result = 31 * result + (imgDesc != null ? imgDesc.hashCode() : 0);
        result = 31 * result + commentNum;
        result = 31 * result + praiseNum;
        result = 31 * result + (latestCommentContent != null ? latestCommentContent.hashCode() : 0);
        return result;
    }
}
