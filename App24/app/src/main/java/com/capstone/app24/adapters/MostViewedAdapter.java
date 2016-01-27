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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.capstone.app24.R;
import com.capstone.app24.activities.MainActivity;
import com.capstone.app24.activities.PostDetailActivity;
import com.capstone.app24.activities.VideoActivity;
import com.capstone.app24.bean.LatestFeedsModel;
import com.capstone.app24.utils.APIsConstants;
import com.capstone.app24.utils.AppController;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.TouchImageView;
import com.capstone.app24.utils.Utils;

import org.parceler.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import volley.Request;
import volley.VolleyError;
import volley.VolleyLog;
import volley.toolbox.StringRequest;

/**
 * Created by amritpal on 4/11/15.
 */
public class MostViewedAdapter extends RecyclerView.Adapter<MostViewedAdapter.ViewHolder>
        implements View.OnClickListener {

    private static final String TAG = MostViewedAdapter.class.getSimpleName();
    private final int mImageHeight;
    private Activity mActivity;
    private LayoutInflater mInflater;
    Intent intent;
    List<LatestFeedsModel> mMostViewedFeedList = new ArrayList<>();
    private String res = "";
    private SweetAlertDialog mDialog;

    public MostViewedAdapter(Activity activity, List<LatestFeedsModel> mostViewedFeedList) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        mImageHeight = Utils.getHeight(mActivity);
        mMostViewedFeedList = mostViewedFeedList;
        //(LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_latest_feeds_with_image, null);
        ViewHolder viewHolder = new ViewHolder(view, mActivity);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MostViewedAdapter.ViewHolder holder, final int position) {
        LatestFeedsModel mostViewedModel = new LatestFeedsModel();
        mostViewedModel = mMostViewedFeedList.get(position);

        if (position == 0 && MainActivity.tabs.getVisibility() == View.VISIBLE) {
            holder.xtra_layout.setVisibility(View.VISIBLE);
        } else {
            holder.xtra_layout.setVisibility(View.GONE);
        }

        if (mostViewedModel.getType().equalsIgnoreCase(Constants.KEY_TEXT)) {
            holder.img_preview.setVisibility(View.GONE);
            holder.img_video_preview.setVisibility(View.GONE);
            holder.layout_img_video_preview.setVisibility(View.GONE);
            holder.progress_dialog.setVisibility(View.GONE);
        } else if (mostViewedModel.getType().equalsIgnoreCase(Constants.KEY_IMAGES)) {
            holder.img_preview.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout
                    .LayoutParams.MATCH_PARENT, (Utils.getHeight(mActivity) / 4) - 100); // (width, height)
            holder.img_preview.setLayoutParams(params);
            Glide.with(mActivity).load(mostViewedModel.getMedia()).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
                    .into(holder.img_preview);
            holder.img_video_preview.setVisibility(View.GONE);
            holder.layout_img_video_preview.setVisibility(View.VISIBLE);
        } else if (mostViewedModel.getType().equalsIgnoreCase(Constants.KEY_VIDEOS)) {
            holder.img_preview.setVisibility(View.VISIBLE);
            holder.img_video_preview.setVisibility(View.VISIBLE);
            holder.layout_img_video_preview.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout
                    .LayoutParams.MATCH_PARENT, (Utils.getHeight(mActivity) / 4) - 100); // (width, height)
            holder.img_preview.setLayoutParams(params);
            Glide.with(mActivity).load(mostViewedModel.getThumbnail()).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
                    .into(holder.img_preview);
            holder.layout_img_video_preview.setVisibility(View.VISIBLE);
        }
        holder.txt_feed_heading.setText(StringEscapeUtils.unescapeJava(mostViewedModel.getTitle()));
        holder.txt_feed_body.setText(StringEscapeUtils.unescapeJava(mostViewedModel
                .getDescription()));
        holder.txt_creator.setText(mostViewedModel.getUser_name());
        if (mostViewedModel.getProfit_amount().equalsIgnoreCase(Constants.ZERO))
            holder.txt_profile_count_login_user.setText(Constants.EMPTY);
        else
            holder.txt_profile_count_login_user.setText(mostViewedModel.getProfit_amount());
        holder.txt_created_time.setText(Utils.getTimeAgo(Long
                .parseLong(mostViewedModel.getCreated())));
        holder.txt_seen.setText(mostViewedModel.getViewcount());
        holder.img_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog(mMostViewedFeedList.get(position).getMedia());
            }
        });
        holder.img_video_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Utils(mActivity).setLatestFeedPreferences(mActivity, mMostViewedFeedList.get
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
                    new Utils(mActivity).setLatestFeedPreferences(mActivity, mMostViewedFeedList.get
                            (position));
                    Utils.debug(TAG, "layout_img_video_preview Clicked : Data of Latest Feed " +
                            "Model : " + new Utils(mActivity)
                            .getLatestFeedPreferences(mActivity));
                    intent = new Intent(mActivity, VideoActivity.class);
                    mActivity.startActivity(intent);
                } else if (holder.img_video_preview
                        .getVisibility() == View.GONE && holder.img_preview.getVisibility() == View.VISIBLE) {
                    showImageDialog(mMostViewedFeedList.get(position).getMedia());
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mMostViewedFeedList.size();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_preview:
                Intent intent = new Intent(mActivity, VideoActivity.class);
                mActivity.startActivity(intent);
                break;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_feed_heading, txt_creator, txt_created_time, txt_profile_count_login_user,
                txt_feed_body, txt_seen;
        //        private final VideoView video;
        private final FrameLayout progress_dialog;

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
            xtra_layout = (RelativeLayout) itemView.findViewById(R.id.xtra_layout);
            layout_img_video_preview = (RelativeLayout) itemView.findViewById(R.id.layout_img_video_preview);
            progress_dialog = (FrameLayout) itemView.findViewById(R.id.progress_dialog);

            // video = (VideoView) itemView.findViewById(R.id.video);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Utils(mActivity).setLatestFeedPreferences(mActivity, mMostViewedFeedList.get
                            (getLayoutPosition()));
                    Utils.debug(TAG, "Data of Latest Feed Model : " + new Utils(mActivity)
                            .getLatestFeedPreferences(mActivity));
                    makeSeenPostRequest(mMostViewedFeedList.get(getLayoutPosition()).getUser_id()
                            , mMostViewedFeedList.get(getLayoutPosition()).getId());
                    mActivity.finish();
                    intent = new Intent(mActivity, PostDetailActivity.class);
                    mActivity.startActivity(intent);
                }
            });
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
        }
    }

    private boolean makeSeenPostRequest(final String user_id, final String id) {
        mDialog = Utils.showSweetProgressDialog(mActivity,
                mActivity.getResources
                        ().getString(R.string.progress_loading), SweetAlertDialog.PROGRESS_TYPE);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_FEED_SEEN,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.debug(TAG, response.toString());
                        Utils.closeSweetProgressDialog(mActivity, mDialog);
                        res = response.toString();
                        try {
                            //  setFeedData(res);
                            Utils.debug("fb", "Now going to post on Facebook");
                            handleResponse(res);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeSweetProgressDialog(mActivity, mDialog);
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                res = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIsConstants.KEY_USER_ID, user_id);
                params.put(APIsConstants.KEY_FEED_ID, id);
                Utils.info("params...", params.toString());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }

    private void handleResponse(String res) {
        Utils.debug(TAG, "Response :  " + res);
//        {"result":true,"message":"One more View saved."}
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

