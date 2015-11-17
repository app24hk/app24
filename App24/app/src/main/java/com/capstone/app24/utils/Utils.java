package com.capstone.app24.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.capstone.app24.R;
import com.capstone.app24.interfaces.OnScrolling;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amritpal on 4/11/15.
 */
public class Utils {

    public static OnScrolling mScrolling;

    private Context _context;

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
    public static SweetAlertDialog showSweetProgressDialog(Context context, String loadingtext) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
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

    public void setPreferences(Activity activity, String key, boolean value) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getSharedPreferences(Activity activity, String key) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        boolean b = sharedPref.getBoolean(key, false);
        return b;
    }

}
