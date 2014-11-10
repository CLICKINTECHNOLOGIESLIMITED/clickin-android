package com.sourcefuse.clickinandroid.model;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sourcefuse.clickinandroid.model.bean.ContactBean;
import com.sourcefuse.clickinandroid.model.bean.CurrentClickerBean;
import com.sourcefuse.clickinandroid.model.bean.FollowerFollowingBean;
import com.sourcefuse.clickinandroid.utils.APIs;
import com.sourcefuse.clickinandroid.utils.Utils;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Observable;

import de.greenrobot.event.EventBus;

public class ProfileManager {

	private StringEntity se = null;
	private AsyncHttpClient client;
	private AuthManager authManager;
	private FollowerFollowingBean followingList = null;
	private ArrayList<FollowerFollowingBean> followingArray = null;
	private FollowerFollowingBean followerList = null;
	private ArrayList<FollowerFollowingBean> followerArray = null;

    public ArrayList<FollowerFollowingBean> followRequesed = new ArrayList<FollowerFollowingBean>();
    public ArrayList<FollowerFollowingBean> pfollowerList =  new ArrayList<FollowerFollowingBean>();

    public static ArrayList<FollowerFollowingBean> following = new ArrayList<FollowerFollowingBean>();
    public static ArrayList<FollowerFollowingBean> followers = new ArrayList<FollowerFollowingBean>();
    public  ArrayList<CurrentClickerBean> currentClickerList = new ArrayList<CurrentClickerBean>();
    public  ArrayList<ContactBean> spreadTheWorldList = new ArrayList<ContactBean>();



