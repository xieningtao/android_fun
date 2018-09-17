package xnt.com.fun.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by NetEase on 2016/10/9 0009.
 */
public class PicGroup extends BmobObject{

    public int number;
    public String imgUrl;
    public String imgLabel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PicGroup picGroup = (PicGroup) o;

        if (number != picGroup.number) return false;
        if (!imgUrl.equals(picGroup.imgUrl)) return false;
        return imgLabel.equals(picGroup.imgLabel);
    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + imgUrl.hashCode();
        result = 31 * result + imgLabel.hashCode();
        return result;
    }
}
