package com.sourcefuse.clickinandroid.model;

import com.sourcefuse.clickinandroid.model.bean.NotificationBean;

import java.util.ArrayList;

/**
 * Created by mukesh on 3/7/14.
 */
public interface NotificationManagerI {

    public abstract void getNotification(String lastNotificationId,String phone,String usertoken);
    public ArrayList<NotificationBean> notificationData = new ArrayList<NotificationBean>();
}
