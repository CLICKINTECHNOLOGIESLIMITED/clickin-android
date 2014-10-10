package com.sourcefuse.clickinandroid.model;

import com.sourcefuse.clickinandroid.model.bean.FeedStarsBean;
import com.sourcefuse.clickinandroid.model.bean.NewsFeedBean;

import java.util.ArrayList;

public interface NewsFeedManagerI {
	
	
	public abstract void fetchNewsFeed(String lastNewsfeedId,String phone,String usertoken);
    public abstract void fetchFbFriends(String access_token,String phone,String usertoken);
    public ArrayList<NewsFeedBean> userFeed = new ArrayList<NewsFeedBean>();
    public ArrayList<FeedStarsBean> feedStarsList = new ArrayList<FeedStarsBean>();
    public abstract void saveStarComment(String phone, String newsfeedsId, String user_token, String comment, String type);

}
