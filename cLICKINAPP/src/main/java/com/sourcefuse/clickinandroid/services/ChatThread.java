package com.sourcefuse.clickinandroid.services;

/**
 * Created by mukesh on 15/11/14.
 */





        import android.app.ActivityManager;
        import android.app.Application;
        import android.content.ContentValues;
        import android.content.Context;
        import android.content.SharedPreferences;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Bundle;
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
        import org.jivesoftware.smack.packet.DefaultPacketExtension;
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

public class ChatThread extends Thread implements ChatMessageListener {

    private static final String TAG = ChatThread.class.getSimpleName();

    public static final int SEND_CHAT = 0;
    public static final int CREATE_ROOM = 1;
    private  Context mContext;
    private final Handler mHandler;
    private QBPrivateChat chat;
    private Handler mMyHandler;
    private JSONObject mRooms = new JSONObject();
    private QBPrivateChat chatObject;
    private HashMap<String, QBChatRoom> mChatRooms = new HashMap<String, QBChatRoom>();
     Application application;
    Pattern p = Pattern.compile("[\\d]+_(.*?)@.*?");

    private AuthManager authManager;

    public ChatThread(Application context, Handler handler) {
        application = context;
        mHandler = handler;
        chatObject = QBChatService.getInstance().createChat();
        chatObject.addChatMessageListener(this);


    }

    public Handler getHandler() {
        return mMyHandler;
    }

    @Override
    public void run() {
        Looper.prepare();
        EventBus.getDefault().register(this);
//loginToChat();
        mMyHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {

                    case SEND_CHAT:
                        Bundle data=msg.getData();
                        if(QBChatService.getInstance().isLoggedIn()) {
                              DefaultPacketExtension extension = null;
                              Message message =  null;
                            switch (data.getInt("ChatType")){

                                case Constants.CHAT_TYPE_TEXT:
                                    extension = new DefaultPacketExtension("extraParams", "jabber:client");
                                    message = new Message();
                                    message.setType(Message.Type.chat); // 1-1 chat message

                                    if(data.getString("clicks").equalsIgnoreCase("no")){
                                        extension.setValue("clicks","no");
                                        message.setBody("" + data.getString("textMsg"));
                                    }else if(data.getString("clicks").equalsIgnoreCase(data.getString("textMsg"))){
                                        extension.setValue("clicks",data.getString("clicks")+"        ");
                                        message.setBody(data.getString("textMsg"));
                                    }else{
                                        extension.setValue("clicks",data.getString("clicks"));
                                        message.setBody(data.getString("clicks") + "        " +  data.getString("textMsg"));
                                    }
                                    message.addExtension(extension);

                                    try {
                                        chatObject.sendMessage(Integer.parseInt(data.getString("partnerQBId")), message);
                                    } catch (XMPPException e) {
                                        e.printStackTrace();
                                        //loginToChat();
                                    }

                                    break;
                                case Constants.CHAT_TYPE_IMAGE:
                                    extension = new DefaultPacketExtension("extraParams", "jabber:client");
                                    message = new Message();
                                    extension.setValue("Clicks",data.getString("clicks"));
                                    message.setType(Message.Type.chat); // 1-1 chat message
                                    message.setBody("" + data.getString("textMsg"));
                                    message.addExtension(extension);

                                    try {
                                        chatObject.sendMessage(Integer.parseInt(data.getString("partnerQBId")), message);
                                    } catch (XMPPException e) {
                                        e.printStackTrace();
                                        //loginToChat();
                                    }

                                    break;



                            }
                        }else{
                           // loginToChat();
                        }
                        break;

                }
            }
        };

        //loginToChat();
        Looper.loop();

        EventBus.getDefault().unregister(this);
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
