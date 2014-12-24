package com.sourcefuse.clickinandroid.services;

/**
 * Created by mukesh on 15/11/14.
 */


import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.model.QBSession;
import com.quickblox.module.chat.QBChat;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.QBPrivateChat;
import com.quickblox.module.chat.QBRoster;
import com.quickblox.module.chat.exception.QBChatException;
import com.quickblox.module.chat.listeners.QBMessageListener;
import com.quickblox.module.chat.listeners.QBRosterListener;
import com.quickblox.module.chat.listeners.QBSubscriptionListener;
import com.quickblox.module.chat.model.QBChatMessage;
import com.quickblox.module.chat.model.QBPresence;
import com.quickblox.module.users.model.QBUser;
import com.sourcefuse.clickinandroid.dbhelper.ClickinDbHelper;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.ChatMessageBody;
import com.sourcefuse.clickinandroid.model.bean.GetrelationshipsBean;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.SplashView;
import com.sourcefuse.clickinapp.R;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

public class ChatThread extends Thread implements QBMessageListener, ConnectionListener {

    public static final int SEND_CHAT = 0;
    public static final int ADD_CHAT_LISTENERS = 1;
    public static final int CHAT_LOGOUT = 2;

    private static final String TAG = ChatThread.class.getSimpleName();
    private final Handler mHandler;
    Application application;
    Pattern p = Pattern.compile("[\\d]+_(.*?)@.*?");
    //list to maintain chat message to add in db
    ArrayList<ChatMessageBody> messageInDb;
    private Context mContext;
    private QBChatService chat;
    private Handler mMyHandler;
    private JSONObject mRooms = new JSONObject();
    private QBPrivateChat chatObject;
    private AuthManager authManager;
    private QBUser mUser;
    private QBRoster chatRoster;

    public ChatThread(Application context, Handler handler) {
        application = context;
        mHandler = handler;
        authManager = ModelManager.getInstance().getAuthorizationManager();

        QBSettings.getInstance().fastConfigInit(Constants.CLICKIN_APP_ID, Constants.CLICKIN_AUTH_KEY, Constants.CLICKIN_AUTH_SECRET);
      //   QBSettings.getInstance().setServerApiDomain("apiclickin.quickblox.com");
        //QBSettings.getInstance().setContentBucketName("qb-clickin");
        //QBSettings.getInstance().setChatServerDomain("chatclickin.quickblox.com");
        QBChatService.setDebugEnabled(true);
        messageInDb = new ArrayList<ChatMessageBody>();
    }

    public Handler getHandler() {
        return mMyHandler;
    }

