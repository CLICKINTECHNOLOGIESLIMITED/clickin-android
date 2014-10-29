package com.sourcefuse.clickinandroid.model;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sourcefuse.clickinandroid.model.bean.CardBean;
import com.sourcefuse.clickinandroid.model.bean.ChatRecordBeen;
import com.sourcefuse.clickinandroid.utils.APIs;
import com.sourcefuse.clickinandroid.utils.Log;
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
import java.util.Observable;

import de.greenrobot.event.EventBus;

public class ChatManager extends Observable implements ChatManagerI {
    private static final String TAG = ChatManager.class.getSimpleName();
    StringEntity se = null;
    AsyncHttpClient client;
    private AuthManager authManager;
    private ChatManager chatManager;
    public ArrayList<CardBean> tabArray = new ArrayList<CardBean>();
    private CardBean cardBean = null;

    private ChatRecordBeen chatRecordBeen = null;

    private int myTotalClick = 0;
    private int partnerTotalClick =0 ;

    public int getMyTotalClick() {
        return myTotalClick;
    }

    public void setMyTotalClick(int myTotalClick) {

        myTotalClick = getMyTotalClick() +myTotalClick;
        this.myTotalClick = myTotalClick;
    }

    public int getPartnerTotalClick() {
        return partnerTotalClick;
    }

    public void setPartnerTotalClick(int partnerTotalClick) {
        partnerTotalClick = getPartnerTotalClick() +partnerTotalClick;
        this.partnerTotalClick = partnerTotalClick;
    }






    public HashMap<String, ArrayList<CardBean>> categories = new HashMap<String, ArrayList<CardBean>>();
    ArrayList<ArrayList<CardBean>> lists = new ArrayList<ArrayList<CardBean>>();
    public ArrayList<CardBean> all = new ArrayList<CardBean>();

    @Override
    public void fetchChatRecord(String relationshipId, String phone,String usertoken,String chatId) {


        chatManager = ModelManager.getInstance().getChatManager();
        // TODO Auto-generated method stub
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone);
            userInputDetails.put("user_token", usertoken);
            userInputDetails.put("relationship_id", relationshipId);
            if(!Utils.isEmptyString(chatId)){
                userInputDetails.put("last_chat_id", chatId);
            }

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            Log.e(TAG, "userInputDetails-->" + userInputDetails);
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
                            Log.e("errorResponse", "->" + errorResponse);
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
                        try {
                            Log.e(TAG,"response FecthChat ->" + response);
                            state = response.getBoolean("success");
                            if (state) {
                                chatManager.refreshivechatList.clear();
                                Utils.clickCustomLog(response.toString());
                                JSONArray list = response.getJSONArray("chats");
                                for (int i = 0; i < list.length(); i++) {
                                    chatRecordBeen = new ChatRecordBeen();
                                    JSONObject data = list.getJSONObject(i);
                                    JSONObject chatObj = data.getJSONObject("Chat");

                                    if (chatObj.has("receiverQB_id"))
                                        chatRecordBeen.setRecieverQbId(chatObj.getString("receiverQB_id"));
                                    if (chatObj.has("sharedMessage"))
                                        chatRecordBeen.setSharedMessage(chatObj.getString("sharedMessage"));
                                    if (chatObj.has("video_thumb"))
                                        chatRecordBeen.setVideo_thumb(chatObj.getString("video_thumb"));
                                    if (chatObj.has("QB_id"))
                                        chatRecordBeen.setSenderQbId(chatObj.getString("QB_id"));
                                    if (chatObj.has("senderUserToken"))
                                        chatRecordBeen.setSenderUserToken(chatObj.getString("senderUserToken"));
                                    if (chatObj.has("type"))
                                        chatRecordBeen.setChatType(chatObj.getString("type"));
                                    if (chatObj.has("message"))
                                        chatRecordBeen.setChatText(chatObj.getString("message"));
                                    if (chatObj.has("content"))
                                       chatRecordBeen.setChatImageUrl(chatObj.getString("content"));
                                    if (chatObj.has("relationshipId"))
                                        chatRecordBeen.setRelationshipId(chatObj.getString("relationshipId"));
                                    if (chatObj.has("_id"))
                                        chatRecordBeen.set_id(chatObj.getString("_id"));
                                    if (chatObj.has("userId"))
                                        chatRecordBeen.setUserId(chatObj.getString("userId"));
                                    if (chatObj.has("location_coordinates"))
                                        chatRecordBeen.setLocation_coordinates(chatObj.getString("location_coordinates"));
                                    if (chatObj.has("clicks"))
                                        chatRecordBeen.setClicks(chatObj.getString("clicks"));
                                    if (chatObj.has("isDelivered"))
                                        chatRecordBeen.setIsDelivered(chatObj.getString("isDelivered"));
                                    if (chatObj.has("imageRatio"))
                                        chatRecordBeen.setImageRatio(chatObj.getString("imageRatio"));
                                    if (chatObj.has("chatId"))
                                        chatRecordBeen.setChatId(chatObj.getString("chatId"));
                                    if (chatObj.has("cards"))
                                        chatRecordBeen.setCards(chatObj.getString("cards"));
                                    if (chatObj.has("sentOn"))
                                        chatRecordBeen.setTimeStamp(chatObj.getString("sentOn"));
                                    chatManager.refreshivechatList.add(chatRecordBeen);
                                }

                                chatManager.chatListFromServer.addAll(0,chatManager.refreshivechatList);

                                EventBus.getDefault().post("FecthChat True");
                            }else{
                                EventBus.getDefault().post("FecthChat False");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );

    }

