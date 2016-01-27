package com.capstone.app24.activities;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.util.Util;
import com.capstone.app24.R;
import com.capstone.app24.bean.LatestFeedsModel;
import com.capstone.app24.bean.OwnerDataModel;
import com.capstone.app24.utils.APIsConstants;
import com.capstone.app24.utils.AlertToastManager;
import com.capstone.app24.utils.AppController;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.CustomDialog;
import com.capstone.app24.utils.FacebookUtils;
import com.capstone.app24.utils.InterfaceListener;
import com.capstone.app24.utils.NetworkUtils;
import com.capstone.app24.utils.Session;
import com.capstone.app24.utils.TouchImageView;
import com.capstone.app24.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Post;
import com.sromku.simple.fb.listeners.OnPublishListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
public class PostDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = PostDetailActivity.class.getSimpleName();
    private RelativeLayout menu_layout;
    private RelativeLayout edit_menu;
    private TextView txt_edit, txt_delete, txt_post_title, txt_description, txt_seen, txt_creator, txt_created_time;
    private int type;
    private RelativeLayout layout_media_preview;
    private ImageView img_preview, img_video_preview;
    private RelativeLayout layout_img_video_preview;
    private Intent intent;
    //private LikeView likeView;
    LatestFeedsModel latestFeedsModel;
    /* Volley Request Tags */
    private String res = "";
    List<LatestFeedsModel> latestFeedList = new ArrayList<>();
    /* End of Volley Request Tags */
    private SweetAlertDialog mDialog;
    private OwnerDataModel mOwnerDataModel;
    //    ShareButton shareButton;
    private boolean isOwner;
    private String profitAmount;
    private int likeCount = 0;
    private TextView txt_like_count;
    private LikeView likeView;
    public static TextView txt_comment;
    public static int commentCount;
    private ShareButton shareButton;
    private CallbackManager callbackManager;
    private String mUrl;
    private SimpleFacebook mSimpleFacebook;
    private RelativeLayout comment;
    private String mPostId = null;
    private boolean isFeedLiked;
    private Button like;
    private ShareLinkContent linkContent;
    private String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        setContentView(R.layout.activity_post_detail);
        callbackManager = CallbackManager.Factory.create();
        latestFeedsModel = new Utils(this).getLatestFeedPreferences(this);
        setHeader(null, true, true, false, false, false, null);
