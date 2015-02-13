package com.sourcefuse.clickinandroid.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

/**
 * Created by prafull on 20/9/14.
 */
public class PrivacyView extends Activity implements View.OnClickListener {
    WebView mprivacywebview;
    private Typeface typefaceBold;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        //code- to handle uncaught exception
        if(Utils.mStartExceptionTrack)
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));



        setContentView(R.layout.view_privacy_layout);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        mprivacywebview = (WebView) findViewById(R.id.web_privacy_view);
        typefaceBold = Typeface.createFromAsset(PrivacyView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_BOLD);
        ((TextView) findViewById(R.id.tv_profile_txt)).setTypeface(typefaceBold);
        findViewById(R.id.iv_back_noti).setOnClickListener(this);
        mprivacywebview.setWebViewClient(new MyBrowser());
        mprivacywebview.getSettings().setLoadsImagesAutomatically(true);
        mprivacywebview.getSettings().setAppCacheEnabled(false);
        mprivacywebview.getSettings().setAllowContentAccess(true);
        mprivacywebview.getSettings().setLoadWithOverviewMode(true);
        mprivacywebview.getSettings().setUseWideViewPort(true);
        mprivacywebview.getSettings().setJavaScriptEnabled(true);
        mprivacywebview.getSettings().setPluginState(WebSettings.PluginState.ON);
        mprivacywebview.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U;`Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, likeGecko) Version/4.0 Mobile Safari/530.17");
        mprivacywebview.loadUrl(Constants.PRIVACY_LINK_URL);

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
            Utils.launchBarDialog(PrivacyView.this);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Utils.dismissBarDialog();

        }
    }
}
