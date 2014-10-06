package com.sourcefuse.clickinandroid.model;

public interface RelationManagerI {
	
	public abstract void getRelationShips(String phone,String usertoken);
	public abstract void fetchprofilerelationships(String othersPhone,String phone, String usertoken);
	public abstract void changeUserVisibility(String relationshipIid,String phone, String usertoken,String mode);
	public abstract void followUser(String followeePhoneNo,String phone, String usertoken);
	public abstract void unFollowUser(String followId,String followingMode,String phone, String usertoken);
	public abstract void updateStatus(String relationshipIid,String phone, String usertoken,String mode);
	public abstract void deleteRelationship(String relationshipIid,String phone, String usertoken);
    public abstract void fetchusersbyname(String name,String phone, String usertoken);
    public abstract void followupdatestatus(String follow_id,String accepted,String phone, String usertoken);
	


}
