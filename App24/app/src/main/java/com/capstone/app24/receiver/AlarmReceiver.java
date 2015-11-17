package com.capstone.app24.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.capstone.app24.R;
import com.capstone.app24.utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Calendar;

/**
 * Created by amritpal on 16/11/15.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();
    public static InterstitialAd mInterstitialAd;

    public static InterstitialAd getInstance(Context context) {
        if (mInterstitialAd == null) {
            mInterstitialAd = new InterstitialAd(context);

        }
        return mInterstitialAd;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        setAlarm(context);
        mInterstitialAd.setAdUnitId(context.getResources().getString(R.string
                .interstitial_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial(context);
                //beginPlayingGame();
            }
        });

        requestNewInterstitial(context);
    }

    private void requestNewInterstitial(Context context) {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();
        //Test Device Id
        // "AFA12AE3A6981A0F0745048448E82F44"
        AlarmReceiver.getInstance(context).loadAd(adRequest);

    }

    public void setAlarm(Context context) {
        Utils.debug(TAG, "Setting Ad cAlarm");
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(System.currentTimeMillis());
        time.add(Calendar.SECOND, 30);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);
    }
}
