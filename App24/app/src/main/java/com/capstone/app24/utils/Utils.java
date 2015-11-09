package com.capstone.app24.utils;

import android.content.Context;
import android.util.Log;

import com.capstone.app24.R;
import com.capstone.app24.interfaces.OnListUpdateListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amritpal on 4/11/15.
 */
public class Utils {

    public static OnListUpdateListener mListUpdateListener;

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

    public static void setOnListUpdateListener(OnListUpdateListener listener) {
        Utils.mListUpdateListener = listener;
    }

    public static void onListUpdate() {
        try {
            mListUpdateListener.onListUpdate();
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


}
