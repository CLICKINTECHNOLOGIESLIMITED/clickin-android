package com.sourcefuse.clickinandroid.pushnotification;


import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.PicassoManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.view.ChatRecordView;
import com.sourcefuse.clickinandroid.view.FeedView;
import com.sourcefuse.clickinandroid.view.FollowerList;
import com.sourcefuse.clickinandroid.view.JumpOtherProfileView;
import com.sourcefuse.clickinandroid.view.PostView;
import com.sourcefuse.clickinandroid.view.ReloadApp;
import com.sourcefuse.clickinandroid.view.UserProfileView;
import com.sourcefuse.clickinapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.greenrobot.event.EventBus;


public class GcmIntentService extends IntentService {
    private static final String TAG = AuthManager.class.getSimpleName();
    public static int NOTIFICATION_ID = 1;
    NotificationCompat.Builder builder;
    private NotificationManager mNotificationManager;
    RelationManager mRelationManager;

    public GcmIntentService() {
        super("GcmIntentService");


    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        mRelationManager = ModelManager.getInstance().getRelationManager();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);


        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                Log.e("in this case-------->", "in this case-------->");

                try {

                    JSONObject jsonObject = new JSONObject();
                    for (String key : extras.keySet()) {
                        jsonObject.put(key, extras.get(key));
                        android.util.Log.e("key gcm------>", key);
                        android.util.Log.e("key value gcm------>", "" + extras.get(key));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

 /*CR for clickin with from other userpage and also form add someone from contact
 CRA when user accept clickinwith request from tick button
 RD when user remove other user from clickin with list
  FR when user request to follow using fllow button
 clk when user send click request
 chat when send chat */

                if (extras.containsKey("Tp")) {

                    if (extras.getString("Tp").equalsIgnoreCase("CR") ||
                            extras.getString("Tp").equalsIgnoreCase("CRA") || extras.getString("Tp").equalsIgnoreCase("RV") ||
                            extras.getString("Tp").equalsIgnoreCase("CRR") || extras.getString("Tp").equalsIgnoreCase("clk")
                            || extras.getString("Tp").equalsIgnoreCase("RD")
                            ) {

                        //data.setClass(getApplicationContext(), UserProfileView.class);

                        Log.e("in case 1","in case 1");
                        if (ModelManager.getInstance().getAuthorizationManager().getUserId() == null) {
                            ModelManager.getInstance().getAuthorizationManager().
                                    getProfileInfo("", ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                                            ModelManager.getInstance().getAuthorizationManager().getUsrToken());
                        }
                    }

                }

                Intent intent1 = new Intent();
                Bundle bundle = new Bundle();
                bundle.putAll(extras);
                intent1.setClass(getApplicationContext(), ReloadApp.class);
                intent1.putExtras(bundle);

                /*intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setClass(getApplicationContext(), ReloadApp.class);*/
                sendNotification("Clickin'", extras.getString("chat_message"), intent1);

            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    private void sendNotification(String title, String msg, Intent intent) {

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

        NOTIFICATION_ID = NOTIFICATION_ID + 1;

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();  // check screen state to show notification


        if (!isAppOnForeground(getApplicationContext()) || !isScreenOn)
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());


    }


    private boolean isAppOnForeground(Context context) { // check application state
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }


        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {

            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

}