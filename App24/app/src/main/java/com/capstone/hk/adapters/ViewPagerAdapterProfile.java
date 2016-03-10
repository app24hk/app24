package com.capstone.hk.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.capstone.hk.bean.UserFeedModel;
import com.capstone.hk.fragments.PostsFragment;
import com.capstone.hk.fragments.UsersFragment;
import com.capstone.hk.utils.Constants;

import java.util.ArrayList;

/**
 * Created by amritpal on 3/11/15.
 */
public class ViewPagerAdapterProfile extends FragmentStatePagerAdapter {
    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    ArrayList<UserFeedModel> mMainList;
    String mSearch;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapterProfile(FragmentManager fm, CharSequence mTitles[], int
            mNumbOfTabsumb, ArrayList<UserFeedModel> mainList, String search) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        mMainList = mainList;
        mSearch = search;
    }


    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Bundle bundle;
        Fragment frag = null;
        switch (position) {
            case 0:
                frag = new UsersFragment();
                bundle = new Bundle();
                bundle.putParcelableArrayList(Constants.SEARCH_LIST, mMainList);
                bundle.putString(Constants.SEARCH_ELEMENT, mSearch);
                frag.setArguments(bundle);
                break;
            case 1:
                frag = new PostsFragment();
                bundle = new Bundle();
                bundle.putParcelableArrayList(Constants.SEARCH_LIST, mMainList);
                bundle.putString(Constants.SEARCH_ELEMENT, mSearch);
                frag.setArguments(bundle);
                break;


        }

        return frag;

    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
