package com.capstone.app24.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.capstone.app24.R;
import com.capstone.app24.activities.GalleryActivity;
import com.capstone.app24.activities.PostDetailActivity;
import com.capstone.app24.bean.LatestFeedsModel;
import com.capstone.app24.fragments.LatestFragment;
import com.capstone.app24.utils.AlertToastManager;
import com.capstone.app24.utils.TouchImageView;
import com.capstone.app24.utils.Utils;

/**
 * Created by amritpal on 4/11/15.
 */
public class LatestFeedsAdapter extends RecyclerView.Adapter<LatestFeedsAdapter.ViewHolder> {

    private static final String TAG = LatestFeedsAdapter.class.getSimpleName();
    private Activity mActivity;
    //  private LayoutInflater mInflater;

    public LatestFeedsAdapter(Activity activity) {
        mActivity = activity;
        // mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v;
        ViewHolder vh;

        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_latest_feeds_with_image, parent, false);
        vh = new ViewHolder(v, viewType);
        return vh;
    }

    @Override
    public void onBindViewHolder(final LatestFeedsAdapter.ViewHolder holder, int position) {
        LatestFeedsModel latestFeedsModel = new LatestFeedsModel();
       /* holder.txt_feed_heading.setText();
        holder.txt_creator.setText();
        holder.txt_created_time.setText();
        holder.txt_profile_count_login_user.setText();
        holder.txt_feed_body.setText();
        holder.txt_seen.setText();*/

        if (position == 0) {
            holder.img_preview.setVisibility(View.VISIBLE);
            holder.img_video_preview.setVisibility(View.VISIBLE);

            holder.img_preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertToastManager.showToast("Video Preview is not available", mActivity);
                }
            });
        } else if (position == 1) {
            holder.img_preview.setVisibility(View.VISIBLE);
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
            holder.img_video_preview.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_feed_heading, txt_creator, txt_created_time, txt_profile_count_login_user,
                txt_feed_body, txt_seen;
        ImageView img_preview, img_video_preview;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
          /*  if (viewType == 0) {
                txt_feed_heading = (TextView) itemView.findViewById(R.id.txt_feed_heading);
                txt_creator = (TextView) itemView.findViewById(R.id.txt_creator);
                txt_created_time = (TextView) itemView.findViewById(R.id.txt_created_time);
                txt_profile_count_login_user = (TextView) itemView.findViewById(R.id.txt_profile_count_login_user);
                txt_feed_body = (TextView) itemView.findViewById(R.id.txt_feed_body);
                txt_seen = (TextView) itemView.findViewById(R.id.txt_seen);
            } else if (viewType == 1) {*/
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


          /*  }*/
        }


        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mActivity, PostDetailActivity.class);
            mActivity.startActivity(intent);
        }
    }

    //............FullView imageView..............
    public void showImageDialog()
    {
        Display display =  mActivity.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        final Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.custom_image_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height);
        final TouchImageView custom_image = (TouchImageView)(dialog.findViewById(R.id.custom_image));
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

