package com.capstone.hk.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by amritpal on 4/11/15.
 */
public class AlertToastManager {


    public static void showToast(String msg, Context context) {
        if (GlobalClass.debuging) {
            Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.show();

        }
    }

}
