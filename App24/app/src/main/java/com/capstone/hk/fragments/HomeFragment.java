package com.capstone.hk.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.capstone.hk.R;
import com.capstone.hk.activities.MainActivity;
import com.capstone.hk.adapters.ViewPagerAdapterHome;
import com.capstone.hk.sliding_tabs.SlidingTabLayout;

import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amritpal on 5/11/15.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    View mView;
    private static ViewPager pager;
    ViewPagerAdapterHome adapter_home;
    CharSequence home_titles[] = {"最新", "最熱"};
    SweetAlertDialog dialog;
    private Timer timer;
    final Handler handler = new Handler();
    private TimerTask timerTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews();
        setClickListeners();
        updateUI();
/*
        Utils.setOnScrolling(this);
*/
        return mView;

    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.getBottomLayout().animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        MainActivity.tabs.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    private void updateUI() {
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter_home = new ViewPagerAdapterHome(getActivity().getSupportFragmentManager(), home_titles,
                home_titles.length, dialog);
        pager.setAdapter(adapter_home);
        // Assiging the Sliding Tab Layout View

        MainActivity.tabs.setDistributeEvenly(true);
        // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        MainActivity.tabs.setViewPager(pager);
        MainActivity.tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                MainActivity.getBottomLayout().animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                MainActivity.tabs.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void initializeViews() {
        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) mView.findViewById(R.id.pager);
        ViewGroup.LayoutParams params = pager.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        pager.setLayoutParams(params);
    }

    public static ViewPager getPager() {
        return pager;
    }

    private void setClickListeners() {}

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
