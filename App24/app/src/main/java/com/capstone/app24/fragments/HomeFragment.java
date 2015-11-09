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
import android.widget.Toast;

import com.capstone.app24.R;
import com.capstone.app24.adapters.ViewPagerAdapterHome;
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
    private SlidingTabLayout tabs;
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
        new loaderHome().execute();
        initializeViews();
        setClickListeners();
        updateUI();
        return mView;

    }

    class loaderHome extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            dialog = Utils.showSweetProgressDialog(getActivity(), getResources().getString(R.string
                    .progress_loading));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Utils.closeSweetProgressDialog(getActivity(), dialog);
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }


    }


    private void updateUI() {

        Utils.debug(TAG, "updateUI() start");
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
        Utils.debug(TAG, "updateUI() End");

    }

    private void initializeViews() {
        Utils.debug(TAG, "initializeViews() Start");

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) mView.findViewById(R.id.pager);

    }

    private void setClickListeners() {

    }

}
