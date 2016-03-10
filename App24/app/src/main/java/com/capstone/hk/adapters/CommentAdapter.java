package com.capstone.hk.adapters;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.capstone.hk.R;
import com.capstone.hk.activities.CommentActivity;
import com.capstone.hk.bean.CommentModel;
import com.capstone.hk.utils.Constants;
import com.capstone.hk.utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by amritpal on 14/1/16.
 */
public class CommentAdapter extends BaseAdapter {
    private static final String TAG = CommentAdapter.class.getSimpleName();
    private List<CommentModel> mCommentList = new ArrayList<>();
    Activity mActivity;
    private LayoutInflater mInflater;

    public CommentAdapter(CommentActivity activity, List<CommentModel> commentList) {
        mActivity = activity;
        mCommentList = commentList;
        mInflater = LayoutInflater.from(activity);

    }

    @Override
    public int getCount() {
        return mCommentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_comment_items, null);
            holder = new ViewHolder();
            holder.txt_comment_creator = (TextView) convertView.findViewById(R.id.txt_comment_creator);
            holder.txt_comment_description = (TextView) convertView.findViewById(R.id.txt_comment_description);
            holder.comment_time = (TextView) convertView.findViewById(R.id.comment_time);
            holder.img_comment_creator_pic = (ImageView) convertView.findViewById(R.id.img_comment_creator_pic);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CommentModel commentModel = mCommentList.get(position);
        holder.txt_comment_creator.setText(commentModel.getName());
        holder.txt_comment_description.setText(commentModel.getMessage());

        String dateString = commentModel.getCreated_time();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.comment_time.setText(Utils.getTimeAgo(mActivity, date.getTime()));

        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String formatedDate = dateFormat.format(date);

        String pictureobj = "";
        try {
            URL image_large = new URL("https://graph.facebook.com/" + commentModel.getId()
                    + "/picture?type=large");
            pictureobj = image_large.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (pictureobj != null && !pictureobj.equalsIgnoreCase(Constants.EMPTY))
            Glide.with(mActivity).load(Uri.parse(pictureobj)).centerCrop().diskCacheStrategy
                    (DiskCacheStrategy.ALL).crossFade().into(holder.img_comment_creator_pic);


        return convertView;
    }

    static class ViewHolder {

        TextView txt_comment_creator, txt_comment_description, comment_time;
        ImageView img_comment_creator_pic;
    }
}
