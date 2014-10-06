package com.sourcefuse.clickinandroid.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sourcefuse.clickinandroid.fragment.PartyCardFragment;
import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.ModelManager;

//import info.androidhive.tabsswipe.MoviesFragment;
//import info.androidhive.tabsswipe.TopRatedFragment;

/**
 * Created by mukesh on 26/8/14.
 */


public class TabsPagerAdapter extends FragmentPagerAdapter {
    private ChatManager chatManager;

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

       /* switch (index) {
            // Top Rated fragment activity
            return new PartyCardFragment();
            case 1:
                // Games fragment activity
                return new PartyCardFragment();
            case 2:
                // Movies fragment activity
                return new PartyCardFragment();
            case 3:
                // Movies fragment activity
                return new PartyCardFragment();

            default:
                return null;

        }*/

        return new PartyCardFragment();
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
       /* chatManager = ModelManager.getInstance().getChatManager();
        return (chatManager.categories.size());*/

        chatManager = ModelManager.getInstance().getChatManager();
        return 10;
    }

}