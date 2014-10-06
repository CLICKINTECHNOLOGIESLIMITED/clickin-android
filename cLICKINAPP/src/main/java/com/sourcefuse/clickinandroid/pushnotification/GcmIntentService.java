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
import com.sourcefuse.clickinandroid.view.UserProfileView;
import com.sourcefuse.clickinapp.R;


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
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        //sendNotification("Clic");
        //x	Toast.makeText(getApplicationContext(), messageType, Toast.LENGTH_LONG).show();
//8287982999
        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + intent.getExtras().toString(), "");
                Log.e("error occured", intent.getExtras().toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
               // sendNotification("Deleted messages on server: " + intent.getExtras().toString(), "");
                Log.e("error occured", intent.getExtras().toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                Log.i("working", "Completed work @ " + SystemClock.elapsedRealtime());
                Log.e("error occured", intent.getExtras().toString());
                // Post notification of received message.
               /* try {
                    String quizId = null;
                    String msg = null;

                    msg = intent.getExtras().getString("message");
                    JSONObject jsonData = new JSONObject(intent.getExtras().getString("extra"));
                    Log.e("MKKKK", "" + intent.getExtras() + "" + jsonData);
                    if (jsonData.has("success"))
                        if (jsonData.getBoolean("success") == true) {

                            quizId = jsonData.getString("id");
                            sendNotification(msg, quizId);
                        }
                    Log.i("jsonData", "" + jsonData);
                } catch (Exception e) {

                }*/
            }

        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, UserProfileView.class), 0);
        Log.e("recive", msg);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle("ClickIn")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}