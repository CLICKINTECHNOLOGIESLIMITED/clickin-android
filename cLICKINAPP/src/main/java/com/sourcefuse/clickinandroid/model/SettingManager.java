package com.sourcefuse.clickinandroid.model;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sourcefuse.clickinandroid.utils.APIs;
import com.sourcefuse.clickinandroid.utils.Log;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by mukesh on 25/7/14.
 */
public class SettingManager {
    private static final String TAG = SettingManager.class.getSimpleName();
    StringEntity se = null;
    private AuthManager authManager;
    private AsyncHttpClient client;

    //akshit Code For App Sound
    public static boolean appSounds = true ;
    public static boolean isAppSounds() {
        return appSounds;
    }

    public static void setAppSounds(boolean appSounds) {
        SettingManager.appSounds = appSounds;
    }
//Ends


    public void changePassword(String phone_no, String user_token, String old_password, String new_password, String confirm_password) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone_no);
            userInputDetails.put("user_token", user_token);
            userInputDetails.put("old_password", old_password);
            userInputDetails.put("new_password", new_password);
            userInputDetails.put("confirm_password", confirm_password);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            Log.e(TAG, "changePassword-->" + userInputDetails);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.SETTINGCHANGEPASSWORD, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        if (errorResponse != null) {
                            EventBus.getDefault().post("ChangePassword False");
                        } else {
                            EventBus.getDefault().post("ChangePassword Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {
                            Log.e(TAG, "response ChangePassword ->" + response);
                            state = response.getBoolean("success");
                            if (state) {

                                EventBus.getDefault().post("ChangePassword True");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }

    public void enableDisablePushNotifications(String phone_no, String user_token, String isEnable) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone_no);
            userInputDetails.put("user_token", user_token);
            userInputDetails.put("is_enable_push_notification", isEnable);


            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            Log.e(TAG, "PushNotifications-->" + se);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.SETTINGCHANGE, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        if (errorResponse != null) {
                            EventBus.getDefault().post("PushNotifications False");
                        } else {
                            EventBus.getDefault().post("PushNotifications Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {
                            Log.e(TAG, "response ChangePassword ->" + response);
                            state = response.getBoolean("success");
                            if (state) {

                                EventBus.getDefault().post("PushNotifications True");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }

    public void deactivteAccount(String phone_no, String user_token, String reason_type, String other_reason, String email_opt_out, String password) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone_no);
            userInputDetails.put("user_token", user_token);
            userInputDetails.put("reason_type", reason_type);
            userInputDetails.put("other_reason", other_reason);
            userInputDetails.put("email_opt_out", email_opt_out);
            userInputDetails.put("password", password);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            Log.e(TAG, "changePassword-->" + se);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Log.e(TAG, "send json ->" + userInputDetails);
        client.post(null, APIs.SETTINGCHANGEDEACTIVATE, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        if (errorResponse != null) {
                            EventBus.getDefault().post("DeactivteAccount False");
                        } else {
                            EventBus.getDefault().post("DeactivteAccount Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {
                            Log.e(TAG, "response DeactivteAccount ->" + response);
                            state = response.getBoolean("success");
                            if (state) {

                                EventBus.getDefault().post("DeactivteAccount True");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }

    public void reportaproblem(String phone_no, String user_token, String problemType, String spamOrAbuseType, String comment) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone_no);
            userInputDetails.put("user_token", user_token);
            userInputDetails.put("problem_type", problemType);
            userInputDetails.put("spam_or_abuse_type", spamOrAbuseType);
            userInputDetails.put("comment", comment);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            Log.e(TAG, "reportaproblem-->" + userInputDetails);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.SETTINGREPORTPROBLEM, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        if (errorResponse != null) {
                            EventBus.getDefault().post("ReportaProblem False");
                        } else {
                            EventBus.getDefault().post("ReportaProblem Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {
                            Log.e(TAG, "response ReportaProblem ->" + response);
                            state = response.getBoolean("success");
                            if (state) {

                                EventBus.getDefault().post("ReportaProblem True");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }

    public void forgotYourPassword(String emailId) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("email", emailId);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            Log.e(TAG, "ForgotPassword-->" + se);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.SETTINGFORGOTPASSWORD, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e,
                                          JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        if (errorResponse != null) {
                            EventBus.getDefault().post("ForgotPassword False");
                        } else {
                            EventBus.getDefault().post("ForgotPassword Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode,
                                          org.apache.http.Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {
                            System.out.println("response ForgotPassword ->" + response);
                            state = response.getBoolean("success");
                            if (state) {
                                authManager.setMessage(response.getString("message"));
                                EventBus.getDefault().post("ForgotPassword True");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }


    public void changeLastSeenTime(String phone_no, String user_token) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone_no);
            userInputDetails.put("user_token", user_token);


            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            Log.e(TAG, "reportaproblem-->" + se);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(null, APIs.SETTINGCHANGELASTSEENTIME, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        if (errorResponse != null) {
                            EventBus.getDefault().post("ChangeLastSeenTime False");
                        } else {
                            EventBus.getDefault().post("ChangeLastSeenTime Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {
                            Log.e(TAG, "response ChangeLastSeenTime ->" + response);
                            state = response.getBoolean("success");
                            if (state) {

                                EventBus.getDefault().post("ChangeLastSeenTime True");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }

}
