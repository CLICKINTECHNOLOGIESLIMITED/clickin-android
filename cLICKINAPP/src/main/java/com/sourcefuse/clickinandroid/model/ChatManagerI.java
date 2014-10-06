package com.sourcefuse.clickinandroid.model;

public interface ChatManagerI {
	
	public abstract void fetchChatRecord(String relationshipId,String phone,String usertoken);
    public abstract void fetchCards(String phone,String usertoken);



}
