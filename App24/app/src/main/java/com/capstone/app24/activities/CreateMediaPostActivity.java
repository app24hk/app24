package com.capstone.app24.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.app24.R;
import com.capstone.app24.utils.Utils;

import java.io.IOException;

/**
 * Created by amritpal on 5/11/15.
 */
public class CreateMediaPostActivity extends BaseActivity {
    private static final String TAG = CreateMediaPostActivity.class.getSimpleName();
    private LinearLayout save;
    private TextView txt_header, txt_post;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private int PICK_IMAGE_REQUEST = 2;
    private LinearLayout camera_tumb;
    private ImageButton ibtn_select_image_from_gallery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        setHeader(null, true, false, true, true, false, null);
        initializeViews();
        setClickListeners();
        UpdateUI();


    }

    /**
     * Update UI with Data
     */
    private void UpdateUI() {
    }

    /**
     * Initialize Views for user interface
     */
    private void initializeViews() {
        camera_tumb = (LinearLayout) findViewById(R.id.camera_tumb);
        ibtn_select_image_from_gallery = (ImageButton) findViewById(R.id
                .ibtn_select_image_from_gallery);
    }

    private void setClickListeners() {
        ibtn_back.setOnClickListener(this);
        ibtn_select_image_from_gallery.setOnClickListener(this);
        ibtn_share.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ibtn_back:
                Toast.makeText(this, "Child Activity", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.ibtn_add_image:
                Utils.debug(TAG, "Camera Clicked");
                dispatchTakePictureIntent();
                break;
            case R.id.ibtn_add_video:
                intent = new Intent("android.media.action.VIDEO_CAMERA");
                startActivity(intent);
                Toast.makeText(this, "Child Activity", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ibtn_select_image_from_gallery:
                intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
            imageView.setLayoutParams(params);
            imageView.setImageBitmap(imageBitmap);
            camera_tumb.addView(imageView);

        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
                imageView.setLayoutParams(params);
                imageView.setImageBitmap(bitmap);
                camera_tumb.addView(imageView);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (camera_tumb.getChildCount() <= 0) {
            ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
        } else {
            ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
        }
    }
}
