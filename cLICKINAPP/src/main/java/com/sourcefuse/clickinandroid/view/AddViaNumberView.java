package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
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
	private EditText edtPhoneNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_addvianumber);
		this.overridePendingTransition(R.anim.slide_in_right ,R.anim.slide_out_right);
		backButton = (Button) findViewById(R.id.btn_go_back_num);
		getClickInVn = (Button) findViewById(R.id.btn_get_click_via_no);
		edtPhoneNo = (EditText) findViewById(R.id.edt_get_ph_no);
		backButton.setOnClickListener(this);
		getClickInVn.setOnClickListener(this);
		authManager = ModelManager.getInstance().getAuthorizationManager();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_go_back_num:
			finish();
			break;
		case R.id.btn_get_click_via_no:

            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.putExtra("sms_body", Constants.SEND_REQUEST_WITH_SMS_MESSAGE);
            smsIntent.putExtra("address", authManager.getPhoneNo());
            smsIntent.setType("vnd.android-dir/mms-sms");
            startActivity(smsIntent);

          //  authManager = ModelManager.getInstance().getAuthorizationManager();
           // authManager.sendNewRequest(authManager.getPhoneNo(), edtPhoneNo.getText().toString(), authManager.getUsrToken());
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
                    relationManager = ModelManager.getInstance().getRelationManager();
                    relationManager. getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
                    finish();
					//switchView();
				} else if (message.equalsIgnoreCase("RequestSend False")) {
					Utils.dismissBarDialog();
					Log.d("2", "message->"+message);
                    Utils.showAlert(AddViaNumberView.this, authManager.getMessage());
                    finish();
				} else if(message.equalsIgnoreCase("RequestSend Network Error")){
					Utils.dismissBarDialog();
					Utils.showAlert(AddViaNumberView.this, AlertMessage.connectionError);
					Log.d("3", "message->"+message);
                    finish();
				}
		}

	
	private void switchView() {
		Intent intent = new Intent(AddViaNumberView.this, UserProfileView.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("FromSignup", true);
   	    intent.putExtra("othersProfile", false);
   	    intent.putExtra("phNumber", "");
		startActivity(intent);
		this.finish();
	}
	
}
