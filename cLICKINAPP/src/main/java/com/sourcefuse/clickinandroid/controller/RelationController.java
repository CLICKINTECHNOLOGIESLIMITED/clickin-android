package com.sourcefuse.clickinandroid.controller;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.RelationManager;

import java.util.Observable;
import java.util.Observer;

public class RelationController implements Observer {

	Activity context;
	RelationManager relationManager;

	public RelationController() {
		relationManager = ModelManager.getInstance().getRelationManager();
		relationManager.addObserver(this);
	}

	public void getUserRelationShips(String phone, String usertoken, Activity context) {
		this.context = context;
		relationManager = ModelManager.getInstance().getRelationManager();
		relationManager.getRelationShips(phone, usertoken);
	}
	
	public void getProfilerelationships(String othersPhone,String phone, String usertoken, Activity context) {
		this.context = context;
		relationManager = ModelManager.getInstance().getRelationManager();
		relationManager.fetchprofilerelationships(othersPhone,phone, usertoken);
	}
	
	
	public void makeFollowUser(String followeePhoneNo,String phone, String usertoken, Activity context) {
		this.context = context;
		relationManager = ModelManager.getInstance().getRelationManager();
		relationManager.followUser(followeePhoneNo, phone, usertoken);
	}
	
	public void makeUnFollowUser(String followId,String followingMode,String phone, String usertoken,Activity context) {
		this.context = context;
		relationManager = ModelManager.getInstance().getRelationManager();
		relationManager.unFollowUser(followId,followingMode, phone, usertoken);
	}
	public void userVisibility(String relationid,String mode,String phone, String usertoken,Activity context) {
		this.context = context;
		relationManager = ModelManager.getInstance().getRelationManager();
		relationManager.changeUserVisibility(relationid, mode, phone, usertoken);
	}
	
	//
	

	public void updateStatus(String relationshipIid,String mode,String phone, String usertoken,Activity context) {
		this.context = context;
		relationManager = ModelManager.getInstance().getRelationManager();
		relationManager.updateStatus(relationshipIid, phone, usertoken, mode);
	}
	public void deleteRelationship(String relationid,String phone, String usertoken,Activity context) {
		this.context = context;
		relationManager = ModelManager.getInstance().getRelationManager();
		relationManager.deleteRelationship(relationid, phone, usertoken);
	}
	
	
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub PlayItSafe
		Log.e("-update-", "update--> " + arg1);
		Intent intent = null;
		if (arg1 instanceof String) {
			if (((String) arg1).startsWith("GetrelationShips")) {
				intent = new Intent("GetrelationShips");
			}else if (((String) arg1).startsWith("Fetchprofilerelationships")) {
				intent = new Intent("Fetchprofilerelationships");
			}else if (((String) arg1).startsWith("FollowUser")) {
				intent = new Intent("FollowUser");
			}else if (((String) arg1).startsWith("UnFollowUser")) {
				intent = new Intent("UnFollowUser");
			}else if (((String) arg1).startsWith("UserVisible")) {
				intent = new Intent("UserVisible");
			}else if (((String) arg1).startsWith("updateStatus")) {
				intent = new Intent("updateStatus");
			}else if (((String) arg1).startsWith("deleteRelationship")) {
				intent = new Intent("deleteRelationship");
			}
			intent.putExtra("message", arg1.toString());
		}
		if (intent != null)
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

	}
}
