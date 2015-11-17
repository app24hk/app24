package com.capstone.app24.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.capstone.app24.R;
import com.capstone.app24.utils.Utils;

import java.io.IOException;

/**
 * Created by amritpal on 7/11/15.
 */
public class VideoActivity extends Activity implements MediaPlayer.OnPreparedListener {
    private static final String TAG = VideoActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private VideoView mVideoView;
    private MediaPlayer mp;
    // private String VideoURL = "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Utils.debug("VideoActivity", "Inside Video Activity");
        final VideoView video = (VideoView) findViewById(R.id.videoView);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        video.setVideoPath("http://download.itcuties.com/teaser/itcuties-teaser-480.mp4");
        video.start();
        video.setOnPreparedListener(this);
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        pDialog.dismiss();
    }
}

