package com.sourcefuse.clickinandroid.view;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;


public class PlayItSafeView extends Activity implements View.OnClickListener, TextWatcher {
    private static final String TAG = PlayItSafeView.class.getSimpleName();
    // private String emailid ,phone,userToken;
    public static Activity act;
    public static Context context;
    //we need pwd to hit sign in webservice, so that we can userqbid-monika
    String pwd = null, repwd = null;
    private Button done;
    private EditText password, rePassword;
    private AuthManager authManager;
    private Typeface typeface, typefaceBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        //code- to handle uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));


        setContentView(R.layout.view_playitsafe);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        typeface = Typeface.createFromAsset(PlayItSafeView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
        typefaceBold = Typeface.createFromAsset(PlayItSafeView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_BOLD);


        Utils.deviceId = Utils.getRegId(PlayItSafeView.this);

        done = (Button) findViewById(R.id.btn_done_play);
        password = (EditText) findViewById(R.id.edt_password);
        rePassword = (EditText) findViewById(R.id.edt_re_password);
        password.addTextChangedListener(this);
        rePassword.addTextChangedListener(this);

        password.setTypeface(typefaceBold);
        rePassword.setTypeface(typefaceBold);

        done.setOnClickListener(this);

//        Utils.prefrences = getSharedPreferences(context.getString(R.string.PREFS_NAME), MODE_PRIVATE);
//        phone = Utils.prefrences.getString(Constants.PREFS_VALUE_PHONE, "");
//        userToken = Utils.prefrences.getString(Constants.PREFS_VALUE_USER_TOKEN, "");
//        emailid =Utils.prefrences.getString(Constants.PREFS_VALUE_USER_EMAILID, "");


        ((RelativeLayout) findViewById(R.id.rl_playitsafe_action)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(rePassword.getWindowToken(), 0);

            }

        });


    }


    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (password.getText().toString().length() > 0 || rePassword.getText().toString().length() > 0) {
            done.setBackgroundResource(R.drawable.done_blue);
        } else {
            done.setBackgroundResource(R.drawable.done_rounded_light);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void onEventMainThread(String message) {

        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (message.equalsIgnoreCase("PlayItSafe True")) {
            //monika-code to fetch Qb id as we are not getting it while verifycode service
            authManager.signIn(authManager.getPhoneNo(), pwd, Utils.deviceId, Constants.DEVICETYPE);


        } else if (message.equalsIgnoreCase("PlayItSafe False")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, authManager.getMessage());

            //	Utils.showAlert(PlayItSafeView.this, authManager.getMessage());
        } else if (message.equalsIgnoreCase("PlayItSafe Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
        } else if (message.equalsIgnoreCase("CheckFriend True")) {
            Utils.dismissBarDialog();
            switchView();

        } else if (message.equalsIgnoreCase("CheckFriend False")) {
            Utils.dismissBarDialog();
            //  Utils.showAlert(this,authManager.getMessage());
            // Utils.fromSignalDialog(this, authManager.getMessage());
            switchView();
        } else if (message.equalsIgnoreCase("CheckFriend Network Error")) {
            Utils.dismissBarDialog();
            //    Utils.showAlert(this, AlertMessage.connectionError);
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
        } else if (message.equalsIgnoreCase("SignIn True")) {
            //temp code-monika
            //   new FetchContactFromPhone(this).getClickerList(authManager.getPhoneNo(), authManager.getUsrToken(), 1);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("myPhoneNo", authManager.getPhoneNo());
            editor.putString("pwd", pwd);
            editor.putString("DeviceId", Utils.deviceId);
            //  editor.putString("DeviceType",Constants.DEVICETYPE);
            editor.commit();
            switchView();
        } else if (message.equalsIgnoreCase("SignIn False")) {
            //monika-code to fetch Qb id as we are not getting it while verifycode service
            Utils.dismissBarDialog();
            //  Utils.showAlert(this,authManager.getMessage());
            // Utils.fromSignalDialog(this, authManager.getMessage());
            switchView();
        } else if (message.equalsIgnoreCase("SignIn Network Error")) {
            //monika-code to fetch Qb id as we are not getting it while verifycode service
            Utils.dismissBarDialog();
            //    Utils.showAlert(this, AlertMessage.connectionError);
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
        }
    }

    private void switchView() {
        Intent intent = new Intent(PlayItSafeView.this, AddSomeoneView.class);
        intent.putExtra("fromsignup", true);
        /*intent.putExtra("FromOwnProfile", false);*/
        startActivity(intent);
        this.finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_done_play:

                pwd = password.getText().toString().trim();
                repwd = rePassword.getText().toString().trim();
                if (pwd.length() > 7) {
                    if (pwd.equals(repwd)) {
                        Utils.launchBarDialog(this);
                        authManager = ModelManager.getInstance().getAuthorizationManager();
                        authManager.playItSafeAuth(pwd, authManager.getPhoneNo(), authManager.getEmailId(), authManager.getUsrToken());
                    } else {
                        Utils.fromSignalDialog(this, AlertMessage.MATCHPASSWORD);
                        //    Utils.showAlert(PlayItSafeView.this, AlertMessage.MATCHPASSWORD);
                    }
                } else {
                    Utils.fromSignalDialog(this, AlertMessage.PASSWORDLENGHT);
                    //Utils.showAlert(PlayItSafeView.this, AlertMessage.PASSWORDLENGHT);
                }
                break;
        }
    }

}

