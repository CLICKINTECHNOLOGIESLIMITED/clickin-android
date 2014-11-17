package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.SettingManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.ClickInAlertDialog;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;


public class SignInView extends Activity implements View.OnClickListener, TextWatcher {
    private static final String TAG = SignInView.class.getSimpleName();
    private Button do_latter;
    private TextView forgotPwd, signUp;
    private EditText ephone, ePwd,getemailid ;
    private boolean activeDone = false;
    private AuthManager authManager;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_signin);
          getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        authManager = ModelManager.getInstance().getAuthorizationManager();
        Utils.deviceId = Utils.getRegId(SignInView.this);
        authManager.setDeviceRegistereId(Utils.deviceId);
        do_latter = (Button) findViewById(R.id.btn_get_clickin);
        ephone = (EditText) findViewById(R.id.edt_email_phoneno);
        ePwd = (EditText) findViewById(R.id.edt_passwd);
        forgotPwd = (TextView) findViewById(R.id.tv_forgot_pwd);
        signUp = (TextView) findViewById(R.id.tv_signup);
        ephone.addTextChangedListener(this);

        ePwd.addTextChangedListener(this);
        forgotPwd.setOnClickListener(this);
        do_latter.setOnClickListener(this);
        forgotPwd.setOnClickListener(this);
        signUp.setOnClickListener(this);

        ephone.setOnClickListener(this);
        ePwd.setOnClickListener(this);

        /*ephone.setTypeface(typefaceBold);
        ePwd.setTypeface(typefaceBold);
        forgotPwd.setTypeface(typeface);
        signUp.setTypeface(typeface);
        signUp.setTypeface(typeface);*/


        // akshit code for closing keypad if touched anywhere outside
        ((RelativeLayout) findViewById(R.id.relative_layout_root_signin)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ePwd.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(ephone.getWindowToken(), 0);

            }

        });

//ends

//
//            //akshit code for country Code
        try {
            String countryCode = Utils.getCountryCodeFromSim(this);
            if (countryCode == null) {
                ephone.setText("+(null)");
            } else {
                ephone.setText(countryCode);
            }


        } catch (Exception e) {
        }



        ephone.setText("+9100007");
        ephone.setSelection(ephone.getText().toString().length());
        //No need For this akshit


