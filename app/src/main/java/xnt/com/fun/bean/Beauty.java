package xnt.com.fun.bean;

import android.text.TextUtils;

import cn.bmob.v3.BmobObject;

/**
 * Created by mac on 2018/6/2.
 */

public class Beauty extends BmobObject {
    public String imgUrl;
    public String imgLabel;
    public String imgDesc;
    public int indexId ;
    public String beautyWords;
    public boolean isPraised;
    public Integer commentNum;
    public Integer praiseNum;

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

    public String getFormatWords() {
        if (TextUtils.isEmpty(beautyWords)){
            return "";
        }
        int size = beautyWords.length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0;i<size;i++){
            builder.append(beautyWords.charAt(i));
            if (i != size -1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }
}
