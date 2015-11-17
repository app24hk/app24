package com.capstone.app24.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.capstone.app24.R;
import com.capstone.app24.utils.AlertToastManager;
import com.capstone.app24.utils.TouchImageView;
import com.capstone.app24.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by amritpal on 4/11/15.
 */
public class PostDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = PostDetailActivity.class.getSimpleName();
    private RelativeLayout menu_layout;
    private RelativeLayout edit_menu;
    private TextView txt_edit, txt_delete;
    private int type;
    private RelativeLayout layout_media_preview;
    private ImageView img_preview, img_video_preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        setHeader(null, true, true, false, false, true, null);
        type = getIntent().getIntExtra("type", 0);
        initializeViews();
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        setClickListeners();
        updateUI();
    }

    private void updateUI() {

        if (type == 0) {
            img_preview.setVisibility(View.VISIBLE);
            img_video_preview.setVisibility(View.VISIBLE);
            layout_media_preview.setVisibility(View.VISIBLE);
            img_video_preview.setOnClickListener(this);
            img_preview.setOnClickListener(this);
        } else if (type == 1) {
            img_preview.setVisibility(View.VISIBLE);
            layout_media_preview.setVisibility(View.VISIBLE);
            img_video_preview.setVisibility(View.GONE);
            img_preview.setOnClickListener(this);
        } else {
            layout_media_preview.setVisibility(View.GONE);
            img_preview.setVisibility(View.GONE);
            img_video_preview.setVisibility(View.GONE);
        }
    }

    private void initializeViews() {
        menu_layout = (RelativeLayout) findViewById(R.id.menu_layout);
        edit_menu = (RelativeLayout) findViewById(R.id.edit_menu);
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        txt_edit = (TextView) findViewById(R.id.txt_edit);
        txt_delete = (TextView) findViewById(R.id.txt_delete);
        img_preview = (ImageView) findViewById(R.id.img_preview);
        img_video_preview = (ImageView) findViewById(R.id.img_video_preview);
        layout_media_preview = (RelativeLayout) findViewById(R.id.layout_media_preview);

    }

    private void setClickListeners() {
        ibtn_share.setOnClickListener(this);
        ibtn_dots.setOnClickListener(this);
        ibtn_back.setOnClickListener(this);
        txt_edit.setOnClickListener(this);
        txt_delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.ibtn_dots:

                if (edit_menu.getVisibility() == View.VISIBLE) {
                    edit_menu.setVisibility(View.GONE);
                    Animation fadeoutAnim = AnimationUtils.loadAnimation(this, R.anim.fadeout);
                    edit_menu.startAnimation(fadeoutAnim);
                } else {
                    edit_menu.setVisibility(View.VISIBLE);
                    Animation fadeinAnim = AnimationUtils.loadAnimation(this, R.anim.fadein);
                    edit_menu.startAnimation(fadeinAnim);
                }
                break;
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.txt_edit:
                edit_menu.setVisibility(View.GONE);
                AlertToastManager.showToast("Edit", this);
                break;
            case R.id.txt_delete:
                edit_menu.setVisibility(View.GONE);
                AlertToastManager.showToast("Delete", this);
                break;
            case R.id.img_preview:
                showImageDialog();
                break;
            case R.id.img_video_preview:
                edit_menu.setVisibility(View.GONE);
                AlertToastManager.showToast("Video Preview is not available", this);
                break;
        }
    }

    //............FullView imageView..............
    public void showImageDialog() {
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.custom_image_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        final TouchImageView custom_image = (TouchImageView) (dialog.findViewById(R.id.custom_image));
        custom_image.setLayoutParams(params);
        custom_image.setImageResource(R.drawable.ads);

        custom_image.setOnTouchImageViewListener(new TouchImageView.OnTouchImageViewListener() {
            @Override
            public void onMove() {
                PointF point = custom_image.getScrollPosition();
                RectF rect = custom_image.getZoomedRect();
                float currentZoom = custom_image.getCurrentZoom();
                boolean isZoomed = custom_image.isZoomed();
            }
        });
        dialog.show();
    }
}
