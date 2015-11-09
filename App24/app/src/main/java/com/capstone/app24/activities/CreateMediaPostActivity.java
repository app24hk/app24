package com.capstone.app24.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.app24.R;
import com.capstone.app24.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

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
    private ImageView ibtn_select_image_from_gallery, img_bitmap;
    private String type_post;
    final static int REQUEST_VIDEO_CAPTURED = 331;
    Uri uriVideo = null;
    private String videoimage_base64;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

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
        //camera_tumb = (LinearLayout) findViewById(R.id.camera_tumb);
        ibtn_select_image_from_gallery = (ImageView) findViewById(R.id
                .ibtn_select_image_from_gallery);
        img_bitmap = (ImageView) findViewById(R.id.image_bitmap);
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


                type_post = "video";

                intent = new Intent(
                        android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
                System.out.println("high quality");
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 12);
                startActivityForResult(intent, REQUEST_VIDEO_CAPTURED);
                //intent = new Intent("android.media.action.VIDEO_CAMERA");
                //startActivity(intent);
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
       /*     ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
            imageView.setLayoutParams(params);
            imageView.setImageBitmap(imageBitmap);
            camera_tumb.addView(imageView);*/
            img_bitmap.setImageBitmap(imageBitmap);
            ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);

        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
               /* ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
                imageView.setLayoutParams(params);
                imageView.setImageBitmap(bitmap);
                camera_tumb.addView(imageView);*/
                img_bitmap.setImageBitmap(bitmap);
                ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*if (camera_tumb.getChildCount() <= 0) {
            ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
        } else {
            ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
        }*/
      /*  if (ibtn_select_image_from_gallery.getDrawable() == null) {
            ibtn_select_image_from_gallery.setImageResource(R.drawable.camera);
        } else {
            ibtn_select_image_from_gallery.setImageResource(R.drawable.color_camera);
        }*/

        if (requestCode == REQUEST_VIDEO_CAPTURED) if (resultCode == RESULT_OK) {
            if (data != null) {
                // saveVideoFileOnActivityResult(data);
                // refreshMediaProvider(StartReport.this,
                // uriVideo.getLastPathSegment());
                uriVideo = data.getData();
                System.out.println("urivideo path is" + uriVideo.getPath());
                String videorealpath = getvideoFilePathFromUri(uriVideo);
                Log.e("videoPath", videorealpath);
                Log.e("URICIDEO", uriVideo + "");
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(
                        videorealpath, MediaStore.Video.Thumbnails.MINI_KIND);
                Log.e("BMPSSSS", "" + bitmap);

                try {
                    img_bitmap.setImageBitmap(bitmap);

                } catch (Exception e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }

                // play.setVisibility(View.VISIBLE);

                int bytesRead;
                FileInputStream imageStream = null;
                try {
                    imageStream = (FileInputStream) getContentResolver()
                            .openInputStream(uriVideo);
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
                    //video_base64 = Base64.encodeBytes(ba);
                    // Image = video_base64;
                    // Log.e("video base 64", video_base64);

                    String[] projection = {BaseColumns._ID,
                            MediaStore.MediaColumns.DATA};
                    @SuppressWarnings("deprecation")
                    Cursor cursor = managedQuery(uriVideo, projection,
                            null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor
                            .getColumnIndexOrThrow(BaseColumns._ID);
                    int id = cursor.getInt(columnIndex);
                    Log.i("path", uriVideo.toString());

                    Bitmap image1 = MediaStore.Video.Thumbnails
                            .getThumbnail(getContentResolver(), id,
                                    MediaStore.Video.Thumbnails.MICRO_KIND,
                                    new BitmapFactory.Options());
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();

                    image1.compress(Bitmap.CompressFormat.PNG, 95, bao);
                    byte[] ba1 = bao.toByteArray();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image1.compress(Bitmap.CompressFormat.PNG, 40, baos);
                    byte[] byteArray = baos.toByteArray();
                    //videoimage_base64 = Base64.encodeBytes(byteArray);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }

    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private String getvideoFilePathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
        return cursor.getString(index);
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // Check that the SDCard is mounted
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "SocialVapeBoss");
        // Create the storage directory(MyCameraVideo) if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                // output.setText("Failed to create directory MyCameraVideo.");
    /*            Toast.makeText(get,
                        "Failed to create directory SocialVapeBoss.",
                        Toast.LENGTH_LONG).show();*/
                Log.d("MyCameraVideo",
                        "Failed to create directory SocialVapeBoss.");
                return null;
            }
        }

        // Create a media file name

        // For unique file name appending current timeStamp with file name
        java.util.Date date = new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date
                .getTime());

        File mediaFile;

        if (type == MEDIA_TYPE_VIDEO) {
            // For unique video file name appending current timeStamp with file
            // name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
