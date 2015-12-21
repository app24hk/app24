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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.capstone.app24.R;
import com.capstone.app24.activities.PostDetailActivity;
import com.capstone.app24.activities.ProfileActivity;
import com.capstone.app24.activities.VideoActivity;
import com.capstone.app24.bean.UserFeedModel;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.TouchImageView;
import com.capstone.app24.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amritpal on 4/11/15.
 */
public class UserFeedAdapter extends RecyclerView.Adapter<UserFeedAdapter.ViewHolder> implements Filterable {

    private static final String TAG = UserFeedAdapter.class.getSimpleName();
    private final int mImageHeight;
    private Activity mActivity;
    private LayoutInflater mInflater;
    Intent intent;

    List<UserFeedModel> friendslist;
    List<UserFeedModel> friendslist_FilterList;
    private ValueFilter valueFilter;
    public int count = 9;

    public UserFeedAdapter(Activity activity, List<UserFeedModel> userFeedList) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        friendslist = userFeedList;
        friendslist_FilterList = userFeedList;
        mImageHeight = Utils.getHeight(mActivity);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_profit_and_feeds_with_image, null);
        ViewHolder viewHolder = new ViewHolder(view, mActivity);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserFeedAdapter.ViewHolder holder, final int position) {
        UserFeedModel userFeedModel = new UserFeedModel();
        userFeedModel = friendslist.get(position);

        holder.xtra_layout.setVisibility(View.GONE);
        holder.view_profit_and_feeds.setVisibility(View.GONE);

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
        holder.txt_created_time.setText(Utils.getTimeAgo(Long.parseLong(userFeedModel.getCreated
                ())));

        holder.img_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog(friendslist.get(position).getMedia());
            }
        });
        holder.img_video_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Utils(mActivity).setUserFeedPreferences(mActivity, friendslist.get
                        (position));
                intent = new Intent(mActivity, VideoActivity.class);
                mActivity.startActivity(intent);
            }
        });
        holder.layout_img_video_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.img_video_preview.getVisibility() == View.VISIBLE) {
                    new Utils(mActivity).setUserFeedPreferences(mActivity, friendslist.get
                            (position));
                    intent = new Intent(mActivity, VideoActivity.class);
                    mActivity.startActivity(intent);
                } else if (holder.img_video_preview
                        .getVisibility() == View.GONE && holder.img_preview.getVisibility() == View.VISIBLE) {
                    showImageDialog(friendslist.get(position).getMedia());
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return friendslist.size();
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
                    new Utils(mActivity).setUserFeedPreferences(mActivity, friendslist.get
                            (getLayoutPosition()));
//                    makeSeenPostRequest(friendslist.get(getLayoutPosition()).getUser_id(), friendslist.get(getLayoutPosition()).getId());
                    intent = new Intent(mActivity, PostDetailActivity.class);
                    //intent.putExtra("type", getLayoutPosition());
                    mActivity.startActivity(intent);
                }
            });

        }


    }

    ///
    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }


    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            // List<AllConversationBean> friendslist;
            // List<AllConversationBean> friendslist_FilterList;
            if (constraint != null && constraint.length() > 0) {
                ArrayList<UserFeedModel> filterList = new ArrayList<UserFeedModel>();
                for (int i = 0; i < friendslist_FilterList.size(); i++) {
                    switch (ProfileActivity.mSearchType) {
                        case USER:
                            if ((friendslist_FilterList.get(i).getUser_name().toUpperCase()).contains
                                    (constraint.toString().toUpperCase())) {
                                UserFeedModel names = (UserFeedModel) friendslist_FilterList
                                        .get(i);
                                filterList.add(names);
                            }
                            break;
                        case POST:
                            if ((friendslist_FilterList.get(i).getTitle().toUpperCase()).contains
                                    (constraint.toString().toUpperCase())) {
                                UserFeedModel names = (UserFeedModel) friendslist_FilterList
                                        .get(i);
                                filterList.add(names);
                            }
                            break;
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = friendslist_FilterList.size();
                results.values = friendslist_FilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      Filter.FilterResults results) {
            friendslist = (ArrayList<UserFeedModel>) results.values;
            notifyDataSetChanged();
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
