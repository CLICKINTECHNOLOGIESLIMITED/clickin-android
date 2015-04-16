package com.sourcefuse.clickinandroid.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;


import com.sourcefuse.clickinandroid.utils.CustomCirclePageIndicator;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;
import com.viewpagerindicator.PageIndicator;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Gagnjot Singh on 25/7/14.
 */
public class CoverFlow extends FragmentActivity {

    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cover_flow);

        if (Utils.mStartExceptionTrack)
            Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

        try {
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            Boolean coverFlow = sharedPreferences.getBoolean("coverFlow", false);

            if (coverFlow) {
                Intent splashView = new Intent(CoverFlow.this, SplashView.class);
                startActivity(splashView);
                finish();
            }
        } catch (Exception e) {

        }

        TestFragmentAdapter mAdapter = new TestFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        PageIndicator mIndicator = (CustomCirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        Timer timer = new Timer();
        MyTimerTask myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 3000, 3000);

    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {


            runOnUiThread(new Runnable() {

                @Override
                public void run() {


                    if (mPager.getCurrentItem() != 5)
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                }
            });
        }
    }
}
