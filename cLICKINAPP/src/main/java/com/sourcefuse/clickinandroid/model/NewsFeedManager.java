package com.sourcefuse.clickinandroid.model;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sourcefuse.clickinandroid.model.bean.CurrentClickerBean;
import com.sourcefuse.clickinandroid.model.bean.FeedStarsBean;
import com.sourcefuse.clickinandroid.model.bean.NewsFeedBean;
import com.sourcefuse.clickinandroid.utils.APIs;
import com.sourcefuse.clickinandroid.utils.Utils;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class NewsFeedManager {
    public ArrayList<NewsFeedBean> userFeed = new ArrayList<NewsFeedBean>();
    public ArrayList<FeedStarsBean> feedStarsList = new ArrayList<FeedStarsBean>();
    StringEntity se = null;
    AsyncHttpClient client;
    private String TAG = this.getClass().getSimpleName();
    private AuthManager authManager;
    private CurrentClickerBean currentClickerBean;
    private ProfileManager profilemanager;

    public void fetchNewsFeed(String lastNewsfeedId, String phone, String usertoken) {
        // TODO Auto-generated method stub
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone);
            userInputDetails.put("user_token", usertoken);
            userInputDetails.put("last_newsfeed_id", lastNewsfeedId);



            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                    "application/json"));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.FETCHNEWSFEEDS, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e,
                                          JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        userFeed.clear();
                        if (errorResponse != null) {

                            EventBus.getDefault().post("NewsFeed False");
                        } else {
                            EventBus.getDefault().post("NewsFeed Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode,
                                          org.apache.http.Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {


                            state = response.getBoolean("success");
                            /*if (state) {

								triggerObservers("News Feed True");
							}*/

                            JSONArray newsfeedArray = response.getJSONArray("newsfeedArray");
                            userFeed.clear();

                            for (int i = 0; i < newsfeedArray.length(); i++) {
                                NewsFeedBean allNewsFeed = new NewsFeedBean();

                                allNewsFeed.setNewsfeedArray_id(newsfeedArray.getJSONObject(i).getString("_id"));
                                allNewsFeed.setNewsfeedArray_user_id(newsfeedArray.getJSONObject(i).getString("user_id"));
                                allNewsFeed.setNewsfeedArray_newsfeed_msg(newsfeedArray.getJSONObject(i).getString("newsfeed_msg"));
                                allNewsFeed.setNewsfeedArray_read(newsfeedArray.getJSONObject(i).getString("read"));
                                /*
                                Follower_user_id Array
                                 */
                                JSONArray followerUserId = newsfeedArray.getJSONObject(i).getJSONArray("follower_user_id");
                                ArrayList<NewsFeedBean> followerArray = new ArrayList<NewsFeedBean>();
                                for (int j = 0; j < followerUserId.length(); j++) {
                                    NewsFeedBean followerUserData = new NewsFeedBean();
                                    followerUserData.setNewsFeedArray_follower_user_id_$id(followerUserId.getJSONObject(j).getJSONObject("_id").getString("$id"));
                                    followerUserData.setNewsFeedArray_follower_user_id_user_id(followerUserId.getJSONObject(j).getString("user_id"));
                                    followerArray.add(followerUserData);
                                }
                                allNewsFeed.setFollowerList(followerArray);

                                if (newsfeedArray.getJSONObject(i).has("stars_count"))
                                    allNewsFeed.setNewsfeedArray_stars_count(newsfeedArray.getJSONObject(i).getInt("stars_count"));

                                if (newsfeedArray.getJSONObject(i).has("comments_count"))
                                    allNewsFeed.setNewsfeedArray_comments_count(newsfeedArray.getJSONObject(i).getInt("comments_count"));

                                if (newsfeedArray.getJSONObject(i).has("user_starred"))
                                    allNewsFeed.setNewsfeedArray_user_starred(newsfeedArray.getJSONObject(i).getString("user_starred"));

                                if (newsfeedArray.getJSONObject(i).has("user_commented"))
                                    allNewsFeed.setNewsfeedArray_user_commented(newsfeedArray.getJSONObject(i).getString("user_commented"));

                                /*
                                chatDetail
                                 */
//                                if (!Utils.isEmptyString(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").toString())) {
                                if (!newsfeedArray.getJSONObject(i).isNull("chatDetail")) {

                                    JSONObject chatObj = newsfeedArray.getJSONObject(i).getJSONObject("chatDetail");
                                    if (chatObj.has("QB_id"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_QB_id(chatObj.getString("QB_id"));
                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("QB_id"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_QB_id(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("QB_id"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("_id"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_id(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("_id"));

                                    if (!newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").isNull("cards")) {
                                        if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("cards"))
                                            allNewsFeed.setNewsFeedArray_chatDetail_cards(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getJSONArray("cards"));
                                    }
                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("chatId"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_chatId(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("chatId"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("clicks"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_clicks(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("clicks"));


                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("comments_count"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_comments_count(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("comments_count"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("content"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_content(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("content"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getJSONObject("created").has("sec"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_created_sec(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getJSONObject("created").getString("sec"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getJSONObject("created").has("usec"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_created_usec(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getJSONObject("created").getString("usec"));

                                    if (!newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").isNull("deliveredChatID")) {
                                        allNewsFeed.setNewsFeedArray_chatDetail_delieveredChatId(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("delieveredChatId"));
                                    } else {
                                        allNewsFeed.setNewsFeedArray_chatDetail_delieveredChatId("");
                                    }


                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("imageRatio"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_imageRatio(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("imageRatio"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("isDelivered"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_isDelievered(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("isDelivered"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("location_coordinates"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_location_coordinates(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("location_coordinates"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("message"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_message(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("message"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getJSONObject("modified").has("sec"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_modified_sec(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getJSONObject("modified").getString("sec"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getJSONObject("modified").has("usec"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_modified_usec(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getJSONObject("modified").getString("usec"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("relationshipId"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_relationshipId(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("relationshipId"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("senderUserToken"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_senderUserToken(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("senderUserToken"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("sentOn"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_sentOn(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("sentOn"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("sharedMessage"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_sharedMessage(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("sharedMessage"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("stars_count"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_stars_count(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("stars_count"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("type"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_type(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("type"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("userId"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_userId(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("userId"));

                                    if (newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").has("video_thumb"))
                                        allNewsFeed.setNewsFeedArray_chatDetail_video_thumb(newsfeedArray.getJSONObject(i).getJSONObject("chatDetail").getString("video_thumb"));
                                }

                                /*
                                Comment Array
                                 */
                                if (!newsfeedArray.getJSONObject(i).isNull("commentArray")) {
                                    JSONArray commentArray = newsfeedArray.getJSONObject(i).getJSONArray("commentArray");
                                    ArrayList<NewsFeedBean> eachCommentArray = new ArrayList<NewsFeedBean>();

                                    for (int l = 0; l < commentArray.length(); l++) {
                                        NewsFeedBean commentUserData = new NewsFeedBean();

                                        commentUserData.setNewsFeedArray_commentArray_id(commentArray.getJSONObject(l).getString("_id"));
                                        commentUserData.setNewsFeedArray_commentArray_chat_id(commentArray.getJSONObject(l).getString("chat_id"));
                                        commentUserData.setNewsFeedArray_commentArray_newsfeed_id(commentArray.getJSONObject(l).getString("newsfeed_id"));
                                        commentUserData.setNewsFeedArray_commentArray_type(commentArray.getJSONObject(l).getString("type"));
                                        commentUserData.setNewsFeedArray_commentArray_comment(commentArray.getJSONObject(l).getString("comment"));
                                        commentUserData.setNewsFeedArray_commentArray_user_id(commentArray.getJSONObject(l).getString("user_id"));
                                        commentUserData.setNewsFeedArray_commentArray_user_name(commentArray.getJSONObject(l).getString("user_name"));
                                        commentUserData.setNewsFeedArray_commentArray_user_pic(commentArray.getJSONObject(l).getString("user_pic"));
                                        commentUserData.setNewsFeedArray_commentArray_modified_sec(commentArray.getJSONObject(l).getJSONObject("modified").getString("sec"));
                                        commentUserData.setNewsFeedArray_commentArray_modified_usec(commentArray.getJSONObject(l).getJSONObject("modified").getString("usec"));
                                        commentUserData.setNewsFeedArray_commentArray_created_sec(commentArray.getJSONObject(l).getJSONObject("created").getString("sec"));
                                        commentUserData.setNewsFeedArray_commentArray_created_usec(commentArray.getJSONObject(l).getJSONObject("created").getString("usec"));

                                        eachCommentArray.add(commentUserData);
                                    }
                                    allNewsFeed.setCommentArrayList(eachCommentArray);
                                }
                                /*
                                Starred Array
                                 */
                                if (!newsfeedArray.getJSONObject(i).isNull("starredArray")) {
                                    JSONArray starredArray = newsfeedArray.getJSONObject(i).getJSONArray("starredArray");
                                    ArrayList<NewsFeedBean> eachStarredArray = new ArrayList<NewsFeedBean>();
                                    for (int k = 0; k < starredArray.length(); k++) {
                                        NewsFeedBean starredUserData = new NewsFeedBean();
                                        starredUserData.setNewsFeedArray_starredArray_id(starredArray.getJSONObject(k).getString("_id"));
                                        starredUserData.setNewsFeedArray_starredArray_user_name(starredArray.getJSONObject(k).getString("user_name"));

                                        eachStarredArray.add(starredUserData);
                                    }
                                    allNewsFeed.setStarredArrayList(eachStarredArray);
                                }
                                allNewsFeed.setNewsfeedArray_created(newsfeedArray.getJSONObject(i).getString("created"));
                                allNewsFeed.setNewsfeedArray_modified(newsfeedArray.getJSONObject(i).getString("modified"));

                                /*
                                Reciever Details
                                 */
                                if (!newsfeedArray.getJSONObject(i).isNull("receiverDetail")) {
                                    allNewsFeed.setNewsFeedArray_receiverDetail_id(newsfeedArray.getJSONObject(i).getJSONObject("receiverDetail").getString("_id"));
                                    allNewsFeed.setNewsFeedArray_receiverDetail_name(newsfeedArray.getJSONObject(i).getJSONObject("receiverDetail").getString("name"));
                                    allNewsFeed.setNewsFeedArray_receiverDetail_user_pic(newsfeedArray.getJSONObject(i).getJSONObject("receiverDetail").getString("user_pic"));
                                    allNewsFeed.setNewsFeedArray_receiverDetail_phno(newsfeedArray.getJSONObject(i).getJSONObject("receiverDetail").getString("phone_no"));
                                } else {
                                    allNewsFeed.setNewsFeedArray_receiverDetail_id("");
                                    allNewsFeed.setNewsFeedArray_receiverDetail_name("");
                                    allNewsFeed.setNewsFeedArray_receiverDetail_phno("");
                                }
                                 /*
                                Sender Details
                                 */
                                if (!newsfeedArray.getJSONObject(i).isNull("senderDetail")) {

                                    if (!newsfeedArray.getJSONObject(i).getJSONObject("senderDetail").getString("_id").equalsIgnoreCase("null")) {
                                        allNewsFeed.setNewsFeedArray_senderDetail_id(newsfeedArray.getJSONObject(i).getJSONObject("senderDetail").getString("_id"));
                                        allNewsFeed.setNewsFeedArray_senderDetail_name(newsfeedArray.getJSONObject(i).getJSONObject("senderDetail").getString("name"));
                                        allNewsFeed.setNewsFeedArray_senderDetail_user_pic(newsfeedArray.getJSONObject(i).getJSONObject("senderDetail").getString("user_pic"));
                                        allNewsFeed.setNewsFeedArray_senderDetail_phno(newsfeedArray.getJSONObject(i).getJSONObject("senderDetail").getString("phone_no"));

                                        userFeed.add(allNewsFeed);

                                    }

                                }
//                                else
//                                {
//                                    allNewsFeed.setNewsFeedArray_senderDetail_id("");
//                                    allNewsFeed.setNewsFeedArray_senderDetail_name("");
//                                    allNewsFeed.setNewsFeedArray_senderDetail_phno("");
//                                }


                            }

                            if (state) {
                                EventBus.getDefault().post("NewsFeed True");
                            }
                        } catch (JSONException e) {
                            EventBus.getDefault().post("NewsFeed False");
                            e.printStackTrace();
                        }



                    }

                }
        );

    }


    public void fetchFbFriends(String accessToken, String phone, String usertoken) {
        profilemanager = ModelManager.getInstance().getProfileManager();
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone);
            userInputDetails.put("user_token", usertoken);
            userInputDetails.put("access_token", accessToken);


            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));


        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.FETCHFBFRIENDS, se, "application/json",
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);

                        if (errorResponse != null) {
                            EventBus.getDefault().post("FetchFbFriend false");
                        } else {
                            EventBus.getDefault().post("FetchFbFriend Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean success = false;
                        try {

                            success = response.getBoolean("success");
                            profilemanager.currentClickerListFB.clear();
                            if (success) {
                                JSONArray list = response.getJSONArray("fbfriends");
                                for (int i = 0; i < list.length(); i++) {
                                    currentClickerBean = new CurrentClickerBean();

                                    JSONObject data = list.getJSONObject(i);
                                    currentClickerBean.setName(data.getString("fb_name"));
                                    currentClickerBean.setGetClickerPhone(data.getString("phone_no"));

                                    String picUrl = null;
                                    picUrl = (data.getString("fb_user_pic_url")).replace("http:", "https:");
                                    currentClickerBean.setClickerPix(picUrl);
                                    if (data.has("follow_status")) {
                                        if (data.getString("follow_status").matches("pending")) {
                                            currentClickerBean.setFollow(1);
                                        } else {
                                            currentClickerBean.setFollow(0);
                                        }
                                    } else {
                                        currentClickerBean.setFollow(0);
                                    }

                                    profilemanager.currentClickerListFB.add(currentClickerBean);
                                }
                                EventBus.getDefault().post("FetchFbFriend True");
                            } else {
                                EventBus.getDefault().post("FetchFbFriend false");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }


    public void saveStarComment(String phone, String user_token, String newsfeedsId, String comment, String type) {
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone);
            userInputDetails.put("user_token", user_token);
            userInputDetails.put("newsfeed_id", newsfeedsId);
            userInputDetails.put("comment", comment);
            userInputDetails.put("type", type);


            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                    "application/json"));


        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.SAVESTARCOMMENT, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e,
                                          JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);

                        if (errorResponse != null) {

                            EventBus.getDefault().post("SaveStarComment False");
                        } else {
                            EventBus.getDefault().post("SaveStarComment Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode,
                                          org.apache.http.Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {

                            state = response.getBoolean("success");
                            if (state) {
                                EventBus.getDefault().post("SaveStarComment True");
                            } else {
                                EventBus.getDefault().post("SaveStarComment False");
                            }
                        } catch (JSONException e) {
                            EventBus.getDefault().post("SaveStarComment False");
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    public void fetchCommentStars(String phone_no, String user_token, String lastId, String newsfeedId, String type) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone_no);
            userInputDetails.put("user_token", user_token);
            userInputDetails.put("last_id", lastId);
            userInputDetails.put("newsfeed_id", newsfeedId);
            userInputDetails.put("type", type);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.FETCHCOMMENTSTATUS, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        feedStarsList.clear();
                        if (errorResponse != null) {
                            EventBus.getDefault().post("FetchCommentStatus False");
                        } else {
                            EventBus.getDefault().post("FetchCommentStatus Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {

                            state = response.getBoolean("success");
                            JSONArray recordsArray = response.getJSONArray("records");
                            feedStarsList.clear();

                            for (int i = 0; i < recordsArray.length(); i++) {
                                FeedStarsBean feedStars = new FeedStarsBean();
                                feedStars.setChatId(recordsArray.getJSONObject(i).getString("chat_id"));
                                feedStars.setId(recordsArray.getJSONObject(i).getString("_id"));
                                feedStars.setNewsId(recordsArray.getJSONObject(i).getString("newsfeed_id"));
                                feedStars.setType(recordsArray.getJSONObject(i).getString("type"));
                                feedStars.setUserId(recordsArray.getJSONObject(i).getString("user_id"));
                                feedStars.setUserName(recordsArray.getJSONObject(i).getString("user_name"));
                                feedStars.setUserPic(recordsArray.getJSONObject(i).getString("user_pic"));
                                if (recordsArray.getJSONObject(i).getJSONObject("modified").has("sec"))

                                feedStars.setcreated_sec(recordsArray.getJSONObject(i).getJSONObject("modified").getString("sec"));

                                if (recordsArray.getJSONObject(i).getJSONObject("modified").has("usec"))
                                    feedStars.setcreated_usec(recordsArray.getJSONObject(i).getJSONObject("modified").getString("usec"));
                                if (!recordsArray.getJSONObject(i).isNull("is_user_in_relation"))
                                    feedStars.setIs_user_in_relation(recordsArray.getJSONObject(i).getInt("is_user_in_relation"));
                                if (!recordsArray.getJSONObject(i).isNull("is_user_follower"))
                                    feedStars.setIs_user_follower(recordsArray.getJSONObject(i).getInt("is_user_follower"));
                                if (!recordsArray.getJSONObject(i).isNull("is_user_follower_acceptance"))
                                    feedStars.setIs_user_follower_acceptance(recordsArray.getJSONObject(i).getString("is_user_follower_acceptance"));
                                if (!recordsArray.getJSONObject(i).isNull("is_user_following_acceptance"))
                                    feedStars.setIs_user_following_acceptance(recordsArray.getJSONObject(i).getString("is_user_following_acceptance"));
                                if (!recordsArray.getJSONObject(i).isNull("is_user_following"))
                                    feedStars.setIs_user_following(recordsArray.getJSONObject(i).getInt("is_user_following"));
                                if (!recordsArray.getJSONObject(i).isNull("is_user_in_relation_acceptance"))
                                    feedStars.setIs_user_in_relation_acceptance(recordsArray.getJSONObject(i).getString("is_user_in_relation_acceptance"));
                                if (!recordsArray.getJSONObject(i).isNull("comment"))
                                    feedStars.setComment(recordsArray.getJSONObject(i).getString("comment"));
                                feedStarsList.add(feedStars);
                            }
                            if (state) {

                                EventBus.getDefault().post("FetchCommentStatus True");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }


    public void newFeedDelete(String phone_no, String user_token, String newsfeedId) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone_no);
            userInputDetails.put("user_token", user_token);
            userInputDetails.put("newsfeed_id", newsfeedId);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.DELETENEWSFEED, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        if (errorResponse != null) {
                            EventBus.getDefault().post("NewsFeedDelete False");
                        } else {
                            EventBus.getDefault().post("NewsFeedDelete Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {

                            state = response.getBoolean("success");
                            if (state) {

                                EventBus.getDefault().post("NewsFeedDelete True");
                            }

                        } catch (JSONException e) {
                            EventBus.getDefault().post("NewsFeedDelete False");
                            e.printStackTrace();
                        }

                    }

                }
        );
    }


    public void reportInAppropriate(String phone_no, String user_token, String newsfeedId) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone_no);
            userInputDetails.put("user_token", user_token);
            userInputDetails.put("newsfeed_id", newsfeedId);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.REPORTINAPPROPRIATE, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        if (errorResponse != null) {
                            EventBus.getDefault().post("ReportInAppReporte False");
                        } else {
                            EventBus.getDefault().post("ReportInAppReporte Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {

                            state = response.getBoolean("success");
                            if (state) {

                                EventBus.getDefault().post("ReportInAppReporte True");
                            }

                        } catch (JSONException e) {
                            EventBus.getDefault().post("ReportInAppReporte False");
                            e.printStackTrace();
                        }

                    }

                }
        );
    }


    public void unStarredNewsFeed(String phone_no, String user_token, String newsfeedId) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone_no);
            userInputDetails.put("user_token", user_token);
            userInputDetails.put("newsfeed_id", newsfeedId);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.UNSTARRENDNEWSFEED, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        if (errorResponse != null) {
                            EventBus.getDefault().post("UnStarredNewsFeed False");
                        } else {
                            EventBus.getDefault().post("UnStarredNewsFeed Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {

                            state = response.getBoolean("success");
                            if (state) {

                                EventBus.getDefault().post("UnStarredNewsFeed True");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }


}
