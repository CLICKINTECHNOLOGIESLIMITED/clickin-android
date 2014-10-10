package com.sourcefuse.clickinandroid.view;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ClickInNotificationManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.NewsFeedManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.ClickInWithAdapter;
import com.sourcefuse.clickinandroid.view.adapter.NotificationAdapter;
import com.sourcefuse.clickinandroid.view.adapter.SearchAdapter;
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter1;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class ClickInBaseView extends Activity implements TextWatcher, SlidingMenu.OnOpenListener, SlidingMenu.OnCloseListener {


    /// Left Menu
    private TextView userName;
    private ImageView userPic,hideSearchlist;
    private AuthManager authManager;
    private RelationManager relationManager;
    private ClickInNotificationManager notificationMngr;
    private Typeface typeface;
    private Button searchInviteView;
    private LinearLayout theFeed, inviteF, findFriend, setting;
    public ListView clickWithlistView, searchList;
    public ClickInWithAdapter clickInadapter;
    private String quickBlockId, partnerPic, partnerName,partnerId,myClicks,userClicks;
    public Boolean stopSearch = true;
    private EditText edt_search;
    private SearchAdapter searchListadapter;
    private RelativeLayout imageMenuRefresh;

    //Right Menu.....
    public ListView notificationList;
    private ImageView backArrowRightSide;
    public NotificationAdapter notificationAdapter;
    public NewsFeedManager newsFeedManager;

    SlidingMenu slidemenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_baseview);
        Log.e("ClickInBaseView1", "onCreate");


    }

    @SuppressWarnings("static-access")
    public void setLeftMenuList() {
        clickInadapter = new ClickInWithAdapter(ClickInBaseView.this, R.layout.row_clickin_with, relationManager.acceptedList);
        String[] mHeaderNames = {"CLICKIN"};
        String[] mHeaderNames2 = {"WITH"};
        Integer[] mHeaderPositions = {0};

        ArrayList<SimpleSectionedListAdapter1.Section> sections = new ArrayList<SimpleSectionedListAdapter1.Section>();
        SimpleSectionedListAdapter1 simpleSectionedGridAdapter ;

        sections.add(new SimpleSectionedListAdapter1.Section(mHeaderPositions[0], mHeaderNames[0], mHeaderNames2[0]));
        simpleSectionedGridAdapter = new SimpleSectionedListAdapter1(ClickInBaseView.this, clickInadapter, R.layout.header_clickwith, R.id.tv_clickintx, R.id.tv_with);
        simpleSectionedGridAdapter.setSections(sections.toArray(new SimpleSectionedListAdapter1.Section[0]));
        clickWithlistView.setAdapter(simpleSectionedGridAdapter);
    }

    private void switchView(String rid) {

        Intent intent = new Intent(ClickInBaseView.this, ChatRecordView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("quickId", quickBlockId);
        intent.putExtra("partnerPic", partnerPic);
        intent.putExtra("partnerName", partnerName);
        intent.putExtra("rId", rid);
        intent.putExtra("partnerId", partnerId);
        intent.putExtra("myClicks", myClicks);
        intent.putExtra("userClicks", userClicks);




        startActivity(intent);

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

        if (setData) {
            getMenuListData();
        } else {
            setMenuListData();

        }

        slidemenu.setOnCloseListener(ClickInBaseView.this);
        slidemenu.setOnOpenListener(ClickInBaseView.this);
        
        /*
         * Right Menu
         */
        slidemenu.setSecondaryMenu(R.layout.view_notification);
        //slidemenu.setSecondaryShadowDrawable(R.drawable.shadow);
        rightMenuElements();
        setNotificationList();


    }


    public void leftMenuElements() {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        relationManager = ModelManager.getInstance().getRelationManager();
        typeface = Typeface.createFromAsset(ClickInBaseView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
        edt_search = (EditText) slidemenu.findViewById(R.id.edt_search);
        searchList = (ListView) slidemenu.findViewById(R.id.search_list);
        hideSearchlist = (ImageView) slidemenu.findViewById(R.id.iv_hide_searchlist);

        clickWithlistView = (ListView) slidemenu.findViewById(R.id.click_with_list_menu);
        clickWithlistView.setDivider(getResources().getDrawable(R.drawable.list_divider));
        clickWithlistView.setDividerHeight(2);
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
        searchInviteView = (Button) searchfooter.findViewById(R.id.btn_invite_view);



        userName.setText(authManager.getUserName());
        userPic.setScaleType(ScaleType.FIT_XY);
        Picasso.with(ClickInBaseView.this).load(authManager.getUserPic())
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .into(userPic);

        edt_search.addTextChangedListener(this);

        clickWithlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (relationManager.acceptedList.size() > 0 && position>=2) {
                    try {
                        partnerName = relationManager.acceptedList.get(position - 2).getPartnerName();
                        String rId = relationManager.acceptedList.get(position - 2).getRelationshipId();
                        partnerPic = relationManager.acceptedList.get(position - 2).getPartnerPic();
                        quickBlockId = relationManager.acceptedList.get(position - 2).getPartnerQBId();
                        partnerId = relationManager.acceptedList.get(position - 2).getPartner_id();
                        myClicks = relationManager.acceptedList.get(position - 2).getClicks();
                        userClicks = relationManager.acceptedList.get(position - 2).getUserClicks();
                        Log.e("", "position--In..> " + rId);
                        switchView(rId);

                    } catch (Exception e) {
                    }
                }
            }
        });

        searchInviteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ClickInBaseView.this, AddSomeoneView.class);
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
                Intent intent = new Intent(ClickInBaseView.this, CurrentClickersView.class);
                intent.putExtra("FromMenu", true);
                startActivity(intent);
            }
        });

        inviteF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ClickInBaseView.this, SpreadWordView.class);
                startActivity(intent);
            }
        });
        theFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                newsFeedManager = ModelManager.getInstance().getNewsFeedManager();
               // newsFeedManager.fetchNewsFeed(lastNewsfeedId, phone, usertoken);
                newsFeedManager.fetchNewsFeed("",authManager.getPhoneNo(), authManager.getUsrToken());
                Utils.launchBarDialog(ClickInBaseView.this);
            }
        });

        imageMenuRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.e("","00000000-userPic"+slidemenu);
                Intent intent = new Intent(ClickInBaseView.this, UserProfileView.class);
                startActivity(intent);
               // slidemenu.animate();
              //  slidemenu.showMenu(true);
                /*slidemenu.showMenu(true);
                slidemenu.showMenu();
                authManager = ModelManager.getInstance().getAuthorizationManager();
                authManager.getProfileInfo("",authManager.getPhoneNo(),authManager.getUsrToken());*/
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.e("","00000000-SettingView Intent");
                Intent intent = new Intent(ClickInBaseView.this, SettingView.class);
                startActivity(intent);
            }
        });

        searchInviteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ClickInBaseView.this, AddSomeoneView.class);
                intent.putExtra("FromOwnProfile", true);
                startActivity(intent);
                searchList.setVisibility(View.GONE);
                hideSearchlist.setVisibility(View.GONE);
                edt_search.setText("");
            }
        });


        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @SuppressWarnings("static-access")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (relationManager.fetchUsersByNameData.size() > 0) {
                    try {
                        Log.e("searchList", "searchList Click-->" + position);
                       /* authManager = ModelManager.getInstance().getAuthorizationManager();
                        String partnerPhone = relationManager.fetchUsersByNameData.get(position).getPhoneNo();
                        Log.e("searchList", "searchList Click-->" + partnerPhone);
                        authManager.sendNewRequest(authManager.getPhoneNo(), partnerPhone, authManager.getUsrToken());*/
                        String partnerPhone = relationManager.fetchUsersByNameData.get(position).getPhoneNo();
                        Intent intent = new Intent(ClickInBaseView.this, JumpOtherProfileView.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("FromOwnProfile", true);
                        intent.putExtra("phNumber", partnerPhone);
                        startActivity(intent);
                        hideSearchlist.setVisibility(View.GONE);
                        searchList.setVisibility(View.GONE);
                    } catch (Exception e) {
                    }
                }
            }
        });


    }

    public void rightMenuElements() {

        authManager = ModelManager.getInstance().getAuthorizationManager();
        notificationMngr = ModelManager.getInstance().getNotificationManagerManager();
        typeface = Typeface.createFromAsset(ClickInBaseView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
        notificationList = (ListView) slidemenu.findViewById(R.id.list_click_notification);
        backArrowRightSide = (ImageView) slidemenu.findViewById(R.id.iv_back_right);
        backArrowRightSide.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                slidemenu.showSecondaryMenu(false);
            }
        });

    }


    public void setNotificationList() {

        notificationMngr = ModelManager.getInstance().getNotificationManagerManager();
        Log.e("NotificationList", "Size" + notificationMngr.notificationData.size());
        notificationAdapter = new NotificationAdapter(ClickInBaseView.this, R.layout.row_notification, notificationMngr.notificationData);
        notificationList.setAdapter(notificationAdapter);

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(EventBus.getDefault().isRegistered(this)){

            EventBus.getDefault().unregister(this);
        }

        EventBus.getDefault().register(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e("2", "onStopClickInBaseView");

        EventBus.getDefault().unregister(this);



       /* if (new MyPreference(getApplicationContext()).isLogin()) {

            Log.e("ClickInBaseView1", "MyPreference");
            if (ModelManager.getInstance() == null) {
                Log.e("ClickInBaseView1", "getInstance");
                authManager = ModelManager.getInstance().getAuthorizationManager();
                authManager.setUsrToken(new MyPreference(ClickInBaseView.this).getToken());
                authManager.setPhoneNo(new MyPreference(ClickInBaseView.this).getmyPhoneNo());
                Intent intent = new Intent(ClickInBaseView.this, UserProfileView.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //this.finish();
            }

        }*/


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

        Log.e("00000", "00000000000");
        if (!(edt_search.getText().toString().length() > 0)) {
            hideSearchlist.setVisibility(View.GONE);
            searchList.setVisibility(View.GONE);
            try {
                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }catch (Exception e){}
        }

            if (edt_search.getText().toString().length() > 2) {
                hideSearchlist.setVisibility(View.VISIBLE);
                searchList.setVisibility(View.VISIBLE);
                stopSearch = true;
                if(stopSearch) {
                    stopSearch = false;
                relationManager = ModelManager.getInstance().getRelationManager();
                authManager = ModelManager.getInstance().getAuthorizationManager();
                relationManager.fetchusersbyname(edt_search.getText().toString(), authManager.getPhoneNo(), authManager.getUsrToken());
            }
        }


    }


    public void getMenuListData() {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        relationManager = ModelManager.getInstance().getRelationManager();
        setLeftMenuList();
        notificationMngr = ModelManager.getInstance().getNotificationManagerManager();
        notificationMngr.getNotification("", authManager.getPhoneNo(), authManager.getUsrToken());
    }

    public void setMenuListData() {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        relationManager = ModelManager.getInstance().getRelationManager();
        userName.setText(authManager.getUserName());
        userPic.setScaleType(ScaleType.FIT_XY);
        Picasso.with(ClickInBaseView.this).load(authManager.getUserPic())
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .into(userPic);
        setLeftMenuList();
    }




    public void onEventMainThread(String message) {
        Log.d("onEventMainThread", "onEventMainThread->" );
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (message.equalsIgnoreCase("SearchResult True")) {
            stopSearch = true;
            Utils.dismissBarDialog();
            Log.d("1", "message aya->" + message);
            searchList.setVisibility(View.VISIBLE);
            setSearchList();
        } else if (message.equalsIgnoreCase("SearchResult False")) {
            stopSearch = true;
            Utils.dismissBarDialog();
            Utils.showAlert(ClickInBaseView.this, authManager.getMessage());
            Log.d("2", "message->" + message);
        } else if (message.equalsIgnoreCase("SearchResult Error")) {
            stopSearch = true;
            Utils.dismissBarDialog();
            Utils.showAlert(ClickInBaseView.this, AlertMessage.connectionError);
            Log.d("3", "message->" + message);
        }else if (message.equalsIgnoreCase("NewsFeed  True")) {
            Utils.dismissBarDialog();
            Log.d("1", "message aya->" + message);
            Intent intent = new Intent(ClickInBaseView.this, FeedView.class);
            startActivity(intent);
        } else if (message.equalsIgnoreCase("NewsFeed False")) {
            Log.d("2", "message->" + message);
            stopSearch = true;
            Utils.dismissBarDialog();
            newsFeedManager.userFeed.clear();
//            Utils.showAlert(ClickInBaseView.this, authManager.getMessage());
            Intent intent = new Intent(ClickInBaseView.this, FeedView.class);

            startActivity(intent);
            Log.d("2", "message->" + message);
        } else if (message.equalsIgnoreCase("NewsFeed Error")) {
            stopSearch = true;
            Utils.dismissBarDialog();
            Utils.showAlert(ClickInBaseView.this, AlertMessage.connectionError);
            Log.d("3", "message->" + message);
        }

    }

    public void setSearchList() {
        relationManager = ModelManager.getInstance().getRelationManager();
        searchListadapter = new SearchAdapter(ClickInBaseView.this, R.layout.row_search_list, relationManager.fetchUsersByNameData);
        searchList.setAdapter(searchListadapter);

    }

    @Override
    public void onOpen() {
        Log.e("y", "if onOpen");
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if(authManager.isMenuUserInfoFlag()) {
            userName.setText(authManager.getUserName());
            userPic.setScaleType(ScaleType.FIT_XY);
            Picasso.with(ClickInBaseView.this).load(authManager.getUserPic())
                    .skipMemoryCache()
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(userPic);
            authManager.setMenuUserInfoFlag(false);
        }else{
            Picasso.with(ClickInBaseView.this).load(authManager.getUserPic())
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(userPic);
        }
        try {
        edt_search.setText("");
            hideSearchlist.setVisibility(View.GONE);
            searchList.setVisibility(View.GONE);

                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }catch (Exception e){}



    }

    @Override
    public void onClose() {
        Log.e("y", "if onClose");


    }
}