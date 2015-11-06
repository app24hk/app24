package com.capstone.app24.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.app24.R;
import com.capstone.app24.adapters.ViewPagerAdapterHome;
import com.capstone.app24.sliding_tabs.SlidingTabLayout;

/**
 * Created by amritpal on 5/11/15.
 */
public class HomeFragment extends Fragment {
    View mView;
    private ViewPager pager;
    private SlidingTabLayout tabs;
    ViewPagerAdapterHome adapter_home;
    CharSequence home_titles[] = {"Latest", "Most Viewed"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        Log.e("Test", "home");
        new LoadData().execute();
        return mView;
    }

    public class LoadData extends AsyncTask<Void, Void, Void> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Loading home");
            pd.show();

            Log.e("Test", "pre");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.e("Test", "doin");
            initializeViews();
            setClickListeners();
            updateUI();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("Test", "post");
            pd.dismiss();

        }
    }

    private void updateUI() {
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter_home = new ViewPagerAdapterHome(getActivity().getSupportFragmentManager(), home_titles,
                home_titles.length);
        pager.setAdapter(adapter_home);

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

    private void initializeViews() {
        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) mView.findViewById(R.id.pager);

    }

    private void setClickListeners() {

    }
}
