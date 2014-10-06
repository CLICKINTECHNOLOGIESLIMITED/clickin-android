package com.sourcefuse.clickinandroid.controller;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;

import java.util.Observable;
import java.util.Observer;

import de.greenrobot.event.EventBus;

public class AuthController implements Observer {
	Activity context;
	AuthManager auth;

	public AuthController() {
		auth = ModelManager.getInstance().getAuthorizationManager();
		auth.addObserver(this);
	}
	
	public void doLogin(String username, String password,String deviceToken, String deviceType, Activity context) {
		this.context = context;
		auth = ModelManager.getInstance().getAuthorizationManager();
		auth.signIn(username, password,deviceToken,deviceType);
	}

	public void doSignUP(String phone, String deviceToken, Activity context) {
		this.context = context;
		auth = ModelManager.getInstance().getAuthorizationManager();
		auth.signUpAuth(phone,deviceToken);
	}
	
	public void getVerifyCode(String phone,String deviceToken,String vCode,String deviceType,Activity context) {
		this.context = context;
		auth = ModelManager.getInstance().getAuthorizationManager();
		auth.getVerifyCode(phone,deviceToken,vCode,deviceType);
	}
	
	public void playItSafe(String password,String phone,String email,String urserToken, Activity context) {
		this.context = context;
		auth = ModelManager.getInstance().getAuthorizationManager();
		auth.playItSafeAuth(password,phone,email,urserToken);
	}
	
	public void resSendVerifyCode(String phone,String deviceToken, Activity context) {
		this.context = context;
		auth = ModelManager.getInstance().getAuthorizationManager();
		auth.reSendVerifyCode(phone,deviceToken);
	}
	public void sendRequst(String phone,String partnerPh,String usrToken, Activity context) {
		this.context = context;
		auth = ModelManager.getInstance().getAuthorizationManager();
		auth.sendNewRequest(phone,partnerPh,usrToken);
	}
	
	
	public void getProfileDetails(String othersphone,String phone,String usrToken, Activity context) {
		this.context = context;
		auth = ModelManager.getInstance().getAuthorizationManager();
		auth.getProfileInfo(othersphone,phone,usrToken);
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub PlayItSafe
		Log.e("-update-","update--> "+arg1);
		Intent intent = null;
		if (arg1 instanceof String) {
			if (((String) arg1).startsWith("Verify")) {
				intent = new Intent("Verify");
			} else if (((String) arg1).startsWith("ReSendVerifyCode")) {
				intent = new Intent("ReSendVerifyCode");
			}else if (((String) arg1).startsWith("PlayItSafe")) {
				intent = new Intent("PlayItSafe");
			}else if (((String) arg1).startsWith("RequestSend")) {
				intent = new Intent("RequestSend");
			}else if (((String) arg1).startsWith("ProfileInfo")) {
				intent = new Intent("ProfileInfo");
			}
			
			intent.putExtra("message", arg1.toString());
		}
			if (intent != null){
                EventBus.getDefault().post(intent);
            }
				//LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

		
	}
}
