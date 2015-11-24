package com.capstone.app24.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.Utils;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        setHeader(null, true, false, false, false, false, getResources().getString(R.string.post));
        initializeViews();
        setClickListeners();
        UpdateUI();


    }

    /**
     * Update UI with Data
     */
    private void UpdateUI() {
        if (AddMediaActivity.imageSelectedPosition == -1 && AddMediaActivity.videoSelectedPosition == -1) {
            camera_tumb.removeAllViews();
        }


        Intent intent = getIntent();
        edit_post_title.setText(intent.getStringExtra(Constants.POST_TITLE));
        edit_write_post.setText(intent.getStringExtra(Constants.POST_BODY));
        isFromMediaActivity = intent.getBooleanExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
        Utils.debug(TAG, "" + isFromMediaActivity);
        Intent intent1 = getIntent();
        Bundle extras = intent1.getExtras();
        Utils.debug(TAG, extras + "");
        if (intent1.hasExtra("capturedVideo")) {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(
                    intent1.getStringExtra("path"), MediaStore.Video.Thumbnails.MINI_KIND);
            SquareImageView imageView = new SquareImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100, 1.0f);
            params.setMargins(5, 10, 5, 10);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageBitmap(bitmap);
            camera_tumb.addView(imageView);
            if (camera_tumb.getChildCount() <= 0)
                ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
            else
                ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
        }


        if (extras != null) {
            Bundle bundle = extras.getBundle(Constants.KEY_BUNDLE);
            if (bundle != null) {
                Bitmap imageBitmap = (Bitmap) bundle.get(Constants.KEY_DATA);
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
            } else if (intent1 != null && intent1.hasExtra(Constants.KEY_COME_FROM)) {
                if (intent1.getStringExtra("media_type").equalsIgnoreCase(Constants.KEY_VIDEOS)) {

                    bundle = extras.getBundle(Constants.KEY_GALLERY_BUNDLE);
                    int id = Integer.parseInt(bundle.getString(Constants.KEY_PATH));
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inSampleSize = 4;
                    bmOptions.inPurgeable = true;

                    SquareImageView imageView = new SquareImageView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100, 1.0f);
                    params.setMargins(5, 10, 5, 10);
                    imageView.setLayoutParams(params);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(
                            getContentResolver(), id,
                            MediaStore.Video.Thumbnails.MINI_KIND, bmOptions);
                    imageView.setImageBitmap(bitmap);
                    camera_tumb.addView(imageView);
                    if (camera_tumb.getChildCount() <= 0)
                        ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
                    else
                        ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
                } else {
                    bundle = extras.getBundle(Constants.KEY_GALLERY_BUNDLE);
                    Uri uri = Uri.parse(bundle.getString(Constants.KEY_PATH));
                    SquareImageView imageView = new SquareImageView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100, 1.0f);
                    params.setMargins(5, 10, 5, 10);
                    imageView.setLayoutParams(params);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setImageURI(uri);
                    camera_tumb.addView(imageView);
                    if (camera_tumb.getChildCount() <= 0)
                        ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
                    else
                        ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
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

    }

    private void setClickListeners() {
        ibtn_back.setOnClickListener(this);
        ibtn_select_image_from_gallery.setOnClickListener(this);
        edit_post_title.setOnFocusChangeListener(this);
        edit_write_post.setOnFocusChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                Intent intent;
                intent = new Intent(CreatePostActivity.this, MainActivity.class);
                intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
                startActivity(intent);
                break;
            case R.id.ibtn_select_image_from_gallery:
                Intent intentLauncher = null;
                if (getIntent() != null) {
                    intentLauncher = getIntent();
                    if (intentLauncher.hasExtra("media_type")) {
                        Utils.debug(TAG, "HAS MEDIA TYPE : " + intentLauncher.getStringExtra("media_type"));
                        if (intentLauncher.getStringExtra("media_type").equalsIgnoreCase(Constants
                                .KEY_VIDEOS)) {
                            Utils.debug(TAG, "HAS MEDIA TYPE Video OK: " + intentLauncher
                                    .getStringExtra("media_type"));

                            intent = new Intent(CreatePostActivity.this, AddMediaActivity.class);
                            intent.putExtra(Constants.POST_TITLE, edit_post_title.getText()
                                    .toString().trim());
                            intent.putExtra(Constants.POST_BODY, edit_write_post.getText()
                                    .toString().trim());
                            intent.putExtra(Constants.KEY_GALLERY_TYPE, Constants.KEY_VIDEOS);
                            startActivity(intent);
                        } else if (intentLauncher.getStringExtra("media_type").equalsIgnoreCase(Constants
                                .KEY_IMAGES)) {
                            Utils.debug(TAG, "HAS MEDIA TYPE Image OK: " + intentLauncher
                                    .getStringExtra("media_type"));
                            intent = new Intent(CreatePostActivity.this, AddMediaActivity.class);
                            intent.putExtra(Constants.KEY_GALLERY_TYPE, Constants.KEY_IMAGES);
                            intent.putExtra(Constants.POST_BODY, edit_write_post.getText()
                                    .toString().trim());
                            intent.putExtra(Constants.KEY_GALLERY_TYPE, Constants.KEY_VIDEOS);
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                        }
                    } else {
                        intent = new Intent(CreatePostActivity.this, AddMediaActivity.class);
                        intent.putExtra(Constants.KEY_GALLERY_TYPE, Constants.KEY_IMAGES);
                        intent.putExtra(Constants.POST_BODY, edit_write_post.getText()
                                .toString().trim());
                        intent.putExtra(Constants.KEY_GALLERY_TYPE, Constants.KEY_VIDEOS);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.debug(TAG, "onActivityResult " + isFromMediaActivity);

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
        intent = new Intent(CreatePostActivity.this, MainActivity.class);
        intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
        AddMediaActivity.videoSelectedPosition = -1;
        AddMediaActivity.imageSelectedPosition = -1;
        startActivity(intent);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        // sv.scrollTo(0, sv.getBottom() + sv.getScrollY());
//        switch (v.getId()) {
//            case R.id.edit_write_post:
//                edit_write_post.requestFocus();
//                break;
//            case R.id.edit_post_title:X
//                edit_post_title.requestFocus();
//
//                break;
//        }
    }
}
