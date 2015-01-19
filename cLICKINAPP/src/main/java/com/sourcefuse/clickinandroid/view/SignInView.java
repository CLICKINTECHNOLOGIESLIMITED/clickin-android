package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.PicassoManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.SettingManager;
import com.sourcefuse.clickinandroid.services.MyQbChatService;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.GPSTracker;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import java.io.InputStream;

import de.greenrobot.event.EventBus;


public class SignInView extends Activity implements View.OnClickListener, TextWatcher {
    private static final String TAG = SignInView.class.getSimpleName();
    public MyQbChatService myQbChatService;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            myQbChatService = ((MyQbChatService.LocalBinder) service).getService();
           /* myQbChatService.createRoom(mRoomName);*/

            // showMessages();

            // Tell the user about this for our demo.
//            Toast.makeText(Binding.this, R.string.local_service_connected,
//                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            myQbChatService = null;
//            Toast.makeText(Binding.this, R.string.local_service_disconnected,
//                    Toast.LENGTH_SHORT).show();
        }
    };
    private Button do_latter;
    private TextView forgotPwd, signUp;
    private EditText ephone, ePwd, getemailid;
    private boolean activeDone = false;
    private AuthManager authManager;
    private Dialog mDialog;
    // private Typeface typeface, typefaceBold;
    private boolean mIsBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_signin);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        /*set picasso maneger value */
        PicassoManager.setLruCache(getApplicationContext());
        PicassoManager.setPicasso(getApplicationContext(), PicassoManager.getLruCache());




        authManager = ModelManager.getInstance().getAuthorizationManager();
        try {
            GPSTracker gpsTracker = new GPSTracker(this);
            String latlan = gpsTracker.getLatitude() + ";" + gpsTracker.getLongitude();
            authManager.setLatLan(latlan);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);


        Utils.deviceId = Utils.getRegId(SignInView.this);
        authManager.setDeviceRegistereId(Utils.deviceId);
        do_latter = (Button) findViewById(R.id.btn_get_clickin_signin);
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


