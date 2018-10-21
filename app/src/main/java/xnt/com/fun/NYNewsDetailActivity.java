package xnt.com.fun;

import android.app.ActionBar;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.basesmartframe.baseui.BaseActivity;
import com.sf.loglib.L;
import com.sf.utils.baseutil.NetWorkManagerUtil;
import com.sflib.CustomView.newhttpview.HttpViewManager;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import xnt.com.fun.bean.NewsDetail;

/**
 * Created by NetEase on 2016/10/10 0010.
 */
public class NYNewsDetailActivity extends BaseActivity {
    public static final String NEWS_ID = "news_id";
    private WebView mWebView;
    private FrameLayout mErrorFl;
    private HttpViewManager mHttpViewManager;
    private TextView mTitleTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ny_topic_detail);
        initActionBar();
        initViews();
        if (NetWorkManagerUtil.isNetworkAvailable()) {
            mHttpViewManager.showHttpLoadingView(false);
            doRequest();
        } else {
            mHttpViewManager.showHttpViewNoNetwork(false);
        }
    }
    private void initActionBar() {
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
        View actionView = LayoutInflater.from(this).inflate(R.layout.ny_home_title,null);
        getActionBar().setCustomView(actionView,params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toolbar parent = (Toolbar) actionView.getParent();
            parent.setContentInsetsAbsolute(0, 0);
        }
    }

    private void updateActionBar() {
        View rootView = getActionBar().getCustomView();
        rootView.setBackgroundColor(getResources().getColor(R.color.white));
        ImageView logoIv = (ImageView) rootView.findViewById(R.id.ny_logo);
        logoIv.setImageResource(R.drawable.app_icon);
    }



    private void initViews() {
        mWebView = (WebView) findViewById(R.id.webview);
        mErrorFl = (FrameLayout) findViewById(R.id.error_fl);
        mTitleTv = (TextView) findViewById(R.id.news_detail_title_tv);
        mHttpViewManager = HttpViewManager.createManagerByDefault(this, mErrorFl);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setBackgroundColor(0);
        mWebView.setBackgroundResource(R.drawable.transparent);

        webSettings.setBuiltInZoomControls(false);
        webSettings.setTextZoom(350);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        mWebView.clearFocus();
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.setVisibility(View.VISIBLE);

            }

        });

        mWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            }
        });

    }


    private void doRequest() {
        String newsId = getNewsId();
        BmobQuery<NewsDetail> newsDetail = new BmobQuery<>();
        newsDetail.addWhereEqualTo("newsId",newsId);
        newsDetail.findObjects(new FindListener<NewsDetail>() {
            @Override
            public void done(List<NewsDetail> list, BmobException e) {
                if (e == null){
                    if (list != null && list.size() > 0) {
                        NewsDetail detail = list.get(0);
                        if (detail != null) {
                            String detailUrl = getDetailUrl(detail);
                            if (!TextUtils.isEmpty(detailUrl)){
                                mWebView.loadUrl(detailUrl);
                                mHttpViewManager.dismissAllHttpView();
                                return;
                            }
                        }
                    }
                } else {
                    L.error("bmob","失败 exception: "+e);
                }
                mHttpViewManager.showHttpViewNOData(false);
            }

            private String getDetailUrl(NewsDetail detail) {
                String detailUrl = "";
                if (detail.newsDetail != null) {
                    detailUrl = detail.newsDetail.getUrl();
                }
                if (TextUtils.isEmpty(detailUrl)){
                    detailUrl = detail.newEtraUrl;
                }
                return detailUrl;
            }
        });
    }

    private String getNewsId() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getStringExtra(NEWS_ID);
        }
        return "";
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }

}
