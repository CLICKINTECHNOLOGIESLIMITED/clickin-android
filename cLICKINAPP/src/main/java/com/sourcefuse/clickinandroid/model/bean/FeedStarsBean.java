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
    private String user_pic, comment;
    private String newsFeedArray_created_sec;
    private String newsFeedArray_created_usec;
    private String is_user_follower_acceptance = null, is_user_following_acceptance = null, is_user_in_relation_acceptance = null;
    private int is_user_following = 0, is_user_in_relation = 0, is_user_follower = 0;
//    private String newsFeedArray_chatDetail_modified_sec;
//    private String newsFeedArray_chatDetail_modified_usec;

    public String getId() {
        return this._id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getChatId() {
        return this.chat_id;
    }

    public void setChatId(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getNewsId() {
        return this.newsfeed_id;
    }

    public void setNewsId(String newsfeed_id) {
        this.newsfeed_id = newsfeed_id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIs_user_follower() {
        return is_user_follower;
    }

    public void setIs_user_follower(int is_user_follower) {
        this.is_user_follower = is_user_follower;

    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getIs_user_in_relation() {
        return is_user_in_relation;
    }

    public void setIs_user_in_relation(int is_user_in_relation) {
        this.is_user_in_relation = is_user_in_relation;
    }

    public int getIs_user_following() {
        return is_user_following;
    }

    public void setIs_user_following(int is_user_following) {
        this.is_user_following = is_user_following;
    }

    public String getIs_user_in_relation_acceptance() {
        return is_user_in_relation_acceptance;
    }

    public void setIs_user_in_relation_acceptance(String is_user_in_relation_acceptance) {
        this.is_user_in_relation_acceptance = is_user_in_relation_acceptance;
    }

    public String getIs_user_following_acceptance() {
        return is_user_following_acceptance;
    }

    public void setIs_user_following_acceptance(String is_user_following_acceptance) {
        this.is_user_following_acceptance = is_user_following_acceptance;
    }

    public String getIs_user_follower_acceptance() {
        return is_user_follower_acceptance;
    }

    public void setIs_user_follower_acceptance(String is_user_follower_acceptance) {
        this.is_user_follower_acceptance = is_user_follower_acceptance;
    }

    public String getUserId() {
        return this.user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getUserName() {
        return this.user_name;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getUserPic() {
        return this.user_pic;
    }

    public void setUserPic(String user_pic) {
        this.user_pic = user_pic;
    }

    public String getcreated_sec() {
        return newsFeedArray_created_sec;
    }

    public void setcreated_sec(String newsFeedArray_created_sec) {
        this.newsFeedArray_created_sec = newsFeedArray_created_sec;
    }

    public String getcreated_usec() {
        return newsFeedArray_created_usec;
    }

    public void setcreated_usec(String newsFeedArray_created_usec) {
        this.newsFeedArray_created_usec = newsFeedArray_created_usec;
    }
}