//        getFeedOwnerData();
        AccessToken.refreshCurrentAccessTokenAsync();

        type = getIntent().getIntExtra("type", 0);
        initializeViews();
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        setClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
    }

    private boolean getFeedOwnerData() {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_FEED_DATA,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
                        res = response.toString();
                        try {
                            makeOwnerDataModel(res);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                res = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
//                        user_id(int)
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIsConstants.KEY_FEED_ID, latestFeedsModel.getId());
                params.put(APIsConstants.KEY_USER_ID, new Utils(PostDetailActivity.this)
                        .getSharedPreferences
                                (PostDetailActivity.this, Constants.KEY_USER_DETAILS, ""));
                Utils.info("params...", params.toString());
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }


    private void updateUI() {

//        latestFeedsModel = new Utils(this).getLatestFeedPreferences(this);
        if (latestFeedsModel != null) {
            if (latestFeedsModel.getType().equalsIgnoreCase(Constants.KEY_TEXT)) {
                layout_media_preview.setVisibility(View.GONE);
                img_preview.setVisibility(View.GONE);
                img_video_preview.setVisibility(View.GONE);
                layout_img_video_preview.setVisibility(View.GONE);

            }
            if (latestFeedsModel.getType().equalsIgnoreCase(Constants.KEY_IMAGES)) {
                img_preview.setVisibility(View.VISIBLE);
                layout_media_preview.setVisibility(View.VISIBLE);
                img_video_preview.setVisibility(View.GONE);
                layout_img_video_preview.setVisibility(View.GONE);
                img_preview.setOnClickListener(this);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout
                        .LayoutParams.MATCH_PARENT, (Utils.getHeight(this) / 2) - 100); // (width,
                // height)
                img_preview.setLayoutParams(params);
                Glide.with(this).load(latestFeedsModel.getMedia()).centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
                        .into(img_preview);
            }
            if (latestFeedsModel.getType().equalsIgnoreCase(Constants.KEY_VIDEOS)) {


                img_preview.setVisibility(View.VISIBLE);
                img_video_preview.setVisibility(View.VISIBLE);
                layout_media_preview.setVisibility(View.VISIBLE);
                layout_img_video_preview.setVisibility(View.VISIBLE);
                img_video_preview.setOnClickListener(this);
                img_preview.setOnClickListener(this);

//                Uri videoURI = Uri.parse("android.resource://" + getPackageName() + "/"
//                        + R.raw.itcuties);
//                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                retriever.setDataSource(this, videoURI);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout
                        .LayoutParams.MATCH_PARENT, (Utils.getHeight(this) / 2) - 100); // (width,
                // height)
                img_preview.setLayoutParams(params);
                Glide.with(this).load(latestFeedsModel.getThumbnail()).centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
                        .into(img_preview);
            }
            txt_post_title.setText(StringEscapeUtils.unescapeJava(latestFeedsModel.getTitle()));
            txt_description.setText(StringEscapeUtils.unescapeJava(latestFeedsModel
                    .getDescription()));
            txt_seen.setText(latestFeedsModel.getViewcount());
            txt_creator.setText(latestFeedsModel.getUser_name());
            txt_created_time.setText(Utils.getTimeAgo(Long.parseLong(latestFeedsModel.getCreated
                    ())));
            profitAmount = latestFeedsModel.getProfit_amount();
            if (profitAmount.equalsIgnoreCase(Constants.ZERO)) {
                setHeader(Constants.EMPTY, true, true, false, false, isOwner, null);
            } else {
                setHeader(profitAmount, true, true, false, false, isOwner, null);
            }
            if (latestFeedsModel.getFb_feed_id() != null && !latestFeedsModel.getFb_feed_id()
                    .equalsIgnoreCase(Constants.EMPTY)) {
                getLikes(latestFeedsModel.getFb_feed_id());
                getComments(latestFeedsModel.getFb_feed_id());
                setSharedContent(latestFeedsModel.getFb_feed_id());
                mPostId = latestFeedsModel.getFb_feed_id();
            }

            makeSeenPostRequest(latestFeedsModel.getUser_id(), latestFeedsModel.getId());

        }

    }


    private void getComments(final String postId) {
        /* make the API call */
        Utils.debug("accessToken", "AccessToken.getCurrentAccessToken() : " + AccessToken.getCurrentAccessToken());
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + postId + "/comments",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */

                        Utils.debug(Constants.FACEBOOK, response.toString());

                        JSONObject graphResponse = response.getJSONObject();
                        Utils.debug(TAG, "graphResponse : " + graphResponse);
                        try {
                            JSONArray jsonArray = graphResponse.getJSONArray(Constants.KEY_DATA);
                            Utils.debug(TAG, "jsonArray : " + jsonArray);
                            commentCount = jsonArray.length();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        txt_comment.setText(commentCount + "");
                    }
                }
        ).executeAsync();
    }


    /**
     * this method is used to get the like counts
     *
     * @param postId
     */
    private void getLikes(String postId) {
        //Post id  : : : : : 130220790685225
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Utils.debug(TAG, "AccessToken.getCurrentAccessToken().toString() : " + AccessToken.getCurrentAccessToken().toString());
        Utils.debug(TAG, "AccessToken.getCurrentAccessToken().toString() : " + AccessToken
                .getCurrentAccessToken().getToken());
        //AccessTokenManager.getInstance().getCurrentAccessToken()
        if (postId != null) {
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/"/*130220790685225*/ + postId + "/likes",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
            /* handle the result */
                            Utils.debug(Constants.FACEBOOK, response.toString());

                            JSONObject graphResponse = response.getJSONObject();
                            Utils.debug(TAG, "graphResponse : " + graphResponse);
                            try {
                                JSONArray jsonArray = graphResponse.getJSONArray(Constants.KEY_DATA);
                                Utils.debug(TAG, "jsonArray : " + jsonArray);
                                Utils.debug(TAG, "Current Person : " + new Utils
                                        (PostDetailActivity.this)
                                        .getSharedPreferences(PostDetailActivity.this,
                                                Constants.KEY_FACEBOOK_ID, ""));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject likedPerson = jsonArray.getJSONObject(i);
                                    Utils.debug(TAG, "likedPerson : " + likedPerson);


                                    String likedId = likedPerson.getString("id");
                                    if (likedId.equalsIgnoreCase(new Utils(PostDetailActivity.this)
                                            .getSharedPreferences(PostDetailActivity.this,
                                                    Constants.KEY_FACEBOOK_ID, ""))) {
                                        isFeedLiked = true;
                                        like.setText("Liked");
                                        break;
                                    } else {
                                        isFeedLiked = false;
                                        like.setText("Like");
                                    }
                                }
                                likeCount = jsonArray.length();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            txt_like_count.setText(likeCount + "");
                        }
                    }
            ).executeAsync();
        } else {
            txt_like_count.setText(likeCount + "");
        }
    }

    private void initializeViews() {
        menu_layout = (RelativeLayout) findViewById(R.id.menu_layout);
        edit_menu = (RelativeLayout) findViewById(R.id.edit_menu);
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        txt_edit = (TextView) findViewById(R.id.txt_edit);
        txt_delete = (TextView) findViewById(R.id.txt_delete);
        img_preview = (ImageView) findViewById(R.id.img_preview);
        img_video_preview = (ImageView) findViewById(R.id.img_video_preview);
        layout_media_preview = (RelativeLayout) findViewById(R.id.layout_media_preview);
        layout_img_video_preview = (RelativeLayout) findViewById(R.id.layout_img_video_preview);
        txt_comment = (TextView) findViewById(R.id.txt_comment);
        comment = (RelativeLayout) findViewById(R.id.comment);
        txt_post_title = (TextView) findViewById(R.id.txt_post_title);
        txt_description = (TextView) findViewById(R.id.txt_description);
        txt_seen = (TextView) findViewById(R.id.txt_seen);
        txt_like_count = (TextView) findViewById(R.id.like_count);
        txt_created_time = (TextView) findViewById(R.id.txt_created_time);
        txt_creator = (TextView) findViewById(R.id.txt_creator);
        txt_creator.setSelected(true);  // Set focus to the textview
        txt_creator.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txt_created_time.setSelected(true);  // Set focus to the textview
        txt_created_time.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        like = (Button) findViewById(R.id.like);
        like.setOnClickListener(this);
        //LikeView Facebook
        likeView = (LikeView) findViewById(R.id.like_view);
        likeView.setAuxiliaryViewPosition(LikeView.AuxiliaryViewPosition.INLINE);
        s = FacebookUtils.getFeedUrl(latestFeedsModel.getFb_feed_id());
        Utils.debug(TAG, "Open Graph URL" + s);
        Utils.debug(TAG, "Open Graph URL" + FacebookUtils.mUrl);
        likeView.setObjectIdAndType(FacebookUtils.mUrl, LikeView.ObjectType.OPEN_GRAPH);
    }

    private void unLike() {
        if (latestFeedsModel.getFb_feed_id() != null) {
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/"/*130220790685225*/ + latestFeedsModel.getFb_feed_id() + "/likes",
                    null,
                    HttpMethod.DELETE,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
            /* handle the result */
                            try {
                                Utils.debug(Constants.FACEBOOK, response.toString());
                                JSONObject graphResponse = response.getJSONObject();
                                Utils.debug(TAG, "graphResponse : " + graphResponse);
                                if (graphResponse != null) {
                                    boolean b = graphResponse.getBoolean("success");
                                    Utils.debug("boolean : ", b + "");
                                    if (b) {
                                        isFeedLiked = false;
                                        like.setText("Like");
                                        Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
                                        getLikes(latestFeedsModel.getFb_feed_id());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);

                        }
                    }
            ).executeAsync();
        }
    }

    private void like() {
        //Post id  : : : : : 130220790685225
        if (latestFeedsModel.getFb_feed_id() != null) {
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/"/*130220790685225*/ + latestFeedsModel.getFb_feed_id() + "/likes",
                    null,
                    HttpMethod.POST,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
            /* handle the result */
                            try {
                                Utils.debug(Constants.FACEBOOK, response.toString());
                                JSONObject graphResponse = response.getJSONObject();
                                Utils.debug(TAG, "graphResponse : " + graphResponse);
                                if (graphResponse != null) {
                                    boolean b = graphResponse.getBoolean("success");
                                    Utils.debug("boolean : ", b + "");
                                    Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
                                    if (b) {
                                        isFeedLiked = true;
                                        like.setText("Liked");
                                        getLikes(latestFeedsModel.getFb_feed_id());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);

                        }
                    }
            ).executeAsync();
        }
    }

    private void setClickListeners() {
        ibtn_share.setOnClickListener(this);
        ibtn_dots.setOnClickListener(this);
        ibtn_back.setOnClickListener(this);
        txt_edit.setOnClickListener(this);
        txt_delete.setOnClickListener(this);
        img_video_preview.setOnClickListener(this);
        layout_img_video_preview.setOnClickListener(this);
        comment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_share:
                mDialog = Utils.showSweetProgressDialog(PostDetailActivity.this, getResources()
                        .getString(R.string.please_wait), SweetAlertDialog.PROGRESS_TYPE);
                getSharedUrl(v);
//                shareDialog = new ShareDialog(this);
//                MessageDialog.show(PostDetailActivity.this, linkContent);
//                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
//                    @Override
//                    public void onSuccess(Sharer.Result result) {
//                        if (result != null) {
//                            Utils.debug(TAG, "result.getPostId() : " + result.getPostId());
//                        } else {
//                            Utils.debug(TAG, "result.getPostId() : NULL");
//                        }
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//
//                    @Override
//                    public void onError(FacebookException error) {
//
//                    }
//                });


//                Feed feed = new Feed.Builder()
//                        .setName(latestFeedsModel.getTitle())
//                        .setDescription(latestFeedsModel.getDescription())
//                        .setPicture(latestFeedsModel.getMedia())
//                        .setLink(mUrl)
//                        .build();
//                mSimpleFacebook.publish(feed, onPublishListener);

                //                if (ShareDialog.canShow(ShareLinkContent.class)) {
//                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                            .setContentTitle(latestFeedsModel.getTitle())
//                            .setContentDescription(latestFeedsModel.getDescription())
//                            .setContentUrl(Uri.parse(mUrl))
//                            .build();
//
//                    shareDialog.show(linkContent);
//                    shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
//                        @Override
//                        public void onSuccess(Sharer.Result result) {
//                            Utils.debug(TAG, "Sharer.Result : " + result + "");
//                        }
//
//                        @Override
//                        public void onCancel() {
//
//                        }
//
//                        @Override
//                        public void onError(FacebookException error) {
//                            Utils.debug(TAG, "FacebookException : " + error);
//                        }
//                    });
//                }


                break;
            case R.id.like:
                mDialog = Utils.showSweetProgressDialog(PostDetailActivity.this, getResources()
                        .getString(R
                                .string.please_wait), SweetAlertDialog.PROGRESS_TYPE);
                if (isFeedLiked) {
                    unLike();
                } else {
                    like();
                }
                break;
            case R.id.ibtn_dots:

                if (edit_menu.getVisibility() == View.VISIBLE) {
                    edit_menu.setVisibility(View.GONE);
                    Animation fadeoutAnim = AnimationUtils.loadAnimation(this, R.anim.fadeout);
                    edit_menu.startAnimation(fadeoutAnim);
                } else {
                    edit_menu.setVisibility(View.VISIBLE);
                    Animation fadeinAnim = AnimationUtils.loadAnimation(this, R.anim.fadein);
                    edit_menu.startAnimation(fadeinAnim);
                }
                break;
            case R.id.ibtn_back:
                intent = new Intent(PostDetailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.txt_edit:
                edit_menu.setVisibility(View.GONE);
                AlertToastManager.showToast("Edit", this);
                intent = new Intent(PostDetailActivity.this, EditPostActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_delete:
                edit_menu.setVisibility(View.GONE);
                AlertToastManager.showToast("Delete", this);
                if (NetworkUtils.isOnline(this)) {
                    deleteFeed();
                } else {
                    Utils.showSweetProgressDialog(this, getResources().getString(R.string
                            .check_your_internet_connection), SweetAlertDialog.WARNING_TYPE);
                }
                break;
            case R.id.img_preview:
                showImageDialog(latestFeedsModel.getMedia());
                break;
            case R.id.img_video_preview:
                intent = new Intent(PostDetailActivity.this, VideoActivity.class);
                startActivity(intent);
                edit_menu.setVisibility(View.GONE);
                //AlertToastManager.showToast("Video Preview is not available", this);
                break;
            case R.id.layout_img_video_preview:
                intent = new Intent(PostDetailActivity.this, VideoActivity.class);
                startActivity(intent);
                break;
            case R.id.comment:
                if (latestFeedsModel.getFb_feed_id() != null && !latestFeedsModel.getFb_feed_id()
                        .equalsIgnoreCase(Constants.EMPTY) && mPostId != null) {
//                    CustomDialog customDialog = new CustomDialog(this, this, Constants
//                            .FB_COMMENT_DIALOG, "");
//                    customDialog.setFacebookCommentDetails(mPostId);
//                    customDialog.show();
                    Intent intent = new Intent(PostDetailActivity.this, CommentActivity.class);
                    intent.putExtra(Constants.POST_ID, latestFeedsModel.getFb_feed_id());
                    startActivity(intent);
                } else
                    AlertToastManager.showToast("Comment Section is under Development", this);
                break;
        }
    }

    private void getSharedUrl(final View v) {
        //String url = FacebookUtils.getFeedUrl(latestFeedsModel.getFb_feed_id());
        // Utils.debug(TAG, "url : ###" + url);
//                shareFeed();
        //  onShareClick(v);

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + latestFeedsModel.getFb_feed_id(),
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                        JSONObject object = response.getJSONObject();

                        if (object == null) {
                            Utils.closeSweetProgressDialog(PostDetailActivity.this,
                                    mDialog);
                            AlertToastManager.showToast("This Feed can't be shared",
                                    PostDetailActivity.this);
                        }

                        JSONObject jsonObject = null;
                        if (object != null) {
                            try {
                                jsonObject = object.getJSONObject(Constants.KEY_DATA);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
                            }
                        }
                        JSONObject urlObject1 = null;
                        if (jsonObject != null) {
                            try {
                                urlObject1 = jsonObject.getJSONObject(Constants.POST);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
                            }
                        }
                        if (urlObject1 != null) {
                            try {
                                String url = urlObject1.getString(Constants.KEY_URL);
                                Utils.debug(Constants.FACEBOOK, "url : " + url);
                                mUrl = url;
                                linkContent = new ShareLinkContent.Builder()
                                        .setContentTitle(latestFeedsModel.getTitle())
                                        .setContentDescription(latestFeedsModel.getDescription())
                                        .setContentUrl(Uri.parse(mUrl))
                                        .build();
                                //onShareClick(v);
                                //   shareIt(linkContent);
                                shareFeed();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
                            }
                        }
                    }
                }
        ).executeAsync();
    }

    private void share() {
        getFeedOwnerData();
        Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
        String urlToShare = mUrl /*"http://stackoverflow.com/questions/7545254"*//*"http://trigma.com/"*/;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, urlToShare);
        startActivity(sendIntent);

    }

    private void shareIt(ShareLinkContent linkContent) {
        ShareApi.share(linkContent, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                if (result != null) {
                    Utils.debug(TAG, "ShareApi result.getPostId() : " + result.getPostId());
                    Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
                    mDialog = Utils.showSweetProgressDialog(PostDetailActivity.this, "Shared " +
                                    "Successfully",
                            SweetAlertDialog.SUCCESS_TYPE);
                    mDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            finish();
                            intent = new Intent(PostDetailActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    Utils.debug(TAG, "ShareApi result.getPostId() : NULL");
                    Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
                }

            }

            @Override
            public void onCancel() {
                Utils.debug(TAG, "ShareApi result.getPostId() : NULLLLLL");
                Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);

            }

            @Override
            public void onError(FacebookException error) {
                Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
                mDialog = Utils.showSweetProgressDialog(PostDetailActivity.this, "Error Occurred",
                        SweetAlertDialog.SUCCESS_TYPE);
                mDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                        intent = new Intent(PostDetailActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private boolean shareFeed() {
//        mDialog = Utils.showSweetProgressDialog(PostDetailActivity.this, getResources().getString(R
//                .string.sharing_feed), SweetAlertDialog.PROGRESS_TYPE);


        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_ADD_SHARE_USER,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
                        Utils.debug(TAG, response.toString());
                        res = response.toString();
                        try {
                            handleShareResponse(res);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                res = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
//                        user_id(int)
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIsConstants.KEY_FEED_ID, latestFeedsModel.getId());
                params.put(APIsConstants.KEY_USER_ID, new Utils(PostDetailActivity.this)
                        .getSharedPreferences
                                (PostDetailActivity.this, Constants.KEY_USER_DETAILS, ""));
                Utils.info("params...", params.toString());
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }

    private void handleShareResponse(String res) {
        Utils.debug(TAG, "Response from AddSharerUser Web Service : " + res);
        Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
        share();

    }

//    private void shareOnFacebook() {
//
//
//        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
//                .putString("og:type", "Create a Post")//613292//
//                .putString("og:title", latestFeedsModel.getTitle())
//                .putString("og:description", latestFeedsModel.getDescription())
//                .build();
//        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
//                .setActionType("capstone_app:create")
//                .putObject("capstone_app:post", object)
//                .build();
//        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
//                .setPreviewPropertyName("capstone_app:post")
//                .setAction(action)
//                .build();
//
//        shareButton = (ShareButton) findViewById(R.id.shareButton);
//        shareButton.setShareContent(content);
//        shareButton.performClick();
//        shareButton.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
//            @Override
//            public void onSuccess(Sharer.Result result) {
//                Utils.debug(TAG, "post Id : " + result.getPostId());
//                //getLikes(result.getPostId());
//                if (!mFeedId.equalsIgnoreCase(Constants.EMPTY) && latestFeedsModel.getFb_feed_id
//                        () != null) {
//                    //updateFacebookFeedId(result.getPostId());
//                }
//            }
//
//            @Override
//            public void onCancel() {
//                Utils.debug(TAG, "Cancelled");
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Utils.debug(TAG, "error : " + error);
//            }
//        });
//    }


    OnPublishListener onPublishListener = new OnPublishListener() {
        @Override
        public void onComplete(String postId) {
            Utils.info(TAG, "Published successfully. The new post id = " + postId);
        }

           /*
            * You can override other methods here:
            * onThinking(), onFail(String reason), onException(Throwable throwable)
            */
    };


    private boolean deleteFeed() {

        /*
         Starting a progress Dialog...
         If second parameter is passed null then progressdialog will show (Loading...) by default if pass string such as(Searching..) then
         it will show (Searching...)
         */
        mDialog = Utils.showSweetProgressDialog(PostDetailActivity.this,
                getResources
                        ().getString(R.string.deleting_feed), SweetAlertDialog.PROGRESS_TYPE);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_DELETE_FEED,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
                        Utils.debug(TAG, response.toString());
                        res = response.toString();
                        try {
                            handleDeleteResponse(res);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                res = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
//                        user_id(int)
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIsConstants.KEY_FEED_ID, latestFeedsModel.getId());
                params.put(APIsConstants.KEY_USER_ID, latestFeedsModel.getUser_id());
                Utils.info("params...", params.toString());
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }


    public void onShareClick(View v) {
        Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
        Resources resources = getResources();

        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(resources.getString(R.string.share_email_native)));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.share_email_subject));
        emailIntent.setType("message/rfc822");

        PackageManager pm = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");


        Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.share_chooser_text));

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if (packageName.contains("twitter") || packageName.contains("facebook")
                    || packageName.contains("mms") || packageName.contains("android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if (packageName.contains("twitter")) {
                    intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.share_twitter));
                } else if (packageName.contains("facebook")) {
                    // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                    // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                    // will show the <meta content ="..."> text from that page with our link in Facebook.
//                    intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.share_facebook));
//                    shareOnFB();

                    // if (linkContent != null) {
//                        mDialog = Utils.showSweetProgressDialog(PostDetailActivity.this,
//                                getResources().getString(R.string.please_wait), SweetAlertDialog
//                                        .PROGRESS_TYPE);
//                        shareIt(linkContent);
//                    }
                    //ShareDialog.show(PostDetailActivity.this, linkContent);
                    //shareIt(linkContent);
                    shareOnFB();
                    //intent.putExtra(Intent.EXTRA_TEXT, mUrl);
                } else if (packageName.contains("mms")) {
                    intent.putExtra(Intent.EXTRA_TEXT, mUrl);
                } else if (packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                    intent.putExtra(Intent.EXTRA_TEXT, mUrl);
                    intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.share_email_subject));
                    intent.setType("message/rfc822");
                }

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }

    private void shareOnFB() {
        String urlToShare = /*mUrl *//*"http://stackoverflow.com/questions/7545254"*/"http://trigma.com / ";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        // intent.putExtra(Intent.EXTRA_SUBJECT, "Foo bar"); // NB: has no effect!
        Utils.debug(TAG, "mUrl : " + mUrl);
        // intent.putExtra(Intent.EXTRA_TEXT, mUrl);
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);

        // See if official Facebook app is found
        boolean facebookAppFound = false;
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                intent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }

