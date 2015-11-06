package com.capstone.app24.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.capstone.app24.fragments.GalleryFragment;

/**
 * Created by amritpal on 6/11/15.
 */
public class GalleryPagerAdapter extends FragmentStatePagerAdapter {


    public GalleryPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new GalleryFragment();
    }

    @Override
    public int getCount() {
        return 1;
    }
}