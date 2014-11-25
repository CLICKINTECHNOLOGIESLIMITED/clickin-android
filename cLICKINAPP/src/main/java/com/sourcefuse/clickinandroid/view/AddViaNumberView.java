package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class AddViaNumberView extends Activity implements View.OnClickListener,TextWatcher {
    private static final String TAG = PlayItSafeView.class.getSimpleName();
	private Button getClickInVn;
	private AuthManager authManager ;
	private EditText edtPhoneNo,edtCountryCode;


    String mPhNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_addvianumber);


		this.overridePendingTransition(R.anim.slide_in_right ,R.anim.slide_out_right);
		getClickInVn = (Button) findViewById(R.id.btn_get_click_via_no);
		edtPhoneNo = (EditText) findViewById(R.id.edt_get_ph_no);
        edtCountryCode= (EditText)findViewById(R.id.edt_cntry_cd);
		edtPhoneNo.addTextChangedListener(AddViaNumberView.this);

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



        ((TextView) findViewById(R.id.btn_go_back_num)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.top_out);
            }
        });

		authManager = ModelManager.getInstance().getAuthorizationManager();
//     try {
//            String CountryZipCode = null;
//            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            int simState = telephonyManager.getSimState();
//            //Log.e("simState",""+simState+"/"+TelephonyManager.SIM_STATE_NETWORK_LOCKED+"/"+TelephonyManager.SIM_STATE_UNKNOWN+"/"+TelephonyManager.SIM_STATE_READY);
//            switch (simState) {
//
//                case (TelephonyManager.SIM_STATE_ABSENT): {
//                    edtCountryCode.setText("+(null)");
//                }
//                break;
//
//                case (TelephonyManager.SIM_STATE_NETWORK_LOCKED): {
//                    edtCountryCode.setText("+(null)");
//                }
//                break;
//                case (TelephonyManager.SIM_STATE_PIN_REQUIRED):
//                    break;
//                case (TelephonyManager.SIM_STATE_PUK_REQUIRED):
//                    break;
//                case (TelephonyManager.SIM_STATE_UNKNOWN): {
//                    edtCountryCode.setText("+(null)");
//                }
//                break;
//                case (TelephonyManager.SIM_STATE_READY): {
//
//                    String simCountry = telephonyManager.getSimCountryIso();
//                    Log.e("simCountry",simCountry);
//
//                    String simOperatorCode = telephonyManager.getSimOperator();
//                    Log.e("simOperatorCode",simOperatorCode);
//
//                    String simOperatorName = telephonyManager.getSimOperatorName();
//                    Log.e("simOperatorName",simOperatorName);
//
//                    String simSerial = telephonyManager.getSimSerialNumber();
//                    Log.e("simSerial",simSerial);
//
//                    CountryZipCode = GetCountryZipCode();
//                    CountryZipCode = "+" + CountryZipCode;
//                    Log.e("COUNTRY ZIP CODE", CountryZipCode);
//                    edtCountryCode.setText(CountryZipCode);
//                }
//                break;
//            }
//
//        }catch(Exception e){
//            Log.e(TAG, "Exception-->" + e.toString());
//        }
//


        //akshit code start For country Code ,

        try{
            String countryCode=Utils.getCountryCodeFromSim(this);
            if(countryCode==null){
                edtCountryCode.setText("+(null)");
            }else{
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
            if(Utils.isCountryCodeValid(edtCountryCode.getText().toString())){
                if (Utils.isPhoneValid(edtPhoneNo.getText().toString()) && (edtPhoneNo.getText().toString().length() >= 5)) {
                     mPhNo=edtCountryCode.getText().toString().trim()+edtPhoneNo.getText().toString().trim();
                    //Monika-need to hit webservice to check num registered or not
            /*        ProfileManager prfManager= ModelManager.getInstance().getProfileManager();
                    if(prfManager.currClickersPhoneNums.contains(mPhNo)){
                        authManager = ModelManager.getInstance().getAuthorizationManager();
                        authManager.sendNewRequest(authManager.getPhoneNo(), mPhNo, authManager.getUsrToken());
                    }else {

                    }*/
                    FetchContactFromPhone.checkNumWithClickInDb(mPhNo);

                }else{
                    Utils.fromSignalDialog(this,AlertMessage.phone);
                }

            }else{
                Utils.fromSignalDialog(this,AlertMessage.country);
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
                    Utils.fromSignalDialog(this,authManager.getMessage());
                    //Utils.showAlert(AddViaNumberView.this, authManager.getMessage());
                   // finish();
				} else if(message.equalsIgnoreCase("RequestSend Network Error")){
					Utils.dismissBarDialog();
				      Utils.fromSignalDialog(this,AlertMessage.connectionError);
				//	Utils.showAlert(AddViaNumberView.this, AlertMessage.connectionError);
                    //finish();
				}else if(message.equalsIgnoreCase("Num Not Registered")){


                    /* send sms if not not register */
                 /*  send sms for nexus 5 check build version*/
                 /* prafull code */
                    try {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) //At least KitKat
                        {
                            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(AddViaNumberView.this); //Need to change the build to API 19

                            Intent sendIntent = new Intent(Intent.ACTION_SEND);
                            sendIntent.setType("text/plain");
                            sendIntent.putExtra(Intent.EXTRA_TEXT, Constants.SEND_REQUEST_WITH_SMS_MESSAGE);

                            if (defaultSmsPackageName != null)//Can be null in case that there is no default, then the user would be able to choose any app that support this intent.
                            {
                                sendIntent.setPackage(defaultSmsPackageName);
                            }
                            startActivity(sendIntent);

                        } else //For early versions, do what worked for you before.
                        {
                            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                            smsIntent.putExtra("sms_body", Constants.SEND_REQUEST_WITH_SMS_MESSAGE);
                            smsIntent.putExtra("address", mPhNo);
                            smsIntent.setType("vnd.android-dir/mms-sms");
                            startActivity(smsIntent);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Exception to send sms--->", "" + e.toString());
                    }



                }else if(message.equalsIgnoreCase("Num Registered")){
                    Utils.launchBarDialog(this);
                    authManager = ModelManager.getInstance().getAuthorizationManager();
                    authManager.sendNewRequest(authManager.getPhoneNo(), mPhNo, authManager.getUsrToken());
                }else if(message.equalsIgnoreCase("Num Check False")){
                    Utils.dismissBarDialog();
                    Utils.fromSignalDialog(this,authManager.getMessage());
                }
		}


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        if (edtPhoneNo.getText().toString().length()>0){

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



}
