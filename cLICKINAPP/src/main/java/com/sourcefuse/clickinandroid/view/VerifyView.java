package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class VerifyView extends Activity implements View.OnClickListener,
		TextWatcher {
    private static final String TAG = VerifyView.class.getSimpleName();
	private Button send;
	private EditText d_one, d_two, d_three, d_four;

	private String digits, phone;
	public static Activity act;
	public static Context context;
	private AuthManager authManager;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_verify);
		this.overridePendingTransition(R.anim.slide_in_right ,R.anim.slide_out_right);
		authManager = ModelManager.getInstance().getAuthorizationManager();
		act = this;
		context = this;
		send = (Button) findViewById(R.id.btn_send);

		d_one = (EditText) findViewById(R.id.edt_one);
		d_two = (EditText) findViewById(R.id.edt_two);
		d_three = (EditText) findViewById(R.id.edt_three);
		d_four = (EditText) findViewById(R.id.edt_four);

		d_one.setRawInputType(Configuration.KEYBOARD_12KEY);
		d_two.setRawInputType(Configuration.KEYBOARD_12KEY);
		d_three.setRawInputType(Configuration.KEYBOARD_12KEY);
		d_four.setRawInputType(Configuration.KEYBOARD_12KEY);

		d_one.addTextChangedListener(this);
		d_two.addTextChangedListener(this);
		d_three.addTextChangedListener(this);
		d_four.addTextChangedListener(this);
		send.setOnClickListener(this);

		Utils.prefrences = getSharedPreferences(context.getString(R.string.PREFS_NAME), MODE_PRIVATE);

        fromSignalertDialog(AlertMessage.SENDVERIFYMSGI,AlertMessage.SENDVERIFYMSGII);
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

		if (d_one.getText().toString().length() > 0) {
			d_one.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field));
			d_two.requestFocus();
		} else {
			//d_one.setBackgroundColor(getResources().getColor(R.color.empty_edt));
			d_one.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field_empty));
		}
		if (d_two.getText().toString().length() > 0) {
			d_two.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field));
			d_three.requestFocus();
		} else {
			d_two.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field_empty));
			// d_one.requestFocus();
		}

		if (d_three.getText().toString().length() > 0) {
			d_three.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field));
			d_four.requestFocus();
		} else {
			d_three.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field_empty));
			// d_two.requestFocus();
		}
		digits = d_one.getText().toString() + d_two.getText().toString()
				+ d_three.getText().toString()
				+ d_four.getText().toString();
		if (d_four.getText().toString().length() > 0 && digits.length()>3) {
			d_four.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field));

			Utils.launchBarDialog(VerifyView.this);
            authManager.getVerifyCode(authManager.getPhoneNo(),Utils.deviceId, digits, Constants.DEVICETYPE);

		} else {
			d_four.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field_empty));
			// d_three.requestFocus();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			Utils.launchBarDialog(VerifyView.this);
            authManager = ModelManager.getInstance().getAuthorizationManager();
            authManager.reSendVerifyCode(authManager.getPhoneNo(), authManager.getUsrToken());
			break;
		}
	}

	private void switchView() {
		Intent intent = new Intent(VerifyView.this, ProfileView.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		this.finish();
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

        public void onEventMainThread(String getMsg){
        Log.d(TAG, "onEventMainThread->"+getMsg);
			authManager = ModelManager.getInstance().getAuthorizationManager();
			if (getMsg.equalsIgnoreCase("Verify True")) {
				Utils.dismissBarDialog();
				switchView();
			} else if (getMsg.equalsIgnoreCase("Verify False")) {
				Utils.dismissBarDialog();
				alertDialog(AlertMessage.WRONGVERIFYCODEI,AlertMessage.WRONGVERIFYCODEII);
			} else if (getMsg.equalsIgnoreCase("Verify Network Error")) {
				Utils.dismissBarDialog();
				Utils.showAlert(act, AlertMessage.connectionError);
			} else if (getMsg.equalsIgnoreCase("ReSendVerifyCode True")) {
				Utils.dismissBarDialog();
			}else if (getMsg.equalsIgnoreCase("ReSendVerifyCode False")) {
				Utils.dismissBarDialog();
				Utils.showAlert(VerifyView.this, authManager.getMessage());
			} else if (getMsg.equalsIgnoreCase("ReSendVerifyCode Network Error")) {
				Utils.dismissBarDialog();
				Utils.showAlert(act, AlertMessage.connectionError);
			}
		}

	public void alertDialog(String msgStrI, String msgStrII) {
		dialog = new Dialog(VerifyView.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		dialog.setContentView(R.layout.alert_nocheck);
        dialog.setCancelable(false);

		TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
		TextView msgII = (TextView) dialog.findViewById(R.id.alert_msgII);
		msgI.setText(msgStrI);
		msgII.setText(msgStrII);

		dialog.setCancelable(false);
		Button dismiss = (Button) dialog.findViewById(R.id.coolio);
		dismiss.setBackgroundResource(R.drawable.try_again);
		dismiss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				//switchView();
				send.setBackgroundResource(R.drawable.damnit_resend_code);
				
				d_one.setText(""); 
				d_two.setText("");
				d_three.setText("");
				d_four.setText("");;
				
			}
		});
		dialog.show();
	}


    public void fromSignalertDialog(String msgStrI, String msgStrII) {
        dialog = new Dialog(VerifyView.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_nocheck);
        // dialog.setCancelable(true);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        TextView msgII = (TextView) dialog.findViewById(R.id.alert_msgII);
        msgI.setText(msgStrI);
        msgII.setText(msgStrII);

        Button dismiss = (Button) dialog.findViewById(R.id.coolio);
        dismiss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

}
