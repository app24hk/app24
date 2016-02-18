package com.capstone.app24.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.capstone.app24.R;
import com.capstone.app24.activities.MainActivity;
import com.capstone.app24.adapters.MostViewedAdapter;
import com.capstone.app24.animations.HidingScrollListener;
import com.capstone.app24.bean.LatestFeedsModel;
import com.capstone.app24.interfaces.OnDeleteListener;
import com.capstone.app24.utils.APIsConstants;
import com.capstone.app24.utils.AppController;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.InterfaceListener;
import com.capstone.app24.utils.NetworkUtils;
import com.capstone.app24.utils.Utils;
import com.github.yasevich.endlessrecyclerview.EndlessRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import volley.Request;
import volley.VolleyError;
import volley.VolleyLog;
import volley.toolbox.StringRequest;

/**
 * Created by amritpal on 4/11/15.
 */
public class MostViewedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        OnDeleteListener, EndlessRecyclerView.Pager {
    private static final String TAG = MostViewedFragment.class.getSimpleName();
    private View mView;
    private Context mContext;
    private Activity mActivity;
    public MostViewedAdapter mMostViewedAdapter;
    //    private RecyclerView most_viewed_feeds;
    private EndlessRecyclerView most_viewed_feeds;

    private int mPageNo = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    List<LatestFeedsModel> mMostViewedFeedList = new ArrayList<>();
    /* End of Volley Request Tags */
    private SweetAlertDialog mDialog;
    private String res = "";
    private String tag_string_req = "feeds_req";

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_most_viewed, container, false);
        mContext = getActivity();
        mActivity = getActivity();
        initializeViews();
        MainActivity.tabs.setVisibility(View.VISIBLE);
        MainActivity.layout_user_profle.setVisibility(View.GONE);
        if (NetworkUtils.isOnline(getActivity())) {
            mDialog = Utils.showSweetProgressDialog(getActivity(),
                    getResources
                            ().getString(R.string.progress_loading), SweetAlertDialog.PROGRESS_TYPE);
            mDialog.setCancelable(true);
            getMostViewedFeeds();
        } else {
            Utils.showSweetProgressDialog(getActivity(), getActivity().getResources().getString(R
                    .string.check_your_internet_connection), SweetAlertDialog.WARNING_TYPE);
        }
        mMostViewedAdapter = new MostViewedAdapter(getActivity(), mMostViewedFeedList);
        most_viewed_feeds.setAdapter(mMostViewedAdapter);
        most_viewed_feeds.setLayoutManager(new LinearLayoutManager(getActivity()));
        most_viewed_feeds.setProgressView(R.layout.item_progress);
        most_viewed_feeds.setPager(this);
        return mView;
    }

    private void updateUI() {
        mMostViewedAdapter.notifyDataSetChanged();
    }

    private void initializeViews() {
        most_viewed_feeds = (EndlessRecyclerView) mView.findViewById(R.id.most_viewed_feeds);
        initRecyclerView();
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        InterfaceListener.setOnDeleteListener(MostViewedFragment.this);


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

    public boolean getMostViewedFeeds() {
        /*
         Starting a progress Dialog...
         If second parameter is passed null then progressdialog will show (Loading...) by default if pass string such as(Searching..) then
         it will show (Searching...)
         */
//        if (mPageNo == 1) {
//            mDialog = Utils.showSweetProgressDialog(getActivity(),
//                    getResources
//                            ().getString(R.string.progress_loading), SweetAlertDialog.PROGRESS_TYPE);
//            mDialog.setCancelable(true);
//        }

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_RECENT_FEEDS,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.debug(TAG, response.toString());

                        res = response.toString();
                        try {
                            refreshMostViewedFeeds(res);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }, new volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mPageNo == 1)
                    Utils.closeSweetProgressDialog(getActivity(), mDialog);
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                res = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
//                        user_id(int)
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIsConstants.KEY_PAGE_NUMBER, mPageNo + "");
                params.put(APIsConstants.TAB_TYPE, APIsConstants.TWO);
                params.put(APIsConstants.KEY_USER_ID, new Utils().getSharedPreferences(getActivity(),
                        Constants.KEY_USER_DETAILS, ""));
                Utils.info("params...", params.toString());
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }

    private List<LatestFeedsModel> refreshMostViewedFeeds(String res) {
        Utils.debug("nnnnn", "Response  : " + res);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            try {

                if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(APIsConstants.KEY_FEED_DATA);
                    if (jsonArray != null) {
                        if (mPageNo == 1)
                            mMostViewedFeedList.clear();
                        ITEMS_ON_PAGE = jsonArray.length();
                        addItems();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            LatestFeedsModel mostViewedModel = new LatestFeedsModel();
                            JSONObject object = jsonArray.getJSONObject(i);
                            if (object != null) {
                                try {
                                    mostViewedModel.setId(object.getString(APIsConstants.KEY_ID));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mostViewedModel.setTitle(object.getString(APIsConstants.KEY_TITLE));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mostViewedModel.setDescription(object.getString(APIsConstants.KEY_DESCRIPTION));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mostViewedModel.setMedia(object.getString(APIsConstants.KEY_MEDIA));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mostViewedModel.setType(object.getString(APIsConstants.KEY_TYPE));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mostViewedModel.setUser_id(object.getString(APIsConstants.KEY_USER_ID));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mostViewedModel.setCreated(object.getString(APIsConstants.KEY_CREATED));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mostViewedModel.setModified(object.getString(APIsConstants.KEY_MODIFIED));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mostViewedModel.setUser_name(object.getString(APIsConstants.KEY_USER_NAME));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mostViewedModel.setViewcount(object.getString(APIsConstants.KEY_VIEWCOUNT));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mostViewedModel.setThumbnail(object.getString(APIsConstants.KEY_THUMBNAIL));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mostViewedModel.setProfit_amount(object.getString(APIsConstants
                                            .KEY_PROFIT_AMOUNT));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mostViewedModel.setFb_feed_id(object.getString(APIsConstants
                                            .KEY_FB_FEED_ID));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (!mMostViewedFeedList.contains(mostViewedModel)) {
                                    mMostViewedFeedList.add(mostViewedModel);
                                }
                            }
                        }
                    }
                    if (jsonArray.length() == 0) {
                        try {
//                            Utils.showSweetProgressDialog(getActivity(), jsonObject.getString(getActivity
//                                            ().getResources().getString(R.string.no_more_data)),
//                                    SweetAlertDialog.ERROR_TYPE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        Utils.debug(Constants.API_TAG, jsonObject.getString(APIsConstants.KEY_MESSAGE));
//                        Utils.showSweetProgressDialog(getActivity(), jsonObject.getString(APIsConstants
//                                .KEY_MESSAGE), SweetAlertDialog.ERROR_TYPE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMostViewedAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
//        if (mPageNo == 1)
        Utils.closeSweetProgressDialog(getActivity(), mDialog);
        return mMostViewedFeedList;
    }


    @Override
    public void onRefresh() {
//        mPageNo = mPageNo + 1;
//        getMostViewedFeeds();
        mPageNo = 1;

//        ITEMS_ON_PAGE = 0;
//        latestFeedList.clear();
        loading = false;
        mMostViewedAdapter.setCount(0);
        getMostViewedFeeds();
    }

    @Override
    public void onDelete(String id, boolean isDelete) {
        if (mMostViewedFeedList.size() > 0) {
            for (int i = 0; i < mMostViewedFeedList.size(); i++) {
                if (mMostViewedFeedList.get(i).getId().equalsIgnoreCase(id)) {
                    mMostViewedFeedList.remove(i);
                    mMostViewedAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    int ITEMS_ON_PAGE = 0;
    private static final int TOTAL_PAGES = 10;
    private static final long DELAY = 1000L;
    private boolean loading = false;

    private final Handler handler = new Handler();

    @Override
    public boolean shouldLoad() {
//        return false;
        return !loading && mMostViewedAdapter.getItemCount() / ITEMS_ON_PAGE < TOTAL_PAGES;

    }


    @Override
    public void loadNextPage() {
        loading = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPageNo = mPageNo + 1;                //addItems();

                getMostViewedFeeds();

                most_viewed_feeds.setRefreshing(false);
                loading = false;
                //addItems();
            }
        }, DELAY);
    }

    private void addItems() {
        mMostViewedAdapter.setCount(mMostViewedAdapter.getItemCount() + ITEMS_ON_PAGE);
    }


}
