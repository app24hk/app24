package com.capstone.app24.fragments;

import android.app.Activity;
import android.content.Context;
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
import android.widget.RelativeLayout;

import com.capstone.app24.R;
import com.capstone.app24.activities.MainActivity;
import com.capstone.app24.adapters.LatestFeedsAdapter;
import com.capstone.app24.animations.HidingScrollListener;
import com.capstone.app24.bean.LatestFeedsModel;
import com.capstone.app24.utils.APIsConstants;
import com.capstone.app24.utils.AppController;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.NetworkUtils;
import com.capstone.app24.utils.RecyclerViewDisabler;
import com.capstone.app24.utils.Utils;

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
 * Created by amritpal on 3/11/15.
 */
public class LatestFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = LatestFragment.class.getSimpleName();
    View mView;
    private RecyclerView list_latest_feeds;
    public LatestFeedsAdapter mLatestFeedsAdapter;
    private Context mContext;
    private Activity mActivity;
    RecyclerView.OnItemTouchListener disabler;
    /* Volley Request Tags */
    private String res = "";
    private String tag_string_req = "feeds_req";
    private int mPageNo = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    List<LatestFeedsModel> latestFeedList = new ArrayList<>();
    /* End of Volley Request Tags */
    private SweetAlertDialog mDialog;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_latest, container, false);
        mContext = getActivity();
        mActivity = getActivity();
        initializeViews();
        MainActivity.tabs.setVisibility(View.VISIBLE);
        MainActivity.layout_user_profle.setVisibility(View.GONE);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    /**
     * This method is used to Update the UI with Feeds
     */
    private void updateUI() {
        if (NetworkUtils.isOnline(getActivity()))
            getLatestFeeds();
        else
            Utils.showSweetProgressDialog(getActivity(), getActivity().getResources().getString(R
                    .string.check_your_internet_connection), SweetAlertDialog.WARNING_TYPE);
        mLatestFeedsAdapter = new LatestFeedsAdapter(getActivity(), latestFeedList);
        list_latest_feeds.setHasFixedSize(true);
        list_latest_feeds.setLayoutManager(new LinearLayoutManager(getActivity()));
        list_latest_feeds.setAdapter(mLatestFeedsAdapter);
        mLatestFeedsAdapter.notifyDataSetChanged();
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
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) MainActivity.getBottomLayout().getLayoutParams();
                int fabBottomMargin = lp.bottomMargin;
                MainActivity.getBottomLayout().animate().translationY(MainActivity.getBottomLayout().getHeight() + fabBottomMargin + 100)
                        .setInterpolator(new AccelerateInterpolator(2)).start();
                RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) MainActivity.tabs
                        .getLayoutParams();
                int fabTopMargin = lp1.topMargin;
                MainActivity.tabs.animate().translationY(-MainActivity.tabs.getHeight() +
                        fabTopMargin).setInterpolator(new
                        AccelerateInterpolator(2));
            }

            @Override
            public void onShow() {
                MainActivity.getBottomLayout().animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                MainActivity.tabs.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
            }
        });
    }

    /**
     * Initialize views for the user Interface
     */
    private void initializeViews() {
        list_latest_feeds = (RecyclerView) mView.findViewById(R.id.list_latest_feeds);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout);
        disabler = new RecyclerViewDisabler();
        swipeRefreshLayout.setOnRefreshListener(this);
        initRecyclerView();
    }

    public boolean getLatestFeeds() {
        /*
         Starting a progress Dialog...
         If second parameter is passed null then progressdialog will show (Loading...) by default if pass string such as(Searching..) then
         it will show (Searching...)
         */

        if (mPageNo == 1)
            mDialog = Utils.showSweetProgressDialog(getActivity(),
                    getResources
                            ().getString(R.string.progress_loading), SweetAlertDialog.PROGRESS_TYPE);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_RECENT_FEEDS,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.debug(TAG, response.toString());

                        res = response.toString();
                        try {
                            refreshLatestFeeds(res);
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
                params.put(APIsConstants.TAB_TYPE, APIsConstants.ONE);
                Utils.info("params...", params.toString());
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }

    private List<LatestFeedsModel> refreshLatestFeeds(String res) throws JSONException {
        Utils.debug(TAG, "Response  : " + res);
        latestFeedList.clear();
        JSONObject jsonObject = new JSONObject(res);
        if (jsonObject != null) {
            if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
                JSONArray jsonArray = jsonObject.getJSONArray(APIsConstants.KEY_FEED_DATA);
                if (jsonArray != null) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        LatestFeedsModel latestFeedsModel = new LatestFeedsModel();
                        JSONObject object = jsonArray.getJSONObject(i);
                        if (object != null) {
                            try {
                                latestFeedsModel.setId(object.getString(APIsConstants.KEY_ID));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                latestFeedsModel.setTitle(object.getString(APIsConstants.KEY_TITLE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                latestFeedsModel.setDescription(object.getString(APIsConstants.KEY_DESCRIPTION));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                latestFeedsModel.setMedia(object.getString(APIsConstants.KEY_MEDIA));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                latestFeedsModel.setType(object.getString(APIsConstants.KEY_TYPE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                latestFeedsModel.setUser_id(object.getString(APIsConstants.KEY_USER_ID));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                latestFeedsModel.setCreated(object.getString(APIsConstants.KEY_CREATED));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                latestFeedsModel.setModified(object.getString(APIsConstants.KEY_MODIFIED));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                latestFeedsModel.setUser_name(object.getString(APIsConstants.KEY_USER_NAME));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                latestFeedsModel.setViewcount(object.getString(APIsConstants.KEY_VIEWCOUNT));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                latestFeedsModel.setThumbnail(object.getString(APIsConstants.KEY_THUMBNAIL));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            latestFeedList.add(latestFeedsModel);
                        }
                    }
                }
                if (jsonArray.length() == 0) {
                    Utils.showSweetProgressDialog(getActivity(), jsonObject.getString(getActivity
                                    ().getResources().getString(R.string.no_more_data)),
                            SweetAlertDialog.ERROR_TYPE);
                }
            } else {
                try {
                    Utils.debug(Constants.API_TAG, jsonObject.getString(APIsConstants.KEY_MESSAGE));
                    Utils.showSweetProgressDialog(getActivity(), jsonObject.getString(APIsConstants
                            .KEY_MESSAGE), SweetAlertDialog.ERROR_TYPE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        mLatestFeedsAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        if (mPageNo == 1)
            Utils.closeSweetProgressDialog(getActivity(), mDialog);
        return latestFeedList;
    }

    @Override
    public void onRefresh() {
        mPageNo = mPageNo + 1;
        Utils.debug(TAG, "swipeRefreshLayout mPAge Number" + mPageNo);
        getLatestFeeds();
    }
}
