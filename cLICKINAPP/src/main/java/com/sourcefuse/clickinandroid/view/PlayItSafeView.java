package com.sourcefuse.clickinandroid.view;



import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.sourcefuse.clickinandroid.view.adapter.ContactAdapter;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;


public class PlayItSafeView extends Activity implements View.OnClickListener,TextWatcher {
    private static final String TAG = PlayItSafeView.class.getSimpleName();
    private Button done;
    private EditText password,rePassword;
    private AuthManager authManager ;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_playitsafe);
        this.overridePendingTransition(R.anim.slide_in_right ,R.anim.slide_out_right);
        authManager = ModelManager.getInstance().getAuthorizationManager();
       
        done = (Button) findViewById(R.id.btn_done_play);
        password = (EditText) findViewById(R.id.edt_password);
        rePassword = (EditText) findViewById(R.id.edt_re_password);
        password.addTextChangedListener(this);
        rePassword.addTextChangedListener(this);

        done.setOnClickListener(this);

        ((RelativeLayout) findViewById(R.id.rl_playitsafe_action)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager)getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(rePassword.getWindowToken(), 0);

            }

        });


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

        if(password.getText().toString().length()>0 || rePassword.getText().toString().length()>0 ){
            done.setBackgroundResource(R.drawable.done_blue);
        }else{
            done.setBackgroundResource(R.drawable.done_rounded_light);
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
        Log.d(TAG, "onEventMainThread->"+message);
			authManager = ModelManager.getInstance().getAuthorizationManager();
			if (message.equalsIgnoreCase("PlayItSafe True")) {
                new FetchContactFromPhone(this).getClickerList(authManager.getPhoneNo(), authManager.getUsrToken(), 1);

			} else if (message.equalsIgnoreCase("PlayItSafe False")) {
				Utils.dismissBarDialog();
                alertDialog(authManager.getMessage());
			} else if(message.equalsIgnoreCase("PlayItSafe Network Error")){
				Utils.dismissBarDialog();
                alertDialog(AlertMessage.connectionError);
			}else if (message.equalsIgnoreCase("CheckFriend True")) {
            Utils.dismissBarDialog();
                switchView();
          } else if (message.equalsIgnoreCase("CheckFriend False")) {
            Utils.dismissBarDialog();
              Utils.showAlert(this,authManager.getMessage());
                switchView();
          //  fromSignalDialog(authManager.getMessage());
         } else if(message.equalsIgnoreCase("CheckFriend Network Error")){
            Utils.dismissBarDialog();
                Utils.showAlert(this, AlertMessage.connectionError);
                switchView();
           // fromSignalDialog( AlertMessage.connectionError);
        }
			
		}
	private void switchView() {
		Intent intent = new Intent(PlayItSafeView.this, AddSomeoneView.class);
        intent.putExtra("FromOwnProfile", false);
		startActivity(intent);
		this.finish();
	}
	
	
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_done_play:
                String pwd,repwd;
                pwd = password.getText().toString().trim();
                repwd = rePassword.getText().toString().trim();
                if(pwd.length()>7) {
                    if (pwd.equals(repwd)) {
                        Utils.launchBarDialog(this);
                        authManager = ModelManager.getInstance().getAuthorizationManager();
                        authManager.playItSafeAuth(pwd, authManager.getPhoneNo(), authManager.getEmailId(), authManager.getUsrToken());
                    } else {
                        alertDialog(AlertMessage.MATCHPASSWORD);
                    }
                }else {
                    alertDialog(AlertMessage.PASSWORDLENGHT);
                }
                break;
        }
    }

    //akshit Code for dialog starts
    public void alertDialog(String msgStrI) {
      final Dialog dialog = new Dialog(PlayItSafeView.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        dialog.setContentView(R.layout.alert_check_dialogs);

        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
//        TextView msgII = (TextView) dialog.findViewById(R.id.alert_msgII);
        msgI.setText(msgStrI);


        // dialog.setCancelable(true);
        Button dismiss = (Button) dialog.findViewById(R.id.coolio);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}

