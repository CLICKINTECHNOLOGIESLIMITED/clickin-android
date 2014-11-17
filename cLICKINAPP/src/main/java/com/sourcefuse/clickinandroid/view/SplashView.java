package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

//import com.crashlytics.android.Crashlytics;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.result.QBSessionResult;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.listeners.SessionCallback;
import com.quickblox.module.chat.smack.SmackAndroid;
import com.quickblox.module.chat.xmpp.QBPrivateChat;
import com.quickblox.module.users.model.QBUser;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.MyPreference;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

public class SplashView extends Activity implements View.OnClickListener {
    private String TAG = this.getClass().getSimpleName();
    private Button signIn;
    private Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Crashlytics.start(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_splash);

       // SmackAndroid.init(this);
        Log.e(TAG, "" + TAG);
        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        signIn = (Button) findViewById(R.id.signin);
        signUp = (Button) findViewById(R.id.signup);


        // new MyPreference(getApplicationContext()).clearAllPreference();

      /*  if (isLogin()) {
            //if user is already logged in, then set all user values in authmanager from prefrence
            AuthManager authManager = ModelManager.getInstance().getAuthorizationManager();
            SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
            authManager.setUsrToken(preferences.getString("token",null));
            authManager.setPhoneNo(preferences.getString("myPhoneNo", null));
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
            String imgUri=preferences.getString("userimageuri",null);
            if(imgUri!=null)
            authManager.setUserImageUri(Uri.parse(imgUri));
            else
            authManager.setUserImageUri(null);
            loginToQuickBlox();
            Intent intent = new Intent(SplashView.this, UserProfileView.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
        } else {*/
            if (signIn.getVisibility() == View.INVISIBLE && signUp.getVisibility() == View.INVISIBLE) {

                signIn.startAnimation(fadeIn);
                signIn.setVisibility(View.VISIBLE);
                signUp.startAnimation(fadeIn);
                signUp.setVisibility(View.VISIBLE);
            }
     //   }
        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin:
                Intent intent = new Intent(SplashView.this, SignInView.class);
                startActivity(intent);
                finish();
                // this.overridePendingTransition(R.anim.slide_in_right
                // ,R.anim.slide_out_right);
                break;
            case R.id.signup:
                Intent signUp = new Intent(SplashView.this, SignUpView.class);
                startActivity(signUp);
                this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
                break;
        }
    }

    //check whether user is already logged in or not
    public boolean isLogin() {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String token=preferences.getString("token",null);
        if(token!=null)
            return true;
        else
            return false;

    }

    public void loginToQuickBlox() {
        final AuthManager authManager = ModelManager.getInstance().getAuthorizationManager();
        SmackAndroid.init(this);
        com.sourcefuse.clickinandroid.utils.Log.e(TAG, "loginToQuickBlox --- getUserId=>" + authManager.getUserId() + ",--getUsrToken-=>" + authManager.getUsrToken());
        QBSettings.getInstance().fastConfigInit(Constants.CLICKIN_APP_ID, Constants.CLICKIN_AUTH_KEY, Constants.CLICKIN_AUTH_SECRET);
        final QBUser user = new QBUser(authManager.getUserId(), authManager.getUsrToken());

        QBAuth.createSession(user, new QBCallbackImpl() {


            @Override
            public void onComplete(Result result) {
                if (result.isSuccess()) {
                    QBSessionResult res = (QBSessionResult) result;
                    user.setId(res.getSession().getUserId());
                    //
                    QBChatService.getInstance().loginWithUser(user, new SessionCallback() {
                        @Override
                        public void onLoginSuccess() {
                            com.sourcefuse.clickinandroid.utils.Log.e(TAG, "Login successfully");
                            QBChatService.getInstance().startAutoSendPresence(5);
                             QBPrivateChat chat;
                            chat = QBChatService.getInstance().createChat();
                            authManager.setqBPrivateChat(chat);
                        }

                        @Override
                        public void onLoginError(String s) {
                            com.sourcefuse.clickinandroid.utils.Log.e(TAG, "onLoginError");
                            loginToQuickBlox();
                        }


                    });
                    android.util.Log.e(TAG, "Session was successfully created");

                } else {
                    android.util.Log.e(TAG, "Errors " + result.getErrors().toString() + "result" + result);
                }
            }
        });


    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
