package com.sourcefuse.clickinandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sourcefuse.clickinandroid.model.ModelManager;

public class MyPreference {

    public String token = "token";
    public String myPhoneNo = "myPhoneNo";


    private SharedPreferences mPreferences;
    private SharedPreferences.Editor editor;


    public MyPreference(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }



    public String getmyPhoneNo() {
        return mPreferences.getString(this.myPhoneNo, "");
    }

    public void setmyPhoneNo(String myPhoneNo) {
        editor = mPreferences.edit();
        editor.putString(this.myPhoneNo, myPhoneNo);
        editor.commit();
    }




    public String getToken() {
        return mPreferences.getString(this.token, "");
    }

    public void setToken(String token) {
        editor = mPreferences.edit();
        editor.putString(this.token, token);
        editor.commit();
    }



    public boolean isLogin() {
        if(!Utils.isEmptyString(getToken()) && ModelManager.getInstanceModelManager()){
            return true;
        }else{
            return false;
        }

    }

    // execute at signup time.
    public void clearAllPreference() {
        setToken("");
    }


}
