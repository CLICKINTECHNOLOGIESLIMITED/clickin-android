package com.sourcefuse.clickinandroid.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.sourcefuse.clickinapp.R;

/**
 * Created by prafull on 9/2/15.
 */
public class DialogActivity extends Activity {

    int RESULT = 1;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_activity);
        setFinishOnTouchOutside(false);
        TextView alert_msgI = (TextView) findViewById(R.id.alert_msg_dialog);
        alert_msgI.setText(getString(R.string.application_crash));
        findViewById(R.id.coolio_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DialogActivity.this, SplashView.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
            }
        });

        final String mInfo = getIntent().getStringExtra("mInfo");
        findViewById(R.id.coolio_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] TO = {"monika.bindal@sourcefuse.com"};
                String[] CC = {"prafull.singh@sourcefuse.com"};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_CC, CC);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Error Report");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "" + mInfo);
                try {
                    startActivityForResult(Intent.createChooser(emailIntent, "Send mail..."), RESULT);

                } catch (android.content.ActivityNotFoundException ex) {

                    Log.e("exception--->", "" + ex.toString());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("in onactivity result", "in onactivity result");

        try {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(DialogActivity.this, SplashView.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();


    }

    @Override
    public void onBackPressed() {
    }
}
