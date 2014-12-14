package com.sourcefuse.clickinandroid.view;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ChatManager;
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
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class
        ClickInBaseView extends Activity implements TextWatcher, SlidingMenu.OnOpenListener, SlidingMenu.OnCloseListener {


    public ListView clickWithlistView, searchList;
    public ClickInWithAdapter clickInadapter;
    public Boolean stopSearch = true;
    public EditText edt_search;
    //Right Menu.....
    public ListView notificationList;
    public NotificationAdapter notificationAdapter;
    public NewsFeedManager newsFeedManager;
    public static SlidingMenu slidemenu;
    SimpleSectionedListAdapter simpleSectionedGridAdapter;
    /// Left Menu
    private TextView userName;
    private ImageView userPic, hideSearchlist;
    private AuthManager authManager;
    private RelationManager relationManager;
    private ClickInNotificationManager notificationMngr;
    private Typeface typeface;
    private TextView searchInviteView;
    private LinearLayout theFeed, inviteF, findFriend, setting;
    private String quickBlockId, partnerPic, partnerName, partnerId, myClicks, userClicks, partnerPh;
    private SearchAdapter searchListadapter;
    private RelativeLayout imageMenuRefresh;
    private int relationListIndex;
    private ImageView backArrowRightSide;
    private Bitmap imageBitmap = null;
    private ChatManager chatManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_baseview);
        Log.e("ClickInBaseView1", "onCreate");

        authManager = ModelManager.getInstance().getAuthorizationManager();

    }

    @SuppressWarnings("static-access")
    public void setLeftMenuList() {
        clickInadapter = new ClickInWithAdapter(ClickInBaseView.this, R.layout.row_clickin_with, relationManager.acceptedList);
        String[] mHeaderNames = {"CLICKIN'"};
        String[] mHeaderNames2 = {" WITH"};
        Integer[] mHeaderPositions = {0};

        ArrayList<SimpleSectionedListAdapter.Section> sections = new ArrayList<SimpleSectionedListAdapter.Section>();


        boolean hidevalue = (relationManager.acceptedList.size() > 0) ? true : false;


        sections.add(new SimpleSectionedListAdapter.Section(mHeaderPositions[0], mHeaderNames[0], mHeaderNames2[0]));
        simpleSectionedGridAdapter = new SimpleSectionedListAdapter(ClickInBaseView.this, clickInadapter, R.layout.header_clickwith, R.id.tv_clickintx, R.id.tv_with, hidevalue);
        simpleSectionedGridAdapter.setSections(sections.toArray(new SimpleSectionedListAdapter.Section[0]));
        clickWithlistView.setAdapter(simpleSectionedGridAdapter);
    }

    private void switchView(String rid, int relationListIndex) {

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

                if (slidemenu.isMenuShowing()) {


                }
                if (slidemenu.isSecondaryMenuShowing()) {
                    if (authManager != null) {
                        authManager.setNotificationCounter(0);
                        EventBus.getDefault().post("update Counter");
                    }
                }

            }
        });
        /*
         * Right Menu
         */
        slidemenu.setSecondaryMenu(R.layout.view_notification);
        //slidemenu.setSecondaryShadowDrawable(R.drawable.shadow);
        rightMenuElements();
        setNotificationList();

        slidemenu.setOnClosedListener(new SlidingMenu.OnClosedListener() {
            @Override
            public void onClosed() {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        slidemenu.findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_search.setText("");
                /*try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        });

    }


    public void leftMenuElements() {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        relationManager = ModelManager.getInstance().getRelationManager();
        typeface = Typeface.createFromAsset(ClickInBaseView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
        edt_search = (EditText) slidemenu.findViewById(R.id.edt_search);
        searchList = (ListView) slidemenu.findViewById(R.id.search_list);


        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {


                //praful code
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Log.e("00000", "00000000000");
                    if (!(edt_search.getText().toString().length() > 0)) {
                        hideSearchlist.setVisibility(View.GONE);
                        searchList.setVisibility(View.GONE);
                        try {
                            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        } catch (Exception e) {
                        }
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




            /*searchList.setDivider(getResources().getDrawable(R.drawable.list_divider));
            searchList.setDividerHeight(2);*/


        hideSearchlist = (ImageView) slidemenu.findViewById(R.id.iv_hide_searchlist);

        clickWithlistView = (ListView) slidemenu.findViewById(R.id.click_with_list_menu);

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
            Log.e("in try---->", "in try--->");
            Bitmap imagebitmap1 = authManager.getUserbitmap();
            if (imagebitmap1 != null)
                com.sourcefuse.clickinandroid.utils.Log.e("user bit map not null", "user bit map not null");
            boolean userpic = Utils.isEmptyString(authManager.getUserPic());
            Log.e("user pic url ---->", "" + authManager.getUserPic());
            Log.e("user pc --->", "" + authManager.getUserPic());
            if (imagebitmap1 != null)
                userPic.setImageBitmap(imagebitmap1);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl") && !userpic)
                Picasso.with(ClickInBaseView.this).load(authManager.getUserPic()).skipMemoryCache().error(R.drawable.female_user).into(userPic);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("guy") && !userpic)
                Picasso.with(ClickInBaseView.this).load(authManager.getUserPic()).skipMemoryCache().error(R.drawable.male_user).into(userPic);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl"))
                userPic.setImageResource(R.drawable.female_user);
            else
                userPic.setImageResource(R.drawable.male_user);

        } catch (Exception e) {
            com.sourcefuse.clickinandroid.utils.Log.e("on exception", "on exception");
            userPic.setImageResource(R.drawable.male_user);
        }

        edt_search.addTextChangedListener(this);

        clickWithlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (relationManager.acceptedList.size() > 0 && position >= 2) {
                    try {
                        partnerName = relationManager.acceptedList.get(position - 2).getPartnerName();
                        String rId = relationManager.acceptedList.get(position - 2).getRelationshipId();
                        partnerPic = relationManager.acceptedList.get(position - 2).getPartnerPic();
                        quickBlockId = relationManager.acceptedList.get(position - 2).getPartnerQBId();
                        partnerId = relationManager.acceptedList.get(position - 2).getPartner_id();
                        userClicks = relationManager.acceptedList.get(position - 2).getClicks();
                        myClicks = relationManager.acceptedList.get(position - 2).getUserClicks();
                        partnerPh = relationManager.acceptedList.get(position - 2).getPhoneNo();
                        Log.e("", "position--In..> " + rId);
                        relationListIndex = (position - 2);
                        switchView(rId, relationListIndex);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        searchInviteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                com.sourcefuse.clickinandroid.utils.Log.e("in cickin baseview on searchInviteView ----->", "in cickin baseview on searchInviteView ----->");
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
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(ClickInBaseView.this, CurrentClickersView.class);
                intent.putExtra("FromMenu", true);
                startActivity(intent);

                        /* code for animation prafull*/


                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

//akshit code for closing menu
//                      if(slidemenu.isSecondaryMenuShowing() || slidemenu.isMenuShowing()){
//                          slidemenu.toggle();
//                      }
            }
        });

        inviteF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(ClickInBaseView.this, SpreadWordView.class);
                intent.putExtra("fromProfile", true);
                startActivity(intent);

                        /* code for animation prafull*/

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });
        theFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {


                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(ClickInBaseView.this, FeedView.class);
                startActivity(intent);

                        /* code for animation prafull*/

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });

        imageMenuRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("", "00000000-userPic" + slidemenu);
                Intent intent = new Intent(ClickInBaseView.this, UserProfileView.class);
                intent.putExtra("isChangeInList", true);
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
                    // slidemenu.showContent(true);
                } else {
                    startActivity(intent);
                              /* code for animation prafull*/

                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    //slidemenu.showContent(true);
                }

            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("", "00000000-SettingView Intent");
                Intent intent = new Intent(ClickInBaseView.this, SettingView.class);
                startActivity(intent);

                        /* code for animation prafull*/

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });

        searchInviteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(ClickInBaseView.this, AddSomeoneView.class);
                intent.putExtra("FromOwnProfile", true);
                startActivity(intent);
                com.sourcefuse.clickinandroid.utils.Log.e("in cickin baseview on searchInviteView ----->", "in cickin baseview on searchInviteView ----->");
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
                    try {
                        try {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e("searchList", "searchList Click-->" + position);

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


    public void setNotificationList() {

        notificationMngr = ModelManager.getInstance().getNotificationManagerManager();
        Log.e("NotificationList", "Size" + notificationMngr.notificationData.size());
        notificationAdapter = new NotificationAdapter(ClickInBaseView.this, R.layout.row_notification, notificationMngr.notificationData);
        notificationList.setAdapter(notificationAdapter);

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
        Log.e("2", "onStopClickInBaseView");

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

        Log.e("00000", "00000000000");

        String search_date = edt_search.getText().toString();
        if (!Utils.isEmptyString(search_date) && search_date.length() < 3) {
            slidemenu.findViewById(R.id.btn_clear).setVisibility(View.VISIBLE);
            slidemenu.findViewById(R.id.btn_progressBar).setVisibility(View.GONE);
        } else if (!Utils.isEmptyString(search_date) && search_date.length() > 2) {
            /*slidemenu.findViewById(R.id.btn_clear).setVisibility(View.GONE);
            slidemenu.findViewById(R.id.btn_progressBar).setVisibility(View.VISIBLE);*/
        } else {
            slidemenu.findViewById(R.id.btn_clear).setVisibility(View.GONE);
            slidemenu.findViewById(R.id.btn_progressBar).setVisibility(View.GONE);
        }
        if (edt_search.getText().toString().length() > 2) {
            /*hideSearchlist.setVisibility(View.VISIBLE);
            searchList.setVisibility(View.VISIBLE);
            stopSearch = true;
            if (stopSearch) {
                stopSearch = false;
                relationManager = ModelManager.getInstance().getRelationManager();
                authManager = ModelManager.getInstance().getAuthorizationManager();
                relationManager.fetchusersbyname(edt_search.getText().toString(), authManager.getPhoneNo(), authManager.getUsrToken());
            }*/
        }


    }


    public void getMenuListData() {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        relationManager = ModelManager.getInstance().getRelationManager();
        setLeftMenuList();
        notificationMngr = ModelManager.getInstance().getNotificationManagerManager();
        Log.e("notifacation---->", "notification--->");
        notificationMngr.getNotification(getApplicationContext(), "", authManager.getPhoneNo(), authManager.getUsrToken());
    }

    public void setMenuListData() {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        relationManager = ModelManager.getInstance().getRelationManager();
        userName.setText(authManager.getUserName());
        userPic.setScaleType(ScaleType.FIT_XY);

        /* to set downloaded image from server*/
        try {
            Log.e("in try---->", "in try--->");
            Bitmap imagebitmap1 = authManager.getUserbitmap();
            if (imagebitmap1 != null)
                com.sourcefuse.clickinandroid.utils.Log.e("user bit map not null", "user bit map not null");
            boolean userpic = Utils.isEmptyString(authManager.getUserPic());
            Log.e("user pic url ---->", "" + authManager.getUserPic());
            Log.e("user pc --->", "" + authManager.getUserPic());
            if (imagebitmap1 != null)
                userPic.setImageBitmap(imagebitmap1);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl") && !userpic)
                Picasso.with(ClickInBaseView.this).load(authManager.getUserPic()).skipMemoryCache().error(R.drawable.female_user).into(userPic);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("guy") && !userpic)
                Picasso.with(ClickInBaseView.this).load(authManager.getUserPic()).skipMemoryCache().error(R.drawable.male_user).into(userPic);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl"))
                userPic.setImageResource(R.drawable.female_user);
            else
                userPic.setImageResource(R.drawable.male_user);

        } catch (Exception e) {
            com.sourcefuse.clickinandroid.utils.Log.e("on exception", "on exception");
            userPic.setImageResource(R.drawable.male_user);
        }

        setLeftMenuList();
    }


    public void onEventMainThread(String message) {
        Log.e("onEventMainThread", "onEventMainThread->");
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (message.equalsIgnoreCase("SearchResult True")) {
            Log.e("on true---->", "on true");
            stopSearch = true;
            Utils.dismissBarDialog();
            slidemenu.findViewById(R.id.btn_clear).setVisibility(View.VISIBLE);
            slidemenu.findViewById(R.id.btn_progressBar).setVisibility(View.GONE);
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("1", "message aya->" + message);
            searchList.setVisibility(View.VISIBLE);
            setSearchList();
        } else if (message.equalsIgnoreCase("SearchResult False")) {
            stopSearch = true;
            Utils.dismissBarDialog();
            slidemenu.findViewById(R.id.btn_clear).setVisibility(View.VISIBLE);
            slidemenu.findViewById(R.id.btn_progressBar).setVisibility(View.GONE);
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            searchList.setVisibility(View.VISIBLE);
            setSearchList();

            Log.d("2", "message->" + message);
        } else if (message.equalsIgnoreCase("SearchResult Error")) {
            stopSearch = true;
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(ClickInBaseView.this, AlertMessage.connectionError);
            Log.d("3", "message->" + message);
        } else if (message.equalsIgnoreCase("NewsFeed  True")) {
            Utils.dismissBarDialog();
            Log.d("1", "message aya->" + message);
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(ClickInBaseView.this, FeedView.class);
            startActivity(intent);

                  /* code for animation prafull*/

            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }  //akshit code
        else if (message.equalsIgnoreCase("NewsFeed False")) {
            Log.d("2", "message->" + message);
            stopSearch = true;
            Utils.dismissBarDialog();
            newsFeedManager = ModelManager.getInstance().getNewsFeedManager();
            if (newsFeedManager.userFeed != null)
                newsFeedManager.userFeed.clear();

            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //ends

        } else if (message.equalsIgnoreCase("NewsFeed Error")) {
            stopSearch = true;
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(ClickInBaseView.this, AlertMessage.connectionError);
            Log.d("3", "message->" + message);
        } else if (message.equalsIgnoreCase("Notification true")) {
            setNotificationList();
        } else if (message.equalsIgnoreCase("Update DB Message")) {
            //temp code
            Toast.makeText(this, "Message received for other partner", Toast.LENGTH_SHORT).show();
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        try {
            userName.setText(authManager.getUserName());//akshit code

            Log.e("in try---->", "in try--->");
            Bitmap imagebitmap1 = authManager.getUserbitmap();
            if (imagebitmap1 != null)
                com.sourcefuse.clickinandroid.utils.Log.e("user bit map not null", "user bit map not null");
            boolean userpic = Utils.isEmptyString(authManager.getUserPic());
            Log.e("user pic url ---->", "" + authManager.getUserPic());
            Log.e("user pc --->", "" + authManager.getUserPic());
            if (imagebitmap1 != null)
                userPic.setImageBitmap(imagebitmap1);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl") && !userpic)
                Picasso.with(ClickInBaseView.this).load(authManager.getUserPic()).skipMemoryCache().error(R.drawable.female_user).into(userPic);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("guy") && !userpic)
                Picasso.with(ClickInBaseView.this).load(authManager.getUserPic()).skipMemoryCache().error(R.drawable.male_user).into(userPic);
            else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl"))
                userPic.setImageResource(R.drawable.female_user);
            else
                userPic.setImageResource(R.drawable.male_user);

        } catch (Exception e) {
            com.sourcefuse.clickinandroid.utils.Log.e("on exception", "on exception");
            userPic.setImageResource(R.drawable.male_user);
        }
        try {
            edt_search.setText("");
            hideSearchlist.setVisibility(View.GONE);
            searchList.setVisibility(View.GONE);

            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }


    }


    public void onDestroy() {
        super.onDestroy();
        Log.e("CLickinbaseview", "Destroy");
    }

    @Override
    public void onClose() {
        Log.e("y", "if onClose");

    }


}