package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;

public class AddViaContactView extends Activity implements View.OnClickListener {
    private String TAG = PlayItSafeView.class.getSimpleName();
	private Button backButton,getClickIn;
	private TextView contacName;
	private EditText phoneNo,cntry_cd;
	private Bitmap bitmap;
	String  image_uri,mPhNo;
	private ImageView conIcon;
	private AuthManager authManager ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_addviacontactlist);
		this.overridePendingTransition(R.anim.slide_in_right ,R.anim.slide_out_right);
		contacName = (TextView) findViewById(R.id.tv_contact_name);
        cntry_cd = (EditText) findViewById(R.id.edt_cntry_cd);
		phoneNo = (EditText) findViewById(R.id.edt_phone_no);
		backButton = (Button) findViewById(R.id.btn_go_back);
		getClickIn = (Button) findViewById(R.id.btn_get_clickIn);
		conIcon  = (ImageView) findViewById(R.id.iv_contact_icon);
		backButton.setOnClickListener(this);
		getClickIn.setOnClickListener(this);

		try {
			Bundle bundle = getIntent().getExtras();
			contacName.setText("" + bundle.getString("ConName"));
            mPhNo = bundle.getString("ConNumber");
            Log.e(TAG,"mPhNo-->"+mPhNo);
            cntry_cd.setText("" + mPhNo.substring(0,3));
			phoneNo.setText("" + mPhNo.substring(3));

			image_uri = bundle.getString("ConUri");
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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_go_back:
			finish();
			break;
		case R.id.btn_get_clickIn:

            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.putExtra("sms_body", Constants.SEND_REQUEST_WITH_SMS_MESSAGE);
            smsIntent.putExtra("address", mPhNo);
            smsIntent.setType("vnd.android-dir/mms-sms");
            startActivity(smsIntent);
           // authManager = ModelManager.getInstance().getAuthorizationManager();
            //authManager.sendNewRequest(authManager.getPhoneNo(), phoneNo.getText().toString(), authManager.getUsrToken());
			break;
		}
		
		
	}


    @Override
    public void onStart() {
        super.onStart();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
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
            Log.d("1", "message->"+message);
            finish();
            //switchView();
        } else if (message.equalsIgnoreCase("RequestSend False")) {
            Utils.dismissBarDialog();
            Log.d("2", "message->"+message);
            Utils.showAlert(AddViaContactView.this, authManager.getMessage());
        } else if(message.equalsIgnoreCase("RequestSend Network Error")){
            Utils.dismissBarDialog();
            Utils.showAlert(AddViaContactView.this, AlertMessage.connectionError);
            Log.d("3", "message->"+message);
            finish();
        }
    }






    @Override
	public void onDestroy() {
		// Unregister since the activity is about to be closed.
		LocalBroadcastManager.getInstance(AddViaContactView.this).unregisterReceiver(
				mMessageReceiver);
		super.onDestroy();
	}
	private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			authManager = ModelManager.getInstance().getAuthorizationManager();
			String message = intent.getStringExtra("message");
				if (message.equalsIgnoreCase("RequestSend True")) {
					Utils.dismissBarDialog();
					Log.d("1", "message->"+message);
					switchView();
				} else if (message.equalsIgnoreCase("RequestSend False")) {
					Utils.dismissBarDialog();
					
					Log.d("2", "message->"+message);
				} else if(message.equalsIgnoreCase("RequestSend Network Error")){
					Utils.dismissBarDialog();
					Utils.showAlert(AddViaContactView.this, AlertMessage.connectionError);
					Log.d("3", "message->"+message);
				}
		}
	};
	private void switchView() {
		Intent intent = new Intent(AddViaContactView.this, UserProfileView.class);
	//	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("FromSignup", true);
   	    intent.putExtra("othersProfile", false);
   	    intent.putExtra("phNumber", "Nothing");
		startActivity(intent);
		this.finish();
	}
}