	public void setProfile(String fname, String lname, String phone,
			String usertoken, String gender, String dob, String city,
			String country, String email, String fbaccesstoken, String userpic) {
		authManager = ModelManager.getInstance().getAuthorizationManager();
		JSONObject userInputDetails = new JSONObject();
		try {

			userInputDetails.put("phone_no", phone);
			userInputDetails.put("user_token", usertoken);
			userInputDetails.put("gender", gender);
			userInputDetails.put("dob", dob);
            if(!Utils.isEmptyString(userpic)){
                userInputDetails.put("user_pic", userpic);
            }else{
                userInputDetails.put("user_pic", "");
            }

			userInputDetails.put("city", city);
			userInputDetails.put("country", country);
			userInputDetails.put("email", email);
			userInputDetails.put("first_name", fname);
			userInputDetails.put("last_name", lname);
			// userInputDetails.put("fb_access_token", "jh");
//			Log.d("", "userInputDetails---> " + userInputDetails);
			client = new AsyncHttpClient();
			se = new StringEntity(userInputDetails.toString());
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		client.post(null, APIs.UPDATEPROFILE, se, "application/json",
				new JsonHttpResponseHandler() {
					boolean success = false;

					@Override
					public void onFailure(int statusCode, Throwable e,
							JSONObject errorResponse) {
						super.onFailure(statusCode, e, errorResponse);
						System.out.println("errorResponse--> " + errorResponse);
						if (errorResponse != null) {
							try {
								authManager.setMessage(errorResponse.getString("message"));
							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
                            EventBus.getDefault().post("UpdateProfile False");
						} else {
                            EventBus.getDefault().post("UpdateProfile Network Error");
						}

					}

					@Override
					public void onSuccess(int statusCode,
							org.apache.http.Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							System.out.println("response--> " + response);
							success = response.getBoolean("success");
							if (success) {

								// authManager =
								// ModelManager.getInstance().getAuthorizationManager();
								// authManager.setUserName(response.getString("user_name"));
								// authManager.setUserPic(response.getString("user_pic"));

                                EventBus.getDefault().post("UpdateProfile True");
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

				});

	}		

	public void getFollwer(String othersPhone,String phone, String usertoken) {
		// TODO Auto-generated method stub
		authManager = ModelManager.getInstance().getAuthorizationManager();
		try {
			client = new AsyncHttpClient();
			//client.addHeader("user-token", usertoken);
			//client.addHeader("phone-no", phone);

            client.addHeader("User-Token", usertoken);
            client.addHeader("Phone-No", phone);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		String str=null;
		if(!Utils.isEmptyString(othersPhone)){
		str =  othersPhone.substring(1);
		}else{
			str =  phone.substring(1);	
		}
		Log.e("APIs.GETUSERFOLLOWER+","----> "+APIs.GETUSERFOLLOWER+":%2B"+str);
		client.get(APIs.GETUSERFOLLOWER+":%2B"+str, new JsonHttpResponseHandler() {
			boolean success = false;

			@Override
			public void onFailure(int statusCode, Throwable e,
					JSONObject errorResponse) {
				super.onFailure(statusCode, e, errorResponse);
				System.out.println("errorResponse--> " + errorResponse);
				if (errorResponse != null) {
					try {
						authManager.setMessage(errorResponse.getString("message"));
					} catch (JSONException e1) {}
                    EventBus.getDefault().post("GetFollower False");
				} else {
                    EventBus.getDefault().post("GetFollower Network Error");
				}

			}

			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				try {
					System.out.println("response--> " + response);
					success = response.getBoolean("success");
					if (success) {
						following.clear();
						followers.clear();
                        followRequesed.clear();
                        pfollowerList.clear();
						JSONArray list = response.getJSONArray("following");
						followingArray = new ArrayList<FollowerFollowingBean>();
						for (int i = 0; i < list.length(); i++) {
							    JSONObject data = list.getJSONObject(i);
                                followingList = new FollowerFollowingBean();
                            if(!Utils.isEmptyString(data.getString("accepted"))){
                                followingList.setAccepted(data.getString("accepted"));
                                JSONObject rId = data.getJSONObject("_id");
                                followingList.setrFollowerId(rId.getString("$id"));
							    data.has("followee_id");
							    followingList.setFolloweeId(data.getString("followee_id"));
							    data.has("followee_name");
							    followingList.setFolloweeName(data.getString("followee_name"));
							    data.has("followee_pic");
							    followingList.setFolloweePic(data.getString("followee_pic"));
							    data.has("phone_no");
							    followingList.setPhoneNo(data.getString("phone_no"));
                                followingList.setIsFollowing("false");
                                /*data.has("following_id");
                                followingList.setFollowingId(data.getString("following_id"));*/
							    followingArray.add(followingList);
                                }else{
                                followingList.setAccepted("");
                                JSONObject rId = data.getJSONObject("_id");
                                followingList.setrFollowerId(rId.getString("$id"));
                                data.has("followee_id");
                                followingList.setFolloweeId(data.getString("followee_id"));
                                data.has("followee_name");
                                followingList.setFolloweeName(data.getString("followee_name"));
                                data.has("followee_pic");
                                followingList.setFolloweePic(data.getString("followee_pic"));
                                data.has("phone_no");
                                followingList.setPhoneNo(data.getString("phone_no"));
                                followingList.setIsFollowing("false");
                                /*data.has("following_id");
                                followingList.setFollowingId(data.getString("following_id"));*/
                                followingArray.add(followingList);
                                }
							}
						JSONArray followerData = response.getJSONArray("follower");
						//followerArray = new ArrayList<FollowerFollowingBean>();
						for (int i = 0; i < followerData.length(); i++) {

                            JSONObject data = followerData.getJSONObject(i);
                            if (!data.getString("accepted").matches("false")){
                                followerList = new FollowerFollowingBean();
                            if (!Utils.isEmptyString(data.getString("accepted"))) {
                                followerList.setAccepted(data.getString("accepted"));
                                JSONObject rId = data.getJSONObject("_id");
                                followerList.setrFollowerId(rId.getString("$id"));
                                if(data.has("follower_id"))
                                followerList.setFolloweeId(data.getString("follower_id"));
                                data.has("follower_name");
                                followerList.setFolloweeName(data.getString("follower_name"));
                                data.has("follower_pic");
                                followerList.setFolloweePic(data.getString("follower_pic"));
                                data.has("phone_no");
                                followerList.setPhoneNo(data.getString("phone_no"));

                                if(data.has("following_id")) {
                                    followerList.setFollowingId(data.getString("following_id"));
                                }

                                if (data.has("is_following")) {
                                    if(!Utils.isEmptyString(data.getString("is_following"))) {
                                        followerList.setIsFollowing(data.getString("is_following"));
                                    }else{
                                        followerList.setIsFollowing("false");
                                    }
                                } else {
                                    followerList.setIsFollowing("false");
                                }
                                if (data.has("following_accepted")) {
                                    if(!Utils.isEmptyString(data.getString("following_accepted"))){
                                        followerList.setFollowingAccepted(data.getString("following_accepted"));
                                    }else{
                                        followerList.setFollowingAccepted("false");
                                    }
                                } else {
                                    followerList.setFollowingAccepted("false");
                                }
                                pfollowerList.add(followerList);
                            } else {
                                followerList.setAccepted("");
                                JSONObject rId = data.getJSONObject("_id");
                                followerList.setrFollowerId(rId.getString("$id"));
                                if(data.has("follower_id"))
                                 followerList.setFolloweeId(data.getString("follower_id"));
                                data.has("follower_name");
                                followerList.setFolloweeName(data.getString("follower_name"));
                                data.has("follower_pic");
                                followerList.setFolloweePic(data.getString("follower_pic"));
                                data.has("phone_no");
                                followerList.setPhoneNo(data.getString("phone_no"));

                                if(data.has("following_id")) {
                                    followerList.setFollowingId(data.getString("following_id"));
                                }
                                if (data.has("is_following")) {
                                    if(!Utils.isEmptyString(data.getString("is_following"))) {
                                        followerList.setIsFollowing(data.getString("is_following"));
                                    }else{
                                        followerList.setIsFollowing("false");
                                    }
                                } else {
                                    followerList.setIsFollowing("false");
                                }
                                if (data.has("following_accepted")) {
                                    if(!Utils.isEmptyString(data.getString("following_accepted"))){
                                        followerList.setFollowingAccepted(data.getString("following_accepted"));
                                    }else{
                                        followerList.setFollowingAccepted("false");
                                    }
                                } else {
                                    followerList.setFollowingAccepted("false");
                                }
                               // followerArray.add(followerList);
                                followRequesed.add(followerList);
                                Log.e("followRequesed size in Mgr",""+followRequesed.size());
                            }
                        }
							}
                        //followerArray.add(followerList);
                        followers.addAll(followRequesed);
						followers.addAll(pfollowerList);
						following.addAll(followingArray);
                        EventBus.getDefault().post("GetFollower True");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void getOthersProfile(String phone, String usertoken,String partner_phone){
		
			// TODO Auto-generated method stub
			authManager = ModelManager.getInstance().getAuthorizationManager();
			try {
				client = new AsyncHttpClient();
				//client.addHeader("user_token", usertoken);
				//client.addHeader("phone_no", phone);
                client.addHeader("User-Token", usertoken);
                client.addHeader("Phone-No", phone);
				Log.e("","usertoken--"+usertoken+",,"+phone+"--partner_phone--"+partner_phone);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			String ss= APIs.FETCHOTHERPROFILEINFO+":"+partner_phone;
			client.get(ss, new JsonHttpResponseHandler() {
				boolean success = false;

				@Override
				public void onFailure(int statusCode, Throwable e,
						JSONObject errorResponse) {
					super.onFailure(statusCode, e, errorResponse);
					System.out.println("errorResponse--> " + errorResponse);
					if (errorResponse != null) {
						try {
							authManager.setMessage(errorResponse
									.getString("message"));
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						//triggerObservers("ProfileInfo False");
					} else {
						//triggerObservers("ProfileInfo Network Error");
					}

				}

				@Override
				public void onSuccess(int statusCode,
						org.apache.http.Header[] headers, JSONObject response) {
					super.onSuccess(statusCode, headers, response);
					try {
						System.out.println("response--> " + response);
						success = response.getBoolean("success");
						if (success) {
							JSONObject jobj = new JSONObject(response.getString("user"));
//							authManager.setGender(jobj.getString("gender"));
//							authManager.setFollower(jobj.getString("follower"));
//							authManager.setFollowing(jobj.getString("following"));
//							authManager.setIsFollowing(jobj.getString("is_following"));
//							authManager.setUserName(jobj.getString("name"));
//							authManager.setUserPic(jobj.getString("user_pic"));
//							authManager.setdOB(jobj.getString("dob"));
							// triggerObservers("ProfileInfo True");
							getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

			});

		}
	

	public void getRelationShips(String phone, String usertoken) {
		// TODO Auto-generated method stub
			JSONObject userInputDetails = new JSONObject();
			try {
				userInputDetails.put("phone_no", phone);
				userInputDetails.put("user_token", usertoken);

				client = new AsyncHttpClient();
				se = new StringEntity(userInputDetails.toString());
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
				Log.e("suserInputDetailse--> ", ""+ userInputDetails);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			client.post(null, APIs.GETRELATIONSHIPS, se, "application/json",
					new JsonHttpResponseHandler() {
						@Override
						public void onFailure(int statusCode, Throwable e,
								JSONObject errorResponse) {
							super.onFailure(statusCode, e, errorResponse);
							System.out.println("errorResponse--> " + errorResponse);
							if (errorResponse != null) {
//								try {
//									
//								} catch (JSONException e1) {
//									// TODO Auto-generated catch block
//									e1.printStackTrace();
//								}
							//	triggerObservers("GetrelationShips False");
							} else {
							//	triggerObservers("GetrelationShips Network Error");
							}
						}

						@Override
						public void onSuccess(int statusCode,
								org.apache.http.Header[] headers,
								JSONObject response) {
							super.onSuccess(statusCode, headers, response);
							boolean state = false;
							try {
								System.out.println("response--> " + response);
								state = response.getBoolean("success");
								if (state) {
									
								//	triggerObservers("GetrelationShips True");
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

					});
		}
	
	
	
}
