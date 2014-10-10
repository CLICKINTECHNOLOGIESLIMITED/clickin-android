package com.sourcefuse.clickinandroid.model.bean;

/**
 * Created by charunigam on 10/10/14.
 */
public class FeedStarsBean {


    private String _id;
    private String chat_id;
    private String newsfeed_id;
    private String type;
    private String user_id;
    private String user_name;
    private String user_pic;

    public String getId() {
        return this._id;
    }
    public String getChatId() {
        return this.chat_id;
    }
    public String getNewsId() {
        return this.newsfeed_id;
    }
    public String getType() {
        return this.type;
    }
    public String getUserId() {
        return this.user_id;
    }
    public String getUserName() {
        return this.user_name;
    }
    public String getUserPic() {
        return this.user_pic;
    }

    public void setId(String _id) {
        this._id = _id;
    }
    public void setChatId(String chat_id) {
        this.chat_id = chat_id;
    }
    public void setNewsId(String newsfeed_id) {
        this.newsfeed_id = newsfeed_id;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setUserId(String user_id) {
        this.user_id = user_id;
    }
    public void setUserName(String user_name) {
        this.user_name = user_name;
    }
    public void setUserPic(String user_pic) {
        this.user_pic = user_pic;
    }


}
