package com.kevin.rfidmanager.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kevin.rfidmanager.Fragments.ItemDetailFrag;
import com.kevin.rfidmanager.Fragments.ItemsEditFrag;
import com.kevin.rfidmanager.Fragments.ItemsListFrag;
import com.kevin.rfidmanager.Fragments.SettingFrag;

/**
 * Created by Kevin on 2017/1/26.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public ItemsListFrag tab1 = new ItemsListFrag();
    public ItemDetailFrag tab2 = new ItemDetailFrag();
    public ItemsEditFrag tab3 = new ItemsEditFrag();
    public SettingFrag tab4 = new SettingFrag();

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return tab1;
            case 1:  // Detail
                return tab2;
            case 2:  // Edit
                return tab3;
            case 3:  // Setting
                return tab4;
            default:
                return tab4;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}