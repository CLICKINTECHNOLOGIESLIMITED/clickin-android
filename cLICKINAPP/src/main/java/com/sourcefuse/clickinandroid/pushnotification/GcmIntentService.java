package com.sourcefuse.clickinandroid.pushnotification;


import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
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
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.GetrelationshipsBean;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.ChatRecordView;
import com.sourcefuse.clickinandroid.view.FollowerList;
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

        mRelationManager  = ModelManager.getInstance().getRelationManager();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);


        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

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

                    if (extras.getString("Tp").equalsIgnoreCase("CR") ||
                            extras.getString("Tp").equalsIgnoreCase("CRA") || extras.getString("Tp").equalsIgnoreCase("RV")
                            ) {
                        data.setClass(getApplicationContext(), UserProfileView.class);
                        UpdateCounter();
                        sendNotification("Clickin'", extras.getString("chat_message"), data);
                    } else if (extras.getString("Tp").equalsIgnoreCase("FR")) {
                        data.setClass(getApplicationContext(), FollowerList.class);
                        data.putExtra("FromOwnProfile", true);
                        UpdateCounter();
                        sendNotification("Clickin'", extras.getString("chat_message"), data);
                    } else if (extras.getString("Tp").equalsIgnoreCase("clk")) {
                        data.setClass(getApplicationContext(), ChatRecordView.class);
                        String mPartnerId = extras.getString("pid");
                        String mRId = extras.getString("Rid");
                        putChatData(data,mPartnerId,mRId);
                        sendNotification("Clickin'", extras.getString("chat_message"), data);


                        /*  pending */
                    } else if (extras.getString("Tp").equalsIgnoreCase("chat")) {
                        data.setClass(getApplicationContext(), ChatRecordView.class);
                        String mPartnerId = extras.getString("pid");
                        String mRId = extras.getString("Rid");
                        putChatData(data,mPartnerId,mRId);
                        /**/
                        if (extras.getString("message").contains(getResources().getString(R.string.chat_msg))) {
                            sendNotification("Clickin'", extras.getString("chat_message"), data);
                        }
                        if (extras.getString("message").contains(getResources().getString(R.string.chat_image))) {
                            sendNotification("Clickin'", extras.getString("chat_message"), data);
                        }
                    } else if (extras.getString("Tp").equalsIgnoreCase("RD")) {
                        data.setClass(getApplicationContext(), UserProfileView.class);
                        UpdateCounter();
                        sendNotification("Clickin'", extras.getString("message"), null);
                    } else if (extras.getString("Tp").equalsIgnoreCase("media")) {
                        String mPartnerId = extras.getString("pid");
                        String mRId = extras.getString("Rid");
                        putChatData(data,mPartnerId,mRId);
                        data.setClass(getApplicationContext(), ChatRecordView.class);
                        sendNotification("Clickin'", extras.getString("message"), null);
                    }else if(extras.getString("Tp").equalsIgnoreCase("shr")) //case for share
                    {

                        data.setClass(getApplicationContext(), ChatRecordView.class);

                        //chat_message
                        //Tp
                        //Nid
                        //from
                        //message

                    }


                }


            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    public void putChatData(Intent mIntent,String mPartnerId,String mRid)
    {


        for( int i =0 ;i<mRelationManager.acceptedList.size();i++)
        {
            if(mRelationManager.acceptedList.get(i).getPartner_id().equalsIgnoreCase(mPartnerId))
            {
                mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mIntent.setAction("UPDATE");
                mIntent.putExtra("quickId", mRelationManager.acceptedList.get(i).getPartnerQBId());
                mIntent.putExtra("partnerPic", mRelationManager.acceptedList.get(i).getPartnerPic());
                mIntent.putExtra("partnerName", mRelationManager.acceptedList.get(i).getPartnerName());
                mIntent.putExtra("rId", mRid);
                mIntent.putExtra("partnerId", mRelationManager.acceptedList.get(i).getPartner_id());
                Log.e("partner id--->", "" + mRelationManager.acceptedList.get(i).getPartner_id());
                mIntent.putExtra("myClicks", mRelationManager.acceptedList.get(i).getUserClicks());
                mIntent.putExtra("userClicks", mRelationManager.acceptedList.get(i).getClicks());
                mIntent.putExtra("partnerPh", mRelationManager.acceptedList.get(i).getPhoneNo());
                mIntent.putExtra("relationListIndex", i);

                ChatManager chatManager = ModelManager.getInstance().getChatManager();
                chatManager.setrelationshipId(mRid);
            }
        }







    }
    private void UpdateCounter() // to update counter
    {
        AuthManager authManager = ModelManager.getInstance().getAuthorizationManager();
        int counter = authManager.getNotificationCounter();
        authManager.setNotificationCounter(counter + 1);
        EventBus.getDefault().postSticky("update Counter");

    }

    private void sendNotification(String title, String msg, Intent intent) {


        /* code to fetch notifiacation */



        /* code to fetch notifiacation */

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent contentIntent = null;
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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