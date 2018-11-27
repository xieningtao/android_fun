package xnt.com.fun;

import android.app.Dialog;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.L;
import com.sf.utils.baseutil.SFBus;
import com.sf.utils.baseutil.SpUtil;
import com.sflib.reflection.core.SFIntegerMessage;
import com.sflib.reflection.core.ThreadId;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import cn.bmob.v3.BmobUser;
import xnt.com.fun.bean.NYBmobUser;
import xnt.com.fun.config.DisplayOptionConfig;
import xnt.com.fun.dialog.NYProgressDialog;
import xnt.com.fun.login.LoginConstant;
import xnt.com.fun.login.ThirdLoginActivity;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by NetEase on 2016/10/9 0009.
 */
public class NYFragmentProfile extends BaseFragment {

    private ImageView mAvatarIv;
    private TextView mNickNameTv;

    private Dialog mDialog;
    private OnUserInfoChangeListener changeListener = new OnUserInfoChangeListener() {
        @Override
        @SFIntegerMessage(messageId = MessageId.USER_INFO_CHANGE, theadId = ThreadId.MainThread)
        public void onUserInfoChange() {
            updateUserInfo();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ny_fragment_profile, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SFBus.register(changeListener);
        initView(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SFBus.unregister(changeListener);
    }

    private void updateUserInfo() {
        NYBmobUser bmobUser = BmobUser.getCurrentUser(NYBmobUser.class);
        if (bmobUser != null) {
            ImageLoader.getInstance().displayImage(bmobUser.getAvatarUrl(), mAvatarIv, DisplayOptionConfig.getDefaultDisplayOption());
            mNickNameTv.setText(bmobUser.getNick());
        } else {
            mAvatarIv.setImageResource(R.drawable.base_avatar_default_bg);
            mNickNameTv.setText(R.string.random_name);
        }
    }

    private void initView(View view) {
        mAvatarIv = view.findViewById(R.id.ny_photo_iv);
        mNickNameTv = view.findViewById(R.id.nick_name);
        updateUserInfo();
        //version
        TextView versionContentTv = view.findViewById(R.id.version_name_tv);
        versionContentTv.setText(BuildConfig.VERSION_NAME);
        //upgrade
        view.findViewById(R.id.version_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upgrade();
            }
        });
        //login
        view.findViewById(R.id.personal_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utils.isLogin()) {
                    ThirdLoginActivity.toLogin(getActivity());
                }
            }
        });

        view.findViewById(R.id.logout_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }



    private void logout() {
        if (mDialog == null) {
            mDialog = NYProgressDialog.getProgressDialog(getActivity(), getString(R.string.logout_tip));
        }
        mDialog.show();
        BmobUser.logOut();
        String loginType = SpUtil.getString(getActivity(), LoginConstant.LOGIN_TYPE);
        if (LoginConstant.SINA.equals(loginType)) {
            UMShareAPI.get(getActivity()).deleteOauth(getActivity(), SHARE_MEDIA.SINA, new UMAuthListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {

                }

                @Override
                public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                    mDialog.dismiss();
//                        UserManager.getInstance().clearUserInfo();
                    updateUserInfo();
                }

                @Override
                public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                    mDialog.dismiss();
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media, int i) {
                    mDialog.dismiss();
                }
            });
        }
    }

    private void upgrade() {
        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
        String apkUrl = "https://raw.githubusercontent.com/xieningtao/documents/master/apk/ofo.apk";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setDestinationInExternalPublicDir("mm", "ofo.apk");
        request.setTitle("M拍");
        request.setDescription("正在下载...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        long downloadId = downloadManager.enqueue(request);
        L.i(TAG, "downloadId: " + downloadId);
    }

    private interface OnUserInfoChangeListener {
        void onUserInfoChange();
    }
}
