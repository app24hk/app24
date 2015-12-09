package com.capstone.app24.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.capstone.app24.bean.GalleryModel;
import com.capstone.app24.custom.SquareImageView;
import com.capstone.app24.utils.AppController;
import com.capstone.app24.utils.Base64;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.Utils;
import com.capstone.app24.webservices_model.FeedRequestModel;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

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
    private String mType;

    /**
     *
     */
    private int count;
    private Bitmap[] thumbnails;
    private boolean[] thumbnailsselection;
    private String[] arrPath;
    private int[] typeMedia;
    private ImageAdapter imageAdapter;

    private SweetAlertDialog mDialog;

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
            try {
                mType = intent.getStringExtra(Constants.KEY_GALLERY_TYPE);
                Utils.debug(TAG, "" + mType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        initializeView();
        setClickListeners();
        if (new Utils(this).getSharedPreferences(this, Constants.FETCH_GALLERY_IMAGE, true)) {
            if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
                mDialog = Utils.showSweetProgressDialog(AddMediaActivity.this, getResources()
                        .getString(R
                                .string
                                .progress_fetching_images), SweetAlertDialog.PROGRESS_TYPE);
            }

        }
        if (new Utils(this).getSharedPreferences(this, Constants.FETCH_GALLERY_VIDEO, true)) {
            if (mType.equalsIgnoreCase
                    (Constants.KEY_VIDEOS)) {
                mDialog = Utils.showSweetProgressDialog(AddMediaActivity.this, getResources()
                        .getString(R
                                .string
                                .progress_fetching_videos), SweetAlertDialog.PROGRESS_TYPE);
            }
        }
        if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
            if (new Utils(this).getSharedPreferences(this, Constants.FETCH_GALLERY_IMAGE, true)) {
                new fetchGalleryData().execute();
            } else {
                fetchGalleryImages();
            }
        } else if (mType.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
            if (new Utils(this).getSharedPreferences(this, Constants.FETCH_GALLERY_VIDEO, true)) {
                new fetchGalleryData().execute();
            } else {
                fetchGalleryImages();
            }
        }
        updateUI();
    }

    class fetchGalleryData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            if (mType.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
                AppController.getInstance().fetchGalleryVideos();
            } else if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
                AppController.getInstance().fetchGalleryImages();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mType.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
                new Utils(AddMediaActivity.this).setPreferences(AddMediaActivity.this, Constants
                        .FETCH_GALLERY_VIDEO, false);
            } else if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
                new Utils(AddMediaActivity.this).setPreferences(AddMediaActivity.this, Constants
                        .FETCH_GALLERY_IMAGE, false);
            }
            fetchGalleryImages();
        }
    }

    private void fetchGalleryImages() {

        if (mType.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
            imageAdapter = new ImageAdapter(this, AppController.getGalleryVideoModelArrayList(), false);
            gridView.setAdapter(imageAdapter);
            Utils.closeSweetProgressDialog(AddMediaActivity.this, mDialog);

        } else if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
            imageAdapter = new ImageAdapter(this, AppController.getGalleryImageModelArrayList(), true);
            gridView.setAdapter(imageAdapter);
            Utils.closeSweetProgressDialog(AddMediaActivity.this, mDialog);
        }
    }

    private void updateUI() {

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Bundle bundle = new Bundle();
                bundle.putString("path", view.getTag().toString());
                Intent intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
                intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
                intent.putExtra(Constants.KEY_GALLERY_TYPE, mType);
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
                Uri uriVideo = null;

                uriVideo = data.getData();
                System.out.println("urivideo path is" + uriVideo.getPath());
                String videorealpath = getvideoFilePathFromUri(uriVideo);
                Log.e("videoPath", videorealpath);
                Log.e("URICIDEO", uriVideo + "");
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(
                        videorealpath, MediaStore.Video.Thumbnails.MINI_KIND);
                Log.e("BMPSSSS", "" + bitmap);


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
                    String video_base64 = Base64.encodeBytes(ba);
                    Log.e("video base 64", video_base64);

                    String[] projection = {BaseColumns._ID,
                            MediaStore.MediaColumns.DATA};
                    Cursor cursor = this.getContentResolver().query(uriVideo, projection,
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
                    //  video_base64

                    FeedRequestModel feedModel = Utils.getFeed();
                    feedModel.setMedia(videorealpath);
                    feedModel.setMediaId("" + id);
                    feedModel.setType(mType);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image1.compress(Bitmap.CompressFormat.PNG, 40, baos);
                    byte[] byteArray = baos.toByteArray();
                    String videoimage_base64 = Base64.encodeBytes(byteArray);
                    Intent intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
                    intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
                    intent.putExtra("media_type", Constants.KEY_VIDEOS);
                    intent.putExtra("capturedVideo", Constants.KEY_VIDEOS);
                    intent.putExtra("path", videorealpath);
                    intent.putExtra("video_base64", videoimage_base64);
                    finish();
                    startActivity(intent);
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
        ArrayList<GalleryModel> mGalleryModelsList;
        Cursor mCursor;
        boolean isImage;

        public ImageAdapter(Context localContext, ArrayList arrayList, boolean isImage) {
            context = localContext;
            mGalleryModelsList = arrayList;
            linearLayoutArrayList = new ArrayList<LinearLayout>();
            mInflater = (LayoutInflater) localContext.getSystemService(Activity
                    .LAYOUT_INFLATER_SERVICE);
            this.isImage = isImage;
        }

        public int getCount() {
            return mGalleryModelsList.size();
        }

        public Object getItem(int position) {
            return mGalleryModelsList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            Utils.debug(TAG, mGalleryModelsList.size() + "");
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

            if (position < mGalleryModelsList.size()) {
                if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
                    Utils.debug("position", "position : " + position);
                    holder.picturesView.setImageBitmap(mGalleryModelsList.get
                            (position).getImage());
                    holder.picturesView.setTag(mGalleryModelsList.get
                            (position).getId());
                    //bitmapsIdImages.get(position)
                } else if (mType.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
                    Utils.debug("position", "position : " + position);
                    holder.picturesView.setImageBitmap(mGalleryModelsList.get(position).getImage());
                    try {
                        holder.picturesView.setTag(mGalleryModelsList.get(position).getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                holder.picturesView.setScaleType(ImageView.ScaleType.FIT_XY);
                final View finalConvertView = convertView;
                linearLayoutArrayList.add(holder.linearLayout);
                if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
                    if (imageSelectedPosition == position) {
                        holder.linearLayout.setBackgroundColor(context.getResources().getColor(R
                                .color.colorPrimary));
                    }
                } else if (mType.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
                    if (videoSelectedPosition == position) {
                        holder.linearLayout.setBackgroundColor(context.getResources().getColor(R
                                .color.colorPrimary));
                    }
                }


                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
                            ViewHolder holder1 = (ViewHolder) finalConvertView.getTag();
                            if (imageSelectedPosition == position) {
                                for (LinearLayout linearLayout : linearLayoutArrayList) {
                                    linearLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                                }
                                imageSelectedPosition = -1;
                            } else {

                                holder1.linearLayout.setBackgroundColor(context.getResources().getColor(R
                                        .color.colorPrimary));
                                imageSelectedPosition = position;

                                FeedRequestModel feedModel = Utils.getFeed();
                                feedModel.setMedia(mGalleryModelsList.get(position).getPath());
                                feedModel.setMediaId("" + mGalleryModelsList.get(position).getId());
                                feedModel.setType(mType);

                                Bundle bundle = new Bundle();
                                bundle.putString("path", holder1.picturesView.getTag() + "");
                                Intent intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
                                intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
                                intent.putExtra(Constants.KEY_GALLERY_TYPE, mType);
                                intent.putExtra("come_from", "gallery");
                                intent.putExtra("gallery_bundle", bundle);
                                finish();
                                startActivity(intent);
                            }
                        } else if (mType.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
                            ViewHolder holder1 = (ViewHolder) finalConvertView.getTag();
                            mDialog = Utils.showSweetProgressDialog(AddMediaActivity.this, getResources()
                                    .getString(R
                                            .string
                                            .progress_loading), SweetAlertDialog.PROGRESS_TYPE);
                            if (videoSelectedPosition == position) {
                                for (LinearLayout linearLayout : linearLayoutArrayList) {
                                    linearLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                                }
                                videoSelectedPosition = -1;
                            } else {
                                holder1.linearLayout.setBackgroundColor(context.getResources().getColor(R
                                        .color.colorPrimary));
                                videoSelectedPosition = position;

                                FeedRequestModel feedModel = Utils.getFeed();
                                feedModel.setMedia(mGalleryModelsList.get(position).getPath());
                                feedModel.setMediaId("" + mGalleryModelsList.get(position).getId());
                                feedModel.setType(mType);

                                Bundle bundle = new Bundle();
                                bundle.putString("path", holder1.picturesView.getTag().toString());
                                Intent intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
                                intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
                                intent.putExtra(Constants.KEY_GALLERY_TYPE, mType);
                                intent.putExtra("come_from", "gallery");
                                intent.putExtra("gallery_bundle", bundle);
                                finish();
                                startActivity(intent);
                            }
                            Utils.closeSweetProgressDialog(AddMediaActivity.this, mDialog);
                        }
                    }
                });
            }
            return convertView;


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
        intent.putExtra(Constants.KEY_GALLERY_TYPE, mType);
        intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
        startActivity(intent);
    }
}
