package xnt.com.fun.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by mac on 2018/6/2.
 */

public class CardPicBean extends BmobObject {
    public String imageUrl;
    public String imgDesc;
    public boolean isPraised;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CardPicBean that = (CardPicBean) o;

        if (imageUrl != null ? !imageUrl.equals(that.imageUrl) : that.imageUrl != null)
            return false;
        return imgDesc != null ? imgDesc.equals(that.imgDesc) : that.imgDesc == null;
    }

    @Override
    public int hashCode() {
        int result = imageUrl != null ? imageUrl.hashCode() : 0;
        result = 31 * result + (imgDesc != null ? imgDesc.hashCode() : 0);
        return result;
    }
}
