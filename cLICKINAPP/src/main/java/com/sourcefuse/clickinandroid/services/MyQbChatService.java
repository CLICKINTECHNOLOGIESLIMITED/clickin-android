package com.sourcefuse.clickinandroid.services;

/**
 * Created by mukesh on 15/11/14.
 */

import android.os.Binder;

        import android.app.ActivityManager;
        import android.app.NotificationManager;
        import android.app.Service;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.IBinder;
        import android.support.v4.app.NotificationCompat;


import com.sourcefuse.clickinandroid.model.bean.ChatMessageBody;
import com.sourcefuse.clickinapp.R;

import org.jivesoftware.smack.packet.Message;
import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.List;
        import java.util.regex.Matcher;
        import java.util.regex.Pattern;

        import de.greenrobot.event.EventBus;

/**
 * Created by amit on 03/10/14.
 */
public class MyQbChatService extends Service {

    private final IBinder mBinder = new LocalBinder();
    Pattern p = Pattern.compile("[\\d]+_(.*?)@.*?");
    private NotificationManager mNM;
    private int mId = 0;
    private ChatThread mChatThread;

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        EventBus.getDefault().register(this);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 0) {
                    final Message message = (Message) msg.obj;
                    if (message.getBody().equals("delivered")) {
                        return;
                    }
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            if (message.getBody() != null && !isAppOnForeground(MyQbChatService.this)) {
                                try {
                                    JSONObject jsonObject = new JSONObject(message.getBody());
                                    boolean onteam = message.getFrom().contains("team1") || message.getFrom().contains("team2");
                                    if (jsonObject.has("venue")) {
                                        showNotification(jsonObject.getString("user_name") + " has proposed a new venue", jsonObject.getString("venue_name"), jsonObject.getString("match_id"), onteam);
                                    } else {
                                        showNotification("New message from " + jsonObject.getString("user_name"), jsonObject.getString("msg"), jsonObject.getString("match_id"), onteam);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            SharedPreferences sp = getSharedPreferences("chats", MODE_PRIVATE);
                            SharedPreferences.Editor e = sp.edit();
                            Matcher m = p.matcher(message.getFrom());
                            m.find();
                            e.putInt(m.group(1).replaceAll("-", ""), sp.getInt(message.getFrom(), 0) + 1);
                            e.putInt("chat_count", sp.getInt("chat_count", 0) + 1);
                            e.apply();
                          //  EventBus.getDefault().post(new FgEvent(FgEvent.CHAT_MESSAGE, true, message));
                            return null;
                        }
                    }.execute();
                } else if (msg.what == 1) {
                   // Session.getInstance(getApplicationContext()).getMessageList();
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
        data.putString("textMsg", msgObject.textMsg);
        data.putString("clicks", msgObject.clicks);
        data.putInt("ChatType",msgObject.chatType);
        msg.setData(data);
        msg.what = ChatThread.SEND_CHAT;
        handler.sendMessage(msg);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //QBChatService.getInstance().destroy();
        EventBus.getDefault().unregister(this);
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
        import android.util.Log;


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

        Log.e(TAG, "CS started");
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