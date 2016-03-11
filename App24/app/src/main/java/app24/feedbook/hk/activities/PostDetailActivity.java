package app24.feedbook.hk.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
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

import app24.feedbook.hk.R;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import app24.feedbook.hk.bean.LatestFeedsModel;
import app24.feedbook.hk.bean.OwnerDataModel;
import app24.feedbook.hk.utils.APIsConstants;
import app24.feedbook.hk.utils.AlertToastManager;
import app24.feedbook.hk.utils.AppController;
import app24.feedbook.hk.utils.Constants;
import app24.feedbook.hk.utils.InterfaceListener;
import app24.feedbook.hk.utils.NetworkUtils;
import app24.feedbook.hk.utils.Session;
import app24.feedbook.hk.utils.TouchImageView;
import app24.feedbook.hk.utils.Utils;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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
    public static TextView txt_comment;
    public static int commentCount;
    private ShareButton shareButton;
    private CallbackManager callbackManager;
    private String mUrl;
    private RelativeLayout comment;
    private String mPostId = null;
    private boolean isFeedLiked;
    private Button like;
    private ShareLinkContent linkContent;
    private String s;
    private String url;
    private boolean isFromListActivity = false;
    private boolean isFeedWithin24Hours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_post_detail);
        callbackManager = CallbackManager.Factory.create();
        latestFeedsModel = new Utils(this).getLatestFeedPreferences(this);
        setHeader(null, true, true, false, false, false, null);
        AccessToken.refreshCurrentAccessTokenAsync();
        type = getIntent().getIntExtra("type", 0);
        initializeViews();
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        setClickListeners();
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.IS_FROM_LIST)) {
            isFromListActivity = intent.getBooleanExtra(Constants.IS_FROM_LIST, false);
            if (isFromListActivity)
                makeSeenPostRequest(new Utils(this).getSharedPreferences(this, Constants
                        .KEY_USER_DETAILS, ""), latestFeedsModel.getId());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();

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
                        .LayoutParams.MATCH_PARENT, (Utils.getHeight(this) / 2) - 100);
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


                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout
                        .LayoutParams.MATCH_PARENT, (Utils.getHeight(this) / 2) - 100);
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
            txt_created_time.setText(Utils.getTimeAgo(this, Long.parseLong(latestFeedsModel
                    .getCreated())));
            profitAmount = latestFeedsModel.getProfit_amount();
            if (profitAmount.equalsIgnoreCase(Constants.ZERO)) {
                setHeader(Constants.EMPTY, true, true, false, false, isOwner, null);
            } else {
                setHeader(profitAmount, true, true, false, false, isOwner, null);
            }

            //getFeedOwnerData();

            if (latestFeedsModel.getFb_feed_id() != null && !latestFeedsModel.getFb_feed_id()
                    .equalsIgnoreCase(Constants.EMPTY)) {
                getLikes(latestFeedsModel.getFb_feed_id());
                getComments(latestFeedsModel.getFb_feed_id());
                mPostId = latestFeedsModel.getFb_feed_id();

            }

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
        if (latestFeedsModel.getFb_feed_id() != null) {
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + latestFeedsModel.getFb_feed_id() + "/likes",
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
                if (!new Utils(PostDetailActivity.this).getSharedPreferences(PostDetailActivity.this, Constants.KEY_USER_DETAILS, "").equalsIgnoreCase(latestFeedsModel.getUser_id())) {
                    mDialog = Utils.showSweetProgressDialog(PostDetailActivity.this, getResources()
                            .getString(R.string.please_wait), SweetAlertDialog.PROGRESS_TYPE);
                    shareFeed(v);
                } else {
                    onShareClick(v);
//                    mDialog = Utils.showSweetProgressDialog(this, getResources().getString(R.string
//                            .you_cant_share_this_feed), SweetAlertDialog.NORMAL_TYPE);
                }
                break;
            case R.id.like:
                if (NetworkUtils.isOnline(this)) {
                    mDialog = Utils.showSweetProgressDialog(PostDetailActivity.this, getResources()
                            .getString(R
                                    .string.please_wait), SweetAlertDialog.PROGRESS_TYPE);
                    if (isFeedLiked) {
                        unLike();
                    } else {
                        like();
                    }
                } else {
                    AlertToastManager.showToast(getResources().getString(R.string
                            .check_your_internet_connection), this);
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
                finish();
                intent = new Intent(PostDetailActivity.this, EditPostActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_delete:
                edit_menu.setVisibility(View.GONE);
                if (NetworkUtils.isOnline(this)) {
                    if (isFeedWithin24Hours) {
                        deleteFeed();
                    } else {
                        Utils.showSweetProgressDialog(this, getResources().getString(R.string
                                .cannot_delete_this_feed), SweetAlertDialog.ERROR_TYPE);

                    }
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
                    if (NetworkUtils.isOnline(this)) {
                        Intent intent = new Intent(PostDetailActivity.this, CommentActivity.class);
                        intent.putExtra(Constants.POST_ID, latestFeedsModel.getFb_feed_id());
                        startActivity(intent);
                    } else {
                        AlertToastManager.showToast(getResources().getString(R.string
                                .check_your_internet_connection), this);
                    }
                } else
                    AlertToastManager.showToast(getResources().getString(R.string.error_occured), this);
                break;
        }
    }


    private boolean shareFeed(final View v) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_ADD_SHARE_USER,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
                        Utils.debug(TAG, response.toString());
                        res = response.toString();
                        try {
                            handleShareResponse(res, v);
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
                //  user_id(int)
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIsConstants.KEY_FEED_ID, latestFeedsModel.getId());
                params.put(APIsConstants.KEY_USER_ID, new Utils(PostDetailActivity.this)
                        .getSharedPreferences
                                (PostDetailActivity.this, Constants.KEY_USER_DETAILS, ""));
                Utils.info("params...", params.toString());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }

    private void handleShareResponse(String res, View v) {
        Utils.debug(TAG, "Response from AddSharerUser Web Service : " + res);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            try {
                if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
                    onShareClick(v);
                    getFeedOwnerData();
                    Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
                } else if (jsonObject.has(Constants.LOGGED_IN)) {
                    boolean loggedIn = jsonObject.getBoolean(Constants.LOGGED_IN);
                    Utils.debug(TAG, loggedIn + "");
                    logout();
                    LoginManager.getInstance().logOut();
                    Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);

                } else {
                    if (jsonObject.has(APIsConstants.KEY_MESSAGE)) {
                        mDialog = Utils.showSweetProgressDialog(PostDetailActivity.this,
                                jsonObject.getString(APIsConstants.KEY_MESSAGE), SweetAlertDialog.ERROR_TYPE);
                    }
                    if (jsonObject.has(APIsConstants.KEY_MESSAGE) && jsonObject.getString
                            (APIsConstants.KEY_MESSAGE).equalsIgnoreCase("You have already shared this feed")) {
                        onShareClick(v);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean logout() {
        mDialog = Utils.showSweetProgressDialog(PostDetailActivity.this,
                getResources
                        ().getString(R.string.progress_loading), SweetAlertDialog.PROGRESS_TYPE);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_LOGOUT,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.debug(TAG, response.toString());
                        Utils.closeSweetProgressDialog(PostDetailActivity.this, mDialog);
                        res = response.toString();
                        try {
                            logoutResponse(res);
                        } catch (JSONException e) {
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
                Utils.debug("acc", new Utils().getSharedPreferences(PostDetailActivity.this, Constants
                        .KEY_FACEBOOK_ACCESS_TOKEN, Constants.EMPTY));
                new Utils().getSharedPreferences(PostDetailActivity.this, Constants
                        .KEY_FACEBOOK_ACCESS_TOKEN, Constants.EMPTY);
                params.put("user_deviceToken", new Utils().getSharedPreferences(PostDetailActivity.this, Constants
                        .KEY_FACEBOOK_ACCESS_TOKEN, Constants.EMPTY));
                Utils.info("params...", params.toString());
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }

    private void logoutResponse(String res) throws JSONException {
        JSONObject jsonObject = new JSONObject(res);
        if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
            new Utils(PostDetailActivity
                    .this).setPreferences
                    (PostDetailActivity.this, Constants
                            .KEY_IS_LOGGED_IN, false);
            new Utils(this).clearSharedPreferences(this);
            finish();
            Intent intent = new Intent(PostDetailActivity.this, SplashActivity.class);
            intent.setAction(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        } else if (jsonObject.getString(APIsConstants.KEY_MESSAGE).contains("User does not exist.")) {
            mDialog = Utils.showSweetProgressDialog(PostDetailActivity.this, jsonObject.getString
                    (APIsConstants
                            .KEY_MESSAGE), SweetAlertDialog.ERROR_TYPE);
            mDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    Session.logout(PostDetailActivity.this);
                }
            });


        }

    }

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
                    intent.putExtra(Intent.EXTRA_TEXT, getUrl());
                } else if (packageName.contains("mms")) {
                    intent.putExtra(Intent.EXTRA_TEXT, getUrl());
                } else if (packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                    intent.putExtra(Intent.EXTRA_TEXT, getUrl());
                    intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.share_email_subject));
                    intent.setType("message/rfc822");
                }

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        // openInChooser.putExtras(extras);
        setResult(Activity.RESULT_OK, openInChooser);
        startActivityForResult(openInChooser, RESULT_OK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                                latestFeedsModel.setViewcount(object.getString(APIsConstants.KEY_VIEWCOUNT));
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
                                String feed24 = object.getString(APIsConstants
                                        .KEY_TIME);
                                if (feed24.equalsIgnoreCase("0")) {
                                    isFeedWithin24Hours = true;
                                } else if (feed24.equalsIgnoreCase("1")) {
                                    isFeedWithin24Hours = false;
                                }

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

                            if (isFeedWithin24Hours) {
                                if (isOwner) {
                                    if (profitAmount.equalsIgnoreCase(Constants.ZERO))
                                        setHeader("", true, true, false, false,
                                                true, null);
                                    else
                                        setHeader(profitAmount, true, true, false, false,
                                                true, null);
                                } else {
                                    if (profitAmount.equalsIgnoreCase(Constants.ZERO))
                                        setHeader("", true, true, false, false,
                                                false, null);
                                    else
                                        setHeader(profitAmount, true, true, false, false,
                                                false, null);
                                }
                            } else {
                                if (profitAmount.equalsIgnoreCase(Constants.ZERO))
                                    setHeader("", true, true, false, false,
                                            false, null);
                                else
                                    setHeader(profitAmount, true, true, false, false,
                                            false, null);
                            }


//                            if (profitAmount.equalsIgnoreCase(Constants.ZERO))
//                                setHeader("", true, true, false, false,
//                                        isOwner, null);
//                            else
//                                setHeader(profitAmount, true, true, false, false,
//                                        isOwner, null);
                            Session.setOwnerModel(mOwnerDataModel);
                            latestFeedsModel.setProfit_amount(profitAmount);

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
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            try {
                if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
                    mDialog = Utils.showSweetProgressDialog(PostDetailActivity.this, getResources
                                    ().getString(R.string.delete_successfully),
                            SweetAlertDialog.SUCCESS_TYPE);
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
        //getFeedOwnerData();
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
                        latestFeedsModel.setViewcount("" + seenCount);
                    }
                    txt_seen.setText(seenCount + "");
                } else {
                    txt_seen.setText(latestFeedsModel.getViewcount());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        getFeedOwnerData();
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

    public String getUrl() {
        String s;
        if (latestFeedsModel.getFb_feed_id().contains("_"))
            s = Constants.PAGE_LINK + latestFeedsModel.getFb_feed_id().split("_")[1];
        else
            s = Constants.PAGE_LINK + latestFeedsModel.getFb_feed_id();
        return s;
    }
}
