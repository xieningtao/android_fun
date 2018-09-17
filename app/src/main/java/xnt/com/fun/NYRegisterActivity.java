package xnt.com.fun;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.basesmartframe.baseui.BaseActivity;
import com.nostra13.universalimageloader.utils.L;
import com.sf.utils.baseutil.SFToast;
import com.sflib.CustomView.baseview.EditTextClearDroidView;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import xnt.com.fun.bean.NYBmobUser;

/**
 * Created by NetEase on 2016/11/29 0029.
 */

public class NYRegisterActivity extends BaseActivity {
    private EditTextClearDroidView mUserName;
    private EditTextClearDroidView mPwd;

    private Button mRegister;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ny_register_activity);
        initial();
    }

    private void initial() {
        mUserName = (EditTextClearDroidView) findViewById(R.id.phoneNum_atv);
        mPwd = (EditTextClearDroidView) findViewById(R.id.pwd_cdv);
        mRegister = (Button) findViewById(R.id.register_bt);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mUserName.getEditText().getText())) {
                    SFToast.showToast(R.string.user_name_input_hint);
                    return;
                }

                if (TextUtils.isEmpty(mPwd.getEditText().getText())) {
                    SFToast.showToast(R.string.pwd_input_hint);
                    return;
                }
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(NYRegisterActivity.this);
                }
                mProgressDialog.show();
                String userName = mUserName.getEditText().getText().toString();
                String pwd = mPwd.getEditText().getText().toString();
                doRegister(userName, pwd);
            }
        });
    }

    private void doRegister(String email, String pwd) {
        NYBmobUser bu = new NYBmobUser();
        bu.setPassword(pwd);
        bu.setEmail(email);
        bu.setUsername(email);
        //注意：不能用save方法进行注册
        bu.signUp(new SaveListener<NYBmobUser>() {
            @Override
            public void done(NYBmobUser s, BmobException e) {
                if(e==null){
                    SFToast.showToast("注册成功:" +s.toString());
                    finish();
                }else{
                    L.e(e);
                }
            }
        });
    }
}
