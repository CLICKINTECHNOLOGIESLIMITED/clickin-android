package com.sourcefuse.clickinandroid.controller;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;

import android.support.v4.content.LocalBroadcastManager;

public class ProfileController implements Observer {

	Activity context;
	ProfileManager profileMngr;
	

	public ProfileController() {
		profileMngr = ModelManager.getInstance().getProfileManager();
		profileMngr.addObserver(this);
	}

	public void setUserProfile(String fname, String lname, String phone,
			String usertoken, String gender, String dob, String city,
			String country, String email, String fbaccesstoken, String userpic,
			Activity context) {
		this.context = context;
		profileMngr = ModelManager.getInstance().getProfileManager();
		profileMngr.setProfile(fname, lname, phone, usertoken, gender, dob, city,
				country, email, fbaccesstoken, userpic);
	}
	
	
	public void getProfileFollower(String othersPhone,String phone,String usertoken,Activity context) {
		this.context = context;
		profileMngr = ModelManager.getInstance().getProfileManager();
		profileMngr.getFollwer(othersPhone,phone, usertoken);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// // TODO Auto-generated method stub
		Log.e("-update-", "update--> " + arg1);
		Intent intent = null;
		if (arg1 instanceof String) {
			if (((String) arg1).startsWith("UpdateProfile")) {
				intent = new Intent("UpdateProfile");
			}else if (((String) arg1).startsWith("GetFollower")) {
				intent = new Intent("GetFollower");
			}else if (((String) arg1).startsWith("ProfileInfo")) {
				intent = new Intent("ProfileInfo");
			}
			intent.putExtra("message", arg1.toString());
		}
		if (intent != null)
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

	}

}
