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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.capstone.app24.R;
import com.capstone.app24.activities.MainActivity;
import com.capstone.app24.adapters.MostViewedAdapter;
import com.capstone.app24.animations.HidingScrollListener;
import com.capstone.app24.sliding_tabs.SlidingTabLayout;
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
        MainActivity.tabs.setVisibility(View.VISIBLE);
        MainActivity.layout_user_profle.setVisibility(View.GONE);
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
                //Utils.setScrollDirection(Constants.SCROLL_UP);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) MainActivity.getBottomLayout().getLayoutParams();
                int fabBottomMargin = lp.bottomMargin;
                MainActivity.getBottomLayout().animate().translationY(MainActivity.getBottomLayout().getHeight() + fabBottomMargin + 100)
                        .setInterpolator(new AccelerateInterpolator(2)).start();

//                final SlidingTabLayout slidingTabLayout = HomeFragment.getHeaderView();
                RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) MainActivity.tabs
                        .getLayoutParams();
                int fabTopMargin = lp1.topMargin;
                MainActivity.tabs.animate().translationY(-MainActivity.tabs.getHeight() +
                        fabTopMargin).setInterpolator(new
                        AccelerateInterpolator(2));
            }

            @Override
            public void onShow() {
                Utils.debug(TAG, "Scrolling Down");

                MainActivity.getBottomLayout().animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                MainActivity.tabs.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                // Utils.setScrollDirection(Constants.SCROLL_DOWN);
            }
        });
    }
}
