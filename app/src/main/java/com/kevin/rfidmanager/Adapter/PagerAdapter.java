package com.kevin.rfidmanager.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kevin.rfidmanager.Fragments.ItemsAddFrag;
import com.kevin.rfidmanager.Fragments.ItemsListFrag;
import com.kevin.rfidmanager.Fragments.SettingFrag;

/**
 * Created by Kevin on 2017/1/26.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ItemsListFrag tab1 = new ItemsListFrag();
                return tab1;
            case 1:  // Edit
                ItemsAddFrag tab3 = new ItemsAddFrag();
                return tab3;
            case 2:  // Delete
                ItemsAddFrag tab4 = new ItemsAddFrag();
                return tab4;
            case 3:  // Add
                ItemsAddFrag tab5 = new ItemsAddFrag();
                return tab5;
            case 4:  // Setting
                SettingFrag tab6 = new SettingFrag();
                return tab6;
            default:
                ItemsAddFrag tab7 = new ItemsAddFrag();
                return tab7;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}