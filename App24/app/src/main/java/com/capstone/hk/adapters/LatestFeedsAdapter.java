package com.capstone.hk.adapters;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.capstone.hk.R;
import com.capstone.hk.activities.MainActivity;
import com.capstone.hk.activities.PostDetailActivity;
import com.capstone.hk.activities.VideoActivity;
import com.capstone.hk.bean.LatestFeedsModel;
import com.capstone.hk.utils.Constants;
import com.capstone.hk.utils.TouchImageView;
import com.capstone.hk.utils.Utils;

import org.parceler.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amritpal on 4/11/15.
 */
public class LatestFeedsAdapter extends RecyclerView.Adapter<LatestFeedsAdapter.ViewHolder> {

    private static final String TAG = LatestFeedsAdapter.class.getSimpleName();
    private final int mImageHeight;
    private Activity mActivity;
    private LayoutInflater mInflater;
    Intent intent;
    List<LatestFeedsModel> mLatestFeedList = new ArrayList<>();
    private String res = "";
    private SweetAlertDialog mDialog;
    private int count;

    public LatestFeedsAdapter(Activity activity, List<LatestFeedsModel> latestFeedList) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        mLatestFeedList = latestFeedList;
        mImageHeight = Utils.getHeight(mActivity);
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
        latestFeedsModel = mLatestFeedList.get(position);

        if (position == 0 && MainActivity.tabs.getVisibility() == View.VISIBLE) {
            holder.xtra_layout.setVisibility(View.VISIBLE);
        } else {
            holder.xtra_layout.setVisibility(View.GONE);
        }


        if (latestFeedsModel.getType().equalsIgnoreCase(Constants.KEY_TEXT)) {
            holder.img_preview.setVisibility(View.GONE);
            holder.img_video_preview.setVisibility(View.GONE);
            holder.progress_dialog.setVisibility(View.GONE);
            holder.progress_dialog_layout.setVisibility(View.GONE);
            //    holder.layout_img_video_preview.setVisibility(View.GONE);
            //  holder.progress_dialog.setVisibility(View.GONE);
        } else if (latestFeedsModel.getType().equalsIgnoreCase(Constants.KEY_IMAGES)) {
            holder.img_preview.setVisibility(View.VISIBLE);
            holder.progress_dialog.setVisibility(View.VISIBLE);
            holder.progress_dialog_layout.setVisibility(View.VISIBLE);

//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
//                    .LayoutParams.MATCH_PARENT, (Utils.getHeight(mActivity) / 4) - 100); // (width, height)
//            holder.main_frame_layout.setLayoutParams(params);
            holder.img_video_preview.setVisibility(View.GONE);
            //    holder.layout_img_video_preview.setVisibility(View.VISIBLE);
            Utils.debug("image_url", "latestFeedsModel.getThumbnail()" + latestFeedsModel.getThumbnail());

            Glide.with(mActivity).load(latestFeedsModel.getMedia())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.img_preview);

        } else if (latestFeedsModel.getType().equalsIgnoreCase(Constants.KEY_VIDEOS)) {
            holder.img_preview.setVisibility(View.VISIBLE);
            holder.img_video_preview.setVisibility(View.VISIBLE);
            //  holder.layout_img_video_preview.setVisibility(View.VISIBLE);

            holder.progress_dialog.setVisibility(View.VISIBLE);
            holder.progress_dialog_layout.setVisibility(View.VISIBLE);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
//                    .LayoutParams.MATCH_PARENT, (Utils.getHeight(mActivity) / 4) - 100); // (width, height)
//            holder.main_frame_layout.setLayoutParams(params);
            // holder.layout_img_video_preview.setVisibility(View.VISIBLE);
            Glide.with(mActivity).load(latestFeedsModel
                    .getThumbnail())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.img_preview);
        }
        holder.txt_feed_heading.setText(StringEscapeUtils.unescapeJava(latestFeedsModel.getTitle()));
        holder.txt_feed_body.setText(StringEscapeUtils.unescapeJava(latestFeedsModel
                .getDescription()));


        holder.txt_creator.setText(latestFeedsModel.getUser_name());
        if (latestFeedsModel.getProfit_amount().equalsIgnoreCase(Constants.ZERO))
            holder.txt_profile_count_login_user.setText(Constants.EMPTY);
        else
            holder.txt_profile_count_login_user.setText(latestFeedsModel.getProfit_amount());
        holder.txt_seen.setText(latestFeedsModel.getViewcount());
        holder.txt_created_time.setText(Utils.getTimeAgo(mActivity, Long.parseLong(latestFeedsModel
                .getCreated())));
        holder.img_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.img_video_preview.getVisibility() == View.VISIBLE) {
                    new Utils(mActivity).setLatestFeedPreferences(mActivity, mLatestFeedList.get
                            (position));
                    intent = new Intent(mActivity, VideoActivity.class);
                    mActivity.startActivity(intent);
                } else if (holder.img_video_preview
                        .getVisibility() == View.GONE && holder.img_preview.getVisibility() == View.VISIBLE) {
                    showImageDialog(mLatestFeedList.get(position).getMedia());
                }
