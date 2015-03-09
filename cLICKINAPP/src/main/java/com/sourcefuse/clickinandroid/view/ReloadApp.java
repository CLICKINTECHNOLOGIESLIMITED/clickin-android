package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sourcefuse.clickinandroid.dbhelper.ClickinDbHelper;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.bean.GetrelationshipsBean;
import com.sourcefuse.clickinandroid.services.MyQbChatService;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import java.io.File;
import java.sql.SQLException;

import de.greenrobot.event.EventBus;

/**
 * Created by prafull on 4/2/15.
 */
public class ReloadApp extends Activity {

    int notf_type = -1;
    String mPartnerId = null;
    boolean RELOAD_APP=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //code- to handle uncaught exception
        if (Utils.mStartExceptionTrack)
            Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));
        //  requestWindowFeature(Window.FEATURE_NO_TITLE);


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
           RELOAD_APP=true;

            setContentView(R.layout.view_splash);
            Utils.launchBarDialog(this);


            EventBus.getDefault().register(this); //should register before hitting webservice

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ReloadApp.this);
            String myPhone = preferences.getString("myPhoneNo", null);
            String pwd = preferences.getString("pwd", null);
            String deviceId = preferences.getString("DeviceId", null);
            if (!Utils.isEmptyString(myPhone) && !Utils.isEmptyString(pwd) && !Utils.isEmptyString(deviceId)) {
                ModelManager.getInstance().getAuthorizationManager().signIn(myPhone, pwd, deviceId, Constants.DEVICETYPE);
                ClickinDbHelper dbHelper = new ClickinDbHelper(ReloadApp.this);
                try {
                    dbHelper.clearDB();  //clear db one application start from crashing
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }else {
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

    }



    public void processValue() {

        Intent intent1 = new Intent();
     //   if(RELOAD_APP)
       //     intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        switch (notf_type) {
            case Constants.USERPROFILE_NOTF:


                intent1.putExtra("isChangeInList", true);
                intent1.setClass(this, UserProfileView.class);
                //   intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                 intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               // intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent1);
                Utils.dismissBarDialog();
                finish();
                break;
            case Constants.CHATRECORDVIEW_NOTF:
                if (mPartnerId != null) {
                    int partnerIndex = getPartnerIndexInList(mPartnerId);
                    if (partnerIndex != -1) {
                        intent1.setAction("UPDATE");
                        intent1.putExtra("partnerIndex", partnerIndex);
                        intent1.setClass(this, ChatRecordView.class);
                         //   intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                      //  intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent1);
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
                intent1.putExtra("FromOwnProfile", true);
                intent1.setClass(getApplicationContext(), FollowerList.class);
                   intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               //  intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent1);
                Utils.dismissBarDialog();
                finish();
                break;
            case Constants.POSTVIEW_NOTF:
                intent1.setClass(getApplicationContext(), PostView.class);
                intent1.putExtra("feedId", getIntent().getExtras().getString("Nid"));
                     intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //   intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                Utils.dismissBarDialog();
                finish();
                break;
            case Constants.FEEDVIEW_NOTF:
                intent1.setClass(getApplicationContext(), FeedView.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //  intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                Utils.dismissBarDialog();
                finish();
                break;
            case Constants.JUMPOTHERPROFILEVIEW_NOTF:

                if (getIntent().getStringExtra("Tp").equalsIgnoreCase("Upp")) {
                    deletePhoto(getIntent().getStringExtra("phone_no"));

                }

                intent1.setClass(getApplicationContext(), JumpOtherProfileView.class);
                intent1.putExtra("FromOwnProfile", true);
                intent1.putExtra("phNumber", getIntent().getExtras().getString("phone_no"));
                   intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                Utils.dismissBarDialog();
                finish();
                break;

            default:
                intent1.setClass(getApplicationContext(), UserProfileView.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                Utils.dismissBarDialog();
                finish();

        }
        RELOAD_APP=false;

    }
    public void deletePhoto(String mPhoneNo) {
        String RelationId = "";
        for (GetrelationshipsBean mAcceptList : ModelManager.getInstance().getRelationManager().acceptedList) {
            if (mPhoneNo.equalsIgnoreCase(mAcceptList.getPhoneNo())) {
                RelationId = mAcceptList.getRelationshipId();
            }
        }

        if (!Utils.isEmptyString(RelationId)) {
            String mPath = Utils.mImagePath + RelationId + ".jpg";
            Uri uri = Utils.getImageContentUri(getApplicationContext(), new File(mPath));
            if (!Utils.isEmptyString("" + uri))
                getContentResolver().delete(uri, null, null);
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


  /*  public void putChatData(intent1 mIntent, String mPartnerId) {


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
        }*/




    @Override
    protected void onStop() {
        super.onStop();

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

    protected void onDestroy(){
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this))
        EventBus.getDefault().unregister(this);
    }
}
