package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
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

import de.greenrobot.event.EventBus;


public class SplashView extends Activity implements View.OnClickListener {
    AuthManager authManager = ModelManager.getInstance().getAuthorizationManager();
    String pwd = null;
    private String TAG = this.getClass().getSimpleName();
    private Button signIn;
    private Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //code- to handle uncaught exception
        if(Utils.mStartExceptionTrack)
            Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_splash);
        authManager = ModelManager.getInstance().getAuthorizationManager();

        //autologin not possible
        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        signIn = (Button) findViewById(R.id.signin);
        signUp = (Button) findViewById(R.id.signup);

        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);


        /*set picasso maneger value */
        PicassoManager.setPicasso(SplashView.this);
        PicassoManager.clearCache();

        Utils.deviceId = Utils.getRegId(SplashView.this);

        //check whether we can do auto login or not
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Setting QuickBlox Id which
        authManager.setQBId(preferences.getString("QBId", null));
        authManager.setPhoneNo(preferences.getString("myPhoneNo", null));
        authManager.setPartnerNo(preferences.getString("myPartnerNo", null));

        authManager.setUserId(preferences.getString("userId", null));
        authManager.setUsrToken(preferences.getString("token", null));
        authManager.setGender(preferences.getString("gender", null));
        authManager.setFollower(preferences.getString("follower", null));
        authManager.setFollowing(preferences.getString("following", null));
        authManager.setIsFollowing(preferences.getString("is_following", null));
        authManager.setUserName(preferences.getString("name", null));
        authManager.setUserPic(preferences.getString("user_pic", null));
        authManager.setdOB(preferences.getString("dob", null));
        authManager.setUserCity(preferences.getString("city", null));
        authManager.setUserCountry(preferences.getString("country", null));
        authManager.setEmailId(preferences.getString("email", null));
        //authManager.setEmailId(preferences.getString("email", "pal.himanshu1991@gmail.com"));
        String imgUri=preferences.getString("userimageuri",null);
        String partnerStatus = preferences.getString("partnerStatus", null);
        pwd = preferences.getString("pwd", null);

        if (isLogin()) {
            Utils.launchBarDialog(this);

            authManager.setDeviceRegistereId(Utils.deviceId);

            //initialize GPS tracker
            //GPSTracker gpsTracker = new GPSTracker(this);
            //String latlan = gpsTracker.getLatitude() + ";" + gpsTracker.getLongitude();
            //authManager.setLatLan(latlan);

            if(isWaiting()) {
                // Log the user in
                authManager.signIn(authManager.getPhoneNo(), pwd, Utils.deviceId, Constants.DEVICETYPE);
            } else {
                showWaitingScreen();
            }

        } else {


        }
    }

    //check whether user is already logged in or not
    public boolean isLogin() {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String token=preferences.getString("token", null);
        Log.e(TAG,"Token----> " + token);
        if(token!=null)
            return true;
        else
            return false;
    }

    //Check whether partner is active or not
    public boolean isWaiting(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String status = preferences.getString("partnerStatus", null);
        Log.e(TAG,"partnerStatus--->" + status);
        if(status!=null)
            return true;
        else
            return false;
    }

    public void showWaitingScreen(){
        Intent waiting = new Intent(SplashView.this, WaitingView.class);
        startActivity(waiting);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin:
                Intent intent = new Intent(SplashView.this, SignInView.class);
                startActivity(intent);
                finish();
                //To track through mixPanel.
                //Click on SignIn Button
                Utils.trackMixpanel(SplashView.this, "", "", "SignInButtonClicked", false);
                // this.overridePendingTransition(R.anim.slide_in_right
                // ,R.anim.slide_out_right);
                break;
            case R.id.signup:
                Intent signUp = new Intent(SplashView.this, SignUpView.class);
                startActivity(signUp);
                this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                //To track through mixPanel.
                //Click To SignUp.
                Utils.trackMixpanel(SplashView.this, "", "", "SignUpButtonClicked", false);
                finish();
                break;
        }
    }


    public void onEventMainThread(String getMsg) {

        if (getMsg.equalsIgnoreCase("SignIn True")) {
            Log.e(TAG,"SignIn True");
            //save only those values in sharedprefrence that required to sing in
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("myPhoneNo", authManager.getPhoneNo());
            editor.putString("pwd", pwd);
            editor.putString("DeviceId", Utils.deviceId);
            editor.putString("authToken", ModelManager.getInstance().getAuthorizationManager().getUsrToken());
            editor.putString("userid", ModelManager.getInstance().getAuthorizationManager().getUserId());
            //  editor.putString("DeviceType",Constants.DEVICETYPE);
            editor.commit();

            authManager.getProfileInfo("", authManager.getPhoneNo(), authManager.getUsrToken());

        } else if (getMsg.equalsIgnoreCase("SignIn False")) {
            Log.e(TAG,"SignIn False");
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.wrong_signIn_details);
            //  Utils.showAlert(this,AlertMessage.wrong_signIn_details);
            //Utils.showAlert(SignInView.this, authManager.getMessage());
        } else if (getMsg.equalsIgnoreCase("SignIn Network Error")) {
            Log.e(TAG,"SignIn Network Error");
            Utils.dismissBarDialog();
            Utils.fromSignalDialogSplsh(this, AlertMessage.connectionError);
        } else if (getMsg.equalsIgnoreCase("ProfileInfo True")) {
            //save values of user in shared prefrence for later use
            Log.e(TAG,"ProfileInfo True");
            RelationManager relationManager = ModelManager.getInstance().getRelationManager();
            relationManager.getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
            // new ImageDownloadTask().execute();


        } else if (getMsg.equalsIgnoreCase("ProfileInfo False")) {
            Log.e(TAG,"ProfileInfo False");
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, authManager.getMessage());
            // Utils.showAlert(SignInView.this, authManager.getMessage());
        } else if (getMsg.equalsIgnoreCase("ProfileInfo Network Error")) {
            Log.e(TAG,"ProfileInfoNetwork Error");
            Utils.dismissBarDialog();
            Utils.fromSignalDialogSplsh(this, AlertMessage.connectionError);


        } else if (getMsg.equalsIgnoreCase("GetRelationShips False")) {
            if (authManager == null)
                authManager = ModelManager.getInstance().getAuthorizationManager();
            if (authManager.getUserPic() != null)
                new DownloadImage().execute(authManager.getUserPic());
            else {
                Utils.dismissBarDialog();
                switchView();
            }

        } else if (getMsg.equalsIgnoreCase("GetRelationShips Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialogSplsh(this, AlertMessage.connectionError);

        } else if (getMsg.equalsIgnoreCase("GetrelationShips True")) {
            Intent i = new Intent(this, MyQbChatService.class);
            startService(i);

            if (authManager == null)
                authManager = ModelManager.getInstance().getAuthorizationManager();
            if (authManager.getUserPic() != null)
                new DownloadImage().execute(authManager.getUserPic());
            else {
                Utils.dismissBarDialog();
                switchView();
            }
        }

    }



    /*public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/


    /* downoad image from server */

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);

    }

    private void switchView() {
        Log.e(TAG,"Switching To UserProfileView");
        Utils.dismissBarDialog();
        Intent intent = new Intent(this, UserProfileView.class);
        startActivity(intent);
        finish();
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {

                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap

                bitmap = BitmapFactory.decodeStream(input);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            String mPath = "";
            if (result != null)
                mPath = Utils.storeImage(result, "" + ModelManager.getInstance().getAuthorizationManager().getUserId(), SplashView.this);

            if (!Utils.isEmptyString("" + mPath))
                ModelManager.getInstance().getAuthorizationManager().setUserImageUri(Uri.parse(mPath));
            if (result != null)
                ModelManager.getInstance().getAuthorizationManager().setUserbitmap(result);
            Utils.dismissBarDialog();
            switchView();

        }
    }

}
