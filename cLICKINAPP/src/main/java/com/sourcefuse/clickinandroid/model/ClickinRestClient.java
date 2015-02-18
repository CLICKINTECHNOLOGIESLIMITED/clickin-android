package com.sourcefuse.clickinandroid.model;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sourcefuse.clickinandroid.utils.Utils;

import org.apache.http.entity.StringEntity;

/**
 * Created by monika on 18/2/15.
 */
public class ClickinRestClient {



    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url,AsyncHttpResponseHandler responseHandler) {
        String user_token=ModelManager.getInstance().getAuthorizationManager().getUsrToken();
        String phone_num=ModelManager.getInstance().getAuthorizationManager().getPhoneNo();
        if(!Utils.isEmptyString(user_token) && !Utils.isEmptyString(phone_num)){
            client.addHeader("User-Token", user_token);
            client.addHeader("Phone-No",phone_num);
        }

        client.get(url, responseHandler);

    }

    public static void post(Context context, String url, StringEntity entity, String content_type,AsyncHttpResponseHandler responseHandler) {
        client.post(context,url,entity,"\"application/json\"",responseHandler);
    }

    public static void postWithHeader(Context context, String url, String headers[],StringEntity entity, String content_type,AsyncHttpResponseHandler responseHandler) {

        client.post(context,url,entity,"\"application/json\"",responseHandler);
    }

}
