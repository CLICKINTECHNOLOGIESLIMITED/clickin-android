package com.sourcefuse.clickinandroid.pushnotification;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sourcefuse.clickinandroid.model.AuthManager;

import com.sourcefuse.clickinapp.R;

import org.json.JSONException;
import org.json.JSONObject;


public class GcmIntentService extends IntentService {
    private static final String TAG = AuthManager.class.getSimpleName();
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
        Log.e(" In GcmIntentService.....", "In GcmIntentService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        try {
            JSONObject jsonObject = new JSONObject();
            for (String key : extras.keySet()) {
                jsonObject.put(key, extras.get(key));
                Log.e("key------>", key);
                Log.e("key value ------>", "" + extras.get(key));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.e("error occured", intent.getExtras().toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.e("error occured", intent.getExtras().toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i("working", "Completed work @ " + SystemClock.elapsedRealtime());
                Log.e("error occured", intent.getExtras().toString());
            }

        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);


    }

}