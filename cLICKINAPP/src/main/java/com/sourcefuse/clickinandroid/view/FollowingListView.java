package com.sourcefuse.clickinandroid.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.FollowingAdapter;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class FollowingListView extends ClickInBaseView implements View.OnClickListener {
    private static final String TAG = FollowingListView.class.getSimpleName();
    public static boolean fromOwnProfile = false;
    public static boolean mchangeinList = false;
    public FollowingAdapter adapter;
    private ListView listView;
    private ProfileManager profManager;
    private AuthManager authManager;
    private TextView profileName;
    private RelativeLayout mFollowingListView, mFollowingListEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_followinglist);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        addMenu(true);
        slidemenu.setTouchModeAbove(2);

        listView = (ListView) findViewById(R.id.list_following);
        mFollowingListView = (RelativeLayout) findViewById(R.id.rl_followingdata);
        mFollowingListEmpty = (RelativeLayout) findViewById(R.id.rl_empty_following);
        profileName = (TextView) findViewById(R.id.tv_profile_txt_ing);



        profManager = ModelManager.getInstance().getProfileManager();
        authManager = ModelManager.getInstance().getAuthorizationManager();


        try {
            fromOwnProfile = getIntent().getExtras().getBoolean("FromOwnProfile");
            if (fromOwnProfile) {
                // profileName.setText(authManager.getUserName());
                Utils.launchBarDialog(FollowingListView.this);
                profManager.getFollwer("", authManager.getPhoneNo(), authManager.getUsrToken());
            } else {
                profileName.setText("" + getIntent().getStringExtra("name"));
                Utils.launchBarDialog(FollowingListView.this);
                profManager.getFollwer("" + getIntent().getStringExtra("phoneNo"), authManager.getPhoneNo(), authManager.getUsrToken());
            }
        } catch (Exception e) {
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FollowingListView.this, UserProfileView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("isChangeInList", mchangeinList);
        startActivity(intent);
        overridePendingTransition(0, R.anim.top_out);
        finish();
    }

    public void setlist() {
        if (profManager.following.size() > 0) {

            findViewById(R.id.tv_tag_screen).setVisibility(View.VISIBLE);
            mFollowingListView.setVisibility(View.VISIBLE);
            adapter = new FollowingAdapter(this, R.layout.row_follower, profManager.following);
            int index = listView.getFirstVisiblePosition();
            View v = listView.getChildAt(0);
            int top = (v == null) ? 0 : v.getTop();
            listView.setAdapter(adapter);
            listView.setSelectionFromTop(index, top);
        } else {
            findViewById(R.id.tv_tag_screen).setVisibility(View.GONE);
            if (fromOwnProfile)
                mFollowingListEmpty.setVisibility(View.VISIBLE);
            else
                mFollowingListEmpty.setVisibility(View.GONE);
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
        android.util.Log.d(TAG, "onEventMainThread->" + getMsg);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (getMsg.equalsIgnoreCase("GetFollower True")) {
            Utils.dismissBarDialog();
            setlist();
            android.util.Log.d("1", "message->" + getMsg);
        } else if (getMsg.equalsIgnoreCase("GetFollower False")) {
            Utils.dismissBarDialog();
            android.util.Log.d("2", "message->" + getMsg);
        } else if (getMsg.equalsIgnoreCase("GetFollower Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
            //Utils.showAlert(FollowingListView.this, AlertMessage.connectionError);
            android.util.Log.d("3", "message->" + getMsg);
        } else if (getMsg.equalsIgnoreCase("UnFollowUser true")) {
            // adapter.notifyDataSetChanged();
            android.util.Log.d("1", "message->" + getMsg);
        } else if (getMsg.equalsIgnoreCase("UnFollowUser false")) {
            Utils.dismissBarDialog();
            android.util.Log.d("2", "message->" + getMsg);
        } else if (getMsg.equalsIgnoreCase("UnFollowUser Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
            //Utils.showAlert(FollowingListView.this, AlertMessage.connectionError);
            android.util.Log.d("3", "message->" + getMsg);
        } else if (getMsg.equalsIgnoreCase("update")) {
            android.util.Log.e("list on notify--->", "" + profManager.following);
            adapter.notifyDataSetChanged();

        }
    }
}
