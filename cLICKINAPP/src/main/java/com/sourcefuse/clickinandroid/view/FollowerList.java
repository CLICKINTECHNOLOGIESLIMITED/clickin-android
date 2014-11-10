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
import com.sourcefuse.clickinandroid.view.adapter.FollowerAdapter;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class FollowerList extends Activity implements
		View.OnClickListener {
    private static final String TAG = FollowerList.class.getSimpleName();
	private ImageView back, notification;
	private ListView listView;
	public static FollowerAdapter adapter;
	private ProfileManager profManager;
	private AuthManager authManager;
	private TextView profileName;
	private Typeface typeface;
    public static boolean fromOwnProfile = false;
    private RelativeLayout mFollowerListView,mFollowerListEmpty;
    private String name="", phNo="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_followerlist);
		this.overridePendingTransition(R.anim.slide_in_right ,R.anim.slide_out_right);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            phNo = getIntent().getExtras().getString("phoneNo");
            name = getIntent().getExtras().getString("name");
        }

		listView = (ListView) findViewById(R.id.list_follower);
        mFollowerListView = (RelativeLayout) findViewById(R.id.rl_followerdata);
        mFollowerListEmpty = (RelativeLayout) findViewById(R.id.rl_empty_follower);
		profileName = (TextView) findViewById(R.id.tv_profile_txt_wer);
		//tagScreen = (TextView) findViewById(R.id.tv_tag_screen);
		back = (ImageView) findViewById(R.id.iv_back);
		notification = (ImageView) findViewById(R.id.iv_notification_list);
		back.setOnClickListener(this);
		notification.setOnClickListener(this);
		profManager = ModelManager.getInstance().getProfileManager();
		authManager = ModelManager.getInstance().getAuthorizationManager();
		typeface = Typeface.createFromAsset(FollowerList.this.getAssets(),Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
		profileName.setTypeface(typeface, typeface.BOLD);
		//tagScreen.setTypeface(typeface);

        ((TextView) findViewById(R.id.tv_follower_msgI)).setTypeface(typeface, typeface.BOLD);
        ((TextView) findViewById(R.id.tv_follower_msgII)).setTypeface(typeface, typeface.BOLD);

        fromOwnProfile = getIntent().getExtras().getBoolean("FromOwnProfile");
        if(fromOwnProfile) {
            profileName.setText(authManager.getUserName());
            Utils.launchBarDialog(FollowerList.this);
             profManager.getFollwer("", authManager.getPhoneNo(), authManager.getUsrToken());
        }else {
            profileName.setText(name);
            Utils.launchBarDialog(FollowerList.this);
            profManager.getFollwer(getIntent().getExtras().getString("phoneNo"), authManager.getPhoneNo(), authManager.getUsrToken());
       }

	}

	public void setlist() {

        if(profManager.followers.size()>0){
            mFollowerListView.setVisibility(View.VISIBLE);
            mFollowerListEmpty.setVisibility(View.GONE);
		adapter = new FollowerAdapter(this, R.layout.row_follower,profManager.followers);
		int index = listView.getFirstVisiblePosition();
		View v = listView.getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();
		listView.setAdapter(adapter);
		listView.setSelectionFromTop(index, top);
        }else{
            if(fromOwnProfile) {
                mFollowerListEmpty.setVisibility(View.VISIBLE);
                mFollowerListView.setVisibility(View.GONE);
            }
            else
            {
                mFollowerListEmpty.setVisibility(View.GONE);
                mFollowerListView.setVisibility(View.GONE);
            }
        }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_notification_list:
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
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    public void onEventMainThread(String getMsg){
        Log.d(TAG, "onEventMainThread->" + getMsg);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (getMsg.equalsIgnoreCase("UnFollowUser true")) {
           // profManager.getFollwer("", authManager.getPhoneNo(), authManager.getUsrToken());
            adapter.notifyDataSetChanged();
        } else if (getMsg.equalsIgnoreCase("UnFollowUser false")) {
            Utils.showAlert(FollowerList.this, authManager.getMessage());
        } else if(getMsg.equalsIgnoreCase("UnFollowUser Network Error")){
            Utils.showAlert(FollowerList.this, AlertMessage.connectionError);
        } else if (getMsg.equalsIgnoreCase("FollowUser true")) {
            adapter.notifyDataSetChanged();
        } else if (getMsg.equalsIgnoreCase("FollowUser false")) {
            Utils.showAlert(FollowerList.this, authManager.getMessage());
        } else if(getMsg.equalsIgnoreCase("FollowUser Network Error")){
            Utils.showAlert(FollowerList.this, AlertMessage.connectionError);
        }else if (getMsg.equalsIgnoreCase("GetFollower True")) {
            Utils.dismissBarDialog();
            setlist();
            Log.d("1", "message->"+getMsg);
        } else if (getMsg.equalsIgnoreCase("GetFollower False")) {
            Utils.dismissBarDialog();
            Log.d("2", "message->"+getMsg);
        } else if(getMsg.equalsIgnoreCase("GetFollower Network Error")){
            Utils.dismissBarDialog();
            Utils.showAlert(FollowerList.this, AlertMessage.connectionError);
            Log.d("3", "message->"+getMsg);
        }else if (getMsg.equalsIgnoreCase("followUpdateStatus True")) {
            adapter.notifyDataSetChanged();
        } else if (getMsg.equalsIgnoreCase("followUpdateStatus False")) {

        } else if(getMsg.equalsIgnoreCase("followUpdateStatus Network Error")){
            Utils.showAlert(FollowerList.this, AlertMessage.connectionError);
        }

    }



}