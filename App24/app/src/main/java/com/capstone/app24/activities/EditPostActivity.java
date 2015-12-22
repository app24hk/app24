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
import com.capstone.app24.bean.OwnerDataModel;
import com.capstone.app24.custom.SquareImageView;
import com.capstone.app24.utils.APIsConstants;
import com.capstone.app24.utils.AlertToastManager;
import com.capstone.app24.utils.AppController;
import com.capstone.app24.utils.Base64;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.NetworkUtils;
import com.capstone.app24.utils.Session;
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
import java.net.MalformedURLException;
import java.net.URL;
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
public class EditPostActivity extends BaseActivity implements View.OnFocusChangeListener {
    private static final String TAG = EditPostActivity.class.getSimpleName();
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
    OwnerDataModel ownerDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_create_post);
        setHeader(null, true, false, false, false, false, getResources().getString(R.string.edit_post));
        AlertToastManager.showToast("EditPostActivity", this);
        initializeViews();
        setClickListeners();
        UpdateUI();
    }


    @Override
    protected void onResume() {
        super.onResume();

        Utils.closeSweetProgressDialog(this, mDialog);

    }

    /**
     * Update UI with Data
     */
    private void UpdateUI() {

        ownerDataModel = Session.getOwnerModel();
        if (ownerDataModel != null) {
            edit_post_title.setText(ownerDataModel.getTitle());
            edit_write_post.setText(ownerDataModel.getDescription());
            mType = ownerDataModel.getType();
        }
        /**
         * geting Media Details
         */
        if (ownerDataModel.getMedia() != null && !ownerDataModel.getMedia().equalsIgnoreCase("")) {
            if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
                //Processing Image
                if (ownerDataModel.getMedia().contains("http://")) {
                    URL newurl = null;
                    try {
                        newurl = new URL(ownerDataModel.getMedia());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    try {
                        bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    base64 = getBase64(bitmap);
                    ownerDataModel.setBase64String(base64);

                } else {
                    try {
                        if (ownerDataModel.getMedia() != null)
                            bitmap = BitmapFactory.decodeFile(ownerDataModel.getMedia());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream thumb_stream = new ByteArrayOutputStream();
                    if (bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, thumb_stream);
                    }
//                byte[] ful_bytes = thumb_stream.toByteArray();
//                imageBytes = ful_bytes;
//                base64 = Base64.encodeBytes(ful_bytes);
                    base64 = getBase64(bitmap);
                    ownerDataModel.setBase64String(base64);

                }
            } else if (mType.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
                if (ownerDataModel.getMedia().contains("http://")) {
                    URL newurl = null;
                    try {
                        newurl = new URL(ownerDataModel.getThumbnail());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    try {
                        bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    base64 = getBase64(bitmap);
//                    ownerDataModel.setBase64String(base64);


                    //Uri uri=Uri.parse(ownerDataModel.getMedia());

                    int bytesRead;
                    FileInputStream imageStream = null;
                    try {

                        imageStream = new FileInputStream(ownerDataModel.getMedia());
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


                } else {
                    // Uri vidFile = Uri.parse(ownerDataModel.getMedia());
//
                    bitmap = ThumbnailUtils.createVideoThumbnail(
                            ownerDataModel.getMedia(), MediaStore.Video.Thumbnails.MINI_KIND);


                    int bytesRead;
                    FileInputStream imageStream = null;
                    try {

                        imageStream = new FileInputStream(ownerDataModel.getMedia());
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
                }


            }


//            try {
//                if (ownerDataModel.getMedia() != null && isFromMediaActivity)
//                    bitmap = BitmapFactory.decodeFile(ownerDataModel.getMedia());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            ByteArrayOutputStream thumb_stream = new ByteArrayOutputStream();
//            if (bitmap != null) {
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, thumb_stream);
//            }
//            byte[] ful_bytes = thumb_stream.toByteArray();
//            imageBytes = ful_bytes;
//            base64 = Base64.encodeBytes(ful_bytes);
//            ownerDataModel.setBase64String(base64);


            //Printing Feed Data
            OwnerDataModel ownerDataModel = Session.getOwnerModel();
            Utils.debug("feedModel", "CreatePost feedModel : " + ownerDataModel.getTitle());
            Utils.debug("feedModel", "CreatePost feedModel : " + ownerDataModel.getDescription());
            Utils.debug("feedModel", "CreatePost feedModel : " + ownerDataModel.getType());
            Utils.debug("feedModel", "CreatePost feedModel : " + ownerDataModel.getMedia());
            Utils.debug("feedModel", "CreatePost feedModel : " + ownerDataModel.getUser_id());


            //Setting Image Resource
            SquareImageView imageView = new SquareImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100, 1.0f);
            params.setMargins(5, 10, 5, 10);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageBitmap(bitmap);
            camera_tumb.addView(imageView);
            //base64 = getBase64(bitmap);
        } else {
            camera_tumb.removeAllViews();
        }

        if (camera_tumb.getChildCount() <= 0)
            ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
        else
            ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
    }

    private String getBase64(Bitmap bitmap) {
        if (ownerDataModel.getType().equalsIgnoreCase(Constants.KEY_IMAGES)) {
            ByteArrayOutputStream thumb_stream = new ByteArrayOutputStream();
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, thumb_stream);
            }
            byte[] ful_bytes = thumb_stream.toByteArray();
            imageBytes = ful_bytes;
            base64 = Base64.encodeBytes(ful_bytes);

            //ownerDataModel.setBase64String(base64);
//            SquareImageView imageView = new SquareImageView(this);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100, 1.0f);
//            params.setMargins(5, 10, 5, 10);
//            imageView.setLayoutParams(params);
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            imageView.setImageBitmap(this.bitmap);
//            mBitmap = this.bitmap;

        } else if (ownerDataModel.getType().equalsIgnoreCase(Constants.KEY_VIDEOS)) {


            Uri vidFile = Uri.parse(ownerDataModel.getMedia());

            this.bitmap = ThumbnailUtils.createVideoThumbnail(
                    ownerDataModel.getMedia(), MediaStore.Video.Thumbnails.MINI_KIND);


            int bytesRead;
            FileInputStream imageStream = null;
            try {

                imageStream = new FileInputStream(ownerDataModel.getMedia());
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
            //  Utils.debug(TAG, "base64 : " + base64);
//            SquareImageView imageView = new SquareImageView(this);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100, 1.0f);
//            params.setMargins(5, 10, 5, 10);
//            imageView.setLayoutParams(params);
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            imageView.setImageBitmap(this.bitmap);
        }
        return base64;
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
        //callbackManager = CallbackManager.Factory.create();
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
                intent = new Intent(EditPostActivity.this, MainActivity.class);
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
                mCurrentFeedModel.setUser_id(new Utils().getSharedPreferences(EditPostActivity
                        .this, Constants.KEY_USER_DETAILS, ""));
                mCurrentFeedModel.setType(mType);
                Utils.setFeed(mCurrentFeedModel);
                //
                if (mType.equalsIgnoreCase(Constants.KEY_TEXT)) {
                    finish();
                    intent = new Intent(EditPostActivity.this, AddMediaActivity.class);
                    intent.putExtra(Constants.KEY_GALLERY_TYPE, Constants.KEY_TEXT);
                    intent.putExtra(Constants.KEY_IS_EDITABLE, true);
                    startActivity(intent);
                } else if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
                    finish();
                    intent = new Intent(EditPostActivity.this, AddMediaActivity.class);
                    intent.putExtra(Constants.KEY_GALLERY_TYPE, Constants.KEY_IMAGES);
                    intent.putExtra(Constants.KEY_IS_EDITABLE, true);

                    startActivity(intent);
                } else if (mType.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
                    finish();
                    intent = new Intent(EditPostActivity.this, AddMediaActivity.class);
                    intent.putExtra(Constants.KEY_GALLERY_TYPE, Constants.KEY_VIDEOS);
                    intent.putExtra(Constants.KEY_IS_EDITABLE, true);
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
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
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
        intent = new Intent(EditPostActivity.this, MainActivity.class);
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
        final SweetAlertDialog pd = Utils.showSweetProgressDialog(EditPostActivity.this,
                getResources
                        ().getString(R.string.posting_feed), SweetAlertDialog.PROGRESS_TYPE);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_EDIT_FEED,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.debug(TAG, response.toString());
                        Utils.closeSweetProgressDialog(EditPostActivity.this, pd);
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
                Utils.closeSweetProgressDialog(EditPostActivity.this, pd);
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                res = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                String userId = new Utils().getSharedPreferences(EditPostActivity.this, Constants
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
                params.put(APIsConstants.KEY_FEED_ID, ownerDataModel.getId());
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


    private void setFeedData(String res) throws JSONException {
        //  Utils.debug(TAG, "Response : " + res);
        try {
            JSONObject jsonObject = new JSONObject(res);
            if (jsonObject != null) {
                if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
                    try {
                        Utils.debug(TAG, jsonObject.getString(APIsConstants.KEY_MESSAGE));
                        Utils.showSweetProgressDialog(EditPostActivity.this, jsonObject
                                .getString(APIsConstants.KEY_MESSAGE), SweetAlertDialog
                                .SUCCESS_TYPE);

//                        postStoryToWall();
                        finish();
                        Intent intent;
                        intent = new Intent(EditPostActivity.this, MainActivity.class);
                        intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
                        Utils.setFeed(null);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Utils.debug(TAG, jsonObject.getString(APIsConstants.KEY_MESSAGE));
                        Utils.showSweetProgressDialog(EditPostActivity.this, jsonObject
                                .getString(APIsConstants.KEY_MESSAGE), SweetAlertDialog
                                .ERROR_TYPE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                AlertToastManager.showToast("Error Occured", EditPostActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    private String getvideoFilePathFromUri(Uri uri) {
//        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToFirst();
//        int index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
//        return cursor.getString(index);
//    }
}
