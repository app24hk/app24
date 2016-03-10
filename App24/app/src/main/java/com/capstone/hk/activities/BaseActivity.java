package com.capstone.hk.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.capstone.hk.R;
import com.capstone.hk.utils.Utils;

/**
 * Created by amritpal on 5/11/15.
 */
public class BaseActivity extends Activity implements View.OnClickListener {
    private static final String TAG = BaseActivity.class.getSimpleName();
    Context context;
    ImageButton ibtn_camera, ibtn_video, ibtn_share, ibtn_dots, ibtn_back;
    TextView txt_header, txt_save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }

    protected void setHeader(String header, boolean show_back, boolean show_share, boolean
            show_camera, boolean show_video, boolean show_dots, String save) {
        if (ibtn_back == null) {
            //    Utils.debug(TAG, "ibtn is not equal to null ");
            ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
            if (show_back) {
                Utils.debug(TAG, "Inside show back if");
                ibtn_back.setVisibility(View.VISIBLE);
                ibtn_back.setOnClickListener(this);

            } else {
                ibtn_back.setVisibility(View.GONE);
            }
        }
        if (ibtn_share == null) {
            ibtn_share = (ImageButton) findViewById(R.id.ibtn_share);
            if (show_share) {
                ibtn_share.setVisibility(View.VISIBLE);
                ibtn_share.setOnClickListener(this);
            } else {
                ibtn_share.setVisibility(View.GONE);
            }
        }
        if (ibtn_dots == null) {
            ibtn_dots = (ImageButton) findViewById(R.id.ibtn_dots);
            if (show_dots) {
                ibtn_dots.setVisibility(View.VISIBLE);
                ibtn_dots.setOnClickListener(this);
            } else {
                ibtn_dots.setVisibility(View.GONE);
            }

        } else {
            if (show_dots) {
                ibtn_dots.setVisibility(View.VISIBLE);
                ibtn_dots.setOnClickListener(this);
            } else {
                ibtn_dots.setVisibility(View.GONE);
            }
        }

        if (ibtn_camera == null) {
            ibtn_camera = (ImageButton) findViewById(R.id.ibtn_add_image);
            if (show_camera) {
                ibtn_camera.setVisibility(View.VISIBLE);
                ibtn_camera.setOnClickListener(this);
            } else {
                ibtn_camera.setVisibility(View.GONE);
            }
        }
        if (ibtn_video == null) {
            ibtn_video = (ImageButton) findViewById(R.id.ibtn_add_video);
            if (show_video) {
                ibtn_video.setVisibility(View.VISIBLE);
                ibtn_video.setOnClickListener(this);
            } else {
                ibtn_video.setVisibility(View.GONE);
            }
        }
        if (txt_save == null) {
            txt_save = (TextView) findViewById(R.id.txt_save);
            if (save != null) {
                txt_save.setVisibility(View.VISIBLE);
                txt_save.setText(save);
                txt_save.setOnClickListener(this);
            } else {
                txt_save.setVisibility(View.GONE);
            }
        } else {
            txt_save.setVisibility(View.GONE);
        }
        if (txt_header == null) {
            txt_header = (TextView) findViewById(R.id.txt_activity_header);
            if (header != null) {
                txt_header.setVisibility(View.VISIBLE);
                txt_header.setText(header);
            } else {
                txt_header.setVisibility(View.GONE);
            }
        } else {
            if (header != null) {
                txt_header.setVisibility(View.VISIBLE);
                txt_header.setText(header);
            } else {
                txt_header.setVisibility(View.GONE);
            }
            //txt_header.setVisibility(View.GONE);
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                overridePendingTransition(R.anim.fadeout, R.anim.fadein);

                break;
            case R.id.ibtn_add_image:
                break;
            case R.id.ibtn_add_video:

                break;
            case R.id.ibtn_dots:
                break;
            case R.id.ibtn_share:
                break;
            case R.id.txt_activity_header:
                break;
            default:
                break;
        }
    }
}
