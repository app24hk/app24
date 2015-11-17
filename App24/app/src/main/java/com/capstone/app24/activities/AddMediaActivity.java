package com.capstone.app24.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.capstone.app24.R;
import com.capstone.app24.custom.SquareImageView;
import com.capstone.app24.utils.AlertToastManager;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.Utils;

import java.io.File;

/**
 * Created by amritpal on 5/11/15.
 */
public class AddMediaActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = AddMediaActivity.class.getSimpleName();
    private GridView gridView;
    private Cursor cursor;
    private int columnIndex;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private boolean isFromMediaActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_media);
        setHeader("Add Media", true, false, true, true, false, null);
        fetchGalleryImages();
        initializeView();
        setClickListeners();
        updateUI();
    }

    private void fetchGalleryImages() {
        // Set up an array of the Thumbnail Image ID column we want
        String[] projection = {MediaStore.Images.Thumbnails._ID};
        // Create the cursor pointing to the SDCard
        cursor = managedQuery(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                MediaStore.Images.Thumbnails.IMAGE_ID);
        // Get the column index of the Thumbnails Image ID
        columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);

    }

    private void updateUI() {
        ImageAdapter imageAdapter = new ImageAdapter(this);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor myCursor = (Cursor) parent.getAdapter().getItem(position);

              //  AlertToastManager.showToast("Clicked Item : " + position, AddMediaActivity.this);
                String picturePath = myCursor.getString(columnIndex);
                AlertToastManager.showToast("Image Path : " + picturePath, AddMediaActivity.this);
                /*File file = new File(picturePath);
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                AlertToastManager.showToast("Image Bitmap : " + myBitmap, AddMediaActivity.this);

                cursor.close();*/
               /* String filePath = (String) parent.getAdapter().getItem(position);
                Utils.debug("info", "filePath:" + filePath);
                File file = new File(filePath);

                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                Utils.debug(TAG, "Image Path : " + myBitmap);*/
            }
        });
    }

    private void setClickListeners() {

    }

    private void initializeView() {
        gridView = (GridView) findViewById(R.id.grid_view);
        Intent intent = getIntent();
        isFromMediaActivity = intent.getBooleanExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
        Utils.debug(TAG, "" + isFromMediaActivity);

    }
    // Image adapter to link images to the gridview


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_add_image:
                Utils.debug(TAG, "Camera Clicked");
                dispatchTakePictureIntent();
                break;
            case R.id.ibtn_back:
                finish();
                Intent intent;
                intent = new Intent(AddMediaActivity.this, MainActivity.class);
                intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
                startActivity(intent);
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = null;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            extras = data.getExtras();
            Utils.debug(TAG, extras + "Inside OnactivityResult");
        }
        if (isFromMediaActivity) {

            Intent intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
            intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
            intent.putExtra("bundle", extras);
            finish();
            startActivity(intent);
        } else {

            Intent resultIntent = new Intent();
            resultIntent.putExtras(extras);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }


    }


    private class ImageAdapter extends BaseAdapter {
        private Context context;

        public ImageAdapter(Context localContext) {
            context = localContext;
        }

        public int getCount() {
            return cursor.getCount();
        }

        public Object getItem(int position) {
            return cursor;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            SquareImageView picturesView;
            if (convertView == null) {
                picturesView = new SquareImageView(context);
                // Move cursor to current position
                cursor.moveToPosition(position);
                // Get the current value for the requested column
                int imageID = cursor.getInt(columnIndex);
                // Set the content of the image based on the provided URI
                picturesView.setImageURI(Uri.withAppendedPath(
                        MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID));
                picturesView.setScaleType(ImageView.ScaleType.FIT_XY);
                picturesView.setPadding(5, 5, 5, 5);
                picturesView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                picturesView = (SquareImageView) convertView;
            }
            return picturesView;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent;
        intent = new Intent(AddMediaActivity.this, MainActivity.class);
        intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
        startActivity(intent);
    }
}