    @Override
    public void run() {
        Looper.prepare();
        loginToChat();
        EventBus.getDefault().register(this);
        registerListeners();
        mMyHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {

                    case SEND_CHAT:
                        Bundle data = msg.getData();
                        if (QBChatService.getInstance().isLoggedIn()) {
                            int partnerQBId = Integer.parseInt(data.getString("partnerQBId"));
                            QBPrivateChat chatObject = null;
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

                                case Constants.CHAT_TYPE_SHARING:

                                    message.setProperty("originalMessageID", data.getString("originalChatId"));
                                    message.setProperty("isMessageSender", data.getString("isMessageSender"));
                                    message.setProperty("shareStatus", data.getString("shareStatus"));
                                    message.setProperty("sharingMedia", data.getString("sharingMedia"));
                                    message.setProperty("isAccepted", data.getString("isAccepted"));
                                    message.setProperty("facebookToken", data.getString("facebookToken"));
                                    message.setProperty("comment", data.getString("caption"));

                                    if (data.containsKey("imageRatio")) {
                                        message.setProperty("imageURL", data.getString("FileId"));
                                        message.setProperty("imageRatio", data.getString("imageRatio"));
                                        //message.setProperty("isFileUploading", data.getString("imageRatio"));

                                    }
                                    if (data.containsKey("videoThumbnail")) {
                                        message.setProperty("videoThumbnail", data.getString("videoThumbnail"));
                                        message.setProperty("videoID", data.getString("FileId"));
                                    }
                                    if (!data.containsKey("imageRatio") && !data.containsKey("videoThumbnail") && !Utils.isEmptyString(data.getString("FileId"))) {
                                        message.setProperty("audioStreamURL", data.getString("FileId"));
                                        //  message.setProperty("isAudioUploading", data.getString("FileId"));

                                    }
                                    break;

                            }
                            if (!(data.getInt("ChatType") == Constants.CHAT_TYPE_CARD)) {
                                if (data.containsKey("clicks"))
                                    message.setProperty("clicks", data.getString("clicks"));
                                //  message.setBody("");

                            }

                            if (data.containsKey("textMsg"))
                                message.setBody(data.getString("textMsg"));

                            //set the delivered notification property in msg
                            if (!(data.getInt("ChatType") == Constants.CHAT_TYPE_NOFITICATION)) {
                                message.setMarkable(true);
                                message.setProperty("common_platform_id", data.getString("ChatId"));
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
                    case ADD_CHAT_LISTENERS:
                        if (QBChatService.getInstance().isLoggedIn())
                            registerListeners();
                        break;
                    case CHAT_LOGOUT:
                        logoutQB();

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

            //check for user token vale
            String userId=null, userToken=null;
            if(!Utils.isEmptyString(authManager.getUsrToken()) && !Utils.isEmptyString(authManager.getUserId())) {
                userId = authManager.getUserId();
                userToken=authManager.getUsrToken();
            }else{
                SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(application);
                userId=preferences.getString("userid",null);
                userToken=preferences.getString("authToken",null);
            }
            android.util.Log.e("ChatThread Login", "authManager.getUsrToken()");
            if(!Utils.isEmptyString(userId) && !Utils.isEmptyString(userToken)) {
                mUser.setLogin(userId);
                mUser.setPassword(userToken);
                QBSession result = QBAuth.createSession(new QBUser(userId, userToken));
                mUser.setId(result.getUserId());
                QBChatService.getInstance().login(mUser);
                QBChatService.getInstance().startAutoSendPresence(5);

                //monika-connection listener
                QBChatService.getInstance().addConnectionListener(this);

                chatRoster = QBChatService.getInstance().getRoster(QBRoster.SubscriptionMode.mutual, subscriptionListener);
                chatRoster.addRosterListener(rosterListener);



                

            }else{
                showDialog("Please Sign in again");
            }
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


    QBRosterListener rosterListener = new QBRosterListener() {
        @Override
        public void entriesDeleted(Collection<Integer> userIds) {

        }

        @Override
        public void entriesAdded(Collection<Integer> userIds) {

        }

        @Override
        public void entriesUpdated(Collection<Integer> userIds) {

        }

        @Override
        public void presenceChanged(QBPresence presence) {

        }
    };

    QBSubscriptionListener subscriptionListener = new QBSubscriptionListener() {
        @Override
        public void subscriptionRequested(int userId) {

        }
    };


    public  boolean getPresence(int userId){

        boolean isOnline = false;
        QBPresence presence = chatRoster.getPresence(userId);
        /*if (presence == null) {
            // No user in your roster
            return;
        }*/

        if (presence.getType() == QBPresence.Type.online) {
            // User is online
            isOnline = true;
        }else{
            // User is offline
            isOnline = false;
        }
        return isOnline;
    }




    @Override
    public void processMessage(QBChat qbChat, QBChatMessage qbChatMessage) {
        android.util.Log.e(TAG, "processMessage--->");
        JSONObject jSONObj = null;

        Message message = qbChatMessage.getSmackMessage();
        try {
            jSONObj = XML.toJSONObject(message.toXML().toString());
            android.util.Log.e(TAG, "--- xmlJSONObj--->" + jSONObj);

            JSONObject messageObj = jSONObj.getJSONObject("message");
            JSONObject extraParamsObj = messageObj.getJSONObject("extraParams");

            if (extraParamsObj.has("isComposing")) { //means user is composing msg now
                if (extraParamsObj.getString("isComposing").equalsIgnoreCase("YES"))
                    EventBus.getDefault().post("Composing YES");
                else
                    EventBus.getDefault().post("Composing NO");

            } else if (messageObj.has("body") && messageObj.getString("body").equalsIgnoreCase("Delivered.")) {//here we sent the msg , its delivr notification only
                EventBus.getDefault().post("Msg Delivered");
            } else {//here we recieved proper chat msg and need to update list

                ChatMessageBody temp = new ChatMessageBody();
                String from = messageObj.getString("from");
                String[] words = from.split("-");
                temp.senderQbId = words[0];
                String body = " ";
                if (messageObj.has("body"))
                    body = messageObj.getString("body");

                if (messageObj.has("common_platform_id"))
                    temp.chatId = messageObj.getString("common_platform_id");

                if (extraParamsObj.has("clicks")) {
                    temp.clicks = extraParamsObj.getString("clicks");
                }
                if (extraParamsObj.has("imageRatio")) {
                    temp.imageRatio = extraParamsObj.getString("imageRatio");
                }

                if (extraParamsObj.has("location_coordinates")) {
                    temp.location_coordinates = extraParamsObj.getString("location_coordinates");
                    temp.content_url = extraParamsObj.getString("locationID");
                }
                if (extraParamsObj.has("fileID")) {
                    temp.content_url = extraParamsObj.getString("fileID");
                } else if (extraParamsObj.has("audioID")) {
                    temp.content_url = extraParamsObj.getString("audioID");
                } else if (extraParamsObj.has("videoThumbnail")) {
                    temp.video_thumb = extraParamsObj.getString("videoThumbnail");
                    temp.content_url = extraParamsObj.getString("videoID");
                } else if (extraParamsObj.has("card_owner")) {

                    temp.clicks = extraParamsObj.getString("card_clicks");
                    temp.card_owner = Integer.toString(extraParamsObj.getInt("card_owner"));
                    temp.is_CustomCard = extraParamsObj.getBoolean("is_CustomCard");
                    if (!temp.is_CustomCard) {
                        temp.card_content = extraParamsObj.getString("card_content");
                        temp.card_DB_ID = extraParamsObj.getString("card_DB_ID");
                        temp.card_url = extraParamsObj.getString("card_url");
                    }
                    temp.card_Accepted_Rejected = extraParamsObj.getString("card_Accepted_Rejected");
                    temp.card_heading = extraParamsObj.getString("card_heading");
                    temp.card_id = extraParamsObj.getString("card_id");
                    temp.card_Played_Countered = extraParamsObj.getString("card_Played_Countered");
                     temp.card_originator = extraParamsObj.getString("card_originator");
                    if (temp.senderQbId.equalsIgnoreCase(authManager.partnerQbId)) {
                        if (temp.card_Accepted_Rejected.equalsIgnoreCase("accepted"))
                            updateValuesClicks(temp);
                    }
                } else if (extraParamsObj.has("sharingMedia")) {

                    temp.facebookToken = extraParamsObj.getString("facebookToken");
                    if (extraParamsObj.has("imageRatio"))
                        temp.originalMessageID = extraParamsObj.getString("imageRatio");
                    //temp.m = extraParamsObj.getString("messageSenderID");
                    temp.sharingMedia = extraParamsObj.getString("sharingMedia");
                    temp.isMessageSender = extraParamsObj.getString("isMessageSender");


                    temp.clicks = extraParamsObj.getString("clicks");
                    temp.originalMessageID = extraParamsObj.getString("originalMessageID");
                    //  temp.is = extraParamsObj.getString("isFileUploading");
                    if (extraParamsObj.has("imageURL"))
                        temp.content_url = extraParamsObj.getString("imageURL");
                    temp.shareStatus = extraParamsObj.getString("shareStatus");
                    temp.isAccepted = extraParamsObj.getString("isAccepted");



                }


                if (temp.clicks.equalsIgnoreCase("no")) {
                    temp.textMsg = body.trim();
                } else if ((!extraParamsObj.has("card_owner"))) {
                    // if(!temp.clicks.equalsIgnoreCase(body))
                    //update value of clicks-add or subtract-monika
                    if (temp.senderQbId.equalsIgnoreCase(authManager.partnerQbId)) {
                        if (temp.clicks.startsWith("+"))
                            Utils.updateClicksWithoutCard(authManager.ourClicks, temp.clicks, true);
                        else
                            Utils.updateClicksWithoutCard(authManager.ourClicks, temp.clicks, false);
                    }
                    if (body.length() > 3)
                        temp.textMsg = body.substring(3).trim();
                    else { //only clicks
                        temp.textMsg = "";
                        temp.clicks = Utils.convertClicks(temp.clicks).trim();
                    }
                }
                temp.sentOn = "" + Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

                saveMessageInDB(temp);
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
        EventBus.getDefault().post("Delivered Msg" + msgId);
    }

    @Override
    public void processMessageRead(QBChat qbChat, String s) {

    }

    //monika-function to set message listeners for all accepted members
    private void registerListeners() {
        if (QBChatService.getInstance().isLoggedIn()) {
            ArrayList<GetrelationshipsBean> clickInPartnerList = ModelManager.getInstance().getRelationManager().acceptedList;
            if (clickInPartnerList.size() != 0) {
                for (GetrelationshipsBean temp : clickInPartnerList) {
                    int partnerQBId = Integer.parseInt(temp.getPartnerQBId());
                    QBPrivateChat chatObject = null;
                    chatObject = QBChatService.getInstance().getPrivateChatManager().getChat(partnerQBId);
                    if (chatObject == null) {
                        chatObject = QBChatService.getInstance().getPrivateChatManager().createChat(partnerQBId, ChatThread.this);
                       Log.e(TAG ,"  R U ON LINE    "+getPresence(partnerQBId));
                    }

                }

            }
        }
    }

    //monika-code for updating clicks value
    private void updateValuesClicks(ChatMessageBody tempObject) {
        RelationManager manager = ModelManager.getInstance().getRelationManager();
        AuthManager authManager1 = ModelManager.getInstance().getAuthorizationManager();
        if (tempObject.card_originator.equalsIgnoreCase(authManager1.getUserId())) {
            Utils.updateClicksValue(authManager.ourClicks, manager.partnerClicks, tempObject.clicks, false);
        } else {
            Utils.updateClicksValue(authManager.ourClicks, manager.partnerClicks, tempObject.clicks, true);
        }
    }

    //monika-fucntion to log out from QB chat
    public void logoutQB() {
        boolean isLoggedIn = QBChatService.getInstance().isLoggedIn();
        if (!isLoggedIn) {
            return;
        }

        QBChatService.getInstance().logout(new QBEntityCallbackImpl() {

            @Override
            public void onSuccess() {
                // success

                QBChatService.getInstance().destroy();
            }

            @Override
            public void onError(final List list) {

            }
        });
    }

    //monika-save message in db
    private void saveMessageInDB(ChatMessageBody obj) {
        //find rId first on basis of qbid
        String partnerRId = "";
        int relationIndex=-1;
        ArrayList<GetrelationshipsBean> partnerList = ModelManager.getInstance().getRelationManager().acceptedList;
        for (GetrelationshipsBean temp : partnerList) {
            relationIndex++;
            if (temp.getPartnerQBId().equalsIgnoreCase(obj.senderQbId)) {
                partnerRId = temp.getRelationshipId();
                obj.relationshipId = partnerRId;
                break;
                //update clicks value in accepted list

            }
        }

        //check whether our activity is on top or not
        ActivityManager am = (ActivityManager)application.getSystemService(Application.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        String className = componentInfo.getClassName();
        if (obj.senderQbId.equalsIgnoreCase(authManager.partnerQbId)){
            if (className.equalsIgnoreCase("com.sourcefuse.clickinandroid.view.ChatRecordView")) {
            ModelManager.getInstance().getChatManager().chatMessageList.add(obj);
            EventBus.getDefault().postSticky("Chat Message Recieve");
          //  saveMessageInDB(temp);
            }else{
                //only if clicks are there
             //   if(!Utils.isEmptyString(obj.clicks) && !obj.clicks.equalsIgnoreCase("no"))
               // Utils.updateClicksBackgroundMsgs(relationIndex,obj);
                EventBus.getDefault().postSticky("UpdateMessageCounter###"+partnerRId);
                ModelManager.getInstance().getChatManager().chatMessageList.add(obj);
                EventBus.getDefault().postSticky("Chat Message Recieve");
            }
        }else {
            //only if clicks are there
            if(!Utils.isEmptyString(obj.clicks) && !obj.clicks.equalsIgnoreCase("no"))
                Utils.updateClicksBackgroundMsgs(relationIndex,obj);
            Log.e("post value------>",""+partnerRId);
            EventBus.getDefault().postSticky("UpdateMessageCounter###"+partnerRId);
        }
        if (!Utils.isEmptyString(partnerRId)) {
            if (messageInDb != null) {
                messageInDb.clear();
                messageInDb.add(obj);
            }

            new DBTask().execute(partnerRId);
        }
    }


    //monika-connection listener
    @Override
    public void connected(XMPPConnection xmppConnection) {
        if (!QBChatService.getInstance().isLoggedIn())
            loginToChat();

        EventBus.getDefault().post("Connected Successfully");
    }

    @Override
    public void authenticated(XMPPConnection xmppConnection) {

    }

    @Override
    public void connectionClosed() {
        //    if(!QBChatService.getInstance().isLoggedIn())
        //      loginToChat();
        EventBus.getDefault().post("Disconnected QB");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        //   if(!QBChatService.getInstance().isLoggedIn())
        // loginToChat();
        EventBus.getDefault().post("Disconnected QB");
    }

    @Override
    public void reconnectingIn(int i) {
        //   if(!QBChatService.getInstance().isLoggedIn())
        //     loginToChat();
        EventBus.getDefault().post("Disconnected QB");
    }

    @Override
    public void reconnectionSuccessful() {
        if (!QBChatService.getInstance().isLoggedIn())
            loginToChat();

        EventBus.getDefault().post("Connected Successfully");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        //  if(!QBChatService.getInstance().isLoggedIn())
        //    loginToChat();
        EventBus.getDefault().post("Disconnected QB");
    }

    class DBTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... str) {
            try {
                ClickinDbHelper dbHelper = new ClickinDbHelper(mContext);
                final String relId = str[0];
                // dbHelper.deleteChat(relId);
                dbHelper.addChatList(messageInDb);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    //dialog box with Ok action
    public void showDialog( String str) {

        final Dialog dialog = new Dialog(application.getApplicationContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_check_dialogs);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        msgI.setText(str);


        Button dismiss = (Button) dialog.findViewById(R.id.coolio);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                dialog.dismiss();
                //monika- stop service running in background
                Intent i = new Intent(application, MyQbChatService.class);
                application.stopService(i);

                ModelManager.setInstance();

                // android.util.android.util.Log.e("", "holder.logoutYes");
                Intent intent5 = new Intent(application, SplashView.class);
                intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                application.startActivity(intent5);
                //finish();


            }
        });
        dialog.show();
    }
}

