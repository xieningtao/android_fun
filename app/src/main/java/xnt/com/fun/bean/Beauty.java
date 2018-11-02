package xnt.com.fun.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by mac on 2018/6/2.
 */

public class Beauty extends BmobObject {
    public String imgUrl;
    public String imgLabel;
    public String imgDesc;
    public int indexId ;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Beauty beauty = (Beauty) o;

        return imgUrl != null ? imgUrl.equals(beauty.imgUrl) : beauty.imgUrl == null;
    }

    @Override
    public int hashCode() {
        return imgUrl != null ? imgUrl.hashCode() : 0;
    }
}
