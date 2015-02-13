package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

/**
 * Created by prafull on 20/9/14.
 */
public class TermUseView extends Activity implements View.OnClickListener {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //code- to handle uncaught exception
        if(Utils.mStartExceptionTrack)
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

        setContentView(R.layout.view_term_use);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        findViewById(R.id.iv_back_noti).setOnClickListener(this);

        webView = (WebView) findViewById(R.id.web_term_view);
        webView.getSettings().setAppCacheEnabled(false);
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U;`Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, likeGecko) Version/4.0 Mobile Safari/530.17");
        webView.loadUrl(Constants.TERMS_LINK_URL);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_noti:
                finish();
                overridePendingTransition(0, R.anim.top_out);//akshit code for animation
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.top_out);//akshit code for animation
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Utils.launchBarDialog(TermUseView.this);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Utils.dismissBarDialog();

        }
    }


}
