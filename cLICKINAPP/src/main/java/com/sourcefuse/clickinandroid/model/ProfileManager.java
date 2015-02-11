package com.sourcefuse.clickinandroid.model;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sourcefuse.clickinandroid.model.bean.ContactBean;
import com.sourcefuse.clickinandroid.model.bean.CurrentClickerBean;
import com.sourcefuse.clickinandroid.model.bean.FollowerFollowingBean;
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

public class ProfileManager {

    public static ArrayList<FollowerFollowingBean> following = new ArrayList<FollowerFollowingBean>();
    public static ArrayList<FollowerFollowingBean> followers = new ArrayList<FollowerFollowingBean>();
    /* for other profile */
    public static ArrayList<FollowerFollowingBean> following_other = new ArrayList<FollowerFollowingBean>();
    public static ArrayList<FollowerFollowingBean> followers_other = new ArrayList<FollowerFollowingBean>();
    public ArrayList<FollowerFollowingBean> followRequesed = new ArrayList<FollowerFollowingBean>();
    public ArrayList<FollowerFollowingBean> pfollowerList = new ArrayList<FollowerFollowingBean>();
    public ArrayList<FollowerFollowingBean> Replacement = new ArrayList<FollowerFollowingBean>();
    public ArrayList<CurrentClickerBean> currentClickerList = new ArrayList<CurrentClickerBean>();
    public ArrayList<ContactBean> spreadTheWorldList = new ArrayList<ContactBean>();
    public ArrayList<String> currClickersPhoneNums = new ArrayList<String>();
    public ArrayList<CurrentClickerBean> currentClickerListFB = new ArrayList<CurrentClickerBean>();
    public ArrayList<FollowerFollowingBean> followRequesed_other = new ArrayList<FollowerFollowingBean>();
    public ArrayList<FollowerFollowingBean> pfollowerList_other = new ArrayList<FollowerFollowingBean>();
    public ArrayList<FollowerFollowingBean> Replacement_other = new ArrayList<FollowerFollowingBean>();
    private StringEntity se = null;
    private AsyncHttpClient client;
    private AuthManager authManager;
    private FollowerFollowingBean followingList = null;
    private ArrayList<FollowerFollowingBean> followingArray = null;
    private FollowerFollowingBean followerList = null;
    private ArrayList<FollowerFollowingBean> followerArray = null;
    private FollowerFollowingBean followerList_other = null;
    private FollowerFollowingBean followingList_other = null;
    private ArrayList<FollowerFollowingBean> followingArray_other = null;


