package com.sourcefuse.clickinandroid.services;

/**
 * Created by mukesh on 15/11/14.
 */


import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.quickblox.core.QBSettings;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.model.QBSession;
import com.quickblox.module.chat.QBChat;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.QBPrivateChat;
import com.quickblox.module.chat.exception.QBChatException;
import com.quickblox.module.chat.listeners.QBMessageListener;
import com.quickblox.module.chat.model.QBChatMessage;
import com.quickblox.module.users.model.QBUser;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.bean.ChatMessageBody;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

public class ChatThread extends Thread implements QBMessageListener {

    public static final int SEND_CHAT = 0;
    public static final int CREATE_ROOM = 1;
    public static final int SEND_TYPE_NOTIFICATION = 2;
    private static final String TAG = ChatThread.class.getSimpleName();
    private final Handler mHandler;
    Application application;
    Pattern p = Pattern.compile("[\\d]+_(.*?)@.*?");
    private Context mContext;
    private QBChatService chat;
    private Handler mMyHandler;
    private JSONObject mRooms = new JSONObject();
    private QBPrivateChat chatObject;
    private AuthManager authManager;
    private QBUser mUser;

    public ChatThread(Application context, Handler handler) {
        application = context;
        mHandler = handler;
        authManager = ModelManager.getInstance().getAuthorizationManager();

        QBSettings.getInstance().fastConfigInit(Constants.CLICKIN_APP_ID, Constants.CLICKIN_AUTH_KEY, Constants.CLICKIN_AUTH_SECRET);
       // QBSettings.getInstance().setServerApiDomain("apiclickin.quickblox.com");
       // QBSettings.getInstance().setContentBucketName("qb-clickin");
        //QBSettings.getInstance().setChatServerDomain("chatclickin.quickblox.com");
        QBChatService.setDebugEnabled(true);
    }

    public Handler getHandler() {
        return mMyHandler;
    }

