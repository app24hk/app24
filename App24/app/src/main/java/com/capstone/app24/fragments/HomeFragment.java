package com.capstone.app24.fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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
import com.capstone.app24.activities.MainActivity;
import com.capstone.app24.adapters.ViewPagerAdapterHome;
import com.capstone.app24.interfaces.OnScrolling;
import com.capstone.app24.receiver.AlarmReceiver;
import com.capstone.app24.sliding_tabs.SlidingTabLayout;
import com.capstone.app24.utils.AlertToastManager;
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
    private static ViewPager pager;
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
/*
        Utils.setOnScrolling(this);
*/
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

//    private void hideViews() {
//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) MainActivity.tabs.getLayoutParams();
//        int fabTopMargin = lp.topMargin;
//        MainActivity.tabs.animate().translationY(MainActivity.tabs.getHeight() + fabTopMargin - 200).setInterpolator(new
//                AccelerateInterpolator(2)).start();
//    }
//
//    private void showViews() {
//        MainActivity.tabs.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
//    }

//    public static SlidingTabLayout getHeaderView() {
//        return MainActivity.tabs;
//    }

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
        //Setting Alarm
        //setAlarm();
    }

    private void setAlarm() {
        try {
            Utils.debug(TAG, "Setting Alarm");
            //Create a new PendingIntent and add it to the AlarmManager
            Intent intent = new Intent(getActivity(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),
                    12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am =
                    (AlarmManager) getActivity().getSystemService(Activity.ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    10 * 1000, pendingIntent);
        } catch (Exception e) {
        }
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

    private void setClickListeners() {

    }
/*
    @Override
    public void ScrollUp(int direction) {
        tabs.setVisibility(View.GONE);
    }

    @Override
    public void ScrollDown(int up) {
        tabs.setVisibility(View.VISIBLE);
    }*/

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
