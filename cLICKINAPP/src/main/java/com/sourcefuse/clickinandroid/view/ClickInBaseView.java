package com.sourcefuse.clickinandroid.view;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.NewsFeedManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.NotificationBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.ClickInWithAdapter;
import com.sourcefuse.clickinandroid.view.adapter.NotificationAdapter;
import com.sourcefuse.clickinandroid.view.adapter.SearchAdapter;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import de.greenrobot.event.EventBus;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ClickInBaseView extends Activity implements TextWatcher, SlidingMenu.OnOpenListener, SlidingMenu.OnCloseListener {


    public StickyListHeadersListView clickWithlistView;
    public ListView searchList;
    public ClickInWithAdapter clickInadapter;
    public Boolean stopSearch = true;
    public EditText edt_search;
    //Right Menu.....
    public NewsFeedManager newsFeedManager;
    public SlidingMenu slidemenu;
    String mLastchatID = "";
    View header;
    /// Left Menu
    private TextView userName;
    private ImageView userPic, hideSearchlist;
    private AuthManager authManager;
    private RelationManager relationManager;
    private TextView searchInviteView;
    private LinearLayout theFeed, inviteF, findFriend, setting;
    private String quickBlockId, partnerPic, partnerName, partnerId, myClicks, userClicks, partnerPh;
    private SearchAdapter searchListadapter;
    private RelativeLayout imageMenuRefresh;
    private int relationListIndex;
    private ImageView backArrowRightSide;
    private ChatManager chatManager;
    private PullToRefreshListView notificationList;
    private NotificationAdapter notificationAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        //code- to handle uncaught exception
        if(Utils.mStartExceptionTrack)
            Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(ClickInBaseView.this));
    }

    @Override
    public void setContentView(int layoutResID) {  // prafull code for comman header
        super.setContentView(layoutResID);
        header = getLayoutInflater().inflate(R.layout.view_baseview, null);
        ((ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0)).addView(header, 1);
        header.findViewById(R.id.iv_open_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.trackMixpanel(ClickInBaseView.this,"","","LeftMenuOpened");////To track through mixPanel,if Left Menu is opened.
                slidemenu.showMenu();
            }
        });

        header.findViewById(R.id.iv_open_right_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                slidemenu.showSecondaryMenu();
            }
        });

    }


    @SuppressWarnings("static-access")
    public void setLeftMenuList() {


        clickInadapter = new ClickInWithAdapter(ClickInBaseView.this, R.layout.row_clickin_with, relationManager.acceptedList);
        clickWithlistView.setAdapter(clickInadapter);
    }

    private void switchView(String rid, int relationListIndex) {

        relationManager = ModelManager.getInstance().getRelationManager();
       /* Intent intent = new Intent();*/
        Intent intent = new Intent(ClickInBaseView.this, ChatRecordView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction("UPDATE");
        intent.putExtra("quickId", quickBlockId);
        intent.putExtra("partnerPic", partnerPic);
        intent.putExtra("partnerName", partnerName);
        intent.putExtra("rId", rid);
        intent.putExtra("partnerId", partnerId);

        intent.putExtra("myClicks", myClicks);
        intent.putExtra("userClicks", userClicks);
        intent.putExtra("partnerPh", partnerPh);
        intent.putExtra("relationListIndex", relationListIndex);

        String mNewPrtner = relationManager.acceptedList.get(relationListIndex).mIs_new_partner;
        String mRelationShipId = relationManager.acceptedList.get(relationListIndex).getRelationshipId();
        String mNewUser = authManager.mIs_new_clickin_user;

        //To track through mixPanel.
        //Total Number of user clicks.
        Utils.trackMixpanel(ClickInBaseView.this,"TotalClicksSent",""+userClicks,"RPageShareButtonClicked");

        String mValue;
        if (mNewUser != null && mNewUser.equalsIgnoreCase("yes") && mNewPrtner != null && mNewPrtner.equalsIgnoreCase("yes")) {
            mValue = "one";

        } else if (mNewPrtner != null && mNewPrtner.equalsIgnoreCase("yes")) {
            mValue = "two";

        } else {
            mValue = "none";
        }


        intent.putExtra("mValue", mValue);

        chatManager = ModelManager.getInstance().getChatManager();
        chatManager.setrelationshipId(rid);


        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        String className = componentInfo.getClassName();
        if (className.equalsIgnoreCase("com.sourcefuse.clickinandroid.view.ChatRecordView")) {
            startActivity(intent);

                  /* for animation prafull */

            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            slidemenu.showContent();
            //  slidemenu.showContent(true);
        } else {
            startActivity(intent);
                  /* for animation prafull */

            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            // slidemenu.showContent(true);
        }


    }
    /// Left Menu End


    public void addMenu(boolean setData) {
        // TODO Auto-generated method stub
        slidemenu = new SlidingMenu(this);
        slidemenu.setMode(SlidingMenu.LEFT_RIGHT);
        slidemenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidemenu.setShadowWidthRes(R.dimen.shadow_width);
        slidemenu.setShadowDrawable(R.drawable.shadow);
        slidemenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidemenu.setFadeDegree(0.35f);
        slidemenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        /*
         * Left Menu
         */
        slidemenu.setMenu(R.layout.menu_view);
        leftMenuElements();


        /* set notification data prafull code */

        if (authManager == null)
            authManager = ModelManager.getInstance().getAuthorizationManager();
        TextView mNotificationText = (TextView) header.findViewById(R.id.iv_open_right_menu);
        if (authManager.getNotificationCounter() > 0) {

            String mValue = "";
            int mNotificationValue = authManager.getNotificationCounter();

            if (mNotificationValue > 99) {
                mValue = "99+";

            } else {
                mValue = String.valueOf(mNotificationValue);


            }
            mNotificationText.setText("" + mValue);
            mNotificationText.setTextColor(Color.parseColor("#39cad4"));
        } else {

            mNotificationText.setText("0");
            mNotificationText.setTextColor(Color.parseColor("#000000"));
        }


        if (setData) {
            getMenuListData();
        } else {
            setMenuListData();

        }

        slidemenu.setOnCloseListener(ClickInBaseView.this);
        slidemenu.setOnOpenListener(ClickInBaseView.this);
        slidemenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {


                if (slidemenu.isSecondaryMenuShowing()) {
                    if (notificationAdapter != null)
                        notificationAdapter.notifyDataSetChanged();

                    ModelManager.getInstance().getAuthorizationManager().setNotificationCounter(0);
                    if (ModelManager.getInstance().getNotificationManagerManager().notificationData.size() == 0) {
                        Utils.launchBarDialog(ClickInBaseView.this);
                        mLastchatID = "";
                        ModelManager.getInstance().getNotificationManagerManager().getNotification(getApplicationContext(), "", ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                                ModelManager.getInstance().getAuthorizationManager().getUsrToken());
                    }

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() { // put delay of 500 millisecond so that adapter get set first.
                        @Override
                        public void run() {

                            for (NotificationBean bean : ModelManager.getInstance().getNotificationManagerManager().notificationData)
                                bean.setIs_read("true");

                        }
                    }, 500);


                }

            }
        });
        /*
         * Right Menu
         */
        slidemenu.setSecondaryMenu(R.layout.view_notification);
        //slidemenu.setSecondaryShadowDrawable(R.drawable.shadow);
        rightMenuElements();


        slidemenu.setOnClosedListener(new SlidingMenu.OnClosedListener() {
            @Override
            public void onClosed() {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(edt_search.getWindowToken() != null)
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);

            }
        });
        slidemenu.findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_search.setText("");

            }
        });

    }


    public void leftMenuElements() {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        relationManager = ModelManager.getInstance().getRelationManager();
        edt_search = (EditText) slidemenu.findViewById(R.id.edt_search);
        searchList = (ListView) slidemenu.findViewById(R.id.search_list);


        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {


                //praful code
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                Utils.trackMixpanel(ClickInBaseView.this,"","","UserSearched");//Track event for MixPanel,if user searched from left slide menu
                    if (!(edt_search.getText().toString().length() > 0)) {
                        hideSearchlist.setVisibility(View.GONE);
                        searchList.setVisibility(View.GONE);

                        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null)
                            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    }
                    String search_date = edt_search.getText().toString();
                    if (!Utils.isEmptyString(search_date) && search_date.length() > 0) {
                        slidemenu.findViewById(R.id.btn_clear).setVisibility(View.GONE);
                        slidemenu.findViewById(R.id.btn_progressBar).setVisibility(View.VISIBLE);
                    } else {
                        slidemenu.findViewById(R.id.btn_clear).setVisibility(View.GONE);
                        slidemenu.findViewById(R.id.btn_progressBar).setVisibility(View.GONE);
                    }
                    if (edt_search.getText().toString().length() > 0) {
                        hideSearchlist.setVisibility(View.VISIBLE);
                        searchList.setVisibility(View.VISIBLE);
                        stopSearch = true;
                        if (stopSearch) {
                            stopSearch = false;
                            relationManager = ModelManager.getInstance().getRelationManager();
                            authManager = ModelManager.getInstance().getAuthorizationManager();
                            relationManager.fetchusersbyname(edt_search.getText().toString(), authManager.getPhoneNo(), authManager.getUsrToken());
                        }
                    }
                    handled = true;
                }
                return handled;
            }
        });


        hideSearchlist = (ImageView) slidemenu.findViewById(R.id.iv_hide_searchlist);

        clickWithlistView = (StickyListHeadersListView) slidemenu.findViewById(R.id.click_with_list_menu);
        clickWithlistView.setDivider(null);
        // Adding  header And footer
        View headerView = ((LayoutInflater) ClickInBaseView.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.menu_header, null, false);
        View footerView = ((LayoutInflater) ClickInBaseView.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.menu_footer, null, false);
        View searchfooter = ((LayoutInflater) ClickInBaseView.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.search_list_footer, null, false);
        clickWithlistView.addHeaderView(headerView);
        clickWithlistView.addFooterView(footerView);
        searchList.addFooterView(searchfooter);

        // heater View....
        userName = (TextView) headerView.findViewById(R.id.iv_usr_name);
        userPic = (ImageView) headerView.findViewById(R.id.iv_usr_img);
        imageMenuRefresh = (RelativeLayout) headerView.findViewById(R.id.image_menu_refresh);

        // footer view
        theFeed = (LinearLayout) footerView.findViewById(R.id.ll_feed);
        inviteF = (LinearLayout) footerView.findViewById(R.id.ll_invite);
        findFriend = (LinearLayout) footerView.findViewById(R.id.ll_friend);
        setting = (LinearLayout) footerView.findViewById(R.id.ll_setting);
        searchInviteView = (TextView) searchfooter.findViewById(R.id.btn_invite_view);


        userName.setText(authManager.getUserName());
        userPic.setScaleType(ScaleType.FIT_XY);


        //prafull code to set image bitmap
        try {
            String mUserImagePath = null;
            Uri mUserImageUri = null;
            Bitmap imagebitmap1 = authManager.getUserbitmap();

            boolean userpic = Utils.isEmptyString(authManager.getUserPic());
            if (authManager.getUserImageUri() != null)
                mUserImagePath = "" + authManager.getUserImageUri().toString();
            if (!Utils.isEmptyString(mUserImagePath))
                mUserImageUri = Utils.getImageContentUri(ClickInBaseView.this, new File(mUserImagePath));


            if (!Utils.isEmptyString("" + mUserImageUri))
                userPic.setImageURI(mUserImageUri);
            else if (imagebitmap1 != null)
                userPic.setImageBitmap(imagebitmap1);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl") && !userpic)
                Picasso.with(ClickInBaseView.this).load(authManager.getUserPic()).error(R.drawable.female_user).into(userPic);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("guy") && !userpic)
                Picasso.with(ClickInBaseView.this).load(authManager.getUserPic()).error(R.drawable.male_user).into(userPic);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl"))
                userPic.setImageResource(R.drawable.female_user);
            else
                userPic.setImageResource(R.drawable.male_user);

        } catch (Exception e) {

            userPic.setImageResource(R.drawable.male_user);
        }

        edt_search.addTextChangedListener(this);

        clickWithlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                if (relationManager.acceptedList.size() > 0 && position >= 1) {
//                    try {removed by akshit
                    partnerName = relationManager.acceptedList.get(position - 1).getPartnerName();
                    String rId = relationManager.acceptedList.get(position - 1).getRelationshipId();
                    partnerPic = relationManager.acceptedList.get(position - 1).getPartnerPic();
                    quickBlockId = relationManager.acceptedList.get(position - 1).getPartnerQBId();


                    ModelManager.getInstance().getAuthorizationManager().partnerQbId = quickBlockId;


                    partnerId = relationManager.acceptedList.get(position - 1).getPartner_id();
                    userClicks = relationManager.acceptedList.get(position - 1).getClicks();
                    myClicks = relationManager.acceptedList.get(position - 1).getUserClicks();
                    partnerPh = relationManager.acceptedList.get(position - 1).getPhoneNo();

                    relationListIndex = (position - 1);

/* prafulll code to set counter to zero */
                    if (relationManager.acceptedList.get(position - 1).getUnreadMsg() != 0) {
                        relationManager.acceptedList.get(position - 1).setUnreadMsg(0);
                        clickInadapter.notifyDataSetChanged();
                    }
/* prafulll code to set counter to zero */
                    //Track event for MixPanel,If Partner name is clicked from left menu.
                    Utils.trackMixpanel(ClickInBaseView.this,"","","LeftMenuPartnerButtonClicked");
                    switchView(rId, relationListIndex);
                }
            }
        });

        searchInviteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if(edt_search.getWindowToken() != null)
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);

                Intent intent = new Intent(ClickInBaseView.this, AddSomeoneView.class);
                intent.putExtra("fromsignup", false);
                startActivity(intent);
            }
        });
        hideSearchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                searchList.setVisibility(View.GONE);
                hideSearchlist.setVisibility(View.GONE);
                edt_search.setText("");
            }
        });

        findFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(edt_search.getWindowToken() != null)
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);

                Intent intent = new Intent(ClickInBaseView.this, CurrentClickersView.class);
                intent.putExtra("FromMenu", true);
                startActivity(intent);

                        /* code for animation prafull*/


                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);


            }
        });

        inviteF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(edt_search.getWindowToken() != null)
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);

                Intent intent = new Intent(ClickInBaseView.this, SpreadWordView.class);
                intent.putExtra("fromProfile", true);
                startActivity(intent);

                        /* code for animation prafull*/

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                //To track through mixPanel. if Invite Button is clicked from Left Menu.
                Utils.trackMixpanel(ClickInBaseView.this,"","","LeftMenuInviteButtonClicked");
            }
        });
        theFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(edt_search.getWindowToken() != null)
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);

                Intent intent = new Intent(ClickInBaseView.this, FeedView.class);
                startActivity(intent);

                        /* code for animation prafull*/

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });

        imageMenuRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(edt_search.getWindowToken() != null)
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);


                Intent intent = new Intent(ClickInBaseView.this, UserProfileView.class);
                intent.putExtra("isChangeInList", true);
                intent.putExtra("updatephoto", Constants.mInAppNotification);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

                ComponentName componentInfo = taskInfo.get(0).topActivity;
                String className = componentInfo.getClassName();
                if (className.equalsIgnoreCase("com.sourcefuse.clickinandroid.view.UserProfileView")) {
                    startActivity(intent);
                    slidemenu.showContent();

                              /* code for animation prafull*/

                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                } else {
                    startActivity(intent);
                              /* code for animation prafull*/

                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                }
                Constants.mInAppNotification = false;

                Utils.trackMixpanel(ClickInBaseView.this,"","","OwnProfileOpened");//To track through mixPanel,If Own Profile is opened from left menu

            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(edt_search.getWindowToken() != null)
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);


                Intent intent = new Intent(ClickInBaseView.this, SettingView.class);
                startActivity(intent);

                        /* code for animation prafull*/

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });

        searchInviteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(edt_search.getWindowToken() != null)
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);

                Intent intent = new Intent(ClickInBaseView.this, AddSomeoneView.class);
                intent.putExtra("FromOwnProfile", true);
                startActivity(intent);

                        /* code for animation prafull*/

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                searchList.setVisibility(View.GONE);
                hideSearchlist.setVisibility(View.GONE);
                edt_search.setText("");
            }
        });


        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @SuppressWarnings("static-access")
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                if (relationManager.fetchUsersByNameData.size() > 0) {

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(edt_search.getWindowToken() != null)
                        imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);


                    String partnerPhone = relationManager.fetchUsersByNameData.get(position).getPhoneNo();
                    Intent intent = new Intent(ClickInBaseView.this, JumpOtherProfileView.class);
                    intent.putExtra("FromOwnProfile", true);
                    intent.putExtra("phNumber", partnerPhone);
                    startActivity(intent);

                        /* code for animation prafull*/

                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    hideSearchlist.setVisibility(View.GONE);
                    searchList.setVisibility(View.GONE);
                    findViewById(R.id.btn_clear).setVisibility(View.GONE);
                    findViewById(R.id.btn_progressBar).setVisibility(View.GONE);
                    ((EditText) findViewById(R.id.edt_search)).setText("");

                }
            }
        });


    }

    public void rightMenuElements() {

        authManager = ModelManager.getInstance().getAuthorizationManager();


        backArrowRightSide = (ImageView) slidemenu.findViewById(R.id.iv_back_right);

        //akshit Code starts for closing secondary menu
        backArrowRightSide.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (slidemenu.isSecondaryMenuShowing() || slidemenu.isMenuShowing()) {
                    slidemenu.toggle();
                }
            }
        });
        //akshit code ends
    }


    public void setNotificationList() { //akshit code ,Implementation of Pull to refresh library.

        notificationAdapter = new NotificationAdapter(ClickInBaseView.this, R.layout.row_notification, ModelManager.getInstance().getNotificationManagerManager().notificationData);
        notificationList = (PullToRefreshListView) slidemenu.findViewById(R.id.list_click_notification);
        notificationList.setMode(PullToRefreshBase.Mode.BOTH);
        notificationList.setAdapter(notificationAdapter);

        if (!Utils.isEmptyString(mLastchatID)) {
            notificationList.getRefreshableView().setSelection(ModelManager.getInstance().getNotificationManagerManager().notificationData.size());
        }

        notificationList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> listViewPullToRefreshBase) {
//                mFirstid = notificationMngr.notificationData.get(0)._id;
                mLastchatID = "";
                ModelManager.getInstance().getNotificationManagerManager().getNotification(getApplicationContext(), "", authManager.getPhoneNo(), authManager.getUsrToken());

            }


            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> listViewPullToRefreshBase) {
                String mLastId = ModelManager.getInstance().getNotificationManagerManager().notificationData.get(ModelManager.getInstance().getNotificationManagerManager().notificationData.size() - 1)._id;
                mLastchatID = mLastId;
                ModelManager.getInstance().getNotificationManagerManager().getNotification(getApplicationContext(), mLastId, authManager.getPhoneNo(), authManager.getUsrToken());
            }


        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().unregister(this);
        }

        EventBus.getDefault().register(this);
    }


    @Override
    protected void onStop() {
        super.onStop();


        EventBus.getDefault().unregister(this);


    }


    @Override
    public void afterTextChanged(Editable s) {


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {


        String search_date = edt_search.getText().toString();
        if (!Utils.isEmptyString(search_date) && search_date.length() < 3) {
            slidemenu.findViewById(R.id.btn_clear).setVisibility(View.VISIBLE);
            slidemenu.findViewById(R.id.btn_progressBar).setVisibility(View.GONE);
        } else if (!Utils.isEmptyString(search_date) && search_date.length() > 2) {

        } else {
            slidemenu.findViewById(R.id.btn_clear).setVisibility(View.GONE);
            slidemenu.findViewById(R.id.btn_progressBar).setVisibility(View.GONE);
        }
        if (edt_search.getText().toString().length() > 2) {

        }


    }


    public void getMenuListData() {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        relationManager = ModelManager.getInstance().getRelationManager();
        setLeftMenuList();


    }

    public void setMenuListData() {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        relationManager = ModelManager.getInstance().getRelationManager();
        userName.setText(authManager.getUserName());
        userPic.setScaleType(ScaleType.FIT_XY);

        String mUserImagePath = null;
        Uri mUserImageUri = null;
        Bitmap imagebitmap1 = authManager.getUserbitmap();

        boolean userpic = Utils.isEmptyString(authManager.getUserPic());
        /* to set downloaded image from server*/
        try {


            if (authManager.getUserImageUri() != null)
                mUserImagePath = "" + authManager.getUserImageUri().toString();
            if (!Utils.isEmptyString(mUserImagePath))
                mUserImageUri = Utils.getImageContentUri(ClickInBaseView.this, new File(mUserImagePath));


            if (!Utils.isEmptyString("" + mUserImageUri))
                userPic.setImageURI(mUserImageUri);
            else if (imagebitmap1 != null)
                userPic.setImageBitmap(imagebitmap1);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl") && !userpic)
                Picasso.with(ClickInBaseView.this).load(authManager.getUserPic()).error(R.drawable.female_user).into(userPic);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("guy") && !userpic)
                Picasso.with(ClickInBaseView.this).load(authManager.getUserPic()).error(R.drawable.male_user).into(userPic);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl"))
                userPic.setImageResource(R.drawable.female_user);
            else
                userPic.setImageResource(R.drawable.male_user);

        } catch (Exception e) {

            userPic.setImageResource(R.drawable.male_user);
        }


        setLeftMenuList();
    }

    protected void onResume() {
        super.onResume();

        if (clickInadapter != null)
            clickInadapter.notifyDataSetChanged();

        EventBus.getDefault().post("update Counter");
    }

    public void onEventMainThread(String message) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        String mTestString = new String(message);

        if (mTestString.contains("UpdateMessageCounter###")) { /* prafulll code to set counter to zero */

            clickInadapter.notifyDataSetChanged();
        } else if (message.equalsIgnoreCase("SearchResult True")) {

            stopSearch = true;
            Utils.dismissBarDialog();
            slidemenu.findViewById(R.id.btn_clear).setVisibility(View.VISIBLE);
            slidemenu.findViewById(R.id.btn_progressBar).setVisibility(View.GONE);

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(edt_search.getWindowToken() != null)
                imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);


            searchList.setVisibility(View.VISIBLE);
            setSearchList();
        } else if (message.equalsIgnoreCase("SearchResult False")) {
            stopSearch = true;
            Utils.dismissBarDialog();
            slidemenu.findViewById(R.id.btn_clear).setVisibility(View.VISIBLE);
            slidemenu.findViewById(R.id.btn_progressBar).setVisibility(View.GONE);

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(edt_search.getWindowToken() != null)
                imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);

            searchList.setVisibility(View.VISIBLE);
            setSearchList();


        } else if (message.equalsIgnoreCase("SearchResult Error")) {
            stopSearch = true;
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(ClickInBaseView.this, AlertMessage.connectionError);

        } else if (message.equalsIgnoreCase("NewsFeed  True")) {
            Utils.dismissBarDialog();


            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(edt_search.getWindowToken() != null)
                imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);

            Intent intent = new Intent(ClickInBaseView.this, FeedView.class);
            startActivity(intent);

                  /* code for animation prafull*/

            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }  //akshit code
        else if (message.equalsIgnoreCase("NewsFeed False")) {

            stopSearch = true;
            Utils.dismissBarDialog();
            newsFeedManager = ModelManager.getInstance().getNewsFeedManager();
            if (newsFeedManager.userFeed != null)
                newsFeedManager.userFeed.clear();


            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(edt_search.getWindowToken() != null)
                imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);


            //ends

        } else if (message.equalsIgnoreCase("NewsFeed Error")) {
            stopSearch = true;
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(ClickInBaseView.this, AlertMessage.connectionError);

        } else if (message.equalsIgnoreCase("Notification true") || message.equalsIgnoreCase("Notification error")) {

            setNotificationList();
            Utils.dismissBarDialog();
            notificationList.onRefreshComplete();

        } else if (message.equalsIgnoreCase("Update DB Message")) {
            //temp code
            Toast.makeText(this, "Message received for other partner", Toast.LENGTH_SHORT).show();
        } else if (message.equalsIgnoreCase("update Counter")) { // prafull code for notification update andrid
            TextView mNotificationText = (TextView) findViewById(R.id.iv_open_right_menu);


            if (ModelManager.getInstance().getAuthorizationManager().getNotificationCounter() > 0) {

                String mValue = "";
                int mNotificationValue = ModelManager.getInstance().getAuthorizationManager().getNotificationCounter();

                if (mNotificationValue > 99) {
                    mValue = "99+";
                } else {
                    mValue = String.valueOf(mNotificationValue);
                }


                if (!slidemenu.isSecondaryMenuShowing()) {//akshit code to hit notification ,on opening secondary menu
                    if (!mNotificationText.getText().toString().equalsIgnoreCase("" + mNotificationValue))
                        Utils.playSound(ClickInBaseView.this, R.raw.notification_inapp);

                    mNotificationText.setText("" + mValue);
                    mNotificationText.setTextColor(Color.parseColor("#39cad4"));
                    ModelManager.getInstance().getNotificationManagerManager().getNotification(this, "",
                            ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                            ModelManager.getInstance().getAuthorizationManager().getUsrToken());

                } else {
                    ModelManager.getInstance().getAuthorizationManager().setNotificationCounter(0);
                    mNotificationText.setText("0");
                    mNotificationText.setTextColor(Color.parseColor("#000000"));
                }


            } else {

                mNotificationText.setText("0");
                mNotificationText.setTextColor(Color.parseColor("#000000"));


            }


        } else if (message.equalsIgnoreCase("updatephoto")) {

            ModelManager.getInstance().getAuthorizationManager().getProfileInfo("", authManager.getPhoneNo(), authManager.getUsrToken());
        }

    }

    public void setSearchList() {
        relationManager = ModelManager.getInstance().getRelationManager();
        searchListadapter = new SearchAdapter(ClickInBaseView.this, R.layout.row_search_list, relationManager.fetchUsersByNameData);
        searchList.setAdapter(searchListadapter);

    }

    @Override
    public void onOpen() {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        authManager = ModelManager.getInstance().getAuthorizationManager();

        String mUserImagePath = null;
        Uri mUserImageUri = null;
        Bitmap imagebitmap1 = authManager.getUserbitmap();
        userName.setText("" + authManager.getUserName());//akshit code
        try {

            boolean userpic = Utils.isEmptyString(authManager.getUserPic());
            if (authManager.getUserImageUri() != null)
                mUserImagePath = "" + authManager.getUserImageUri().toString();
            if (!Utils.isEmptyString(mUserImagePath))
                mUserImageUri = Utils.getImageContentUri(ClickInBaseView.this, new File(mUserImagePath));


            if (!Utils.isEmptyString("" + mUserImageUri))
                userPic.setImageURI(mUserImageUri);
            else if (imagebitmap1 != null)
                userPic.setImageBitmap(imagebitmap1);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl") && !userpic)
                Picasso.with(ClickInBaseView.this).load(authManager.getUserPic()).error(R.drawable.female_user).into(userPic);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("guy") && !userpic)
                Picasso.with(ClickInBaseView.this).load(authManager.getUserPic()).error(R.drawable.male_user).into(userPic);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl"))
                userPic.setImageResource(R.drawable.female_user);
            else
                userPic.setImageResource(R.drawable.male_user);

        } catch (Exception e) {

            userPic.setImageResource(R.drawable.male_user);
        }

        edt_search.setText("");
        hideSearchlist.setVisibility(View.GONE);
        searchList.setVisibility(View.GONE);

        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null)
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


    }


    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClose() {


    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();


        if(getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) { // check window token is null or not
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }



    }


}