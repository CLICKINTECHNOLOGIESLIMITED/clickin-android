package com.sourcefuse.clickinandroid.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
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
        if (Utils.mStartExceptionTrack)
            Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog);
        dialog.setCancelable(false);
        TextView Ok = (TextView) dialog.findViewById(R.id.ok);
        TextView Message = (TextView) dialog.findViewById(R.id.alert_msg_dialog);
        TextView Cancel = (TextView) dialog.findViewById(R.id.cancel);
        Message.setText(getString(R.string.application_crash));
        dialog.show();

        /**
         * Cancel button start Splash Activity Directly without sending error report.
         *
         * */
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Intent intent = new Intent(DialogActivity.this, SplashView.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
            }
        });
        /**
         * Ok button used to send error report to client or developer.
         *
         * */
        final String mInfo = getIntent().getStringExtra("mInfo");
        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new Thread(new Runnable() {

                    public void run() {

                        try {

                            GmailCrashLogSender sender = new GmailCrashLogSender(
                                    "tester.sourcefuse@gmail.com",//sender username and password for authentication.
                                    "sourcefuse");
                            sender.sendMail("Error Report", "" + mInfo,
                                    "tester.sourcefuse@gmail.com"//sender email to set sender in mail
                            );

                        } catch (Exception e) {// In case of Exception start Splash View.
                            Intent intent = new Intent(DialogActivity.this, SplashView.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                            finish();

                        }

                    }

                }).start();

                Intent intent = new Intent(DialogActivity.this, SplashView.class);//start splash view
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null)
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        /**
         * Start splash view after sending error report.
         *
         * */

        Intent intent = new Intent(DialogActivity.this, SplashView.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();


    }

    @Override
    public void onBackPressed() {
        /* restrict user to press back button */

    }
}
