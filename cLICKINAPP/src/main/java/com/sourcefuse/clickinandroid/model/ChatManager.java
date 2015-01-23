package com.sourcefuse.clickinandroid.model;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sourcefuse.clickinandroid.model.bean.CardBean;
import com.sourcefuse.clickinandroid.model.bean.ChatMessageBody;
import com.sourcefuse.clickinandroid.model.bean.ChatRecordBeen;
import com.sourcefuse.clickinandroid.utils.APIs;
import com.sourcefuse.clickinandroid.utils.Utils;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ChatManager {
    private static final String TAG = ChatManager.class.getSimpleName();
    public ArrayList<CardBean> tabArray = new ArrayList<CardBean>();
    public ArrayList<ChatMessageBody> chatListFromServer = new ArrayList<ChatMessageBody>();
    public ArrayList<ChatMessageBody> refreshivechatList = new ArrayList<ChatMessageBody>();
    public HashMap<String, ArrayList<CardBean>> categories = new HashMap<String, ArrayList<CardBean>>();
    public ArrayList<CardBean> all = new ArrayList<CardBean>();
    //this list to maintain current chat list to view in chat record view
    public ArrayList<ChatMessageBody> chatMessageList = new ArrayList<ChatMessageBody>();
    StringEntity se = null;
    AsyncHttpClient client;
    ArrayList<ArrayList<CardBean>> lists = new ArrayList<ArrayList<CardBean>>();
    private AuthManager authManager;
    private ChatManager chatManager;
    private CardBean cardBean = null;
    private ChatRecordBeen chatRecordBeen = null;
    private int myTotalClick = 0;
    private String relationshipId;
    private int partnerTotalClick = 0;

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setrelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }


    public int getMyTotalClick() {
        return myTotalClick;
    }

    public void setMyTotalClick(int myTotalClick) {

        myTotalClick = getMyTotalClick() + myTotalClick;
        this.myTotalClick = myTotalClick;
    }

    public int getPartnerTotalClick() {
        return partnerTotalClick;
    }

    public void setPartnerTotalClick(int partnerTotalClick) {
        partnerTotalClick = getPartnerTotalClick() + partnerTotalClick;
        this.partnerTotalClick = partnerTotalClick;
    }


    public void fetchChatRecord(String relationshipId, String phone, String usertoken, String chatId) {


        chatManager = ModelManager.getInstance().getChatManager();
        // TODO Auto-generated method stub
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone);
            userInputDetails.put("user_token", usertoken);
            userInputDetails.put("relationship_id", relationshipId);
            if (!Utils.isEmptyString(chatId)) {
                userInputDetails.put("last_chat_id", chatId);
            }

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.FETCHCHATRECORDS, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e,
                                          JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        if (errorResponse != null) {

                            EventBus.getDefault().post("FecthChat False");
                        } else {
                            EventBus.getDefault().post("FecthChat Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode,
                                          org.apache.http.Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        ChatMessageBody temp = null;
                        JSONObject chatObj=null;
                        try {
                            state = response.getBoolean("success");
                            if (state) {

                                refreshivechatList.clear();

                                JSONArray list = response.getJSONArray("chats");

                                for (int i = 0; i < list.length(); i++) {
                                    try {
                                        temp = new ChatMessageBody();
                                        JSONObject data = list.getJSONObject(i);
                                         chatObj = data.getJSONObject("Chat");

                                        // if (chatObj.has("receiverQB_id"))
                                        //   temp.re(chatObj.getString("receiverQB_id"));
                                        if (chatObj.has("sharedMessage"))
                                            temp.sharedMessage = chatObj.getString("sharedMessage");
                                        if (chatObj.has("video_thumb"))
                                            temp.video_thumb = chatObj.getString("video_thumb");
                                        if (chatObj.has("QB_id"))
                                            temp.senderQbId = chatObj.getString("QB_id");
                                        if (chatObj.has("senderUserToken"))
                                            temp.senderUserToken = chatObj.getString("senderUserToken");
                                        if (chatObj.has("type"))
                                            temp.chatType = Integer.parseInt(chatObj.getString("type"));
                                        if (chatObj.has("message"))
                                            temp.textMsg = chatObj.getString("message");
                                        if (chatObj.has("content"))
                                            temp.content_url = chatObj.getString("content");
                                        if (chatObj.has("relationshipId"))
                                            temp.relationshipId = chatObj.getString("relationshipId");
                                        if (chatObj.has("_id"))
                                            temp._id = chatObj.getString("_id");
                                        if (chatObj.has("userId"))
                                            temp.userId = chatObj.getString("userId");
                                        if (chatObj.has("location_coordinates"))
                                            temp.location_coordinates = chatObj.getString("location_coordinates");
                                        if (chatObj.has("clicks")) {
                                            temp.clicks = chatObj.getString("clicks");
                                            if (Utils.isEmptyString(temp.clicks))
                                                temp.clicks = "no";
                                        }

                                        if (chatObj.has("chatId")) {
                                            temp.chatId = chatObj.getString("chatId");


                                        }
                                        if (chatObj.has("isDelivered")) {
                                            temp.isDelivered = chatObj.getString("isDelivered");
                                            if(temp.isDelivered.equalsIgnoreCase("yes")){
                                                temp.deliveredChatID = temp.chatId; //if deliveredChatId exists, it means delivered-monika
                                                                                    //we have to do this to match live chat params
                                            }
                                        }
                                        if (chatObj.has("imageRatio"))
                                            temp.imageRatio = chatObj.getString("imageRatio");


                                        // temp.cardchatObj.getString("cards"));
                                        if (chatObj.has("sentOn"))
                                            temp.sentOn = chatObj.getString("sentOn");
                                        JSONArray cards = chatObj.getJSONArray("cards");
                                        if (cards != null) {
                                            temp.card_id = (String) cards.get(0);
                                            temp.card_heading = (String) cards.get(1);
                                            temp.card_content = (String) cards.get(2);
                                            temp.card_url = (String) cards.get(3);
                                            temp.clicks = (String) cards.get(4);
                                            temp.card_Accepted_Rejected = (String) cards.get(5);
                                            temp.card_originator = (String) cards.get(6);
                                            temp.is_CustomCard = Boolean.valueOf((String) cards.get(7));
                                            temp.card_DB_ID = (String) cards.get(8);
                                            temp.card_Played_Countered = (String) cards.get(9);
                                            if(temp.card_Accepted_Rejected.equalsIgnoreCase("countered"))
                                                temp.card_Played_Countered="played";

                                            //this param is added new, in history might be it not come
                                            if(cards.length()>10) {
                                                temp.card_owner = (String) cards.get(10);
                                                if (Utils.isEmptyString(temp.card_owner))
                                                    temp.card_owner = "";
                                            }else{
                                                temp.card_owner = "";
                                            }
                                        }

                                        //code to fetch share array, if exists-monika
                                        JSONArray sharedMessage = chatObj.getJSONArray("sharedMessage");
                                        if (sharedMessage != null) {
                                            temp.originalMessageID = (String) sharedMessage.get(0);
                                            temp.shareStatus = (String) sharedMessage.get(1);
                                            temp.senderQbId = (String) sharedMessage.get(2);
                                            temp.isAccepted = (String) sharedMessage.get(3);
                                            temp.isMessageSender = (String) sharedMessage.get(4);
                                            temp.shareComment = (String) sharedMessage.get(5);
                                            temp.sharingMedia = (String) sharedMessage.get(6);

                                            temp.facebookToken = (String) sharedMessage.get(7);

                                        }

                                        refreshivechatList.add(temp);
                                    } catch (JSONException e) { //specially for cards array
                                        e.printStackTrace();
                                        try {
                                            JSONArray sharedMessage = chatObj.getJSONArray("sharedMessage");
                                            if (sharedMessage != null) {
                                                temp.originalMessageID = (String) sharedMessage.get(0);
                                                temp.shareStatus = (String) sharedMessage.get(1);
                                                temp.senderQbId = (String) sharedMessage.get(2);
                                                temp.isAccepted = (String) sharedMessage.get(3);
                                                temp.isMessageSender = (String) sharedMessage.get(4);
                                                temp.shareComment = (String) sharedMessage.get(5);
                                                temp.sharingMedia = (String) sharedMessage.get(6);

                                                temp.facebookToken = (String) sharedMessage.get(7);

                                            }
                                        }catch (Exception e1){
                                         //   e1.printStackTrace();
                    //                        refreshivechatList.add(temp);
                                        }
                                        refreshivechatList.add(temp);
                                    }
                                }

                                chatManager.chatMessageList.addAll(0, refreshivechatList);

                                EventBus.getDefault().post("FecthChat True");
                            } else {
                                EventBus.getDefault().post("FecthChat False");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );

    }


    public void fetchCards(String phone, String usertoken) {

        try {
            client = new AsyncHttpClient();

            client.addHeader("User-Token", usertoken);
            client.addHeader("Phone-No", phone);

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        client.get(APIs.FETCHCARDS, new JsonHttpResponseHandler() {
            boolean success = false;

            @Override
            public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                super.onFailure(statusCode, e, errorResponse);

                if (errorResponse != null) {

                    EventBus.getDefault().post("FetchCard False");
                } else {
                    EventBus.getDefault().post("FetchCard Network Error");
                }

            }

            @SuppressWarnings("unused")
            @Override
            public void onSuccess(int statusCode,
                                  org.apache.http.Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {


                    success = response.getBoolean("success");

                    tabArray.clear();

                    if (success) {

                        JSONArray list = response.getJSONArray("categories");
                        for (int i = 0; i < list.length(); i++) {
                            cardBean = new CardBean();
                            JSONObject data = list.getJSONObject(i);
                            JSONObject categoryObj = data.getJSONObject("Category");
                            cardBean.setCategoriesName(categoryObj.getString("name"));
                            cardBean.setCategoriesActive(categoryObj.getString("active"));
                            tabArray.add(cardBean);

                            //  lists.add(new ArrayList<CardBean>());
                            categories.put(categoryObj.getString("name"), new ArrayList<CardBean>());


                        }


                        JSONArray cardList = response.getJSONArray("cards");
                        for (int j = 0; j < cardList.length(); j++) {

                            JSONObject data = cardList.getJSONObject(j);
                            JSONObject cardObject = data.getJSONObject("Card");
                            JSONArray categoryList = cardObject.getJSONArray("category");

                            for (int k = 0; k < categoryList.length(); k++) {
                                JSONObject category = categoryList.getJSONObject(k);

                                List<CardBean> ls = categories.get(category.getString("name"));

                                cardBean = new CardBean();
                                cardBean.setCardUrl(cardObject.getString("image"));
                                cardBean.setCard_Id(cardObject.getString("_id"));
                                cardBean.setCardActive(cardObject.getString("active"));
                                cardBean.setCardDescription(cardObject.getString("description"));
                                cardBean.setCardTitle(cardObject.getString("title"));
                                if(ls != null)
                                ls.add(cardBean);

                            }

                        }

                        EventBus.getDefault().post("FetchCard True");
                    }

                } catch (JSONException e) {
                    EventBus.getDefault().post("FetchCard False");
                    e.printStackTrace();
                }

            }

        });

    }

    public void chatShare(String phone_no, String user_token, String relationshipId, String chatId, String media, String fbAccessToken, String comment, String accepted) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone_no);
            userInputDetails.put("user_token", user_token);
            userInputDetails.put("relationship_id", relationshipId);
            userInputDetails.put("chat_id", chatId);
            userInputDetails.put("media", media);
            userInputDetails.put("fb_access_token", fbAccessToken);
           /* userInputDetails.put("twitter_access_token", twitterAccessToken);
            userInputDetails.put("twitter_access_token_secret", twitterAccessTokenSecret);
            userInputDetails.put("googleplus_access_token", googlePlusAccessToken);*/
            userInputDetails.put("comment", comment);
            userInputDetails.put("accepted", accepted);


            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.CHATSHARE, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);


                        if (errorResponse != null) {
                            EventBus.getDefault().post("ChatShare False");
                        } else {
                            EventBus.getDefault().post("ChatShare Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {

                            state = response.getBoolean("success");
                            if (state) {

                                EventBus.getDefault().post("ChatShare True");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }

    public void sharingAction(String phone_no, String user_token, String sharingId, String status) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone_no);
            userInputDetails.put("user_token", user_token);
            userInputDetails.put("sharing_id", sharingId);
            userInputDetails.put("status", status);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.SHARINGACTION, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        if (errorResponse != null) {
                            EventBus.getDefault().post("ShareAction False");
                        } else {
                            EventBus.getDefault().post("ShareAction Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {

                            state = response.getBoolean("success");
                            if (state) {

                                EventBus.getDefault().post("ShareAction True");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }

    public void savecards(String phone_no, String user_token, String title) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone_no);
            userInputDetails.put("user_token", user_token);
            userInputDetails.put("title", title);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.SAVECARDS, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        if (errorResponse != null) {
                            EventBus.getDefault().post("ShareCards False");
                        } else {
                            EventBus.getDefault().post("ShareCards Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {

                            state = response.getBoolean("success");
                            if (state) {

                                EventBus.getDefault().post("ShareCards True");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }

    public void getUnReadMessageCount(String phone_no, String user_token, String relationshipId) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone_no);
            userInputDetails.put("user_token", user_token);
            userInputDetails.put("relationship_id", relationshipId);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.GETUNREADMESSAGECOUNT, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        if (errorResponse != null) {
                            EventBus.getDefault().post("GetUnReadMessageCount False");
                        } else {
                            EventBus.getDefault().post("GetUnReadMessageCount Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {

                            state = response.getBoolean("success");
                            if (state) {

                                EventBus.getDefault().post("GetUnReadMessageCount True");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }


    public void resetUnReadMessageCount(String phone_no, String user_token, String relationshipId) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone_no);
            userInputDetails.put("user_token", user_token);
            userInputDetails.put("relationship_id", relationshipId);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.RESETUNREADMESSAGECOUNT, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        if (errorResponse != null) {
                            EventBus.getDefault().post("ResetUnReadMessageCount False");
                        } else {
                            EventBus.getDefault().post("ResetUnReadMessageCount Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {

                            state = response.getBoolean("success");
                            if (state) {

                                EventBus.getDefault().post("ResetUnReadMessageCount True");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }

    public void resetBadgeCount(String phone_no, String user_token) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone_no);
            userInputDetails.put("user_token", user_token);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.RESETBADGECOUNT, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        if (errorResponse != null) {
                            EventBus.getDefault().post("ResetBadgeCount False");
                        } else {
                            EventBus.getDefault().post("ResetBadgeCount Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {

                            state = response.getBoolean("success");
                            if (state) {

                                EventBus.getDefault().post("ResetBadgeCount True");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }


}
