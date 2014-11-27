package com.sourcefuse.clickinandroid.view;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ClickInNotificationManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter1;
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter1.Section;
import com.sourcefuse.clickinandroid.view.adapter.UserRelationAdapter;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class UserProfileView extends ClickInBaseView implements View.OnClickListener {
    private static final String TAG = UserProfileView.class.getSimpleName();
    private Button btnAddSomeone;
    TextView EditProfile, follower, following;
    private TextView profileHeader;
    private ListView mUserRelationlistView;
    private ImageView userimage;
    public UserRelationAdapter adapter;
    private AuthManager authManager;
    private RelationManager relationManager;
    private TextView name, userdetails;
    public String phone;
    private ClickInNotificationManager notificationMngr;
    private Typeface typefaceBold, typefaceMedium;
    private View footerView;
    private Bitmap imageBitmap;

	/*
     *
	 * HeaderRelated Things
	 */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_userprofile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        addMenu(true);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        typefaceMedium = Typeface.createFromAsset(UserProfileView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
        typefaceBold = Typeface.createFromAsset(UserProfileView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_BOLD);

        following = (TextView) findViewById(R.id.btn_following);
        follower = (TextView) findViewById(R.id.btn_follower);
        EditProfile = (TextView) findViewById(R.id.btn_edit_profile);

        mUserRelationlistView = (ListView) findViewById(R.id.list_click_with_profile);


        ((ImageView) findViewById(R.id.iv_open_left_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                slidemenu.showMenu(true);
            }
        });
        ((ImageView) findViewById(R.id.iv_open_right_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                slidemenu.showSecondaryMenu(true);
            }
        });


        ((TextView) findViewById(R.id.tv_profile_txt)).setText("PROFILE");


        userimage = (ImageView) findViewById(R.id.iv_usr_icon);
        userimage.setScaleType(ScaleType.FIT_XY);

        name = (TextView) findViewById(R.id.tv_name);
        userdetails = (TextView) findViewById(R.id.tv_user_details);
        profileHeader = (TextView) findViewById(R.id.tv_profile_txt);


        following.setOnClickListener(this);
        follower.setOnClickListener(this);
        EditProfile.setOnClickListener(this);

        name.setTypeface(typefaceBold);
        userdetails.setTypeface(typefaceMedium);
        profileHeader.setTypeface(typefaceBold);


        following.setTypeface(typefaceBold);
        follower.setTypeface(typefaceBold);


        //code to set adapter to populate list
        footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_pro_list, null, false);
        mUserRelationlistView.addFooterView(footerView);


        //    Utils.launchBarDialog(this);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        notificationMngr = ModelManager.getInstance().getNotificationManagerManager();
        //  notificationMngr.getNotification("", authManager.getPhoneNo(), authManager.getUsrToken());

        //make next webservice request after current is finished
        Bundle b = getIntent().getExtras();
        boolean FromSignup = false;
        if (b != null) {
            if (b.containsKey("FromSignup")) {
                FromSignup = getIntent().getExtras().getBoolean("FromSignup");
            }
        }

        if (FromSignup) {
            authManager.getProfileInfo("", authManager.getPhoneNo(), authManager.getUsrToken());
        } else {
            setNotificationList();
            setLeftMenuList();
            setProfileDataView();
            setlist();
            //   relationManager = ModelManager.getInstance().getRelationManager();
            // relationManager.getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
        }
        Log.e(TAG, "vv" + authManager.getPhoneNo() + "" + authManager.getUsrToken());


        findViewById(R.id.iv_usr_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
            }
        });


    }

    public void setProfileDataView() {

        authManager = ModelManager.getInstance().getAuthorizationManager();
        name.setText(authManager.getUserName());
        String dtails = "";
        String gender = "";
        String dob = "";
        try {
            try {
                Log.e(TAG, "Gender -->" + authManager.getGender());
                if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().matches("girl")) {
                    dtails = "Female, ";
                    gender = "girl";
                } else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().matches("guy")) {
                    dtails = "Male,";
                    gender = "guy";
                }
            } catch (Exception e) {
            }
            dtails = dtails + Utils.getCurrentYear(authManager.getdOB()) + " " + getResources().getString(R.string.txt_yold);
        } catch (Exception e) {
        }

        if (!Utils.isEmptyString(authManager.getUserCity()) && Utils.isEmptyString(authManager.getUserCountry())) {
            dtails = dtails + "\n" + authManager.getUserCity();
            userdetails.setText(dtails);
        } else if (Utils.isEmptyString(authManager.getUserCity()) && !Utils.isEmptyString(authManager.getUserCountry())) {
            dtails = dtails + "\n" + authManager.getUserCountry();
            userdetails.setText(dtails);
        } else if (!Utils.isEmptyString(authManager.getUserCity()) && !Utils.isEmptyString(authManager.getUserCountry())) {
            userdetails.setText(dtails + "\n" + authManager.getUserCity() + "," + authManager.getUserCountry());
        } else {
            userdetails.setText(dtails + "\n");
        }
        setFollowAndFollowingCount();

        //prafull code to set image bitmap
        try {
            Bitmap imagebitmap = authManager.getUserbitmap();
            if(imagebitmap != null)
                userimage.setImageBitmap(imageBitmap);
            else if(!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl"))
                Picasso.with(UserProfileView.this).load(authManager.getUserPic()).skipMemoryCache().error(R.drawable.female_user).into(userimage);
            else if(!Utils.isEmptyString(authManager.getGender()))
                Picasso.with(UserProfileView.this).load(authManager.getUserPic()).skipMemoryCache().error(R.drawable.male_user).into(userimage);

        }catch (Exception e)
        {
            if(!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl"))
                userimage.setImageResource(R.drawable.male_user);
            else if(!Utils.isEmptyString(authManager.getGender()))
                userimage.setImageResource(R.drawable.female_user);
        }





    }
//akshit code ends


    private void setFollowAndFollowingCount() {

        relationManager = ModelManager.getInstance().getRelationManager();
        String text = "<font color=#cccccc>" + relationManager.getFollowerListCount() + "</font> <font color=#oob0c7>" + getResources().getString(R.string.txt_follower) + "</font>";
        follower.setText(Html.fromHtml(text));
        String textfollowing = "<font color=#f29691>" + getResources().getString(R.string.txt_following) + "</font> <font color=#cccccc>" + relationManager.getFollowingListCount() + "</font>";
        following.setText(Html.fromHtml(textfollowing));

    }


    @Override
    protected void onResume() {
        Log.e("onResume", "onResume UserProfile");
        super.onResume();
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (authManager.isEditProfileFlag()) {
            //data is already updated in authmanager, so no need to make a webservice call
            //  authManager.getProfileInfo("", authManager.getPhoneNo(), authManager.getUsrToken());
            setProfileDataView();
            authManager.setEditProfileFlag(false);
        }
    }

    public void setlist() {

        setFollowAndFollowingCount();

        ArrayList<Section> sections = new ArrayList<Section>();
        SimpleSectionedListAdapter1 simpleSectionedGridAdapter;
        relationManager = ModelManager.getInstance().getRelationManager();
        adapter = new UserRelationAdapter(UserProfileView.this, R.layout.row_userprofile, relationManager.getrelationshipsData);
        String[] mHeaderNames = {"CLICKIN'", "CLICKIN'"};
        String[] mHeaderNames2 = {" REQUESTS", " WITH"};
        Integer[] mHeaderPositions = {0, relationManager.requestedList.size()};
        int positionOfHeader = 0;
        int noOfHeader = 0;

        if (relationManager.acceptedList.size() == 0 && relationManager.requestedList.size() == 0) {
            positionOfHeader = 2;
            noOfHeader = 2;
        } else if (relationManager.requestedList.size() > 0 && relationManager.acceptedList.size() == 0) {
            positionOfHeader = 0;
            noOfHeader = 1;
        } else if ((relationManager.requestedList.size() == 0) && (relationManager.acceptedList.size() > 0)) {
            positionOfHeader = 1;
            noOfHeader = 2;
        } else if ((relationManager.requestedList.size() > 0) && (relationManager.requestedList.size() > 0)) {
            positionOfHeader = 0;
            noOfHeader = 2;
        }
        for (int i = positionOfHeader; i < noOfHeader; i++) {
            sections.add(new Section(mHeaderPositions[i], mHeaderNames[i], mHeaderNames2[i]));
        }
        simpleSectionedGridAdapter = new SimpleSectionedListAdapter1(UserProfileView.this, adapter, R.layout.list_item_header, R.id.tv_clickintx, R.id.tv_with);
        simpleSectionedGridAdapter.setSections(sections.toArray(new Section[0]));
        mUserRelationlistView.setAdapter(simpleSectionedGridAdapter);

        btnAddSomeone = (Button) footerView.findViewById(R.id.btn_add_someone);
        btnAddSomeone.setOnClickListener(this);
    }

    /*
     * Header Related things
     */
    @Override
    public void onClick(View v) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        ProfileManager profileManager = ModelManager.getInstance().getProfileManager();
        switch (v.getId()) {
            case R.id.btn_follower:
                Intent intentFollower = new Intent(UserProfileView.this, FollowerList.class);
                intentFollower.putExtra("FromOwnProfile", true);
                startActivity(intentFollower);
                this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.btn_following:
                Intent intentFollowing = new Intent(UserProfileView.this, FollowingListView.class);
                intentFollowing.putExtra("FromOwnProfile", true);
                startActivity(intentFollowing);
                this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.iv_menu:
                Log.e("iv_menu", "iv_menu");
                slidemenu.showMenu(true);

                break;
            case R.id.iv_notification:
                Log.e("iv_notification", "iv_notification");
                slidemenu.showSecondaryMenu(true);
                break;
            case R.id.btn_add_someone:
                Intent intent = new Intent(UserProfileView.this, AddSomeoneView.class);
                intent.putExtra("FromOwnProfile", true);
                intent.putExtra("fromsignup",false);
                startActivity(intent);
                break;
            case R.id.btn_edit_profile:
                Intent editProfile = new Intent(UserProfileView.this, EditMyProfileView.class);
                startActivity(editProfile);
                //akshit code for animation
                //  overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();


    }

    public void onEventMainThread(String message) {
        super.onEventMainThread(message);
        Log.e(TAG, "onEventMainThread-->" + message);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        relationManager = ModelManager.getInstance().getRelationManager();
        if (message.equalsIgnoreCase("deleteRelationship True")) {
            relationManager.getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
        } else if (message.equalsIgnoreCase("deleteRelationship False")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, authManager.getMessage());
        } else if (message.equalsIgnoreCase("deleteRelationship Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);


        } else if (message.equalsIgnoreCase("GetRelationShips False")) {
            Utils.dismissBarDialog();
            doRestInitialization();

        } else if (message.equalsIgnoreCase("GetRelationShips Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);

        } else if (message.equalsIgnoreCase("updateStatus true")) {
            relationManager.getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
        } else if (message.equalsIgnoreCase("UserVisible true")) {

            if (Utils.DEBUG)
                com.sourcefuse.clickinandroid.utils.Log.e("on error when change type", "on error when change type");

            Log.d("3", "message->" + message);
        } else if (message.equalsIgnoreCase("Notification true")) {


        } else if (message.equalsIgnoreCase("GetrelationShips True")) {
            Utils.dismissBarDialog();

            setlist();
        } else if (message.equalsIgnoreCase("ProfileInfo True")) {
            Log.e(TAG, "ProfileInfo True");
            //setProfileDataView();
            relationManager.getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
        } else if (message.equalsIgnoreCase("ProfileInfo False")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, authManager.getMessage());
            //Utils.showAlert(UserProfileView.this, authManager.getMessage());
        } else if (message.equalsIgnoreCase("ProfileInfo Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
        } else if (message.equalsIgnoreCase("UserVisible Network Error")) {
            if (Utils.DEBUG)
                com.sourcefuse.clickinandroid.utils.Log.e("on error when change type", "on error when change type");
        } else if (message.equalsIgnoreCase("UserVisible true on error")) {
            if (Utils.DEBUG)
                com.sourcefuse.clickinandroid.utils.Log.e("on error when change type on error", "on error when change type on error");
        }


    }

    @Override
    public void onBackPressed() {

    }

    private void switchView() {
        Intent intent = new Intent(UserProfileView.this, FollowerList.class);
        //	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //this.finish();
    }

    private void switchViewToFollowingList() {
        Intent intent = new Intent(UserProfileView.this, FollowingListView.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //this.finish();
    }

    private void doRestInitialization() {

        setNotificationList();
        setLeftMenuList();
        setProfileDataView();
        setlist();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null && intent.getExtras().containsKey("isChangeInList")) {
            if (intent.getExtras().getBoolean("isChangeInList")) {
                relationManager = ModelManager.getInstance().getRelationManager();
                relationManager.getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
            }
        }


        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        ComponentName componentInfo = taskInfo.get(0).topActivity;


        com.sourcefuse.clickinandroid.utils.Log.e("package name--->",""+componentInfo.getClass());
    }

    public void onDestroy() {
        super.onDestroy();

        Log.e("UserprofileView", "Destroy");
    }
}
