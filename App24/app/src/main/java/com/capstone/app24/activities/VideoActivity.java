package com.capstone.app24.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.capstone.app24.R;
import com.capstone.app24.utils.Utils;

/**
 * Created by amritpal on 7/11/15.
 */
public class VideoActivity extends Activity /*implements MediaPlayer.OnPreparedListener */ {
    private static final String TAG = VideoActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    public SurfaceHolder sholder;
    VideoView video;
    String UrlPath;
    private boolean fullscreen;
    int actualHeight;
    // private String Video
    // URL = "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";

    Bundle newBundy = new Bundle();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

//        UrlPath = "android.resource://" + getPackageName() + "/" + R.raw.itc;

        Utils.debug("VideoActivity", "Inside Video Activity");
        video = (VideoView) findViewById(R.id.videoView);
//        pDialog = new ProgressDialog(this);
//        pDialog.setMessage("Loading...");
//        pDialog.setCancelable(false);
//        pDialog.show();
//        video.setVideoPath("http://download.itcuties.com/teaser/itcuties-teaser-480.mp4");
//        video.start();
//        video.setOnPreparedListener(this);
//        getSurfacehHolder(sholder);
//
//
        String UrlPath = "android.resource://" + getPackageName() + "/" + R.raw.itcuties;
        video.setVideoURI(Uri.parse(UrlPath));
        video.start();
        RelativeLayout.LayoutParams playerParams = (android.widget.RelativeLayout.LayoutParams) video
                .getLayoutParams();
        actualHeight = playerParams.height;


    }

    //    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        // Checks the orientation of the screen
//        Utils.debug(TAG, "onConfigurationChanged");
//
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
////            Utils.debug(TAG, "Orientation Landscape");
////            fullscreen = true;
////            doLayout();
//            // fullScreenVideo();
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
////            fullscreen = false;
////            doLayout();
////            Utils.debug(TAG, "Orientation Portrait");
//        }
//    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fullscreen = true;
            doLayout();
            onSaveInstanceState(newBundy);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            fullscreen = false;
            doLayout();
            onSaveInstanceState(newBundy);
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBundle("newBundy", newBundy);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        savedInstanceState.getBundle("newBundy");
//    }

    public void doLayout() {
        // pan_tilt_zoom_rl
        RelativeLayout.LayoutParams playerParams = (RelativeLayout.LayoutParams) video
                .getLayoutParams();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Utils.debug(TAG, "Height : " + height);
        Utils.debug(TAG, "Width : " + width);
        if (fullscreen) {
            playerParams.height = height;
            playerParams.width = width;

        } else {
            playerParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = actualHeight;
//            playerParams.height = (int) getResources().getDimension(
//                    R.dimen.live_view_height);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (video != null) {
            if (video.isPlaying()) {
                video.stopPlayback();
                video = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

