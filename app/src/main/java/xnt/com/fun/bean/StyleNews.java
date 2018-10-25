package xnt.com.fun.bean;

import cn.bmob.v3.BmobObject;

public class StyleNews extends BmobObject {
    public String imgUrl;
    public String imgLabel;
    public String imgDesc;
    public String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StyleNews styleNews = (StyleNews) o;

        return imgUrl != null ? imgUrl.equals(styleNews.imgUrl) : styleNews.imgUrl == null;
    }

    @Override
    public int hashCode() {
        return imgUrl != null ? imgUrl.hashCode() : 0;
    }
}