//                showImageDialog(mLatestFeedList.get(position).getMedia());
            }
        });
        holder.img_video_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Utils(mActivity).setLatestFeedPreferences(mActivity, mLatestFeedList.get
                        (position));
                intent = new Intent(mActivity, VideoActivity.class);
                mActivity.startActivity(intent);
            }
        });
       /* holder.layout_img_video_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.img_video_preview.getVisibility() == View.VISIBLE) {
                    new Utils(mActivity).setLatestFeedPreferences(mActivity, mLatestFeedList.get
                            (position));
                    intent = new Intent(mActivity, VideoActivity.class);
                    mActivity.startActivity(intent);
                } else if (holder.img_video_preview
                        .getVisibility() == View.GONE && holder.img_preview.getVisibility() == View.VISIBLE) {
                    showImageDialog(mLatestFeedList.get(position).getMedia());
                }
            }
        });*/


    }

    @Override
    public int getItemCount() {
        return /*mLatestFeedList.size()*/count;
    }

    public void setCount(int count) {
        this.count = count;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ProgressBar progress_dialog;
        //   private final FrameLayout progress_dialog;
        private LinearLayout layout_feed_body = null;
        //        private VideoView video = null;
        private final TextView txt_feed_heading, txt_creator, txt_created_time, txt_profile_count_login_user,
                txt_feed_body, txt_seen;
        private final ImageView img_preview, img_video_preview;
        private final RelativeLayout main_frame_layout, progress_dialog_layout,
                xtra_layout;

        public ViewHolder(final View itemView, Activity act) {
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
            // layout_img_video_preview = (RelativeLayout) itemView.findViewById(R.id
            //      .layout_img_video_preview);
            xtra_layout = (RelativeLayout) itemView.findViewById(R.id.xtra_layout);
            layout_feed_body = (LinearLayout) itemView.findViewById(R.id.layout_feed_body);
            progress_dialog = (ProgressBar) itemView.findViewById(R.id.progress_dialog);
            progress_dialog_layout = (RelativeLayout) itemView.findViewById(R.id.progress_dialog_layout);
            main_frame_layout = (RelativeLayout) itemView.findViewById(R.id.main_frame_layout);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Utils(mActivity).setLatestFeedPreferences(mActivity, mLatestFeedList.get
                            (getLayoutPosition()));
                    Utils.debug(TAG, "Data of Latest Feed Model : " + new Utils(mActivity)
                            .getLatestFeedPreferences(mActivity));
                    mActivity.finish();
                    intent = new Intent(mActivity, PostDetailActivity.class);
                    intent.putExtra(Constants.IS_FROM_LIST, true);
                    mActivity.startActivity(intent);
                }
            });
        }


    }

    //............FullView imageView..............
    public void showImageDialog(String media) {
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

        Glide.with(mActivity).load(media)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(custom_image);
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

    private void handleResponse(String res) {
        Utils.debug(TAG, "Response :  " + res);
    }
}

