package com.capstone.app24.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.capstone.app24.R;
import com.capstone.app24.webservices_model.FeedRequestModel;
import com.capstone.app24.webservices_model.UserLoginModel;
import com.google.gson.Gson;

/**
 * Created by amritpal on 27/11/15.
 */
public class Session {
    private static final String TAG = Session.class.getSimpleName();
    Activity mActivity;
    Context mContext;
    SharedPreferences mPrefs;

    public Session(Activity mActivity) {
        this.mActivity = mActivity;
        mPrefs = mActivity.getSharedPreferences(mActivity.getResources().getString(R.string
                .app_name), Context.MODE_PRIVATE);
    }

//    public Session(Context mContext) {
//        this.mContext = mContext;
//        mPrefs = mActivity.getPreferences(Context.MODE_PRIVATE);
//    }

    public void setUser(UserLoginModel userLoginModel) {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userLoginModel);
        prefsEditor.putString(Constants.KEY_USER_DETAILS, json);
        prefsEditor.commit();
        Utils.debug(TAG, "User Saved Successfully");
    }

    public UserLoginModel getUser() {
        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.KEY_USER_DETAILS, "");
        UserLoginModel userLoginModel = gson.fromJson(json, UserLoginModel.class);
        return userLoginModel;
    }

    public void setFeed(FeedRequestModel feedModel) {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(feedModel);
        prefsEditor.putString(Constants.KEY_USER_DETAILS, json);
        prefsEditor.commit();
        Utils.debug(TAG, "User Saved Successfully");
    }

    public FeedRequestModel getFeed() {
        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.KEY_USER_DETAILS, "");
        FeedRequestModel feedModel = gson.fromJson(json, FeedRequestModel.class);
        return feedModel;
    }
}
