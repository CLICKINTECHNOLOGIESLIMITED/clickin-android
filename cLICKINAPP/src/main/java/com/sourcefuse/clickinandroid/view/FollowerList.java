package com.sourcefuse.clickinandroid.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.FollowerAdapter;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class FollowerList extends ClickInBaseView implements
        View.OnClickListener {
    private static final String TAG = FollowerList.class.getSimpleName();
    public static FollowerAdapter adapter;
    public static boolean fromOwnProfile = false;
    public static boolean mListchangeVariable_flag = false;
    private ListView listView;
    private ProfileManager profManager;
    private AuthManager authManager;
    private TextView profileName;

    /*  to check change in list variable in adapter */
    private RelativeLayout mFollowerListView, mFollowerListEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_followerlist);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        addMenu(true);
        slidemenu.setTouchModeAbove(2);

//code- to handle uncaught exception
        if(Utils.mStartExceptionTrack)
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

        listView = (ListView) findViewById(R.id.list_follower);
        mFollowerListView = (RelativeLayout) findViewById(R.id.rl_followerdata);
        mFollowerListEmpty = (RelativeLayout) findViewById(R.id.rl_empty_follower);
        profileName = (TextView) findViewById(R.id.tv_profile_txt_wer);

        profManager = ModelManager.getInstance().getProfileManager();
        authManager = ModelManager.getInstance().getAuthorizationManager();


        fromOwnProfile = getIntent().getExtras().getBoolean("FromOwnProfile");
        if (fromOwnProfile) {
            //  profileName.setText(authManager.getUserName());
            Utils.launchBarDialog(FollowerList.this);
            profManager.getFollwer("", authManager.getPhoneNo(), authManager.getUsrToken());
        } else {
            profileName.setText("" + getIntent().getStringExtra("name"));
            Utils.launchBarDialog(FollowerList.this);
            profManager.getFollwer(getIntent().getExtras().getString("phoneNo"), authManager.getPhoneNo(), authManager.getUsrToken());
        }

    }

    @Override
    public void onBackPressed() {

        if (mListchangeVariable_flag) {

            Intent intent = new Intent(this, UserProfileView.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("isChangeInList", true);
            startActivity(intent);
            finish();
            overridePendingTransition(0, R.anim.top_out);
        } else {

            finish();
            overridePendingTransition(0, R.anim.top_out);
        }
        super.onBackPressed();


    }

    public void setlist() {

        if (profManager.followers.size() > 0) {
            mFollowerListView.setVisibility(View.VISIBLE);
            mFollowerListEmpty.setVisibility(View.GONE);

            adapter = new FollowerAdapter(this, R.layout.row_follower, profManager.followers);
            int index = listView.getFirstVisiblePosition();
            View v = listView.getChildAt(0);
            int top = (v == null) ? 0 : v.getTop();
            listView.setAdapter(adapter);
            listView.setSelectionFromTop(index, top);
        } else {
            if (fromOwnProfile) {
                mFollowerListEmpty.setVisibility(View.VISIBLE);
                mFollowerListView.setVisibility(View.GONE);
            } else {
                mFollowerListEmpty.setVisibility(View.GONE);
                mFollowerListView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void onEventMainThread(String getMsg) {
        super.onEventMainThread(getMsg);
        Utils.dismissBarDialog();
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (getMsg.equalsIgnoreCase("UnFollowUser true")) {


            adapter.notifyDataSetChanged();
        } else if (getMsg.equalsIgnoreCase("UnFollowUser false")) {

        } else if (getMsg.equalsIgnoreCase("UnFollowUser Network Error")) {
            Utils.fromSignalDialog(FollowerList.this, AlertMessage.connectionError);
        } else if (getMsg.equalsIgnoreCase("FollowUser true")) {

            adapter.notifyDataSetChanged();
        } else if (getMsg.equalsIgnoreCase("FollowUser false")) {

        } else if (getMsg.equalsIgnoreCase("FollowUser Network Error")) {
            Utils.fromSignalDialog(FollowerList.this, AlertMessage.connectionError);
        } else if (getMsg.equalsIgnoreCase("GetFollower True")) {
            Utils.dismissBarDialog();
            setlist();
        } else if (getMsg.equalsIgnoreCase("GetFollower False")) {
            Utils.dismissBarDialog();
        } else if (getMsg.equalsIgnoreCase("GetFollower Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(FollowerList.this, AlertMessage.connectionError);
        } else if (getMsg.equalsIgnoreCase("followUpdateStatus True")) {


            adapter.notifyDataSetChanged();
        } else if (getMsg.equalsIgnoreCase("followUpdateStatus False")) {

        } else if (getMsg.equalsIgnoreCase("followUpdateStatus Network Error")) {
            Utils.fromSignalDialog(FollowerList.this, AlertMessage.connectionError);
        }

    }


}