    public void setProfile(String fname, String lname, String phone,
                           String usertoken, String gender, String dob, String city,
                           String country, String email, String fbaccesstoken, String userpic, String profile_image_change) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {

            userInputDetails.put("phone_no", phone);
            userInputDetails.put("user_token", usertoken);

            //akshit becoz was previously returning null
            if (!Utils.isEmptyString(gender)) {
                userInputDetails.put("gender", gender);
            } else {
                userInputDetails.put("gender", "");

            }//ends
            userInputDetails.put("dob", dob);
            if (!Utils.isEmptyString(userpic)) {
                userInputDetails.put("user_pic", userpic);
            } else {
                userInputDetails.put("user_pic", "");
            }
            userInputDetails.put("city", city);
            userInputDetails.put("country", country);
            userInputDetails.put("email", email);
            userInputDetails.put("first_name", fname);
            userInputDetails.put("last_name", lname);
            userInputDetails.put("profile_image_change", profile_image_change);
            // userInputDetails.put("fb_access_token", "jh");

            ModelManager.getInstance().getAuthorizationManager().getMixpanelAPI().registerSuperProperties(userInputDetails);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                    "application/json"));
        } catch (Exception e1) {
            e1.printStackTrace();

        }
        client.post(null, APIs.UPDATEPROFILE, se, "application/json",
                new JsonHttpResponseHandler() {
                    boolean success = false;

                    @Override
                    public void onFailure(int statusCode, Throwable e,
                                          JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);

                        if (errorResponse != null) {
                            try {
                                authManager.setMessage(errorResponse.getString("message"));
                            } catch (JSONException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();

                            }
                            EventBus.getDefault().post("UpdateProfile False");
                        } else {
                            EventBus.getDefault().post("UpdateProfile Network Error");
                        }

                    }

                    @Override
                    public void onSuccess(int statusCode,
                                          org.apache.http.Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {

                            success = response.getBoolean("success");
                            if (success) {

                                // authManager =
                                // ModelManager.getInstance().getAuthorizationManager();
                                // authManager.setUserName(response.getString("user_name"));
                                // authManager.setUserPic(response.getString("user_pic"));

                                EventBus.getDefault().post("UpdateProfile True");

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }

                }
        );

    }

    public void getFollwer(String othersPhone, String phone, String usertoken) {
        // TODO Auto-generated method stub
        authManager = ModelManager.getInstance().getAuthorizationManager();
        try {
            client = new AsyncHttpClient();
            //client.addHeader("user-token", usertoken);
            //client.addHeader("phone-no", phone);

            client.addHeader("User-Token", usertoken);
            client.addHeader("Phone-No", phone);
        } catch (Exception e1) {
            e1.printStackTrace();

        }

        String str = null;
        if (!Utils.isEmptyString(othersPhone)) {
            str = othersPhone.substring(1);
        } else {
            str = phone.substring(1);
        }

        client.get(APIs.GETUSERFOLLOWER + ":%2B" + str, new JsonHttpResponseHandler() {
            boolean success = false;

            @Override
            public void onFailure(int statusCode, Throwable e,
                                  JSONObject errorResponse) {
                super.onFailure(statusCode, e, errorResponse);

                if (errorResponse != null) {
                    try {
                        authManager.setMessage(errorResponse.getString("message"));
                    } catch (JSONException e1) {

                    }
                    EventBus.getDefault().post("GetFollower False");
                } else {
                    EventBus.getDefault().post("GetFollower Network Error");
                }

            }

            @Override
            public void onSuccess(int statusCode,
                                  org.apache.http.Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {

                    success = response.getBoolean("success");
                    if (success) {
                        following.clear();
                        followers.clear();
                        followRequesed.clear();
                        pfollowerList.clear();
                        JSONArray list = response.getJSONArray("following");
                        followingArray = new ArrayList<FollowerFollowingBean>();
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject data = list.getJSONObject(i);
                            followingList = new FollowerFollowingBean();

                            if (!Utils.isEmptyString(data.getString("accepted"))) {
                                followingList.setAccepted(data.getString("accepted"));

                                JSONObject rId = data.getJSONObject("_id");
                                followingList.setrFollowerId(rId.getString("$id"));
                                if (data.has("followee_id"))
                                    followingList.setFolloweeId(data.getString("followee_id"));
                                if (data.has("followee_name"))
                                    followingList.setFolloweeName(data.getString("followee_name"));
                                try {
                                    if (data.has("followee_pic"))
                                        if (data.getString("followee_pic").contains("profile_pic")) {

                                            String mPic = new String(data.getString("followee_pic"));
                                            String pthumbImage = mPic.replace("profile_pic", "thumb_profile_pic");
                                            followingList.setFolloweePic(pthumbImage);
                                        } else {
                                            followingList.setFolloweePic("");
                                        }
                                } catch (Exception e) {
                                    e.printStackTrace();

                                }
                                followingList.setFolloweePic(data.getString("followee_pic"));
                                if (data.has("phone_no"))
                                    followingList.setPhoneNo(data.getString("phone_no"));
                                followingList.setIsFollowing("false");
                                /*data.has("following_id");
                                followingList.setFollowingId(data.getString("following_id"));*/
                                followingArray.add(followingList);
                            } else {
                                followingList.setAccepted("");
                                JSONObject rId = data.getJSONObject("_id");
                                followingList.setrFollowerId(rId.getString("$id"));
                                if (data.has("followee_id"))
                                    followingList.setFolloweeId(data.getString("followee_id"));
                                if (data.has("followee_name"))
                                    followingList.setFolloweeName(data.getString("followee_name"));
                                try {
                                    if (data.has("followee_pic"))
                                        if (data.getString("followee_pic").contains("profile_pic")) {

                                            String following_pic = new String(data.getString("followee_pic"));
                                            String pthumbImage = following_pic.replace("profile_pic", "thumb_profile_pic");
                                            followingList.setFolloweePic(pthumbImage);
                                        } else {
                                            followingList.setFolloweePic("");
                                        }
                                } catch (Exception e) {
                                    e.printStackTrace();

                                }
                                if (data.has("phone_no"))
                                    followingList.setPhoneNo(data.getString("phone_no"));
                                followingList.setIsFollowing("false");
                                /*data.has("following_id");
                                followingList.setFollowingId(data.getString("following_id"));*/
                                followingArray.add(followingList);
                            }
                        }
                        JSONArray followerData = response.getJSONArray("follower");
                        //followerArray = new ArrayList<FollowerFollowingBean>();
                        for (int i = 0; i < followerData.length(); i++) {

                            JSONObject data = followerData.getJSONObject(i);
                            if (!data.getString("accepted").matches("false")) {
                                followerList = new FollowerFollowingBean();
                                if (!Utils.isEmptyString(data.getString("accepted"))) {
                                    followerList.setAccepted(data.getString("accepted"));
                                    JSONObject rId = data.getJSONObject("_id");
                                    followerList.setrFollowerId(rId.getString("$id"));
                                    if (data.has("follower_id"))
                                        followerList.setFolloweeId(data.getString("follower_id"));
                                    if (data.has("follower_name"))
                                        followerList.setFolloweeName(data.getString("follower_name"));
                                    if (data.has("follower_pic"))

                                        try {
                                            if (data.getString("follower_pic").contains("profile_pic")) {

                                                String mPic = new String(data.getString("follower_pic"));
                                                String pthumbImage = mPic.replace("profile_pic", "thumb_profile_pic");
                                                followerList.setFolloweePic(pthumbImage);
                                            } else {
                                                followerList.setFolloweePic("");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();

                                        }
                                    if (data.has("phone_no"))
                                        followerList.setPhoneNo(data.getString("phone_no"));

                                    if (data.has("following_id")) {
                                        followerList.setFollowingId(data.getString("following_id"));
                                    }

                                    if (data.has("is_following")) {
                                        if (!Utils.isEmptyString(data.getString("is_following"))) {
                                            followerList.setIsFollowing(data.getString("is_following"));
                                        } else {
                                            followerList.setIsFollowing("false");
                                        }
                                    } else {
                                        followerList.setIsFollowing("false");
                                    }
                                    if (data.has("following_accepted")) {
                                        if (!Utils.isEmptyString(data.getString("following_accepted"))) {
                                            followerList.setFollowingAccepted(data.getString("following_accepted"));
                                        } else {
                                            followerList.setFollowingAccepted("false");
                                        }
                                    } else {
                                        followerList.setFollowingAccepted("false");
                                    }
                                    pfollowerList.add(followerList);
                                } else {
                                    followerList.setAccepted("");
                                    JSONObject rId = data.getJSONObject("_id");
                                    followerList.setrFollowerId(rId.getString("$id"));
                                    if (data.has("follower_id"))
                                        followerList.setFolloweeId(data.getString("follower_id"));
                                    if (data.has("follower_name"))
                                        followerList.setFolloweeName(data.getString("follower_name"));
                                    try {
                                        if (data.has("follower_pic"))
                                            if (data.getString("follower_pic").contains("profile_pic")) {

                                                String mPic = new String(data.getString("follower_pic"));
                                                String pthumbImage = mPic.replace("profile_pic", "thumb_profile_pic");
                                                followerList.setFolloweePic(pthumbImage);
                                            } else {
                                                followerList.setFolloweePic("");
                                            }
                                    } catch (Exception e) {
                                        e.printStackTrace();

                                    }
                                    if (data.has("phone_no"))
                                        followerList.setPhoneNo(data.getString("phone_no"));

                                    if (data.has("following_id")) {
                                        followerList.setFollowingId(data.getString("following_id"));
                                    }
                                    if (data.has("is_following")) {
                                        if (!Utils.isEmptyString(data.getString("is_following"))) {
                                            followerList.setIsFollowing(data.getString("is_following"));
                                        } else {
                                            followerList.setIsFollowing("false");
                                        }
                                    } else {
                                        followerList.setIsFollowing("false");
                                    }
                                    if (data.has("following_accepted")) {
                                        if (!Utils.isEmptyString(data.getString("following_accepted"))) {
                                            followerList.setFollowingAccepted(data.getString("following_accepted"));
                                        } else {
                                            followerList.setFollowingAccepted("false");
                                        }
                                    } else {
                                        followerList.setFollowingAccepted("false");
                                    }
                                    // followerArray.add(followerList);
                                    followRequesed.add(followerList);

                                }
                            }
                        }
                        //followerArray.add(followerList);
                        followers.addAll(followRequesed);
                        followers.addAll(pfollowerList);
                        following.addAll(followingArray);

                        EventBus.getDefault().postSticky("GetFollower True");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        });
    }
/* jump to other profile view*/


    public void getFollwerOther(String othersPhone, String phone, String usertoken) {
        // TODO Auto-generated method stub
        authManager = ModelManager.getInstance().getAuthorizationManager();
        try {
            client = new AsyncHttpClient();
            //client.addHeader("user-token", usertoken);
            //client.addHeader("phone-no", phone);

            client.addHeader("User-Token", usertoken);
            client.addHeader("Phone-No", phone);
        } catch (Exception e1) {
            e1.printStackTrace();

        }

        String str = null;
        if (!Utils.isEmptyString(othersPhone)) {
            str = othersPhone.substring(1);
        } else {
            str = phone.substring(1);
        }

        client.get(APIs.GETUSERFOLLOWER + ":%2B" + str, new JsonHttpResponseHandler() {
            boolean success = false;

            @Override
            public void onFailure(int statusCode, Throwable e,
                                  JSONObject errorResponse) {
                super.onFailure(statusCode, e, errorResponse);

                if (errorResponse != null) {
                    try {
                        authManager.setMessage(errorResponse.getString("message"));
                    } catch (JSONException e1) {


                    }
                    EventBus.getDefault().post("GetFollower False");
                } else {
                    EventBus.getDefault().post("GetFollower Network Error");
                }

            }

            @Override
            public void onSuccess(int statusCode,
                                  org.apache.http.Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {

                    success = response.getBoolean("success");
                    if (success) {
                        following_other.clear();
                        followers_other.clear();
                        followRequesed_other.clear();
                        pfollowerList_other.clear();
                        JSONArray list = response.getJSONArray("following");
                        followingArray_other = new ArrayList<FollowerFollowingBean>();
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject data = list.getJSONObject(i);
                            followingList_other = new FollowerFollowingBean();
                            if (!Utils.isEmptyString(data.getString("accepted"))) {
                                followingList_other.setAccepted(data.getString("accepted"));
                                JSONObject rId = data.getJSONObject("_id");
                                followingList_other.setrFollowerId(rId.getString("$id"));
                                data.has("followee_id");
                                followingList_other.setFolloweeId(data.getString("followee_id"));
                                data.has("followee_name");
                                followingList_other.setFolloweeName(data.getString("followee_name"));
                                data.has("followee_pic");
                                followingList_other.setFolloweePic(data.getString("followee_pic"));
                                data.has("phone_no");
                                followingList_other.setPhoneNo(data.getString("phone_no"));
                                followingList_other.setIsFollowing("false");
                                followingArray_other.add(followingList_other);
                            } else {
                                followingList_other.setAccepted("");
                                JSONObject rId = data.getJSONObject("_id");
                                followingList_other.setrFollowerId(rId.getString("$id"));
                                data.has("followee_id");
                                followingList_other.setFolloweeId(data.getString("followee_id"));
                                data.has("followee_name");
                                followingList_other.setFolloweeName(data.getString("followee_name"));
                                data.has("followee_pic");
                                followingList_other.setFolloweePic(data.getString("followee_pic"));
                                data.has("phone_no");
                                followingList_other.setPhoneNo(data.getString("phone_no"));
                                followingList_other.setIsFollowing("false");
                                /*data.has("following_id");
                                followingList.setFollowingId(data.getString("following_id"));*/
                                followingArray_other.add(followingList_other);
                            }
                        }
                        JSONArray followerData = response.getJSONArray("follower");
                        //followerArray = new ArrayList<FollowerFollowingBean>();
                        for (int i = 0; i < followerData.length(); i++) {

                            JSONObject data = followerData.getJSONObject(i);
                            if (!data.getString("accepted").matches("false")) {
                                followerList_other = new FollowerFollowingBean();
                                if (!Utils.isEmptyString(data.getString("accepted"))) {
                                    followerList_other.setAccepted(data.getString("accepted"));
                                    JSONObject rId = data.getJSONObject("_id");
                                    followerList_other.setrFollowerId(rId.getString("$id"));
                                    if (data.has("follower_id"))
                                        followerList_other.setFolloweeId(data.getString("follower_id"));
                                    data.has("follower_name");
                                    followerList_other.setFolloweeName(data.getString("follower_name"));
                                    data.has("follower_pic");
                                    followerList_other.setFolloweePic(data.getString("follower_pic"));
                                    data.has("phone_no");
                                    followerList_other.setPhoneNo(data.getString("phone_no"));

                                    if (data.has("following_id")) {
                                        followerList_other.setFollowingId(data.getString("following_id"));
                                    }

                                    if (data.has("is_following")) {
                                        if (!Utils.isEmptyString(data.getString("is_following"))) {
                                            followerList_other.setIsFollowing(data.getString("is_following"));
                                        } else {
                                            followerList_other.setIsFollowing("false");
                                        }
                                    } else {
                                        followerList_other.setIsFollowing("false");
                                    }
                                    if (data.has("following_accepted")) {
                                        if (!Utils.isEmptyString(data.getString("following_accepted"))) {
                                            followerList_other.setFollowingAccepted(data.getString("following_accepted"));
                                        } else {
                                            followerList_other.setFollowingAccepted("false");
                                        }
                                    } else {
                                        followerList_other.setFollowingAccepted("false");
                                    }
                                    pfollowerList_other.add(followerList_other);
                                } else {
                                    followerList_other.setAccepted("");
                                    JSONObject rId = data.getJSONObject("_id");
                                    followerList_other.setrFollowerId(rId.getString("$id"));
                                    if (data.has("follower_id"))
                                        followerList_other.setFolloweeId(data.getString("follower_id"));
                                    data.has("follower_name");
                                    followerList_other.setFolloweeName(data.getString("follower_name"));
                                    data.has("follower_pic");
                                    followerList_other.setFolloweePic(data.getString("follower_pic"));
                                    data.has("phone_no");
                                    followerList_other.setPhoneNo(data.getString("phone_no"));

                                    if (data.has("following_id")) {
                                        followerList_other.setFollowingId(data.getString("following_id"));
                                    }
                                    if (data.has("is_following")) {
                                        if (!Utils.isEmptyString(data.getString("is_following"))) {
                                            followerList_other.setIsFollowing(data.getString("is_following"));
                                        } else {
                                            followerList_other.setIsFollowing("false");
                                        }
                                    } else {
                                        followerList_other.setIsFollowing("false");
                                    }
                                    if (data.has("following_accepted")) {
                                        if (!Utils.isEmptyString(data.getString("following_accepted"))) {
                                            followerList_other.setFollowingAccepted(data.getString("following_accepted"));
                                        } else {
                                            followerList_other.setFollowingAccepted("false");
                                        }
                                    } else {
                                        followerList_other.setFollowingAccepted("false");
                                    }
                                    // followerArray.add(followerList);
                                    followRequesed_other.add(followerList_other);

                                }
                            }
                        }
                        //followerArray.add(followerList);
                        followers_other.addAll(followRequesed_other);
                        followers_other.addAll(pfollowerList_other);
                        following_other.addAll(followingArray_other);
                        EventBus.getDefault().post("GetFollower True");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        });
    }


    /* jump to other profile view*/
    public void getOthersProfile(String phone, String usertoken, String partner_phone) {

        // TODO Auto-generated method stub
        authManager = ModelManager.getInstance().getAuthorizationManager();
        try {
            client = new AsyncHttpClient();
            //client.addHeader("user_token", usertoken);
            //client.addHeader("phone_no", phone);
            client.addHeader("User-Token", usertoken);
            client.addHeader("Phone-No", phone);

        } catch (Exception e1) {
            e1.printStackTrace();

        }

        String ss = APIs.FETCHOTHERPROFILEINFO + ":" + partner_phone;
        client.get(ss, new JsonHttpResponseHandler() {
            boolean success = false;

            @Override
            public void onFailure(int statusCode, Throwable e,
                                  JSONObject errorResponse) {
                super.onFailure(statusCode, e, errorResponse);

                if (errorResponse != null) {
                    try {
                        authManager.setMessage(errorResponse
                                .getString("message"));
                    } catch (JSONException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();

                    }
                    //triggerObservers("ProfileInfo False");
                } else {
                    //triggerObservers("ProfileInfo Network Error");
                }

            }

            @Override
            public void onSuccess(int statusCode,
                                  org.apache.http.Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {

                    success = response.getBoolean("success");
                    if (success) {
                        JSONObject jobj = new JSONObject(response.getString("user"));
//							authManager.setGender(jobj.getString("gender"));
//							authManager.setFollower(jobj.getString("follower"));
//							authManager.setFollowing(jobj.getString("following"));
//							authManager.setIsFollowing(jobj.getString("is_following"));
//							authManager.setUserName(jobj.getString("name"));
//							authManager.setUserPic(jobj.getString("user_pic"));
//							authManager.setdOB(jobj.getString("dob"));
                        // triggerObservers("ProfileInfo True");
                        getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }

        });

    }


    public void getRelationShips(String phone, String usertoken) {
        // TODO Auto-generated method stub
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone);
            userInputDetails.put("user_token", usertoken);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        } catch (Exception e1) {
            e1.printStackTrace();

        }
        client.post(null, APIs.GETRELATIONSHIPS, se, "application/json",
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Throwable e,
                                          JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);

                        if (errorResponse != null) {
//								try {
//									
//								} catch (JSONException e1) {
//									// TODO Auto-generated catch block
//									e1.printStackTrace();
//								}
                            //	triggerObservers("GetrelationShips False");
                        } else {
                            //	triggerObservers("GetrelationShips Network Error");
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

                                //	triggerObservers("GetrelationShips True");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }

                }
        );
    }


}
