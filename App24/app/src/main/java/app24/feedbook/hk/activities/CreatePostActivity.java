package app24.feedbook.hk.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import app24.feedbook.hk.R;
import app24.feedbook.hk.custom.SquareImageView;
import app24.feedbook.hk.utils.APIsConstants;
import app24.feedbook.hk.utils.AlertToastManager;
import app24.feedbook.hk.utils.AppController;
import app24.feedbook.hk.utils.Base64;
import app24.feedbook.hk.utils.Constants;
import app24.feedbook.hk.utils.NetworkUtils;
import app24.feedbook.hk.utils.Session;
import app24.feedbook.hk.utils.Utils;
import app24.feedbook.hk.webservices_model.FeedRequestModel;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.share.widget.ShareButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.apache.commons.lang.StringEscapeUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
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
    private ImageButton ibtn_select_image_from_gallery;
    private LinearLayout camera_tumb;
    private boolean isFromMediaActivity;
    private ScrollView sv;
    private EditText edit_post_title, edit_write_post;
    private String base64;
    private FeedRequestModel mCurrentFeedModel;
    private String mType = "";
    private SweetAlertDialog mDialog;
    private String res = "";
    private Bitmap mBitmap;
    private ShareButton shareButton;
    private Intent intent;
    private CallbackManager callbackManager;
    Bitmap bitmap = null;
    private boolean isVideo;
    private String mFeedId = "";
    private String fb_share_url = "";
    private URLSpan[] urls;
    private Bundle mParams = new Bundle();
    private byte[] mBytes_array = new byte[1024];
    ArrayList<byte[]> mBytesArrayList = new ArrayList<>();
    private int count = 0;
    String video_id = null, start_offset = null, mEnd_offset = null, upload_session_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_create_post);
        callbackManager = CallbackManager.Factory.create();
        setHeader(null, true, false, false, false, false, CreatePostActivity.this.getResources()
                .getString(R.string
                        .post));
        initializeViews();
        setClickListeners();
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
        } else {
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
        mCurrentFeedModel = Utils.getFeed();
        Utils.debug("feedModel", "feedModel.getTitle() : " + feedModel.getTitle());
        Utils.debug("feedModel", "feedModel.getDescription() : " + feedModel.getDescription());
        Utils.debug("feedModel", "feedModel.getType() : " + feedModel.getType());
        Utils.debug("feedModel", "feedModel.getMedia() : " + feedModel.getMedia());
        Utils.debug("feedModel", "feedModel.getUser_id() : " + feedModel.getUser_id());
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
                        .EMPTY)) {
                    if (feedModel.getType().equalsIgnoreCase(Constants.KEY_IMAGES)) {
                        try {
                            if (feedModel.getMedia() != null && isFromMediaActivity)
                                bitmap = BitmapFactory.decodeFile(feedModel.getMedia());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream thumb_stream = new ByteArrayOutputStream();
                        if (bitmap != null) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, thumb_stream);
                        }
                        mBytes_array = thumb_stream.toByteArray();
                        base64 = Base64.encodeBytes(mBytes_array);
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
                                if (mBitmap != null) {
                                    camera_tumb.addView(imageView);
                                    if (camera_tumb.getChildCount() <= 0)
                                        ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
                                    else
                                        ibtn_select_image_from_gallery.setImageResource(R.drawable
                                                .color_camera);
                                }
                            }
                        }, 1000);

