package com.capstone.app24.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.capstone.app24.R;
import com.capstone.app24.bean.LatestFeedsModel;
import com.capstone.app24.bean.UserFeedModel;
import com.capstone.app24.interfaces.OnListUpdateListener;
import com.capstone.app24.interfaces.OnScrolling;
import com.capstone.app24.webservices_model.FeedRequestModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amritpal on 4/11/15.
 */
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();
    public static OnScrolling mScrolling;

    private Context _context;
    public static RecyclerView mRecyclerView;
    private static FeedRequestModel mFeedModel;


    // constructor
    public Utils(Context context) {
        this._context = context;
    }

    public Utils() {
        super();
    }

    public static void debug(String TAG, String msg) {
        if (GlobalClass.showComment) {
            Log.d(TAG, msg);
        }
    }

    public static void error(String TAG, String msg) {
        if (GlobalClass.showComment) {
            Log.e(TAG, msg);
        }
    }

    public static void warning(String TAG, String msg) {
        if (GlobalClass.showComment) {
            Log.w(TAG, msg);
        }
    }

    public static void info(String TAG, String msg) {
        if (GlobalClass.showComment) {
            Log.i(TAG, msg);
        }
    }

    private static void setOnScrolling(OnScrolling listener) {
        Utils.mScrolling = listener;

    }

    private static void setScrollDirection(int direction) {
        try {
            if (direction == Constants.SCROLL_UP) {
                mScrolling.ScrollUp(direction);
            }
            if (direction == Constants.SCROLL_DOWN) {
                mScrolling.ScrollDown(direction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //..........Common method for showing Sweet progress Dialog.........
    public static SweetAlertDialog showSweetProgressDialog(Context context, String loadingtext,
                                                           int progressType) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(context, progressType)
                .setTitleText(loadingtext == null ? context.getResources().getString(
                        R.string.progress_loading) : loadingtext);
        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;
    }


    //..........Common method for showing progress Dialog.........
    public static void closeSweetProgressDialog(Context context, SweetAlertDialog pdialog) {
        if (pdialog != null) {
            pdialog.dismiss();
        }
    }

    public static void showKeyboard(Activity activity, View v) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        // If no view currently has focus, create a new one, just
        // so
        // we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm.isAcceptingText()) {
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        }

    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        // If no view currently has focus, create a new one, just
        // so
        // we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm.isAcceptingText()) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();
        // If no view currently has focus, create a new one, just
        // so
        // we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static FeedRequestModel getFeed() {
        if (mFeedModel == null)
            mFeedModel = new FeedRequestModel();
        return mFeedModel;
    }

    public static void setFeed(FeedRequestModel feedModel) {
        if (mFeedModel == null)
            mFeedModel = new FeedRequestModel();
        mFeedModel = feedModel;
        Utils.debug(TAG, "FeedRequestModel Saved Successfully");
    }

    public void setPreferences(Activity activity, String key, boolean value) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources()
                .getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void setPreferences(Context activity, String key, boolean value) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources()
                .getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getSharedPreferences(Activity activity, String key, boolean defaultValue) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources()
                .getString(R.string.app_name), Context.MODE_PRIVATE);
        boolean b = sharedPref.getBoolean(key, defaultValue);
        return b;
    }

    public void setPreferences(Activity activity, String key, String value) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources()
                .getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void setPreferences(Context activity, String key, String value) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources()
                .getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getSharedPreferences(Activity activity, String key, String defaultValue) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources()
                .getString(R.string.app_name), Context.MODE_PRIVATE);
        String s = sharedPref.getString(key, defaultValue);
        return s;
    }

    public static int getHeight(Activity activity) {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        return height;

    }

    public void setLatestFeedPreferences(Activity activity, LatestFeedsModel model) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources()
                .getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(model);
        prefsEditor.putString(Constants.LATEST_FEED_MODEL, json);
        prefsEditor.commit();
        Log.e("Saved Model", "LatestModelSaved ");
    }

    public LatestFeedsModel getLatestFeedPreferences(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources()
                .getString(R.string.app_name), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPref.getString(Constants.LATEST_FEED_MODEL, "");
        LatestFeedsModel feedsModel = gson.fromJson(json, LatestFeedsModel.class);
        return feedsModel;
    }

    public void setUserFeedPreferences(Activity activity, UserFeedModel model) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources()
                .getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(model);
        prefsEditor.putString(Constants.LATEST_FEED_MODEL, json);
        prefsEditor.commit();
        Log.e("Saved Model", "LatestModelSaved ");
    }

    public UserFeedModel getUserFeedPreferences(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources()
                .getString(R.string.app_name), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPref.getString(Constants.LATEST_FEED_MODEL, "");
        UserFeedModel feedsModel = gson.fromJson(json, UserFeedModel.class);
        return feedsModel;
    }

    public void clearSharedPreferences(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources()
                .getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }


/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(Activity activity, long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return activity.getResources().getString(R.string.just_now);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return activity.getResources().getString(R.string.a_minute_ago);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + activity.getResources().getString(R.string.minute_ago);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return activity.getResources().getString(R.string.an_hour_ago);
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + activity.getResources().getString(R.string.hour_ago);
        } else if (diff < 48 * HOUR_MILLIS) {
            return activity.getResources().getString(R.string.yesterday);
        } else {
            return diff / DAY_MILLIS + activity.getResources().getString(R.string.day_ago);
        }
    }

    // validating email id
    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
