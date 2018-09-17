package xnt.com.fun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.nostra13.universalimageloader.utils.L;
import com.sf.utils.baseutil.SFToast;
import com.sflib.CustomView.baseview.EditTextClearDroidView;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import xnt.com.fun.bean.NYBmobUser;

/**
 * Created by NetEase on 2016/11/29 0029.
 */

public class NYLoginActivity extends BaseActivity {

    private Button mLoginBt;
    private EditTextClearDroidView mUserName;
    private EditTextClearDroidView mPwd;
    private TextView mRegisterTv;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ny_login_activity);
        initView();
    }

    private void initView() {
        mLoginBt = (Button) findViewById(R.id.login_bt);
        mUserName = (EditTextClearDroidView) findViewById(R.id.login_atv);
        mPwd = (EditTextClearDroidView) findViewById(R.id.pwd_cdv);
        mRegisterTv = (TextView) findViewById(R.id.register_tv);

        mLoginBt.setOnClickListener(new View.OnClickListener() {
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
                    mProgressDialog = new ProgressDialog(NYLoginActivity.this);
                }
                mProgressDialog.show();
                String userName = mUserName.getEditText().getText().toString();
                String pwd = mPwd.getEditText().getText().toString();
                doLogin(userName, pwd);
            }
        });
        mRegisterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NYLoginActivity.this, NYRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mPwd.getEditText().setHint(R.string.pwd_input_hint);
        mPwd.getEditText().addTextChangedListener(mTextWatcher);
    }

    private void doLogin(final String email, final String pwd) {
        BmobUser use = new BmobUser();
        use.setUsername(email);
        use.setPassword(pwd);
        use.login(new SaveListener<BmobUser>() {

            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if(e==null){
                    SFToast.showToast("登录成功:");
                    //通过BmobUser user = BmobUser.getCurrentUser()获取登录成功后的本地用户信息
                    //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(MyUser.class)获取自定义用户信息
                    NYBmobUser user = BmobUser.getCurrentUser(NYBmobUser.class);
                    L.i(TAG,"cur user info : "+user);
                }else{
                    L.e(e);
                }
            }
        });
    }

    private void doChatLogin(String username, String password) {

    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            updateLoginState();
        }
    };

    private void updateLoginState() {
        if (!TextUtils.isEmpty(mUserName.getEditText().getText()) && !TextUtils.isEmpty(mPwd.getEditText().getText())) {
            mLoginBt.setEnabled(true);
        } else {
            mLoginBt.setEnabled(false);
        }
    }

}
