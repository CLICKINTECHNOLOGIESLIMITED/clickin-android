package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class AddViaNumberView extends Activity implements View.OnClickListener {
    private static final String TAG = PlayItSafeView.class.getSimpleName();
	private Button backButton,getClickInVn;
	private AuthManager authManager ;
    private RelationManager relationManager ;
	private EditText edtPhoneNo,edtCountryCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_addvianumber);
		this.overridePendingTransition(R.anim.slide_in_right ,R.anim.slide_out_right);
		backButton = (Button) findViewById(R.id.btn_go_back_num);
		getClickInVn = (Button) findViewById(R.id.btn_get_click_via_no);
		edtPhoneNo = (EditText) findViewById(R.id.edt_get_ph_no);
        edtCountryCode= (EditText)findViewById(R.id.edt_cntry_cd);
		backButton.setOnClickListener(this);
		getClickInVn.setOnClickListener(this);
        ((RelativeLayout) findViewById(R.id.rl_addvia_no_action)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtPhoneNo.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(edtCountryCode.getWindowToken(), 0);

            }

        });
		authManager = ModelManager.getInstance().getAuthorizationManager();
     try {
            String CountryZipCode = null;
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            int simState = telephonyManager.getSimState();
            //Log.e("simState",""+simState+"/"+TelephonyManager.SIM_STATE_NETWORK_LOCKED+"/"+TelephonyManager.SIM_STATE_UNKNOWN+"/"+TelephonyManager.SIM_STATE_READY);
            switch (simState) {

                case (TelephonyManager.SIM_STATE_ABSENT): {
                    edtCountryCode.setText("+(null)");
                }
                break;

                case (TelephonyManager.SIM_STATE_NETWORK_LOCKED): {
                    edtCountryCode.setText("+(null)");
                }
                break;
                case (TelephonyManager.SIM_STATE_PIN_REQUIRED):
                    break;
                case (TelephonyManager.SIM_STATE_PUK_REQUIRED):
                    break;
                case (TelephonyManager.SIM_STATE_UNKNOWN): {
                    edtCountryCode.setText("+(null)");
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
                    edtCountryCode.setText(CountryZipCode);
                }
                break;
            }

        }catch(Exception e){
            Log.e(TAG, "Exception-->" + e.toString());
        }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_go_back_num:
			finish();
			break;
		case R.id.btn_get_click_via_no:
            String mPhNo=edtCountryCode.getText().toString().trim()+edtPhoneNo.getText().toString().trim();
            ProfileManager prfManager= ModelManager.getInstance().getProfileManager();
            if(prfManager.currClickersPhoneNums.contains(mPhNo)){
                authManager = ModelManager.getInstance().getAuthorizationManager();
                authManager.sendNewRequest(authManager.getPhoneNo(), mPhNo, authManager.getUsrToken());
            }else {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.putExtra("sms_body", Constants.SEND_REQUEST_WITH_SMS_MESSAGE);
                smsIntent.putExtra("address", mPhNo);
                smsIntent.setType("vnd.android-dir/mms-sms");
                startActivity(smsIntent);
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
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    public void onEventMainThread(String message){
			authManager = ModelManager.getInstance().getAuthorizationManager();
				if (message.equalsIgnoreCase("RequestSend True")) {
					Utils.dismissBarDialog();
                    Intent intent = new Intent(this,CurrentClickersView.class);
                    intent.putExtra("FromMenu",false);
                    intent.putExtra("FromSignup", true);
                    startActivity(intent);

                    finish();
					//switchView();
				} else if (message.equalsIgnoreCase("RequestSend False")) {
					Utils.dismissBarDialog();
                    Utils.showAlert(AddViaNumberView.this, authManager.getMessage());
                   // finish();
				} else if(message.equalsIgnoreCase("RequestSend Network Error")){
					Utils.dismissBarDialog();
					Utils.showAlert(AddViaNumberView.this, AlertMessage.connectionError);
                    //finish();
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
	

	
}
