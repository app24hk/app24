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
import com.capstone.app24.utils.AlertToastManager;
import com.capstone.app24.utils.GlobalClass;
import com.capstone.app24.utils.Utils;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_latest, container, false);
        mContext = getActivity();
        mActivity = getActivity();
        initializeViews();
        updateUI();
        return mView;
    }


    /**
     * This method is used to Update the UI with Feeds
     */
    private void updateUI() {
        mLatestFeedsAdapter = new LatestFeedsAdapter(getActivity());
        list_latest_feeds.setAdapter(mLatestFeedsAdapter);
        list_latest_feeds.setLayoutManager(new LinearLayoutManager(getActivity()));
     /*   list_latest_feeds.addOnItemTouchListener(new GlobalClass.RecyclerTouchListener(mContext,
                list_latest_feeds, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                AlertToastManager.showToast("Position : " + position, mContext);
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/
    }

    /**
     * Initialize views for the user Interface
     */
    private void initializeViews() {
        list_latest_feeds = (RecyclerView) mView.findViewById(R.id.list_latest_feeds);

    }
}
