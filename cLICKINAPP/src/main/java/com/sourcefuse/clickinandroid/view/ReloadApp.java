package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
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
import com.sourcefuse.clickinandroid.services.MyQbChatService;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import java.sql.SQLException;

import de.greenrobot.event.EventBus;

/**
 * Created by prafull on 4/2/15.
 */
public class ReloadApp extends Activity {

    String pwd, deviceId;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utils.launchBarDialog(this);


        ClickinDbHelper dbHelper = new ClickinDbHelper(this);
        try {
            dbHelper.clearDB();  //clear db one applicatoin start from crashing
        } catch (SQLException e) {
            e.printStackTrace();
        }


        Log.e("in Reload app------>", "in Reload app------>");
        extras = getIntent().getExtras();

        if (!Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().getUserId())) {  // process value if userid is not null
            Log.e("case 1---->", "case 1---->");
            mProfile = true;
            mRelation = true;
            processvalue();
        } else {  // else signin again to get value as if needed.
            setContentView(R.layout.view_splash);
            Log.e("case 2---->", "case 2---->");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String myPhone = preferences.getString("myPhoneNo", null);
            pwd = preferences.getString("pwd", null);
            deviceId = preferences.getString("DeviceId", null);
            ModelManager.getInstance().getAuthorizationManager().signIn(myPhone, pwd, deviceId, Constants.DEVICETYPE);
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

    public void processvalue() {


        Intent data = new Intent();
        data.putExtra("isChangeInList", true);

        data.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (mProfile && mRelation) {
            if (extras.containsKey("Tp")) {

                Log.e("value of tp---->",""+extras.getString("Tp"));
                if (extras.getString("Tp").equalsIgnoreCase("CR") ||
                        extras.getString("Tp").equalsIgnoreCase("CRA") || extras.getString("Tp").equalsIgnoreCase("RV") ||
                        extras.getString("Tp").equalsIgnoreCase("CRR")
                        ) {

                    Log.e("user profile---------", "user profile---------");
                    data.setClass(getApplicationContext(), UserProfileView.class);
                    data.putExtra("FromSignup", true);

                } else if (extras.getString("Tp").equalsIgnoreCase("FR")) {  // case follow request

                    data.setClass(getApplicationContext(), FollowerList.class);
                    data.putExtra("FromOwnProfile", true);
                    Log.e("FollowerList---------", "FollowerList---------");

                } else if (extras.getString("Tp").equalsIgnoreCase("clk")) {


                    data.setClass(getApplicationContext(), ChatRecordView.class);
                    String mPartnerId = extras.getString("pid");
                    Log.e("ChatRecordView---------", "ChatRecordView---------");
                    putChatData(data, mPartnerId);


                } else if (extras.getString("Tp").equalsIgnoreCase("chat")) {

                    data.setClass(getApplicationContext(), ChatRecordView.class);
                    String mPartnerId = extras.getString("pid");

                    putChatData(data, mPartnerId);
                    Log.e("ChatRecordView---------", "ChatRecordView---------");
                } else if (extras.getString("Tp").equalsIgnoreCase("RD")) {

                    data.setClass(getApplicationContext(), UserProfileView.class);
                    data.putExtra("FromSignup", true);

                    Log.e("UserProfileView---------", "UserProfileView---------");
                } else if (extras.getString("Tp").equalsIgnoreCase("media")) {  // case when share media and send media

                    String mPartnerId = extras.getString("pid");
                    putChatData(data, mPartnerId);
                    data.setClass(getApplicationContext(), ChatRecordView.class);
                    Log.e("ChatRecordView---------", "ChatRecordView---------");
                } else if (extras.getString("Tp").equalsIgnoreCase("shr")) //case for share
                {

                    data.setClass(getApplicationContext(), FeedView.class);
                    Log.e("FeedView---------", "FeedView---------");

                } else if (extras.getString("Tp").equalsIgnoreCase("Upp")) //case for Profile Update
                {

                    data.setClass(getApplicationContext(), JumpOtherProfileView.class);
                    data.putExtra("FromOwnProfile", true);
                    data.putExtra("phNumber", extras.getString("phone_no"));




                    Log.e("JumpOtherProfileView---------", "JumpOtherProfileView---------");
                    PicassoManager.setPicasso(getApplicationContext());
                    PicassoManager.clearCache();

                } else if (extras.getString("Tp").equalsIgnoreCase("card")) //case for card
                // case when card accepted
                // case when card rejected
                // case when counter card
                // case when want to share card
                {

                    String mPartnerId = extras.getString("pid");
                    putChatData(data, mPartnerId);
                    data.setClass(getApplicationContext(), ChatRecordView.class);
                    Log.e("ChatRecordView---------", "ChatRecordView---------");
                } else if (extras.getString("Tp").equalsIgnoreCase("str") || extras.getString("Tp").equalsIgnoreCase("cmt")
                        || extras.getString("Tp").equalsIgnoreCase("Rpt")) //case for feed star
                {

                    data.setClass(getApplicationContext(), PostView.class);
                    data.putExtra("feedId", extras.getString("Nid"));
                    Log.e("PostView---------", "PostView---------");

                }
            }
            startActivity(data);
            finish();
            Utils.dismissBarDialog();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        extras = intent.getExtras();

        if (!Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().getUserId())) {// process value if userid is not null
            Log.e("case 1---->", "case 1---->");
            mProfile = true;
            mRelation = true;
            processvalue();
        } else {// else signin again to get value as if needed.
            Log.e("case 2---->", "case 2---->");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String myPhone = preferences.getString("myPhoneNo", null);
            pwd = preferences.getString("pwd", null);
            deviceId = preferences.getString("DeviceId", null);
            ModelManager.getInstance().getAuthorizationManager().signIn(myPhone, pwd, deviceId, Constants.DEVICETYPE);
        }
    }

    public void putChatData(Intent mIntent, String mPartnerId) {


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


    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    boolean mProfile = false, mRelation = false;

    public void onEventMainThread(String message) {

        if (message.equalsIgnoreCase("SignIn False") || message.equalsIgnoreCase("ProfileInfo False") ||
                message.equalsIgnoreCase("ProfileInfo Network Error") ||
                message.equalsIgnoreCase("GetRelationShips False") ||
                message.equalsIgnoreCase("GetRelationShips Network Error")) {
            Log.e("sign in false----->", "sign in false----->");
        } else if (message.equalsIgnoreCase("SignIn True")) {

            ModelManager.getInstance().getAuthorizationManager().getProfileInfo("", ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                    ModelManager.getInstance().getAuthorizationManager().getUsrToken());
            ModelManager.getInstance().getRelationManager().getRelationShips(ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                    ModelManager.getInstance().getAuthorizationManager().getUsrToken());
        }
        if (message.equalsIgnoreCase("ProfileInfo True")) {
            mProfile = true;
            processvalue();
        }
        if (message.equalsIgnoreCase("GetRelationShips True")) {
            mRelation = true;
            Intent i = new Intent(this, MyQbChatService.class);  // start service again
            startService(i);
            processvalue();
        }
    }
}
