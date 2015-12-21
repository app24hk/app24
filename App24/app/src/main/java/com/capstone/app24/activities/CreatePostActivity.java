package com.capstone.app24.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
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
import com.capstone.app24.utils.NetworkUtils;
import com.capstone.app24.utils.Utils;
import com.capstone.app24.webservices_model.FeedRequestModel;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareButton;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_create_post);
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        callbackManager = CallbackManager.Factory.create();
        Permission[] permissions = new Permission[]{
                Permission.USER_PHOTOS,
                Permission.EMAIL,
                Permission.PUBLISH_ACTION,
                Permission.PUBLIC_PROFILE,
                Permission.EMAIL, Permission.USER_ABOUT_ME, Permission.READ_STREAM
        };

        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId("1673090976236867")
                .setNamespace("com_capstone_app")
                .setPermissions(permissions)
                .build();
        SimpleFacebook.setConfiguration(configuration);

        setHeader(null, true, false, false, false, false, getResources().getString(R.string.post));
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
            } else {
                Utils.debug(TAG, "Intent : " + intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (intent.hasExtra(Constants.IS_FROM_MEDIA_ACTIVITY)) {
            isFromMediaActivity = intent.getBooleanExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
        }

        if (isFromMediaActivity) {
            try {
                Bundle bundle = intent.getBundleExtra(Constants.KEY_BUNDLE);
                bitmap = (Bitmap) bundle.get(Constants.KEY_DATA);

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


        if (AddMediaActivity.imageSelectedPosition == -1 && AddMediaActivity.videoSelectedPosition == -1) {
            camera_tumb.removeAllViews();
        }
        FeedRequestModel feedModel = Utils.getFeed();
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
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, thumb_stream);
                        }
                        byte[] ful_bytes = thumb_stream.toByteArray();
                        imageBytes = ful_bytes;
                        base64 = Base64.encodeBytes(ful_bytes);
                        feedModel.setBase64String(base64);
                        SquareImageView imageView = new SquareImageView(this);
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

                    } else if (feedModel.getType().equalsIgnoreCase(Constants.KEY_VIDEOS)) {


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
                        SquareImageView imageView = new SquareImageView(this);
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

        //shareButton = (ShareButton) findViewById(R.id.shareButton);
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
        //    mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Utils.debug(TAG, "onActivityResult " + isFromMediaActivity);
        //    callbackManager.onActivityResult(requestCode, resultCode, data);

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
                            //  postToWall();
                        } catch (JSONException e) {
                            //TODO Auto-generated catch block
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
                if (!mType.equalsIgnoreCase(Constants.KEY_TEXT)) {
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
    private void postToWall() {

        // set object to be shared
//        Story.StoryObject storyObject = new Story.StoryObject.Builder()
//                .setUrl("http://romkuapps.com/github/simple-facebook/object-apple.html")
//                .setNoun("post")
//                .build();
//        // set action to be done
//        Story.StoryAction storyAction = new Story.StoryAction.Builder()
//                .setAction("post")
//                        //.addProperty("taste", "sweet")
//                .build();
//
//        // build story
//        Story story = new Story.Builder()
//                .setObject(storyObject)
//                .setAction(storyAction)
//                .build();
//
//// publish
//        mSimpleFacebook.publish(story, onPublishListener);


//        Feed feed = new Feed.Builder()
//                .setMessage("Clone it out...")
//                .setName("Simple Facebook for Android")
//                .setCaption("Code less, do the same.")
//                .setDescription("The Simple Facebook library project makes the life much easier by coding less code for being able to login, publish feeds and open graph stories, invite friends and more.")
//                        //.setPicture("https://raw.github" +
//                        // ".com/sromku/android-simple-facebook/master/Refs/android_facebook_sdk_logo.png")
//                .setPicture(Utils.getFeed().getMedia())
//                .setLink("https://github.com/sromku/android-simple-facebook")
//                .build();


//        Story.StoryObject storyObject = new Story.StoryObject.Builder()
//                .setUrl("http://romkuapps.com/github/simple-facebook/object-apple.html")
//                .setNoun("food")
//                .build();
//
//// set action to be done
//        Story.StoryAction storyAction = new Story.StoryAction.Builder()
//                .setAction("eat")
//                .addProperty("taste", "sweet")
//                .build();
//
//// build story
//        Story story = new Story.Builder()
//                .setObject(storyObject)
//                .setAction(storyAction)
//                .build();

// publish
//        mSimpleFacebook.publish(story, onPublishListener);

        //create(StoryObject, OnCreateStoryObject)
        // Create an object
        Utils.debug("fb", "inside Post to wall ");

        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
//                .putString("og:type", "books.book")//613292//
//                .putString("og:title", "A Game of Thrones")
//                .putString("og:description", "In the frozen wastes to the north of Winterfell, sinister and supernatural forces are mustering.")
//                .putString("books:isbn", "0-553-57340-3")
                .putString("title", "0-553-57340-3")
                .putString("description", "0-553-57340-3")
                .build();

        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("post.create")
                .putObject("post", object)
                .build();
////        Create an action
//        Bitmap bitmap = null;
//        try {
//            bitmap = BitmapFactory.decodeFile(Utils.getFeed().getMedia());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Utils.debug("fb", "setting photo");
//        SharePhoto photo = new SharePhoto.Builder()
//                .setBitmap(bitmap)
//                .setUserGenerated(true)
//                .build();
//        Utils.debug("fb", "Setting Actions ");
//
//        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
//                .setActionType("books.reads")
//                .putObject("book", object)
//                .putPhoto("image", photo)
//                .build();//
//        Utils.debug("fb", "Setting Content ");
        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("post")
                .setAction(action)
                .build();
//        Utils.debug("fb", "Showing Dialog ");
//        shareButton.setShareContent(content);

//        ShareDialog.show(this, content);
//        Utils.debug("fb", "GOint to share feed on facebook with call back ");
        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Utils.debug("fb", "facebook Result  : " + result);
            }

            @Override
            public void onCancel() {
                Utils.debug("fb", "facebook Cancel  : ");
            }

            @Override
            public void onError(FacebookException error) {
                Utils.debug("fb", "facebook Error  : " + error);
            }
        });
//

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
        Utils.debug(TAG, "Response : " + res);
        try {
            JSONObject jsonObject = new JSONObject(res);
            if (jsonObject != null) {
                if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
                    try {
                        Utils.debug(TAG, jsonObject.getString(APIsConstants.KEY_MESSAGE));
                        mDialog = Utils.showSweetProgressDialog(CreatePostActivity.this, jsonObject
                                .getString(APIsConstants.KEY_MESSAGE), SweetAlertDialog
                                .SUCCESS_TYPE);

                        // postStoryToWall();
                        mDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                finish();
                                Intent intent;
                                intent = new Intent(CreatePostActivity.this, MainActivity.class);
                                intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
                                Utils.setFeed(null);
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

//        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
//                .putString("com_capstone_app:title", "0-553-57340-3")
//                .putString("com_capstone_app:description", "0-553-57340-3")
//                        //.putString("og:type", "post.post")//613292//
//                .build();
//
//        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
//                .setActionType("post.create")
//                .putObject("post", object)
//                .build();
//
//        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
//                .setPreviewPropertyName("post")
//                .setAction(action)
//                .build();
//        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
//            @Override
//            public void onSuccess(Sharer.Result result) {
//                Utils.debug("fb", "facebook Result  : " + result);
//            }
//
//            @Override
//            public void onCancel() {
//                Utils.debug("fb", "facebook Cancel  : ");
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Utils.debug("fb", "facebook Error  : " + error);
//            }
//        });

        shareButton = (ShareButton) findViewById(R.id.shareButton);
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

        shareButton.setShareContent(content);

//
//        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
//                .putString("og:type", "fitness.course")
//                .putString("og:title", "Sample Course")
//                .putString("og:description", "This is a sample course.")
//                .putInt("fitness:duration:value", 100)
//                .putString("fitness:duration:units", "s")
//                .putInt("fitness:distance:value", 12)
//                .putString("fitness:distance:units", "km")
//                .putInt("fitness:speed:value", 5)
//                .putString("fitness:speed:units", "m/s")
//                .build();
//        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
//                .setActionType("fitness.runs")
//                .putObject("fitness:course", object)
//                .build();
//        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
//                .setPreviewPropertyName("fitness:course")
//                .setAction(action)
//                .build();
//        shareButton.setShareContent(content);
//        shareButton.callOnClick();


//        shareButton.performClick();
//        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
//            @Override
//            public void onSuccess(Sharer.Result result) {
//                Utils.debug("fb", "facebook Result  : " + result);
//            }
//
//            @Override
//            public void onCancel() {
//                Utils.debug("fb", "facebook Cancel  : ");
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Utils.debug("fb", "facebook Error  : " + error);
//            }
//        });

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


        /*Feed feed = new Feed.Builder()
//                .setMessage(title)
                .setName(title)
//                .setCaption(description)
                .setDescription(description)
                .setPicture("http://dev614.trigma.us/24app/development/assets/images/uploads/feeds/feed_media1449576680_.jpg")
//                .setLink("https://www.facebook.com/games/?app_id=1673090976236867&fb_object_id=178413342510329&fb_action_ids=1705467516354449&fb_action_types=books.reads&pnref=story")
                        //.addAction("Clone", "https://github.com/sromku/android-simple-facebook")
                        //.addProperty("Full documentation", "http://sromku.github" +
                        //".io/android-simple-facebook", "http://sromku.github.io/android-simple-facebook")
                        // .addProperty("Stars", "14")

                .build();*/

    }

//    private String getvideoFilePathFromUri(Uri uri) {
//        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToFirst();
//        int index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
//        return cursor.getString(index);
//    }
}
