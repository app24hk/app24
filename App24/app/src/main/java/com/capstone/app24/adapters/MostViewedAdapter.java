package com.capstone.app24.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.capstone.app24.R;
import com.capstone.app24.activities.PostDetailActivity;
import com.capstone.app24.activities.VideoActivity;
import com.capstone.app24.bean.LatestFeedsModel;
import com.capstone.app24.utils.AlertToastManager;
import com.capstone.app24.utils.TouchImageView;
import com.capstone.app24.utils.Utils;

/**
 * Created by amritpal on 4/11/15.
 */
public class MostViewedAdapter extends RecyclerView.Adapter<MostViewedAdapter.ViewHolder>
        implements View.OnClickListener {

    private static final String TAG = MostViewedAdapter.class.getSimpleName();
    private Activity mActivity;
    private LayoutInflater mInflater;
    Intent intent;

    public MostViewedAdapter(Activity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);

        //(LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_latest_feeds_with_image, null);
        ViewHolder viewHolder = new ViewHolder(view, mActivity);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MostViewedAdapter.ViewHolder holder, int position) {
        LatestFeedsModel latestFeedsModel = new LatestFeedsModel();

        if (position == 0) {
            holder.img_preview.setVisibility(View.VISIBLE);
            holder.img_video_preview.setVisibility(View.VISIBLE);
            holder.txt_feed_body.setOnClickListener(this);
            holder.img_preview.setOnClickListener(this);
        } else if (position == 1) {
            holder.img_preview.setVisibility(View.VISIBLE);
            holder.txt_feed_body.setOnClickListener(this);
            holder.img_preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImageDialog();
//                    Intent intent = new Intent(mActivity, GalleryActivity.class);
//                    mActivity.startActivity(intent);
                }
            });
            holder.img_video_preview.setVisibility(View.GONE);
        } else {
            holder.img_preview.setVisibility(View.GONE);
            holder.txt_feed_body.setOnClickListener(this);
            holder.img_video_preview.setVisibility(View.GONE);

        }

        Utils.debug(TAG, "MostViewedAdapter Adapter onBindViewHolder ");

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_preview:
                Intent intent = new Intent(mActivity, VideoActivity.class);
                mActivity.startActivity(intent);
                //AlertToastManager.showToast("Video Preview is not available", mActivity);
                break;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_feed_heading, txt_creator, txt_created_time, txt_profile_count_login_user,
                txt_feed_body, txt_seen;
        ImageView img_preview, img_video_preview;

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

            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            txt_feed_body.setOnClickListener(this);
            txt_feed_body.setClickable(true);
        }


        @Override
        public void onClick(View v) {
           /* switch (v.getId()) {
                case R.id.txt_feed_body:*/
            intent = new Intent(mActivity, PostDetailActivity.class);
            intent.putExtra("type", getLayoutPosition());
            mActivity.startActivity(intent);
                    /*break;
            }
*/
            /*Intent intent = new Intent(mActivity, PostDetailActivity.class);
            mActivity.startActivity(intent);*/
        }
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

