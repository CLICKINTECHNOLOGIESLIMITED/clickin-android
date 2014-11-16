package com.sourcefuse.clickinandroid.view;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.fragment.FragmentCustomTab;
import com.sourcefuse.clickinandroid.fragment.PartyCardFragment;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.bean.TabBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

/**
 * Created by mukesh on 25/8/14.
 */


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CardView extends FragmentActivity{
    private static final String TAG = CardView.class.getSimpleName();

    private ImageView mBackButton;
    public TabHost tabHost;
    public TextView text ;
    private ChatManager chatManager;
    private AuthManager authManager;
    private TabBean bean;
    String card ;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        bean = new TabBean();

        chatManager = ModelManager.getInstance().getChatManager();
        authManager = ModelManager.getInstance().getAuthorizationManager();
        Utils.launchBarDialog(CardView.this);
        chatManager.fetchCards(authManager.getPhoneNo(),authManager.getUsrToken());

    }


    private  void  setView(){
        setContentView(R.layout.view_tabhost2);

        mBackButton = (ImageView) findViewById(R.id.iv_back_trade);
        final LinearLayout l = (LinearLayout)findViewById(R.id.Linear_layout);
        final TabWidget tabWidget = (TabWidget)findViewById(android.R.id.tabs);

        final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) findViewById(R.id.H_view);
        text = (TextView) findViewById(R.id.Layout_Tab_2);
        horizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                horizontalScrollView.smoothScrollTo(l.getLeft(), 0);

            }
        });



        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();
        // Adding Tabs
        for (int i =0;i<chatManager.tabArray.size(); i++) {

            //actionBar.addTab(actionBar.newTab().setText(chatManager.tabArray.get(i).getCategoriesName())
            //      .setTabListener(this));

            TabHost.TabSpec spec1 = tabHost.newTabSpec("TAB" + i);
            spec1.setContent(R.id.Layout_Tab_2);
            spec1.setIndicator(chatManager.tabArray.get(i).getCategoriesName());

            tabHost.addTab(spec1);
            tabHost.setCurrentTab(0);
            tabHost.getTabWidget().setStripEnabled(false);
//            Typeface localTypeface1 = Typeface.createFromAsset(getAssets(),
//                    "fonts/AvenirNextLTPro-BoldCn_0.otf");
            settabStartup();
            //setTbWidth();
           // setTabTextSize();


           setabColor();
        }





        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {

                int pos = tabHost.getCurrentTab();
                bean.setTab_content(chatManager.tabArray.get(pos).getCategoriesName());
                card = bean.getTab_content();
                tabHost.getTabWidget().getChildAt(pos);
                //tabHost.getCurrentTabView().setBackgroundResource();
               // tabdrawable();
                 setTabTextColor();
               if(card.equals("Custom")){

                   FragmentManager fm = getSupportFragmentManager();
                   FragmentCustomTab cfm = new FragmentCustomTab();
                   fm.beginTransaction().replace(R.id.framelayout,cfm ).commit();

               }else {
                   FragmentManager fm = getSupportFragmentManager();
//                android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
                   PartyCardFragment pf = new PartyCardFragment();
                   fm.beginTransaction().replace(R.id.framelayout, pf).commit();
               }

            }


        });


        /**
         * on swiping the viewpager make respective tab selected
//         * */
//        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            @Override
//            public void onPageSelected(int pos) {
//                // on changing the page
//                // make respected tab selected
//                // main.scrollTo(l.getLeft(),0);
//
//                tabHost.setCurrentTab(pos);
//            }
//
//            @Override
//            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
//
//                View tabview = tabHost.getTabWidget().getChildAt(pos);
//                if(tabview!=null){
//                    final int width = horizontalScrollView.getWidth();
//                    final int scrollposition = tabview.getLeft()-(width - tabview.getWidth())/2 ;
//                    horizontalScrollView.smoothScrollTo(scrollposition, 0);
//                }
//                else{
//                    horizontalScrollView.scrollBy(positionOffsetPixels, 0);
//
//                }
//
//            }
//            @Override
//            public void onPageScrollStateChanged(int arg0) {
//            }
//        });


        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
                overridePendingTransition(R.anim.slide_in_finish_up,R.anim.slide_out_finish_up);
            }
        });
    }


//    private void setTbWidth() {
//        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
//        {
//            tabHost.getTabWidget().getChildAt(i).getLayoutParams().width = 145 ;
//
//        }
//    }

    private void tabdrawable(){

        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);

        }

        TextView tv = (TextView) tabHost.getCurrentTabView().findViewById(android.R.id.title);
        tv.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.tab_underline_selector);


    }
    private void setTabTextColor(){


        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                    tv.setTextColor(Color.parseColor("#999999"));
                }

                    TextView tv = (TextView) tabHost.getCurrentTabView().findViewById(android.R.id.title);
                    tv.setTextColor(Color.parseColor("#39cad4"));

                   // tv.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.tabbottomline);



        }
    private void setabColor() {

        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
        {
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#f4f4f4"));
        }


    }

    private void settabStartup() {
        //tabHost.getTabWidget().setBackgroundDrawable( getResources().getDrawable(R.drawable.underline));
        setTabTextColor();
        TextView tv = (TextView) tabHost.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
        tv.setTextColor(Color.parseColor("#40e0d0"));
        bean.setTab_content(chatManager.tabArray.get(0).getCategoriesName());
        FragmentManager fm = getSupportFragmentManager();
        PartyCardFragment pf = new PartyCardFragment();
        fm.beginTransaction().replace(R.id.framelayout,pf).commit();
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
            Utils.dismissBarDialog();
            setView();
        } else if (getMsg.equalsIgnoreCase("FetchCard False")) {
            Utils.dismissBarDialog();

        } else if (getMsg.equalsIgnoreCase("FetchCard Network Error")) {
            Utils.dismissBarDialog();
            Utils.showAlert(CardView.this, AlertMessage.connectionError);
        }
    }

}