//                        mParams.putByteArray("picture", mBytes_array);
                        mParams.putByteArray("source", mBytes_array);

                        mParams.putString("message", feedModel.getTitle());
                        mParams.putString("description", feedModel.getDescription());

                    } else if (feedModel.getType().equalsIgnoreCase(Constants.KEY_VIDEOS)) {
                        bitmap = ThumbnailUtils.createVideoThumbnail(
                                feedModel.getMedia(), MediaStore.Video.Thumbnails.MINI_KIND);
                        ByteArrayOutputStream thumb_stream = new ByteArrayOutputStream();
                        /**
                         *
                         */


                        if (bitmap != null) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, thumb_stream);
                        }
                        byte[] ful_bytes = thumb_stream.toByteArray();

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
                            mBytes_array = bos.toByteArray();
                            base64 = Base64.encodeBytes(mBytes_array);
                            mParams.putByteArray("source", mBytes_array);
                            mParams.putString("title", feedModel.getTitle());
                            mParams.putString("description", feedModel.getDescription());
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
                                mBitmap = bitmap;
                                if (mBitmap != null) {
                                    camera_tumb.addView(imageView);
                                    if (camera_tumb.getChildCount() <= 0)
                                        ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
                                    else
                                        ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
                                }                                // Utils.closeSweetProgressDialog(this, mDialog);
                            }
                        }, 1000);

                    } else if (feedModel.getType().equalsIgnoreCase(Constants.KEY_TEXT)) {
                        if (isVideo) {
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
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SquareImageView imageView = new SquareImageView(CreatePostActivity.this);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100, 1.0f);
                                    params.setMargins(5, 10, 5, 10);
                                    imageView.setLayoutParams(params);
                                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    imageView.setImageBitmap(bitmap);
                                    mBitmap = bitmap;
                                    if (mBitmap != null) {

                                        camera_tumb.addView(imageView);
                                        if (camera_tumb.getChildCount() <= 0)
                                            ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
                                        else
                                            ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
                                    }
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
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, thumb_stream);
                            }
                            byte[] ful_bytes = thumb_stream.toByteArray();
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
                                    if (mBitmap != null) {
                                        camera_tumb.addView(imageView);
                                        if (camera_tumb.getChildCount() <= 0)
                                            ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
                                        else
                                            ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);

                                    }
                                }
                            }, 1000);
                            mParams.putString(Constants.TITLE, feedModel.getTitle());
                            mParams.putString(APIsConstants.KEY_MESSAGE, feedModel.getTitle());
                            mParams.putString(Constants.DESCRIPTION, feedModel.getDescription());
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void postOnPageImage() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + MainActivity.pageId + "/photos",
                mParams,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                        Utils.debug(TAG, response.getRawResponse().toString());
                        JSONObject object = null;
                        try {
                            object = response.getJSONObject();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (object != null) {
                            try {
                                String mPageFeedId = object.getString(Constants.KEY_ID);
                                Utils.debug(TAG, "Page Feed Id : " + mPageFeedId);
                                updateFacebookFeedId(mPageFeedId, fb_share_url);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //getAllPageFeeds(pageId);
                        }
                    }
                }
        ).executeAsync();
    }

    private void postOnPageText() {

        new GraphRequest(

                AccessToken.getCurrentAccessToken(),
                "/" + MainActivity.pageId + "/feed",
                mParams,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        Utils.debug(TAG, response.toString());
                        JSONObject object = null;
                        try {
                            object = response.getJSONObject();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (object != null) {
                            try {
                                String mPageFeedId = object.getString(Constants.KEY_ID);
                                Utils.debug(TAG, "Page Feed Id : " + mPageFeedId);
                                updateFacebookFeedId(mPageFeedId, fb_share_url);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //getAllPageFeeds(pageId);
                        }
                    }
                }
        ).executeAsync();
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
    }

    /**
     * Initialize the Click Listeners
     */
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
                intent = new Intent(CreatePostActivity.this, MainActivity.class);
                intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
                Utils.setFeed(null);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.fadeout, R.anim.fadein);


                break;
            case R.id.txt_save:
                if (TextUtils.isEmpty(edit_post_title.getText().toString().trim())) {
                    edit_post_title.setError(getResources().getString(R.string.please_add_a_title));
                    return;
                }
                if (TextUtils.isEmpty(edit_write_post.getText().toString().trim())) {
                    edit_write_post.setError(getResources().getString(R.string
                            .please_add_description));
                    return;
                }
                if (NetworkUtils.isOnline(this)) {
                    makePostFeedRequest();//postToWall();
                } else {
                    Utils.showSweetProgressDialog(this, getResources().getString(R.string.check_your_internet_connection),
                            SweetAlertDialog.WARNING_TYPE);
                }
                break;
            case R.id.ibtn_select_image_from_gallery:
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
    /******************************************************************************************
     * Posting Video On Facebook in Multiform data.                                           *
     ******************************************************************************************/
    /**
     * This method is used to call the Graph API to upload video on facebook. This method
     * initialise the Upload session for video which will further used by the transfer and finish
     * process
     */
    private void postOnPageVideos() {
        mParams.putString(APIsConstants.KEY_UPLOAD_PHASE, APIsConstants.KEY_START);
        mParams.putInt(APIsConstants.KEY_FILE_SIZE, mBytes_array.length);

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + MainActivity.pageId + "/videos",
                mParams,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        Utils.debug(TAG, "" +
                                response.getError());
                        Utils.debug(TAG, response.toString());
                        JSONObject object = null;
                        //{"video_id":"1725578134343387","start_offset":"0","mEnd_offset":"89115",
                        // "upload_session_id":"1725578137676720"}

                        try {
                            video_id = response.getJSONObject().getString(APIsConstants.VIDEO_ID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            start_offset = response.getJSONObject().getString(APIsConstants
                                    .START_OFFSET);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            mEnd_offset = response.getJSONObject().getString(APIsConstants
                                    .END_OFFSET);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            upload_session_id = response.getJSONObject().getString(APIsConstants
                                    .UPLOAD_SESSION_ID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        updateFacebookFeedId(video_id, fb_share_url);
                        transferData(video_id, start_offset, mEnd_offset, upload_session_id, getChunk(start_offset, mEnd_offset));
                    }

                }
        ).executeAsync();
    }

    /**
     * This method is used to divide the video into chunk. each time when this mwthod is called
     * it will return the chunk of video data.
     *
     * @param start_offset
     * @param end_offset
     * @return
     */
    private byte[] getChunk(String start_offset, String end_offset) {
        File f = new File(mCurrentFeedModel.getMedia());  //
        Uri imageUri = Uri.fromFile(f);
        byte[] chunk = new byte[Integer.parseInt(this.mEnd_offset)];
        try {
            chunk = readBytes(imageUri, Integer.parseInt(this.start_offset), Integer.parseInt(end_offset));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chunk;
    }


    /**
     * Transfer data to facebook server in chunks. This is calling the facebook graph API for
     * tranfering data.
     *
     * @param video_id
     * @param start_offset
     * @param end_offset
     * @param upload_session_id
     * @param chunk
     */
    private void transferData(final String video_id, final String start_offset, final String end_offset, final String
            upload_session_id, byte[] chunk) {

        mParams.putString(APIsConstants.KEY_UPLOAD_PHASE, APIsConstants.KEY_TRANSFER);
        mParams.putLong(APIsConstants.KEY_UPLOAD_SESSION_ID, Long.parseLong(upload_session_id));

        mParams.putByteArray(APIsConstants.KEY_VIDEO_FILE_CHUNK, chunk);
        mParams.putLong(APIsConstants.KEY_START_OFFSET, Long.parseLong(start_offset));
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + MainActivity.pageId + "/videos",
                mParams,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
//                        if (response.getError().getErrorType().equalsIgnoreCase("FacebookApiException"))
//                        AlertToastManager.showToast(response.getError().getErrorMessage(), CreatePostActivity.this);
                        Utils.debug(TAG, "" + response.getError());
                        Utils.debug(TAG, response.toString());
                        JSONObject object = null;
                        object = response.getJSONObject();
                        try {
                            if (!object.getString(APIsConstants.END_OFFSET).equalsIgnoreCase(object.getString(APIsConstants.START_OFFSET))) {

                                try {
                                    transferData(video_id, start_offset, end_offset, upload_session_id, getChunk(object.getString(APIsConstants.START_OFFSET), object.getString(APIsConstants
                                            .START_OFFSET)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                finishTransfer(upload_session_id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
        ).executeAsync();
    }

    /**
     * This method is used to finish the video data transfer. This will require the
     * upload_session_id to finish the upload process.
     *
     * @param upload_session_id
     */
    private void finishTransfer(String upload_session_id) {
        mParams.putString(APIsConstants.KEY_UPLOAD_PHASE, APIsConstants.KEY_FINISH);
        mParams.putLong(APIsConstants.KEY_UPLOAD_SESSION_ID, Long.parseLong(upload_session_id));
        //mParams.putLong("start_offset", Long.parseLong(start_offset));
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + MainActivity.pageId + "/videos",
                mParams,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
//                        if (response.getError().getErrorType().equalsIgnoreCase("FacebookApiException"))
//                        AlertToastManager.showToast(response.getError().getErrorMessage(), CreatePostActivity.this);
                        Utils.debug(TAG, "" +
                                response.getError());
                        Utils.debug(TAG, response.toString());
                        JSONObject object = null;
                        object = response.getJSONObject();
                        try {
                            boolean isSuccess = object.getBoolean(APIsConstants.SUCCESS);
                            if (!isSuccess) {
                                AlertToastManager.showToast(getResources().getString(R.string
                                                .upload_error),
                                        CreatePostActivity.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
        ).executeAsync();
    }

    /**
     * Get Video Chunk for transfer
     *
     * @param uri
     * @param start_offset
     * @param end_offset
     * @return
     * @throws IOException
     */
    public byte[] readBytes(Uri uri, int start_offset, int end_offset) throws IOException {
        // this dynamically extends to take the bytes you read
        InputStream inputStream = getContentResolver().openInputStream(uri);

        // this is storage overwritten on each iteration with bytes
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        int bufferSize = Integer.parseInt(mEnd_offset);
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = end_offset;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, start_offset, len);
        }
        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
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
            if (camera_tumb.getChildCount() <= 0) {
                ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
            } else {
                ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
            }
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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    /**
     * Make Request for saving feed on Server. Web Service name :"/saveFeeds"
     *
     * @return
     */
    private boolean makePostFeedRequest() {
        mDialog = Utils.showSweetProgressDialog(CreatePostActivity.this,
                getResources
                        ().getString(R.string.posting_feed), SweetAlertDialog.PROGRESS_TYPE);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_SAVE_FEEDS,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.debug(TAG, response.toString());
                        //  Utils.closeSweetProgressDialog(CreatePostActivity.this, mDialog);
                        res = response.toString();
                        try {
                            setFeedData(res);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeSweetProgressDialog(CreatePostActivity.this, mDialog);
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
                params.put(APIsConstants.KEY_TITLE, StringEscapeUtils.escapeJava(title));
                params.put(APIsConstants.KEY_DESCRIPTION, StringEscapeUtils.escapeJava(description));
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

    private void setFeedData(String res) throws JSONException {
        //Utils.debug(TAG, "Response : AFTER fEED CREATE " + res);
        try {
            JSONObject jsonObject = new JSONObject(res);
            if (jsonObject != null) {
                if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
                    try {
                        Utils.debug(TAG, jsonObject.getString(APIsConstants.KEY_MESSAGE));
//                        mDialog = Utils.showSweetProgressDialog(CreatePostActivity.this,
//                                getResources().getString(R.string.feed_saved_successfully),
//                                SweetAlertDialog.SUCCESS_TYPE);
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
                        postOnFacebookPage();
//                        mDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                Utils.closeSweetProgressDialog(CreatePostActivity.this, mDialog);
//                                AddMediaActivity.videoSelectedPosition = -1;
//                                AddMediaActivity.imageSelectedPosition = -1;
//                                AddMediaActivity.textSelectedPosition = -1;
//                                finish();
//                                intent = new Intent(CreatePostActivity.this, MainActivity.class);
//                                intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
//                                Utils.setFeed(null);
//                                startActivity(intent);
//
//                            }
//                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (jsonObject.has(Constants.LOGGED_IN)) {
                    logout();
                    LoginManager.getInstance().logOut();
                } else if (jsonObject.getString(APIsConstants.KEY_MESSAGE).contains("User Doesn't exist")) {
                    logout();
                    LoginManager.getInstance().logOut();
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

    private void postOnFacebookPage() {
        if (mType.equalsIgnoreCase(Constants.KEY_TEXT)) {
            mParams.putString(APIsConstants.KEY_MESSAGE, edit_post_title.getText().toString().trim());
            mParams.putString(Constants.DESCRIPTION, edit_write_post.getText().toString().trim
                    ());
            postOnPageText();
        } else if (mType.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
            //mParams.putString(Constants.TITLE, edit_post_title.getText().toString().trim());
            mParams.putString(APIsConstants.KEY_MESSAGE, edit_post_title.getText().toString().trim());
            mParams.putString(Constants.DESCRIPTION, edit_post_title.getText().toString().trim()/*edit_write_post.getText().toString().trim()*/);
            postOnPageVideos();

        } else if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
            mParams.putString(APIsConstants.KEY_MESSAGE, edit_post_title.getText().toString().trim());
            mParams.putString(Constants.DESCRIPTION, edit_write_post.getText().toString().trim());
            postOnPageImage();
        }

    }

    /**
     * This method is used to delete the created feed from database.
     *
     * @return
     */
    private boolean deletePost() {

        /*
         Starting a progress Dialog...
         If second parameter is passed null then progressdialog will show (Loading...) by default if pass string such as(Searching..) then
         it will show (Searching...)
         */

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_DELETE_FEED,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.closeSweetProgressDialog(CreatePostActivity.this, mDialog);
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
                Utils.closeSweetProgressDialog(CreatePostActivity.this, mDialog);
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                res = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIsConstants.KEY_FEED_ID, mFeedId);
                params.put(APIsConstants.KEY_USER_ID, new Utils(CreatePostActivity.this)
                        .getSharedPreferences(CreatePostActivity.this, Constants
                                .KEY_USER_DETAILS, ""));
                Utils.info("params...", params.toString());
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }

    private void handleDeleteResponse(String res) {
        Utils.debug(TAG, res);
    }

    private boolean updateFacebookFeedId(final String postId, final String fbUrl) {

        mDialog = Utils.showSweetProgressDialog(CreatePostActivity.this,
                getResources
                        ().getString(R.string.posting_feed), SweetAlertDialog.PROGRESS_TYPE);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_UPDATE_FB_FEED_ID,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.debug(TAG, response.toString());
                        res = response.toString();
                        try {
                            setFBFeedData(res);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeSweetProgressDialog(CreatePostActivity.this, mDialog);
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
        mDialog = Utils.showSweetProgressDialog(CreatePostActivity.this, getResources().getString(R
                .string
                .feed_saved_successfully), SweetAlertDialog.SUCCESS_TYPE);
//        mDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//            @Override
//            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                Intent intent = new Intent(CreatePostActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//                Utils.closeSweetProgressDialog(CreatePostActivity.this, mDialog);
//
//            }
//        });
        mDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Utils.closeSweetProgressDialog(CreatePostActivity.this, mDialog);
                AddMediaActivity.videoSelectedPosition = -1;
                AddMediaActivity.imageSelectedPosition = -1;
                AddMediaActivity.textSelectedPosition = -1;
                finish();
                intent = new Intent(CreatePostActivity.this, MainActivity.class);
                intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
                Utils.setFeed(null);
                startActivity(intent);

            }
        });
    }


//    private void createPost(String title, String description) {
//        /**
//         * Client facebook
//         */
//        ShareOpenGraphObject object = null;
//        if (!fb_share_url.equalsIgnoreCase(Constants.EMPTY) && !fb_share_url.endsWith(".mp4")) {
//            object = new ShareOpenGraphObject.Builder()
//                    .putString("og:type", "Create a Post")
//                            //.putString("og:like", "post.post")
//                    .putString("og:title", title)
//                    .putString("og:description", description)
//                    .putString("og:image", fb_share_url)
//                    .build();
//        }
//        if (!fb_share_url.equalsIgnoreCase(Constants.EMPTY)) {
//            if (fb_share_url.endsWith(".mp4") || fb_share_url.endsWith(".3gp"))
//                object = new ShareOpenGraphObject.Builder()
//                        .putString("og:type", "Create a Post")
//                        .putString("og:title", title)
//                        .putString("og:description", description)
//                        .putString("og:video", fb_share_url)
//                        .build();
//        } else {
//            object = new ShareOpenGraphObject.Builder()
//                    .putString("og:type", "Create a Post")
//                            //.putString("og:like", "post.post")
//                    .putString("og:title", title)
//                    .putString("og:description", description)
//                    .build();
//        }
//
//        SharePhoto photo = new SharePhoto.Builder()
//                .setBitmap(bitmap)
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
//            public void onSuccess(final Sharer.Result result) {
//                Utils.debug(TAG, "post Id : " + result.getPostId());
////                Utils.debug(TAG, "fbUrl : " + fbUrl);
//                //fbUrl = FacebookUtils.getFeedUrl(result.getPostId());
//
//
//                if (result != null && result.getPostId() != null) {
//                    new GraphRequest(
//                            AccessToken.getCurrentAccessToken(),
//                            "/" + result.getPostId(),
//                            null,
//                            HttpMethod.GET,
//                            new GraphRequest.Callback() {
//                                public void onCompleted(GraphResponse response) {
//                                    /* handle the result */
//                                    JSONObject object = response.getJSONObject();
//                                    JSONObject jsonObject = null;
//                                    if (object != null) {
//                                        try {
//                                            jsonObject = object.getJSONObject(Constants.KEY_DATA);
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                    JSONObject urlObject1 = null;
//                                    if (jsonObject != null) {
//                                        try {
//                                            urlObject1 = jsonObject.getJSONObject(Constants.POST);
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                    if (urlObject1 != null) {
//                                        try {
//                                            String url = urlObject1.getString(Constants.KEY_URL);
//                                            if (result != null && result.getPostId() != null) {
//                                                //postToWall();
//                                                updateFacebookFeedId(result.getPostId(), url);
//                                            } else {
//                                                if (NetworkUtils.isOnline(CreatePostActivity
//                                                        .this)) {
//                                                    deletePost();
//                                                } else {
//                                                    Utils.showSweetProgressDialog
//                                                            (CreatePostActivity.this,
//                                                                    getResources().getString(R.string
//                                                                            .check_your_internet_connection), SweetAlertDialog.WARNING_TYPE);
//                                                }
//                                            }
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }
//                            }
//                    ).executeAsync();
//                } else {
//                    if (NetworkUtils.isOnline(CreatePostActivity
//                            .this)) {
//                        deletePost();
//                    } else {
//                        Utils.showSweetProgressDialog
//                                (CreatePostActivity.this,
//                                        getResources().getString(R.string
//                                                .check_your_internet_connection), SweetAlertDialog.WARNING_TYPE);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancel() {
//                Utils.debug(TAG, "Cancelled");
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Utils.debug(TAG, "error : " + error);
//            }
//        });
//
//    }

    /**
     * Calling Web Service of Logout. WebService name : "/logout"
     *
     * @return boolean
     */
    private boolean logout() {
        mDialog = Utils.showSweetProgressDialog(CreatePostActivity.this,
                getResources
                        ().getString(R.string.progress_loading), SweetAlertDialog.PROGRESS_TYPE);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_LOGOUT,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.debug(TAG, response.toString());
                        Utils.closeSweetProgressDialog(CreatePostActivity.this, mDialog);
                        res = response.toString();
                        try {
                            logoutResponse(res);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }, new volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeSweetProgressDialog(CreatePostActivity.this, mDialog);
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                res = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
//                        user_id(int)
                Map<String, String> params = new HashMap<String, String>();
                Utils.debug("acc", new Utils().getSharedPreferences(CreatePostActivity.this, Constants
                        .KEY_FACEBOOK_ACCESS_TOKEN, Constants.EMPTY));
//                new Utils().getSharedPreferences(CreatePostActivity.this, Constants
//                        .KEY_FACEBOOK_ACCESS_TOKEN, Constants.EMPTY);
                params.put(APIsConstants.KEY_USER_DEVICE_TOKEN, new Utils().getSharedPreferences(CreatePostActivity
                        .this, Constants
                        .KEY_FACEBOOK_ACCESS_TOKEN, Constants.EMPTY));
                Utils.info("params...", params.toString());
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }

    /**
     * Handle the response from the logut Web Service
     *
     * @param res
     * @throws JSONException
     */
    private void logoutResponse(String res) throws JSONException {
        JSONObject jsonObject = new JSONObject(res);
        if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
            new Utils(CreatePostActivity
                    .this).setPreferences
                    (CreatePostActivity.this, Constants
                            .KEY_IS_LOGGED_IN, false);
            new Utils(this).clearSharedPreferences(this);
            finish();
            Intent intent = new Intent(CreatePostActivity.this, SplashActivity.class);
            intent.setAction(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        } else if (jsonObject.getString(APIsConstants.KEY_MESSAGE).contains("User does not exist.")) {
            mDialog = Utils.showSweetProgressDialog(CreatePostActivity.this, jsonObject.getString
                    (APIsConstants
                            .KEY_MESSAGE), SweetAlertDialog.ERROR_TYPE);
            mDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    Session.logout(CreatePostActivity.this);
                }
            });


        }
        /* else {
            try {
                Utils.debug(Constants.API_TAG, jsonObject.getString(APIsConstants.KEY_MESSAGE));
                Utils.showSweetProgressDialog(CreatePostActivity.this, jsonObject.getString(APIsConstants
                        .KEY_MESSAGE), SweetAlertDialog.ERROR_TYPE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

    }

}
