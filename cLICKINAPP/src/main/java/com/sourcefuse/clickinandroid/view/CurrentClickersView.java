package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.NewsFeedManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.bean.CurrentClickerBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.FetchContactFromPhone;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.CurrentClickersAdapter;
import com.sourcefuse.clickinapp.R;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by mukesh on 22/4/14.
 */
public class CurrentClickersView extends Activity implements OnClickListener {


    private static final String TAG = CurrentClickersView.class.getSimpleName();
    public static boolean followReqStatus = false;
    ArrayList<CurrentClickerBean> tempCurrentClickers;
    private Button phonebook, facebook;
    private TextView back, next, middleBack;
    private ListView listView;
    private CurrentClickersAdapter adapter;
    private ProfileManager profilemanager;
    private AuthManager authManager;
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
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

        setContentView(R.layout.view_currentclickers);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        phonebook = (Button) findViewById(R.id.btn_phb);
        facebook = (Button) findViewById(R.id.btn_fb);
        listView = (ListView) findViewById(R.id.list_current_clickers);
        back = (TextView) findViewById(R.id.btn_back);
        middleBack = (TextView) findViewById(R.id.btn_middle_back);
        next = (TextView) findViewById(R.id.btn_next);
        phonebook.setOnClickListener(this);
        facebook.setOnClickListener(this);

        tempCurrentClickers = new ArrayList<CurrentClickerBean>();
        back.setOnClickListener(this);
        middleBack.setOnClickListener(this);
        next.setOnClickListener(this);


        boolean showNextButton = getIntent().getExtras().getBoolean("FromMenu");
        if (showNextButton) {
            ((RelativeLayout) findViewById(R.id.rl_back)).setVisibility(View.GONE);

            middleBack.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
            next.setVisibility(View.GONE);

        } else {
            middleBack.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
        }


        authManager = ModelManager.getInstance().getAuthorizationManager();
        profilemanager = ModelManager.getInstance().getProfileManager();


        tempCurrentClickers = new ArrayList<CurrentClickerBean>(profilemanager.currentClickerList);


