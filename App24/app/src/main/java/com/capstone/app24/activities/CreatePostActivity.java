package com.capstone.app24.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.capstone.app24.R;
import com.capstone.app24.custom.SquareImageView;
import com.capstone.app24.utils.APIsConstants;
import com.capstone.app24.utils.AlertToastManager;
import com.capstone.app24.utils.AppController;
import com.capstone.app24.utils.Base64;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.FacebookUtils;
import com.capstone.app24.utils.NetworkUtils;
import com.capstone.app24.utils.Utils;
import com.capstone.app24.webservices_model.FeedRequestModel;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareButton;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.entities.Photo;
import com.sromku.simple.fb.entities.Video;
import com.sromku.simple.fb.listeners.OnPublishListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.RestAdapter;
import volley.Request;
import volley.VolleyError;
import volley.VolleyLog;
import volley.toolbox.StringRequest;

/**
 * Created by amritpal on 5/11/15.
 */
public class CreatePostActivity extends BaseActivity implements View.OnFocusChangeListener {
    private static final String TAG = CreatePostActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 12;
    private LinearLayout save;
    private TextView txt_header, txt_post;
    private ImageButton ibtn_select_image_from_gallery;
    private static final int SELECT_PICTURE = 1;
    private ImageView image_bitmap;
    private LinearLayout camera_tumb;
    private boolean isFromMediaActivity;
    private ScrollView sv;
    private EditText edit_post_title, edit_write_post;
    private Bitmap mIcon_val;
    private RestAdapter mRestAdapter;
    private FeedRequestModel mFeedModel;
    private String base64;
    private String gallery_type;
    private FeedRequestModel mCurrentFeedModel;
    private String mType = "";
    private SweetAlertDialog mDialog;
    private String res = "";
    private byte[] imageBytes;
    private Bitmap mBitmap;
    private ShareButton shareButton;
    private SimpleFacebook mSimpleFacebook;
    private Intent intent;
    private CallbackManager callbackManager;
    Bitmap bitmap = null;
    private boolean editable;
    private Intent editIntent;
    private boolean isVideo;
    private int likeCount;
    private String mFeedId = "";
    private String fb_share_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        setContentView(R.layout.activity_create_post);
        callbackManager = CallbackManager.Factory.create();
        setHeader(null, true, false, false, false, false, getResources().getString(R.string.post));
        initializeViews();
        setClickListeners();
        //getLikes("");
        //planATrip();
    }

    private void planATrip() {
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .setUserGenerated(true)
                .build();
        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type", "dsjfhgu")//613292//
                .putString("og:title", "title")
                .putString("og:description", "description")
                        //.putString("og:image", "http://dev614.trigma.us/24app/development/assets/images/uploads/feeds/feed_1451478575.png")
                .putString("og:video", "http://dev614.trigma.us/24app/development/assets/images/uploads/feeds/feed_media1451565010_.mp4")
                        //.putPhoto("capstone_app:picture", photo)
                .build();
        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("capstone_app:plan")
                .putObject("capstone_app:trip", object)
                .build();
        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("capstone_app:trip")
                .setAction(action)
                .build();

        shareButton = (ShareButton) findViewById(R.id.shareButton);
        shareButton.setShareContent(content);
        // shareButton.performClick();
        shareButton.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Utils.debug(TAG, "post Id : " + result.getPostId());
                //getLikes(result.getPostId());
                String fbUrl = "";
                fbUrl = FacebookUtils.getFeedUrl(result.getPostId());
                if (!mFeedId.equalsIgnoreCase(Constants.EMPTY) && result.getPostId() != null) {
                    updateFacebookFeedId(result.getPostId(), fbUrl);
                }
            }

            @Override
            public void onCancel() {
                Utils.debug(TAG, "Cancelled");

            }

            @Override
            public void onError(FacebookException error) {
                Utils.debug(TAG, "error : " + error);
            }
        });
    }

    private void createPost(String title, String description) {


/**
 * Working code for facebook story
 */

//        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
//                .putString("og:type", "dsjfhgu")//613292//
//                .putString("og:title", title)
//                .putString("og:description", description)
//                .build();
//        SharePhoto photo = new SharePhoto.Builder()
//                .setBitmap(bitmap)
//                .build();
//        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
//                .setActionType("app_capstone:create")
//                .putObject("app_capstone:post", object)
//                .build();
//        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
//                .setPreviewPropertyName("app_capstone:post")
//                .setAction(action)
//                .build();
//
//        shareButton = (ShareButton) findViewById(R.id.shareButton);
//        shareButton.setShareContent(content);

/**
 *
 */
        /**
         * Client facebook
         */
        ShareOpenGraphObject object = null;
        if (!fb_share_url.equalsIgnoreCase(Constants.EMPTY) && !fb_share_url.endsWith(".mp4")) {
            object = new ShareOpenGraphObject.Builder()
                    .putString("og:type", "dsjfhgu")//613292//
                    .putString("og:title", title)
                    .putString("og:description", description)
                    .putString("og:image", fb_share_url)
                    .build();
        }
        if (!fb_share_url.equalsIgnoreCase(Constants.EMPTY)) {
            if (fb_share_url.endsWith(".mp4") || fb_share_url.endsWith(".3gp"))
                object = new ShareOpenGraphObject.Builder()
                        .putString("og:type", "dsjfhgu")//613292//
                        .putString("og:title", title)
                        .putString("og:description", description)
                        .putString("og:video", fb_share_url)
                        .build();
        } else {
            object = new ShareOpenGraphObject.Builder()
                    .putString("og:type", "dsjfhgu")//613292//
                    .putString("og:title", title)
                    .putString("og:description", description)
                    .build();
        }

//        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
//                .putString("og:type", "dsjfhgu")//613292//
//                .putString("og:title", title)
//                .putString("og:description", description)
//                .putString("og:image", fb_share_url)
//                .build();
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("capstone_app:create")
                .putObject("capstone_app:post", object)
//                .putString("fb:explicitly_shared", "true")
//                .putPhoto("capstone_app:pic", photo)
                .build();
        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("capstone_app:post")
                .setAction(action)
                .build();

        shareButton = (ShareButton) findViewById(R.id.shareButton);
        shareButton.setShareContent(content);
        shareButton.performClick();
        shareButton.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Utils.debug(TAG, "post Id : " + result.getPostId());
                //getLikes(result.getPostId());
                String fbUrl = "";
                fbUrl = FacebookUtils.getFeedUrl(result.getPostId());
                if (!mFeedId.equalsIgnoreCase(Constants.EMPTY) && result.getPostId() != null) {
                    updateFacebookFeedId(result.getPostId(), fbUrl);
                }
            }

            @Override
            public void onCancel() {
                Utils.debug(TAG, "Cancelled");

            }

            @Override
            public void onError(FacebookException error) {
                Utils.debug(TAG, "error : " + error);
            }
        });

    }

    private boolean updateFacebookFeedId(final String postId, final String fbUrl) {

        final SweetAlertDialog pd = Utils.showSweetProgressDialog(CreatePostActivity.this,
                getResources
                        ().getString(R.string.posting_feed), SweetAlertDialog.PROGRESS_TYPE);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_UPDATE_FB_FEED_ID,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.debug(TAG, response.toString());
                        Utils.closeSweetProgressDialog(CreatePostActivity.this, pd);
                        res = response.toString();
                        try {
                            setFBFeedData(res);
                            Utils.debug("fb", "Now going to post on Facebook");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeSweetProgressDialog(CreatePostActivity.this, pd);
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                res = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIsConstants.KEY_FEED_ID, mFeedId);
                params.put(APIsConstants.KEY_FB_FEED_ID, postId);
                params.put(APIsConstants.KEY_FB_SHARE_URL, fbUrl);
                Utils.info("params...", params.toString());
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;

    }

    private void setFBFeedData(String res) {
        Utils.debug(Constants.FACEBOOK, "Feed id updated" + res);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mDialog = Utils.showSweetProgressDialog(this, getResources().getString(R.string
                        .progress_loading),
                SweetAlertDialog.PROGRESS_TYPE);
        Intent intent = getIntent();
        try {
            if (intent.getStringExtra(Constants.KEY_GALLERY_TYPE) != null) {
                mType = intent.getStringExtra(Constants.KEY_GALLERY_TYPE);
                Utils.debug(TAG, Constants.KEY_GALLERY_TYPE + " : " + mType);
            }
            if (mType.equalsIgnoreCase(Constants.KEY_TEXT)) {

                if (intent.hasExtra(Constants.IS_VIDEO)) {
                    isVideo = intent.getBooleanExtra(Constants.IS_VIDEO, false);
                    Utils.debug(TAG, Constants.IS_VIDEO + " : " + isVideo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (intent.hasExtra(Constants.IS_FROM_MEDIA_ACTIVITY)) {
            isFromMediaActivity = intent.getBooleanExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
        }

        if (isFromMediaActivity) {
            try {
                if (intent.hasExtra(Constants.KEY_BUNDLE)) {
                    Bundle bundle = intent.getBundleExtra(Constants.KEY_BUNDLE);
                    bitmap = (Bitmap) bundle.get(Constants.KEY_DATA);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        UpdateUI();
        Utils.closeSweetProgressDialog(this, mDialog);

    }

    /**
     * Update UI with Data
     */
    private void UpdateUI() {
        intent = getIntent();
        Utils.debug(TAG, "Intent Data  : " + intent);
        if (intent != null) {
            try {
                mType = intent.getStringExtra(Constants.KEY_GALLERY_TYPE);
                Utils.debug(TAG, "" + mType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (AddMediaActivity.imageSelectedPosition == -1 && AddMediaActivity
                .videoSelectedPosition == -1 && AddMediaActivity.textSelectedPosition == -1) {
            camera_tumb.removeAllViews();
        }
        final FeedRequestModel feedModel = Utils.getFeed();
        Utils.debug("feedModel", "CreatePost feedModel : " + feedModel.getTitle());
        Utils.debug("feedModel", "CreatePost feedModel : " + feedModel.getDescription());
        Utils.debug("feedModel", "CreatePost feedModel : " + feedModel.getType());
        Utils.debug("feedModel", "CreatePost feedModel : " + feedModel.getMedia());
        Utils.debug("feedModel", "CreatePost feedModel : " + feedModel.getUser_id());
        if (feedModel != null) {
            if (feedModel.getTitle() != null && !feedModel.getTitle().equalsIgnoreCase(Constants
                    .EMPTY)) {
                edit_post_title.setText(feedModel.getTitle());
            }
            if (feedModel.getDescription() != null && !feedModel.getDescription()
                    .equalsIgnoreCase(Constants
                            .EMPTY)) {
                edit_write_post.setText(feedModel.getDescription());
            }
            if (feedModel.getType() != null && !feedModel.getType().equalsIgnoreCase(Constants
                    .EMPTY)) {
                mType = feedModel.getType();
            }
            if (feedModel.getMedia() == null && mType.equalsIgnoreCase(Constants.KEY_TEXT)) {
                feedModel.setMedia(Constants.EMPTY);
            } else {
                if (feedModel.getType() != null && !feedModel.getType().equalsIgnoreCase(Constants
                        .EMPTY) && feedModel.getMedia() != null) {
                    if (feedModel.getType().equalsIgnoreCase(Constants.KEY_IMAGES)) {
                        try {
                            if (feedModel.getMedia() != null && isFromMediaActivity)
                                bitmap = BitmapFactory.decodeFile(feedModel.getMedia());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream thumb_stream = new ByteArrayOutputStream();
                        if (bitmap != null) {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, thumb_stream);
                        }
                        byte[] ful_bytes = thumb_stream.toByteArray();
                        imageBytes = ful_bytes;
                        base64 = Base64.encodeBytes(ful_bytes);
                        feedModel.setBase64String(base64);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SquareImageView imageView = new SquareImageView
                                        (CreatePostActivity.this);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100, 1.0f);
                                params.setMargins(5, 10, 5, 10);
                                imageView.setLayoutParams(params);
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                imageView.setImageBitmap(bitmap);
                                mBitmap = bitmap;
                                camera_tumb.addView(imageView);
                                if (camera_tumb.getChildCount() <= 0)
                                    ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
                                else
                                    ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
                            }
                        }, 1000);

                    } else if (feedModel.getType().equalsIgnoreCase(Constants.KEY_VIDEOS)) {
//                        mDialog = Utils.showSweetProgressDialog(this, "", SweetAlertDialog.PROGRESS_TYPE);

//                        Uri vidFile = Uri.parse(feedModel.getMedia());
                        bitmap = ThumbnailUtils.createVideoThumbnail(
                                feedModel.getMedia(), MediaStore.Video.Thumbnails.MINI_KIND);


                        int bytesRead;
                        FileInputStream imageStream = null;
                        try {

                            imageStream = new FileInputStream(feedModel.getMedia());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        byte[] b = new byte[1024];
                        try {
                            while ((bytesRead = imageStream.read(b)) != -1) {
                                bos.write(b, 0, bytesRead);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            byte[] ba = bos.toByteArray();
                            base64 = Base64.encodeBytes(ba);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Utils.debug(TAG, "base64 : " + base64);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SquareImageView imageView = new SquareImageView
                                        (CreatePostActivity.this);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100, 1.0f);
                                params.setMargins(5, 10, 5, 10);
                                imageView.setLayoutParams(params);
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                imageView.setImageBitmap(bitmap);
                                camera_tumb.addView(imageView);
                                if (camera_tumb.getChildCount() <= 0)
                                    ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
                                else
                                    ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
                                // Utils.closeSweetProgressDialog(this, mDialog);
                            }
                        }, 1000);

                    } else if (feedModel.getType().equalsIgnoreCase(Constants.KEY_TEXT)) {
                        if (isVideo) {
                            Uri vidFile = Uri.parse(feedModel.getMedia());
//
                            bitmap = ThumbnailUtils.createVideoThumbnail(
                                    feedModel.getMedia(), MediaStore.Video.Thumbnails.MINI_KIND);


                            int bytesRead;
                            FileInputStream imageStream = null;
                            try {

                                imageStream = new FileInputStream(feedModel.getMedia());
                            } catch (FileNotFoundException e1) {
                                e1.printStackTrace();
                            }

                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            byte[] b = new byte[1024];
                            try {
                                while ((bytesRead = imageStream.read(b)) != -1) {
                                    bos.write(b, 0, bytesRead);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                byte[] ba = bos.toByteArray();
                                base64 = Base64.encodeBytes(ba);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Utils.debug(TAG, "base64 : " + base64);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SquareImageView imageView = new SquareImageView(CreatePostActivity.this);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100, 1.0f);
                                    params.setMargins(5, 10, 5, 10);
                                    imageView.setLayoutParams(params);
                                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    imageView.setImageBitmap(bitmap);
                                    camera_tumb.addView(imageView);
                                    if (camera_tumb.getChildCount() <= 0)
                                        ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
                                    else
                                        ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
                                }
                            }, 1000);
                        } else {
                            try {
                                if (feedModel.getMedia() != null && isFromMediaActivity)
                                    bitmap = BitmapFactory.decodeFile(feedModel.getMedia());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream thumb_stream = new ByteArrayOutputStream();
                            if (bitmap != null) {
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, thumb_stream);
                            }
                            byte[] ful_bytes = thumb_stream.toByteArray();
                            imageBytes = ful_bytes;
                            base64 = Base64.encodeBytes(ful_bytes);
                            feedModel.setBase64String(base64);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SquareImageView imageView = new SquareImageView
                                            (CreatePostActivity.this);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100, 1.0f);
                                    params.setMargins(5, 10, 5, 10);
                                    imageView.setLayoutParams(params);
                                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    imageView.setImageBitmap(bitmap);
                                    mBitmap = bitmap;
                                    camera_tumb.addView(imageView);
                                    if (camera_tumb.getChildCount() <= 0)
                                        ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
                                    else
                                        ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
                                }
                            }, 1000);

                        }


                    }
                }
            }
        }
        sv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                sv.post(new Runnable() {
                    public void run() {
                        sv.scrollTo(0, sv.getBottom() + sv.getScrollY());
                    }
                });
            }
        });


    }

    Bitmap getPreview(Uri uri) {
        File image = new File(uri.getPath());

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getPath(), bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
            return null;

        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                : bounds.outWidth;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize;
        return BitmapFactory.decodeFile(image.getPath(), opts);
    }

    public byte[] readBytes(Uri uri) throws IOException {
        // this dynamically extends to take the bytes you read
        Base64.InputStream inputStream = (Base64.InputStream) getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    /**
     * Initialize Views for user interface
     */

    private void initializeViews() {
        ibtn_select_image_from_gallery = (ImageButton) findViewById(R.id
                .ibtn_select_image_from_gallery);
        camera_tumb = (LinearLayout) findViewById(R.id.camera_tumb);
        sv = (ScrollView) findViewById(R.id.sv);
        edit_post_title = (EditText) findViewById(R.id.edit_post_title);
        edit_write_post = (EditText) findViewById(R.id.edit_write_post);
        txt_save = (TextView) findViewById(R.id.txt_save);

        callbackManager = CallbackManager.Factory.create();
        //shareButton.setOnClickListener(this);
    }

    private void setClickListeners() {
        ibtn_back.setOnClickListener(this);
        ibtn_select_image_from_gallery.setOnClickListener(this);
        edit_post_title.setOnFocusChangeListener(this);
        edit_write_post.setOnFocusChangeListener(this);
        txt_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ibtn_back:
                AddMediaActivity.videoSelectedPosition = -1;
                AddMediaActivity.imageSelectedPosition = -1;
                AddMediaActivity.textSelectedPosition = -1;
                finish();
                intent = new Intent(CreatePostActivity.this, MainActivity.class);
                intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
                Utils.setFeed(null);
                startActivity(intent);
                break;
            case R.id.txt_save:
                if (NetworkUtils.isOnline(this)) {
                    makePostFeedRequest();
                } else {
                    Utils.showSweetProgressDialog(this, "Please check internet connection",
                            SweetAlertDialog.WARNING_TYPE);
                }
                // postToWall();
                break;
            case R.id.ibtn_select_image_from_gallery:
                Intent intentLauncher = null;

                // Preparing the feed object to post
                if (mCurrentFeedModel == null)
                    mCurrentFeedModel = new FeedRequestModel();
                mCurrentFeedModel.setTitle(edit_post_title.getText().toString().trim());
                mCurrentFeedModel.setDescription(edit_write_post.getText().toString().trim());
                mCurrentFeedModel.setUser_id(new Utils().getSharedPreferences(CreatePostActivity
                        .this, Constants.KEY_USER_DETAILS, ""));
                mCurrentFeedModel.setType(mType);
                Utils.setFeed(mCurrentFeedModel);
                //
                if (mType.equalsIgnoreCase(Constants.KEY_TEXT)) {
                    finish();
                    intent = new Intent(CreatePostActivity.this, AddMediaActivity.class);
                    intent.putExtra(Constants.KEY_GALLERY_TYPE, Constants.KEY_TEXT);
                    startActivity(intent);
                } else if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
                    finish();
                    intent = new Intent(CreatePostActivity.this, AddMediaActivity.class);
                    intent.putExtra(Constants.KEY_GALLERY_TYPE, Constants.KEY_IMAGES);
                    startActivity(intent);
                } else if (mType.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
                    finish();
                    intent = new Intent(CreatePostActivity.this, AddMediaActivity.class);
                    intent.putExtra(Constants.KEY_GALLERY_TYPE, Constants.KEY_VIDEOS);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.debug(TAG, "onActivityResult " + isFromMediaActivity);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // TODO Extract the data returned from the child Activity.
            Bundle extras = data.getExtras();
            Utils.debug(TAG, extras + "");
            Bitmap imageBitmap = (Bitmap) extras.get(Constants.KEY_DATA);
            SquareImageView imageView = new SquareImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100, 1.0f);
            params.setMargins(5, 10, 5, 10);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageBitmap(imageBitmap);
            camera_tumb.addView(imageView);
            if (camera_tumb.getChildCount() <= 0)
                ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
            else
                ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);

        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent;
        intent = new Intent(CreatePostActivity.this, MainActivity.class);
        intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
        AddMediaActivity.videoSelectedPosition = -1;
        AddMediaActivity.imageSelectedPosition = -1;
        AddMediaActivity.textSelectedPosition = -1;
        Utils.setFeed(null);
        startActivity(intent);
    }

    OnPublishListener onPublishListener = new OnPublishListener() {
        @Override
        public void onComplete(String postId) {
            Log.i(TAG, "Published successfully. The new post id = " + postId);
        }

    /*
     * You can override other methods here:
     * onThinking(), onFail(String reason), onException(Throwable throwable)
     */
    };


    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }


    private boolean makePostFeedRequest() {
        final SweetAlertDialog pd = Utils.showSweetProgressDialog(CreatePostActivity.this,
                getResources
                        ().getString(R.string.posting_feed), SweetAlertDialog.PROGRESS_TYPE);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_SAVE_FEEDS,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.debug(TAG, response.toString());
                        Utils.closeSweetProgressDialog(CreatePostActivity.this, pd);
                        res = response.toString();
                        try {
                            setFeedData(res);
                            Utils.debug("fb", "Now going to post on Facebook");
                            // postToWall();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeSweetProgressDialog(CreatePostActivity.this, pd);
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                res = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                String userId = new Utils().getSharedPreferences(CreatePostActivity.this, Constants
                        .KEY_USER_DETAILS, "");
                String title = edit_post_title.getText().toString().trim();
                String description = edit_write_post.getText().toString().trim();
                String media = "";
                if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
                    media = Utils.getFeed().getBase64String();
                }
                if (mType.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
                    media = Utils.getFeed().getBase64String();
                } else {
                    media = "";
                }
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIsConstants.KEY_USER_ID, userId);
                params.put(APIsConstants.KEY_TITLE, title);
                params.put(APIsConstants.KEY_DESCRIPTION, description);
                if (!mType.equalsIgnoreCase(Constants.KEY_TEXT) || camera_tumb.getChildCount() > 0) {
                    params.put(APIsConstants.KEY_MEDIA, base64);
                } else {
                    params.put(APIsConstants.KEY_MEDIA, "");
                }

                params.put(APIsConstants.KEY_TYPE, mType);
                Utils.info("params...", params.toString());
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }

    //CallbackManager callbackManager;
    private void postToWall(String fb_share_url) {
        createPost(edit_post_title.getText().toString().trim(), edit_write_post.getText()
                .toString().trim());

    }

    private void postTextToWall() {
        Feed feed = new Feed.Builder()
                .setMessage(Utils.getFeed().getTitle())
                .setDescription(Utils.getFeed().getDescription())
                .build();
        mSimpleFacebook.publish(feed, false, onPublishListener);
    }

    private void postPhotoToWall() {
        Photo photo = new Photo.Builder()
                .setImage(mBitmap)
                .setName(edit_post_title.getText().toString())
                        //.setPlace("110619208966868")
                .build();
        mSimpleFacebook.publish(photo, false, onPublishListener);
    }

    private void postVideoToWall() {
        File videoFile = null;

// create a Video instance and add some properties
        Video video = new Video.Builder()
                .setVideo(videoFile)
                .setDescription("Video from #android_simple_facebook sample application")
                .setName("Cool video")
                .build();
//        Publish video to "Videos" album

        mSimpleFacebook.publish(video, onPublishListener);
    }

    private void setFeedData(String res) throws JSONException {
        Utils.debug(TAG, "Response : AFTER fEED CREATE " + res);
        try {
            JSONObject jsonObject = new JSONObject(res);
            if (jsonObject != null) {
                if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
                    try {
                        Utils.debug(TAG, jsonObject.getString(APIsConstants.KEY_MESSAGE));
                        mDialog = Utils.showSweetProgressDialog(CreatePostActivity.this, jsonObject
                                .getString(APIsConstants.KEY_MESSAGE), SweetAlertDialog
                                .SUCCESS_TYPE);
                        try {
                            mFeedId = jsonObject.getString(APIsConstants.KEY_FEED_ID);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        JSONObject object = null;
                        try {
                            object = jsonObject.getJSONObject(APIsConstants.KEY_FEED_DATA);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            fb_share_url = object.getString(APIsConstants.KEY_MEDIA);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Utils.debug(TAG, "mFeedId : " + mFeedId);
                        Utils.debug(TAG, "fb_share_url : " + fb_share_url);
                        postToWall(fb_share_url);
                        mDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Utils.closeSweetProgressDialog(CreatePostActivity.this, mDialog);
                                finish();
                                Intent intent = new Intent(CreatePostActivity.this, MainActivity.class);
                                startActivity(intent);

                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Utils.debug(TAG, jsonObject.getString(APIsConstants.KEY_MESSAGE));
                        Utils.showSweetProgressDialog(CreatePostActivity.this, jsonObject
                                .getString(APIsConstants.KEY_MESSAGE), SweetAlertDialog
                                .ERROR_TYPE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                AlertToastManager.showToast("Error Occured", CreatePostActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void postStoryToWall() {

//        shareButton = (ShareButton) findViewById(R.id.shareButton);
        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type", "app_capstone.post")
//                .putString("og:type", "fitness.course")
//                .putString("og:title", "Sample Course")
//                .putString("og:description", "This is a sample course.")
                .putString("title", "Sample Title")
                .putString("description", "Sample Description")
//                .putInt("fitness:duration:value", 200)
//                .putString("fitness:duration:units", "s")
//                .putInt("fitness:distance:value", 24)
//                .putString("fitness:distance:units", "km")
//                .putInt("fitness:speed:value", 5)
//                .putString("fitness:speed:units", "m/s")
                .build();
        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("app_capstone.create")
                .putObject("app_capstone:post", object)
                .build();
        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("app_capstone:post")
                .setAction(action)
                .build();


        Utils.debug(TAG, "Inside Post to Wall ");
        String userId = new Utils().getSharedPreferences(CreatePostActivity.this, Constants
                .KEY_USER_DETAILS, "");
        String title = edit_post_title.getText().toString().trim();
        String description = edit_write_post.getText().toString().trim();
        if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
            Feed feed = new Feed.Builder()
                    .setName(title)
                    .setDescription(description)
                    .setPicture("http://dev614.trigma.us/24app/development/assets/images/uploads/feeds/feed_media1449576680_.jpg")
                    .build();
            // mSimpleFacebook.publish(feed, onPublishListener);
        }
        if (mType.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
            Feed feed = new Feed.Builder()
                    .setName(title)
                    .setDescription(description)
                    .setPicture("http://dev614.trigma.us/24app/development/assets/images/uploads/feeds/feed_media1449576680_.jpg")
                    .build();
            // mSimpleFacebook.publish(feed, onPublishListener);

        }
        if (mType.equalsIgnoreCase(Constants.KEY_TEXT)) {
            Feed feed = new Feed.Builder()
                    .setName(title)
                    .setDescription(description)
                    .setPicture("http://dev614.trigma.us/24app/development/assets/images/uploads/feeds/feed_media1449576680_.jpg")
                    .build();
            //  mSimpleFacebook.publish(feed, onPublishListener);
        }
    }
}
