package xnt.com.fun.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by mac on 2018/6/2.
 */

public class NYBmobUser extends BmobUser {

    private Boolean sex;
    private String nick;
    private Integer age;

    public boolean getSex() {
        return this.sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getNick() {
        return this.nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "NYBmobUser{" +
                "sex=" + sex +
                ", nick='" + nick + '\'' +
                ", age=" + age +
                '}';
    }
}
