package com.capstone.app24.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.capstone.app24.R;
import com.capstone.app24.adapters.ViewPagerAdapterHome;
import com.capstone.app24.interfaces.OnScrolling;
import com.capstone.app24.sliding_tabs.SlidingTabLayout;
import com.capstone.app24.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amritpal on 5/11/15.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    View mView;
    private ViewPager pager;
    private static SlidingTabLayout tabs;
    ViewPagerAdapterHome adapter_home;
    CharSequence home_titles[] = {"Latest", "Most Viewed"};
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
        return mView;

    }

   /* @Override
    public void ScrollUp(int up) {
        showViews();
    }

    @Override
    public void ScrollDown(int down) {
        hideViews();
    }*/

    private void hideViews() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabs.getLayoutParams();
        int fabTopMargin = lp.topMargin;
        tabs.animate().translationY(tabs.getHeight() + fabTopMargin - 200).setInterpolator(new
                AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        tabs.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    public static SlidingTabLayout getHeaderView() {
        return tabs;
    }

    private void updateUI() {
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter_home = new ViewPagerAdapterHome(getActivity().getSupportFragmentManager(), home_titles,
                home_titles.length, dialog);
        pager.setAdapter(adapter_home);
        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) mView.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setViewPager(pager);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout
    }

    private void initializeViews() {
        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) mView.findViewById(R.id.pager);
        ViewGroup.LayoutParams params = pager.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        pager.setLayoutParams(params);
    }

    private void setClickListeners() {

    }
}
