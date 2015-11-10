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

import com.capstone.app24.R;
import com.capstone.app24.adapters.MostViewedAdapter;
import com.capstone.app24.animations.HidingScrollListener;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.Utils;

/**
 * Created by amritpal on 4/11/15.
 */
public class MostViewedFragment extends Fragment {
    private static final String TAG = MostViewedFragment.class.getSimpleName();
    private View mView;
    private Context mContext;
    private Activity mActivity;
    private MostViewedAdapter mMostViewedAdapter;
    private RecyclerView most_viewed_feeds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_most_viewed, container, false);
        mContext = getActivity();
        mActivity = getActivity();
        initializeViews();
        updateUI();
        return mView;
    }

    private void updateUI() {
        mMostViewedAdapter = new MostViewedAdapter(getActivity());
        most_viewed_feeds.setAdapter(mMostViewedAdapter);
        most_viewed_feeds.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initializeViews() {
        most_viewed_feeds = (RecyclerView) mView.findViewById(R.id.most_viewed_feeds);
        initRecyclerView();

    }

    private void initRecyclerView() {


        most_viewed_feeds.addOnScrollListener(new HidingScrollListener() {
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
}
