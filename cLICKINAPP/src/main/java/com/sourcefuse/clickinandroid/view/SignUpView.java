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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.FetchContactFromPhone;
import com.sourcefuse.clickinandroid.utils.MyPreference;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class SignUpView extends Activity implements TextWatcher,OnClickListener {

    private static final String TAG = SignUpView.class.getSimpleName();

	private Button checkmeout;
	private TextView textCntrycode;
	private EditText phoneNo, cntrycode;
	private Dialog dialog;
	public static Activity act;
	public static Context context;
	private AuthManager authManager ;
	private Typeface typeface,typefaceBold;
    private String myNumber;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_signup);

		act = this;
	    context = this;
	    typeface = Typeface.createFromAsset(SignUpView.this.getAssets(),Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
        typefaceBold = Typeface.createFromAsset(SignUpView.this.getAssets(),Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_BOLD);
		Utils.deviceId = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);




		checkmeout = (Button) findViewById(R.id.checkmeout);

		cntrycode = (EditText) findViewById(R.id.edt_code);
		phoneNo = (EditText) findViewById(R.id.edt_phoneno);


		phoneNo.addTextChangedListener(this);
		
		textCntrycode = (TextView) findViewById(R.id.tv_cntrycode);
		textCntrycode.setTypeface(typeface);
		cntrycode.setTypeface(typefaceBold);
		phoneNo.setTypeface(typefaceBold);
		checkmeout.setOnClickListener(this);
        checkmeout.setEnabled(false);

        try {
            TelephonyManager mTelephonyMgr;
            mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            myNumber = mTelephonyMgr.getLine1Number();
            Log.e(TAG,"myNumber-->"+myNumber);
            phoneNo.setText(myNumber);
        }catch (Exception e){
            Log.e(TAG,"Exception-->"+e.toString());
        }

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.checkmeout:
            if(Utils.isCountryCodeValid(cntrycode.getText().toString())) {
                if (Utils.isPhoneValid(phoneNo.getText().toString()) && (phoneNo.getText().toString().length() >= 5)) {
                    Utils.launchBarDialog(SignUpView.this);
                    authManager = ModelManager.getInstance().getAuthorizationManager();
                    String cntrycodeS=cntrycode.getText().toString().replaceAll("\\s+","");
                    authManager.setCountrycode(cntrycodeS);
                    authManager.setPhoneNo(cntrycodeS+ phoneNo.getText().toString().trim());
                    authManager.signUpAuth(cntrycodeS + phoneNo.getText().toString().trim(), Utils.deviceId.toString());
                } else {
                    Utils.showAlert(SignUpView.this, AlertMessage.phone);
                }
            }else{
                Utils.showAlert(SignUpView.this, AlertMessage.country);
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

		if (phoneNo.getText().toString().length() > 1) {
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
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    public void onEventMainThread(String getMsg){
        Log.d(TAG, "onEventMainThread->"+getMsg);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (getMsg.equalsIgnoreCase("SignUp True")) {
            Utils.dismissBarDialog();
            Log.d("1", "message->"+getMsg);
            switchView();
        } else if (getMsg.equalsIgnoreCase("SignUp False")) {
            Utils.dismissBarDialog();
            Utils.showAlert(SignUpView.this, AlertMessage.usrAllreadyExists);
        } else if(getMsg.equalsIgnoreCase("SignUp Network Error")){
            Utils.dismissBarDialog();
            Utils.showAlert(act, AlertMessage.connectionError);
        }
    }

	private void switchView() {
	//	 new FetchContactFromPhone(act).readContacts();
		Intent intent = new Intent(this, VerifyView.class);
	//	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}
}
