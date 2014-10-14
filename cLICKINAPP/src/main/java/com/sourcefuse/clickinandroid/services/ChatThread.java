package com.sourcefuse.clickinandroid.services;

/**
 * Created by mukesh on 13/10/14.
 */





        import android.app.ActivityManager;
        import android.content.ContentValues;
        import android.content.Context;
        import android.content.SharedPreferences;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Handler;
        import android.os.Looper;
        import android.util.Log;


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
        import com.quickblox.module.chat.smack.SmackAndroid;
        import com.quickblox.module.chat.xmpp.QBPrivateChat;
        import com.quickblox.module.users.model.QBUser;
        import com.sourcefuse.clickinandroid.model.AuthManager;
        import com.sourcefuse.clickinandroid.model.ModelManager;
        import com.sourcefuse.clickinandroid.utils.Constants;

        import org.jivesoftware.smack.ConnectionListener;
        import org.jivesoftware.smack.XMPPException;
        import org.jivesoftware.smack.packet.Message;
        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.UnsupportedEncodingException;
        import java.security.MessageDigest;
        import java.security.NoSuchAlgorithmException;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.regex.Matcher;
        import java.util.regex.Pattern;

        import de.greenrobot.event.EventBus;

/**
 * Created by amit on 10/10/14.
 */
public class ChatThread extends Thread implements ChatMessageListener {

    private static final String TAG = ChatThread.class.getSimpleName();

    public static final int SEND_CHAT = 0;
    public static final int CREATE_ROOM = 1;
    private  Context mContext;
    private final Handler mHandler;
    private QBPrivateChat chat;
    private Handler mMyHandler;
    private JSONObject mRooms = new JSONObject();
    private HashMap<String, QBChatRoom> mChatRooms = new HashMap<String, QBChatRoom>();

    Pattern p = Pattern.compile("[\\d]+_(.*?)@.*?");

    private AuthManager authManager;

    public ChatThread(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    public Handler getHandler() {
        return mMyHandler;
    }

    @Override
    public void run() {
        Looper.prepare();
        EventBus.getDefault().register(this);
        SmackAndroid.init(mContext);
        QBSettings.getInstance().fastConfigInit(Constants.CLICKIN_APP_ID, Constants.CLICKIN_AUTH_KEY, Constants.CLICKIN_AUTH_SECRET);

        mMyHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case SEND_CHAT:
                        if(QBChatService.getInstance().isLoggedIn()) {

                        }else{
                            loginToChat();
                        }
                        break;

                }
            }
        };

        //loginToChat();
        Looper.loop();

        EventBus.getDefault().unregister(this);
    }

    private void loginToChat() {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        final QBUser mQBUser = new QBUser(authManager.getUserId(), authManager.getUsrToken());

        QBAuth.createSession(mQBUser, new QBCallbackImpl() {
            @Override
            public void onComplete(Result result) {
                Log.e("amit", result.getRawBody());
                if (result.isSuccess()) {
                    QBSessionResult res = (QBSessionResult) result;
                    QBChatService.getInstance().loginWithUser(mQBUser, new SessionCallback() {
                        @Override
                        public void onLoginSuccess() {
                            Log.e(TAG, "Login was successfully created");
                            QBChatService.getInstance().startAutoSendPresence(5);

                            ConnectionListener connectionListener = new ConnectionListener() {
                                @Override
                                public void connectionClosed() {
                                    //connection closed
                                    Log.e(TAG, "connection closed");
                                }

                                @Override
                                public void connectionClosedOnError(Exception e) {
                                    // connection closed on error. It will be established soon
                                    Log.e(TAG, "connection closed error");
                                }

                                @Override
                                public void reconnectingIn(int seconds) {
                                    Log.e(TAG, "connection closed error");
                                }

                                @Override
                                public void reconnectionSuccessful() {
                                    Log.e(TAG,  "reconnectionSuccessful");
                                }

                                @Override
                                public void reconnectionFailed(Exception e) {
                                    Log.e(TAG,  "reconnectionFailed");
                                }
                            };

                            QBChatService.getInstance().addConnectionListener(connectionListener);

//                            EventBus.getDefault().post(new FgEvent(FgEvent.CHAT_LOGGED_IN, true));
//                            Session.getInstance(ChatService.this).getMessageList();
                           // Session.getInstance(mContext).getMessageList();

                            chat = QBChatService.getInstance().createChat();
                            //chat.addChatMessageListener(this);
                            authManager.setqBPrivateChat(chat);

                        }
                        @Override
                        public void onLoginError(String s) {
                            Log.e(TAG, "onLoginError was successfully created");
                        }
                    });
                    Log.e(TAG, "Session was successfully created");
                }
            }
        });
    }


//    private void sendOutMessages(QBChatRoom qbChatRoom) {
//        List<String> messages = mChatRooms.get(qbChatRoom.getName());
//        for(int i = messages.size() - 1; i >=0; i--) {
//            String message = messages.get(i);
//            try {
//                qbChatRoom.sendMessage(message);
//                messages.remove(i);
////                mChatRooms.put(qbChatRoom.getName(), messages);
//            } catch (XMPPException e) {
//                e.printStackTrace();
//            }
//        }
//    }



    public void sendMessage(String roomName, String message) {

    }


    @Override
    public void processMessage(Message message) {

       // Log.e(TAG,""+message.getBody().getBytes("UTF-8"));
        Log.e(TAG,""+message.getBody().toString());


       /* ContentValues values = new ContentValues();
        Matcher m = p.matcher(message.getFrom());
        if(m.find()) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                String hash = new String(md.digest(message.getBody().getBytes("UTF-8")));
                Cursor result = db.query("messages", new String[]{"*"}, "id = ? and hash = ?", new String[]{String.valueOf(m.group(1)), hash}, null, null, null);
                if(result.getCount() == 0) {
                    values.put("id", m.group(1).replaceAll("team1", "").replaceAll("team2", "").replaceAll("-", ""));
                    values.put("hash", hash);
                    values.put("room", m.group(1));
                    JSONObject jsonObject = new JSONObject(message.getBody());
                    values.put("time", jsonObject.getString("time"));
                    db.insert("messages", null, values);
                }
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        android.os.Message msg = new android.os.Message();
        msg.what = 0;
        msg.obj = message;
        mHandler.sendMessage(msg);*//* ContentValues values = new ContentValues();
        Matcher m = p.matcher(message.getFrom());
        if(m.find()) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                String hash = new String(md.digest(message.getBody().getBytes("UTF-8")));
                Cursor result = db.query("messages", new String[]{"*"}, "id = ? and hash = ?", new String[]{String.valueOf(m.group(1)), hash}, null, null, null);
                if(result.getCount() == 0) {
                    values.put("id", m.group(1).replaceAll("team1", "").replaceAll("team2", "").replaceAll("-", ""));
                    values.put("hash", hash);
                    values.put("room", m.group(1));
                    JSONObject jsonObject = new JSONObject(message.getBody());
                    values.put("time", jsonObject.getString("time"));
                    db.insert("messages", null, values);
                }
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        android.os.Message msg = new android.os.Message();
        msg.what = 0;
        msg.obj = message;
        mHandler.sendMessage(msg);*/
    }

    @Override
    public boolean accept(Message.Type type) {
        switch (type) {
            case chat:
                return true; // process room chat messages
            default:
                return false;
        }
    }


    public void onEventMainThread(String getMsg) {
        if (getMsg.equalsIgnoreCase("Logined in chat")){

        }
    }
}
