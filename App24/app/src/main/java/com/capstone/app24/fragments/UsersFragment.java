package com.capstone.app24.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.app24.R;
import com.capstone.app24.adapters.ViewPagerAdapterProfile;
import com.capstone.app24.sliding_tabs.SlidingTabLayout;

/**
 * Created by amritpal on 4/11/15.
 */
public class UsersFragment extends Fragment {
    View mView;
    private ViewPager pager;
    private SlidingTabLayout tabs;
    ViewPagerAdapterProfile adapter_profile;
    CharSequence profile_titles[] = {"Users", "Posts"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_users, container, false);
        // initializeViews();
        // updateUI();
        return mView;
    }

    private void initializeViews() {
        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) mView.findViewById(R.id.pager);
        ViewGroup.LayoutParams params = pager.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        pager.setLayoutParams(params);
    }

    private void updateUI() {
        adapter_profile = new ViewPagerAdapterProfile(getActivity().getSupportFragmentManager(),
                profile_titles,
                profile_titles.length);
        pager.setAdapter(adapter_profile);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) mView.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }


}
