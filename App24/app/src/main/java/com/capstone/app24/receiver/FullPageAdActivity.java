package com.capstone.app24.receiver;

import android.app.Activity;
import android.content.Context;

import com.capstone.app24.R;
import com.capstone.app24.utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by amritpal on 20/11/15.
 */
public class FullPageAdActivity {


    private static final String TAG = FullPageAdActivity.class.getSimpleName();
    private static InterstitialAd mInterstitialAd;

    public static InterstitialAd getInstance(Context context) {
        if (mInterstitialAd == null) {
            mInterstitialAd = new InterstitialAd(context);
            Utils.debug(TAG, "get Instance of InterstitialAd  ");
        }
        return mInterstitialAd;
    }

    public FullPageAdActivity(Context context) {
        // mInterstitialAd = new InterstitialAd(activity);
        getInstance(context).setAdUnitId(context.getResources().getString(R.string
                .interstitial_ad_unit_id));
        Utils.debug(TAG, "InterstitialAd  Calling");
        requestNewInterstitial();

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                // beginPlayingGame();
            }
        });
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();
        //Test Device Id
        // "AFA12AE3A6981A0F0745048448E82F44"
        Utils.debug(TAG, "InterstitialAd Loading.. ");
        mInterstitialAd.loadAd(adRequest);
        Utils.debug(TAG, "InterstitialAd Loaded ");


    }

}
