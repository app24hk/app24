package com.capstone.app24.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.app24.R;
import com.capstone.app24.custom.SquareImageView;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.Utils;

import org.w3c.dom.Text;

/**
 * Created by amritpal on 5/11/15.
 */
public class CreatePostActivity extends BaseActivity {
    private static final String TAG = CreatePostActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 12;
    private LinearLayout save;
    private TextView txt_header, txt_post;
    private ImageButton ibtn_select_image_from_gallery;
    private static final int SELECT_PICTURE = 1;
    private ImageView image_bitmap;
    private LinearLayout camera_tumb;
    private boolean isFromMediaActivity;

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
            }
        }
    }

    /**
     * Initialize Views for user interface
     */
    private void initializeViews() {
        ibtn_select_image_from_gallery = (ImageButton) findViewById(R.id
                .ibtn_select_image_from_gallery);
        camera_tumb = (LinearLayout) findViewById(R.id.camera_tumb);

    }

    private void setClickListeners() {
        ibtn_back.setOnClickListener(this);
        ibtn_select_image_from_gallery.setOnClickListener(this);
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
}
