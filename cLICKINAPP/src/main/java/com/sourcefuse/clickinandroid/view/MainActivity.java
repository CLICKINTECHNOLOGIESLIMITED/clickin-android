package com.sourcefuse.clickinandroid.view;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.sourcefuse.clickinapp.R;

import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity implements ActionBar.TabListener {
    private ViewPager viewPager;
    ActionBar bar;
    List<Fragment> fragList = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_card);
        viewPager = (ViewPager) findViewById(R.id.pager);
         bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        for (int i=1; i <= 30; i++) {
            Tab tab = bar.newTab();
            tab.setText("Tab " + i);
            tab.setTabListener(this);
            bar.addTab(tab);
        }


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                bar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());

        Fragment f = null;
        TabFragment tf = null;

        if (fragList.size() > tab.getPosition())
            fragList.get(tab.getPosition());

        if (f == null) {
            tf = new TabFragment();
            Bundle data = new Bundle();
            data.putInt("idx",  tab.getPosition());
            tf.setArguments(data);
            fragList.add(tf);
        }
        else
            tf = (TabFragment) f;

        fragmentTransaction.replace(android.R.id.content, tf);


    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {

        if (fragList.size() > tab.getPosition()) {
            fragmentTransaction.remove(fragList.get(tab.getPosition()));
        }

    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {

    }
}