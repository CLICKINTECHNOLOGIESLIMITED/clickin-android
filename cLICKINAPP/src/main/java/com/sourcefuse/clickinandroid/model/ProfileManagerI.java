package com.sourcefuse.clickinandroid.model;

import com.sourcefuse.clickinandroid.model.bean.ContactBean;
import com.sourcefuse.clickinandroid.model.bean.CurrentClickerBean;
import com.sourcefuse.clickinandroid.model.bean.FollowerFollowingBean;

import java.util.ArrayList;

public interface ProfileManagerI {

	public abstract void setProfile(String fname, String lname, String phone,
			String usertoken, String gender, String dob, String city,
			String country, String email, String fbaccesstoken, String userpic);
	public abstract void getFollwer(String othersPhone,String phone, String usertoken);
	
	
	public static ArrayList<FollowerFollowingBean> following = new ArrayList<FollowerFollowingBean>();
	public static ArrayList<FollowerFollowingBean> followers = new ArrayList<FollowerFollowingBean>();
    public  ArrayList<CurrentClickerBean> currentClickerList = new ArrayList<CurrentClickerBean>();
    public  ArrayList<ContactBean> spreadTheWorldList = new ArrayList<ContactBean>();

}
