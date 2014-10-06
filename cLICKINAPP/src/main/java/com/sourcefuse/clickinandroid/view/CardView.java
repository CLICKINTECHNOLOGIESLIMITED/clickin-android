package com.sourcefuse.clickinandroid.view;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.TabsPagerAdapter;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

/**
 * Created by mukesh on 25/8/14.
 */


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CardView extends FragmentActivity implements ActionBar.TabListener{
    private static final String TAG = CardView.class.getSimpleName();
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private ImageView mBackButton;

    private ChatManager chatManager;
    private AuthManager authManager;


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       /* chatManager = ModelManager.getInstance().getChatManager();
        authManager = ModelManager.getInstance().getAuthorizationManager();
        chatManager.fetchCards(authManager.getPhoneNo(), authManager.getUsrToken());*/

        setView();
    }


    private  void  setView(){
        setContentView(R.layout.view_card);

        mBackButton = (ImageView) findViewById(R.id.iv_back_trade);
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
       // actionBar.setHomeButtonEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(false);
       // actionBar.setDisplayUseLogoEnabled(false);
       // actionBar.setDisplayShowTitleEnabled(true);
       // actionBar.setDisplayShowCustomEnabled(false);



       /* LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.cardview_header, null);

        View homeIcon = findViewById(android.R.id.home);
        ((View) homeIcon.getParent()).setVisibility(View.GONE);
        ((View) homeIcon).setVisibility(View.GONE);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setCustomView(v);*/
        for (int i =0;i<10; i++) {
            actionBar.addTab(actionBar.newTab().setText("DONE")
                    .setTabListener(this));
        }

        View customNav = LayoutInflater.from(this).inflate(R.layout.cardview_header, null);


        ActionBar.LayoutParams lp = new ActionBar
                .LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(customNav, lp);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);




        // Adding Tabs
        /*for (int i =0;i<10; i++) {
            actionBar.addTab(actionBar.newTab().setText(chatManager.tabArray.get(i).getCategoriesName())
                    .setTabListener(this));
        }
*/
/*

           for (int i =0;i<10; i++) {
            actionBar.addTab(actionBar.newTab().setText("DONE")
                    .setTabListener(this));
        }
*/




        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });


        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });


    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
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
        if (getMsg.equalsIgnoreCase("FetchCard True")) {
            setView();
        } else if (getMsg.equalsIgnoreCase("FetchCard False")) {
            Utils.dismissBarDialog();

        } else if (getMsg.equalsIgnoreCase("FetchCard Network Error")) {
            Utils.dismissBarDialog();
            Utils.showAlert(CardView.this, AlertMessage.connectionError);
        }
    }


}


