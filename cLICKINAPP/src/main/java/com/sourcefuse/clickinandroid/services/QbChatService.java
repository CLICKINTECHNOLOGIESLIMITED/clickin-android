package com.sourcefuse.clickinandroid.services;

/**
 * Created by mukesh on 11/10/14.
 */

        import android.app.NotificationManager;
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
                            /*if(jsonObject.has("venue")) {
                                showNotification(jsonObject.getString("user_name") + " has proposed a new venue", jsonObject.getString("venue_name"));
                            } else {
                                showNotification("New message from " + jsonObject.getString("user_name"), jsonObject.getString("msg"));
                            }*/
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
   /* // Start the service
    public void startService(View view) {
        startService(new Intent(this, MyService.class));
    }

    // Stop the service
    public void stopService(View view) {
        stopService(new Intent(this, MyService.class));
    }
    */

}
