package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.sourcefuse.clickinandroid.model.RelationManager;
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



        ((Button) findViewById(R.id.btn_go_back_num)).setOnClickListener(new View.OnClickListener() {
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
                    fromSignalDialog(AlertMessage.phone);
                }

            }else{
                fromSignalDialog(AlertMessage.country);
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
                    fromSignalDialog(authManager.getMessage());
                    //Utils.showAlert(AddViaNumberView.this, authManager.getMessage());
                   // finish();
				} else if(message.equalsIgnoreCase("RequestSend Network Error")){
					Utils.dismissBarDialog();
				      fromSignalDialog(AlertMessage.connectionError);
				//	Utils.showAlert(AddViaNumberView.this, AlertMessage.connectionError);
                    //finish();
				}else if(message.equalsIgnoreCase("Num Not Registered")){
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.putExtra("sms_body", Constants.SEND_REQUEST_WITH_SMS_MESSAGE);
                    smsIntent.putExtra("address", mPhNo);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    startActivity(smsIntent);
                }else if(message.equalsIgnoreCase("Num Registered")){
                    Utils.launchBarDialog(this);
                    authManager = ModelManager.getInstance().getAuthorizationManager();
                    authManager.sendNewRequest(authManager.getPhoneNo(), mPhNo, authManager.getUsrToken());
                }else if(message.equalsIgnoreCase("Num Check False")){
                    Utils.dismissBarDialog();
                    fromSignalDialog(authManager.getMessage());
                }
		}

// public String GetCountryZipCode(){
//        String CountryID="";
//        String CountryZipCode="";
//        try {
//            TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//            //getNetworkCountryIso
//            CountryID = manager.getSimCountryIso().toUpperCase();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
//        for(int i=0;i<rl.length;i++){
//            String[] g=rl[i].split(",");
//            if(g[1].trim().equals(CountryID.trim())){
//                CountryZipCode=g[0];
//                Log.e("Code","Tis is Code>>>>>" +CountryZipCode);
//                break;
//            }
//        }
//        return CountryZipCode;
//    }
    // Akshit Code Starts
    public void fromSignalDialog(String str){

        final Dialog dialog = new Dialog(AddViaNumberView.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_check_dialogs);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        msgI.setText(str);


        Button dismiss = (Button) dialog.findViewById(R.id.coolio);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }
        });
        dialog.show();
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
