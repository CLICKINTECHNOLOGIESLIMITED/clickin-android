package com.sourcefuse.clickinandroid.services;

/**
 * Created by mukesh on 15/11/14.
 */

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.result.Result;
import com.quickblox.module.custom.QBCustomObjects;
import com.quickblox.module.custom.model.QBCustomObject;
import com.quickblox.module.custom.result.QBCustomObjectResult;
import com.sourcefuse.clickinandroid.dbhelper.ClickinDbHelper;
import com.sourcefuse.clickinandroid.model.bean.ChatMessageBody;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import org.jivesoftware.smack.packet.Message;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import de.greenrobot.event.EventBus;

/**
 * Created by monika on 03/10/14.
 */
public class MyQbChatService extends Service {

    private final IBinder mBinder = new LocalBinder();
    Pattern p = Pattern.compile("[\\d]+_(.*?)@.*?");
    private NotificationManager mNM;
    private int mId = 0;
    private ChatThread mChatThread;
    public static final int MSG_DELIVERED=1;

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        EventBus.getDefault().register(this);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
              switch(msg.what){
                  case MSG_DELIVERED:
                      Bundle data=msg.getData();
                      HashMap<String, Object> fields = new HashMap<String, Object>();
                      fields.put("type", Constants.CHAT_TYPE_DELIVERED);

                      fields.put("chatId", data.getString("chatID"));

                      fields.put("deliveredChatID", data.getString("deliveredChatID"));

                      QBCustomObject qbCustomObject = new QBCustomObject();
                      qbCustomObject.setClassName("chats");  // your Class name
                      qbCustomObject.setFields(fields);

                      // Activity currentActivity = application.getApplicationContext().getCurrentActivity();
                      QBCustomObjects.createObject(qbCustomObject, new QBCallbackImpl() {
                          @Override
                          public void onComplete(Result result) {
                              if (result.isSuccess()) {
                                  QBCustomObjectResult qbCustomObjectResult = (QBCustomObjectResult) result;
                                  QBCustomObject qbCustomObject = qbCustomObjectResult.getCustomObject();

                              } else {

                              }
                          }
                      });

              }
            }
        };
        mChatThread = new ChatThread(getApplication(), handler);
        mChatThread.start();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private boolean isAppOnForeground(Context context) {
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

    public void sendMessage(ChatMessageBody msgObject) {
        Handler handler = mChatThread.getHandler();
        android.os.Message msg = new android.os.Message();
        Bundle data = new Bundle();
        data.putString("partnerQBId", msgObject.partnerQbId);
        // if(Utils.isEmptyString(msgObject.textMsg))
        data.putString("textMsg", msgObject.textMsg);
        data.putInt("ChatType", msgObject.chatType);

        if (Utils.isEmptyString(msgObject.isAccepted)) { //in case of shared accept/reject no clicks there, no chatid
            data.putString("clicks", msgObject.clicks);

        }
        data.putString("ChatId", msgObject.chatId);

        switch (msgObject.chatType) {
            case Constants.CHAT_TYPE_CARD:
                if (!msgObject.is_CustomCard) {
                    data.putString("card_DB_ID", msgObject.card_DB_ID);
                    data.putString("card_content", msgObject.card_content);
                    data.putString("card_url", msgObject.card_url);
                }
                // data.putString("card_clicks",msgObject.clicks);
                data.putString("card_owner", msgObject.card_owner);
                data.putBoolean("is_CustomCard", msgObject.is_CustomCard);
                data.putString("accepted_Rejected", msgObject.card_Accepted_Rejected);
                data.putString("card_heading", msgObject.card_heading);
                data.putString("card_id", msgObject.card_id);
                data.putString("card_Played_Countered", msgObject.card_Played_Countered);
                data.putString("card_originator", msgObject.card_originator);
                break;
            case Constants.CHAT_TYPE_IMAGE:
                data.putString("imageRatio", msgObject.imageRatio);
                data.putString("FileId", msgObject.content_url);
                break;
            case Constants.CHAT_TYPE_AUDIO:
                data.putString("FileId", msgObject.content_url);
                break;
            case Constants.CHAT_TYPE_VIDEO:
                data.putString("videoThumbnail", msgObject.video_thumb);
                data.putString("FileId", msgObject.content_url);
                break;
            case Constants.CHAT_TYPE_LOCATION:
                data.putString("location_coordinates", msgObject.location_coordinates);
                data.putString("imageRatio", msgObject.imageRatio);
                data.putString("FileId", msgObject.content_url);
                break;

            case Constants.CHAT_TYPE_SHARING:

                if (!Utils.isEmptyString(msgObject.imageRatio)) {
                    data.putString("imageRatio", msgObject.imageRatio);
                    data.putString("FileId", msgObject.content_url);
                } else if (!Utils.isEmptyString(msgObject.video_thumb)) {
                    data.putString("videoThumbnail", msgObject.video_thumb);
                    data.putString("FileId", msgObject.content_url);
                } else if (Utils.isEmptyString(msgObject.imageRatio) && Utils.isEmptyString(msgObject.video_thumb)) {
                    data.putString("FileId", msgObject.content_url);
                } else if (!Utils.isEmptyString(msgObject.card_originator)) {

                    if (!msgObject.is_CustomCard) {
                        data.putString("card_DB_ID", msgObject.card_DB_ID);
                        data.putString("card_content", msgObject.card_content);
                        data.putString("card_url", msgObject.card_url);
                    }
                    data.putString("card_owner", msgObject.card_owner);
                    data.putBoolean("is_CustomCard", msgObject.is_CustomCard);
                    data.putString("accepted_Rejected", msgObject.card_Accepted_Rejected);
                    data.putString("card_heading", msgObject.card_heading);
                    data.putString("card_id", msgObject.card_id);
                    data.putString("card_Played_Countered", msgObject.card_Played_Countered);
                    data.putString("card_originator", msgObject.card_originator);
                }
                data.putString("sharingMedia", msgObject.sharingMedia);
                data.putString("originalChatId", msgObject.originalMessageID);
                data.putString("caption", msgObject.shareComment);
                data.putString("isMessageSender", msgObject.isMessageSender);
                data.putString("shareStatus", msgObject.shareStatus);
                data.putString("messageSenderID",msgObject.messageSenderId);
                data.putString("facebookToken", msgObject.facebookToken);
                if (Utils.isEmptyString(msgObject.isAccepted)) {
                    data.putString("isAccepted", "null");
                } else {
                    data.putString("isAccepted", msgObject.isAccepted);
                }


                break;
        }

        msg.setData(data);
        msg.what = ChatThread.SEND_CHAT;
        handler.sendMessage(msg);
    }


    //function to send typing notification chat to other
    public void sendTypeNotification(String typeFlag, String partnerQbId) {
        Handler handler = mChatThread.getHandler();
        android.os.Message msg = new android.os.Message();
        Bundle data = new Bundle();
        data.putString("isComposing", typeFlag);
        data.putInt("ChatType", Constants.CHAT_TYPE_NOFITICATION);
        data.putString("partnerQBId", partnerQbId);
        msg.setData(data);
        msg.what = ChatThread.SEND_CHAT;
        handler.sendMessage(msg);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        //QBChatService.getInstance().destroy();
        EventBus.getDefault().unregister(this);
        logoutFromQb();
        ClickinDbHelper dbHelper = new ClickinDbHelper(this);

        try {
            dbHelper.clearDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        db.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification(String title, String message, String match_id, boolean on_team) {
        // In this sample, we'll use the same text for the ticker and the expanded notification

        // Set the icon, scrolling text and timestamp
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message);
//        Notification notification = new Notification(R.drawable.ic_launcher, message, System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
     /*   Intent intent = new Intent(this, MatchOrganizer.class);
        intent.putExtra("match_id", match_id);
        intent.putExtra("OnChat", true);
        intent.putExtra("on_team", on_team);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);*/
        // Set the info for the views that show in the notification panel.
//        notification.setLatestEventInfo(this, getText(R.string.local_service_label),
//                text, contentIntent);

        // Send the notification.
        mNM.notify(mId++, mBuilder.build());
    }

    public void setChatListeners() {
        Handler handler = mChatThread.getHandler();
        android.os.Message msg = new android.os.Message();
        Bundle data = new Bundle();

        msg.setData(data);
        msg.what = ChatThread.ADD_CHAT_LISTENERS;
        handler.sendMessage(msg);
    }

    //function to send online status of partner
    public void CheckOnlineStatus(int partnerQBId) {
        Handler handler = mChatThread.getHandler();
        android.os.Message msg = new android.os.Message();
        Bundle data = new Bundle();
        data.putInt("partnerQBId", partnerQBId);
        msg.setData(data);
        msg.what = ChatThread.CHECK_PRESENCE;
        handler.sendMessage(msg);
    }


    public void logoutFromQb() {
        Handler handler = mChatThread.getHandler();
        android.os.Message msg = new android.os.Message();
        Bundle data = new Bundle();

        msg.setData(data);
        msg.what = ChatThread.CHAT_LOGOUT;
        handler.sendMessage(msg);
    }

    public void onEventMainThread(String getMsg) {
        /*switch (event.getType()) {
            case FgEvent.EVENT_GETMESSAGE_LIST:
                if (event.getStatus()) {
                    JSONObject object = (JSONObject) event.getValue();
                    try {
                        boolean status;
                        status = object.getBoolean("ok");
                        if (status) {
                            JSONArray list = object.getJSONArray("msg");
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject data = list.getJSONObject(i);
                                android.os.Message msg = new android.os.Message();
                                Bundle bundle = new Bundle();
                                bundle.putString("room", data.getString("match_id"));
                                msg.setData(bundle);
                                msg.what = ChatThread.CREATE_ROOM;
                                mChatThread.getHandler().sendMessage(msg);
                                msg = new android.os.Message();
                                bundle = new Bundle();
                                bundle.putString("room", data.getString("match_id") + data.getString("room_team"));
                                msg.setData(bundle);
                                msg.what = ChatThread.CREATE_ROOM;
                                mChatThread.getHandler().sendMessage(msg);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }*/
    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        public MyQbChatService getService() {
            return MyQbChatService.this;
        }
    }
}































     /*   import android.app.NotificationManager;
        import android.app.Service;
        import android.content.Intent;
        import android.os.Handler;
        import android.os.IBinder;
        import android.widget.Toast;

        import android.app.ActivityManager;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.app.Service;
        import android.content.ContentValues;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Binder;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.IBinder;
        import android.support.v4.app.NotificationCompat;


        import com.quickblox.module.chat.QBChatService;
        import com.sourcefuse.clickinandroid.view.UserProfileView;
        import com.sourcefuse.clickinapp.R;

        import org.jivesoftware.smack.packet.Message;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.UnsupportedEncodingException;
        import java.security.MessageDigest;
        import java.security.NoSuchAlgorithmException;
        import java.util.List;
        import java.util.regex.Matcher;
        import java.util.regex.Pattern;





public class QbChatService extends Service {

    private static final String TAG = QbChatService.class.getSimpleName();
    private NotificationManager systemService;


    private final IBinder mBinder = new LocalBinder();
    private NotificationManager mNM;

    private int mId = 0;
    private ChatThread mChatThread;


    @Override
    public void onCreate() {

        systemService = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNM =systemService;
        Handler handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 0) {
                    Message message = (Message) msg.obj;
                    if(message.getBody().equals("delivered")) {
                        return;
                    }
                    if (message.getBody() != null && !isAppOnForeground(QbChatService.this)) {
                        try {
                            JSONObject jsonObject = new JSONObject(message.getBody());
                            *//*if(jsonObject.has("venue")) {
                                showNotification(jsonObject.getString("user_name") + " has proposed a new venue", jsonObject.getString("venue_name"));
                            } else {
                                showNotification("New message from " + jsonObject.getString("user_name"), jsonObject.getString("msg"));
                            }*//*
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //EventBus.getDefault().post(new FgEvent(FgEvent.CHAT_MESSAGE, true));
                }
            }
        };
        mChatThread = new ChatThread(this, handler);
        mChatThread.start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {

    }


    private boolean isAppOnForeground(Context context) {
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


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public QbChatService getService() {
            return QbChatService.this;
        }
    }

    private void showNotification(String title, String message) {
        // In this sample, we'll use the same text for the ticker and the expanded notification

        // Set the icon, scrolling text and timestamp
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message);
//        Notification notification = new Notification(R.drawable.ic_launcher, message, System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, UserProfileView.class), 0);

        mBuilder.setContentIntent(contentIntent);
        // Set the info for the views that show in the notification panel.
//        notification.setLatestEventInfo(this, getText(R.string.local_service_label),
//                text, contentIntent);

        // Send the notification.
        mNM.notify(mId++, mBuilder.build());
    }

    // Call this Service from any activity to start
   *//* // Start the service
    public void startService(View view) {
        startService(new Intent(this, MyService.class));
    }

    // Stop the service
    public void stopService(View view) {
        stopService(new Intent(this, MyService.class));
    }
    *//*

}
*/