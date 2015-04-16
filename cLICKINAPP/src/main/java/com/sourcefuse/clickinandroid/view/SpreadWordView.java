package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.bean.ContactBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.FetchContactFromPhone;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.SpreadWordAdapter;
import com.sourcefuse.clickinapp.R;

import java.util.ListIterator;

import de.greenrobot.event.EventBus;

/**
 * Created by mukesh on 22/4/14.
 */
public class SpreadWordView extends Activity implements OnClickListener {

    public boolean fromProfile = false;
    private Button phonebook, facebook;
    private AuthManager authManager;
    private ProfileManager profilemanager;
    //Methods for Facebook
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //code- to handle uncaught exception
        if (Utils.mStartExceptionTrack)
            Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

        setContentView(R.layout.view_spread_word);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        phonebook = (Button) findViewById(R.id.btn_phb);
        facebook = (Button) findViewById(R.id.btn_fb);

        findViewById(R.id.btn_invite).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);

        phonebook.setOnClickListener(this);
        facebook.setOnClickListener(this);


        AuthManager authManager = ModelManager.getInstance().getAuthorizationManager();
        //  EventBus.getDefault().register(this);
        Utils.groupSms.clear();
        //Utils.launchBarDialog(this);
        //  new FetchContactFromPhone(SpreadWordView.this).getClickerList(authManager.getPhoneNo(),authManager.getUsrToken(),1);
        //   setlist();
        profilemanager = ModelManager.getInstance().getProfileManager();

        if (profilemanager.spreadTheWorldList != null && profilemanager.spreadTheWorldList.size() != 0) {
            for (ContactBean temp : profilemanager.spreadTheWorldList) {
                temp.setChecked(false);
            }
            setlist();
        } else {
            Utils.launchBarDialog(this);
            new LoadContacts().execute();
            //  new FetchContactFromPhone(this).getClickerList(authManager.getPhoneNo(), authManager.getUsrToken(), 1);
        }


        //akshit code starts
        fromProfile = getIntent().getExtras().getBoolean("fromProfile");
        if (fromProfile) {
            findViewById(R.id.btn_next).setVisibility(View.GONE);
            findViewById(R.id.btn_back).setVisibility(View.GONE);
        } else {
            findViewById(R.id.btn_next).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_back).setVisibility(View.VISIBLE);

        }

