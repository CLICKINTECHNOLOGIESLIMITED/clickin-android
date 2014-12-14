package com.sourcefuse.clickinandroid.model;

import android.content.Context;

import com.sourcefuse.clickinandroid.model.bean.NotificationBean;

import java.util.ArrayList;

/**
 * Created by mukesh on 3/7/14.
 */
public interface NotificationManagerI {

    public ArrayList<NotificationBean> notificationData = new ArrayList<NotificationBean>();

    public abstract void getNotification(Context context, String lastNotificationId, String phone, String usertoken);
}
