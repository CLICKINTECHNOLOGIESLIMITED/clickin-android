package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;

public class AddViaContactView extends Activity implements View.OnClickListener,TextWatcher {
    private String TAG = PlayItSafeView.class.getSimpleName();
	private Button getClickIn;
	private EditText phoneNo,cntry_cd;
	private ImageView conIcon;
	private AuthManager authManager ;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_addviacontactlist);
		this.overridePendingTransition(R.anim.slide_in_right ,R.anim.slide_out_right);

        cntry_cd = (EditText) findViewById(R.id.edt_cntry_cd);
		phoneNo = (EditText) findViewById(R.id.edt_phone_no);
        phoneNo.addTextChangedListener(this);
		getClickIn = (Button) findViewById(R.id.btn_get_clickIn);
		conIcon  = (ImageView) findViewById(R.id.iv_contact_icon);
		getClickIn.setOnClickListener(this);

		try {
			Bundle bundle = getIntent().getExtras();
            ((TextView) findViewById(R.id.tv_contact_name)).setText("" + bundle.getString("ConName"));
            String mlPhNo = bundle.getString("ConNumber");
            String onlyPhNo = null;
            String countryCode = null;

            //first check country code from SIM and compare it with num
            countryCode=Utils.getCountryCodeFromSim(this);
            if(countryCode!=null){
                if(mlPhNo.startsWith(countryCode)){
                    onlyPhNo=mlPhNo.replace(countryCode,"");
                }
            }else {
                String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
                for (int i = 0; i < rl.length; i++) {
                    String[] g = rl[i].split(",");
                    String tempcountryCode = g[0];
                    tempcountryCode = "+" + tempcountryCode;
                    if (mlPhNo.startsWith(tempcountryCode)) {
                        onlyPhNo = mlPhNo.replace(tempcountryCode, "");
                        countryCode = tempcountryCode;
                        break;
                    }
                }
            }

            if(countryCode==null){
                countryCode="+(null)";
                onlyPhNo=mlPhNo.replace("+","");
            }
            cntry_cd.setText("" + countryCode);
			phoneNo.setText("" + onlyPhNo);
            String 	image_uri = bundle.getString("ConUri");
             try {
                if(!Utils.isEmptyString(image_uri)) {
                    Picasso.with(AddViaContactView.this).
                            load(image_uri)
                            .skipMemoryCache()

                            .error(R.drawable.male_user)
                            .into(conIcon);
                } else {
                    conIcon.setImageResource(R.drawable.male_user);
                }
            } catch (Exception e) {
                conIcon.setImageResource(R.drawable.male_user);
            }
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		authManager = ModelManager.getInstance().getAuthorizationManager();

        ((RelativeLayout) findViewById(R.id.rl_addvia_contact_action)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(cntry_cd.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(phoneNo.getWindowToken(), 0);

            }

        });


        ((Button) findViewById(R.id.btn_go_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.top_out);
            }
        });


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

		case R.id.btn_get_clickIn:

         if(phoneNo.getText().toString().length()>=5) {

          String mPhNo = cntry_cd.getText().toString().trim()+phoneNo.getText().toString().trim();
            ProfileManager prfManager= ModelManager.getInstance().getProfileManager();
            if(prfManager.currClickersPhoneNums.contains(mPhNo)){
                authManager = ModelManager.getInstance().getAuthorizationManager();
                Utils.launchBarDialog(this);
                authManager.sendNewRequest(authManager.getPhoneNo(), mPhNo, authManager.getUsrToken());
            }else{
                /*Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.putExtra("sms_body", Constants.SEND_REQUEST_WITH_SMS_MESSAGE);
                smsIntent.putExtra("address", mPhNo);
                smsIntent.setType("vnd.android-dir/mms-sms");
                startActivity(smsIntent);*/

                  /* send sms if not not register */
                 /*  send sms for nexus 5 check build version*/
                 /* prafull code */
                  try {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) //At least KitKat
                        {
                              String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(AddViaContactView.this); //Need to change the build to API 19

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
            }

 }else {
                    Utils.fromSignalDialog(this,AlertMessage.phone);

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
        Log.d(TAG, "onEventMainThread->"+message);
        if (message.equalsIgnoreCase("RequestSend True")) {
            Utils.dismissBarDialog();
            //Log.d("1", "message->"+message);

            Intent intent = new Intent(this,CurrentClickersView.class);
            intent.putExtra("FromMenu",false);
            intent.putExtra("FromSignup", true);
            startActivity(intent);

            finish();

        } else if (message.equalsIgnoreCase("RequestSend False")) {
            Utils.dismissBarDialog();
            Log.d("2", "message->"+message);
            Utils.fromSignalDialog(this,authManager.getMessage());
          //  Utils.showAlert(AddViaContactView.this, authManager.getMessage());
        } else if(message.equalsIgnoreCase("RequestSend Network Error")){
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this,AlertMessage.connectionError);
           // Utils.showAlert(AddViaContactView.this, AlertMessage.connectionError);
            Log.d("3", "message->"+message);

        }
    }






    @Override
	public void onDestroy() {


		super.onDestroy();
	}





    // akshit code start for Active and inActive Button
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        if (phoneNo.getText().toString().length()>0){

            getClickIn.setBackgroundResource(R.drawable.c_getclicin_active);
            getClickIn.setEnabled(true);
        } else {
            getClickIn.setEnabled(false);
          getClickIn.setBackgroundResource(R.drawable.c_getclicin_deactive);
        }
    }
    @Override
    public void afterTextChanged(Editable editable) {

    }
// Ends
}
