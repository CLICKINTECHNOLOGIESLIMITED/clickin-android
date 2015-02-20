package com.sourcefuse.clickinandroid.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
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
import com.sourcefuse.clickinandroid.model.PicassoManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.services.MyQbChatService;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter1;
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter1.Section;
import com.sourcefuse.clickinandroid.view.adapter.UserRelationAdapter;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;


public class UserProfileView extends ClickInBaseView implements View.OnClickListener {
    public UserRelationAdapter adapter;
    public String phone;
    public MyQbChatService myQbChatService;
    TextView EditProfile, follower, following;
    private Button btnAddSomeone;
    private TextView profileHeader;
    private ListView mUserRelationlistView;
    private ImageView userimage;
    private AuthManager authManager;
    private RelationManager relationManager;
    private TextView name, userdetails;
    private ClickInNotificationManager notificationMngr;
    private Typeface typefaceBold, typefaceMedium;
    private View footerView;
    private Bitmap imageBitmap;
    /*
     *
	 * HeaderRelated Things
	 */
    private boolean mIsBound;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            myQbChatService = ((MyQbChatService.LocalBinder) service).getService();
            mIsBound = true;
           /* myQbChatService.createRoom(mRoomName);*/

            // showMessages();

            // Tell the user about this for our demo.
//            Toast.makeText(Binding.this, R.string.local_service_connected,
//                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            myQbChatService = null;
            mIsBound = false;
//            Toast.makeText(Binding.this, R.string.local_service_disconnected,
//                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //code- to handle uncaught exception
        if (Utils.mStartExceptionTrack)
            Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

        setContentView(R.layout.view_userprofile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        addMenu(true);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);





        authManager = ModelManager.getInstance().getAuthorizationManager();
        typefaceMedium = Typeface.createFromAsset(getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
        typefaceBold = Typeface.createFromAsset(getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_BOLD);

        following = (TextView) findViewById(R.id.btn_following);
        follower = (TextView) findViewById(R.id.btn_follower);
        EditProfile = (TextView) findViewById(R.id.btn_edit_profile);

        mUserRelationlistView = (ListView) findViewById(R.id.list_click_with_profile);


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


        authManager = ModelManager.getInstance().getAuthorizationManager();
        notificationMngr = ModelManager.getInstance().getNotificationManagerManager();
        //  notificationMngr.getNotification("", authManager.getPhoneNo(), authManager.getUsrToken());

        //make next webservice request after current is finished
        Bundle b = getIntent().getExtras();
        boolean FromSignup = false;
        boolean isChangeInList = false;
        boolean updatephoto = false;


        if (b != null) {
            if (b.containsKey("FromSignup")) {
                FromSignup = getIntent().getExtras().getBoolean("FromSignup");
            }
            if (b.containsKey("isChangeInList")) {
                isChangeInList = getIntent().getExtras().getBoolean("isChangeInList");
            }
            if (b.containsKey("updatephoto")) {
                updatephoto = getIntent().getExtras().getBoolean("updatephoto");

            }

        }


        if (FromSignup) {
            Utils.launchBarDialog(this);
            authManager.getProfileInfo("", authManager.getPhoneNo(), authManager.getUsrToken());
        } else {
            setNotificationList();
            setLeftMenuList();
            setProfileDataView();
            setlist();
        }
        if (updatephoto || isChangeInList) {
            // Utils.launchBarDialog(this);
            ModelManager.getInstance().getRelationManager().getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
        }

        findViewById(R.id.iv_usr_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (edt_search.getWindowToken() != null)
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
            }
        });