//        ephone.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//           InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//            inputMethodManager.showSoftInput(ephone, 0);
//
//
//                if(ephone.getText().toString().contains("null"))
//                {
//                    if (ephone.getSelectionStart() <= 6) {
//                        return false;
//                    } else {
//                        return true;
//                    }
//                }
//
//                else {
//                    if (ephone.getSelectionStart() <= 2) {
//                        return false;
//                    } else {
//                        return true;
//                    }
//                }
//            }
//        });
//
//    }


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

        if (ephone.getText().toString().length() > 0 && ePwd.getText().toString().length() > 0) {
            activeDone = true;
            do_latter.setBackgroundResource(R.drawable.c_getclicin_active);
//        } else if(ephone.getText().toString().length()==0){
//            Utils.fromSignalDialog(this,AlertMessage.enterPhoneEmail);
//
////            activeDone = false;ep
////            do_latter.setBackgroundResource(R.drawable.c_getclicin_deactive);
//        }
//       }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_clickin:


             /*  ClickInAlertDialog.networkErrorAlert(SignInView.this);*/
                RelativeLayout layout = (RelativeLayout)findViewById(R.id.relative_layout_root_signin);

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);

                if (activeDone && ephone.getText().toString().length() >0 && ephone.getText().toString() != "+null" && ePwd.getText().toString().length()>0) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    authManager = ModelManager.getInstance().getAuthorizationManager();
                    Utils.launchBarDialog(SignInView.this);
                    authManager.signIn(ephone.getText().toString(), ePwd.getText().toString(), authManager.getDeviceRegistereId(), Constants.DEVICETYPE);
                }
                else if(ephone.getText().toString().length() ==0){
                    Utils.fromSignalDialog(this,AlertMessage.enterPhoneEmail);
                }else if(ePwd.getText().toString().length()==0){
                    Utils.fromSignalDialog(this,AlertMessage.enterPassword);
                }

                break;
            case R.id.tv_forgot_pwd:
                forgetPasswordAlert(SignInView.this);
                break;
            case R.id.tv_signup:
                Intent intent = new Intent(SignInView.this, SignUpView.class);
             //   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                break;
        }
    }


    

    private void switchView() {
        Intent intent = new Intent(SignInView.this, UserProfileView.class);
      //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


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

    public void onEventMainThread(String getMsg) {
        Log.d(TAG, "onEventMainThread->" + getMsg);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (getMsg.equalsIgnoreCase("SignIn True")) {
            //save all user values in shared prefrence
            SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("token",authManager.getUsrToken());
            editor.putString("myPhoneNo",authManager.getPhoneNo());
            editor.commit();

          //  new MyPreference(SignInView.this).setToken(authManager.getUsrToken());
            //new MyPreference(SignInView.this).setmyPhoneNo(authManager.getPhoneNo());

            loginToQuickBlox();
            authManager.getProfileInfo("", authManager.getPhoneNo(), authManager.getUsrToken());
        } else if (getMsg.equalsIgnoreCase("SignIn False")) {
            Utils.dismissBarDialog();
           Utils.fromSignalDialog(this,AlertMessage.wrong_signIn_details);
          //  Utils.showAlert(this,AlertMessage.wrong_signIn_details);
            //Utils.showAlert(SignInView.this, authManager.getMessage());
        } else if (getMsg.equalsIgnoreCase("SignIn Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this,AlertMessage.connectionError);
            //Utils.showAlert(act, AlertMessage.connectionError);
        } else if (getMsg.equalsIgnoreCase("ProfileInfo True")) {
            //save values of user in shared prefrence for later use
            SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("gender",authManager.getGender());
            editor.putString("follower",authManager.getFollower());
            editor.putString("following",authManager.getFollowing());
            editor.putString("is_following",authManager.getIsFollowing());
            editor.putString("name",authManager.getUserName());
            editor.putString("user_pic",authManager.getUserPic());
            editor.putString("dob",authManager.getdOB());
            editor.putString("city",authManager.getUserCity());
            editor.putString("country",authManager.getUserCountry());
            editor.putString("email",authManager.getEmailId());
            editor.commit();
           RelationManager relationManager = ModelManager.getInstance().getRelationManager();
            relationManager.getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
           // new ImageDownloadTask().execute();


        } else if (getMsg.equalsIgnoreCase("ProfileInfo False")) {
            Utils.dismissBarDialog();

            Utils.fromSignalDialog(this,authManager.getMessage());
          // Utils.showAlert(SignInView.this, authManager.getMessage());
        } else if (getMsg.equalsIgnoreCase("ProfileInfo Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this,AlertMessage.connectionError);
            //Utils.showAlert(act, AlertMessage.connectionError);
        } else if (getMsg.equalsIgnoreCase("ForgotPassword True")) {
            mDialog.dismiss();
            ClickInAlertDialog.clickInAlert(SignInView.this,authManager.getMessage(),"",false);
            authManager.getMessage();
            Utils.dismissBarDialog();
        } else if (getMsg.equalsIgnoreCase("ForgotPassword False")) {
            Utils.dismissBarDialog();
            mDialog.dismiss();
        } else if (getMsg.equalsIgnoreCase("ForgotPassword Network Error")) {
            mDialog.dismiss();
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this,AlertMessage.connectionError);
         //   Utils.showAlert(act, AlertMessage.connectionError);
        }else if (getMsg.equalsIgnoreCase("GetRelationShips False")) {
            Utils.dismissBarDialog();

//           setLeftMenuList();
            //         setlist();
        } else if(getMsg.equalsIgnoreCase("GetRelationShips Network Error")){
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this,AlertMessage.connectionError);
        }else if(getMsg.equalsIgnoreCase("GetrelationShips True")){
            Utils.dismissBarDialog();
            switchView();
        }

    }



    public void forgetPasswordAlert(Activity contex) {

        // custom dialog
        mDialog = new Dialog(contex);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mDialog.setContentView(R.layout.alert_forgot_password);
        mDialog.setCancelable(false);
        // set the custom dialog components - text, image and button
        TextView text = (TextView) mDialog.findViewById(R.id.text);
        //text.setText("Android custom dialog example!");
        getemailid = (EditText) mDialog.findViewById(R.id.edt_getemailid);

        Button dialogButton = (Button) mDialog.findViewById(R.id.dialog_cancel);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });


        Button continueBtn = (Button) mDialog.findViewById(R.id.dialog_continue);
        // if button is clicked, close the custom dialog
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isEmailValid(getemailid.getText().toString())) {

                    SettingManager settingManager = ModelManager.getInstance().getSettingManager();
                    settingManager.forgotYourPassword(getemailid.getText().toString());
                }else{
                    ClickInAlertDialog.clickInAlert(SignInView.this,"Please enter a valid email address","Error",true);
                }
            }
        });


        mDialog.show();
    }



    public void loginToQuickBlox() {
        SmackAndroid.init(this);
        com.sourcefuse.clickinandroid.utils.Log.e(TAG, "loginToQuickBlox --- getUserId=>" + authManager.getUserId() + ",--getUsrToken-=>" + authManager.getUsrToken());
        QBSettings.getInstance().fastConfigInit(Constants.CLICKIN_APP_ID, Constants.CLICKIN_AUTH_KEY, Constants.CLICKIN_AUTH_SECRET);
        QBSettings.getInstance().setServerApiDomain("apiclickin.quickblox.com");
        QBSettings.getInstance().setContentBucketName("qb-clickin");
        QBSettings.getInstance().setChatServerDomain("chatclickin.quickblox.com");
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

                            QBPrivateChat chat = QBChatService.getInstance().createChat();
                            authManager.setqBPrivateChat(chat);
                        }

                        @Override
                        public void onLoginError(String s) {
                            com.sourcefuse.clickinandroid.utils.Log.e(TAG, "onLoginError");
                            loginToQuickBlox();
                        }


                    });
                    Log.e(TAG, "Session was successfully created");

                } else {
                    Log.e(TAG, "Errors " + result.getErrors().toString() + "result" + result);
                }
            }
        });


    }




}

