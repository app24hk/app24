package com.capstone.app24.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.capstone.app24.R;

/**
 * Created by amritpal on 7/11/15.
 */
public class VideoActivity extends Activity {
    private ProgressDialog pDialog;
    private VideoView myvideoview;
    private String VideoURL = "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_video);
        myvideoview = (VideoView) findViewById(R.id.myvideoview);
        // Execute StreamVideo AsyncTask

        // Create a progressbar
        pDialog = new ProgressDialog(VideoActivity.this);
        // Set progressbar title
        pDialog.setTitle("Android Video Streaming Tutorial");
        // Set progressbar message
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    VideoActivity.this);
            mediacontroller.setAnchorView(myvideoview);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(VideoURL);
            myvideoview.setMediaController(mediacontroller);
            myvideoview.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        myvideoview.requestFocus();
        myvideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                myvideoview.start();
            }
        });

    }
}

