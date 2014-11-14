package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class SignUpView extends Activity implements TextWatcher,OnClickListener {

 //   private static final String TAG = SignUpView.class.getSimpleName();

	private Button checkmeout;
	private EditText phoneNo, cntrycode;
	private AuthManager authManager ;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_signup);
		Utils.deviceId = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);

		checkmeout = (Button) findViewById(R.id.checkmeout);
		cntrycode = (EditText) findViewById(R.id.edt_code);
		phoneNo = (EditText) findViewById(R.id.edt_phoneno);
		phoneNo.addTextChangedListener(this);
		checkmeout.setOnClickListener(this);
        checkmeout.setEnabled(false);
		    ((RelativeLayout) findViewById(R.id.rl_main_signup)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager)getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(cntrycode.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(phoneNo.getWindowToken(), 0);

            }

        });

        try {
            String CountryZipCode = null;
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            int simState = telephonyManager.getSimState();
            //Log.e("simState",""+simState+"/"+TelephonyManager.SIM_STATE_NETWORK_LOCKED+"/"+TelephonyManager.SIM_STATE_UNKNOWN+"/"+TelephonyManager.SIM_STATE_READY);
            switch (simState) {

                case (TelephonyManager.SIM_STATE_ABSENT): {
                    cntrycode.setText("+(null)");
                }
                break;

                case (TelephonyManager.SIM_STATE_NETWORK_LOCKED): {
                    cntrycode.setText("+(null)");
                }
                break;
                case (TelephonyManager.SIM_STATE_PIN_REQUIRED):
                    break;
                case (TelephonyManager.SIM_STATE_PUK_REQUIRED):
                    break;
                case (TelephonyManager.SIM_STATE_UNKNOWN): {
                    cntrycode.setText("+(null)");
                }
                break;
                case (TelephonyManager.SIM_STATE_READY): {

                    String simCountry = telephonyManager.getSimCountryIso();
                    Log.e("simCountry",simCountry);

                    String simOperatorCode = telephonyManager.getSimOperator();
                    Log.e("simOperatorCode",simOperatorCode);

                    String simOperatorName = telephonyManager.getSimOperatorName();
                    Log.e("simOperatorName",simOperatorName);

                    String simSerial = telephonyManager.getSimSerialNumber();
                    Log.e("simSerial",simSerial);

                    CountryZipCode = GetCountryZipCode();
                    CountryZipCode = "+" + CountryZipCode;
                    Log.e("COUNTRY ZIP CODE", CountryZipCode);
                    cntrycode.setText(CountryZipCode);
                }
                break;
            }
//            TelephonyManager mTelephonyMgr;
//            mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            myNumber = mTelephonyMgr.getLine1Number();
//            Log.e(TAG,"myNumber-->"+myNumber);
//            phoneNo.setText(myNumber);
            }catch(Exception e){
                Log.e("Exception TAG" , "Exception-->" + e.toString());
            }
        }


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.checkmeout:
            String countryCode = cntrycode.getText().toString().trim().toString().replaceAll("\\s+","");
            String enterdPhoneNo = phoneNo.getText().toString().trim();
            if(Utils.isCountryCodeValid(countryCode)) {
                if (Utils.isPhoneValid(enterdPhoneNo) && (enterdPhoneNo.length() >= 5)) {
                    Utils.launchBarDialog(SignUpView.this);
                    authManager = ModelManager.getInstance().getAuthorizationManager();
                    authManager.setCountrycode(countryCode);
                    authManager.setPhoneNo(countryCode+ enterdPhoneNo);
                    authManager.signUpAuth(countryCode + enterdPhoneNo, Utils.deviceId.toString());
                } else {

                    fromSignalDialog(AlertMessage.phone);
                    //Utils.showAlert(SignUpView.this, AlertMessage.phone);
                }
            }else{

                fromSignalDialog(AlertMessage.country);
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


    public String GetCountryZipCode(){
        String CountryID="";
        String CountryZipCode="";
        try {
            TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            //getNetworkCountryIso
            CountryID = manager.getSimCountryIso().toUpperCase();
        }catch (Exception e){
            e.printStackTrace();
        }

        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                Log.e("Code","Tis is Code>>>>>" +CountryZipCode);
                break;
            }
        }
        return CountryZipCode;
    }



    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    public void onEventMainThread(String getMsg){
        if (getMsg.equalsIgnoreCase("SignUp True")) {
            Utils.dismissBarDialog();
            Intent intent = new Intent(this, VerifyView.class);
            startActivity(intent);
            finish();
        } else if (getMsg.equalsIgnoreCase("SignUp False")) {
            Utils.dismissBarDialog();
            fromSignalDialog(AlertMessage.usrAllreadyExists);
           // Utils.showAlert(SignUpView.this, AlertMessage.usrAllreadyExists);
        } else if(getMsg.equalsIgnoreCase("SignUp Network Error")){
            Utils.dismissBarDialog();
            fromSignalDialog(AlertMessage.connectionError);
           // Utils.showAlert(this, AlertMessage.connectionError);
        }
    }


    // Akshit Code Starts
    public void fromSignalDialog(String str){

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


