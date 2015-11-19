package com.capstone.app24.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
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

import com.capstone.app24.R;
import com.capstone.app24.activities.PostDetailActivity;
import com.capstone.app24.activities.VideoActivity;
import com.capstone.app24.bean.LatestFeedsModel;
import com.capstone.app24.utils.TouchImageView;

/**
 * Created by amritpal on 4/11/15.
 */
public class UserProfitAdapter extends RecyclerView.Adapter<UserProfitAdapter.ViewHolder> {

    private static final String TAG = UserProfitAdapter.class.getSimpleName();
    private Activity mActivity;
    private LayoutInflater mInflater;
    Intent intent;

    public UserProfitAdapter(Activity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_profit_and_feeds_with_image, null);
        ViewHolder viewHolder = new ViewHolder(view, mActivity);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserProfitAdapter.ViewHolder holder, final int position) {
        LatestFeedsModel latestFeedsModel = new LatestFeedsModel();
        if (position == 0) {
            holder.xtra_layout.setVisibility(View.VISIBLE);
        } else {
            holder.xtra_layout.setVisibility(View.GONE);
        }
        if (position == 0) {
            holder.view_profit_and_feeds.setVisibility(View.VISIBLE);
            holder.img_preview.setVisibility(View.VISIBLE);
            holder.img_video_preview.setVisibility(View.VISIBLE);
            holder.layout_img_video_preview.setVisibility(View.VISIBLE);
            Uri videoURI = Uri.parse("android.resource://" + mActivity.getPackageName() + "/"
                    + R.raw.itcuties);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mActivity, videoURI);
            Bitmap bitmap = retriever
                    .getFrameAtTime(10, MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);
            Drawable drawable = new BitmapDrawable(mActivity.getResources(), bitmap);
            holder.img_preview.setImageDrawable(drawable);
        } else if (position == 1) {
            holder.view_profit_and_feeds.setVisibility(View.GONE);
            holder.img_preview.setVisibility
                    (View.VISIBLE);
            holder.img_video_preview.setVisibility(View.GONE);
            holder.img_preview.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.pic_two));
        } else {
            holder.view_profit_and_feeds.setVisibility(View.GONE);
            holder.img_preview.setVisibility(View.GONE);
            holder.layout_img_video_preview.setVisibility(View.GONE);
            holder.img_video_preview.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout layout_feed_body;
        private final LinearLayout view_profit_and_feeds;
        TextView txt_feed_heading, txt_creator, txt_created_time, txt_profile_count_login_user,
                txt_feed_body, txt_seen;
        ImageView img_preview, img_video_preview;
        RelativeLayout layout_img_video_preview, xtra_layout;

        public ViewHolder(View itemView, Activity act) {
            super(itemView);
            txt_feed_heading = (TextView) itemView.findViewById(R.id.txt_feed_heading);
            txt_creator = (TextView) itemView.findViewById(R.id.txt_creator);
            txt_created_time = (TextView) itemView.findViewById(R.id.txt_created_time);
            txt_profile_count_login_user = (TextView) itemView.findViewById(R.id.txt_profile_count_login_user);
            txt_feed_body = (TextView) itemView.findViewById(R.id.txt_feed_body);
            txt_seen = (TextView) itemView.findViewById(R.id.txt_seen);
            img_preview = (ImageView) itemView.findViewById(R.id.img_preview);
            img_video_preview = (ImageView) itemView.findViewById(R.id.img_video_preview);
            layout_feed_body = (LinearLayout) itemView.findViewById(R.id.layout_feed_body);
            view_profit_and_feeds = (LinearLayout) itemView.findViewById(R.id.view_profit_and_feeds);
            layout_img_video_preview = (RelativeLayout) itemView.findViewById(R.id.layout_img_video_preview);
            xtra_layout = (RelativeLayout) itemView.findViewById(R.id.xtra_layout);
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
//                    intent = new Intent(mActivity, VideoActivity.class);
//                    mActivity.startActivity(intent);
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
        }


     /*   @Override
        public void onClick(View v) {
            AlertToastManager.showToast("Clicked on adapter Item", mActivity);
           *//* switch (v.getId()) {
                case R.id.txt_feed_body:*//*
            intent = new Intent(mActivity, PostDetailActivity.class);
            intent.putExtra("type", getLayoutPosition());
            mActivity.startActivity(intent);
                    *//*break;
            }
*//*
            *//*Intent intent = new Intent(mActivity, PostDetailActivity.class);
            mActivity.startActivity(intent);*//*
        }*/
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
        custom_image.setImageResource(R.drawable.pic_two);

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

