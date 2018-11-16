package xnt.com.fun.login;

import android.app.Activity;

import com.umeng.socialize.UMAuthListener;

public interface LoginAction {

    boolean login(Activity context,UMAuthListener listener);
}