//ends
    }

    public void setlist() {
        ProfileManager profilemanager = ModelManager.getInstance().getProfileManager();
        ListView listView = (ListView) findViewById(R.id.list_current_clickers);
        SpreadWordAdapter adapter = new SpreadWordAdapter(SpreadWordView.this, R.layout.row_invitefriend, profilemanager.spreadTheWorldList);
        listView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.top_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  Utils.groupSms.clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!(EventBus.getDefault().isRegistered(this)))
            EventBus.getDefault().register(this);
    }

    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_phb:
                phonebook.setBackgroundResource(R.drawable.c_phonebook_pink);
                facebook.setBackgroundResource(R.drawable.c_fb_grey);
                break;
            case R.id.btn_fb:
                // phonebook.setBackgroundResource(R.drawable.c_phonebook_grey);
                // facebook.setBackgroundResource(R.drawable.c_fb_pink);
                Utils.launchBarDialog(SpreadWordView.this);
                if (Utils.isConnectingToInternet(SpreadWordView.this)) {
                    Session session = Session.getActiveSession();

                    if (session == null) {
                        if (session == null) {
                            session = new Session(this);
                        }
                        Session.setActiveSession(session);
                    }
                    if (!session.isOpened() && !session.isClosed()) {
                        session.openForRead(new Session.OpenRequest(this).setCallback(callback).setPermissions("user_birthday", "basic_info", "email", "user_location"));
                    } else {
                        Session.openActiveSession(this, true, callback);
                    }
                } else {
                    Utils.dismissBarDialog();
                    Utils.fromSignalDialog(this, AlertMessage.connectionError);
                    //  Toast.makeText(getApplicationContext(), "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_back:
                finish();
                overridePendingTransition(0, R.anim.top_out);
                break;
            case R.id.btn_next:
                Utils.launchBarDialog(this);

                //To track through mixPanel.
                //Click To Skip Inviting Friends.
                Utils.trackMixpanel(SpreadWordView.this, "", "", "SignUpSkipInvitingFriends", false);
                //   authManager.getProfileInfo("", authManager.getPhoneNo(), authManager.getUsrToken());
                Intent clickersView = new Intent(SpreadWordView.this, UserProfileView.class);
                clickersView.putExtra("FromSignup", true);
                startActivity(clickersView);
                finish();
                break;
            case R.id.btn_invite:
                if (Utils.groupSms.size() > 0) {


                    StringBuilder uri = new StringBuilder("sms:");
                    ListIterator<String> iterator = Utils.groupSms.listIterator();
                    while (iterator.hasNext()) {
                        String num = iterator.next();
                        uri.append(num);
                        if (iterator.hasNext())
                            uri.append(",");
                    }

                    //To track through mixPanel.
                    //Click To Invite Friends
//                    Utils.trackMixpanel(SpreadWordView.this, "", "" + Utils.groupSms.size(), "FriendsInvited", false);
                    Utils.trackMixpanel_superProperties(SpreadWordView.this,Utils.groupSms.size(),"spreadwordview");
                    /* send sms if not not register */
                 /*  send sms for nexus 5 check build version*/
                 /* prafull code */
                    try {


                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.putExtra("sms_body", Constants.SEND_REQUEST_WITH_SMS_MESSAGE);
                        smsIntent.putExtra(Intent.EXTRA_TEXT, Constants.SEND_REQUEST_WITH_SMS_MESSAGE);
                        smsIntent.putExtra("text", Constants.SEND_REQUEST_WITH_SMS_MESSAGE);
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.setData(Uri.parse(uri.toString()));
                        smsIntent.putExtra("exit_on_sent", true);
                        startActivity(smsIntent);


                    } catch (Exception e) {
                        try {
                            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(SpreadWordView.this); //Need to change the build to API 19
                            Intent sendIntent = new Intent(Intent.ACTION_SEND);
                            sendIntent.setType("text/plain");

                            sendIntent.putExtra("address", uri.toString());
                            sendIntent.putExtra(Intent.EXTRA_TEXT, Constants.SEND_REQUEST_WITH_SMS_MESSAGE);
                            sendIntent.putExtra(Intent.ACTION_ATTACH_DATA, Uri.parse(uri.toString()));
                            if (defaultSmsPackageName != null)//Can be null in case that there is no default, then the user would be able to choose any app that support this intent.
                            {
                                sendIntent.setPackage(defaultSmsPackageName);
                            }
                            sendIntent.putExtra("exit_on_sent", true);
                            startActivity(sendIntent);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }


                } else {
                    Utils.fromSignalDialog(this, AlertMessage.GROUPSMSMSG);
                    //Utils.showAlert(SpreadWordView.this, AlertMessage.GROUPSMSMSG);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            super.onActivityResult(requestCode, resultCode, data);
            try {
                Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
            } catch (Exception e) {
            }
        } catch (Exception e) {
        } catch (Error e) {
        }
        Utils.dismissBarDialog();
    }

    private void onSessionStateChange(Session session, SessionState state,
                                      Exception exception) {
        if (state.isOpened()) {
            final String access_Token = session.getAccessToken();
            sendRequestDialogForFriendList();
        } else if (state.isClosed()) {
            Utils.dismissBarDialog();

        }
    }


    private void sendRequestDialogForFriendList() {
        try {
            AuthManager authManager = ModelManager.getInstance().getAuthorizationManager();
            Bundle params = new Bundle();
            params.putString("message", authManager.getUserName() + AlertMessage.SREADTHEWORDMSGON);
            WebDialog requestsDialog = (
                    new WebDialog.RequestsDialogBuilder(SpreadWordView.this,
                            Session.getActiveSession(),
                            params))
                    .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                                               @Override
                                               public void onComplete(Bundle values,
                                                                      FacebookException error) {
                                                   try {
                                                       final String requestId = values.getString("request");
                                                       if (requestId != null) {
                                                           Utils.dismissBarDialog();
                                                       } else {
                                                           Utils.dismissBarDialog();
                                                       }
                                                   } catch (Exception e) {

                                                   }

                                                   phonebook.setBackgroundResource(R.drawable.c_phonebook_pink);
                                                   facebook.setBackgroundResource(R.drawable.c_fb_grey);
                                                   Utils.dismissBarDialog();
                                               }

                                           }
                    )
                    .

                            build();

            requestsDialog.show();
        } catch (Exception e) {
        }
    }


    public void onEventMainThread(String message) {
        if (message.equalsIgnoreCase("CheckFriend True")) {
            Utils.dismissBarDialog();
            setlist();
        } else if (message.equalsIgnoreCase("CheckFriend False")) {
            Utils.dismissBarDialog();
            //  Utils.showAlert(this,authManager.getMessage());
            //   Utils.fromSignalDialog(this, authManager.getMessage());

        } else if (message.equalsIgnoreCase("CheckFriend Network Error")) {
            Utils.dismissBarDialog();
            //    Utils.showAlert(this, AlertMessage.connectionError);
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
        }
    }


    private class LoadContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            new FetchContactFromPhone(SpreadWordView.this).readContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            if (authManager == null)
                authManager = ModelManager.getInstance().getAuthorizationManager();
            new FetchContactFromPhone(SpreadWordView.this).getClickerList(authManager.getPhoneNo(), authManager.getUsrToken(), 1);
        }
    }

}
