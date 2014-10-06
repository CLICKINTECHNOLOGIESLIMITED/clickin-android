/*
package com.sourcefuse.clickinandroid.view;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.result.QBSessionResult;
import com.quickblox.module.chat.QBChatRoom;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.listeners.ChatMessageListener;
import com.quickblox.module.chat.listeners.RoomListener;
import com.quickblox.module.users.model.QBUser;
import com.sourcefuse.clickinapp.R;

import org.jivesoftware.smack.packet.Message;

import java.util.List;

import de.greenrobot.event.EventBus;

*/
/**
 * Created by mukesh on 3/10/14.
 *//*






        import android.app.ActivityManager;
        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.app.Service;
        import android.content.Context;
        import android.content.Intent;
        import android.os.Binder;
        import android.os.IBinder;
        import android.support.v4.app.NotificationCompat;
        import android.widget.Toast;
        import android.util.Log;

        import com.example.footgloryflow.com.example.footgloryflow.common.GlobalsFG;
        import com.example.footgloryflow.events.FgEvent;
        import com.example.footgloryflow.session.Session;
        import com.quickblox.core.QBCallbackImpl;
        import com.quickblox.core.QBSettings;
        import com.quickblox.core.result.Result;
        import com.quickblox.module.auth.QBAuth;
        import com.quickblox.module.auth.result.QBSessionResult;
        import com.quickblox.module.chat.QBChatRoom;
        import com.quickblox.module.chat.QBChatService;
        import com.quickblox.module.chat.listeners.ChatMessageListener;
        import com.quickblox.module.chat.listeners.RoomListener;
        import com.quickblox.module.chat.listeners.SessionCallback;
        import com.quickblox.module.users.model.QBUser;

        import org.jivesoftware.smack.packet.Message;

        import java.util.List;

        import de.greenrobot.event.EventBus;

*/
/**
 * Created by amit on 03/10/14.
 *//*

public class amitsir1 extends Service implements RoomListener, ChatMessageListener {

    private static final String APP_ID = "13168";
    private static final String AUTH_KEY = "j4wYjku39SArj7p";
    private static final String AUTH_SECRET = "KqpK7BcWzTTqjTj";

    private NotificationManager mNM;
    GlobalsFG globalsFG;
    private boolean mLoggedIn = false;
    private int mId = 0;

    @Override
    public void onCreatedRoom(QBChatRoom qbChatRoom) {
        qbChatRoom.addMessageListener(this);
        EventBus.getDefault().post(new FgEvent(FgEvent.CHAT_ROOM_CREATED, true, qbChatRoom));
    }

    @Override
    public void onJoinedRoom(QBChatRoom qbChatRoom) {
        qbChatRoom.addMessageListener(this);
        EventBus.getDefault().post(new FgEvent(FgEvent.CHAT_ROOM_JOINED, true, qbChatRoom));
    }

    @Override
    public void onError(String s) {

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
    public void processMessage(Message message) {
        if(message.getBody() != null && !isAppOnForeground(this)) {
            String s[] = message.getBody().split("”");
            showNotification("New message from " + s[2], s[1]);
        }
        globalsFG.chatCount++;
        EventBus.getDefault().post(new FgEvent(FgEvent.CHAT_MESSAGE, true, message));
    }

    @Override
    public boolean accept(Message.Type type) {
        switch (type) {
            case normal:
            case chat:
            case groupchat:
                return true; // process room chat messages
            default:
                return false;
        }
    }

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
//    private int NOTIFICATION = R.string.local_service_started;

    */
/**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     *//*

    public class LocalBinder extends Binder {
        public ChatService getService() {
            return ChatService.this;
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        globalsFG = GlobalsFG.getInstance();
    }



    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void createRoom(String roomName) {
        QBChatService.getInstance().createRoom(roomName, false, false, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);

        final QBUser user = new QBUser(Session.getInstance(this).getFb_id(), Session.getInstance(this).getchat_pwd());

        QBAuth.createSession(user, new QBCallbackImpl() {
            @Override
            public void onComplete(Result result) {
                if (result.isSuccess()) {
                    QBSessionResult res = (QBSessionResult) result;
                    user.setId(res.getSession().getUserId());
                    QBChatService.getInstance().loginWithUser(user, new SessionCallback() {
                        @Override
                        public void onLoginSuccess() {
//                            Log.e(TAG, "Login was successfully created");
                            QBChatService.getInstance().startAutoSendPresence(600000000);
                            EventBus.getDefault().post(new FgEvent(FgEvent.CHAT_LOGGED_IN, true));
                            mLoggedIn = true;
                        }

                        @Override
                        public void onLoginError(String s) {
//                            ((FootGloryBaseView) getActivity()).dismissLoading();
//                            Log.e(TAG, "onLoginError was successfully created");
                        }
                    });
//                    Log.e(TAG, "Session was successfully created");
                }
            }
        });

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        QBChatService.getInstance().logout();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if(mLoggedIn) {
            EventBus.getDefault().post(new FgEvent(FgEvent.CHAT_LOGGED_IN, true));
        }
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    */
/**
     * Show a notification while this service is running.
     *//*

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
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        mBuilder.setContentIntent(contentIntent);
        // Set the info for the views that show in the notification panel.
//        notification.setLatestEventInfo(this, getText(R.string.local_service_label),
//                text, contentIntent);

        // Send the notification.
        mNM.notify(mId++, mBuilder.build());
    }
}*/
