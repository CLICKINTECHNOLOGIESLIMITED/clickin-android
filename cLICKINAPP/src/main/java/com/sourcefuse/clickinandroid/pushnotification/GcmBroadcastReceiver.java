package com.sourcefuse.clickinandroid.pushnotification;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.sourcefuse.clickinandroid.model.SettingManager;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


        if (SettingManager.mNotification_Enable) {

            ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
        }
    }
}