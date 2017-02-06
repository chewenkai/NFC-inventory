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
                if (tab1 == null)
                    return new ItemsListFrag();
                else
                    return tab1;
            case 1:  // Detail
                if (tab2 == null || tab2.view ==null) {
                    tab2 = new ItemDetailFrag();
                    return tab2;
                }
                else
                    return tab2;
            case 2:  // Edit
                if (tab3 == null || tab3.view ==null) {
                    tab3 =  new ItemsEditFrag();
                    return tab3;
                }
                else
                    return tab3;
            case 3:  // Setting
                if (tab4 == null)
                    return new SettingFrag();
                else
                    return tab4;
            default:
                return new SettingFrag();
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}