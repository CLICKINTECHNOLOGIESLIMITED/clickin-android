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
        if (Utils.mStartExceptionTrack)
            Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));


        /*set picasso maneger value */
        PicassoManager.setPicasso(SplashView.this);
        PicassoManager.clearCache();
        Utils.deviceId = Utils.getRegId(SplashView.this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_splash);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        //check whether we can do auto login or not
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String myPhone = preferences.getString("myPhoneNo", null);
        pwd = preferences.getString("pwd", null);
        String deviceId = preferences.getString("DeviceId", null);

        if (!Utils.isEmptyString(myPhone) && !Utils.isEmptyString(pwd) && !Utils.isEmptyString(deviceId)) {
            Utils.launchBarDialog(this);

            Utils.deviceId = deviceId;
            authManager.setDeviceRegistereId(Utils.deviceId);

            //initialize GPS tracker
            GPSTracker gpsTracker = new GPSTracker(this);

            String latlan = gpsTracker.getLatitude() + ";" + gpsTracker.getLongitude();

            authManager.setLatLan(latlan);

            authManager.signIn(myPhone, pwd, Utils.deviceId, Constants.DEVICETYPE);

        } else {//autologin not possible


            Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

            signIn = (Button) findViewById(R.id.signin);
            signUp = (Button) findViewById(R.id.signup);


            // new MyPreference(getApplicationContext()).clearAllPreference();


            if (signIn.getVisibility() == View.INVISIBLE && signUp.getVisibility() == View.INVISIBLE) {

                signIn.startAnimation(fadeIn);
                signIn.setVisibility(View.VISIBLE);
                signUp.startAnimation(fadeIn);
                signUp.setVisibility(View.VISIBLE);
            }
            //   }
            signIn.setOnClickListener(this);
            signUp.setOnClickListener(this);
        }//
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
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.wrong_signIn_details);
            //  Utils.showAlert(this,AlertMessage.wrong_signIn_details);
            //Utils.showAlert(SignInView.this, authManager.getMessage());
        } else if (getMsg.equalsIgnoreCase("SignIn Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialogSplsh(this, AlertMessage.connectionError);


        } else if (getMsg.equalsIgnoreCase("ProfileInfo True")) {
            //save values of user in shared prefrence for later use
            Intent i = new Intent(this, MyQbChatService.class);
            startService(i);
            RelationManager relationManager = ModelManager.getInstance().getRelationManager();
            relationManager.getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
            // new ImageDownloadTask().execute();


        } else if (getMsg.equalsIgnoreCase("ProfileInfo False")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, authManager.getMessage());
            // Utils.showAlert(SignInView.this, authManager.getMessage());
        } else if (getMsg.equalsIgnoreCase("ProfileInfo Network Error")) {
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
        Utils.dismissBarDialog();
        Intent intent = new Intent(this, UserProfileView.class);
        //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
