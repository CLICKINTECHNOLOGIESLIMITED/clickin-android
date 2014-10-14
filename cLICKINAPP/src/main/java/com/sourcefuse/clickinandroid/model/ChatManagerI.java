package com.sourcefuse.clickinandroid.model;

import com.sourcefuse.clickinandroid.model.bean.ChatRecordBeen;
import com.sourcefuse.clickinandroid.model.bean.ContactBean;

import java.util.ArrayList;

public interface ChatManagerI {
	
	public abstract void fetchChatRecord(String relationshipId,String phone,String usertoken,String chatId);
    public abstract void fetchCards(String phone,String usertoken);
    public ArrayList<ChatRecordBeen> chatListFromServer = new ArrayList<ChatRecordBeen>();



}
