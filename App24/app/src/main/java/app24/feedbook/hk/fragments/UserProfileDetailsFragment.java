package app24.feedbook.hk.fragments;

import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app24.feedbook.hk.R;
import app24.feedbook.hk.activities.MainActivity;
import app24.feedbook.hk.activities.ProfileActivity;
import app24.feedbook.hk.activities.SettingsActivity;
import app24.feedbook.hk.adapters.MostViewedAdapter;
import app24.feedbook.hk.adapters.UserProfitAdapter;
import app24.feedbook.hk.animations.HidingScrollListener;
import app24.feedbook.hk.bean.UserFeedModel;
import app24.feedbook.hk.interfaces.OnLoadMoreListener;
import app24.feedbook.hk.utils.APIsConstants;
import app24.feedbook.hk.utils.AppController;
import app24.feedbook.hk.utils.Constants;
import app24.feedbook.hk.utils.InterfaceListener;
import app24.feedbook.hk.utils.NetworkUtils;
import app24.feedbook.hk.utils.Utils;

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
 * Created by amritpal on 6/11/15.
 */
public class UserProfileDetailsFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener/*,
        TextWatcher, View.OnFocusChangeListener, CustomDrawablEditText.OnDrawableClickListener */ /*EndlessRecyclerView.Pager*/ {
    private static final String TAG = UserProfileDetailsFragment.class.getSimpleName();
    View mView;
    private ImageButton ibtn_setting, ibtn_search;
    //    private RecyclerView user_feeds_list;
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
    private LinearLayout profit_details;
    private String mTotalProfit = "", mTodayProfit = "", mUnreceivedProfit = "", mThisMonthProfit = "";


    private TextView txt_todays_profit_value, txt_this_month_profit_value,
            txt_unreceived_profit_value, txt_heading_todays_profit_value;
    private RelativeLayout xtra_layout;
    private boolean hasMoreItems = true;

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
        if (NetworkUtils.isOnline(getActivity())) {
            mDialog = Utils.showSweetProgressDialog(getActivity(),
                    getResources
                            ().getString(R.string.progress_loading), SweetAlertDialog.PROGRESS_TYPE);
            getUserFeeds();
        } else {
            Utils.showSweetProgressDialog(getActivity(), getActivity().getResources().getString(R
                    .string.check_your_internet_connection), SweetAlertDialog.WARNING_TYPE);
        }

        user_feeds_list.setHasFixedSize(true);
        user_feeds_list.setLayoutManager(new LinearLayoutManager(getActivity()));

        mLatestFeedsAdapter = new UserProfitAdapter(getActivity(), userFeedList, user_feeds_list);
        user_feeds_list.setAdapter(mLatestFeedsAdapter);

        mLatestFeedsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
                if (userFeedList.size() >= 10) {
                    if (hasMoreItems) {
                        userFeedList.add(null);
                        mLatestFeedsAdapter.notifyItemInserted(userFeedList.size() - 1);

                        refreshItems();
                    } else {
                    }
                }
            }
        });

        mLatestFeedsAdapter.notifyDataSetChanged();
    }

    private void refreshItems() {
        mPageNo = mPageNo + 1;
        getUserFeeds();
    }

    private boolean getUserFeeds() {
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
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }


    private void initializeViews() {

        user_feeds_list = (RecyclerView) mView.findViewById(R.id.user_feeds_list);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        profit_details = (LinearLayout) mView.findViewById(R.id.profit_details);
        xtra_layout = (RelativeLayout) mView.findViewById(R.id.xtra_layout);
        profit_details.setVisibility(View.GONE);


        RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) profit_details
                .getLayoutParams();
        lp1.topMargin = 30;
        profit_details.setLayoutParams(lp1);

        txt_todays_profit_value = (TextView) mView.findViewById(R.id.txt_todays_profit_value);
        txt_this_month_profit_value = (TextView) mView.findViewById(R.id.txt_this_month_profit_value);
        txt_unreceived_profit_value = (TextView) mView.findViewById(R.id.txt_unreceived_profit_value);
        txt_heading_todays_profit_value = (TextView) mView.findViewById(R.id.txt_heading_todays_profit_value);
        initRecyclerView();

    }

    private void initRecyclerView() {
//        user_feeds_list.addOnScrollListener(new HidingScrollListener() {
//            @Override
//            public void onHide() {
//                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) MainActivity.getBottomLayout().getLayoutParams();
//                int fabBottomMargin = lp.bottomMargin;
//                MainActivity.getBottomLayout().animate().translationY(MainActivity.getBottomLayout().getHeight() + fabBottomMargin + 100)
//                        .setInterpolator(new AccelerateInterpolator(2)).start();
//
//                RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) MainActivity.layout_user_profle
//                        .getLayoutParams();
//                int fabTopMargin = lp1.topMargin;
//                MainActivity.layout_user_profle.animate().translationY(-MainActivity
//                        .layout_user_profle.getHeight() + fabTopMargin).setInterpolator(new
//                        AccelerateInterpolator(2));
//            }
//
//            @Override
//            public void onShow() {
//                MainActivity.getBottomLayout().animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
//                MainActivity.layout_user_profle.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)); // Utils.setScrollDirection(Constants.SCROLL_DOWN);
//            }
//        });
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
        return f;
    }

    private List<UserFeedModel> refreshUserFeeds(String res) throws JSONException {
        JSONObject jsonObject = new JSONObject(res);
        if (jsonObject != null) {
            if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
                JSONArray jsonArray = jsonObject.getJSONArray(APIsConstants.KEY_FEED_DATA);
                profit_details.setVisibility(View.GONE);
                if (jsonArray != null) {

                    if (mPageNo > 1) {
                        if (userFeedList.size() != 0) {
                            userFeedList.remove(userFeedList.size() - 1);
                            mLatestFeedsAdapter.notifyItemRemoved(userFeedList.size());
                        }
                    }

                    if (mPageNo == 1)
                        userFeedList.clear();
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
                                new Utils().setPreferences(getActivity(), Constants.KEY_USER_NAME,
                                        object.getString(APIsConstants.KEY_USER_NAME));
                                MainActivity.setUsername(new Utils(getActivity())
                                        .getSharedPreferences(getActivity(), Constants
                                                .KEY_USER_NAME, ""));
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
                }
            } else {


                if (userFeedList.size() != 0) {
                    hasMoreItems = false;
                    userFeedList.remove(userFeedList.size() - 1);
                    mLatestFeedsAdapter.notifyItemRemoved(userFeedList.size());
                    mLatestFeedsAdapter.notifyDataSetChanged();
                }

                if (userFeedList.size() <= 0) {
                    profit_details.setVisibility(View.VISIBLE);
                    xtra_layout.setVisibility(View.VISIBLE);
                }
                getProfitAmount();
                try {
                    Utils.debug(Constants.API_TAG, jsonObject.getString(APIsConstants.KEY_MESSAGE));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        mLatestFeedsAdapter.notifyDataSetChanged();
        mLatestFeedsAdapter.setLoaded();
        swipeRefreshLayout.setRefreshing(false);
        if (mPageNo == 1)
            Utils.closeSweetProgressDialog(getActivity(), mDialog);
        return userFeedList;
    }

    @Override
    public void onRefresh() {
        InterfaceListener.onAdView();
        mPageNo = 1;
        getUserFeeds();
    }

    /**
     * get Profit Data of User
     *
     * @return boolean
     */
    private boolean getProfitAmount() {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_PROFIT_AMOUNT,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        res = response.toString();
                        try {
                            setProfitData(res);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }, new volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                res = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIsConstants.KEY_USER_ID, new Utils().getSharedPreferences(getActivity(),
                        Constants.KEY_USER_DETAILS, ""));
                Utils.info("params... of " + APIsConstants.API_RECENT_FEEDS, params.toString());
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }

    private void setProfitData(String res) {
        Utils.debug(TAG, res);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            try {
                if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
                    try {
                        mTotalProfit = jsonObject.getString(Constants.KEY_TOTAL_PROFIT);
                        Utils.debug(TAG, mTotalProfit);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        mTodayProfit = jsonObject.getString(Constants.KEY_TODAY_PROFIT);
                        Utils.debug(TAG, mTodayProfit);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        mUnreceivedProfit = jsonObject.getString(Constants.KEY_UNRECEIVED_PROFIT);
                        Utils.debug(TAG, mUnreceivedProfit);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        mThisMonthProfit = jsonObject.getString(Constants.KEY_THIS_MONTH_PROFIT);
                        Utils.debug(TAG, mThisMonthProfit);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                updateProfitDetails();
                mLatestFeedsAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void updateProfitDetails() {

        // txt_todays_profit_value.setVisibility(View.GONE);
        if (mTodayProfit.equalsIgnoreCase(Constants.ZERO)) {
            txt_todays_profit_value.setVisibility(View.GONE);
        } else {
            txt_todays_profit_value.setVisibility(View.VISIBLE);
            txt_todays_profit_value.setText(Constants.DOLLAR_SIGN + " " + mTodayProfit);
        }

        if (mThisMonthProfit.equalsIgnoreCase(Constants.ZERO)) {
            txt_this_month_profit_value.setVisibility(View.GONE);
        } else {
            txt_this_month_profit_value.setVisibility(View.VISIBLE);
            txt_this_month_profit_value.setText(Constants.DOLLAR_SIGN + " " + mThisMonthProfit);
        }
        if (mUnreceivedProfit.equalsIgnoreCase(Constants.ZERO)) {
            txt_unreceived_profit_value.setVisibility(View.GONE);
        } else {
            txt_unreceived_profit_value.setVisibility(View.VISIBLE);
            txt_unreceived_profit_value.setText(Constants.DOLLAR_SIGN + " " + mUnreceivedProfit);
        }
//        if (mTotalProfit.equalsIgnoreCase(Constants.ZERO)) {
//            txt_heading_todays_profit_value.setVisibility(View.GONE);
//        } else {
        txt_heading_todays_profit_value.setVisibility(View.VISIBLE);
        txt_heading_todays_profit_value.setText(Constants.DOLLAR_SIGN + " " + mTotalProfit);
//        }

    }

  /*  int ITEMS_ON_PAGE = 0;
    private static final int TOTAL_PAGES = 10;
    private static final long DELAY = 1000L;
    private boolean loading = false;

    private final Handler handler = new Handler();

    @Override
    public boolean shouldLoad() {
//        return false;
        return !loading && mLatestFeedsAdapter.getItemCount() / ITEMS_ON_PAGE < TOTAL_PAGES;

    }


    @Override
    public void loadNextPage() {
        loading = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPageNo = mPageNo + 1;                //addItems();

                getUserFeeds();

                user_feeds_list.setRefreshing(false);
                loading = false;
                //addItems();
            }
        }, DELAY);
    }

    private void addItems() {
        mLatestFeedsAdapter.setCount(mLatestFeedsAdapter.getItemCount() + ITEMS_ON_PAGE);
    }*/
}