//start service first, so that it will not get destroyed when unbind.
        //also unbind it onDestroy rather than onstop--monika

        Intent i = new Intent(this, MyQbChatService.class);
        startService(i);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);

    }

    public void setProfileDataView() {

        authManager = ModelManager.getInstance().getAuthorizationManager();
        name.setText(authManager.getUserName());
        String dtails = "";
        String gender = "";
        String dob = "";

        if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl")) {
            dtails = "Female, ";
            gender = "girl";
        } else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("guy")) {
            dtails = "Male,";
            gender = "guy";
        }

        if (!Utils.isEmptyString(authManager.getdOB()))
            dtails = dtails + Utils.getCurrentYear(authManager.getdOB()) + " " + getResources().getString(R.string.txt_yold);


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
        Bitmap imagebitmap1 = authManager.getUserbitmap();
        String mUserImagePath = null;
        Uri mUserImageUri = null;
        boolean userpic = Utils.isEmptyString(authManager.getUserPic());
        //prafull code to set image bitmap
        try {
            if (authManager.getUserImageUri() != null)
                mUserImagePath = "" + authManager.getUserImageUri().toString();
            if (!Utils.isEmptyString(mUserImagePath))
                mUserImageUri = Utils.getImageContentUri(this, new File(mUserImagePath));

            if (!Utils.isEmptyString("" + mUserImageUri))
                userimage.setImageURI(mUserImageUri);
            else if (imagebitmap1 != null)
                userimage.setImageBitmap(imagebitmap1);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl") && !userpic)
                Picasso.with(this).load(authManager.getUserPic()).error(R.drawable.female_user).into(userimage);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("guy") && !userpic)
                Picasso.with(this).load(authManager.getUserPic()).error(R.drawable.male_user).into(userimage);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl"))
                userimage.setImageResource(R.drawable.female_user);
            else
                userimage.setImageResource(R.drawable.male_user);

        } catch (Exception e) {
            userimage.setImageResource(R.drawable.male_user);
        }


    }


    private void setFollowAndFollowingCount() {

        relationManager = ModelManager.getInstance().getRelationManager();
        String text = "<font color=#cccccc>" + relationManager.getFollowerListCount() + "</font> <font color=#oob0c7>" + getResources().getString(R.string.txt_follower) + "</font>";
        follower.setText(Html.fromHtml(text));
        String textfollowing = "<font color=#f29691>" + getResources().getString(R.string.txt_following) + "</font> <font color=#cccccc>" + relationManager.getFollowingListCount() + "</font>";
        following.setText(Html.fromHtml(textfollowing));

    }


    @Override
    protected void onResume() {
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
        adapter = new UserRelationAdapter(this, R.layout.row_userprofile, relationManager.getrelationshipsData);
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
        simpleSectionedGridAdapter = new SimpleSectionedListAdapter1(this, adapter, R.layout.list_item_header, R.id.tv_clickintx, R.id.tv_with);
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
                Intent intentFollower = new Intent(this, FollowerList.class);
                intentFollower.putExtra("FromOwnProfile", true);
                startActivity(intentFollower);
                this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                Utils.trackMixpanel(this, "", "", "MyFollowers", false);//track Followers Button mixpanel

                break;
            case R.id.btn_following:
                Intent intentFollowing = new Intent(this, FollowingListView.class);
                intentFollowing.putExtra("FromOwnProfile", true);
                startActivity(intentFollowing);
                this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                Utils.trackMixpanel(this, "", "", "MyFollowing", false);//track Following Button mixpanel

                break;
            case R.id.btn_add_someone:
                Intent intent = new Intent(this, AddSomeoneView.class);
                intent.putExtra("FromOwnProfile", true);
                intent.putExtra("fromsignup", false);
                startActivity(intent);
                Utils.trackMixpanel(this, "", "", "ClickInWithSomeone", false);//track Click on Add Someone Button mixpanel
                break;
            case R.id.btn_edit_profile:
                Intent editProfile = new Intent(this, EditMyProfileView.class);
                startActivity(editProfile);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                Utils.trackMixpanel(this, "", "", "Edit Profile", false);////track For Edit profile Button Followers Button mixpanel
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


        } else if (message.equalsIgnoreCase("GetrelationShips True")) {
            Utils.dismissBarDialog();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (myQbChatService != null)
                        myQbChatService.setChatListeners();
                }
            }, 10000);
            setLeftMenuList();
            if(adapter != null)
                adapter.notifyDataSetChanged();;
            setlist();
        } else if (message.equalsIgnoreCase("ProfileInfo True")) {
            //monika-start service in case of sign up only, else it will be done from sign in
            Intent i = new Intent(this, MyQbChatService.class);
            startService(i);
            setProfileDataView();
            Utils.dismissBarDialog();
            relationManager.getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
        } else if (message.equalsIgnoreCase("ProfileInfo False")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, authManager.getMessage());
        } else if (message.equalsIgnoreCase("ProfileInfo Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
        } else if (message.equalsIgnoreCase("UserVisible Network Error")) {

        } else if (message.equalsIgnoreCase("UserVisible true on error")) {

        }


    }

    @Override
    public void onBackPressed() {

    }

    private void switchView() {
        Intent intent = new Intent(this, FollowerList.class);
        startActivity(intent);

    }

    private void switchViewToFollowingList() {
        Intent intent = new Intent(this, FollowingListView.class);
        startActivity(intent);

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


        if (myQbChatService == null) {
            Intent i = new Intent(this, MyQbChatService.class);
            startService(i);
            bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        }
        if (intent.getExtras() != null && intent.getExtras().containsKey("isChangeInList")) {


            if (intent.getExtras().getBoolean("isChangeInList")) {
                if (slidemenu.isMenuShowing())
                    slidemenu.showContent();

                relationManager = ModelManager.getInstance().getRelationManager();
                relationManager.getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
            }
        } else if (intent.getExtras() != null && intent.getExtras().containsKey("updatephoto")) {

            if (slidemenu.isMenuShowing())
                slidemenu.showContent();
            authManager.getProfileInfo("", authManager.getPhoneNo(), authManager.getUsrToken());
        }

        // addMenu(false);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }

    }
}
