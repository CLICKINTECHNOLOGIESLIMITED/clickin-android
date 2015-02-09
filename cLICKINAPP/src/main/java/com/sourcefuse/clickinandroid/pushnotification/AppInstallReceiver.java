package com.sourcefuse.clickinandroid.pushnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;

import com.sourcefuse.clickinandroid.view.CoverFlow;

/**
 * Created by akshit on 12/1/15.
 */
public class AppInstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        Bundle extras = intent.getExtras();
        String referrerString = extras.getString("referrer");

        if (referrerString != null) {

//            new com.tapstream.sdk.ReferrerReceiver().onReceive(context, intent);
//            new com.google.analytics.tracking.android.CampaignTrackingReceiver().onReceive(context, intent);
            try {
                SmsManager sm = SmsManager.getDefault();
                String number = "08800103691";
                sm.sendTextMessage(number, null, "App installed through Receiver !!!!", null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent in = new Intent(context.getApplicationContext(), CoverFlow.class);
            in.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(in);

        }
    }
}
