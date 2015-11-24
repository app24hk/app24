package com.capstone.app24.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.capstone.app24.R;
import com.capstone.app24.custom.SquareImageView;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by amritpal on 5/11/15.
 */
public class AddMediaActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = AddMediaActivity.class.getSimpleName();
    public static final int RESULT_VIDEO_OK = 666;
    private GridView gridView;
    final static int REQUEST_VIDEO_CAPTURED = 331;
    private Cursor cursor;
    private int columnIndex;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private boolean isFromMediaActivity;
    public static int videoSelectedPosition = -1;
    public static int imageSelectedPosition = -1;
    private Intent intent;
    private String type;

    /**
     *
     */
    private int count;
    private Bitmap[] thumbnails;
    private boolean[] thumbnailsselection;
    private String[] arrPath;
    private int[] typeMedia;
    private ImageAdapter imageAdapter;
    ArrayList<Bitmap> bitmapsList = new ArrayList<>();
    ArrayList<String> bitmapsPath = new ArrayList<>();
    ArrayList<Integer> bitmapsId = new ArrayList<>();

    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_media);
        setHeader("Add Media", true, false, true, true, false, null);
        intent = getIntent();
        Utils.debug(TAG, "Intent Data  : " + intent);
        if (intent != null) {
            type = intent.getStringExtra(Constants.KEY_GALLERY_TYPE);
        }
        initializeView();
        setClickListeners();
        fetchGalleryImages(type);
        updateUI();
    }

    private void fetchGalleryImages(String type) {
        if (type.equalsIgnoreCase(Constants.KEY_IMAGES)) {
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

            imageAdapter = new ImageAdapter(this, cursor);


        } else if (type.equalsIgnoreCase(Constants.KEY_VIDEOS)) {


            String[] columns = {MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.DATE_ADDED,
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.MIME_TYPE,
                    MediaStore.Files.FileColumns.TITLE,
            };
            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
            final String orderBy = MediaStore.Files.FileColumns.DATE_ADDED;
            Uri queryUri = MediaStore.Files.getContentUri("external");

            cursor = managedQuery(queryUri,
                    columns,
                    selection,
                    null, // Selection args (none).
                    MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
            );

            int image_column_index = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
            this.count = cursor.getCount();
            this.thumbnails = new Bitmap[this.count];
            this.arrPath = new String[this.count];
            this.typeMedia = new int[this.count];
            this.thumbnailsselection = new boolean[this.count];
            for (int i = 0; i < this.count; i++) {
                cursor.moveToPosition(i);
                int id = cursor.getInt(image_column_index);
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inSampleSize = 4;
                bmOptions.inPurgeable = true;
                int type1 = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
                int t = cursor.getInt(type1);
                if (t == 1)
                    thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
                            getContentResolver(), id,
                            MediaStore.Images.Thumbnails.MINI_KIND, bmOptions);
                else if (t == 3) {
                    thumbnails[i] = MediaStore.Video.Thumbnails.getThumbnail(
                            getContentResolver(), id,
                            MediaStore.Video.Thumbnails.MINI_KIND, bmOptions);
                    bitmapsList.add(MediaStore.Video.Thumbnails.getThumbnail(
                            getContentResolver(), id,
                            MediaStore.Video.Thumbnails.MINI_KIND, bmOptions));
                    bitmapsPath.add(cursor.getString(dataColumnIndex));
                    bitmapsId.add(id);
                }
                arrPath[i] = cursor.getString(dataColumnIndex);
                typeMedia[i] = cursor.getInt(type1);
            }
            imageAdapter = new ImageAdapter(this, bitmapsList);

        }

    }

    private void updateUI() {
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Bundle bundle = new Bundle();
                bundle.putString("path", view.getTag().toString());
                Intent intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
                intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
                intent.putExtra(Constants.POST_TITLE, getIntent().getStringExtra(Constants.POST_TITLE));
                intent.putExtra(Constants.POST_BODY, getIntent().getStringExtra(Constants.POST_TITLE));
                intent.putExtra("come_from", "gallery");
                intent.putExtra("gallery_bundle", bundle);
                finish();
                startActivity(intent);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_add_image:
                Utils.debug(TAG, "Camera Clicked");
                dispatchTakePictureIntent();
                break;
            case R.id.ibtn_add_video:
                Utils.debug(TAG, "Video Clicked");

                intent = new Intent(
                        android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
                System.out.println("high quality");
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 12);
                startActivityForResult(intent, REQUEST_VIDEO_CAPTURED);
                //intent = new Intent("android.media.action.VIDEO_CAMERA");


                break;
            case R.id.ibtn_back:
                finish();
                Intent intent;
                intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
                intent.putExtra(Constants.POST_TITLE, getIntent().getStringExtra(Constants.POST_TITLE));
                intent.putExtra(Constants.POST_BODY, getIntent().getStringExtra(Constants.POST_TITLE));
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

            if (isFromMediaActivity) {

                Intent intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
                intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
                intent.putExtra("media_type", Constants.KEY_IMAGES);
                intent.putExtra("bundle", extras);
                intent.putExtra(Constants.POST_TITLE, getIntent().getStringExtra(Constants.POST_TITLE));
                intent.putExtra(Constants.POST_BODY, getIntent().getStringExtra(Constants.POST_TITLE));
                finish();
                startActivity(intent);
            } else {

                Intent resultIntent = new Intent();
                resultIntent.putExtras(extras);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        }
        if (requestCode == REQUEST_VIDEO_CAPTURED) if (resultCode == RESULT_OK) {
            if (data != null) {
                extras = data.getExtras();
                Utils.debug(TAG, "INSIDE VIDEO CAPTURED ONACTIVITY RESULT");
                // saveVideoFileOnActivityResult(data);
                // refreshMediaProvider(StartReport.this,
                // uriVideo.getLastPathSegment());
                Uri uriVideo = null;

                uriVideo = data.getData();
                System.out.println("urivideo path is" + uriVideo.getPath());
                String videorealpath = getvideoFilePathFromUri(uriVideo);
                Log.e("videoPath", videorealpath);
                Log.e("URICIDEO", uriVideo + "");
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(
                        videorealpath, MediaStore.Video.Thumbnails.MINI_KIND);
                Log.e("BMPSSSS", "" + bitmap);
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("path", videorealpath);
//                setResult(RESULT_VIDEO_OK, resultIntent);
//                finish();
                Intent intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
                intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
                intent.putExtra("media_type", Constants.KEY_VIDEOS);
                intent.putExtra("capturedVideo", Constants.KEY_VIDEOS);
                intent.putExtra(Constants.POST_TITLE, getIntent().getStringExtra(Constants.POST_TITLE));
                intent.putExtra(Constants.POST_BODY, getIntent().getStringExtra(Constants.POST_TITLE));
                intent.putExtra("path", videorealpath);
                finish();
                startActivity(intent);


//
//                try {
//                    img_bitmap.setImageBitmap(bitmap);
//
//                } catch (Exception e2) {
//                    // TODO Auto-generated catch block
//                    e2.printStackTrace();
//                }

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

    private String getvideoFilePathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
        return cursor.getString(index);
    }

    private class ImageAdapter extends BaseAdapter {
        private Context context;
        LayoutInflater mInflater;
        ArrayList<LinearLayout> linearLayoutArrayList;
        ArrayList<Bitmap> mBitmaps;
        ArrayList<Bitmap> mBitmapsPath;
        Cursor mCursor;
        boolean isImage;

        public ImageAdapter(Context localContext, Cursor cursor) {
            context = localContext;
            mCursor = cursor;
            linearLayoutArrayList = new ArrayList<LinearLayout>();
            mInflater = (LayoutInflater) localContext.getSystemService(Activity
                    .LAYOUT_INFLATER_SERVICE);
            isImage = true;
        }

        public ImageAdapter(Context localContext, ArrayList arrayList) {
            context = localContext;
            mBitmaps = arrayList;
            linearLayoutArrayList = new ArrayList<LinearLayout>();
            mInflater = (LayoutInflater) localContext.getSystemService(Activity
                    .LAYOUT_INFLATER_SERVICE);
            isImage = false;
        }

        public int getCount() {
            if (isImage) {
                return mCursor.getCount();
            } else {
                return mBitmaps.size();
            }
        }

        public Object getItem(int position) {
            if (isImage) {
                return mCursor;

            } else {
                return mBitmaps;
            }
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.grid_item_view, null);
                holder = new ViewHolder();
                holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.layout_selection);
                holder.picturesView = (SquareImageView) convertView.findViewById(R.id.pictures_view);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            cursor.moveToPosition(position);
            // Get the current value for the requested column
            // Set the content of the image based on the provided URI
            Uri uri = null;

            if (type.equalsIgnoreCase(Constants.KEY_IMAGES)) {
                int imageID = mCursor.getInt(columnIndex);

                uri = Uri.withAppendedPath(
                        MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID);
                holder.picturesView.setImageURI(uri);
                holder.picturesView.setTag(Uri.withAppendedPath(
                        MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID));
            } else if (type.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
                holder.picturesView.setImageBitmap(mBitmaps.get(position));
                //     Utils.debug(TAG, "Video Id : " + bitmapsId.get(position));
                holder.picturesView.setTag(bitmapsId.get(position));
            }


            holder.picturesView.setScaleType(ImageView.ScaleType.FIT_XY);
//            holder.picturesView.setPadding(5, 5, 5, 5);
//            holder.picturesView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams
//                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            final View finalConvertView = convertView;
            linearLayoutArrayList.add(holder.linearLayout);
            if (type.equalsIgnoreCase(Constants.KEY_IMAGES)) {
                if (imageSelectedPosition == position) {
                    holder.linearLayout.setBackgroundColor(context.getResources().getColor(R
                            .color.colorPrimary));
                }
            } else if (type.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
                if (videoSelectedPosition == position) {
                    holder.linearLayout.setBackgroundColor(context.getResources().getColor(R
                            .color.colorPrimary));
                }
            }


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type.equalsIgnoreCase(Constants.KEY_IMAGES)) {


                        ViewHolder holder1 = (ViewHolder) finalConvertView.getTag();
                        if (imageSelectedPosition == position) {
                            for (LinearLayout linearLayout : linearLayoutArrayList) {
                                linearLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                            }
                            imageSelectedPosition = -1;
                        } /*else if (selectedPosition != -1) {
                        for (LinearLayout linearLayout : linearLayoutArrayList) {
                            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                        }
                        holder1.linearLayout.setBackgroundColor(context.getResources().getColor(R
                                .color.colorPrimary));
                        selectedPosition = position;
                        Bundle bundle = new Bundle();
                        bundle.putString("path", holder1.picturesView.getTag().toString());
                        Intent intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
                        intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
                        intent.putExtra("come_from", "gallery");
                        intent.putExtra("gallery_bundle", bundle);
                        finish();
                        startActivity(intent);
                    } */ else {

                            holder1.linearLayout.setBackgroundColor(context.getResources().getColor(R
                                    .color.colorPrimary));
                            imageSelectedPosition = position;

                            Bundle bundle = new Bundle();
                            bundle.putString("path", holder1.picturesView.getTag() + "");
                            Intent intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
                            intent.putExtra(Constants.POST_TITLE, getIntent().getStringExtra(Constants.POST_TITLE));
                            intent.putExtra(Constants.POST_BODY, getIntent().getStringExtra(Constants.POST_TITLE));
                            intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
                            intent.putExtra("come_from", "gallery");
                            intent.putExtra("media_type", Constants.KEY_IMAGES);
                            intent.putExtra("gallery_bundle", bundle);
                            finish();
                            startActivity(intent);
                        }
                    } else if (type.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
                        ViewHolder holder1 = (ViewHolder) finalConvertView.getTag();
                        if (videoSelectedPosition == position) {
                            for (LinearLayout linearLayout : linearLayoutArrayList) {
                                linearLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                            }
                            videoSelectedPosition = -1;
                        } else {
                            holder1.linearLayout.setBackgroundColor(context.getResources().getColor(R
                                    .color.colorPrimary));
                            videoSelectedPosition = position;

                            Bundle bundle = new Bundle();
                            bundle.putString("path", holder1.picturesView.getTag().toString());
                            Intent intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
                            intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
                            intent.putExtra("media_type", Constants.KEY_VIDEOS);
                            intent.putExtra(Constants.POST_TITLE, getIntent().getStringExtra(Constants.POST_TITLE));
                            intent.putExtra(Constants.POST_BODY, getIntent().getStringExtra(Constants.POST_TITLE));
                            intent.putExtra("come_from", "gallery");
                            intent.putExtra("gallery_bundle", bundle);
                            finish();
                            startActivity(intent);
                        }
                    }
                }
            });
            return convertView;


