package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.FollowingAdapter;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class FollowingListView extends Activity implements
        View.OnClickListener {
      private static final String TAG = FollowingListView.class.getSimpleName();
      private ImageView back, notification;
      private ListView listView;
      private FollowingAdapter adapter;
      private ProfileManager profManager;
      private AuthManager authManager;
      private TextView profileName, tagScreen;
      private Typeface typeface;
      public static boolean fromOwnProfile = false;
      private RelativeLayout mFollowingListView, mFollowingListEmpty;
      private String name = "", phNo = "";

      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.view_followinglist);
            this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                  phNo = getIntent().getExtras().getString("phoneNo");
                  name = getIntent().getExtras().getString("name");
            }

            listView = (ListView) findViewById(R.id.list_following);
            mFollowingListView = (RelativeLayout) findViewById(R.id.rl_followingdata);
            mFollowingListEmpty = (RelativeLayout) findViewById(R.id.rl_empty_following);
            back = (ImageView) findViewById(R.id.iv_back_ing);
            profileName = (TextView) findViewById(R.id.tv_profile_txt_ing);
            tagScreen = (TextView) findViewById(R.id.tv_tag_screen);

            notification = (ImageView) findViewById(R.id.iv_notification_list_ing);
            back.setOnClickListener(this);
            notification.setOnClickListener(this);
            profManager = ModelManager.getInstance().getProfileManager();
            authManager = ModelManager.getInstance().getAuthorizationManager();
            typeface = Typeface.createFromAsset(FollowingListView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
            profileName.setTypeface(typeface, typeface.BOLD);


            ((TextView) findViewById(R.id.tv_following_msgI)).setTypeface(typeface, typeface.BOLD);
            ((TextView) findViewById(R.id.tv_following_msgII)).setTypeface(typeface, typeface.BOLD);
            try {
                  fromOwnProfile = getIntent().getExtras().getBoolean("FromOwnProfile");
                  if (fromOwnProfile) {
                       // profileName.setText(authManager.getUserName());
                        Utils.launchBarDialog(FollowingListView.this);
                        profManager.getFollwer("", authManager.getPhoneNo(), authManager.getUsrToken());
                  } else {
                        profileName.setText(name);
                        Utils.launchBarDialog(FollowingListView.this);
                        profManager.getFollwer(phNo, authManager.getPhoneNo(), authManager.getUsrToken());
                  }
            } catch (Exception e) {
            }

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
            } else {
                  if (fromOwnProfile)
                        mFollowingListEmpty.setVisibility(View.VISIBLE);
                  else
                        mFollowingListEmpty.setVisibility(View.GONE);
            }

      }

      @Override
      public void onClick(View v) {
            switch (v.getId()) {
                  case R.id.iv_back_ing:
                        finish();
                        break;
                  case R.id.iv_notification_list_ing:
                        finish();
                        break;
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
                  Utils.showAlert(FollowingListView.this, AlertMessage.connectionError);
                  Log.d("3", "message->" + getMsg);
            }
      }
}
