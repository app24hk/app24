package com.capstone.app24.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.capstone.app24.R;
import com.capstone.app24.adapters.LatestFeedsAdapter;
import com.capstone.app24.animations.HidingScrollListener;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.Utils;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amritpal on 3/11/15.
 */
public class LatestFragment extends Fragment {

    private static final String TAG = LatestFragment.class.getSimpleName();
    View mView;
    private RecyclerView list_latest_feeds;
    private LatestFeedsAdapter mLatestFeedsAdapter;
    private Context mContext;
    private Activity mActivity;
    SweetAlertDialog mDialog;
    RecyclerView.OnItemTouchListener disabler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_latest, container, false);
        mContext = getActivity();
        mActivity = getActivity();
        initializeViews();
        updateUI();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * This method is used to Update the UI with Feeds
     */
    private void updateUI() {

        mLatestFeedsAdapter = new LatestFeedsAdapter(getActivity());
        list_latest_feeds.setLayoutManager(new LinearLayoutManager(getActivity()));
        list_latest_feeds.setAdapter(mLatestFeedsAdapter);

    }

    private void initRecyclerView() {


        list_latest_feeds.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                Utils.debug(TAG, "Scrolling up");
                Utils.setScrollDirection(Constants.SCROLL_UP);
            }

            @Override
            public void onShow() {
                Utils.debug(TAG, "Scrolling Down");
                Utils.setScrollDirection(Constants.SCROLL_DOWN);
            }
        });
    }

    /**
     * Initialize views for the user Interface
     */
    private void initializeViews() {
        list_latest_feeds = (RecyclerView) mView.findViewById(R.id.list_latest_feeds);
        initRecyclerView();
    }
}
