package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class SignUpView extends Activity implements TextWatcher, OnClickListener {

    //   private static final String TAG = SignUpView.class.getSimpleName();

    private Button checkmeout;
    private EditText phoneNo, cntrycode;
    private AuthManager authManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //code- to handle uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));


        setContentView(R.layout.view_signup);
        Utils.deviceId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

        checkmeout = (Button) findViewById(R.id.checkmeout);
        cntrycode = (EditText) findViewById(R.id.edt_code);
        phoneNo = (EditText) findViewById(R.id.edt_phoneno);
        phoneNo.addTextChangedListener(this);
        checkmeout.setOnClickListener(this);
        checkmeout.setEnabled(false);
        ((RelativeLayout) findViewById(R.id.rl_main_signup)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(cntrycode.getWindowToken() != null)
                    imm.hideSoftInputFromWindow(cntrycode.getWindowToken(), 0);
                if(phoneNo.getWindowToken() != null)
                    imm.hideSoftInputFromWindow(phoneNo.getWindowToken(), 0);

            }

        });

        String countryCode = Utils.getCountryCodeFromSim(this);
        if (countryCode == null) {
            cntrycode.setText("+(null)");
        } else {
            cntrycode.setText(countryCode);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkmeout:
                String countryCode = cntrycode.getText().toString().trim().toString().replaceAll("\\s+", "");
                String enterdPhoneNo = phoneNo.getText().toString().trim();
                if (Utils.isCountryCodeValid(countryCode)) {
                    if (Utils.isPhoneValid(enterdPhoneNo) && (enterdPhoneNo.length() >= 5)) {
                        Utils.launchBarDialog(SignUpView.this);
                        authManager = ModelManager.getInstance().getAuthorizationManager();
                        authManager.setCountrycode(countryCode);
                        authManager.setPhoneNo(countryCode + enterdPhoneNo);
                        authManager.signUpAuth(countryCode + enterdPhoneNo, Utils.deviceId.toString());
                    } else {

                        Utils.fromSignalDialog(this, AlertMessage.phone);
                        //Utils.showAlert(SignUpView.this, AlertMessage.phone);
                    }
                } else {

                    Utils.fromSignalDialog(this, AlertMessage.country);
                    // Utils.showAlert(SignUpView.this, AlertMessage.country);
                }
                break;
        }
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

        if (phoneNo.getText().toString().length() >= 1) {
            checkmeout.setEnabled(true);
            checkmeout.setBackgroundResource(R.drawable.s_checkout_active);
        } else {
            checkmeout.setEnabled(false);
            checkmeout.setBackgroundResource(R.drawable.s_checkout_inactive);
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

    public void onEventMainThread(String getMsg) {
        if (getMsg.equalsIgnoreCase("SignUp True")) {
            Utils.dismissBarDialog();
            Intent intent = new Intent(this, VerifyView.class);
            intent.putExtra("fromsignup", true);
            startActivity(intent);
            finish();
        } else if (getMsg.equalsIgnoreCase("SignUp False")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.usrAllreadyExists);
            // Utils.showAlert(SignUpView.this, AlertMessage.usrAllreadyExists);
        } else if (getMsg.equalsIgnoreCase("SignUp Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
            // Utils.showAlert(this, AlertMessage.connectionError);
        }
    }


    // Akshit Code Starts
    public void fromSignalDialog(String str) {

        final Dialog dialog = new Dialog(SignUpView.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_check_dialogs);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        msgI.setText(str);


        Button dismiss = (Button) dialog.findViewById(R.id.coolio);
        dismiss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }
// Ends

}


