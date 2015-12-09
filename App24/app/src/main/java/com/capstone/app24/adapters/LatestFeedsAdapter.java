package com.capstone.app24.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.capstone.app24.R;
import com.capstone.app24.activities.MainActivity;
import com.capstone.app24.activities.PostDetailActivity;
import com.capstone.app24.activities.VideoActivity;
import com.capstone.app24.bean.LatestFeedsModel;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.TouchImageView;
import com.capstone.app24.utils.Utils;

import java.util.ArrayList;
import java.util.List;

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
//        Utils.debug(TAG, latestFeedsModel.getUser_name());
//        Utils.debug(TAG, latestFeedsModel.getTitle());
//        Utils.debug(TAG, latestFeedsModel.getDescription());
//        Utils.debug(TAG, latestFeedsModel.getId());
//        Utils.debug(TAG, latestFeedsModel.getMedia());
//        Utils.debug(TAG, latestFeedsModel.getModified());
//        Utils.debug(TAG, latestFeedsModel.getThumbnail());
//        Utils.debug(TAG, latestFeedsModel.getUser_id());
//        Utils.debug(TAG, latestFeedsModel.getViewcount());
//        Utils.debug(TAG, latestFeedsModel.getType());

        if (position == 0 && MainActivity.tabs.getVisibility() == View.VISIBLE) {
            holder.xtra_layout.setVisibility(View.VISIBLE);
        } else {
            holder.xtra_layout.setVisibility(View.GONE);
        }


        if (latestFeedsModel.getType().equalsIgnoreCase(Constants.KEY_TEXT)) {
            holder.img_preview.setVisibility(View.GONE);
            holder.img_video_preview.setVisibility(View.GONE);
            holder.layout_img_video_preview.setVisibility(View.GONE);
            holder.progress_dialog.setVisibility(View.GONE);
        } else if (latestFeedsModel.getType().equalsIgnoreCase(Constants.KEY_IMAGES)) {
            holder.img_preview.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout
                    .LayoutParams.MATCH_PARENT, (Utils.getHeight(mActivity) / 4) - 100); // (width, height)
            holder.img_preview.setLayoutParams(params);
            Glide.with(mActivity).load(latestFeedsModel.getMedia()).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
                    .into(holder.img_preview);
            holder.img_video_preview.setVisibility(View.GONE);
            holder.layout_img_video_preview.setVisibility(View.VISIBLE);
        } else if (latestFeedsModel.getType().equalsIgnoreCase(Constants.KEY_VIDEOS)) {
            holder.img_preview.setVisibility(View.VISIBLE);
            holder.img_video_preview.setVisibility(View.VISIBLE);
            holder.layout_img_video_preview.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout
                    .LayoutParams.MATCH_PARENT, (Utils.getHeight(mActivity) / 4) - 100); // (width, height)
            holder.img_preview.setLayoutParams(params);
            Glide.with(mActivity).load(latestFeedsModel.getThumbnail()).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
                    .into(holder.img_preview);
            holder.layout_img_video_preview.setVisibility(View.VISIBLE);
        }
        holder.txt_feed_heading.setText(latestFeedsModel.getTitle());
        holder.txt_feed_body.setText(latestFeedsModel.getDescription());
        holder.txt_creator.setText(latestFeedsModel.getUser_name());
        holder.txt_seen.setText(latestFeedsModel.getViewcount());
        holder.txt_created_time.setText(latestFeedsModel.getModified());
//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Utils(mActivity).setLatestFeedPreferences(mActivity, mLatestFeedList.get
//                        (itemView.get));
//                intent = new Intent(mActivity, PostDetailActivity.class);
//                intent.putExtra("type", getLayoutPosition());
//                mActivity.startActivity(intent);
//            }
//        });

        holder.img_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog(mLatestFeedList.get(position).getMedia());
            }
        });
        holder.img_video_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Utils(mActivity).setLatestFeedPreferences(mActivity, mLatestFeedList.get
                        (position));
                Utils.debug(TAG, "img_video_preview Clicked : Data of Latest Feed " +
                        "Model : " + new Utils(mActivity)
                        .getLatestFeedPreferences(mActivity));
                intent = new Intent(mActivity, VideoActivity.class);
                mActivity.startActivity(intent);
            }
        });
        holder.layout_img_video_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.img_video_preview.getVisibility() == View.VISIBLE) {
                    new Utils(mActivity).setLatestFeedPreferences(mActivity, mLatestFeedList.get
                            (position));
                    Utils.debug(TAG, "layout_img_video_preview Clicked : Data of Latest Feed " +
                            "Model : " + new Utils(mActivity)
                            .getLatestFeedPreferences(mActivity));
                    intent = new Intent(mActivity, VideoActivity.class);
                    mActivity.startActivity(intent);
                } else if (holder.img_video_preview
                        .getVisibility() == View.GONE && holder.img_preview.getVisibility() == View.VISIBLE) {
                    showImageDialog(mLatestFeedList.get(position).getMedia());
                }
            }
        });


