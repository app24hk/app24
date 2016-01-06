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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.capstone.app24.R;
import com.capstone.app24.activities.PostDetailActivity;
import com.capstone.app24.activities.VideoActivity;
import com.capstone.app24.bean.UserFeedModel;
import com.capstone.app24.utils.APIsConstants;
import com.capstone.app24.utils.AppController;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.TouchImageView;
import com.capstone.app24.utils.Utils;

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
public class UserProfitAdapter extends RecyclerView.Adapter<UserProfitAdapter.ViewHolder> {

    private static final String TAG = UserProfitAdapter.class.getSimpleName();
    private final int mImageHeight;
    private List<UserFeedModel> mUserFeedList = new ArrayList<>();
    private Activity mActivity;
    private LayoutInflater mInflater;
    Intent intent;
    private String res = "";
    private SweetAlertDialog mDialog;

    public UserProfitAdapter(Activity activity, List<UserFeedModel> userFeedList) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        mUserFeedList = userFeedList;
        mImageHeight = Utils.getHeight(mActivity);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_profit_and_feeds_with_image, null);
        ViewHolder viewHolder = new ViewHolder(view, mActivity);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserProfitAdapter.ViewHolder holder, final int position) {
        UserFeedModel userFeedModel = new UserFeedModel();
        userFeedModel = mUserFeedList.get(position);

        if (position == 0) {
            holder.xtra_layout.setVisibility(View.VISIBLE);
            holder.view_profit_and_feeds.setVisibility(View.VISIBLE);

        } else {
            holder.xtra_layout.setVisibility(View.GONE);
            holder.view_profit_and_feeds.setVisibility(View.GONE);

        }

        if (userFeedModel.getType().equalsIgnoreCase(Constants.KEY_TEXT)) {
            holder.img_preview.setVisibility(View.GONE);
            holder.img_video_preview.setVisibility(View.GONE);
            holder.layout_img_video_preview.setVisibility(View.GONE);
            holder.progress_dialog.setVisibility(View.GONE);
        } else if (userFeedModel.getType().equalsIgnoreCase(Constants.KEY_IMAGES)) {
            holder.img_preview.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout
                    .LayoutParams.MATCH_PARENT, (Utils.getHeight(mActivity) / 4) - 100); // (width, height)
            holder.img_preview.setLayoutParams(params);
            Glide.with(mActivity).load(userFeedModel.getMedia()).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
                    .into(holder.img_preview);
            holder.img_video_preview.setVisibility(View.GONE);
            holder.layout_img_video_preview.setVisibility(View.VISIBLE);
        } else if (userFeedModel.getType().equalsIgnoreCase(Constants.KEY_VIDEOS)) {
            holder.img_preview.setVisibility(View.VISIBLE);
            holder.img_video_preview.setVisibility(View.VISIBLE);
            holder.layout_img_video_preview.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout
                    .LayoutParams.MATCH_PARENT, (Utils.getHeight(mActivity) / 4) - 100); // (width, height)
            holder.img_preview.setLayoutParams(params);
            Glide.with(mActivity).load(userFeedModel.getThumbnail()).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
                    .into(holder.img_preview);
            holder.layout_img_video_preview.setVisibility(View.VISIBLE);
        }
        holder.txt_feed_heading.setText(userFeedModel.getTitle());
        holder.txt_feed_body.setText(userFeedModel.getDescription());
        holder.txt_creator.setText(userFeedModel.getUser_name());
        holder.txt_seen.setText(userFeedModel.getViewcount());
        if (userFeedModel.getProfit_amount().equalsIgnoreCase(Constants.ZERO))
            holder.txt_profile_count_login_user.setText(Constants.EMPTY);
        else
            holder.txt_profile_count_login_user.setText(userFeedModel.getProfit_amount());
        holder.txt_created_time.setText(Utils.getTimeAgo(Long.parseLong(userFeedModel.getCreated
                ())));
        holder.img_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog(mUserFeedList.get(position).getMedia());
            }
        });
        holder.img_video_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Utils(mActivity).setUserFeedPreferences(mActivity, mUserFeedList.get
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
                    new Utils(mActivity).setUserFeedPreferences(mActivity, mUserFeedList.get
                            (position));
                    Utils.debug(TAG, "layout_img_video_preview Clicked : Data of Latest Feed " +
                            "Model : " + new Utils(mActivity)
                            .getLatestFeedPreferences(mActivity));
                    intent = new Intent(mActivity, VideoActivity.class);
                    mActivity.startActivity(intent);
                } else if (holder.img_video_preview
                        .getVisibility() == View.GONE && holder.img_preview.getVisibility() == View.VISIBLE) {
                    showImageDialog(mUserFeedList.get(position).getMedia());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mUserFeedList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout progress_dialog;

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
            progress_dialog = (FrameLayout) itemView.findViewById(R.id.progress_dialog);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Utils(mActivity).setUserFeedPreferences(mActivity, mUserFeedList.get
                            (getLayoutPosition()));
                    Utils.debug(TAG, "Data of Latest Feed Model : " + new Utils(mActivity)
                            .getLatestFeedPreferences(mActivity));
                    makeSeenPostRequest(mUserFeedList.get(getLayoutPosition()).getUser_id(), mUserFeedList.get(getLayoutPosition()).getId());
                    mActivity.finish();
                    intent = new Intent(mActivity, PostDetailActivity.class);
                    mActivity.startActivity(intent);
                }
            });

        }


    } //............FullView imageView..............

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
                        try {
                            Utils.closeSweetProgressDialog(mActivity, mDialog);
                        } catch (Exception e) {
                        }
                        res = response.toString();
                        try {
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
    }
}