    @Override
    public void run() {
        Looper.prepare();
        loginToChat();
        EventBus.getDefault().register(this);
        mMyHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {

                    case SEND_CHAT:
                        Bundle data = msg.getData();
                        if (QBChatService.getInstance().isLoggedIn()) {
                            int partnerQBId = Integer.parseInt(data.getString("partnerQBId"));
                            chatObject = QBChatService.getInstance().getPrivateChatManager().getChat(partnerQBId);
                            if (chatObject == null)
                                chatObject = QBChatService.getInstance().getPrivateChatManager().createChat(partnerQBId, ChatThread.this);
                            QBChatMessage message = new QBChatMessage();
                            switch (data.getInt("ChatType")) {

                                case Constants.CHAT_TYPE_NOFITICATION:
                                    message.setProperty("isComposing", data.getString("isComposing"));
                                    break;

                                case Constants.CHAT_TYPE_CARD:
                                    if (!data.getBoolean("is_CustomCard")) {
                                        message.setProperty("card_DB_ID", data.getString("card_DB_ID"));
                                        message.setProperty("card_url", data.getString("card_url"));
                                        message.setProperty("card_content", data.getString("card_content"));
                                    }
                                    message.setProperty("is_CustomCard", String.valueOf(data.getBoolean("is_CustomCard")));
                                    message.setProperty("card_clicks", data.getString("clicks"));
                                    message.setProperty("card_owner", data.getString("card_owner"));
                                    message.setProperty("card_Accepted_Rejected", data.getString("accepted_Rejected"));
                                    message.setProperty("card_heading", data.getString("card_heading"));
                                    message.setProperty("card_id", data.getString("card_id"));
                                    message.setProperty("card_Played_Countered", data.getString("card_Played_Countered"));
                                    message.setProperty("card_originator", data.getString("card_originator"));

                                    break;
                                case Constants.CHAT_TYPE_IMAGE:

                                    message.setProperty("fileID", data.getString("FileId"));
                                    message.setProperty("imageRatio", data.getString("imageRatio"));

                                    break;
                                case Constants.CHAT_TYPE_AUDIO:

                                    message.setProperty("audioID", data.getString("FileId"));

                                    break;
                                case Constants.CHAT_TYPE_VIDEO:

                                    message.setProperty("videoThumbnail", data.getString("videoThumbnail"));
                                    message.setProperty("videoID", data.getString("FileId"));

                                    break;
                                case Constants.CHAT_TYPE_LOCATION:

                                    message.setProperty("location_coordinates", data.getString("location_coordinates"));
                                    message.setProperty("imageRatio", data.getString("imageRatio"));
                                    message.setProperty("locationID", data.getString("FileId"));

                                    break;
                            }
                            if (!(data.getInt("ChatType") == Constants.CHAT_TYPE_CARD)) {
                                if (data.containsKey("clicks"))
                                    message.setProperty("clicks", data.getString("clicks"));
                                    message.setBody("");

                            }

                            if (data.containsKey("textMsg"))
                                message.setBody(data.getString("textMsg"));

                            //set the delivered notification property in msg
                            if(!(data.getInt("ChatType")==Constants.CHAT_TYPE_NOFITICATION)) {
                                message.setMarkable(true);
                               // message.setProperty("сommon_platform_id",data.getString("ChaidId"));
                            }

                            try {
                                try {
                                    chatObject.sendMessage(message);
                                } catch (SmackException.NotConnectedException e) {
                                    e.printStackTrace();
                                }
                            } catch (XMPPException e) {
                                e.printStackTrace();
                                //loginToChat();
                            }
                        } else {
                            loginToChat();
                        }
                        break;
                    case CREATE_ROOM:
                        if (!QBChatService.getInstance().isLoggedIn())
                            loginToChat();
                        break;

                }
            }
        };

        //  loginToChat();
        Looper.loop();

        //
        EventBus.getDefault().unregister(this);
    }


    private void loginToChat() {
        try {

            QBChatService chatService;
            if (!QBChatService.isInitialized()) {
                QBChatService.init(application.getApplicationContext());
            }
            mUser = new QBUser();
            authManager = ModelManager.getInstance().getAuthorizationManager();
            mUser.setLogin(authManager.getUserId());
            Log.e("ChatThread Login", "authManager.getUsrToken()");
            mUser.setPassword(authManager.getUsrToken());
            QBSession result = QBAuth.createSession(new QBUser(authManager.getUserId(), authManager.getUsrToken()));
            mUser.setId(result.getUserId());
            QBChatService.getInstance().login(mUser);
            QBChatService.getInstance().startAutoSendPresence(5);
            // android.os.Message msg = new android.os.Message();
            //msg.what = 1;
            // mHandler.sendMessage(msg);
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }
    }


    public void onEventMainThread(String getMsg) {
        if (getMsg.equalsIgnoreCase("Logined in chat")) {

        }
    }


   /* QBMessageListener privateMsgListener=new QBMessageListener(){

        @Override
        public void processMessage(QBChat qbChat, QBChatMessage qbChatMessage) {
            Log.e(TAG,"processMessage--->");
            JSONObject jSONObj = null;

            Message message = qbChatMessage.getSmackMessage();
            try {
                jSONObj = XML.toJSONObject(message.toXML().toString());
                Log.e(TAG, "--- xmlJSONObj--->" + jSONObj);

                JSONObject messageObj = jSONObj.getJSONObject("message");
                JSONObject extraParamsObj = messageObj.getJSONObject("extraParams");

                if (extraParamsObj.has("isComposing")) { //means user is composing msg now
                    if(extraParamsObj.getString("isComposing").equalsIgnoreCase("YES"))
                        EventBus.getDefault().post("Composing YES");
                    else
                    EventBus.getDefault().post("Composing NO");

                }else if(messageObj.getString("body").equalsIgnoreCase("Delivered.")) {//here we sent the msg , its delivr notification only
                    EventBus.getDefault().post("Msg Delivered");
                }else {//here we recieved proper chat msg and need to update list

                    ChatMessageBody temp=new ChatMessageBody();
                    String from = messageObj.getString("from");
                    String[] words = from.split("-");
                    temp.senderQbId = words[0];
                    String body = messageObj.getString("body");

                    if (extraParamsObj.has("clicks")) {
                        temp.clicks = extraParamsObj.getString("clicks");
                    }
                    if (extraParamsObj.has("imageRatio")) {
                        temp.imageRatio = extraParamsObj.getString("imageRatio");
                    }
                    if (extraParamsObj.has("fileID")) {
                        temp.content_url = extraParamsObj.getString("fileID");
                    }
                    if(temp.clicks.equalsIgnoreCase("no")){
                       temp.textMsg = body;
                    }else{
                       // if(!temp.clicks.equalsIgnoreCase(body))
                        if(body.length()>3)
                        temp.textMsg=body.substring(2).trim();
                        else //only clicks
                         temp.textMsg="";
                    }
                    ModelManager.getInstance().getChatManager().chatMessageList.add(temp);

                    EventBus.getDefault().post("Chat Message Recieve");
                }
                }catch(Exception e){
                    e.printStackTrace();
                }

        }

        @Override
        public void processError(QBChat qbChat, QBChatException e, QBChatMessage qbChatMessage) {

        }

        @Override
        public void processMessageDelivered(QBChat qbChat, String s) {
            Log.e(TAG,"processMessageDelivered--->");
        }

        @Override
        public void processMessageRead(QBChat qbChat, String s) {

        }
    };*/

    @Override

    public void processMessage(QBChat qbChat, QBChatMessage qbChatMessage) {
        Log.e(TAG, "processMessage--->");
        JSONObject jSONObj = null;

        Message message = qbChatMessage.getSmackMessage();
        try {
            jSONObj = XML.toJSONObject(message.toXML().toString());
            Log.e(TAG, "--- xmlJSONObj--->" + jSONObj);

            JSONObject messageObj = jSONObj.getJSONObject("message");
            JSONObject extraParamsObj = messageObj.getJSONObject("extraParams");

            if (extraParamsObj.has("isComposing")) { //means user is composing msg now
                if (extraParamsObj.getString("isComposing").equalsIgnoreCase("YES"))
                    EventBus.getDefault().post("Composing YES");
                else
                    EventBus.getDefault().post("Composing NO");

            } else if (messageObj.getString("body").equalsIgnoreCase("Delivered.")) {//here we sent the msg , its delivr notification only
                EventBus.getDefault().post("Msg Delivered");
            } else {//here we recieved proper chat msg and need to update list

                ChatMessageBody temp = new ChatMessageBody();
                String from = messageObj.getString("from");
                String[] words = from.split("-");
                temp.senderQbId = words[0];
                String body = messageObj.getString("body");

                if (extraParamsObj.has("clicks")) {
                    temp.clicks = extraParamsObj.getString("clicks");
                }
                if (extraParamsObj.has("imageRatio")) {
                    temp.imageRatio = extraParamsObj.getString("imageRatio");
                }
                if (extraParamsObj.has("fileID")) {
                    temp.content_url = extraParamsObj.getString("fileID");
                }else if(extraParamsObj.has("audioID")){
                    temp.content_url = extraParamsObj.getString("audioID");
                }else if(extraParamsObj.has("card_owner")){

                    temp.clicks = extraParamsObj.getString("card_clicks");
                    temp.card_owner = Integer.toString(extraParamsObj.getInt("card_owner"));
                    temp.card_content = extraParamsObj.getString("card_content");
                    temp.is_CustomCard = extraParamsObj.getBoolean("is_CustomCard");
                    temp.card_DB_ID =extraParamsObj.getString("card_DB_ID");
                    temp.card_Accepted_Rejected = extraParamsObj.getString("card_Accepted_Rejected");
                    temp.card_heading = extraParamsObj.getString("card_heading");
                    temp.card_url =extraParamsObj.getString("card_url");
                    temp.card_id = extraParamsObj.getString("card_id");
                    temp.card_Played_Countered = extraParamsObj.getString("card_Played_Countered");
                    temp.card_originator = extraParamsObj.getString("card_originator");
                }
                if (temp.clicks.equalsIgnoreCase("no")) {
                    temp.textMsg = body;
                } else {
                    // if(!temp.clicks.equalsIgnoreCase(body))
                    if (body.length() > 3)
                        temp.textMsg = body.substring(3).trim();
                    else { //only clicks
                        temp.textMsg = "";
                        temp.clicks = Utils.convertClicks(temp.clicks).trim();
                    }
                }
                ModelManager.getInstance().getChatManager().chatMessageList.add(temp);

                EventBus.getDefault().post("Chat Message Recieve");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void processError(QBChat qbChat, QBChatException e, QBChatMessage qbChatMessage) {

    }

    @Override
    public void processMessageDelivered(QBChat qbChat, String msgId) {
        EventBus.getDefault().post("Delivered Msg"+ msgId);
    }

    @Override
    public void processMessageRead(QBChat qbChat, String s) {

    }
}

