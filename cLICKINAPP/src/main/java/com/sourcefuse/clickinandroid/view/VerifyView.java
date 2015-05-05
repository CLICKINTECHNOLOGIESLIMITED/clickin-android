package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class VerifyView extends Activity implements OnClickListener,
        TextWatcher {
    private static final String TAG = VerifyView.class.getSimpleName();
    private Button btn_verify;
    private Button btn_resend;
    private EditText verify_code;
    private String digits, phone;
    public static Activity act;
    public static Context context;
    private Dialog dialog;
    private AuthManager authManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //code- to handle uncaught exception
        if (Utils.mStartExceptionTrack)
            Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_verify);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        act = this;
        context = this;
        btn_verify 		= (Button) findViewById(R.id.btn_verify);
        btn_resend 	= (Button) findViewById(R.id.btn_resend);

        verify_code = (EditText) findViewById(R.id.verify_code);
        verify_code.setRawInputType(Configuration.KEYBOARD_12KEY);
        verify_code.addTextChangedListener(this);

        digits = verify_code.getText().toString();

        btn_verify.setOnClickListener(this);
        btn_resend.setOnClickListener(this);

        //Temporaily commented for quick signup testing
        btn_verify.setEnabled(false);

        Utils.prefrences = getSharedPreferences(context.getString(R.string.PREFS_NAME), MODE_PRIVATE);

        //fromSignalertDialog(AlertMessage.SENDVERIFYMSGI,AlertMessage.SENDVERIFYMSGII);
        //Utils.fromSignalDialog1(this, AlertMessage.SENDVERIFYMSGI, AlertMessage.SENDVERIFYMSGII);


    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        digits = verify_code.getText().toString();

        if (verify_code.getText().toString().length() > 3) {
            // Will make the button active & add a different color to the background later
            btn_verify.setEnabled(true);
            btn_verify.setBackgroundResource(R.drawable.roundbutton);

            Utils.launchBarDialog(VerifyView.this);
            authManager.getVerifyCode(authManager.getPhoneNo(),Utils.deviceId, digits, Constants.DEVICETYPE);

        } else {
            // Will make the button inactive & add a different color to the background later
            btn_verify.setEnabled(false);
            btn_verify.setBackgroundResource(R.drawable.roundbutton_inactive);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_verify:
                Utils.launchBarDialog(VerifyView.this);
                authManager.getVerifyCode(authManager.getPhoneNo(),Utils.deviceId, digits, Constants.DEVICETYPE);
                break;
            case R.id.btn_resend:
                Utils.launchBarDialog(VerifyView.this);
                authManager = ModelManager.getInstance().getAuthorizationManager();
                authManager.reSendVerifyCode(authManager.getPhoneNo(), Utils.deviceId.toString());
                break;
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

    /*
    public void onEventMainThread(String getMsg) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (getMsg.equalsIgnoreCase("Verify True")) {
            Utils.dismissBarDialog();
            Intent intent = new Intent(VerifyView.this, ProfileView.class);
            intent.putExtra("fromsignup", getIntent().getBooleanExtra("fromsignup", false));
            startActivity(intent);
            finish();
        } else if (getMsg.equalsIgnoreCase("Verify False")) {
            Utils.dismissBarDialog();
            //To track through mixPanel.
            //Click Resend Verification Code.
            Utils.trackMixpanel(VerifyView.this, "", "", "ResendVerificationCode", false);
            alertDialog(AlertMessage.WRONGVERIFYCODEI, AlertMessage.WRONGVERIFYCODEII);
        } else if (getMsg.equalsIgnoreCase("Verify Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
            //Utils.showAlert(this, AlertMessage.connectionError);
        } else if (getMsg.equalsIgnoreCase("ReSendVerifyCode True")) {
            Utils.dismissBarDialog();
        } else if (getMsg.equalsIgnoreCase("ReSendVerifyCode False")) {

            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, authManager.getMessage());
            //Utils.showAlert(VerifyView.this, authManager.getMessage());
        } else if (getMsg.equalsIgnoreCase("ReSendVerifyCode Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
            //Utils.showAlert(this, AlertMessage.connectionError);
        }
    }*/

    public void onEventMainThread(String getMsg){
        Log.d(TAG, "onEventMainThread->" + getMsg);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (getMsg.equalsIgnoreCase("Verify True")) {
            Utils.dismissBarDialog();
            switchView();
        } else if (getMsg.equalsIgnoreCase("Verify False")) {
            Utils.dismissBarDialog();
            alertDialog(AlertMessage.WRONGCODE,AlertMessage.WRONGVERIFYCODEII);
        } else if (getMsg.equalsIgnoreCase("Verify Network Error")) {
            Utils.dismissBarDialog();
            Utils.showAlert(act, AlertMessage.connectionError);
        } else if (getMsg.equalsIgnoreCase("ReSendVerifyCode True")) {
            Utils.dismissBarDialog();
        }else if (getMsg.equalsIgnoreCase("ReSendVerifyCode False")) {
            Utils.dismissBarDialog();
            Utils.showAlert(VerifyView.this, authManager.getMessage());
        } else if (getMsg.equalsIgnoreCase("ReSendVerifyCode Network Error")) {
            Utils.dismissBarDialog();
            Utils.showAlert(act, AlertMessage.connectionError);
        }
    }

    public void switchView(){
        Intent intent = new Intent(VerifyView.this, ProfileView.class);
        intent.putExtra("fromsignup", getIntent().getBooleanExtra("fromsignup", false));
        startActivity(intent);
        finish();
    }

    public void alertDialog(String msgStrI, String msgStrII) {
        dialog = new Dialog(VerifyView.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_nocheck);
        dialog.setCancelable(false);

        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        TextView msgII = (TextView) dialog.findViewById(R.id.alert_msgII);
        msgI.setText(msgStrI);
        msgII.setText(msgStrII);

        dialog.setCancelable(false);
        Button dismiss = (Button) dialog.findViewById(R.id.coolio);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                //switchView();
                btn_verify.setBackgroundResource(R.drawable.roundbutton_inactive);
                verify_code.setText("");

            }
        });
        dialog.show();
    }


}
