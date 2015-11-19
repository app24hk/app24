package com.capstone.app24.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.capstone.app24.R;
import com.capstone.app24.activities.MainActivity;
import com.capstone.app24.activities.PostDetailActivity;
import com.capstone.app24.activities.VideoActivity;
import com.capstone.app24.bean.LatestFeedsModel;
import com.capstone.app24.utils.TouchImageView;
import com.capstone.app24.utils.Utils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by amritpal on 4/11/15.
 */
public class LatestFeedsAdapter extends RecyclerView.Adapter<LatestFeedsAdapter.ViewHolder> {

    private static final String TAG = LatestFeedsAdapter.class.getSimpleName();
    private Activity mActivity;
    private LayoutInflater mInflater;
    Intent intent;

    public LatestFeedsAdapter(Activity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_latest_feeds_with_image, null);
        ViewHolder viewHolder = new ViewHolder(view, mActivity);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final LatestFeedsAdapter.ViewHolder holder, final int position) {
        LatestFeedsModel latestFeedsModel = new LatestFeedsModel();
        if (position == 0) {
            holder.xtra_layout.setVisibility(View.VISIBLE);
        } else {
            holder.xtra_layout.setVisibility(View.GONE);
        }
        if (position % 3 == 0) {
            holder.img_preview.setVisibility(View.VISIBLE);
            holder.img_video_preview.setVisibility(View.VISIBLE);
            holder.layout_img_video_preview.setVisibility(View.VISIBLE);
            holder.txt_feed_body.setText(mActivity.getResources().getString(R.string.chinese_lorem_ipsum));
        } else if (position % 3 == 1) {
            holder.img_preview.setVisibility(View.VISIBLE);
            holder.img_video_preview.setVisibility(View.GONE);
            holder.txt_feed_body.setText(mActivity.getResources().getString(R.string.lorem_ipsum));
        } else {
            holder.img_preview.setVisibility(View.GONE);
            holder.img_video_preview.setVisibility(View.GONE);
            holder.layout_img_video_preview.setVisibility(View.GONE);
            holder.txt_feed_body.setText(mActivity.getResources().getString(R.string.lorem_ipsum));

        }
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layout_feed_body = null;
        //        private VideoView video = null;
        TextView txt_feed_heading, txt_creator, txt_created_time, txt_profile_count_login_user,
                txt_feed_body, txt_seen;
        ImageView img_preview, img_video_preview;
        RelativeLayout layout_img_video_preview, xtra_layout;

        public ViewHolder(View itemView, Activity act) {
            super(itemView);

//            if (itemView == null) {
            txt_feed_heading = (TextView) itemView.findViewById(R.id.txt_feed_heading);
            txt_creator = (TextView) itemView.findViewById(R.id.txt_creator);
            txt_created_time = (TextView) itemView.findViewById(R.id.txt_created_time);
            txt_profile_count_login_user = (TextView) itemView.findViewById(R.id.txt_profile_count_login_user);
            txt_feed_body = (TextView) itemView.findViewById(R.id.txt_feed_body);
            txt_seen = (TextView) itemView.findViewById(R.id.txt_seen);
            img_preview = (ImageView) itemView.findViewById(R.id.img_preview);
            img_video_preview = (ImageView) itemView.findViewById(R.id.img_video_preview);
            layout_img_video_preview = (RelativeLayout) itemView.findViewById(R.id.layout_img_video_preview);
            xtra_layout = (RelativeLayout) itemView.findViewById(R.id.xtra_layout);
            layout_feed_body = (LinearLayout) itemView.findViewById(R.id.layout_feed_body);
//            video = (VideoView) itemView.findViewById(R.id.video);
           /* itemView.setClickable(true);*/
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(mActivity, PostDetailActivity.class);
                    intent.putExtra("type", getLayoutPosition());
                    mActivity.startActivity(intent);
                }
            });

            img_preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImageDialog();
                }
            });
            img_video_preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    video.setVisibility(View.VISIBLE);
//                    img_preview.setVisibility(View.GONE);
//                    img_video_preview.setVisibility(View.GONE);
//                    String UrlPath = "android.resource://" + mActivity.getPackageName() + "/" + R
//                            .raw
//                            .itcuties;
//                    video.setVideoURI(Uri.parse(UrlPath));
//                    video.start();
                    intent = new Intent(mActivity, VideoActivity.class);
                    mActivity.startActivity(intent);
                }
            });
            layout_img_video_preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (img_video_preview.getVisibility() == View.VISIBLE) {
//                        video.setVisibility(View.VISIBLE);
//                        img_preview.setVisibility(View.GONE);
//                        img_video_preview.setVisibility(View.GONE);
//                        String UrlPath = "android.resource://" + mActivity.getPackageName() + "/" + R.raw.itcuties;
//                        video.setVideoURI(Uri.parse(UrlPath));
//                        video.start();
                        intent = new Intent(mActivity, VideoActivity.class);
                        mActivity.startActivity(intent);
                    } else if (img_video_preview
                            .getVisibility() == View.GONE && img_preview.getVisibility() == View.VISIBLE) {
                        showImageDialog();
                    }
//                    else if (img_video_preview
//                            .getVisibility() == View.GONE) {
//                        video.pause();
//                        video.setVisibility(View.GONE);
//                        img_preview.setVisibility(View.VISIBLE);
//                        img_preview.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.pic_two));
//                        img_video_preview.setVisibility(View.VISIBLE);
//                    }
//                    intent = new Intent(mActivity, VideoActivity.class);
//                    mActivity.startActivity(intent);
                }
            });
//            video.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Utils.debug(TAG, "img_video_preview" + img_video_preview.getVisibility());
//                    Utils.debug(TAG, "video" + video.getVisibility());
//                    Utils.debug(TAG, "img_preview" + img_preview.getVisibility());
//                    if (video.isPlaying()) {
//                        video.stopPlayback();
//                        video.setVisibility(View.GONE);
//                        img_preview.setVisibility(View.VISIBLE);
//                        img_preview.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.pic_two));
//                        img_video_preview.setVisibility(View.VISIBLE);
//                    }
//                }
//            });
            // }

        }


        //............FullView imageView..............
        public void showImageDialog() {
            Display display = mActivity.getWindowManager().getDefaultDisplay();
            int width = display.getWidth();
            int height = display.getHeight();

            final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
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


}

