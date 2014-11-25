package com.sourcefuse.clickinandroid.model;

import android.graphics.Bitmap;
import android.net.Uri;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import com.quickblox.module.chat.QBPrivateChat;
import com.sourcefuse.clickinandroid.utils.APIs;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;




//praful code
  /* resize bit map*/

public class AuthManager {
      StringEntity se = null;
      AsyncHttpClient client;
      private static final String TAG = AuthManager.class.getSimpleName();
      private AuthManager authManager;


      //values stored in Authmanager
      public Boolean deviceRegistered;
      private String message;
      private String phoneNo;
      private String QBId;
      private String usrToken;
      private String userId;
      private String userName;

      public String getDeviceRegistereId() {
            return deviceRegistereId;
      }

      public void setDeviceRegistereId(String deviceRegistereId) {
            this.deviceRegistereId = deviceRegistereId;
      }

      private String userPic;
      private String emailId;
      private String follower;

      public String getUserCity() {
            return userCity;
      }


      private String following;
      private String isFollowing;
      private String gender;
      private String dOB;
      private String deviceRegistereId;
      private String userCity;
      private String userCountry;
      private Uri userImageUri = null;
      private Bitmap userbitmap;


      private Bitmap mResizeBitmap;



      private Bitmap mOrginalBitmap;

      private String tmpCity;
      private String tmpCountry;
      private String tmpFollowId;
      private int tmpIsFollowingRequested;

      private boolean editProfileFlag = false;

      private String tmpQBId;
      private String tmpUserId;
      private String tmpUserName;
      private String tmpUserPic = "";
      private String tmpFollower = "0";
      private String tmpFollowing = "0";
      private int tmpIsFollowing;
      private String tmpGender = "";
      private String tmpDOB;

      public void setUserCity(String userCity) {
            this.userCity = userCity;
      }

      public String getUserCountry() {
            return userCountry;
      }

      public void setUserCountry(String userCountry) {
            this.userCountry = userCountry;
      }

      /**
       * @return the deviceRegistered
       */
      public Boolean getDeviceRegistered() {
            return deviceRegistered;
      }

      /**
       * @param deviceRegistered the deviceRegistered to set
       */
      public void setDeviceRegistered(Boolean deviceRegistered) {
            this.deviceRegistered = deviceRegistered;
      }

      /**
       * @return the message
       */
      public String getMessage() {
            return message;
      }

      /**
       * @param message the message to set
       */
      public void setMessage(String message) {
            this.message = message;
      }

      /**
       * @return the phoneNo
       */
      public String getPhoneNo() {
            return phoneNo;
      }

