package com.sourcefuse.clickinandroid.model;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sourcefuse.clickinandroid.model.bean.NotificationBean;
import com.sourcefuse.clickinandroid.utils.APIs;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;


/**
 * Created by mukesh on 3/7/14.
 */
public class ClickInNotificationManager implements NotificationManagerI {
    StringEntity se = null;
    AsyncHttpClient client;

    private ArrayList<NotificationBean> notificationArray = null;
    private NotificationBean ntificationBeanList = null;

    @Override
    public void getNotification(Context context, String lastNotificationId, String phone, String usertoken) {


        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone);
            userInputDetails.put("user_token", usertoken);
            userInputDetails.put("last_notification_id", lastNotificationId);

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(context, APIs.FETCHNOTIFICATIONS, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e,
                                          JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);

                        try {
                            if (errorResponse.has("message")) {
                                if (errorResponse.getString("message").equalsIgnoreCase("User don't have any notification.")) {
                                    notificationData.clear(); // to clear notification list as no notification is available
                                    EventBus.getDefault().postSticky("Notification error");
                                }
                            }
                            if (errorResponse != null) {

                                EventBus.getDefault().postSticky("Notification false");
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }


                    }

                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {

                            state = response.getBoolean("success");
                            notificationData.clear(); // to clear notification list as we featch all data
                            if (state) {

                                JSONArray list = response.getJSONArray("notificationArray");
                                notificationArray = new ArrayList<NotificationBean>();
                                for (int i = 0; i < list.length(); i++) {
                                    ntificationBeanList = new NotificationBean();
                                    JSONObject data = list.getJSONObject(i);
                                    ntificationBeanList.setNotificationMsg(data.getString("notification_msg"));
                                    ntificationBeanList.setNotificationType(data.getString("type"));

                                    notificationArray.add(ntificationBeanList);
                                }

                                notificationData.addAll(notificationArray);


                                EventBus.getDefault().postSticky("Notification true");

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );


    }

}
