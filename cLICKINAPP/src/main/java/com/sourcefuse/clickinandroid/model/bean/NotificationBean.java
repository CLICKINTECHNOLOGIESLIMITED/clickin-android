package com.sourcefuse.clickinandroid.model.bean;

/**
 * Created by mukesh on 3/7/14.
 */

public class NotificationBean {
    private String notificationMsg;
    private String notificationType;
    public String _id;  // last chat Id prafull
    public String mUser_id;  // last partner Id
    public String newsfeed_id;  // last partner Id
    public String update_user_id;

    public String getIs_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }

    public String is_read;

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }


    public String getNotificationMsg() {
        return notificationMsg;
    }

    public void setNotificationMsg(String notificationMsg) {
        this.notificationMsg = notificationMsg;
    }


}
