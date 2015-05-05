package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.PicassoManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.services.MyQbChatService;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.GPSTracker;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import java.io.InputStream;
import java.util.List;

import de.greenrobot.event.EventBus;


public class WaitingView extends Activity implements View.OnClickListener {
    private String TAG = this.getClass().getSimpleName();
    private Button btnViaSMS;
    private Button btnViaWhatsapp;
    private ImageView ivWaitingHeart;


    public static Activity act;
    public static Context context;
    private Dialog dialog;
    private AuthManager authManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_waiting);

        btnViaSMS       = (Button) findViewById(R.id.btnInviteViaSMS);
        btnViaWhatsapp  = (Button) findViewById(R.id.btnInviteViaWhatsapp);
        ivWaitingHeart  = (ImageView) findViewById(R.id.ivWaitingHeart);

        btnViaSMS.setOnClickListener(this);
        btnViaWhatsapp.setOnClickListener(this);

        authManager = ModelManager.getInstance().getAuthorizationManager();
        Log.d(TAG, "PhoneNo->" + authManager.getPhoneNo() + " PartnerNo-->" + authManager.getPartnerNo());
        authManager.getPartnerStatus(authManager.getPhoneNo().toString(), authManager.getPartnerNo().toString());

        Animation newsAnim= AnimationUtils.loadAnimation(this, R.anim.heart_beat);
        //newsAnim.reset();  // reset initialization state
        //newsAnim.setRepeatMode(Animation.RESTART);
        //newsAnim.setRepeatCount(Animation.INFINITE); // Or a number of times
        ImageView animatedText = (ImageView) findViewById(R.id.ivWaitingHeart);
        animatedText.startAnimation(newsAnim);

        if (isPartnerActive()) {

            /*
            if (signIn.getVisibility() == View.INVISIBLE && signUp.getVisibility() == View.INVISIBLE) {
                signIn.startAnimation(fadeIn);
                signIn.setVisibility(View.GONE);
                signUp.startAnimation(fadeIn);
                signUp.setVisibility(View.VISIBLE);
            }
            */

        } else {

            // Do nothing as the the waiting screen is already

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInviteViaSMS:
                try {
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.putExtra("sms_body", Constants.SEND_REQUEST_WITH_SMS_MESSAGE);
                    smsIntent.putExtra("address", authManager.getPartnerNo());
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    startActivity(smsIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "Some error occured please try sending an SMS manually.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnInviteViaWhatsapp:
                try {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, Constants.SEND_REQUEST_WITH_SMS_MESSAGE);
                    sendIntent.setPackage("com.whatsapp");
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //check whether user is already logged in or not
    public boolean isPartnerActive() {
        //SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        //String token=preferences.getString("token", null);
        //com.sourcefuse.clickinandroid.utils.Log.e(TAG,"Token----"+ token);
        //if(token!=null)
        //    return true;
        //else
        return false;
    }

    private void switchView() {
        Intent intent = new Intent(WaitingView.this, UserProfileView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    public void onEventMainThread(String getMsg){
        Log.d(TAG, "onEventMainThread->" + getMsg);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (getMsg.equalsIgnoreCase("Partner True")) {
            Utils.dismissBarDialog();

            Log.d(TAG, "onEventMainThread-> Partner True. Setting");

            //ToDO: Add verfied check on partner later
            //Partner found, now settting preferences
            SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("partnerStatus", "active");
            editor.commit();

            // Loading icon
            Utils.launchBarDialog(WaitingView.this);

            // If partner is found, it sends a reuqest automatically
            // The request will be automatically accepted
            // Will also send a notification
            authManager.sendNewRequest(authManager.getPhoneNo(), authManager.getPartnerNo(), authManager.getUsrToken());

        } else if (getMsg.equalsIgnoreCase("Partner False")) {
            Utils.dismissBarDialog();
            Log.d(TAG, "onEventMainThread-> Partner False");
        } else if (getMsg.equalsIgnoreCase("Partner Network Error")) {
            Utils.dismissBarDialog();
            Utils.showAlert(act, AlertMessage.connectionError);
            Log.d(TAG, "onEventMainThread-> Partner Connection Error");

        // Request sent
        } else if (getMsg.equalsIgnoreCase("RequestSend True")){

            switchView();

        } else if (getMsg.equalsIgnoreCase("RequestSend False")){

            Utils.dismissBarDialog();
            Utils.showAlert(act, AlertMessage.connectionError);
            Log.d(TAG, "onEventMainThread-> RequestSend False");

        } else {
            Utils.dismissBarDialog();
            Utils.showAlert(act, AlertMessage.connectionError);
            Log.d(TAG, "onEventMainThread-> Partner Connection Error");
        }
    }


}
