package com.sourcefuse.clickinandroid.pushnotification;


import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.ReloadApp;
import com.sourcefuse.clickinapp.R;

import java.util.List;

import de.greenrobot.event.EventBus;

import android.os.Handler;

public class GcmIntentService extends IntentService {
    public static int NOTIFICATION_ID = 1;
    RelationManager mRelationManager;
    private NotificationManager mNotificationManager;
    Handler handler;

    public GcmIntentService() {
        super("GcmIntentService");
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
                        if (!Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().getUsrToken()))
                            ModelManager.getInstance().getRelationManager().getRelationShips(
                                    ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                                    ModelManager.getInstance().getAuthorizationManager().getUsrToken());
                        break;
                    case 2:
                        if (!Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().getUsrToken()))
                            ModelManager.getInstance().getNotificationManagerManager().getNotification(getApplicationContext(),
                                    "", ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                                    ModelManager.getInstance().getAuthorizationManager().getUsrToken());

                }
            }
        };

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        // EventBus.getDefault().post("update Counter");
        mRelationManager = ModelManager.getInstance().getRelationManager();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);


        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                Log.e("in this case-------->", "in this case-------->");

                /*try {

                    JSONObject jsonObject = new JSONObject();
                    for (String key : extras.keySet()) {
                        jsonObject.put(key, extras.get(key));
                        android.util.Log.e("key gcm------>", key);
                        android.util.Log.e("key value gcm------>", "" + extras.get(key));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                Intent data = new Intent();
                data.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
/* CR for clickin with from other userpage and also form add someone from contact */
/* CRA when user accept clickinwith request from tick button */
/* RD when user remove other user from clickin with list */
/*  FR when user request to follow using fllow button  */
/* clk when user send click request */
/* chat when send chat */

                if (extras.containsKey("Tp")) {

                    if (extras.getString("Tp").equalsIgnoreCase("CR") ||
                            extras.getString("Tp").equalsIgnoreCase("CRA") || extras.getString("Tp").equalsIgnoreCase("RV") ||
                            extras.getString("Tp").equalsIgnoreCase("CRR")
                            ) {

                        data.putExtra("Tp", extras.getString("Tp"));
                        sendNotification("Clickin'", extras.getString("chat_message"), data);
                    } else if (extras.getString("Tp").equalsIgnoreCase("FR")) {  // case follow request

                        data.putExtra("FromOwnProfile", true);
                        data.putExtra("Tp", extras.getString("Tp"));
                        sendNotification("Clickin'", extras.getString("chat_message"), data);
                    } else if (extras.getString("Tp").equalsIgnoreCase("clk")) {


                        data.putExtra("Tp", extras.getString("Tp"));
                        String mPartnerId = extras.getString("pid");
                        data.putExtra("pid", mPartnerId);

                        sendNotification("Clickin'", extras.getString("chat_message"), data);

                    } else if (extras.getString("Tp").equalsIgnoreCase("chat")) {

                        data.putExtra("Tp", extras.getString("Tp"));
                        String mPartnerId = extras.getString("pid");
                        data.putExtra("pid", mPartnerId);


                        if (extras.getString("message").contains(getResources().getString(R.string.chat_msg))) {
                            sendNotification("Clickin'", extras.getString("chat_message"), data);
                        }
                        if (extras.getString("message").contains(getResources().getString(R.string.chat_image))) {
                            sendNotification("Clickin'", extras.getString("chat_message"), data);
                        }
                    } else if (extras.getString("Tp").equalsIgnoreCase("RD")) {

                        data.putExtra("Tp", extras.getString("Tp"));
                        sendNotification("Clickin'", extras.getString("message"), data);
                    } else if (extras.getString("Tp").equalsIgnoreCase("media")) {  // case when share media and send media

                        String mPartnerId = extras.getString("pid");
                        data.putExtra("pid", mPartnerId);
                        data.putExtra("Tp", extras.getString("Tp"));
                        sendNotification("Clickin'", extras.getString("message"), data);
                    } else if (extras.getString("Tp").equalsIgnoreCase("shr")) //case for share
                    {

                        data.putExtra("Tp", extras.getString("Tp"));
                        sendNotification("Clickin'", extras.getString("chat_message"), data);


                    } else if (extras.getString("Tp").equalsIgnoreCase("Upp")) //case for Profile Update
                    {

                        data.putExtra("Tp", extras.getString("Tp"));
                        data.putExtra("FromOwnProfile", true);
                        data.putExtra("phone_no", extras.getString("phone_no"));

                        sendNotification("Clickin'", extras.getString("message"), data);
                        EventBus.getDefault().post("updatePhoto");

                    } else if (extras.getString("Tp").equalsIgnoreCase("card")) //case for card
                    {

                        String mPartnerId = extras.getString("pid");

                        data.putExtra("Tp", extras.getString("Tp"));
                        data.putExtra("pid", mPartnerId);
                        sendNotification("Clickin'", extras.getString("chat_message"), data);
                    } else if (extras.getString("Tp").equalsIgnoreCase("str") || extras.getString("Tp").equalsIgnoreCase("cmt")
                            || extras.getString("Tp").equalsIgnoreCase("Rpt")) //case for feed star
                    {


                        data.putExtra("Nid", extras.getString("Nid"));
                        data.putExtra("Tp", extras.getString("Tp"));
                        sendNotification("Clickin'", extras.getString("chat_message"), data);


                    }

                }


            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    private void sendNotification(String title, String msg, Intent intent) {





        /* code to fetch notifiacation */

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent contentIntent = null;
        if (intent != null) {
            intent.setClass(getApplicationContext(), ReloadApp.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            contentIntent = PendingIntent.getActivity(this,NOTIFICATION_ID, intent, PendingIntent.FLAG_ONE_SHOT);
        }else {
            Log.e("intent null","intent null");
        }


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        .setNumber(NOTIFICATION_ID)
                        .setSound(soundUri).setVibrate(new long[]{0, 100, 200, 300});
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);

        NOTIFICATION_ID = NOTIFICATION_ID + 1;

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();  // check screen state to show notification


        if (!isAppOnForeground(getApplicationContext()) || !isScreenOn) {
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            android.os.Message msg1 = new android.os.Message();
            msg1.what = 1;
            handler.sendMessage(msg1);
        }
        android.os.Message msg1 = new android.os.Message();
        msg1.what = 2;
        handler.sendMessage(msg1);

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