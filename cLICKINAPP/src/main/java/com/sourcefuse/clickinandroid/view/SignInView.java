package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.sourcefuse.clickinandroid.services.QbChatService;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.ClickInAlertDialog;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.MyPreference;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import org.jivesoftware.smack.ConnectionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import de.greenrobot.event.EventBus;


public class SignInView extends Activity implements View.OnClickListener, TextWatcher {
    private static final String TAG = SignInView.class.getSimpleName();
    private Button do_latter;
    private TextView forgotPwd, signUp;
    private EditText ephone, ePwd,getemailid ;
    public static Activity act;
    public static Context context;

    private boolean activeDone = false;
    private AuthManager authManager;
    private QBPrivateChat chat;
    private RelationManager mRelationManager;

    private SettingManager settingManager;

    private Typeface typeface, typefaceBold;
    private Dialog mDialog;
    public Thread myThread;
    boolean gotoProfile = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_signin);

        act = this;
        context = this;
        typeface = Typeface.createFromAsset(SignInView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
        typefaceBold = Typeface.createFromAsset(SignInView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_BOLD);
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




        ephone.setTypeface(typefaceBold);
        ePwd.setTypeface(typefaceBold);
        forgotPwd.setTypeface(typeface);
        signUp.setTypeface(typeface);
        signUp.setTypeface(typeface);

        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String cCode = manager.getNetworkCountryIso();
            TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String networkOperator = tel.getNetworkOperator();
            int mcc = 0, mnc = 0;
            if (networkOperator != null) {
                mcc = Integer.parseInt(networkOperator.substring(0, 3));
                mnc = Integer.parseInt(networkOperator.substring(3));
            }
            Log.e(TAG, "MCC : " + mcc + "\n" + "MNC : " + mnc + "-000-" + networkOperator);
            String cCodecode = context.getResources().getConfiguration().locale.getCountry();
            Locale loc = new Locale(cCodecode);
            String cCodeN = getApplicationContext().getResources().getConfiguration().locale.getCountry(); // get country code
            Log.e(TAG, "cCode" + cCode + "-cCodeN -" + cCodeN + "-" + getUserCountry(context) + "---" + loc.getISO3Country() + "--->" + cCodecode);

        } catch (Exception e) {
        }


    }


    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        } catch (Exception e) {
        }
        return null;
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
        } else {
            activeDone = false;
            do_latter.setBackgroundResource(R.drawable.c_getclicin_deactive);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_clickin:

             /*  ClickInAlertDialog.networkErrorAlert(SignInView.this);*/

                if (activeDone) {
                    authManager = ModelManager.getInstance().getAuthorizationManager();
                    Utils.launchBarDialog(SignInView.this);
                    authManager.signIn(ephone.getText().toString(), ePwd.getText().toString(), authManager.getDeviceRegistereId(), Constants.DEVICETYPE);
                }
                break;
            case R.id.tv_forgot_pwd:
                forgetPasswordAlert(SignInView.this);
                break;
            case R.id.tv_signup:
                Intent intent = new Intent(SignInView.this, SignUpView.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                break;
        }
    }


    

    private void switchView() {
        Intent intent = new Intent(SignInView.this, UserProfileView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
            Utils.showAlert(SignInView.this, authManager.getMessage());
        } else if (getMsg.equalsIgnoreCase("SignIn Network Error")) {
            Utils.dismissBarDialog();
            Utils.showAlert(act, AlertMessage.connectionError);
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
            new ImageDownloadTask().execute();

        } else if (getMsg.equalsIgnoreCase("ProfileInfo False")) {
            Utils.dismissBarDialog();
            Utils.showAlert(SignInView.this, authManager.getMessage());
        } else if (getMsg.equalsIgnoreCase("ProfileInfo Network Error")) {
            Utils.dismissBarDialog();
            Utils.showAlert(act, AlertMessage.connectionError);
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
            Utils.showAlert(act, AlertMessage.connectionError);
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

                    settingManager = ModelManager.getInstance().getSettingManager();
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

                            chat = QBChatService.getInstance().createChat();
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

    private  class ImageDownloadTask extends AsyncTask<Void,Void,File> {
        File file;
        @Override
        protected File doInBackground( Void... params ) {
            InputStream is = null;

           file = new File(context.getFilesDir().getAbsolutePath() + "/userpic1.jpg");
            try { 
                URL url = new URL(authManager.getUserPic());
            /* Open a connection to that URL. */
                URLConnection ucon = url.openConnection();

            /*
             * Define InputStreams to read from the URLConnection.
             */
                is = ucon.getInputStream();
              //  FileOutputStream fOut = openFileOutput("uerpic.jpg",MODE_WORLD_READABLE);
                OutputStream os = new FileOutputStream(file) ;
                byte [ ] data = new byte [ is.available ( ) ] ;
                is.read ( data ) ; os.write (data );is.close ( ) ; os.close ( ) ;
                return file;
            }
            catch (Exception e){
                Log .d ( "ImageManager " , " Error: " + e ) ;
            }

            return null;
        }
        protected void onPostExecute (File file) {
         /*   try{
                MediaScannerConnection.scanFile(null, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i(" External Storage", " Scanned " + path + " : ");
                        Log.i(" E x t e r n a l S t o r a g e ", " - > u r i = " + uri);
                        authManager.setUserImageUri(uri);
                    }
                }) ;
            }catch (Exception e) {
                e.printStackTrace();
                // TODO: handle exception
            }*/
            Uri uri=Uri.fromFile(file);
            try {
                Bitmap imageBitmap = Utils.decodeUri(uri, SignInView.this);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            authManager.setUserImageUri(uri);
            SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor=preferences.edit();
            String uriStr=uri.toString();
            editor.putString("userimageuri",uriStr);
            editor.commit();
            switchView();
        }}

}

