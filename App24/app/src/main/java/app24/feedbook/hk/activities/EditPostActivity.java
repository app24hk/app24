package app24.feedbook.hk.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import app24.feedbook.hk.R;
import app24.feedbook.hk.bean.OwnerDataModel;
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
import com.facebook.share.widget.ShareButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
    private Intent intent;
    private CallbackManager callbackManager;
    Bitmap bitmap = null;
    OwnerDataModel ownerDataModel;
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
        setHeader(null, true, false, false, false, false, getResources().getString(R.string.save));
        AlertToastManager.showToast("EditPostActivity", this);
        initializeViews();
        UpdateUI();
        setClickListeners();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Update UI with Data
     */
    private void UpdateUI() {


        mDialog = Utils.showSweetProgressDialog(this, getResources().getString(R.string.progress_loading),
                SweetAlertDialog.PROGRESS_TYPE);
        ownerDataModel = Session.getOwnerModel();
        if (ownerDataModel != null) {
            edit_post_title.setText(ownerDataModel.getTitle());
            edit_write_post.setText(ownerDataModel.getDescription());
            mType = ownerDataModel.getType();
        }
        if (mType.equalsIgnoreCase(Constants.KEY_TEXT)) {
            camera_tumb.removeAllViews();
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
                    mParams.putByteArray("picture", mBytes_array);
                    mParams.putString("message", ownerDataModel.getTitle());
                    mParams.putString("description", ownerDataModel.getDescription());
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
                    base64 = getBase64(bitmap);
                    ownerDataModel.setBase64String(base64);
                    mParams.putByteArray("picture", mBytes_array);
                    mParams.putString("message", ownerDataModel.getTitle());
                    mParams.putString("description", ownerDataModel.getDescription());
                }


//                if (bitmap != null) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            SquareImageView imageView = new SquareImageView
//                                    (EditPostActivity.this);
//                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100, 1.0f);
//                            params.setMargins(5, 10, 5, 10);
//                            imageView.setLayoutParams(params);
//                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                            imageView.setImageBitmap(bitmap);
//                            mBitmap = bitmap;
//                            camera_tumb.addView(imageView);
////                        if (camera_tumb.getChildCount() <= 0)
////                            ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
////                        else
////                            ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
//                        }
//                    }, 1000);
//                }
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
                        mBytes_array = bos.toByteArray();
                        base64 = Base64.encodeBytes(mBytes_array);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mParams.putByteArray("source", mBytes_array);
                    mParams.putString("title", ownerDataModel.getTitle());
                    mParams.putString("description", ownerDataModel.getDescription());

                } else {
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
                        mBytes_array = bos.toByteArray();
                        base64 = Base64.encodeBytes(mBytes_array);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mParams.putByteArray("source", mBytes_array);
                    mParams.putString("title", ownerDataModel.getTitle());
                    mParams.putString("description", ownerDataModel.getDescription());
                }
//                if (bitmap != null) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            SquareImageView imageView = new SquareImageView
//                                    (EditPostActivity.this);
//                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100, 1.0f);
//                            params.setMargins(5, 10, 5, 10);
//                            imageView.setLayoutParams(params);
//                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                            imageView.setImageBitmap(bitmap);
//                            mBitmap = bitmap;
//                            camera_tumb.addView(imageView);
////                        if (camera_tumb.getChildCount() <= 0)
////                            ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
////                        else
////                            ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
//                        }
//                    }, 1000);
//                }


            } else {
                mParams.putString(Constants.TITLE, ownerDataModel.getTitle());
                mParams.putString(APIsConstants.KEY_MESSAGE, ownerDataModel.getTitle());
                mParams.putString(Constants.DESCRIPTION, ownerDataModel.getDescription());
            }

            OwnerDataModel ownerDataModel = Session.getOwnerModel();
            Utils.debug("feedModel", "EditPostActivity feedModel : " + ownerDataModel.getTitle());
            Utils.debug("feedModel", "EditPostActivity feedModel : " + ownerDataModel.getDescription
                    ());
            Utils.debug("feedModel", "EditPostActivity feedModel : " + ownerDataModel.getType());
            Utils.debug("feedModel", "EditPostActivity feedModel : " + ownerDataModel.getMedia());
            Utils.debug("feedModel", "EditPostActivity feedModel : " + ownerDataModel.getUser_id());


//            //Setting Image Resource
//            SquareImageView imageView = new SquareImageView(this);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100, 1.0f);
//            params.setMargins(5, 10, 5, 10);
//            imageView.setLayoutParams(params);
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            imageView.setImageBitmap(bitmap);
//            camera_tumb.addView(imageView);
//            //base64 = getBase64(bitmap);
        } else {
            camera_tumb.removeAllViews();
        }
        if (bitmap != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SquareImageView imageView = new SquareImageView
                            (EditPostActivity.this);
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
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SquareImageView imageView = new SquareImageView
//                        (EditPostActivity.this);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100, 1.0f);
//                params.setMargins(5, 10, 5, 10);
//                imageView.setLayoutParams(params);
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setImageBitmap(bitmap);
//                mBitmap = bitmap;
//                camera_tumb.addView(imageView);
//                if (camera_tumb.getChildCount() <= 0)
//                    ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
//                else
//                    ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
//            }
//        }, 1000);
        if (camera_tumb.getChildCount() <= 0)
            ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
        else
            ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
        Utils.closeSweetProgressDialog(this, mDialog);
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
                mBytes_array = bos.toByteArray();
                base64 = Base64.encodeBytes(mBytes_array);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    }/******************************************************************************************
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
                        //updateFacebookFeedId(video_id, fb_share_url);
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
                                        EditPostActivity.this);
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
//                        Utils.showSweetProgressDialog(EditPostActivity.this, jsonObject
//                                .getString(APIsConstants.KEY_MESSAGE), SweetAlertDialog
//                                .SUCCESS_TYPE);
                        mDialog = Utils.showSweetProgressDialog(EditPostActivity.this,
                                getResources().getString(R.string.feed_saved_successfully),
                                SweetAlertDialog.SUCCESS_TYPE);
//                        postStoryToWall();
                        mDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Utils.closeSweetProgressDialog(EditPostActivity.this, mDialog);
                                AddMediaActivity.videoSelectedPosition = -1;
                                AddMediaActivity.imageSelectedPosition = -1;
                                AddMediaActivity.textSelectedPosition = -1;
                                finish();
                                intent = new Intent(EditPostActivity.this, MainActivity.class);
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
}
