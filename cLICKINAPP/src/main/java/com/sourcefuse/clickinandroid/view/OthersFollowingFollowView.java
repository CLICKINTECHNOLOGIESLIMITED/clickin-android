package com.sourcefuse.clickinandroid.view;

/**
 * Created by mukesh on 22/11/14.
 */

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
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





public class OthersFollowingFollowView extends ClickInBaseView implements View.OnClickListener {
    private static final String TAG = FollowingListView.class.getSimpleName();
    private ImageView back, notification;
    private ListView listView;
    public FollowingAdapter adapter;
    private ProfileManager profManager;
    private AuthManager authManager;
    private TextView profileName, tagScreen;
    private Typeface typeface;
    public static boolean fromOwnProfile = false;
    private RelativeLayout mFollowingListView, mFollowingListEmpty;
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
        }

        listView = (ListView) findViewById(R.id.list_following_other);
        back = (ImageView) findViewById(R.id.iv_back_ing_other);
        profileName = (TextView) findViewById(R.id.tv_profile_txt_ing_other);
        tagScreen = (TextView) findViewById(R.id.tv_tag_screen_other);

        notification = (ImageView) findViewById(R.id.iv_notification_list_ing);
        back.setOnClickListener(this);
        notification.setOnClickListener(this);
        profManager = ModelManager.getInstance().getProfileManager();
        authManager = ModelManager.getInstance().getAuthorizationManager();


        Utils.launchBarDialog(OthersFollowingFollowView.this);
        profManager.getFollwer(phNo, authManager.getPhoneNo(), authManager.getUsrToken());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

            }
        });


    }

    public void setlist() {
        if (profManager.following.size() > 0) {
            mFollowingListView.setVisibility(View.VISIBLE);
            adapter = new FollowingAdapter(this, R.layout.row_follower, profManager.following);
            int index = listView.getFirstVisiblePosition();
            View v = listView.getChildAt(0);
            int top = (v == null) ? 0 : v.getTop();
            listView.setAdapter(adapter);
            listView.setSelectionFromTop(index, top);
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
            case R.id.iv_back_ing:
                Intent intent = new Intent(OthersFollowingFollowView.this, UserProfileView.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("isChangeInList", isChangeInList);
                startActivity(intent);
                overridePendingTransition(0,R.anim.top_out);
                break;
            case R.id.iv_notification_list_ing:
                slidemenu.showSecondaryMenu(true);
                break;
        }

    }

    public void onEventMainThread(String getMsg) {
        Log.d(TAG, "onEventMainThread->" + getMsg);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (getMsg.equalsIgnoreCase("GetFollower True")) {
            Utils.dismissBarDialog();
            setlist();
            Log.d("1", "message->" + getMsg);
        } else if (getMsg.equalsIgnoreCase("GetFollower False")) {
            Utils.dismissBarDialog();
            Log.d("2", "message->" + getMsg);
        } else if (getMsg.equalsIgnoreCase("GetFollower Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
            //Utils.showAlert(FollowingListView.this, AlertMessage.connectionError);
            Log.d("3", "message->" + getMsg);
        }
    }


}
