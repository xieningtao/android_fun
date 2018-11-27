package xnt.com.fun;

import xnt.com.fun.bean.NYBmobUser;

public class UserManager {
    private NYBmobUser mUser = new NYBmobUser();
    private static UserManager manager = null;
    private static Object lock = new Object();
    private UserManager(){

    }

    public static UserManager getInstance(){
        if(manager == null){
            synchronized (lock){
                if(manager == null){
                    manager = new UserManager();
                }
            }
        }
        return manager;
    }

    public NYBmobUser getUser(){
        return mUser;
    }

    public void setAvatarUrl(String avatarUrl){
        mUser.setAvatarUrl(avatarUrl);
    }

    public void setNickName(String nickName){
        mUser.setNick(nickName);
    }

    public String getAvatarUrl(){
        return mUser.getAvatarUrl();
    }

    public String getNickName(){
        return mUser.getNick();
    }

    public void clearUserInfo(){
        mUser.setNick("");
        mUser.setAvatarUrl("");
    }
}
