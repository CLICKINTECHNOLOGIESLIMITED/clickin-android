package com.sourcefuse.clickinandroid.view;

/**
 * Created by mukesh on 22/11/14.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.otherLollowerFollowingAdapter;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;


public class OthersFollowingFollowView extends ClickInBaseView implements View.OnClickListener {
    private static final String TAG = FollowingListView.class.getSimpleName();
    public otherLollowerFollowingAdapter adapter;
    public boolean isFollowing = false;
    private ListView listView;
    private ProfileManager profManager;
    private AuthManager authManager;
    private String name = "", phNo = "";
    private boolean isChangeInList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_othes_follow_following);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        addMenu(true);
        slidemenu.setTouchModeAbove(2);
/*            this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);*/

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phNo = getIntent().getExtras().getString("phoneNo");
            name = getIntent().getExtras().getString("name");
            isFollowing = getIntent().getExtras().getBoolean("isFollowing");
            ((TextView) findViewById(R.id.tv_profile_txt_ing_other)).setText(name);

        }

        listView = (ListView) findViewById(R.id.list_following_other);
        profManager = ModelManager.getInstance().getProfileManager();
        authManager = ModelManager.getInstance().getAuthorizationManager();


        Utils.launchBarDialog(OthersFollowingFollowView.this);
        profManager.getFollwerOther(phNo, authManager.getPhoneNo(), authManager.getUsrToken());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                listView.setBackgroundColor(getResources().getColor(R.color.transparent));

                if (isFollowing) {
                    try {
                        if (profManager.following_other.size() >= 0) {
                            String phNo = profManager.following_other.get(position).getPhoneNo();
                            if (!ModelManager.getInstance().getAuthorizationManager().getPhoneNo().equalsIgnoreCase(phNo)) {
                                switchView(phNo);//akshit code No Action To be Taken on User Own Name
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        if (profManager.pfollowerList_other.size() >= 0) {
                            String phNo = profManager.pfollowerList_other.get(position).getPhoneNo();
                            if (!ModelManager.getInstance().getAuthorizationManager().getPhoneNo().equalsIgnoreCase(phNo)) {
                                switchView(phNo);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.top_out);
    }

    private void switchView(String phone) {
        Intent intent = new Intent(OthersFollowingFollowView.this, JumpOtherProfileView.class);
        intent.putExtra("FromOwnProfile", true);
        intent.putExtra("phNumber", phone);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void setlist() {

        //  mFollowingListView.setVisibility(View.VISIBLE);
        if (isFollowing && profManager.following_other.size() > 0) {
            ((TextView) findViewById(R.id.tv_tag_screen_other)).setText(getResources().getString(R.string.txt_following));
            adapter = new otherLollowerFollowingAdapter(this, R.layout.row_others_following_follow, profManager.following_other);
            listView.setAdapter(adapter);
        } else if (profManager.pfollowerList_other.size() > 0) {
            ((TextView) findViewById(R.id.tv_tag_screen_other)).setText(getResources().getString(R.string.txt_follower));
            adapter = new otherLollowerFollowingAdapter(this, R.layout.row_others_following_follow, profManager.pfollowerList_other);
            listView.setAdapter(adapter);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }

    }

    public void onEventMainThread(String getMsg) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (getMsg.equalsIgnoreCase("GetFollower True")) {
            Utils.dismissBarDialog();
            setlist();
        } else if (getMsg.equalsIgnoreCase("GetFollower False")) {
            Utils.dismissBarDialog();
        } else if (getMsg.equalsIgnoreCase("GetFollower Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
            //Utils.showAlert(FollowingListView.this, AlertMessage.connectionError);
        }
    }


}