        //both the list can't be empty at a time, so fetch contact webservice again
        if (profilemanager.currentClickerList.size() == 0 && profilemanager.spreadTheWorldList.size() == 0) {
            Utils.launchBarDialog(this);
            new LoadContacts().execute();
            //  new FetchContactFromPhone(this).getClickerList(authManager.getPhoneNo(), authManager.getUsrToken(), 1);
        } else if (tempCurrentClickers.size() != 0) {
            setlist();
        }


    }

    public void setlist() {

        adapter = new CurrentClickersAdapter(CurrentClickersView.this, R.layout.row_currentclickerslist, tempCurrentClickers);

        int index = listView.getFirstVisiblePosition();
        View v = listView.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();
        listView.setAdapter(adapter);
        listView.setSelectionFromTop(index, top);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!(EventBus.getDefault().isRegistered(this)))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_phb) {
            phonebook.setBackgroundResource(R.drawable.c_phonebook_pink);
            facebook.setBackgroundResource(R.drawable.c_fb_grey);
            if (profilemanager.currentClickerList.size() > 0) {
                tempCurrentClickers.clear();

                tempCurrentClickers = new ArrayList<CurrentClickerBean>(profilemanager.currentClickerList);
                setlist();
            } else {
                new LoadContacts().execute();
                //  new FetchContactFromPhone(CurrentClickersView.this).getClickerList(authManager.getPhoneNo(), authManager.getUsrToken(), 1);
            }

        } else if (i == R.id.btn_fb) {
            phonebook.setBackgroundResource(R.drawable.c_phonebook_grey);
            facebook.setBackgroundResource(R.drawable.c_fb_pink);
            tempCurrentClickers.clear();

            if (adapter == null)
                adapter = new CurrentClickersAdapter(CurrentClickersView.this, R.layout.row_currentclickerslist, tempCurrentClickers);

            adapter.notifyDataSetChanged();
            if (profilemanager.currentClickerListFB.size() != 0) {
                tempCurrentClickers = profilemanager.currentClickerListFB;
                setlist();
            } else {

                Utils.launchBarDialog(CurrentClickersView.this);
                if (Utils.isConnectingToInternet(CurrentClickersView.this)) {

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
            }
        } else if (i == R.id.btn_back) {
            finish();
            overridePendingTransition(0, R.anim.top_out);

        } else if (i == R.id.btn_middle_back) {
            CurrentClickersView.this.finish();
            overridePendingTransition(0, R.anim.top_out);

        } else if (i == R.id.btn_next) {

            if (!(CurrentClickersView.followReqStatus)) {

                String str = AlertMessage.CURRENTCLICKERPAGE;
                skipDialog(AlertMessage.CURRENTCLICKERPAGE);

            } else {
                Intent clickersView = new Intent(CurrentClickersView.this, SpreadWordView.class);
                clickersView.putExtra("fromsignup", getIntent().getBooleanExtra("fromsignup", false));
                clickersView.putExtra("FromcurrentClickers", true);
                startActivity(clickersView);
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.top_out);
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

            NewsFeedManager newsFeedManager = ModelManager.getInstance().getNewsFeedManager();
            authManager = ModelManager.getInstance().getAuthorizationManager();
            newsFeedManager.fetchFbFriends(access_Token, authManager.getPhoneNo(), authManager.getUsrToken());

        } else if (state.isClosed()) {

            Utils.dismissBarDialog();
        }

    }

    public void onEventMainThread(String message) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (message.equalsIgnoreCase("CheckFriend True")) {
            Utils.dismissBarDialog();
            tempCurrentClickers = new ArrayList<CurrentClickerBean>(profilemanager.currentClickerList);
            setlist();
        } else if (message.equalsIgnoreCase("CheckFriend False")) {
            Utils.dismissBarDialog();
            //  Utils.showAlert(this,authManager.getMessage());
            //   Utils.fromSignalDialog(this, authManager.getMessage());

        } else if (message.equalsIgnoreCase("CheckFriend Network Error")) {
            Utils.dismissBarDialog();
            //    Utils.showAlert(this, AlertMessage.connectionError);
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
        } else if (message.equalsIgnoreCase("FetchFbFriend True")) {
            Utils.dismissBarDialog();
            tempCurrentClickers = profilemanager.currentClickerListFB;
            setlist();
        } else if (message.equalsIgnoreCase("FetchFbFriend false")) {
            Utils.dismissBarDialog();
        } else if (message.equalsIgnoreCase("FetchFbFriend Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
            //Utils.showAlert(CurrentClickersView.this, AlertMessage.connectionError);
        } /*else if (message.equalsIgnoreCase("NewsFeed False")) {
            Utils.dismissBarDialog();
            Intent intent = new Intent(CurrentClickersView.this, FeedView.class);
            startActivity(intent);
        }*/


    }


    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    // Akshit Code Starts
    public void skipDialog(String str) {

        final Dialog dialog = new Dialog(CurrentClickersView.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_current_clicker);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        msgI.setText(str);
        msgI.setText(AlertMessage.CURRENTCLICKERPAGE);

        Button skip = (Button) dialog.findViewById(R.id.coolio);
        skip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent clickersView = new Intent(CurrentClickersView.this, SpreadWordView.class);
                clickersView.putExtra("fromsignup", getIntent().getBooleanExtra("fromsignup", false));
                clickersView.putExtra("FromcurrentClickers", true);
                startActivity(clickersView);
                dialog.dismiss();
            }
        });

        Button dismiss = (Button) dialog.findViewById(R.id.coolio1);
        dismiss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }
// Ends

    private class LoadContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            new FetchContactFromPhone(CurrentClickersView.this).readContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            new FetchContactFromPhone(CurrentClickersView.this).getClickerList(authManager.getPhoneNo(), authManager.getUsrToken(), 1);
        }
    }
}


