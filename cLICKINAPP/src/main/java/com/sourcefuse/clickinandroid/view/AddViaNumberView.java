package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.FetchContactFromPhone;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class AddViaNumberView extends Activity implements View.OnClickListener, TextWatcher {
    private static final String TAG = PlayItSafeView.class.getSimpleName();
    String mPhNo;
    boolean fromProfile = false;
    private Button getClickInVn;
    private AuthManager authManager;
    private EditText edtPhoneNo, edtCountryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        //code- to handle uncaught exception
        if(Utils.mStartExceptionTrack)
            Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

        setContentView(R.layout.view_addvianumber);


        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        getClickInVn = (Button) findViewById(R.id.btn_get_click_via_no);
        edtPhoneNo = (EditText) findViewById(R.id.edt_get_ph_no);
        edtCountryCode = (EditText) findViewById(R.id.edt_cntry_cd);
        edtPhoneNo.addTextChangedListener(AddViaNumberView.this);

        getClickInVn.setOnClickListener(this);
        ((RelativeLayout) findViewById(R.id.rl_addvia_no_action)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(edtPhoneNo.getWindowToken() != null)
                    imm.hideSoftInputFromWindow(edtPhoneNo.getWindowToken(), 0);
                if(edtCountryCode.getWindowToken() != null)
                    imm.hideSoftInputFromWindow(edtCountryCode.getWindowToken(), 0);

            }

        });

        //akshit code to hide back button.
        if (!getIntent().getBooleanExtra("fromsignup", false)) {
            findViewById(R.id.rl_back).setVisibility(View.GONE);
        }


        ((TextView) findViewById(R.id.btn_go_back_num)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.top_out);
            }
        });

        authManager = ModelManager.getInstance().getAuthorizationManager();


        //akshit code start For country Code ,

        try {
            String countryCode = Utils.getCountryCodeFromSim(this);
            if (countryCode == null) {
                edtCountryCode.setText("+(null)");
            } else {
                edtCountryCode.setText(countryCode);
            }


        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.top_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_click_via_no:
                //To track through mixPanel.
                //Signup AddPartner.
                Utils.trackMixpanel(AddViaNumberView.this,"","","SignUpAddAPartner",false);
                if (Utils.isCountryCodeValid(edtCountryCode.getText().toString())) {
                    if (Utils.isPhoneValid(edtPhoneNo.getText().toString()) && (edtPhoneNo.getText().toString().length() >= 5) && !((EditText) findViewById(R.id.edt_cntry_cd)).getText().toString().equalsIgnoreCase("+(null)")) {
                        mPhNo = edtCountryCode.getText().toString().trim() + edtPhoneNo.getText().toString().trim();
                        Utils.launchBarDialog(AddViaNumberView.this);
                        FetchContactFromPhone.checkNumWithClickInDb(mPhNo);

                    } else {
                        Utils.fromSignalDialog(this, AlertMessage.phone);
                    }

                } else {
                    Utils.fromSignalDialog(this, AlertMessage.country);
                }

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

    public void onEventMainThread(String message) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (message.equalsIgnoreCase("RequestSend True")) {
            Utils.dismissBarDialog();
            if (getIntent().getBooleanExtra("fromsignup", false)) {
                Intent intent = new Intent(this, CurrentClickersView.class);
                intent.putExtra("FromMenu", false);
                intent.putExtra("Fromsignup", true);
                startActivity(intent);
                finish();
            } else {
                ((RelativeLayout) findViewById(R.id.rl_back)).setVisibility(View.GONE);
                Intent intent = new Intent(this, UserProfileView.class);
                intent.putExtra("isChangeInList", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
            }
            //switchView();
        } else if (message.equalsIgnoreCase("RequestSend False")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, authManager.getMessage());
            //Utils.showAlert(AddViaNumberView.this, authManager.getMessage());
            // finish();
        } else if (message.equalsIgnoreCase("RequestSend Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
            //	Utils.showAlert(AddViaNumberView.this, AlertMessage.connectionError);
            //finish();
        } else if (message.equalsIgnoreCase("Num Not Registered")) {
            Utils.dismissBarDialog();


                    /* try to send with sms if sms is not present than it will send it towards hangout */

                    /* send sms if not not register */
                 /*  send sms for nexus 5 check build version*/
                 /* prafull code */

            try {


                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.putExtra("sms_body", Constants.SEND_REQUEST_WITH_SMS_MESSAGE_SPREAD);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.setData(Uri.parse(mPhNo));
                smsIntent.putExtra("exit_on_sent", true);
                startActivity(smsIntent);


            } catch (Exception e) {
                try {
                    String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(AddViaNumberView.this); //Need to change the build to API 19

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra("address", mPhNo);
                    sendIntent.putExtra(Intent.ACTION_ATTACH_DATA, Uri.parse(mPhNo));
                    sendIntent.putExtra(Intent.EXTRA_TEXT, Constants.SEND_REQUEST_WITH_SMS_MESSAGE);
                    if (defaultSmsPackageName != null)//Can be null in case that there is no default, then the user would be able to choose any app that support this intent.
                    {
                        sendIntent.setPackage(defaultSmsPackageName);
                    }
                    sendIntent.putExtra("exit_on_sent", true);
                    startActivity(sendIntent);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }


        } else if (message.equalsIgnoreCase("Num Registered")) {
            /*Utils.launchBarDialog(this);*/
            authManager = ModelManager.getInstance().getAuthorizationManager();
            authManager.sendNewRequest(authManager.getPhoneNo(), mPhNo, authManager.getUsrToken());
        } else if (message.equalsIgnoreCase("Num Check False")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, authManager.getMessage());
        }
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        if (edtPhoneNo.getText().toString().length() > 0) {

            getClickInVn.setBackgroundResource(R.drawable.c_getclicin_active);
            getClickInVn.setEnabled(true);
        } else {
            getClickInVn.setEnabled(false);
            getClickInVn.setBackgroundResource(R.drawable.c_getclicin_deactive);
        }


    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
// Ends


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


    }


}
