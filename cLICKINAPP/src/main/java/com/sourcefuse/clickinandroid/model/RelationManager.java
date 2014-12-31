package com.sourcefuse.clickinandroid.model;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sourcefuse.clickinandroid.model.bean.FetchUsersByNameBean;
import com.sourcefuse.clickinandroid.model.bean.GetrelationshipsBean;
import com.sourcefuse.clickinandroid.model.bean.ProfileRelationShipBean;
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

public class RelationManager {
    private static final String TAG = RelationManager.class.getSimpleName();
    public ArrayList<ProfileRelationShipBean> usrRelationShipData = new ArrayList<ProfileRelationShipBean>();
    public ArrayList<ProfileRelationShipBean> profileRelationShipData = new ArrayList<ProfileRelationShipBean>();
    public ArrayList<GetrelationshipsBean> getrelationshipsData = new ArrayList<GetrelationshipsBean>();
    public ArrayList<FetchUsersByNameBean> fetchUsersByNameData = new ArrayList<FetchUsersByNameBean>();
    public ArrayList<GetrelationshipsBean> acceptedList = new ArrayList<GetrelationshipsBean>();
    public ArrayList<GetrelationshipsBean> initiatorList = new ArrayList<GetrelationshipsBean>();
    public ArrayList<GetrelationshipsBean> requestedList = new ArrayList<GetrelationshipsBean>();
    public String getPartnerName = "";
    public String partnerClicks = null;
    StringEntity se = null;
    AsyncHttpClient client;
    String strService = null;
    private RelationManager relationManager;
    // getrelation data
    private GetrelationshipsBean getRelationShipList = null;
    private ArrayList<GetrelationshipsBean> getRelationShipArray = null;
    // fetch profile relationship data
    private ProfileRelationShipBean ProfileRelationShipList = null;
    private ArrayList<ProfileRelationShipBean> ProfileRelationShipArray = null;
    private ArrayList<GetrelationshipsBean> clickWithData = new ArrayList<GetrelationshipsBean>();
    private ArrayList<GetrelationshipsBean> clickRequestedData = new ArrayList<GetrelationshipsBean>();
    private ArrayList<FetchUsersByNameBean> fetchUsersByNameArray = null;
    private FetchUsersByNameBean FetchUsersByNameList = null;
    private String followerListCount = "0";
    private String followingListCount = "0";
    private String followerOListCount = "0";
    private String followingOListCount = "0";
    private String relationStatus;
    private String statusMsg;
    private String successstatus;

