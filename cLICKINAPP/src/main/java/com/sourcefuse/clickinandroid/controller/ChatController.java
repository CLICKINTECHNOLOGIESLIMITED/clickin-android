package com.sourcefuse.clickinandroid.controller;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.ModelManager;

public class ChatController implements Observer{
	private ChatManager chatManager;
	Activity context;
	
	
	public ChatController() {
		chatManager = ModelManager.getInstance().getChatManager();
		chatManager.addObserver(this);
	}

	public void chatRecords(String relationshipId, String phone,
			String usertoken, Activity context) {
		this.context = context;
		chatManager = ModelManager.getInstance().getChatManager();
		//chatManager.fetchChatRecord(relationshipId, phone, usertoken);
	}
	
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// // TODO Auto-generated method stub
		Log.e("-update-", "update--> " + arg1);
		Intent intent = null;
		if (arg1 instanceof String) {
			if (((String) arg1).startsWith("FecthChat")) {
				intent = new Intent("FecthChat");
			}
			intent.putExtra("message", arg1.toString());
		}
		if (intent != null)
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

	}

}
