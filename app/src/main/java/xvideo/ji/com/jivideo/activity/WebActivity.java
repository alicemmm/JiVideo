package xvideo.ji.com.jivideo.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xvideo.ji.com.jivideo.R;
import xvideo.ji.com.jivideo.utils.JiLog;

public class WebActivity extends BaseActivity {
    private static final String TAG = WebActivity.class.getSimpleName();

    private Context mContext;

    private ChromeClient mWebChromeClient;
    private WebSettings mWebSettings;
    private MyWebViewClient mMyWebViewClient;

    private String mUrl = "";
    private String mHistoryUrl = "";

    private boolean isWebLoaded = false;

    @Bind(R.id.title_tv)
    TextView mTitleTop;

    @Bind(R.id.title_right_ib)
    ImageButton mTitleShare;

    @Bind(R.id.webview_refresh_tv)
    TextView mBottomRefresh;

    @Bind(R.id.webProgress_pb)
    ProgressBar mWebProgressBar;

    @Bind(R.id.webview_title_layout)
    LinearLayout mTitleLl;

    @Bind(R.id.webview_bottom_layout)
    LinearLayout mBottomLl;

    @Bind(R.id.webview)
    WebView mWebView;


    @OnClick(R.id.title_left_ib)
    void onClickFinish() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    @OnClick(R.id.webview_back_tv)
    void onClickWebViewBack() {
        WebBackForwardList webBackForwardList = mWebView.copyBackForwardList();
        if (mWebView.canGoBack()) {
            if (webBackForwardList.getCurrentIndex() > 0) {
                mHistoryUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - 1).getUrl();
                mUrl = mHistoryUrl;
            }
            mWebView.goBack();
        }
    }

    @OnClick(R.id.webview_forward_tv)
    void onClickWebViewForward() {
        WebBackForwardList webBackForwardList = mWebView.copyBackForwardList();
        if (mWebView.canGoForward()) {
            if (webBackForwardList.getCurrentIndex() > 0) {
                mHistoryUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() + 1).getUrl();
                mUrl = mHistoryUrl;
            }
            mWebView.goForward();
        }
    }

    @OnClick(R.id.webview_refresh_tv)
    void onClickWebViewRefresh() {
        if (isWebLoaded) {
            mWebView.loadUrl(mUrl);
        } else {
            mWebView.stopLoading();
        }
    }

    @OnClick(R.id.webview_link_tv)
    void onClickWebViewLink() {
        useOSBrower();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mContext = this;
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");
        mMyWebViewClient = new MyWebViewClient();
        mWebView.setWebViewClient(mMyWebViewClient);

        mWebChromeClient = new ChromeClient();
        mWebView.setWebChromeClient(mWebChromeClient);

        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(false);
        mWebSettings.setDomStorageEnabled(true);

        if (Build.VERSION.SDK_INT >= 19) {
            mWebSettings.setLoadsImagesAutomatically(true);
        } else {
            mWebSettings.setLoadsImagesAutomatically(false);
        }

        mWebSettings.setBlockNetworkImage(true);

        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.requestFocus();

        mWebView.loadUrl(mUrl);

    }

    private void useOSBrower() {
        try {
            Intent gotoIntent = new Intent();
            gotoIntent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(mUrl);
            gotoIntent.setData(content_url);
            startActivity(gotoIntent);
        } catch (Exception e) {
            JiLog.error(TAG, "intent action VIEW failed");
        }
    }

    /**
     * 通过自己的Webview来显示所有网页
     */
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mUrl = url;
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            if (!mWebSettings.getLoadsImagesAutomatically()) {
                mWebSettings.setLoadsImagesAutomatically(true);
            }

            mWebSettings.setBlockNetworkImage(false);

            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
    }

    /**
     * WebChromeClient自定义继承类,获取标题，获得当前进度
     */
    private class ChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                mWebProgressBar.setVisibility(View.GONE);
                isWebLoaded = true;
                mBottomRefresh.setBackgroundResource(R.mipmap.webview_refresh);
            } else {
                mWebProgressBar.setVisibility(View.VISIBLE);
                mWebProgressBar.setProgress(newProgress);
                isWebLoaded = false;
                mBottomRefresh.setBackgroundResource(R.mipmap.webview_stop);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mTitleTop.setText(title);
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
