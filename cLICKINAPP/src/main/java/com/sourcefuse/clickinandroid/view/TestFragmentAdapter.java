package com.sourcefuse.clickinandroid.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sourcefuse.clickinapp.R;
import com.viewpagerindicator.IconPagerAdapter;


class TestFragmentAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter {

    protected static final int[] CONTENT = new int[]{R.drawable.first_screen, R.drawable.second_screen, R.drawable.third_screen, R.drawable.fourth_screen, R.drawable.fifth_screen, R.drawable.sixth_page};
    protected static final int[] ICONS = new int[]{};

    private int mCount = CONTENT.length;

    public TestFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getIconResId(int index) {
        return ICONS[index % ICONS.length];
    }

}