      /**
       * @param phoneNo the phoneNo to set
       */
      public void setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
      }

      /**
       * @return the qBId
       */
      public String getQBId() {
            return QBId;
      }

      /**
       * @param qBId the qBId to set
       */
      public void setQBId(String qBId) {
            QBId = qBId;
      }

      /**
       * @return the usrToken
       */
      public String getUsrToken() {
            return usrToken;
      }

      /**
       * @param usrToken the usrToken to set
       */
      public void setUsrToken(String usrToken) {
            this.usrToken = usrToken;
      }

      /**
       * @return the userId
       */
      public String getUserId() {
            return userId;
      }

      /**
       * @param userId the userId to set
       */
      public void setUserId(String userId) {
            this.userId = userId;
      }

      /**
       * @return the userName
       */
      public String getUserName() {
            return userName;
      }

      /**
       * @param userName the userName to set
       */
      public void setUserName(String userName) {
            this.userName = userName;
      }

      /**
       * @return the userPic
       */
      public String getUserPic() {
            return userPic;
      }

      /**
       * @param userPic the userPic to set
       */
      public void setUserPic(String userPic) {
            this.userPic = userPic;
      }

      /**
       * @return the emailId
       */
      public String getEmailId() {
            return emailId;
      }

      /**
       * @param emailId the emailId to set
       */
      public void setEmailId(String emailId) {
            this.emailId = emailId;
      }

      /**
       * @return the follower
       */
      public String getFollower() {
            return follower;
      }

      /**
       * @param follower the follower to set
       */
      public void setFollower(String follower) {
            this.follower = follower;
      }

      /**
       * @return the following
       */
      public String getFollowing() {
            return following;
      }

      /**
       * @param following the following to set
       */
      public void setFollowing(String following) {
            this.following = following;
      }

      /**
       * @return the isFollowing
       */
      public String getIsFollowing() {
            return isFollowing;
      }

      /**
       * @param isFollowing the isFollowing to set
       */
      public void setIsFollowing(String isFollowing) {
            this.isFollowing = isFollowing;
      }

      /**
       * @return the gender
       */
      public String getGender() {
            return gender;
      }

      /**
       * @param gender the gender to set
       */
      public void setGender(String gender) {
            this.gender = gender;
      }

      /**
       * @return the dOB
       */
      public String getdOB() {
            return dOB;
      }

      /**
       * @param dOB the dOB to set
       */
      public void setdOB(String dOB) {
            this.dOB = dOB;
      }


      public String getCountrycode() {
            return countrycode;
      }

      public void setCountrycode(String countrycode) {
            this.countrycode = countrycode;
      }

      private String countrycode;

      public boolean isEditProfileFlag() {
            return editProfileFlag;
      }

      public void setEditProfileFlag(boolean editProfileFlag) {
            this.editProfileFlag = editProfileFlag;
      }


      public boolean isMenuUserInfoFlag() {
            return menuUserInfoFlag;
      }

      public void setMenuUserInfoFlag(boolean menuUserInfoFlag) {
            this.menuUserInfoFlag = menuUserInfoFlag;
      }

      private boolean menuUserInfoFlag = false;


      /**
       * @return the tmpQBId
       */
      public String getTmpQBId() {
            return tmpQBId;
      }

      /**
       * @param tmpQBId the tmpQBId to set
       */
      public void setTmpQBId(String tmpQBId) {
            this.tmpQBId = tmpQBId;
      }

      /**
       * @return the tmpUserId
       */
      public String getTmpUserId() {
            return tmpUserId;
      }

      /**
       * @param tmpUserId the tmpUserId to set
       */
      public void setTmpUserId(String tmpUserId) {
            this.tmpUserId = tmpUserId;
      }

      /**
       * @return the tmpUserName
       */
      public String getTmpUserName() {
            return tmpUserName;
      }

      /**
       * @param tmpUserName the tmpUserName to set
       */
      public void setTmpUserName(String tmpUserName) {
            this.tmpUserName = tmpUserName;
      }

      /**
       * @return the tmpUserPic
       */
      public String getTmpUserPic() {
            return tmpUserPic;
      }

      /**
       * @param tmpUserPic the tmpUserPic to set
       */
      public void setTmpUserPic(String tmpUserPic) {
            this.tmpUserPic = tmpUserPic;
      }

      /**
       * @return the tmpFollower
       */
      public String getTmpFollower() {
            return tmpFollower;
      }

      /**
       * @param tmpFollower the tmpFollower to set
       */
      public void setTmpFollower(String tmpFollower) {
            this.tmpFollower = tmpFollower;
      }

      /**
       * @return the tmpFollowing
       */
      public String getTmpFollowing() {
            return tmpFollowing;
      }

      /**
       * @param tmpFollowing the tmpFollowing to set
       */
      public void setTmpFollowing(String tmpFollowing) {
            this.tmpFollowing = tmpFollowing;
      }

      /**
       * @return the tmpIsFollowing
       */
      public int getTmpIsFollowing() {
            return tmpIsFollowing;
      }

      /**
       * @param tmpIsFollowing the tmpIsFollowing to set
       */
      public void setTmpIsFollowing(int tmpIsFollowing) {
            this.tmpIsFollowing = tmpIsFollowing;
      }

      /**
       * @return the tmpGender
       */
      public String getTmpGender() {
            return tmpGender;
      }

      /**
       * @param tmpGender the tmpGender to set
       */
      public void setTmpGender(String tmpGender) {
            this.tmpGender = tmpGender;
      }

      /**
       * @return the tmpDOB
       */
      public String getTmpDOB() {
            return tmpDOB;
      }

      /**
       * @param tmpDOB the tmpDOB to set
       */
      public void setTmpDOB(String tmpDOB) {
            this.tmpDOB = tmpDOB;
      }

      /**
       * @return the tmpCity
       */
      public String getTmpCity() {
            return tmpCity;
      }

      /**
       * @param tmpCity the tmpCity to set
       */
      public void setTmpCity(String tmpCity) {
            this.tmpCity = tmpCity;
      }

      /**
       * @return the tmpCountry
       */
      public String getTmpCountry() {
            return tmpCountry;
      }

      /**
       * @param tmpCountry the tmpCountry to set
       */
      public void setTmpCountry(String tmpCountry) {
            this.tmpCountry = tmpCountry;
      }

      /**
       * @return the tmpFollowId
       */
      public String getTmpFollowId() {
            return tmpFollowId;
      }

      /**
       * @param tmpFollowId the tmpFollowId to set
       */
      public void setTmpFollowId(String tmpFollowId) {
            this.tmpFollowId = tmpFollowId;
      }

      /**
       * @return the tmpIsFollowingRequested
       */
      public int getTmpIsFollowingRequested() {
            return tmpIsFollowingRequested;
      }

      /**
       * @param tmpIsFollowingRequested the tmpIsFollowingRequested to set
       */
      public void setTmpIsFollowingRequested(int tmpIsFollowingRequested) {
            this.tmpIsFollowingRequested = tmpIsFollowingRequested;
      }


      // private QBPrivateChat qBPrivateChatReal;
      private QBPrivateChat qBPrivateChat;

      public QBPrivateChat getqBPrivateChat() {

            return qBPrivateChat;
      }

      public void setqBPrivateChat(QBPrivateChat qBPrivateChat) {
            this.qBPrivateChat = qBPrivateChat;
      }

      public void setUserImageUri(Uri uri) {
            this.userImageUri = uri;
      }

      public Uri getUserImageUri() {
            return this.userImageUri;
      }

      public void setUserbitmap(Bitmap userbitmap) {
            this.userbitmap = userbitmap;
      }

      public Bitmap getUserbitmap() {
            return this.userbitmap;
      }


      /* resize bit map*/

      public Bitmap getmResizeBitmap() {
            return mResizeBitmap;
      }

      public void setmResizeBitmap(Bitmap mResizeBitmap) {
            this.mResizeBitmap = mResizeBitmap;
      }

      public Bitmap getOrginalBitmap() {
            return mOrginalBitmap;
      }

      public void setOrginalBitmap(Bitmap mOrginalBitmap) {
            this.mOrginalBitmap = mOrginalBitmap;
      }


      public void signIn(String username, String password, String deviceToken,
                         String deviceType) {
            authManager = ModelManager.getInstance().getAuthorizationManager();
            JSONObject userInputDetails = new JSONObject();
            try {

                  boolean retval = username.contains("@");
                  if (retval) {
                        userInputDetails.put("email", username);
                  } else {
                        userInputDetails.put("phone_no", username);
                  }

                  userInputDetails.put("device_token", deviceToken);
                  userInputDetails.put("password", password);
                  userInputDetails.put("device_type", deviceType);
                  Log.e(TAG, "userInputDetails-> " + userInputDetails);
                  client = new AsyncHttpClient();
                  se = new StringEntity(userInputDetails.toString());
                  se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                                                           "application/json"));
            } catch (Exception e1) {
                  e1.printStackTrace();

            }
            Log.e("SIGNIN url", APIs.SIGNIN);
            client.post(null, APIs.SIGNIN, se, "application/json",
                               new JsonHttpResponseHandler() {

                                     @Override
                                     public void onFailure(int statusCode, Throwable e,
                                                           JSONObject errorResponse) {
                                           super.onFailure(statusCode, e, errorResponse);
                                           if (errorResponse != null) {
                                                 Log.e("errorResponse", "->" + errorResponse);
                                                 try {
                                                       authManager.setMessage(errorResponse.getString("message"));
                                                 } catch (JSONException e1) {
                                                 }
                                                 EventBus.getDefault().post("SignIn False");
                                           } else {
                                                 EventBus.getDefault().post("SignIn Network Error");
                                           }

                                     }

                                     @Override
                                     public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                                           super.onSuccess(statusCode, headers, response);
                                           boolean state = false;
                                           try {
                                                 Log.e(TAG, "response  SIGNIN->" + response);
                                                 state = response.getBoolean("success");
                                                 if (state) {

                                                       if (response.has("phone_no")) {
                                                             authManager.setPhoneNo(response.getString("phone_no"));
                                                       }
                                                       if (response.has("message")) {
                                                             authManager.setMessage(response.getString("message"));
                                                       }
                                                       if (response.has("device_registered")) {
                                                             authManager.setDeviceRegistered(response.getBoolean("device_registered"));
                                                       }
                                                       if (response.has("QB_id")) {
                                                             authManager.setQBId(response.getString("QB_id"));
                                                       }
                                                       if (response.has("user_id")) {
                                                             authManager.setUserId(response.getString("user_id"));
                                                       }
                                                       if (response.has("user_name")) {
                                                             authManager.setUserName(response.getString("user_name"));
                                                       }
                                                       if (response.has("user_pic")) {
                                                             authManager.setUserPic(response.getString("user_pic"));
                                                       }
                                                       if (response.has("user_token")) {
                                                             authManager.setUsrToken(response.getString("user_token"));
                                                       }

                                                 }
                                                 EventBus.getDefault().post("SignIn True");
                                           } catch (JSONException e) {
                                                 e.printStackTrace();
                                           }

                                     }

                               }
            );

      }


      public void signUpAuth(String phone, String deviceToken) {
            JSONObject userInputDetails = new JSONObject();
            authManager = ModelManager.getInstance().getAuthorizationManager();
            try {
                  userInputDetails.put("phone_no", phone);
                  userInputDetails.put("device_token", deviceToken);

                  client = new AsyncHttpClient();
                  se = new StringEntity(userInputDetails.toString());
                  se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            } catch (Exception e1) {
                  e1.printStackTrace();
            }
            client.post(null, APIs.CREATEUSER, se, "application/json", new JsonHttpResponseHandler() {
                  @Override
                  public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        System.out.println("errorResponse--> " + errorResponse);
                        if (errorResponse != null) {
                              try {
                                    authManager.setMessage(errorResponse.getString("message"));
                              } catch (JSONException e1) {
                                    e1.printStackTrace();
                              }
                              EventBus.getDefault().post("SignUp False");
                        } else {
                              EventBus.getDefault().post("SignUp Network Error");
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
                                    EventBus.getDefault().post("SignUp True");
                              }
                        } catch (JSONException e) {
                              e.printStackTrace();
                        }

                  }

            });
      }


      public void getVerifyCode(String phone, String deviceToken, String vCode,
                                String deviceType) {
            authManager = ModelManager.getInstance().getAuthorizationManager();
            JSONObject inputDetails = new JSONObject();
            try {
                  inputDetails.put("phone_no", phone);
                  inputDetails.put("vcode", vCode);
                  inputDetails.put("device_token", deviceToken);
                  inputDetails.put("device_type", deviceType);

                  System.out.println("inputDetails--> " + inputDetails);
                  client = new AsyncHttpClient();
                  se = new StringEntity(inputDetails.toString());
                  se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            } catch (Exception e1) {
                  e1.printStackTrace();
            }
            client.post(null, APIs.VERIFYCODE, se, "application/json",
                               new JsonHttpResponseHandler() {

                                     @Override
                                     public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                                           super.onFailure(statusCode, e, errorResponse);
                                           Log.e("Auth", "errorResponse--> " + errorResponse);
                                           if (errorResponse != null) {
                                                 try {
                                                       authManager.setMessage(errorResponse.getString("message"));
                                                 } catch (JSONException e1) {
                                                       // TODO Auto-generated catch block
                                                       e1.printStackTrace();
                                                 }
                                                 EventBus.getDefault().post("Verify False");
                                           } else {
                                                 EventBus.getDefault().post("Verify Network Error");
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
                                                       if (response.has("QB_id"))
                                                             authManager.setQBId(response.getString("QB_id"));
                                                       if (response.has("user_id"))
                                                             authManager.setUserId(response.getString("user_id"));
                                                       if (response.has("user_token"))
                                                             authManager.setUsrToken(response.getString("user_token"));
                                                       Log.e("getUsrToken--", "" + authManager.getUsrToken());
                                                       EventBus.getDefault().post("Verify True");
                                                 }
                                           } catch (JSONException e) {
                                                 e.printStackTrace();
                                           }

                                     }

                               }
            );

      }


      public void playItSafeAuth(String password, String phone, String email,
                                 String urserToken) {
            authManager = ModelManager.getInstance().getAuthorizationManager();
            JSONObject inputDetails = new JSONObject();
            try {
                  inputDetails.put("phone_no", phone);
                  inputDetails.put("user_token", urserToken);
                  inputDetails.put("email", email);
                  inputDetails.put("password", password);

                  client = new AsyncHttpClient();
                  se = new StringEntity(inputDetails.toString());
                  se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                                                           "application/json"));
            } catch (Exception e1) {
                  e1.printStackTrace();
            }
            client.post(null, APIs.INSERTEMAIL, se, "application/json",
                               new JsonHttpResponseHandler() {
                                     @Override
                                     public void onFailure(int statusCode, Throwable e,
                                                           JSONObject errorResponse) {
                                           super.onFailure(statusCode, e, errorResponse);
                                           System.out.println("errorResponse--> " + errorResponse);
                                           if (errorResponse != null) {
                                                 try {
                                                       authManager.setMessage(errorResponse
                                                                                      .getString("message"));
                                                 } catch (JSONException e1) {
                                                       // TODO Auto-generated catch block
                                                       e1.printStackTrace();
                                                 }
                                                 EventBus.getDefault().post("PlayItSafe False");
                                           } else {
                                                 EventBus.getDefault().post("PlayItSafe Network Error");
                                           }

                                     }

                                     @Override
                                     public void onSuccess(int statusCode,
                                                           org.apache.http.Header[] headers,
                                                           JSONObject response) {
                                           super.onSuccess(statusCode, headers, response);
                                           boolean state = false;
                                           try {
                                                 System.out.println("response-INSERTEMAIL--> " + response);
                                                 state = response.getBoolean("success");
                                                 if (state) {

                                                       EventBus.getDefault().post("PlayItSafe True");
                                                 }

                                           } catch (JSONException e) {
                                                 e.printStackTrace();
                                           }

                                     }

                               }
            );

      }


      public void reSendVerifyCode(String phone, String deviceToken) {
            // TODO Auto-generated method stub

            authManager = ModelManager.getInstance().getAuthorizationManager();
            JSONObject inputDetails = new JSONObject();
            try {
                  inputDetails.put("phone_no", phone);
                  inputDetails.put("device_token", deviceToken);

                  client = new AsyncHttpClient();
                  se = new StringEntity(inputDetails.toString());
                  se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                  Log.e("", "inputDetails-->" + inputDetails);
            } catch (Exception e1) {
                  e1.printStackTrace();
            }
            client.post(null, APIs.RESENDVERIFYCODE, se, "application/json", new JsonHttpResponseHandler() {
                  @Override
                  public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        Log.e("Auth", "errorResponse--> " + errorResponse);
                        if (errorResponse != null) {
                              try {
                                    authManager.setMessage(errorResponse.getString("message"));
                              } catch (JSONException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                              }
                              EventBus.getDefault().post("ReSendVerifyCode False");
                        } else {
                              EventBus.getDefault().post("ReSendVerifyCode Network Error");
                        }

                  }

                  @Override
                  public void onSuccess(int statusCode,
                                        org.apache.http.Header[] headers,
                                        JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {
                              System.out.println("response-INSERTEMAIL--> " + response);
                              state = response.getBoolean("success");
                              if (state) {
                                    EventBus.getDefault().post("ReSendVerifyCode True");
                              }

                        } catch (JSONException e) {
                              e.printStackTrace();
                        }

                  }

            });

      }


      public void sendNewRequest(String phone, String partner, String usertoken) {
            // TODO Auto-generated method stub

            authManager = ModelManager.getInstance().getAuthorizationManager();
            JSONObject inputDetails = new JSONObject();
            try {
                  inputDetails.put("phone_no", phone);
                  inputDetails.put("partner_phone_no", partner);
                  inputDetails.put("user_token", usertoken);
                  client = new AsyncHttpClient();
                  se = new StringEntity(inputDetails.toString());
                  se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                                                           "application/json"));
                  Log.e("", "inputDetails-->" + inputDetails);
            } catch (Exception e1) {
                  e1.printStackTrace();
            }
            client.post(null, APIs.NEWREQUEST, se, "application/json",
                               new JsonHttpResponseHandler() {
                                     @Override
                                     public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                                           super.onFailure(statusCode, e, errorResponse);
                                           System.out.println("errorResponse--> " + errorResponse);
                                           if (errorResponse != null) {
                                                 try {
                                                       authManager.setMessage(errorResponse.getString("message"));
                                                 } catch (JSONException e1) {
                                                       // TODO Auto-generated catch block
                                                       e1.printStackTrace();
                                                 }
                                                 EventBus.getDefault().post("RequestSend False");
                                           } else {
                                                 EventBus.getDefault().post("RequestSend Network Error");
                                           }

                                     }

                                     @Override
                                     public void onSuccess(int statusCode,
                                                           org.apache.http.Header[] headers,
                                                           JSONObject response) {
                                           super.onSuccess(statusCode, headers, response);
                                           boolean state = false;
                                           try {
                                                 System.out.println("response-INSERTEMAIL--> "
                                                                            + response);
                                                 state = response.getBoolean("success");
                                                 if (state) {
                                                       EventBus.getDefault().post("RequestSend True");
                                                 }

                                           } catch (JSONException e) {
                                                 e.printStackTrace();
                                           }

                                     }

                               }
            );

      }

      public void getProfileInfo(final String othersphone, String phone, String usertoken) {
            // TODO Auto-generated method stub
            authManager = ModelManager.getInstance().getAuthorizationManager();
            String str = null;
            try {
                  client = new AsyncHttpClient();


                  //for development
//            client.addHeader("user_token", usertoken);
//            client.addHeader("phone_no", phone);

                  //for prod
                  client.addHeader("User-Token", usertoken);
                  client.addHeader("Phone-No", phone);

                  Log.e("usertoken-phone_no-othersphone-->", "" + usertoken + "-" + phone + "-" + othersphone);


                  if (!Utils.isEmptyString(othersphone)) {
                        str = othersphone.substring(1);
                  } else {
                        str = phone.substring(1);
                  }
            } catch (Exception e1) {
                  e1.printStackTrace();
            }
            Log.e("othersphone url-->", "" + APIs.FETCHPROFILEINFO + "%2B" + str);
            client.get(APIs.FETCHPROFILEINFO + "%2B" + str, new JsonHttpResponseHandler() {
                  boolean success = false;

                  @Override
                  public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        System.out.println("errorResponse--> " + errorResponse);
                        if (errorResponse != null) {
                              try {
                                    authManager.setMessage(errorResponse.getString("message"));
                              } catch (JSONException e1) {
                              }

                              EventBus.getDefault().post("ProfileInfo False");
                        } else {
                              EventBus.getDefault().post("ProfileInfo Network Error");
                        }

                  }

                  @SuppressWarnings("unused")
                  @Override
                  public void onSuccess(int statusCode,
                                        org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {

                              Log.e(TAG, "response--> " + response);
                              success = response.getBoolean("success");
                              if (success) {

                                    if (!Utils.isEmptyString(othersphone)) {
                                          JSONObject jobj = new JSONObject(response.getString("user"));
                                          if (jobj.has("gender"))
                                                authManager.setTmpGender(jobj.getString("gender"));
                                                 else
                                                authManager.setGender("");
                                          if (jobj.has("follower"))
                                                authManager.setTmpFollower(jobj.getString("follower"));
                                          if (jobj.has("following"))
                                                authManager.setTmpFollowing(jobj.getString("following"));
                                          if (jobj.has("is_following"))
                                                authManager.setTmpIsFollowing(jobj.getInt("is_following"));
                                          if (jobj.has("name"))
                                                authManager.setTmpUserName(jobj.getString("name"));
                                          if (jobj.has("user_pic"))
                                                authManager.setTmpUserPic(jobj.getString("user_pic"));
                                          if (jobj.has("dob"))
                                                authManager.setTmpDOB(jobj.getString("dob"));
                                          if (jobj.has("is_following_requested"))
                                                authManager.setTmpIsFollowingRequested(jobj.getInt("is_following_requested"));

                                    } else {
                                          JSONObject jobj = new JSONObject(response.getString("user"));
                                          if (jobj.has("gender"))
                                                authManager.setGender(jobj.getString("gender"));
//                            }else
//                            authManager.setGender("");

                                          if (jobj.has("follower"))
                                                authManager.setFollower(jobj.getString("follower"));
                                          if (jobj.has("following"))
                                                authManager.setFollowing(jobj.getString("following"));
                                          if (jobj.has("is_following"))
                                                authManager.setIsFollowing(jobj.getString("is_following"));
                                          if (jobj.has("name"))
                                                authManager.setUserName(jobj.getString("name"));
                                          if (jobj.has("user_pic"))
                                                authManager.setUserPic(jobj.getString("user_pic"));
                                          if (jobj.has("dob"))
                                                authManager.setdOB(jobj.getString("dob"));
                                          if (jobj.has("city"))
                                                authManager.setUserCity(jobj.getString("city"));
                                          if (jobj.has("country"))
                                                authManager.setUserCountry(jobj.getString("country"));
                                          if (jobj.has("email"))
                                                authManager.setEmailId(jobj.getString("email"));
                                    }

                                    EventBus.getDefault().post("ProfileInfo True");
                              }


                        } catch (JSONException e) {
                              e.printStackTrace();
                        }

                  }

            });

      }


}
