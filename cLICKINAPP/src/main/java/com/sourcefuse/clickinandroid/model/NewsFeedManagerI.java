package com.sourcefuse.clickinandroid.model;

import com.sourcefuse.clickinandroid.model.bean.NewsFeedBean;

import java.util.ArrayList;

public interface NewsFeedManagerI {
	
	
	public abstract void fetchNewsFeed(String lastNewsfeedId,String phone,String usertoken);
    public abstract void fetchFbFriends(String access_token,String phone,String usertoken);
    public ArrayList<NewsFeedBean> userFeed = new ArrayList<NewsFeedBean>();


}
