package com.sourcefuse.clickinandroid.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.bean.ContactBean;
import com.sourcefuse.clickinandroid.model.bean.CurrentClickerBean;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import java.util.Comparator;
import java.util.Iterator;

import de.greenrobot.event.EventBus;

public class FetchContactFromPhone {

      private ContactBean mcantactBean;
      private Context context;
      private static StringEntity se = null;
      private static AsyncHttpClient client;
      private static AuthManager authManager;
      private ProfileManager profilemanager;
      private ArrayList<CurrentClickerBean> clickerArray;
      private CurrentClickerBean clickerList = null;
      private ContactBean contactBean = null;


      public FetchContactFromPhone(Context context) {
            this.context = context;

      }

	/*public void getAllContacts() {
            AsyncTaskRunner runner = new AsyncTaskRunner();
		String sleepTime = "10";
		runner.execute(sleepTime);
	}*/

      public void readContacts() {
            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                                         null, null, null);
            String phone = null;
            String image_uri = "";
            String name = "";

            String mPhone = null;
            String key = "";
            int l = 0;
            Utils.itData.clear();
            if (cur.getCount() > 0) {
                  while (cur.moveToNext()) {
                        //mcantactBean = new ContactBean("","","",true);
                        mcantactBean = new ContactBean();
                        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                        name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                        if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                              Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                              while (pCur.moveToNext()) {
                                    phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    try {
                                          String temp1 = phone.replaceAll("\\s", "");
                                          String temp2 = temp1.replaceAll("-", "");

                                          String temp = temp2.substring(1);

                                          long ph = Long.parseLong(temp);

                                    } catch (Exception e) {
                                         e.printStackTrace();
                                          break;

                                    }

                                    authManager = ModelManager.getInstance().getAuthorizationManager();

                                    //Monika- we need to show exact phone list without adding country , will add country code later on
                                    //But we need to maintain phone number with country code as key
                                    String countryCode = authManager.getCountrycode();

                                    try {


                                          if(phone.contains("-"))
                                          {
                                                phone = phone.replaceAll("-","");
                                          }

                                          if (!(phone.contains("+")))
                                                key = countryCode + phone.replaceAll("\\s", "");
                                          else
                                                key = phone.replaceAll("\\s", "");

                                          mPhone = phone.replaceAll("\\s", "");
                                          mcantactBean.setConName(name);
                                          mcantactBean.setChecked(false);
                                          Log.d("contactNumber", "contactNumber->" + mPhone + " , ");
                                          mcantactBean.setConNumber(mPhone);
                                    }catch (Exception e)
                                    {
                                          e.printStackTrace();

                                    }
						/*l = mPhone.length();
						 if((l-10)>=0){
						 mcantactBean.setConName(name);
                         mcantactBean.setChecked(false);
                             Log.d("contactNumber","contactNumber->"+mPhone+" , ");
						 mcantactBean.setConNumber("+91" + mPhone.substring((l - 10)));
						 }*/

                                    if (image_uri != null) {
                                          try {
                                                // bitmap =
                                                // MediaStore.Images.Media.getBitmap(this.getContentResolver(),Uri.parse(image_uri));
                                                // System.out.println(bitmap);
                                                mcantactBean.setConUri(image_uri);

                                          } catch (Exception e) {

                                                e.printStackTrace();
                                          }
                                    }
                                    Utils.contactMap.put(key, mcantactBean);

                                    Utils.itData.add(mcantactBean);

                              }
                              pCur.close();


                        }
                  }
            }

            Collections.sort(Utils.itData, FetchContactFromPhone.NameComparator);
      }

	/*private class AsyncTaskRunner extends AsyncTask<String, String, String> {
		private String resp;

		@Override
		protected String doInBackground(String... params) {
			publishProgress("Fetching contact"); // Calls onProgressUpdate()
			try {

				readContacts();
				int time = Integer.parseInt(params[0]);
				Thread.sleep(time);
				resp = "Slept for " + time + " milliseconds";
			} catch (InterruptedException e) {
				e.printStackTrace();
				resp = e.getMessage();
			} catch (Exception e) {
				e.printStackTrace();
				resp = e.getMessage();
			}
			return resp;
		}

		@Override
		protected void onPostExecute(String result) {
            Log.e("onPostExecute", "-----> ");
			
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onProgressUpdate(String... text) {
			// finalResult.setText(text[0]);
			Log.e("onProgressUpdate", "-----> " + text[0]);
			//Toast.makeText(context, "" + text[0], Toast.LENGTH_SHORT).show();
		}
	}*/

      public void getClickerList(String phone, String usertoken, final int clickers) {
            // TODO Auto-generated method stub


            readContacts();

            try {
                  client = new AsyncHttpClient();
                  JSONObject userInputDetails = new JSONObject();
                  ArrayList<String> list = new ArrayList<String>();
                  Iterator myiterator = Utils.contactMap.keySet().iterator();
                  String countryCode = authManager.getCountrycode();
                  while (myiterator.hasNext()) {
                        String key1 = (String) myiterator.next();
                        ContactBean value = (ContactBean) Utils.contactMap.get(key1);
                        if (!(key1.contains("+")))
                              key1 = countryCode + key1;
                        list.add(key1);

                  }
                  userInputDetails.put("phone_no", phone);
                  userInputDetails.put("user_token", usertoken);
                  userInputDetails.put("phone_nos", new JSONArray(list));
                  Log.d("", "userInputDetails---> " + userInputDetails);
                  se = new StringEntity(userInputDetails.toString());
                  se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            } catch (Exception e1) {
                  e1.printStackTrace();

            }
            client.post(null, APIs.CHECKREGISTEREDFRIENDS, se, "application/json", new JsonHttpResponseHandler() {
                  boolean success = false;

                  @Override
                  public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        Log.d("", "errorResponse--> " + errorResponse);
                        if (errorResponse != null) {
                              try {
                                    authManager.setMessage(errorResponse.getString("message"));

                              } catch (JSONException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();

                              }
                              EventBus.getDefault().post("CheckFriend False");
                        }

                  }

                  @Override
                  public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                              Log.d("", "Current Clickers response--> " + response);
                              success = response.getBoolean("success");
                              if (success) {
                                    profilemanager = ModelManager.getInstance().getProfileManager();
                                    profilemanager.currClickersPhoneNums.clear();
                                    profilemanager.currentClickerList.clear();
                                    profilemanager.spreadTheWorldList.clear();
                                    JSONArray list = response.getJSONArray("phone_nos");
                                    clickerArray = new ArrayList<CurrentClickerBean>();
                                    for (int i = 0; i < list.length(); i++) {
                                          JSONObject data = list.getJSONObject(i);
                                          int existcode = data.getInt("exists");
                                          if (clickers == existcode) {
                                                ContactBean cb = Utils.contactMap.get(data.getString("phone_no"));
                                                Log.e("ContactBean", "ContactBean" + cb);
                                                if (cb != null) {
                                                      clickerList = new CurrentClickerBean();
                                                      if (data.has("phone_no")) {
                                                            clickerList.setGetClickerPhone(data.getString("phone_no"));
                                                            profilemanager.currClickersPhoneNums.add(data.getString("phone_no"));
                                                      }
                                                      if (data.has("user_pic")) {
                                                            clickerList.setClickerPix(data.getString("user_pic"));
                                                      }
                                                      if (data.has("following")) {
                                                            clickerList.setFollow(data.getInt("following"));
                                                      } else {
                                                            clickerList.setFollow(0);
                                                      }
                                                      clickerList.setName(cb.getConName());
                                                      com.sourcefuse.clickinandroid.utils.Log.e("point 1","point 1");
                                                      profilemanager.currentClickerList.add(clickerList);

                                                }
                                          } else if (existcode == 0) {
                                                ContactBean cb = Utils.contactMap.get(data.getString("phone_no"));
                                                if (cb != null) {
                                                      // contactBean = new ContactBean("","","",true);
                                                      contactBean = new ContactBean();
                                                      contactBean.setConName(cb.getConName());
                                                      contactBean.setConNumber(cb.getConNumber());
                                                      contactBean.setChecked(false);
                                                      contactBean.setConUri(cb.getConUri());
                                                      profilemanager.spreadTheWorldList.add(contactBean);
                                                      com.sourcefuse.clickinandroid.utils.Log.e("point 2","point 2");
                                                }
                                          }
                                    }
                                    Collections.sort(profilemanager.spreadTheWorldList, FetchContactFromPhone.NameComparator);
                                    Collections.sort(profilemanager.currentClickerList, FetchContactFromPhone.CurrentClickersNameComparator);
                                    Collections.sort(profilemanager.currentClickerListFB, FetchContactFromPhone.CurrentClickersNameComparator);

                                    EventBus.getDefault().post("CheckFriend True");
                              }
                        } catch (JSONException e) {
                              e.printStackTrace();

                        }
                  }
            });
      }

      //Monika- to check whether a number registered with clickin or not
      public static void checkNumWithClickInDb(String num) {
            authManager = ModelManager.getInstance().getAuthorizationManager();

            try {

                  client = new AsyncHttpClient();
                  JSONObject userInputDetails = new JSONObject();
                  ArrayList<String> list = new ArrayList<String>();
                  list.add(num);


                  userInputDetails.put("phone_no", authManager.getPhoneNo());
                  userInputDetails.put("user_token", authManager.getUsrToken());
                  userInputDetails.put("phone_nos", new JSONArray(list));
                  //   Log.d("", "userInputDetails---> " + userInputDetails);
                  se = new StringEntity(userInputDetails.toString());
                  se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            } catch (Exception e1) {
                  e1.printStackTrace();

            }

            client.post(null, APIs.CHECKREGISTEREDFRIENDS, se, "application/json", new JsonHttpResponseHandler() {
                  boolean success = false;

                  @Override
                  public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        Log.d("", "errorResponse--> " + errorResponse);
                        if (errorResponse != null) {
                              try {
                                    authManager.setMessage(errorResponse.getString("message"));

                              } catch (JSONException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();

                              }
                              EventBus.getDefault().post("Num Check False");
                        }

                  }

                  @Override
                  public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                              Log.d("", "Current Clickers response--> " + response);
                              success = response.getBoolean("success");
                              String eventBusRes = "Num Not Registered";
                              if (success) {

                                    JSONArray list = response.getJSONArray("phone_nos");

                                    for (int i = 0; i < list.length(); i++) {
                                          JSONObject data = list.getJSONObject(i);
                                          int existcode = data.getInt("exists");
                                          if (existcode == 1) {
                                                eventBusRes = "Num Registered";
                                          } else if (existcode == 0) {
                                                eventBusRes = "Num Not Registered";
                                          }
                                    }

                                    EventBus.getDefault().post(eventBusRes);
                              }
                        } catch (JSONException e) {

                              e.printStackTrace();
                        }
                  }
            });
      }

      //monika -to sort current phonebook and spreadword list
      public static final Comparator<ContactBean> NameComparator = new Comparator<ContactBean>() {


            @Override
            public int compare(ContactBean contactBean, ContactBean contactBean2) {
                  return contactBean.getConName().compareTo(contactBean2.getConName());
            }
      };

      //monika -to sort current clickers list
      public static final Comparator<CurrentClickerBean> CurrentClickersNameComparator = new Comparator<CurrentClickerBean>() {


            @Override
            public int compare(CurrentClickerBean contactBean, CurrentClickerBean contactBean2) {
                  return contactBean.getName().compareTo(contactBean2.getName());
            }
      };


}
