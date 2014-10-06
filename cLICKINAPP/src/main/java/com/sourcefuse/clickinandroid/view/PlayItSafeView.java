package com.sourcefuse.clickinandroid.view;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;


public class PlayItSafeView extends Activity implements View.OnClickListener,TextWatcher {
    private static final String TAG = PlayItSafeView.class.getSimpleName();
    private Button done;
    private EditText password,rePassword;
   // private String emailid ,phone,userToken;
    public static Activity act;
    public static Context context;
    private AuthManager authManager ;
    private Typeface typeface,typefaceBold;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_playitsafe);
        this.overridePendingTransition(R.anim.slide_in_right ,R.anim.slide_out_right);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        typeface = Typeface.createFromAsset(PlayItSafeView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
        typefaceBold = Typeface.createFromAsset(PlayItSafeView.this.getAssets(),Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_BOLD);

         act = this;
	     context = this;;
       
        done = (Button) findViewById(R.id.btn_done_play);
        password = (EditText) findViewById(R.id.edt_password);
        rePassword = (EditText) findViewById(R.id.edt_re_password);
        password.addTextChangedListener(this);
        rePassword.addTextChangedListener(this);

        password.setTypeface(typefaceBold);
        rePassword.setTypeface(typefaceBold);

        done.setOnClickListener(this);
        
//        Utils.prefrences = getSharedPreferences(context.getString(R.string.PREFS_NAME), MODE_PRIVATE);
//        phone = Utils.prefrences.getString(Constants.PREFS_VALUE_PHONE, "");
//        userToken = Utils.prefrences.getString(Constants.PREFS_VALUE_USER_TOKEN, "");
//        emailid =Utils.prefrences.getString(Constants.PREFS_VALUE_USER_EMAILID, "");

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
        Log.d(TAG, "onEventMainThread->"+message);
			authManager = ModelManager.getInstance().getAuthorizationManager();
			if (message.equalsIgnoreCase("PlayItSafe True")) {
				//new FetchContactFromPhone(PlayItSafeView.this).getClickerList(authManager.getPhoneNo(),authManager.getUsrToken());
				getUserDetails();
			} else if (message.equalsIgnoreCase("PlayItSafe False")) {
				Utils.dismissBarDialog();
				Utils.showAlert(PlayItSafeView.this, authManager.getMessage());
			} else if(message.equalsIgnoreCase("PlayItSafe Network Error")){
				Utils.dismissBarDialog();
				Utils.showAlert(act, AlertMessage.connectionError);	
			}else if (message.equalsIgnoreCase("ProfileInfo True")) {
				Utils.dismissBarDialog();
				switchView();
			} else if (message.equalsIgnoreCase("ProfileInfo False")) {
				Utils.dismissBarDialog();
				Utils.showAlert(PlayItSafeView.this, authManager.getMessage());
			} else if(message.equalsIgnoreCase("ProfileInfo Network Error")){
				Utils.dismissBarDialog();
				Utils.showAlert(act, AlertMessage.connectionError);
				
			}
			
		}
    
    private void getUserDetails(){
    	authManager = ModelManager.getInstance().getAuthorizationManager();
        authManager.getProfileInfo(null,authManager.getPhoneNo(), authManager.getUsrToken());

    }
	private void switchView() {
		Intent intent = new Intent(PlayItSafeView.this, AddSomeoneView.class);
        intent.putExtra("FromOwnProfile", false);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		this.finish();
	}
	
	
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_done_play:
                if(password.getText().toString().length()>7) {
                    if (password.getText().toString().matches(rePassword.getText().toString())) {
                        Utils.launchBarDialog(PlayItSafeView.this);
                        authManager = ModelManager.getInstance().getAuthorizationManager();
                        authManager.playItSafeAuth(password.getText().toString(), authManager.getPhoneNo(), authManager.getEmailId(), authManager.getUsrToken());
                    } else {
                        Utils.showAlert(PlayItSafeView.this, AlertMessage.MATCHPASSWORD);
                    }
                }else {
                    Utils.showAlert(PlayItSafeView.this, AlertMessage.PASSWORDLENGHT);
                }
                break;
        }
    }
}

