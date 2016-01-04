package com.capstone.app24.fragments;

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
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.capstone.app24.R;
import com.capstone.app24.activities.MainActivity;
import com.capstone.app24.activities.ProfileActivity;
import com.capstone.app24.activities.SettingsActivity;
import com.capstone.app24.adapters.UserProfitAdapter;
import com.capstone.app24.animations.HidingScrollListener;
import com.capstone.app24.bean.UserFeedModel;
import com.capstone.app24.bean.UserFeedModel;
import com.capstone.app24.utils.APIsConstants;
import com.capstone.app24.utils.AppController;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.InterfaceListener;
import com.capstone.app24.utils.NetworkUtils;
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
 * Created by amritpal on 6/11/15.
 */
public class UserProfileDetailsFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener/*,
        TextWatcher, View.OnFocusChangeListener, CustomDrawablEditText.OnDrawableClickListener */ {
    private static final String TAG = UserProfileDetailsFragment.class.getSimpleName();
    View mView;
    private ImageButton ibtn_setting, ibtn_search;
    private RecyclerView user_feeds_list;
    private UserProfitAdapter mLatestFeedsAdapter;
    /* Volley Request Tags */
    private String res = "";
    private String tag_string_req = "feeds_req";
    private int mPageNo = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<UserFeedModel> userFeedList = new ArrayList<>();
    /* End of Volley Request Tags */
    private SweetAlertDialog mDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_user_profile_details, container, false);
        initializeViews();
        updateUI();
        MainActivity.tabs.setVisibility(View.GONE);
        MainActivity.layout_user_profle.setVisibility(View.VISIBLE);
        return mView;
    }

    private void updateUI() {
        MainActivity.setUsername(new Utils(getActivity())
                .getSharedPreferences(getActivity(), Constants
                        .KEY_USER_NAME, ""));
        if (NetworkUtils.isOnline(getActivity()))
            getUserFeeds();
        else
            Utils.showSweetProgressDialog(getActivity(), getActivity().getResources().getString(R
                    .string.check_your_internet_connection), SweetAlertDialog.WARNING_TYPE);
        mLatestFeedsAdapter = new UserProfitAdapter(getActivity(), userFeedList);
        user_feeds_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        user_feeds_list.setAdapter(mLatestFeedsAdapter);
    }

    private boolean getUserFeeds() {

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
                APIsConstants.API_BASE_URL + APIsConstants.API_USER_FEEDS,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        res = response.toString();
                        try {
                            refreshUserFeeds(res);
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
                params.put(APIsConstants.KEY_USER_ID, new Utils(getActivity())
                        .getSharedPreferences(getActivity(), Constants.KEY_USER_DETAILS, ""));
                Utils.info("params...", params.toString());
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }


    private void initializeViews() {

        user_feeds_list = (RecyclerView) mView.findViewById(R.id.user_feeds_list);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);


        initRecyclerView();

    }

    private void initRecyclerView() {
        user_feeds_list.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                Utils.debug(TAG, "Scrolling up");
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) MainActivity.getBottomLayout().getLayoutParams();
                int fabBottomMargin = lp.bottomMargin;
                MainActivity.getBottomLayout().animate().translationY(MainActivity.getBottomLayout().getHeight() + fabBottomMargin + 100)
                        .setInterpolator(new AccelerateInterpolator(2)).start();

                RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) MainActivity.layout_user_profle
                        .getLayoutParams();
                int fabTopMargin = lp1.topMargin;
                MainActivity.layout_user_profle.animate().translationY(-MainActivity
                        .layout_user_profle.getHeight() + fabTopMargin).setInterpolator(new
                        AccelerateInterpolator(2));
            }

            @Override
            public void onShow() {
                Utils.debug(TAG, "Scrolling Down");
                MainActivity.getBottomLayout().animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                MainActivity.layout_user_profle.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)); // Utils.setScrollDirection(Constants.SCROLL_DOWN);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ibtn_search:
                intent = new Intent(getActivity(), ProfileActivity.class);
                Utils.debug(TAG, userFeedList.size() + " Size");
                startActivity(intent);
                break;
            case R.id.ibtn_setting:
                intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }

    public static UserProfileDetailsFragment newInstance() {
        UserProfileDetailsFragment f = new UserProfileDetailsFragment();
        Bundle args = new Bundle();
//        username = args.getString(Constants.KEY_USER_NAME);
        return f;
    }

    private List<UserFeedModel> refreshUserFeeds(String res) throws JSONException {
        Utils.debug(TAG, "Response for UserProfile   : " + res);
        JSONObject jsonObject = new JSONObject(res);
        if (jsonObject != null) {
            if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
                JSONArray jsonArray = jsonObject.getJSONArray(APIsConstants.KEY_FEED_DATA);
                if (jsonArray != null) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        UserFeedModel userFeedModel = new UserFeedModel();
                        JSONObject object = jsonArray.getJSONObject(i);
                        if (object != null) {
                            try {
                                userFeedModel.setId(object.getString(APIsConstants.KEY_ID));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setTitle(object.getString(APIsConstants.KEY_TITLE));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setDescription(object.getString(APIsConstants.KEY_DESCRIPTION));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setMedia(object.getString(APIsConstants.KEY_MEDIA));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setType(object.getString(APIsConstants.KEY_TYPE));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setUser_id(object.getString(APIsConstants.KEY_USER_ID));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setCreated(object.getString(APIsConstants.KEY_CREATED));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setModified(object.getString(APIsConstants.KEY_MODIFIED));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setUser_name(object.getString(APIsConstants.KEY_USER_NAME));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setViewcount(object.getString(APIsConstants.KEY_VIEWCOUNT));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setThumbnail(object.getString(APIsConstants.KEY_THUMBNAIL));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setProfit_amount(object.getString(APIsConstants
                                        .KEY_PROFIT_AMOUNT));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setFb_feed_id(object.getString(APIsConstants
                                        .KEY_FB_FEED_ID));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (!userFeedList.contains(userFeedModel)) {
                                userFeedList.add(userFeedModel);
                            }
                        }
                    }
                }
                if (jsonArray.length() == 0) {
//                    Utils.showSweetProgressDialog(getActivity(), jsonObject.getString(getActivity
//                                    ().getResources().getString(R.string.no_more_data)),
//                            SweetAlertDialog.ERROR_TYPE);
                }
            } else {
                try {
                    Utils.debug(Constants.API_TAG, jsonObject.getString(APIsConstants.KEY_MESSAGE));
//                    Utils.showSweetProgressDialog(getActivity(), jsonObject.getString(APIsConstants
//                            .KEY_MESSAGE), SweetAlertDialog.ERROR_TYPE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        mLatestFeedsAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        if (mPageNo == 1)
            Utils.closeSweetProgressDialog(getActivity(), mDialog);
        return userFeedList;
    }

    @Override
    public void onRefresh() {
        mPageNo = mPageNo + 1;
        getUserFeeds();
    }

}
