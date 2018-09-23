package xnt.com.fun.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by NetEase on 2016/10/9 0009.
 */
public class News extends BmobObject{

   public String newsId;

    public String newsTitle;

    public String newsDigest;

    public String newsCoverUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        News news = (News) o;

        return newsId != null ? newsId.equals(news.newsId) : news.newsId == null;
    }

    @Override
    public int hashCode() {
        return newsId != null ? newsId.hashCode() : 0;
    }
}
