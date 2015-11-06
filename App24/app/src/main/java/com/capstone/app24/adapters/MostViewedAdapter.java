package com.capstone.app24.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.app24.R;
import com.capstone.app24.activities.GalleryActivity;
import com.capstone.app24.activities.PostDetailActivity;
import com.capstone.app24.bean.LatestFeedsModel;
import com.capstone.app24.utils.AlertToastManager;
import com.capstone.app24.utils.Utils;

/**
 * Created by amritpal on 4/11/15.
 */
public class MostViewedAdapter extends RecyclerView.Adapter<MostViewedAdapter.ViewHolder> {

    private static final String TAG = MostViewedAdapter.class.getSimpleName();
    private Activity mActivity;
    //  private LayoutInflater mInflater;

    public MostViewedAdapter(Activity activity) {
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
    public void onBindViewHolder(final MostViewedAdapter.ViewHolder holder, int position) {
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
            AlertToastManager.showToast("Video Preview is not available", mActivity);

            /*holder.img_preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, GalleryActivity.class);
                    mActivity.startActivity(intent);
                }
            });*/
        } else if (position == 1) {
            holder.img_preview.setVisibility(View.VISIBLE);
            holder.img_preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, GalleryActivity.class);
                    mActivity.startActivity(intent);
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
        }
    }
}

