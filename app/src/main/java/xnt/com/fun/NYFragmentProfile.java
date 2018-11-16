package xnt.com.fun;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.basesmartframe.baseui.BaseFragment;
import com.nostra13.universalimageloader.utils.L;

import xnt.com.fun.login.ThirdLoginActivity;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by NetEase on 2016/10/9 0009.
 */
public class NYFragmentProfile extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ny_fragment_profile, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.personal_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ThirdLoginActivity.class);
                startActivity(intent);
//                Intent intent = new Intent(getActivity(), NYLoginActivity.class);
//                startActivity(intent);
            }
        });

        view.findViewById(R.id.version_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upgrade();
            }
        });
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
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
//        request.setMimeType("application/cn.trinea.download.file");
        long downloadId = downloadManager.enqueue(request);
        L.i(TAG,"downloadId: "+downloadId);
    }
}
