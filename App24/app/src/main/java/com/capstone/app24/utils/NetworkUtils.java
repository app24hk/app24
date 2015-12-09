package com.capstone.app24.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by amritpal on 3/12/15.
 */
public class NetworkUtils {
    Activity mActivity;

    public NetworkUtils(Activity activity) {
        mActivity = activity;
    }

    /***
     * Method Name : isOnline()
     *
     * @param : activity
     * @return : true/false.
     * Description : Used to check whether Internet is available or not.
     */
    public static boolean isOnline(Activity activity) {
        ConnectivityManager conMgr = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        // ARE WE CONNECTED TO THE NET

        if (conMgr.getActiveNetworkInfo() != null

                && conMgr.getActiveNetworkInfo().isAvailable()

                && conMgr.getActiveNetworkInfo().isConnected()) {

            return true;

        } else {
            return false;

        }

    }
}