    @Override
    public void fetchCards(String phone, String usertoken) {

        try {
            client = new AsyncHttpClient();
            client.addHeader("user_token", usertoken);
            client.addHeader("phone_no", phone);
            Log.e("usertoken-phone_no-othersphone-->", "" + usertoken + "-" + phone);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        client.get(APIs.FETCHCARDS, new JsonHttpResponseHandler() {
            boolean success = false;

            @Override
            public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                super.onFailure(statusCode, e, errorResponse);
                System.out.println("errorResponse--> " + errorResponse);
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
                    Log.e("", "response--> " + response);
                    Utils.clickCustomLog(response.toString());
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
                            Log.e(TAG,"Catagories Values" +categories);

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

    public void chatShare(String phone_no, String user_token, String relationshipId, String chatId, String media, String fbAccessToken, String twitterAccessToken, String twitterAccessTokenSecret, String googlePlusAccessToken, String comment) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone_no);
            userInputDetails.put("user_token", user_token);
            userInputDetails.put("relationship_id", relationshipId);
            userInputDetails.put("chat_id", chatId);
            userInputDetails.put("media", media);
            userInputDetails.put("fb_access_token", fbAccessToken);
            userInputDetails.put("twitter_access_token", twitterAccessToken);
            userInputDetails.put("twitter_access_token_secret", twitterAccessTokenSecret);
            userInputDetails.put("googleplus_access_token", googlePlusAccessToken);
            userInputDetails.put("comment", comment);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            Log.e(TAG, "ChatShare-->" + userInputDetails);
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
                            Log.e(TAG, "response ChatShare ->" + response);
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
            Log.e(TAG, "ShareAction-->" + userInputDetails);
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
                            Log.e(TAG, "response ShareAction ->" + response);
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
            Log.e(TAG, "ShareCards-->" + userInputDetails);
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
                            Log.e(TAG, "response ShareCards ->" + response);
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
            Log.e(TAG, "GetUnReadMessageCount-->" + userInputDetails);
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
                            Log.e(TAG, "response GetUnReadMessageCount ->" + response);
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
            Log.e(TAG, "ResetUnReadMessageCount-->" + userInputDetails);
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
                            Log.e(TAG, "response ResetUnReadMessageCount ->" + response);
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
            Log.e(TAG, "ResetBadgeCount-->" + userInputDetails);
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
                            Log.e(TAG, "response ResetBadgeCount ->" + response);
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
