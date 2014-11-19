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
import com.quickblox.module.chat.QBChat;

import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.QBPrivateChat;
import com.quickblox.module.chat.exception.QBChatException;

import com.quickblox.module.chat.listeners.QBMessageListener;

import com.quickblox.module.chat.model.QBChatMessage;

import com.quickblox.module.users.model.QBUser;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.utils.Constants;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
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

public class ChatThread extends Thread  {

    private static final String TAG = ChatThread.class.getSimpleName();

    public static final int SEND_CHAT = 0;
    public static final int CREATE_ROOM = 1;
    private  Context mContext;
    private final Handler mHandler;
    private QBChatService chat;
    private Handler mMyHandler;
    private JSONObject mRooms = new JSONObject();
    private QBPrivateChat chatObject;
    Application application;
    Pattern p = Pattern.compile("[\\d]+_(.*?)@.*?");

    private AuthManager authManager;

    public ChatThread(Application context, Handler handler) {
        application = context;
        mHandler = handler;
        authManager = ModelManager.getInstance().getAuthorizationManager();
        com.sourcefuse.clickinandroid.utils.Log.e(TAG, "loginToQuickBlox --- getUserId=>" + authManager.getUserId() + ",--getUsrToken-=>" + authManager.getUsrToken());
        QBSettings.getInstance().fastConfigInit(Constants.CLICKIN_APP_ID, Constants.CLICKIN_AUTH_KEY, Constants.CLICKIN_AUTH_SECRET);


   //     QBChatService.setDebugEnabled(true);
        if (!QBChatService.getInstance().isInitialized()) {
            QBChatService.init(application.getApplicationContext());
        }

//        chatService = QBChatService.getInstance();

        QBUser mUser = new QBUser();
        mUser.setLogin(authManager.getUserId());
        mUser.setPassword(authManager.getUsrToken());




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
                            int partnerQBId=Integer.parseInt(data.getString("partnerQBId"));
                            chatObject = QBChatService.getInstance().getPrivateChatManager().createChat(partnerQBId,privateMsgListener);
                            //DefaultPacketExtension extension = null;
                            QBChatMessage message =  null;
                            switch (data.getInt("ChatType")){

                                case Constants.CHAT_TYPE_TEXT:
                                 //   QBChatMessage message = new QBChatMessage();
                                  //  message.ID = uniqueString;
                                  //  extension = new DefaultPacketExtension("extraParams", "jabber:client");
                                    message = new QBChatMessage();
                                    message.setProperty("clicks",data.getString("clicks"));


                                    message.setBody(data.getString("textMsg"));


                                    break;
                                case Constants.CHAT_TYPE_IMAGE:
                                  //  extension = new DefaultPacketExtension("extraParams", "jabber:client");
                                    message = new QBChatMessage();
                                    message.setProperty("clicks",data.getString("clicks"));
                                    message.setBody("" + data.getString("textMsg"));

                                    try {
                                        // chatObject.sendMessage();
                                        try {
                                            chatObject.sendMessage(message);
                                        } catch (SmackException.NotConnectedException e) {
                                            e.printStackTrace();
                                        }
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







    public void onEventMainThread(String getMsg) {
        if (getMsg.equalsIgnoreCase("Logined in chat")){

        }
    }


    QBMessageListener privateMsgListener=new QBMessageListener(){

        @Override
        public void processMessage(QBChat qbChat, QBChatMessage qbChatMessage) {

        }

        @Override
        public void processError(QBChat qbChat, QBChatException e, QBChatMessage qbChatMessage) {

        }

        @Override
        public void processMessageDelivered(QBChat qbChat, String s) {

        }

        @Override
        public void processMessageRead(QBChat qbChat, String s) {

        }
    };
}