    public void getRelationShips(String phone, String usertoken) {
        // TODO Auto-generated method stub
        relationManager = ModelManager.getInstance().getRelationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone);
            userInputDetails.put("user_token", usertoken);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            android.util.Log.e("UsrInput-For-GETRELATIONSHIPS-> ", "" + userInputDetails);
            strService = APIs.GETRELATIONSHIPS;

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, strService, se, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                super.onFailure(statusCode, e, errorResponse);
                android.util.Log.e(TAG, "errorResponse--> " + errorResponse);
                if (errorResponse != null) {
                    getrelationshipsData.clear();
                    acceptedList.clear();
                    initiatorList.clear();
                    requestedList.clear();
                    EventBus.getDefault().post("GetRelationShips False");
                } else {
                    EventBus.getDefault().post("GetRelationShips Network Error");
                }
            }

            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                boolean state = false;
                try {
                    android.util.Log.e(TAG, "response--> " + response);
                    state = response.getBoolean("success");
                    if (state) {
                        getrelationshipsData.clear();
                        acceptedList.clear();
                        initiatorList.clear();
                        requestedList.clear();
                        try {
                            relationManager.setFollowerListCount(response.getString("follower"));
                            relationManager.setFollowingListCount(response.getString("following"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        JSONArray list = response.getJSONArray("relationships");
                        getRelationShipArray = new ArrayList<GetrelationshipsBean>();
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject data = list.getJSONObject(i);
                            getRelationShipList = new GetrelationshipsBean();

                            if (!Utils.isEmptyString(data.getString("partner_QB_id"))) {
                                if (data.getString("accepted").matches("true")) {
                                    setArrayListForDifferentStatus(data, acceptedList);
                                } else {
                                    setArrayListForDifferentStatus(data, requestedList);
                                }
                            }
                        }
                        getrelationshipsData.addAll(requestedList);
                        getrelationshipsData.addAll(acceptedList);
                        //getrelationshipsData.addAll(initiatorList);
                        EventBus.getDefault().post("GetRelationShips True");
                    } else {
                        getrelationshipsData.clear();
                        acceptedList.clear();
                        initiatorList.clear();
                        requestedList.clear();
                        EventBus.getDefault().post("GetRelationShips False");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
    }

    private void setArrayListForDifferentStatus(JSONObject jsondata, ArrayList<GetrelationshipsBean> listdata) {
        getRelationShipList = new GetrelationshipsBean();
        try {
            JSONObject rId = jsondata.getJSONObject("id");
            getRelationShipList.setRelationshipId(rId.getString("$id"));
            if (jsondata.has("partner_QB_id"))
                getRelationShipList.setPartnerQBId(jsondata.getString("partner_QB_id"));
            if (jsondata.has("partner_id"))
                getRelationShipList.setPartner_id(jsondata.getString("partner_id"));
            if (jsondata.has("accepted"))
                getRelationShipList.setStatusAccepted(jsondata.getString("accepted"));
            if (jsondata.has("user_clicks"))
                getRelationShipList.setUserClicks(jsondata.getString("user_clicks"));
            if (jsondata.has("clicks"))
                getRelationShipList.setClicks(jsondata.getString("clicks"));
            if (jsondata.has("phone_no"))
                getRelationShipList.setPhoneNo(jsondata.getString("phone_no"));
            if (jsondata.has("partner_pic"))
                if (jsondata.getString("partner_pic").contains("profile_pic")) {
                    String pthumbImage = jsondata.getString("partner_pic").replace("profile_pic", "thumb_profile_pic");
                    getRelationShipList.setPartnerPic(pthumbImage);
                } else {
                    getRelationShipList.setPartnerPic("");
                }
            if (jsondata.has("request_initiator"))
                getRelationShipList.setRequestInitiator(jsondata.getString("request_initiator"));
            else
                getRelationShipList.setRequestInitiator("false");
            if (jsondata.has("public")) {
                getRelationShipList.setmStatuspublic(jsondata.getString("public"));
            }
            if (jsondata.has("partner_name"))
                getRelationShipList.setPartnerName(jsondata.getString("partner_name"));
            if (jsondata.has("last_seen_time")) {
                try {
                    JSONObject mLastSeenTime = jsondata.getJSONObject("last_seen_time");
                    if (mLastSeenTime.has("sec"))
                        getRelationShipList.mLastSeenTime = mLastSeenTime.getString("sec");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            listdata.add(getRelationShipList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getRelationStatus() {
        return relationStatus;
    }

    public void setRelationStatus(String relationStatus) {
        this.relationStatus = relationStatus;
    }

    public void fetchprofilerelationships(String othersPhone, String phone,
                                          String usertoken) {
        // TODO Auto-generated method stub
        relationManager = ModelManager.getInstance().getRelationManager();
//		JSONObject userInputDetails = new JSONObject();
        try {
//			userInputDetails.put("phone_no", phone);
//			userInputDetails.put("user_token", usertoken);

            client = new AsyncHttpClient();
//			client.addHeader("user_token", usertoken);
//			client.addHeader("phone_no", phone);
            client.addHeader("User-Token", usertoken);
            client.addHeader("Phone-No", phone);
//			android.util.Log.e("INPUT_DATA-> ", "" + userInputDetails);
            strService = APIs.FETCHPROFILERELATIONSHIPS
                    + othersPhone.substring(1);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.get(strService, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Throwable e,
                                  JSONObject errorResponse) {
                super.onFailure(statusCode, e, errorResponse);
                System.out.println("errorResponse--> " + errorResponse);
                if (errorResponse != null) {
                    EventBus.getDefault().post("Fetchprofilerelationships False");
                } else {
                    EventBus.getDefault().post("Fetchprofilerelationships Network Error");
                }
            }

            @Override
            public void onSuccess(int statusCode,
                                  org.apache.http.Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                boolean state = false;
                try {
                    System.out.println("response--> " + response);
                    state = response.getBoolean("success");
                    if (state) {
                        profileRelationShipData.clear();
                        JSONArray list = response.getJSONArray("relationships");

                        if (response.has("relation_status"))
                            relationManager.setRelationStatus(response.getString("relation_status"));

                        if (response.has("follower"))
                            relationManager.setOFollowerListCount(response.getString("follower"));
                        if (response.has("following"))
                            relationManager.setOFollowingListCount(response.getString("following"));

                        ProfileRelationShipArray = new ArrayList<ProfileRelationShipBean>();
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject data = list.getJSONObject(i);
                            JSONObject rId = data.getJSONObject("id");

                            ProfileRelationShipList = new ProfileRelationShipBean();
                            if (!Utils.isEmptyString(data.getString("partner_QB_id"))) {
                                ProfileRelationShipList.setRelationshipId(rId.getString("$id"));
                                ProfileRelationShipList.setPartnerQBId(data.getString("partner_QB_id"));
                                if (data.has("partner_id"))
                                    ProfileRelationShipList.setPartner_id(data.getString("partner_id"));
                                if (data.has("accepted"))
                                    ProfileRelationShipList.setStatusAccepted(data.getString("accepted"));
                                if (data.has("user_clicks"))
                                    ProfileRelationShipList.setUserClicks(data.getString("user_clicks"));
                                if (data.has("clicks"))
                                    ProfileRelationShipList.setClicks(data.getString("clicks"));
                                if (data.has("phone_no"))
                                    ProfileRelationShipList.setPhoneNo(data.getString("phone_no"));
                                if (data.has("partner_pic"))
                                    ProfileRelationShipList.setPartnerPic(data.getString("partner_pic"));
                                if (data.has("public"))
                                    ProfileRelationShipList.setmStatuspublic(data.getString("public"));
                                if (data.has("partner_name"))
                                    ProfileRelationShipList.setPartnerName(data.getString("partner_name"));
                                ProfileRelationShipArray.add(ProfileRelationShipList);
                            }
                        }
                        profileRelationShipData.addAll(ProfileRelationShipArray);
                        EventBus.getDefault().post("Fetchprofilerelationships True");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
    }

    public void changeUserVisibility(String relationshipIid, String mode,
                                     String phone, String usertoken) {
        // TODO Auto-generated method stub
        relationManager = ModelManager.getInstance().getRelationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone);
            userInputDetails.put("user_token", usertoken);
            userInputDetails.put("relationship_id", relationshipIid);
            userInputDetails.put("public", mode);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            android.util.Log.e("change user visibility android-------> ", "" + userInputDetails);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.CHANGEVISIBILITY, se, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                super.onFailure(statusCode, e, errorResponse);
                android.util.Log.e("errorResponse on change user visibility-------> ", "" + errorResponse);
                if (errorResponse != null) {
                    EventBus.getDefault().post("UserVisible true on error");
                } else {
                    EventBus.getDefault().post("UserVisible Network Error");
                }
            }

            @Override
            public void onSuccess(int statusCode,
                                  org.apache.http.Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                boolean state = false;
                try {
                    System.out.println("response--> " + response);
                    state = response.getBoolean("success");
                    if (state) {
                        EventBus.getDefault().post("UserVisible true");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
    }

    public void followUser(String followeePhoneNo, String phone,
                           String usertoken) {
        relationManager = ModelManager.getInstance().getRelationManager();
        // TODO Auto-generated method stub
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone);
            userInputDetails.put("user_token", usertoken);
            userInputDetails.put("followee_phone_no", followeePhoneNo);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            android.util.Log.e("suserInputDetailse-FollowUser-> ", "" + userInputDetails);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.FOLLOWUSER, se, "application/json",
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Throwable e,
                                          JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        try {
                            System.out.println("errorResponse--> " + errorResponse);
                            if (errorResponse != null) {
                                relationManager.setStatusMsg(errorResponse.getString("message"));
                                EventBus.getDefault().post("FollowUser false");
                            } else {
                                EventBus.getDefault().post("FollowUser Network Error");
                            }
                        } catch (Exception ex) {
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode,
                                          org.apache.http.Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {
                            System.out.println("FOLLOWUSER--> " + response);
                            state = response.getBoolean("success");
                            if (state) {
                                relationManager.setStatusMsg(response.getString("message"));
                                EventBus.getDefault().post("FollowUser true");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }

    public void unFollowUser(String followId, String followingMode, String phone,
                             String usertoken) {
        // TODO Auto-generated method stub
        relationManager = ModelManager.getInstance().getRelationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone);
            userInputDetails.put("user_token", usertoken);
            userInputDetails.put("follow_id", followId);
            userInputDetails.put("following", followingMode);
            //  userInputDetails.put("accepted", accepted);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                    "application/json"));
            android.util.Log.e("suserInputDetailse-UnFollowUser-> ", ""
                    + userInputDetails);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.UNFOLLOWUSER, se, "application/json",
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Throwable e,
                                          JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        try {
                            System.out.println("errorResponse--> " + errorResponse);
                            if (errorResponse != null) {
                                relationManager.setStatusMsg(errorResponse.getString("message"));
                                EventBus.getDefault().post("UnFollowUser  false");
                            } else {
                                EventBus.getDefault().post("UnFollowUser Network Errer");
                            }
                        } catch (Exception ex) {
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode,
                                          org.apache.http.Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {
                            System.out.println("UnFollowUser--> " + response);
                            state = response.getBoolean("success");
                            if (state) {
                                relationManager.setStatusMsg(response.getString("message"));
                                EventBus.getDefault().post("UnFollowUser true");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    public void updateStatus(String relationshipIid, String phone,
                             String usertoken, String mode) {
        relationManager = ModelManager.getInstance().getRelationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone);
            userInputDetails.put("user_token", usertoken);
            userInputDetails.put("relationship_id", relationshipIid);
            userInputDetails.put("accepted", mode);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            android.util.Log.e("suserInputDetailse-UPDATESTATUS-> ", "" + userInputDetails);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.UPDATESTATUS, se, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                super.onFailure(statusCode, e, errorResponse);
                android.util.Log.e("UPDATESTATUS", "errorResponse--> " + errorResponse);
                if (errorResponse != null) {
                    EventBus.getDefault().post("updateStatus false");

                } else {
                    EventBus.getDefault().post("updateStatus error");

                }
            }

            @Override
            public void onSuccess(int statusCode,
                                  org.apache.http.Header[] headers,
                                  JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                boolean state = false;
                try {
                    System.out.println("response--> " + response);
                    state = response.getBoolean("success");
                    if (state) {
                        EventBus.getDefault().post("updateStatus true");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });

    }

    public void deleteRelationship(String relationshipIid, String phone, String usertoken) {
        relationManager = ModelManager.getInstance().getRelationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone);
            userInputDetails.put("user_token", usertoken);
            userInputDetails.put("relationship_id", relationshipIid);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            android.util.Log.e("suserInputDetailse-DELETERELATIONSHIP-> ", "" + userInputDetails);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.DELETERELATIONSHIP, se, "application/json",
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Throwable e,
                                          JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        System.out.println("errorResponse--> " + errorResponse);
                        if (errorResponse != null) {

                            EventBus.getDefault().post("deleteRelationship false");
                        } else {
                            EventBus.getDefault().post("deleteRelationship error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode,
                                          org.apache.http.Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {
                            System.out.println("response--> " + response);
                            state = response.getBoolean("success");
                            if (state) {
                                EventBus.getDefault().post("deleteRelationship true");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );

    }

    public void fetchusersbyname(String name, String phone, String usertoken) {
        relationManager = ModelManager.getInstance().getRelationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone);
            userInputDetails.put("user_token", usertoken);
            userInputDetails.put("name", name);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());


            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            android.util.Log.e("suserInputDetailse-fetchusersbyname-> ", "" + userInputDetails);
        } catch (Exception e1) {
        }
        client.post(null, APIs.FETCHUSERSBYNAME, se, "application/json",
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        System.out.println("errorResponse--> " + errorResponse);
                        if (errorResponse != null) {


                        } else {

                        }
                        try {
                            fetchUsersByNameData.clear();
                        } catch (Exception e1) {

                        }
                        EventBus.getDefault().post("SearchResult False");
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {
                            System.out.println("response--> " + response);
                            state = response.getBoolean("success");
                            fetchUsersByNameData.clear();
                            if (state) {
                                JSONArray list = response.getJSONArray("users");
                                fetchUsersByNameArray = new ArrayList<FetchUsersByNameBean>();
                                for (int i = 0; i < list.length(); i++) {
                                    FetchUsersByNameList = new FetchUsersByNameBean();
                                    JSONObject data = list.getJSONObject(i);
                                    // JSONObject rId = data.getJSONObject("id");
                                    // FetchUsersByNameList.setrId(rId.getString("$id"));
                                    if (data.has("name"))
                                        FetchUsersByNameList.setName(data.getString("name"));
                                    // FetchUsersByNameList.setqBId(data.getString("QB_id"));
                                    if (data.has("phone_no"))
                                        FetchUsersByNameList.setPhoneNo(data.getString("phone_no"));
                                    if (data.has("user_pic"))
                                        FetchUsersByNameList.setUserPic(data.getString("user_pic"));
                                    if (data.has("city"))
                                        FetchUsersByNameList.setCity(data.getString("city"));
                                    if (data.has("country"))
                                        FetchUsersByNameList.setCountry(data.getString("country"));

                                    // FetchUsersByNameList.setUserToken(data.getString("user_token"));
                                    fetchUsersByNameArray.add(FetchUsersByNameList);
                                }
                                fetchUsersByNameData.addAll(fetchUsersByNameArray);
                                EventBus.getDefault().post("SearchResult true");
                                //android.util.android.util.Log.e("in post ---->", "in post---->");
                            } else {
                                EventBus.getDefault().post("SearchResult False");
                            }
                        } catch (Exception e) {
                            // android.util.android.util.Log.e("on error ", "on error");
                            EventBus.getDefault().post("SearchResult False");
                            e.printStackTrace();
                        }

                    }

                }
        );
    }

    public void followupdatestatus(String followId, String accepted, String phone, String usertoken) {
        relationManager = ModelManager.getInstance().getRelationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone);
            userInputDetails.put("user_token", usertoken);
            userInputDetails.put("accepted", accepted);
            userInputDetails.put("follow_id", followId);
            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            android.util.Log.e("suserInputDetailse-followupdatestatus-> ", "" + userInputDetails);
        } catch (Exception e1) {
        }
        client.post(null, APIs.FOLLOWUPDATESTATUS, se, "application/json",
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        System.out.println("errorResponse--> " + errorResponse);
                        if (errorResponse != null) {

                            EventBus.getDefault().post("followUpdateStatus false");
                        } else {
                            EventBus.getDefault().post("followUpdateStatus error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {
                            System.out.println("response--> " + response);
                            state = response.getBoolean("success");
                            if (state) {
                                EventBus.getDefault().post("followUpdateStatus true");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }

    /**
     * @return the statusMsg
     */
    public String getStatusMsg() {
        return statusMsg;
    }

    /**
     * @param statusMsg the statusMsg to set
     */
    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    /**
     * @return the successstatus
     */
    public String getSuccessstatus() {
        return successstatus;
    }

    /**
     * @param successstatus the successstatus to set
     */
    public void setSuccessstatus(String successstatus) {
        this.successstatus = successstatus;
    }

    public String getFollowerListCount() {
        return followerListCount;
    }

    public void setFollowerListCount(String followerListCount) {
        this.followerListCount = followerListCount;
    }

    public String getFollowingListCount() {
        return followingListCount;
    }

    public void setFollowingListCount(String followingListCount) {
        this.followingListCount = followingListCount;
    }


    public String getOFollowerListCount() {
        return followerOListCount;
    }

    public void setOFollowerListCount(String followerOListCount) {
        this.followerOListCount = followerOListCount;
    }

    public String getOFollowingListCount() {
        return followingOListCount;
    }

    public void setOFollowingListCount(String followingOListCount) {
        this.followingOListCount = followingOListCount;
    }


}
