package com.sourcefuse.clickinandroid.model;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sourcefuse.clickinandroid.model.bean.NotificationBean;
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
import java.util.Iterator;

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
    public void getNotification(Context context, final String lastNotificationId, String phone, String usertoken) {


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
                            if (errorResponse != null && errorResponse.has("message")) {
                                if (errorResponse.getString("message").equalsIgnoreCase("User don't have any notification.")) {
//                                    notificationData.clear(); // to clear notification list as no notification is available
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
//                            notificationData.clear(); // to clear notification list as we featch all data
                            if (state) {

                                JSONArray list = response.getJSONArray("notificationArray");
                                notificationArray = new ArrayList<NotificationBean>();
                                for (int i = 0; i < list.length(); i++) {
                                    ntificationBeanList = new NotificationBean();
                                    JSONObject data = list.getJSONObject(i);
                                    ntificationBeanList.setNotificationMsg(data.getString("notification_msg"));
                                    ntificationBeanList.setNotificationType(data.getString("type"));
                                    ntificationBeanList._id = data.getString("_id");
                                    ntificationBeanList.mUser_id = data.getString("user_id");
                                    ntificationBeanList.setIs_read(data.getString("read"));
                                    if (data.getString("read").equalsIgnoreCase("false")) {
                                        int i1 = ModelManager.getInstance().getAuthorizationManager().getNotificationCounter();
                                        ModelManager.getInstance().getAuthorizationManager().setNotificationCounter(i1 + 1);
                                    }
                                    if (data.has("newsfeed_id"))
                                        ntificationBeanList.newsfeed_id = data.getString("newsfeed_id");
                                    if (data.has("update_user_id"))
                                        ntificationBeanList.update_user_id = data.getString("update_user_id");

                                    notificationArray.add(ntificationBeanList);
                                }


                                if (Utils.isEmptyString(lastNotificationId)) {
                                    if (notificationData.size() > 0) {
                                        for (int j = 0; j < notificationData.size(); j++) {  // compare last notification with new one to get only
                                            for (int i = 0; i < notificationArray.size(); i++) {
                                                if (notificationArray.get(i)._id.equalsIgnoreCase(notificationData.get(j)._id)) {
                                                    notificationArray.remove(i);
                                                }
                                            }
                                        }

                                        notificationData.addAll(0, notificationArray); // add new notification in notification list at top.
                                    } else {
                                        notificationData.addAll(notificationArray);
                                    }


                                } else {

                                    notificationData.addAll(notificationArray);

                                }


                                Log.e("notificationData size---", "" + notificationData.size());

                                EventBus.getDefault().postSticky("Notification true");
                                EventBus.getDefault().post("update Counter");

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );


    }

}
