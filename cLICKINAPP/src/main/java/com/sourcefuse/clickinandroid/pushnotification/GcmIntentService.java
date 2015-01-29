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
                            extras.getString("Tp").equalsIgnoreCase("CRA") || extras.getString("Tp").equalsIgnoreCase("RV") ||
                            extras.getString("Tp").equalsIgnoreCase("CRR")
                            ) {

                        data.setClass(getApplicationContext(), UserProfileView.class);
                        UpdateCounter();
                        sendNotification("Clickin'", extras.getString("chat_message"), data);
                    } else if (extras.getString("Tp").equalsIgnoreCase("FR")) {  // case follow request

                        data.setClass(getApplicationContext(), FollowerList.class);
                        data.putExtra("FromOwnProfile", true);
                        UpdateCounter();
                        sendNotification("Clickin'", extras.getString("chat_message"), data);
                    } else if (extras.getString("Tp").equalsIgnoreCase("clk")) {

                        data.setClass(getApplicationContext(), ChatRecordView.class);
                        String mPartnerId = extras.getString("pid");

                        putChatData(data, mPartnerId);

                        sendNotification("Clickin'", extras.getString("chat_message"), data);


                        /*  pending */
                    } else if (extras.getString("Tp").equalsIgnoreCase("chat")) {

                        data.setClass(getApplicationContext(), ChatRecordView.class);
                        String mPartnerId = extras.getString("pid");

                        putChatData(data, mPartnerId);
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
                        sendNotification("Clickin'", extras.getString("message"), data);
                    } else if (extras.getString("Tp").equalsIgnoreCase("media")) {  // case when share media and send media

                        String mPartnerId = extras.getString("pid");
                        putChatData(data, mPartnerId);
                        data.setClass(getApplicationContext(), ChatRecordView.class);
                        sendNotification("Clickin'", extras.getString("message"), data);
                    } else if (extras.getString("Tp").equalsIgnoreCase("shr")) //case for share
                    {

                        data.setClass(getApplicationContext(), FeedView.class);
                        UpdateCounter();
                        sendNotification("Clickin'", extras.getString("chat_message"), data);


                    } else if (extras.getString("Tp").equalsIgnoreCase("Upp")) //case for Profile Update
                    {

                        data.setClass(getApplicationContext(), JumpOtherProfileView.class);
                        data.putExtra("FromOwnProfile", true);
                        data.putExtra("phNumber", extras.getString("phone_no"));
                        UpdateCounter();
                        sendNotification("Clickin'", extras.getString("message"), data);
                        EventBus.getDefault().post("updatePhoto");
                        PicassoManager.clearCache();

                    } else if (extras.getString("Tp").equalsIgnoreCase("card")) //case for card
                    // case when card accepted
                    // case when card rejected
                    // case when counter card
                    // case when want to share card
                    {

                        String mPartnerId = extras.getString("pid");
                        putChatData(data, mPartnerId);
                        data.setClass(getApplicationContext(), ChatRecordView.class);
                        sendNotification("Clickin'", extras.getString("chat_message"), data);
                    } else if (extras.getString("Tp").equalsIgnoreCase("str") || extras.getString("Tp").equalsIgnoreCase("cmt")
                            || extras.getString("Tp").equalsIgnoreCase("Rpt")) //case for feed star
                    {

                        data.setClass(getApplicationContext(), PostView.class);
                        data.putExtra("feedId",extras.getString("Nid"));
                        UpdateCounter();
                        sendNotification("Clickin'", extras.getString("chat_message"), data);


                    }

                }


            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    public void putChatData(Intent mIntent, String mPartnerId) {


        for (int i = 0; i < mRelationManager.acceptedList.size(); i++) {
            if (mRelationManager.acceptedList.get(i).getPartner_id().equalsIgnoreCase(mPartnerId)) {
                mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mIntent.setAction("UPDATE");
                mIntent.putExtra("quickId", mRelationManager.acceptedList.get(i).getPartnerQBId());
                mIntent.putExtra("partnerPic", mRelationManager.acceptedList.get(i).getPartnerPic());
                mIntent.putExtra("partnerName", mRelationManager.acceptedList.get(i).getPartnerName());
                mIntent.putExtra("rId", mRelationManager.acceptedList.get(i).getRelationshipId());
                mIntent.putExtra("partnerId", mRelationManager.acceptedList.get(i).getPartner_id());

                mIntent.putExtra("myClicks", mRelationManager.acceptedList.get(i).getUserClicks());
                mIntent.putExtra("userClicks", mRelationManager.acceptedList.get(i).getClicks());
                mIntent.putExtra("partnerPh", mRelationManager.acceptedList.get(i).getPhoneNo());
                mIntent.putExtra("relationListIndex", i);

                ChatManager chatManager = ModelManager.getInstance().getChatManager();
                chatManager.setrelationshipId(mRelationManager.acceptedList.get(i).getRelationshipId());
            }
        }


    }

    private void UpdateCounter() // to update counter
    {
        AuthManager authManager = ModelManager.getInstance().getAuthorizationManager();
        int counter = authManager.getNotificationCounter();
        authManager.setNotificationCounter(counter + 1);
        EventBus.getDefault().postSticky("update Counter");
        Intent intent = new Intent();
        Constants.mInAppNotification = true;

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