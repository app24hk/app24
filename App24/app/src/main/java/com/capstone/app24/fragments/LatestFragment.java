package com.capstone.app24.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.capstone.app24.R;
import com.capstone.app24.activities.MainActivity;
import com.capstone.app24.activities.PostDetailActivity;
import com.capstone.app24.adapters.LatestFeedsAdapter;
import com.capstone.app24.animations.HidingScrollListener;
import com.capstone.app24.animations.ListAnimation;
import com.capstone.app24.sliding_tabs.SlidingTabLayout;
import com.capstone.app24.utils.GlobalClass;
import com.capstone.app24.utils.RecyclerViewDisabler;
import com.capstone.app24.utils.Utils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

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
        MainActivity.tabs.setVisibility(View.VISIBLE);
        MainActivity.layout_user_profle.setVisibility(View.GONE);
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
        list_latest_feeds.setHasFixedSize(true);
        list_latest_feeds.setLayoutManager(new LinearLayoutManager(getActivity()));
        list_latest_feeds.setAdapter(mLatestFeedsAdapter);
      /*  list_latest_feeds.addOnItemTouchListener(new GlobalClass.RecyclerTouchListener(getActivity(),
                list_latest_feeds, new GlobalClass.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                getActivity().startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/
    }

    private void initRecyclerView() {


        list_latest_feeds.addOnScrollListener(new HidingScrollListener() {
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
                /*ListAnimation.settranslationofArrow(0.0f, 0.0f, 0.0f, -1.0f,
                        100, slidingTabLayout);*/
               /* if (!list_latest_feeds.isAnimating()) {
                    slidingTabLayout.setVisibility(View.GONE);
                }*/

//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(80);
//                            slidingTabLayout.setVisibility(View.GONE);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });


                // slidingTabLayout.setVisibility(View.GONE);
            }

            @Override
            public void onShow() {
                Utils.debug(TAG, "Scrolling Down");

                MainActivity.getBottomLayout().animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
//                final SlidingTabLayout slidingTabLayout = HomeFragment.getHeaderView();
               /* ListAnimation.settranslationofArrow(0.0f, 0.0f, -1.0f, 0.0f,
                        300, slidingTabLayout);*/
                MainActivity.tabs.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(80);
//                            slidingTabLayout.setVisibility(View.VISIBLE);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
                /*if (!list_latest_feeds.isAnimating()) {
                    slidingTabLayout.setVisibility(View.VISIBLE);
                }*/


                // Utils.setScrollDirection(Constants.SCROLL_DOWN);
                //slidingTabLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Initialize views for the user Interface
     */
    private void initializeViews() {
        list_latest_feeds = (RecyclerView) mView.findViewById(R.id.list_latest_feeds);
        //swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout);
        disabler = new
                RecyclerViewDisabler();

        initRecyclerView();
    }

}
