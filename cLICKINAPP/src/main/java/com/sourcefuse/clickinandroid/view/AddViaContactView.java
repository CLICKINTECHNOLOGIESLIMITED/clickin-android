package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
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
import com.sourcefuse.clickinandroid.model.ProfileManager;
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
	String  image_uri,mPhNo;
	private ImageView conIcon;
	private AuthManager authManager ;
    Dialog dialog ;
	
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


            ProfileManager prfManager= ModelManager.getInstance().getProfileManager();
            if(prfManager.currClickersPhoneNums.contains(mPhNo)){
                authManager = ModelManager.getInstance().getAuthorizationManager();
                authManager.sendNewRequest(authManager.getPhoneNo(), phoneNo.getText().toString(), authManager.getUsrToken());
            }else{
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
            fromSignalDialog(authManager.getMessage());
          //  Utils.showAlert(AddViaContactView.this, authManager.getMessage());
        } else if(message.equalsIgnoreCase("RequestSend Network Error")){
            Utils.dismissBarDialog();
            fromSignalDialog(AlertMessage.connectionError);
           // Utils.showAlert(AddViaContactView.this, AlertMessage.connectionError);
            Log.d("3", "message->"+message);

        }
    }






    @Override
	public void onDestroy() {


		super.onDestroy();
	}

    // Akshit Code Starts
    public void fromSignalDialog(String str){

        dialog = new Dialog(AddViaContactView.this);
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
// Ends
}
