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
    String feedId=null;
    int msg_type=-1; //to determine which webservice needs to be hit to update data-monika

    public GcmIntentService() {
        super("GcmIntentService");
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                Bundle data = msg.getData();
                switch (msg.what) {

                    case Constants.USERPROFILE_NOTF:
                        if (!Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().getUsrToken()))
                            ModelManager.getInstance().getRelationManager().getRelationShips(
                                    ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                                    ModelManager.getInstance().getAuthorizationManager().getUsrToken());
                          break;
                    case Constants.FEEDVIEW_NOTF:
                        if (!Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().getUsrToken()))
                        ModelManager.getInstance().getNewsFeedManager().fetchNewsFeed("",
                                ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                                ModelManager.getInstance().getAuthorizationManager().getUsrToken());
                        break;
                    case Constants.POSTVIEW_NOTF:
                        if (!Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().getUsrToken())){
                            ModelManager.getInstance().getProfileManager().getFollwer("",
                                    ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                                    ModelManager.getInstance().getAuthorizationManager().getUsrToken());
                            String feedId=null;
                            if(data.containsKey("FeedId"))
                                 feedId=data.getString("FeedId");
                            if(!Utils.isEmptyString(feedId))
                            ModelManager.getInstance().getNewsFeedManager().ViewNewsFeed(feedId,
                                    ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                                    ModelManager.getInstance().getAuthorizationManager().getUsrToken());
                        }

                        break;
                    case Constants.FOLLOWER_FOLLOWING_NOTF:

                        if (!Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().getUsrToken()))
                        ModelManager.getInstance().getProfileManager().getFollwer(
                              " ",
                                ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                                ModelManager.getInstance().getAuthorizationManager().getUsrToken());

                    break;
                    case Constants.JUMPOTHERPROFILEVIEW_NOTF:


                }
        /*        if (!Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().getUsrToken()))
                    ModelManager.getInstance().getNotificationManagerManager().getNotification(getApplicationContext(),
                            "", ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                            ModelManager.getInstance().getAuthorizationManager().getUsrToken());*/
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
                if (extras.containsKey("Tp")) {
                    Bundle dataToBeSend = new Bundle();
                    String extraKey = extras.getString("Tp");
                    if (extraKey.equalsIgnoreCase("CR") ||
                            extraKey.equalsIgnoreCase("CRA") || extraKey.equalsIgnoreCase("RV") ||
                            extraKey.equalsIgnoreCase("CRR") || extraKey.equalsIgnoreCase("RD")
                            ) {
                        dataToBeSend.putInt("msg_type", Constants.USERPROFILE_NOTF);


                    } else if (extraKey.equalsIgnoreCase("clk") ||
                            extraKey.equalsIgnoreCase("chat") ||
                            extraKey.equalsIgnoreCase("media") ||
                            extraKey.equalsIgnoreCase("card")) {  // case follow request
                        dataToBeSend.putInt("msg_type", Constants.CHATRECORDVIEW_NOTF);
                        //   msg_type=Constants.CHATRECORDVIEW_NOTF;
                    } else if (extras.getString("Tp").equalsIgnoreCase("str") || extras.getString("Tp").equalsIgnoreCase("cmt")
                            || extras.getString("Tp").equalsIgnoreCase("Rpt")) //case for feed star
                    {
                        dataToBeSend.putInt("msg_type", Constants.POSTVIEW_NOTF);
                        dataToBeSend.putString("FeedId", extras.getString("Nid"));
                        // feedId=extras.getString("Nid");
                        //msg_type=Constants.POSTVIEW_NOTF;
                    } else if (extras.getString("Tp").equalsIgnoreCase("shr")) //case for share
                    {
                        dataToBeSend.putInt("msg_type", Constants.FEEDVIEW_NOTF);
                        //    msg_type=Constants.FEEDVIEW_NOTF;
                    } else if (extras.getString("Tp").equalsIgnoreCase("Upp")) //case for Profile Update
                    {
                        dataToBeSend.putInt("msg_type", Constants.JUMPOTHERPROFILEVIEW_NOTF);
                        dataToBeSend.putString("phone_no", extras.getString("phone_no"));
                        msg_type = Constants.JUMPOTHERPROFILEVIEW_NOTF;
                    } else if (extras.getString("Tp").equalsIgnoreCase("FR")) {  // case follow request

                        dataToBeSend.putInt("msg_type", Constants.FOLLOWER_FOLLOWING_NOTF);

                        //  msg_type=Constants.FOLLOWER_FOLLOWING_NOTF;
                    }

                        //common for all- monika
                        sendNotification(intent, extras.getString("chat_message"),dataToBeSend);

                }
            }

            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }


    private void sendNotification(Intent intent,String msg,Bundle data) {
    /* code to fetch notifiacation */

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent contentIntent = null;
        if (intent != null) {
            intent.setClass(getApplicationContext(), ReloadApp.class);
            intent.putExtra("NOTIFICATION_TYPE",data.getInt("msg_type"));
       //     intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            contentIntent = PendingIntent.getActivity(this,NOTIFICATION_ID, intent, PendingIntent.FLAG_ONE_SHOT);
        }


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle("Clickin'")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        .setNumber(NOTIFICATION_ID)
                        .setSound(soundUri).setVibrate(new long[]{0, 100, 200, 300});
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);

        NOTIFICATION_ID = NOTIFICATION_ID + 1;

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();  // check screen state to show notification


          if (!isAppOnForeground(getApplicationContext()) || !isScreenOn)
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        android.os.Message msg1 = new android.os.Message();
        msg1.what = data.getInt("msg_type");
        handler.sendMessage(msg1);
        //   }
        //      android.os.Message msg1 = new android.os.Message();
        //    msg1.what = 2;
        //  handler.sendMessage(msg1);

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