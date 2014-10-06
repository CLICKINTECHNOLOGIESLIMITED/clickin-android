package com.sourcefuse.clickinandroid.controller;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.NewsFeedManager;

public class NewsFeedController implements Observer {
	private NewsFeedManager newsFeedMgr;
	Activity context;

	public NewsFeedController() {
		newsFeedMgr = ModelManager.getInstance().getNewsFeedManager();
		newsFeedMgr.addObserver(this);
	}

	public void getNewsFeed(String lastNewsfeedId, String phone,
			String usertoken, Activity context) {
		this.context = context;
		newsFeedMgr = ModelManager.getInstance().getNewsFeedManager();
		newsFeedMgr.fetchNewsFeed(lastNewsfeedId, phone, usertoken);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// // TODO Auto-generated method stub
		Log.e("-update-", "update--> " + arg1);
		Intent intent = null;
		if (arg1 instanceof String) {
			if (((String) arg1).startsWith("News Feed")) {
				intent = new Intent("News Feed");
			}
			intent.putExtra("message", arg1.toString());
		}
		if (intent != null)
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

	}

}