//        if (position % 3 == 0) {
//            holder.img_preview.setVisibility(View.VISIBLE);
//            holder.img_video_preview.setVisibility(View.VISIBLE);
//            holder.layout_img_video_preview.setVisibility(View.VISIBLE);
//            holder.txt_feed_body.setText(mActivity.getResources().getString(R.string.chinese_lorem_ipsum));
//            Uri videoURI = Uri.parse("android.resource://" + mActivity.getPackageName() + "/"
//                    + R.raw.itcuties);
//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//            retriever.setDataSource(mActivity, videoURI);
//            Bitmap bitmap = retriever
//                    .getFrameAtTime(10, MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);
//            Drawable drawable = new BitmapDrawable(mActivity.getResources(), bitmap);
//            holder.img_preview.setImageDrawable(drawable);
//        } else if (position % 3 == 1) {
//            holder.img_preview.setVisibility(View.VISIBLE);
//            holder.img_video_preview.setVisibility(View.GONE);
//            holder.txt_feed_body.setText(mActivity.getResources().getString(R.string.lorem_ipsum));
//            holder.img_preview.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.pic_two));
//        } else {
//            holder.img_preview.setVisibility(View.GONE);
//            holder.img_video_preview.setVisibility(View.GONE);
//            holder.layout_img_video_preview.setVisibility(View.GONE);
//            holder.txt_feed_body.setText(mActivity.getResources().getString(R.string.lorem_ipsum));
//
//        }
//        String UrlPath = "android.resource://" + mActivity.getPackageName() + "/" + R.raw.itcuties;
//        Utils.debug(TAG, "Url Path > > > " + UrlPath);            //new MediaMetadataRetriever()
//        // .getFrameAtTime()
//        Bitmap thumb = ThumbnailUtils.createVideoThumbnail("http://download.itcuties.com/teaser/itcuties-teaser-480.mp4", MediaStore.Images.Thumbnails
//                .MICRO_KIND);
//        holder.img_preview.setImageBitmap(thumb);

//        ContentResolver crThumb = mActivity.getContentResolver();
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 1;
//        Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb, R.raw.itcuties,
//                MediaStore.Video.Thumbnails.MINI_KIND, options);
//        holder.img_preview.setImageBitmap(curThumb);

    }

    @Override
    public int getItemCount() {
        return mLatestFeedList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final FrameLayout progress_dialog;
        private LinearLayout layout_feed_body = null;
        //        private VideoView video = null;
        TextView txt_feed_heading, txt_creator, txt_created_time, txt_profile_count_login_user,
                txt_feed_body, txt_seen;
        ImageView img_preview, img_video_preview;
        RelativeLayout layout_img_video_preview, xtra_layout;

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
            layout_img_video_preview = (RelativeLayout) itemView.findViewById(R.id.layout_img_video_preview);
            xtra_layout = (RelativeLayout) itemView.findViewById(R.id.xtra_layout);
            layout_feed_body = (LinearLayout) itemView.findViewById(R.id.layout_feed_body);
            progress_dialog = (FrameLayout) itemView.findViewById(R.id.progress_dialog);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Utils(mActivity).setLatestFeedPreferences(mActivity, mLatestFeedList.get
                            (getLayoutPosition()));
                    Utils.debug(TAG, "Data of Latest Feed Model : " + new Utils(mActivity)
                            .getLatestFeedPreferences(mActivity));
                    intent = new Intent(mActivity, PostDetailActivity.class);
                    //intent.putExtra("type", getLayoutPosition());
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
                .diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
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

}

