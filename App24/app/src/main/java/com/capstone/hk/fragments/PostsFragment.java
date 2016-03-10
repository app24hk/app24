package com.capstone.hk.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.capstone.hk.R;
import com.capstone.hk.adapters.UserFeedAdapter;
import com.capstone.hk.bean.UserFeedModel;
import com.capstone.hk.utils.APIsConstants;
import com.capstone.hk.utils.AppController;
import com.capstone.hk.utils.Constants;
import com.capstone.hk.utils.NetworkUtils;
import com.capstone.hk.utils.Utils;

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
public class PostsFragment extends Fragment implements TextWatcher {
    private static final String TAG = PostsFragment.class.getSimpleName();
    View mView;
    ArrayList<UserFeedModel> mUserFeedModelArrayList = new ArrayList<>();
    private UserFeedAdapter mUserFeedsAdapter;
    private RecyclerView list_user_feeds;
    EditText edit_search;
    /* Volley Request Tags */
    private String res = "";
    private String tag_string_req = "feeds_req";
    private int mPageNo = 1;
    private SweetAlertDialog mDialog;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_posts, container, false);
        Bundle bundle = getArguments();
        initializeViews();
        try {
            mUserFeedModelArrayList = bundle.getParcelableArrayList(Constants
                    .SEARCH_LIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setClickListener();
        updateUI();
        return mView;
    }

    private void updateUI() {
        if (NetworkUtils.isOnline(getActivity()))
            getLatestFeeds();
        else
            Utils.showSweetProgressDialog(getActivity(), this.getResources().getString(R
                    .string.check_your_internet_connection), SweetAlertDialog.WARNING_TYPE);


        mUserFeedsAdapter = new UserFeedAdapter(getActivity(), mUserFeedModelArrayList);
        list_user_feeds.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        list_user_feeds.setLayoutManager(linearLayoutManager);
        list_user_feeds.setAdapter(mUserFeedsAdapter);
        mUserFeedsAdapter.notifyDataSetChanged();
    }

    private void setClickListener() {
        edit_search.addTextChangedListener(this);

    }

    private void initializeViews() {
        list_user_feeds = (RecyclerView) mView.findViewById(R.id.list_user_feeds);
        edit_search = (EditText) getActivity().findViewById(R.id.edit_search);
    }

    public boolean getLatestFeeds() {
        mDialog = Utils.showSweetProgressDialog(getActivity(),
                getResources
                        ().getString(R.string.progress_loading), SweetAlertDialog.PROGRESS_TYPE);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_GET_ALL_USER_FEED,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIsConstants.KEY_USER_ID, new Utils(getActivity())
                        .getSharedPreferences
                                (getActivity(), Constants.KEY_USER_DETAILS, ""));
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }

    private List<UserFeedModel> refreshLatestFeeds(String res) throws JSONException {
        Utils.debug(TAG, "Result Of Profile Activity " + res);
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
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setTitle(object.getString(APIsConstants.KEY_TITLE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setDescription(object.getString(APIsConstants.KEY_DESCRIPTION));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setMedia(object.getString(APIsConstants.KEY_MEDIA));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setType(object.getString(APIsConstants.KEY_TYPE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setUser_id(object.getString(APIsConstants.KEY_USER_ID));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setCreated(object.getString(APIsConstants.KEY_CREATED));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setModified(object.getString(APIsConstants.KEY_MODIFIED));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setUser_name(object.getString(APIsConstants.KEY_USER_NAME));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setViewcount(object.getString(APIsConstants.KEY_VIEWCOUNT));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setThumbnail(object.getString(APIsConstants.KEY_THUMBNAIL));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                userFeedModel.setProfit_amount(object.getString(APIsConstants
                                        .KEY_PROFIT_AMOUNT));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (!mUserFeedModelArrayList.contains(userFeedModel)) {
                                mUserFeedModelArrayList.add(userFeedModel);
                            }
                        }
                    }
                }
            } else {
                try {
                    Utils.debug(Constants.API_TAG, jsonObject.getString(APIsConstants.KEY_MESSAGE));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Utils.closeSweetProgressDialog(getActivity(), mDialog);
        mUserFeedsAdapter.notifyDataSetChanged();
        return mUserFeedModelArrayList;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mUserFeedsAdapter.getFilter().filter(s.toString().trim());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