//        forgotPwd.setTypeface(typeface);
//        signUp.setTypeface(typeface);
//        signUp.setTypeface(typeface);

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


        ephone.setSelection(ephone.getText().toString().length());
        //No need. For this akshit




        /* to save default lat and lng */


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
            case R.id.btn_get_clickin_signin:


             /*  ClickInAlertDialog.networkErrorAlert(SignInView.this);*/
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.relative_layout_root_signin);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
                if (activeDone && ephone.getText().toString().trim().length() > 0 && ephone.getText().toString() != "+(null)" && ePwd.getText().toString().length() > 0) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    authManager = ModelManager.getInstance().getAuthorizationManager();
                    Utils.launchBarDialog(SignInView.this);
                    authManager.signIn(ephone.getText().toString().trim(), ePwd.getText().toString().trim(), Utils.deviceId, Constants.DEVICETYPE);
                } else if (ephone.getText().toString().length() == 0) {
                    Utils.fromSignalDialog(this, AlertMessage.enterPhoneEmail);
                } else if (ePwd.getText().toString().length() == 0) {
                    Utils.fromSignalDialog(this, AlertMessage.enterPassword);
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
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (getMsg.equalsIgnoreCase("SignIn True")) {
            //save only those values in sharedprefrence that required to sign in
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("myPhoneNo", authManager.getPhoneNo());
            editor.putString("pwd", ePwd.getText().toString().trim());
            editor.putString("DeviceId", Utils.deviceId);
            editor.putString("authToken", ModelManager.getInstance().getAuthorizationManager().getUsrToken());
            editor.putString("userid", ModelManager.getInstance().getAuthorizationManager().getUserId());
            //  editor.putString("DeviceType",Constants.DEVICETYPE);
            editor.commit();


            //  new MyPreference(SignInView.this).setToken(authManager.getUsrToken());
            //new MyPreference(SignInView.this).setmyPhoneNo(authManager.getPhoneNo());
            //  authManager.getProfileInfo("", authManager.getPhoneNo(), authManager.getUsrToken());

            //loginToQuickBlox();
            authManager.getProfileInfo("", authManager.getPhoneNo(), authManager.getUsrToken());
        } else if (getMsg.equalsIgnoreCase("SignIn False")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.wrong_signIn_details);
            //  Utils.showAlert(this,AlertMessage.wrong_signIn_details);
            //Utils.showAlert(SignInView.this, authManager.getMessage());
        } else if (getMsg.equalsIgnoreCase("SignIn Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
            //Utils.showAlert(act, AlertMessage.connectionError);
        } else if (getMsg.equalsIgnoreCase("ProfileInfo True")) {
            //save values of user in shared prefrence for later use

     /*       SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("gender", authManager.getGender());
            editor.putString("follower", authManager.getFollower());
            editor.putString("following", authManager.getFollowing());
            editor.putString("is_following", authManager.getIsFollowing());
            editor.putString("name", authManager.getUserName());
            editor.putString("user_pic", authManager.getUserPic());
            editor.putString("dob", authManager.getdOB());
            editor.putString("city", authManager.getUserCity());
            editor.putString("country", authManager.getUserCountry());
            editor.putString("email", authManager.getEmailId());
            editor.commit();*/
            RelationManager relationManager = ModelManager.getInstance().getRelationManager();
            relationManager.getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
            // new ImageDownloadTask().execute();


        } else if (getMsg.equalsIgnoreCase("ProfileInfo False")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, authManager.getMessage());
            // Utils.showAlert(SignInView.this, authManager.getMessage());
        } else if (getMsg.equalsIgnoreCase("ProfileInfo Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
            //Utils.showAlert(act, AlertMessage.connectionError);
        } else if (getMsg.equalsIgnoreCase("ForgotPassword True")) {

            mDialog.dismiss();
            Utils.fromSignalDialog(this, AlertMessage.password_recovey);
            // ClickInAlertDialog.clickInAlert(SignInView.this, authManager.getMessage(), "", false);
            authManager.getMessage();
            Utils.dismissBarDialog();
        } else if (getMsg.equalsIgnoreCase("ForgotPassword False")) {
            Utils.dismissBarDialog();
            mDialog.dismiss();
            Utils.fromSignalDialog(this, "Email not registered");
        } else if (getMsg.equalsIgnoreCase("ForgotPassword Network Error")) {
            mDialog.dismiss();
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
            //   Utils.showAlert(act, AlertMessage.connectionError);
        } else if (getMsg.equalsIgnoreCase("GetRelationShips False")) {
            if (authManager == null)
                authManager = ModelManager.getInstance().getAuthorizationManager();
            if (authManager.getUserPic() != null) {

                new DownloadImage().execute(authManager.getUserPic());

            }
            else {

                Utils.dismissBarDialog();
                switchView();
            }

        } else if (getMsg.equalsIgnoreCase("GetRelationShips Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
        } else if (getMsg.equalsIgnoreCase("GetrelationShips True")) {
            Intent i = new Intent(this, MyQbChatService.class);
            startService(i);

            if (authManager == null)
                authManager = ModelManager.getInstance().getAuthorizationManager();
            if (authManager.getUserPic() != null) {

                new DownloadImage().execute(authManager.getUserPic());
            }
            else {

                Utils.dismissBarDialog();
                switchView();
            }
        }

    }

    public void forgetPasswordAlert(final Activity contex) {

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


                //akshit code to hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(contex.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getemailid.getWindowToken(), 0);
                mDialog.dismiss();

            }
        });


        Button continueBtn = (Button) mDialog.findViewById(R.id.dialog_continue);
        // if button is clicked, close the custom dialog
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isEmailValid(getemailid.getText().toString())) {
                    Utils.launchBarDialog(SignInView.this);
                    SettingManager settingManager = ModelManager.getInstance().getSettingManager();
                    settingManager.forgotYourPassword(getemailid.getText().toString());

                    InputMethodManager imm = (InputMethodManager) getSystemService(contex.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getemailid.getWindowToken(), 0);
                    mDialog.dismiss();

                } else {
                    Utils.fromSignalDialog(SignInView.this, AlertMessage.vEmailid);
                    //    ClickInAlertDialog.clickInAlert(SignInView.this,"Please enter a valid email address","Error",true);
                }
            }
        });


        mDialog.show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }





    /* downoad image from server */

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
            String mPath = Utils.storeImage(result, ""+ModelManager.getInstance().getAuthorizationManager().getUserId(), SignInView.this);
            if (authManager == null)
                authManager = ModelManager.getInstance().getAuthorizationManager();
            if (!Utils.isEmptyString(""+mPath))
                authManager.setUserImageUri(Uri.parse(mPath)); // save image Uri
            if(result != null)
                authManager.setUserbitmap(result);
            Utils.dismissBarDialog();
            switchView();

        }
    }


}

