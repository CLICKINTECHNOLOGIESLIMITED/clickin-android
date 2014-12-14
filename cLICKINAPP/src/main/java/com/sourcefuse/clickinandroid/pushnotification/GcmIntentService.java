package com.sourcefuse.clickinandroid.pushnotification;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ClickInNotificationManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.view.FollowerList;
import com.sourcefuse.clickinandroid.view.UserProfileView;
import com.sourcefuse.clickinapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;


public class GcmIntentService extends IntentService {
    private static final String TAG = AuthManager.class.getSimpleName();
    public static int NOTIFICATION_ID = 1;
    NotificationCompat.Builder builder;
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
        Log.e(" In GcmIntentService.....", "In GcmIntentService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        Log.e("gcm notification---->", "" + extras);

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);


        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i("working", "Completed work @ " + SystemClock.elapsedRealtime());
                try {
                    JSONObject jsonObject = new JSONObject();
                    for (String key : extras.keySet()) {
                        jsonObject.put(key, extras.get(key));
                        Log.e("key gcm------>", key);
                        Log.e("key value gcm------>", "" + extras.get(key));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent data = new Intent();
                data.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                data.putExtra("isChangeInList", true);
/* CR for clickin with from other userpage and also form add someone from contact */
/* CRA when user accept clickinwith request from tick button */
/* RD when user remove other user from clickin with list */
/*  FR when user request to follow using fllow button  */
/* clk when user send click request */
/* chat when send chat */

                if (extras.containsKey("Tp")) {
                    com.sourcefuse.clickinandroid.utils.Log.e("contains Tp", "contains TP");
                    com.sourcefuse.clickinandroid.utils.Log.e("value of Tp", "" + extras.getString("TP"));
                    if (extras.getString("Tp").equalsIgnoreCase("CR") || extras.getString("Tp").equalsIgnoreCase("RD") ||
                            extras.getString("Tp").equalsIgnoreCase("CRA") || extras.getString("Tp").equalsIgnoreCase("RV")
                            ) {
                        data.setClass(getApplicationContext(), UserProfileView.class);
                        sendNotification("Clickin'", extras.getString("chat_message"), data);
                    } else if (extras.getString("Tp").equalsIgnoreCase("FR")) {
                        data.setClass(getApplicationContext(), FollowerList.class);
                        data.putExtra("FromOwnProfile", true);
                        sendNotification("Clickin'", extras.getString("chat_message"), data);
                    } else if (extras.getString("Tp").equalsIgnoreCase("clk")) {
                        data.setClass(getApplicationContext(), UserProfileView.class);
                        sendNotification("Clickin'", extras.getString("chat_message"), data);
                        /*  pending */
                    } else if (extras.getString("Tp").equalsIgnoreCase("chat")) {
                        data.setClass(getApplicationContext(), UserProfileView.class);
                        /**/
                        if (extras.getString("message").contains(getResources().getString(R.string.chat_msg))) {
                            sendNotification("Clickin'", extras.getString("chat_message"), data);
                        }
                        if (extras.getString("message").contains(getResources().getString(R.string.chat_image))) {
                            sendNotification("Clickin'", extras.getString("chat_message"), data);
                        }
                    }


                }


            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    private void sendNotification(String title, String msg, Intent intent) {


        /* code to fetch notifiacation */

        AuthManager authManager = ModelManager.getInstance().getAuthorizationManager();
        RelationManager relationManager = ModelManager.getInstance().getRelationManager();
        ClickInNotificationManager notificationMngr = ModelManager.getInstance().getNotificationManagerManager();
        notificationMngr.getNotification(getApplicationContext(), "", authManager.getPhoneNo(), authManager.getUsrToken());

        int counter = authManager.getNotificationCounter();
        authManager.setNotificationCounter(counter + 1);

        /* code to fetch notifiacation */

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent contentIntent = null;
        if (intent != null) {
            contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        .setSound(soundUri).setVibrate(new long[]{0, 100, 200, 300});
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);

        NOTIFICATION_ID = +1;

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        EventBus.getDefault().post("update Counter");

    }


}