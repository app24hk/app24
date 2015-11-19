package com.capstone.app24.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
        setHeader(null, true, false, false, false, false, "Post");
        initializeViews();
        setClickListeners();
        UpdateUI();


    }

    /**
     * Update UI with Data
     */
    private void UpdateUI() {
        Intent intent = getIntent();
        isFromMediaActivity = intent.getBooleanExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
        Utils.debug(TAG, "" + isFromMediaActivity);
        Intent intent1 = getIntent();
        Bundle extras = intent1.getExtras();
        Utils.debug(TAG, extras + "");
        if (extras != null) {
            Bundle bundle = extras.getBundle("bundle");
            if (bundle != null) {
                Bitmap imageBitmap = (Bitmap) bundle.get("data");
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
            } else if (intent1 != null && intent1.hasExtra("come_from")) {


                bundle = extras.getBundle("gallery_bundle");
                Uri uri = Uri.parse(bundle.getString("path"));
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
                intent = new Intent(CreatePostActivity.this, AddMediaActivity.class);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
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
            Bitmap imageBitmap = (Bitmap) extras.get("data");
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