//            SquareImageView picturesView;
//            LinearLayout layoutPicturesView;
//            if (convertView == null) {
//                //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
//                //     .grid_item_view, null);
//
//                // pictures_view  =  (SquareImageView)findViewById(R.id.pic)
//                layoutPicturesView = new LinearLayout(context);
//                layoutPicturesView.setBackgroundColor(context.getResources().getColor(R.color.black));
//                picturesView = new SquareImageView(context);
//                // Move cursor to current position
//                cursor.moveToPosition(position);
//                // Get the current value for the requested column
//                int imageID = cursor.getInt(columnIndex);
//                // Set the content of the image based on the provided URI
//                picturesView.setImageURI(Uri.withAppendedPath(
//                        MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID));
//                picturesView.setTag(Uri.withAppendedPath(
//                        MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID));
//                picturesView.setScaleType(ImageView.ScaleType.FIT_XY);
//                picturesView.setPadding(5, 5, 5, 5);
//                picturesView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams
//                        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                layoutPicturesView.addView(picturesView);
//            } else {
//                picturesView = (SquareImageView) convertView;
//            }
//            return picturesView;
        }

        class ViewHolder {
            SquareImageView picturesView;
            LinearLayout linearLayout;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent;
        intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
        intent.putExtra(Constants.POST_TITLE, getIntent().getStringExtra(Constants.POST_TITLE));
        intent.putExtra(Constants.POST_BODY, getIntent().getStringExtra(Constants.POST_TITLE));
        intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
        startActivity(intent);
    }
}
