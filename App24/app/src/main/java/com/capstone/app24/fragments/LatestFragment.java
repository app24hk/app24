package com.capstone.app24.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.capstone.app24.R;
import com.capstone.app24.activities.PostDetailActivity;
import com.capstone.app24.adapters.LatestFeedsAdapter;
import com.capstone.app24.interfaces.ClickListener;
import com.capstone.app24.interfaces.OnListUpdateListener;
import com.capstone.app24.utils.AlertToastManager;
import com.capstone.app24.utils.GlobalClass;
import com.capstone.app24.utils.Utils;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amritpal on 3/11/15.
 */
public class LatestFragment extends Fragment implements OnListUpdateListener {

    private static final String TAG = LatestFragment.class.getSimpleName();
    View mView;
    private RecyclerView list_latest_feeds;
    private LatestFeedsAdapter mLatestFeedsAdapter;
    private Context mContext;
    private Activity mActivity;
    SweetAlertDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_latest, container, false);
        mContext = getActivity();
        mActivity = getActivity();
        initializeViews();
        updateUI();
        Utils.setOnListUpdateListener(this);
        return mView;
    }

    /**
     * This method is used to Update the UI with Feeds
     */
    private void updateUI() {

        Utils.debug(TAG, "setting LatestFeedsFragments in Main Activity start");

        mLatestFeedsAdapter = new LatestFeedsAdapter(getActivity());
        list_latest_feeds.setLayoutManager(new LinearLayoutManager(getActivity()));
        list_latest_feeds.setAdapter(mLatestFeedsAdapter);

        Utils.debug(TAG, "setting LatestFeedsFragments in Main Activity end");

    }

    /**
     * Initialize views for the user Interface
     */
    private void initializeViews() {
        list_latest_feeds = (RecyclerView) mView.findViewById(R.id.list_latest_feeds);

    }

    @Override
    public void onListUpdate() {
        if (list_latest_feeds.getAdapter() != null) {
            //list_latest_feeds.setAdapter(mLatestFeedsAdapter);
            mLatestFeedsAdapter.notifyDataSetChanged();
        } else {
            list_latest_feeds.setAdapter(mLatestFeedsAdapter);
            mLatestFeedsAdapter.notifyDataSetChanged();
        }
    }
}
