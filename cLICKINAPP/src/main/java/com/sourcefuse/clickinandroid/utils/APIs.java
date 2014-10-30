package com.sourcefuse.clickinandroid.utils;

public class APIs {
	

	public static final String baseurl ="https://api.clickinapp.com";//prod
    //public static final String baseurl = "http://23.21.65.97"; //staging



   //clickin@sourcefuse.com
   //sourcefuse1234


    public static final String CHECKREGISTEREDFRIENDS = baseurl+"/users/checkregisteredfriends/";
    public static final String RESENDVERIFYCODE = baseurl+"/users/resendvcode/";
    public static final String UPDATEPROFILE = baseurl+"/users/updateprofile/";
    public static final String UNFOLLOWUSER = baseurl+"/users/unfollowuser/";
    public static final String INSERTEMAIL = baseurl+"/users/insertemail/";
	public static final String CREATEUSER = baseurl+"/users/createuser/";
	public static final String VERIFYCODE = baseurl+"/users/verifycode/";
	public static final String SIGNIN = baseurl+"/users/signin/";

	//Relationships
    public static final String FETCHPROFILERELATIONSHIPS = baseurl+"/users/fetchprofilerelationships/profile_phone_no:%2B";
    public static final String INVITEANDFOLLOWUSERS = baseurl+"/relationships/inviteandfollowusers/";
    public static final String DELETERELATIONSHIP = baseurl+"/relationships/deleterelationship/";
    public static final String FETCHUSERSBYNAME = baseurl+"/relationships/fetchusersbyname";
    public static final String GETRELATIONSHIPS = baseurl+"/relationships/getrelationships/";
    public static final String CHANGEVISIBILITY = baseurl+"/relationships/changevisibility";
    public static final String FOLLOWUPDATESTATUS = baseurl+"/users/followupdatestatus";//REJECT FOLLOW REQUEST
    public static final String NEWREQUEST = baseurl+"/relationships/newrequest/";
	public static final String FOLLOWUSER = baseurl+"/relationships/followuser/";
	public static final String UPDATESTATUS = baseurl+"/relationships/updatestatus/";

	//chats
    public static final String RESETUNREADMESSAGECOUNT = baseurl+"/chats/resetunreadmessagecount/";
    public static final String GETUNREADMESSAGECOUNT = baseurl+"/chats/getunreadmessagecount/";
    public static final String FETCHCHATRECORDS = baseurl+"/chats/fetchchatrecords/";
    public static final String RESETBADGECOUNT = baseurl+"/chats/resetbadgecount/";
	public static final String SHARINGACTION = baseurl+"/chats/sharingaction/";
    public static final String FETCHCARDS = baseurl+"/chats/fetchcards/";
    public static final String SAVECARDS = baseurl+"/chats/savecards/";
    public static final String CHATSHARE = baseurl+"/chats/share/";

	// Fetch Notifications
	public static final String FETCHNOTIFICATIONS = baseurl+"/notification/fetchnotifications/";
	
	//Fetch Newsfeed
    public static final String REPORTINAPPROPRIATE = baseurl+"/newsfeed/reportinappropriate/";
    public static final String UNSTARRENDNEWSFEED = baseurl+"/newsfeed/unstarrednewsfeed/";
    public static final String FETCHCOMMENTSTATUS = baseurl+"/newsfeed/fetchcommentstars/";
    public static final String FETCHNEWSFEEDS = baseurl+"/newsfeed/fetchnewsfeeds/";
    public static final String FETCHFBFRIENDS = baseurl+"/newsfeed/fetchfbfriends/";
    public static final String DELETENEWSFEED = baseurl+"/newsfeed/delete/";
    public static final String SAVESTARCOMMENT = baseurl+"/newsfeed/savecommentstar/";


    //fetchprofile
	public static final String FETCHOTHERPROFILEINFO = baseurl+"/users/fetchprofileinfo/profile_phone_no";
    public static final String GETUSERFOLLOWER = baseurl+"/users/fetchprofilefollow/profile_phone_no";
    public static final String FETCHPROFILEINFO = baseurl+"/users/fetchprofileinfo/profile_phone_no:";

    //Setting..
    public static final String SETTINGCHANGELASTSEENTIME = baseurl+"/settings/changelastseentime";
    public static final String SETTINGCHANGEDEACTIVATE = baseurl+"/settings/deactivateaccount";
    public static final String SETTINGCHANGEPASSWORD = baseurl+"/settings/changepassword";
    public static final String SETTINGREPORTPROBLEM = baseurl+"/settings/reportaproblem";
    public static final String SETTINGFORGOTPASSWORD = baseurl+"/settings/forgotpassword";
    public static final String SETTINGCHANGE = baseurl+"/settings/change";
}
