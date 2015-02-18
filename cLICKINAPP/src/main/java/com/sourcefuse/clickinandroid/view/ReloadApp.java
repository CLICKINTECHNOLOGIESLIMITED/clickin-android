package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;

import com.sourcefuse.clickinandroid.dbhelper.ClickinDbHelper;
import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.PicassoManager;
import com.sourcefuse.clickinandroid.model.bean.GetrelationshipsBean;
import com.sourcefuse.clickinandroid.services.MyQbChatService;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import java.sql.SQLException;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by prafull on 4/2/15.
 */
public class ReloadApp extends Activity {

    int notf_type = -1;
    String mPartnerId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //code- to handle uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));
        //  requestWindowFeature(Window.FEATURE_NO_TITLE);


        Log.e("in Reload app------>", "in Reload app------>");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("NOTIFICATION_TYPE"))
                notf_type = extras.getInt("NOTIFICATION_TYPE");
            if (extras.containsKey("pid"))
                mPartnerId = extras.getString("pid");

        }


        if (!Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().getUserId())) {
            // if user id is not null, means app is running properly

            processValue();
        } else {  // else reload the app

            setContentView(R.layout.view_splash);
            Utils.launchBarDialog(this);


            ClickinDbHelper dbHelper = new ClickinDbHelper(ReloadApp.this);
            try {
                dbHelper.clearDB();  //clear db one application start from crashing
            } catch (SQLException e) {
                e.printStackTrace();
            }


            Log.e("case 2---->", "case 2---->");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ReloadApp.this);
            String myPhone = preferences.getString("myPhoneNo", null);
            String pwd = preferences.getString("pwd", null);
            String deviceId = preferences.getString("DeviceId", null);
            if (!Utils.isEmptyString(myPhone) && !Utils.isEmptyString(pwd) && !Utils.isEmptyString(deviceId))
                ModelManager.getInstance().getAuthorizationManager().signIn(myPhone, pwd, deviceId, Constants.DEVICETYPE);
            else {
                //means no way to reload the app, we have to ask user to sign in again
                Intent intent = new Intent(ReloadApp.this, SplashView.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().unregister(this);
        }
        EventBus.getDefault().register(this);
    }

    public void processValue() {

        Intent intent = new Intent();
        switch (notf_type) {
            case Constants.USERPROFILE_NOTF:
                intent.putExtra("isChangeInList", true);
                intent.setClass(getApplicationContext(), UserProfileView.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);
                Utils.dismissBarDialog();
                finish();
                break;
            case Constants.CHATRECORDVIEW_NOTF:
                if (mPartnerId != null) {
                    int partnerIndex = getPartnerIndexInList(mPartnerId);
                    if (partnerIndex != -1) {
                        intent.setAction("UPDATE");
                        intent.putExtra("partnerIndex", partnerIndex);
                        intent.setClass(getApplicationContext(), ChatRecordView.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        startActivity(intent);
                        Utils.dismissBarDialog();
                        finish();
                    } else {
                        if (!Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().getPhoneNo()))
                            ModelManager.getInstance().getRelationManager().getRelationShips(
                                    ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                                    ModelManager.getInstance().getAuthorizationManager().getUsrToken());
                    }
                }
                break;
            case Constants.FOLLOWER_FOLLOWING_NOTF:
                intent.putExtra("FromOwnProfile", true);
                intent.setClass(getApplicationContext(), FollowerList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                Utils.dismissBarDialog();
                finish();
                break;
            case Constants.POSTVIEW_NOTF:
                intent.setClass(getApplicationContext(), PostView.class);
                intent.putExtra("feedId", getIntent().getExtras().getString("Nid"));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                Utils.dismissBarDialog();
                finish();
                break;
            case Constants.FEEDVIEW_NOTF:
                intent.setClass(getApplicationContext(), FeedView.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                Utils.dismissBarDialog();
                finish();
                break;
            case Constants.JUMPOTHERPROFILEVIEW_NOTF:
                intent.setClass(getApplicationContext(), JumpOtherProfileView.class);
                intent.putExtra("FromOwnProfile", true);
                intent.putExtra("phNumber", getIntent().getExtras().getString("phone_no"));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                Utils.dismissBarDialog();
                finish();
                break;

            default:

        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.e("case 2 on new Intent---->", "case 2 on new Intent---->");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ReloadApp.this);
        String myPhone = preferences.getString("myPhoneNo", null);
        String pwd = preferences.getString("pwd", null);
        String deviceId = preferences.getString("DeviceId", null);
        if (!Utils.isEmptyString(myPhone) && !Utils.isEmptyString(pwd) && !Utils.isEmptyString(deviceId))
            ModelManager.getInstance().getAuthorizationManager().signIn(myPhone, pwd, deviceId, Constants.DEVICETYPE);
        else {
            Intent intent1 = new Intent(ReloadApp.this, SplashView.class);
            startActivity(intent1);
            finish();
        }

    }


    //function to find index in accepted list
    private int getPartnerIndexInList(String partnerId) {
        int index = -1;
        for (GetrelationshipsBean temp : ModelManager.getInstance().getRelationManager().acceptedList) {
            index++;
            if (temp.getPartner_id().equalsIgnoreCase(partnerId)) {
                return index;
            }
        }
        return index;
    }

  /*  public void putChatData(Intent mIntent, String mPartnerId) {


        for (int i = 0; i < ModelManager.getInstance().getRelationManager().acceptedList.size(); i++) {

            if (ModelManager.getInstance().getRelationManager().acceptedList.get(i).getPartner_id().equalsIgnoreCase(mPartnerId)) {
                mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mIntent.setAction("UPDATE");
                mIntent.putExtra("quickId", ModelManager.getInstance().getRelationManager().acceptedList.get(i).getPartnerQBId());

                mIntent.putExtra("partnerPic", ModelManager.getInstance().getRelationManager().acceptedList.get(i).getPartnerPic());


                mIntent.putExtra("partnerName", ModelManager.getInstance().getRelationManager().acceptedList.get(i).getPartnerName());


                mIntent.putExtra("rId", ModelManager.getInstance().getRelationManager().acceptedList.get(i).getRelationshipId());


                mIntent.putExtra("partnerId", ModelManager.getInstance().getRelationManager().acceptedList.get(i).getPartner_id());


                mIntent.putExtra("myClicks", ModelManager.getInstance().getRelationManager().acceptedList.get(i).getUserClicks());


                mIntent.putExtra("userClicks", ModelManager.getInstance().getRelationManager().acceptedList.get(i).getClicks());


                mIntent.putExtra("partnerPh", ModelManager.getInstance().getRelationManager().acceptedList.get(i).getPhoneNo());


                mIntent.putExtra("relationListIndex", i);


                ChatManager chatManager = ModelManager.getInstance().getChatManager();
                chatManager.setrelationshipId(ModelManager.getInstance().getRelationManager().acceptedList.get(i).getRelationshipId());


            }
        }


    }*/

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(String message) {

        if (message.equalsIgnoreCase("SignIn False") || message.equalsIgnoreCase("ProfileInfo False") ||
                message.equalsIgnoreCase("ProfileInfo Network Error") ||
                message.equalsIgnoreCase("GetRelationShips False") ||
                message.equalsIgnoreCase("GetRelationShips Network Error")) {

            Utils.fromSignalDialogSplsh(this, AlertMessage.connectionError); // exit from application as it come false
        } else if (message.equalsIgnoreCase("SignIn True")) {

            ModelManager.getInstance().getAuthorizationManager().getProfileInfo("", ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                    ModelManager.getInstance().getAuthorizationManager().getUsrToken());

        }
        if (message.equalsIgnoreCase("ProfileInfo True")) {
            ModelManager.getInstance().getRelationManager().getRelationShips(ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                    ModelManager.getInstance().getAuthorizationManager().getUsrToken());
        }
        if (message.equalsIgnoreCase("GetRelationShips True")) {
            //        mRelation = true;
            Intent i = new Intent(this, MyQbChatService.class);  // start service again
            startService(i);
            processValue();
        }
    }
}