// As fallback, launch sharer.php in a browser
        if (!facebookAppFound) {
//            String sharerUrl = mUrl;https://www.facebook.com/sharer/sharer.php?fb_object_id=876098475841535
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }

        startActivity(intent);
    }

    private void makeOwnerDataModel(String res) {
        Utils.debug(TAG, "Response owner Model:" + res);
        latestFeedList.clear();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            try {
                if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
                    JSONObject object = jsonObject.getJSONObject(APIsConstants.KEY_FEED_DATA);
                    if (object != null) {
                        mOwnerDataModel = new OwnerDataModel();
                        if (object != null) {
                            try {
                                mOwnerDataModel.setId(object.getString(APIsConstants.KEY_ID));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                mOwnerDataModel.setTitle(object.getString(APIsConstants.KEY_TITLE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                mOwnerDataModel.setDescription(object.getString(APIsConstants.KEY_DESCRIPTION));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                mOwnerDataModel.setMedia(object.getString(APIsConstants.KEY_MEDIA));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                mOwnerDataModel.setType(object.getString(APIsConstants.KEY_TYPE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                mOwnerDataModel.setUser_id(object.getString(APIsConstants.KEY_USER_ID));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                mOwnerDataModel.setCreated(object.getString(APIsConstants.KEY_CREATED));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                mOwnerDataModel.setModified(object.getString(APIsConstants.KEY_MODIFIED));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                mOwnerDataModel.setUser_name(object.getString(APIsConstants.KEY_USER_NAME));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                mOwnerDataModel.setViewcount(object.getString(APIsConstants.KEY_VIEWCOUNT));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                mOwnerDataModel.setThumbnail(object.getString(APIsConstants.KEY_THUMBNAIL));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                mOwnerDataModel.setThumbnail(object.getString(APIsConstants.KEY_PROFIT_AMOUNT));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                mOwnerDataModel.setFeed_owner(object.getString(APIsConstants.KEY_FEED_OWNER));
                                if (object.getString(APIsConstants.KEY_FEED_OWNER)
                                        .equalsIgnoreCase(Constants.YES)) {
                                    isOwner = true;
                                } else {
                                    isOwner = false;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                mOwnerDataModel.setProfit_amount(object.getString(APIsConstants
                                        .KEY_PROFIT_AMOUNT));
                                profitAmount = object.getString(APIsConstants
                                        .KEY_PROFIT_AMOUNT);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                mOwnerDataModel.setFb_feed_id(object.getString(APIsConstants
                                        .KEY_FB_FEED_ID));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (profitAmount.equalsIgnoreCase(Constants.ZERO))
                                setHeader("", true, true, false, false,
                                        isOwner, null);
                            else
                                setHeader(profitAmount, true, true, false, false,
                                        isOwner, null);
                            Session.setOwnerModel(mOwnerDataModel);
                        }
                    }
                } else {
                    try {
                        Utils.debug(Constants.API_TAG, jsonObject.getString(APIsConstants.KEY_MESSAGE));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void handleDeleteResponse(String res) {
        Utils.debug(TAG, "Result of delete API: " + res);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            try {
                if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
                    mDialog = Utils.showSweetProgressDialog(PostDetailActivity.this, jsonObject
                            .getString
                                    (APIsConstants.KEY_MESSAGE), SweetAlertDialog.SUCCESS_TYPE);
                    mDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Intent intent = new Intent(PostDetailActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    });
                    InterfaceListener.OnDelete(latestFeedsModel.getId(), true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void setSharedContent(String postId) {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + postId,
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public String url;

                    public void onCompleted(GraphResponse response) {
                        if (response != null) {

            /* handle the result */
                            Utils.debug(Constants.FACEBOOK, "response.getRawResponse() :  " +
                                    "" + response.getRawResponse());
                            Utils.debug(Constants.FACEBOOK, "response.getRawResponse() :  " +
                                    "" + response.getJSONObject());
                            if (response.getRawResponse() != null) {
                                JSONObject object = response.getJSONObject();
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = object.getJSONObject(Constants.KEY_DATA);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                JSONObject urlObject1 = null;
                                try {
                                    urlObject1 = jsonObject.getJSONObject(Constants.POST);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    try {
                                        url = urlObject1.getString(Constants.KEY_URL);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Utils.debug(Constants.FACEBOOK, "url : " + url);
                                    ShareLinkContent content = new ShareLinkContent.Builder()
                                            .setContentUrl(Uri.parse(url))
                                            .build();
                                    shareButton = (ShareButton) findViewById(R.id.shareButton);
                                    shareButton.setShareContent(content);
                                    ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
                                        @Override
                                        public void onSuccess(Sharer.Result result) {
                                            Utils.debug(TAG, "Sharer.Result : " + result + "");

                                        }

                                        @Override
                                        public void onCancel() {
                                            Utils.debug(TAG, "onCancel ");

                                        }

                                        @Override
                                        public void onError(FacebookException error) {
                                            Utils.debug(TAG, "Sharer.error : " + error + "");

                                        }
                                    });
                                    mUrl = url;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
        ).executeAsync();

    }

    private boolean makeSeenPostRequest(final String user_id, final String id) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_FEED_SEEN,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.debug(TAG, response.toString());
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
        int seenCount = 0;
        Utils.debug(TAG, "Response :  " + res);
        getFeedOwnerData();
        JSONObject object = null;
        try {
            object = new JSONObject(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (object != null) {
            Utils.debug(TAG, object + "");
            try {
                boolean seen = object.getBoolean(APIsConstants.KEY_RESULT);
                if (seen) {
                    if (latestFeedsModel.getViewcount().equalsIgnoreCase(Constants.EMPTY))
                        seenCount = 1;
                    else {
                        seenCount = Integer.parseInt(latestFeedsModel.getViewcount()) + 1;
                    }
                    txt_seen.setText(seenCount + "");
                } else {
                    txt_seen.setText(latestFeedsModel.getViewcount());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    //............FullView imageView..............//

    public void showImageDialog(String media) {
        Display display = this.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.custom_image_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        final TouchImageView custom_image = (TouchImageView) (dialog.findViewById(R.id.custom_image));
        custom_image.setLayoutParams(params);
        custom_image.setImageResource(R.drawable.pic_two);
        Glide.with(this).load(media).centerCrop()
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PostDetailActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
