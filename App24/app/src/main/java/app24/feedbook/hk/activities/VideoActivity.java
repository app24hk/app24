package app24.feedbook.hk.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import app24.feedbook.hk.R;
import app24.feedbook.hk.bean.LatestFeedsModel;
import app24.feedbook.hk.utils.Utils;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amritpal on 7/11/15.
 */
public class VideoActivity extends Activity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener /*implements MediaPlayer.OnPreparedListener */ {
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
    private MediaController videoMediaController;
    private SweetAlertDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

//        UrlPath = "android.resource://" + getPackageName() + "/" + R.raw.itc;
        LatestFeedsModel latestFeedsModel = new Utils(this).getLatestFeedPreferences(this);
        Utils.debug("VideoActivity", "Inside Video Activity");
        video = (VideoView) findViewById(R.id.videoView);

        String mUrl = latestFeedsModel.getMedia();
        Utils.debug(TAG, "mUrl : " + mUrl);
        videoMediaController = new MediaController(this);
        video.setVideoPath(mUrl);
        videoMediaController.setMediaPlayer(video);
        video.setMediaController(videoMediaController);
        video.requestFocus();
        video.start();
        mDialog = Utils.showSweetProgressDialog(this, "Please wait...", SweetAlertDialog
                .PROGRESS_TYPE);
        mDialog.setCancelable(true);
        video.setOnPreparedListener(this);
        video.setOnInfoListener(this);
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
        super.onBackPressed();
        finish();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (mp.isPlaying()) {
            Utils.closeSweetProgressDialog(this, mDialog);
        }
        return false;
    }
}

