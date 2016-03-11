package app24.feedbook.hk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import app24.feedbook.hk.R;

import com.google.gson.Gson;

import app24.feedbook.hk.activities.SplashActivity;
import app24.feedbook.hk.bean.OwnerDataModel;
import app24.feedbook.hk.webservices_model.FeedRequestModel;
import app24.feedbook.hk.webservices_model.UserLoginModel;

/**
 * Created by amritpal on 27/11/15.
 */
public class Session {
    private static final String TAG = Session.class.getSimpleName();
    Activity mActivity;
    Context mContext;
    SharedPreferences mPrefs;
    public static OwnerDataModel mOwnerDataModel;

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

    public static void setOwnerModel(OwnerDataModel ownerDataModel) {
        if (mOwnerDataModel == null)
            mOwnerDataModel = new OwnerDataModel();
        mOwnerDataModel = ownerDataModel;
    }

    public static OwnerDataModel getOwnerModel() {
        return mOwnerDataModel;
    }

    /**
     * This method will logout user from App and Facebook.
     *
     * @param activity
     */
    public static void logout(Activity activity) {
        new Utils(activity).setPreferences
                (activity, Constants
                        .KEY_IS_LOGGED_IN, false);
        new Utils(activity).clearSharedPreferences(activity);
        Intent intent = new Intent(activity, SplashActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        activity.finish();
        activity.startActivity(intent);
    }
}
