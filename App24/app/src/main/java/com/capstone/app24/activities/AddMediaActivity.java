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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.capstone.app24.R;
import com.capstone.app24.bean.GalleryModel;
import com.capstone.app24.bean.OwnerDataModel;
import com.capstone.app24.custom.SquareImageView;
import com.capstone.app24.interfaces.OnNewMediaListener;
import com.capstone.app24.utils.AlertToastManager;
import com.capstone.app24.utils.AppController;
import com.capstone.app24.utils.Base64;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.InterfaceListener;
import com.capstone.app24.utils.Session;
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
    public static int textSelectedPosition = -1;
    private Intent intent;
    private String mType;
    GalleryTask galleryTask;
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
    private boolean isEditable;
    private ArrayList<GalleryModel> mGalleryList;

    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_media);
        setHeader(getResources().getString(R.string.add_media), true, false, true, true, false,
                null);
        intent = getIntent();
        if (intent != null) {
            try {
                mType = intent.getStringExtra(Constants.KEY_GALLERY_TYPE);
                Utils.debug(TAG, "" + mType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                isEditable = intent.getBooleanExtra(Constants.KEY_IS_EDITABLE, false);
                Utils.debug(TAG, "" + isEditable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        initializeView();
        setClickListeners();
        if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
            fetchGallery();
            galleryTask = new GalleryTask();
            galleryTask.execute();
        } else if (mType.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
            fetchGallery();
            galleryTask = new GalleryTask();
            galleryTask.execute();
        } else if (mType.equalsIgnoreCase(Constants.KEY_TEXT)) {
            fetchGallery();
            galleryTask = new GalleryTask();
            galleryTask.execute();
        }
        updateUI();
    }


    class GalleryTask extends AsyncTask<Void, Integer, Void> {
        String selection;

        @Override
        protected Void doInBackground(Void... params) {
            String[] columns = {MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.DATE_ADDED,
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.MIME_TYPE,
                    MediaStore.Files.FileColumns.TITLE,
            };

            if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
                selection =
                        MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
            } else if (mType.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
                selection =
                        MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

            } else if (mType.equalsIgnoreCase(Constants.KEY_TEXT)) {
                selection =
                        MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + " OR " + MediaStore
                                .Files.FileColumns.MEDIA_TYPE + "="
                                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
            }


            final String orderBy = MediaStore.Files.FileColumns.DATE_ADDED;
            Uri queryUri = MediaStore.Files.getContentUri("external");

            cursor = AddMediaActivity.this.getContentResolver().query(queryUri,
                    columns,
                    selection,
                    null, // Selection args (none).
                    orderBy + " DESC" // Sort order.
            );

            int image_column_index = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
            count = cursor.getCount();
            thumbnails = new Bitmap[count];
            arrPath = new String[count];
            typeMedia = new int[count];
            thumbnailsselection = new boolean[count];

            mGalleryList.clear();
            boolean isVideo = false;


            for (int j = 0; j < count; j++) {
                mGalleryList.add(new GalleryModel());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageAdapter.notifyDataSetChanged();
                    }
                });
            }


            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);
                int id = cursor.getInt(image_column_index);
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inSampleSize = 4;
                bmOptions.inPurgeable = true;
                int type1 = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
                int t = cursor.getInt(type1);
                if (t == 1) {
                    isVideo = false;
                    thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
                            getContentResolver(), id,
                            MediaStore.Images.Thumbnails.MINI_KIND, bmOptions);
                    GalleryModel galleryModel = new GalleryModel(id, cursor.getString
                            (dataColumnIndex), MediaStore.Images.Thumbnails
                            .getThumbnail(
                                    getContentResolver(), id,
                                    MediaStore.Images.Thumbnails.MINI_KIND, bmOptions), isVideo);
                    mGalleryList.get(i).setId(galleryModel.getId());
                    mGalleryList.get(i).setImage(galleryModel.getImage());
                    mGalleryList.get(i).setPath(galleryModel.getPath());
                    mGalleryList.get(i).setIsVideo(isVideo);
                } else if (t == 3) {
                    isVideo = true;
                    thumbnails[i] = MediaStore.Video.Thumbnails.getThumbnail(
                            getContentResolver(), id,
                            MediaStore.Video.Thumbnails.MINI_KIND, bmOptions);
                    GalleryModel galleryModel = new GalleryModel(id, cursor.getString
                            (dataColumnIndex), MediaStore.Video.Thumbnails.getThumbnail(
                            getContentResolver(), id,
                            MediaStore.Video.Thumbnails.MINI_KIND, bmOptions), isVideo);
                    mGalleryList.get(i).setId(galleryModel.getId());
                    mGalleryList.get(i).setImage(galleryModel.getImage());
                    mGalleryList.get(i).setPath(galleryModel.getPath());
                    mGalleryList.get(i).setIsVideo(isVideo);
                }


                publishProgress(1);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //  super.onProgressUpdate(values);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageAdapter.notifyDataSetChanged();
                }
            });

        }
    }


    private void fetchGallery() {

        mGalleryList = new ArrayList<GalleryModel>();


        if (mType.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
            imageAdapter = new ImageAdapter(this, mGalleryList, false);
            gridView.setAdapter(imageAdapter);
            Utils.closeSweetProgressDialog(AddMediaActivity.this, mDialog);

        } else if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
            // mGalleryList = AppController.getGalleryImageModelArrayList();
            imageAdapter = new ImageAdapter(this, mGalleryList, true);
            gridView.setAdapter(imageAdapter);
            Utils.closeSweetProgressDialog(AddMediaActivity.this, mDialog);
        } else if (mType.equalsIgnoreCase(Constants.KEY_TEXT)) {
            //  mGalleryList = AppController.getGalleryImagesAndVideoModelArrayList();
            imageAdapter = new ImageAdapter(this, mGalleryList, true);
            gridView.setAdapter(imageAdapter);
            Utils.closeSweetProgressDialog(AddMediaActivity.this, mDialog);
        }
    }

    private void updateUI() {
//
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//
//                Bundle bundle = new Bundle();
//                bundle.putString("path", view.getTag().toString());
//                Intent intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
//                intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
//                intent.putExtra(Constants.KEY_GALLERY_TYPE, mType);
//                if (isEditable)
//                    intent.putExtra(Constants.KEY_IS_EDITABLE, true);
//                intent.putExtra("come_from", "gallery");
//                intent.putExtra("gallery_bundle", bundle);
//                finish();
//
//                startActivity(intent);
//            }
//        });

    }


    private void setClickListeners() {
    }

    private void initializeView() {
        gridView = (GridView) findViewById(R.id.grid_view);
        Intent intent = getIntent();
        isFromMediaActivity = intent.getBooleanExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_add_image:
                dispatchTakePictureIntent();
                break;
            case R.id.ibtn_add_video:
                intent = new Intent(
                        android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 12);
                startActivityForResult(intent, REQUEST_VIDEO_CAPTURED);
                break;
            case R.id.ibtn_back:
                finish();
                Intent intent;
                galleryTask.cancel(true);
                cursor.close();
                if (isEditable) {
                    intent = new Intent(AddMediaActivity.this, EditPostActivity.class);
                    intent.putExtra(Constants.KEY_IS_EDITABLE, true);
                } else {
                    intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
                }
                intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
                intent.putExtra(Constants.KEY_GALLERY_TYPE, mType);
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
                Intent intent1;
                if (isEditable) {
                    intent.putExtra(Constants.KEY_IS_EDITABLE, true);
                    OwnerDataModel ownerDataModel = Session.getOwnerModel();
                    ownerDataModel.setType(mType);
                    Session.setOwnerModel(ownerDataModel);
                    intent1 = new Intent(AddMediaActivity.this, EditPostActivity.class);
                } else {
                    FeedRequestModel feedModel = Utils.getFeed();
                    feedModel.setType(mType);
                    Utils.setFeed(feedModel);
                    intent1 = new Intent(AddMediaActivity.this, CreatePostActivity.class);

                }
                intent1.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
                intent1.putExtra("media_type", Constants.KEY_IMAGES);
                intent1.putExtra(Constants.KEY_GALLERY_TYPE, mType);
                intent1.putExtra("bundle", extras);
                Bitmap image = (Bitmap) extras.get("data");
                finish();
                startActivity(intent1);
            } else {


                if (isEditable) {
                    Intent intent1;
                    OwnerDataModel ownerDataModel = Session.getOwnerModel();
                    ownerDataModel.setType(mType);
                    Session.setOwnerModel(ownerDataModel);
                    intent1 = new Intent(AddMediaActivity.this, EditPostActivity.class);
                    intent1.putExtra(Constants.KEY_IS_EDITABLE, true);
                    intent1.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
                    intent1.putExtra("media_type", Constants.KEY_IMAGES);
                    intent1.putExtra(Constants.KEY_GALLERY_TYPE, mType);
                    intent1.putExtra("bundle", extras);
                    Bitmap image = (Bitmap) extras.get("data");
                    startActivity(intent1);
                    finish();
                } else {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtras(extras);
                    setResult(Activity.RESULT_OK, resultIntent);
                }
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


                    Intent intent1;
                    if (isEditable) {
                        OwnerDataModel ownerDataModel = Session.getOwnerModel();
                        ownerDataModel.setType(Constants.KEY_VIDEOS);
                        ownerDataModel.setMediaId("" + id);
                        ownerDataModel.setMedia(videorealpath);
                        Session.setOwnerModel(ownerDataModel);

                    } else {
                        FeedRequestModel feedModel = Utils.getFeed();
                        feedModel.setMedia(videorealpath);
                        feedModel.setMediaId("" + id);
                        feedModel.setType(Constants.KEY_VIDEOS);
                    }


                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image1.compress(Bitmap.CompressFormat.PNG, 40, baos);
                    byte[] byteArray = baos.toByteArray();
                    String videoimage_base64 = Base64.encodeBytes(byteArray);
                    if (isEditable) {
                        intent1 = new Intent(AddMediaActivity.this, EditPostActivity.class);
                        intent1.putExtra(Constants.KEY_IS_EDITABLE, true);
                    } else {
                        intent1 = new Intent(AddMediaActivity.this, CreatePostActivity.class);
                    }
                    intent1.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
                    intent1.putExtra("media_type", Constants.KEY_VIDEOS);
                    intent1.putExtra("capturedVideo", Constants.KEY_VIDEOS);
                    intent1.putExtra("path", videorealpath);
                    intent1.putExtra("video_base64", videoimage_base64);
                    finish();
                    startActivity(intent1);
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
        ArrayList<RelativeLayout> linearLayoutArrayList;
        ArrayList<GalleryModel> mGalleryModelsList;
        Cursor mCursor;
        boolean isImage;

        public ImageAdapter(Context localContext, ArrayList arrayList, boolean isImage) {
            context = localContext;
            mGalleryModelsList = arrayList;
            linearLayoutArrayList = new ArrayList<RelativeLayout>();
            mInflater = (LayoutInflater) localContext.getSystemService(Activity
                    .LAYOUT_INFLATER_SERVICE);
            this.isImage = isImage;
        }

        public int getCount() {
            return /*mGalleryModelsList.size();*/count;
        }

        public Object getItem(int position) {
            return mGalleryModelsList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.grid_item_view, null);
                holder = new ViewHolder();
                holder.linearLayout = (RelativeLayout) convertView.findViewById(R.id.layout_selection);
                holder.picturesView = (SquareImageView) convertView.findViewById(R.id.pictures_view);
                holder.iconView = (ImageView) convertView.findViewById(R.id.iconView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
//            holder.linearLayout
            if (position < mGalleryModelsList.size()) {

                if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
                    holder.picturesView.setImageBitmap(mGalleryModelsList.get
                            (position).getImage());
                    holder.picturesView.setTag(mGalleryModelsList.get
                            (position).getId());
                    holder.iconView.setVisibility(View.GONE);
                    //bitmapsIdImages.get(position)
                } else if (mType.equalsIgnoreCase(Constants.KEY_VIDEOS)) {
                    holder.picturesView.setImageBitmap(mGalleryModelsList.get(position).getImage());
                    try {
                        holder.picturesView.setTag(mGalleryModelsList.get(position).getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (mType.equalsIgnoreCase(Constants.KEY_TEXT)) {
                    holder.picturesView.setImageBitmap(mGalleryModelsList.get(position).getImage());
                    if (mGalleryModelsList.get(position).isVideo())
                        holder.iconView.setVisibility(View.VISIBLE);
                    else {
                        holder.iconView.setVisibility(View.GONE);
                    }
                    try {
                        holder.picturesView.setTag(mGalleryModelsList.get(position).getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                holder.picturesView.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
                } else if (mType.equalsIgnoreCase(Constants.KEY_TEXT)) {
                    if (textSelectedPosition == position) {
                        holder.linearLayout.setBackgroundColor(context.getResources().getColor(R
                                .color.colorPrimary));
                    }
                }

                finalConvertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog = Utils.showSweetProgressDialog(AddMediaActivity.this,
                                getResources().getString(R
                                        .string.progress_loading), SweetAlertDialog.PROGRESS_TYPE);
                        finish();
                        closeCursor();
                        Utils.debug(TAG, "mGalleryModelsList.get(position).getImage() : " + mGalleryModelsList.get(position).getImage());
                        if (mGalleryModelsList.get(position).getImage() != null) {

                            if (mType.equalsIgnoreCase(Constants.KEY_IMAGES)) {
                                ViewHolder holder1 = (ViewHolder) finalConvertView.getTag();
                                if (imageSelectedPosition == position) {
                                    for (RelativeLayout linearLayout : linearLayoutArrayList) {
                                        linearLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                                    }
                                    imageSelectedPosition = -1;
                                    mType = Constants.KEY_IMAGES;
                                } else {

                                    holder1.linearLayout.setBackgroundColor(context.getResources().getColor(R
                                            .color.colorPrimary));
                                    imageSelectedPosition = position;
                                    if (isEditable) {
                                        OwnerDataModel ownerDataModel = Session.getOwnerModel();
                                        ownerDataModel.setMedia(mGalleryModelsList.get(position).getPath());
                                        ownerDataModel.setMediaId("" + mGalleryModelsList.get(position).getId());
                                        ownerDataModel.setType(mType);
                                        Session.setOwnerModel(ownerDataModel);
                                    } else {
                                        FeedRequestModel feedModel = Utils.getFeed();
                                        feedModel.setMedia(mGalleryModelsList.get(position).getPath());
                                        feedModel.setMediaId("" + mGalleryModelsList.get(position).getId());
                                        feedModel.setType(mType);
                                        Utils.setFeed(feedModel);

                                    }
                                    Bundle bundle = new Bundle();
                                    bundle.putString("path", holder1.picturesView.getTag() + "");
                                    Intent intent;
                                    if (isEditable) {
                                        intent = new Intent(AddMediaActivity.this, EditPostActivity.class);
                                        intent.putExtra(Constants.KEY_IS_EDITABLE, true);
                                    } else {
                                        intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
                                    }
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
                                    for (RelativeLayout linearLayout : linearLayoutArrayList) {
                                        linearLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                                    }
                                    videoSelectedPosition = -1;
                                    mType = Constants.KEY_VIDEOS;
                                } else {
                                    holder1.linearLayout.setBackgroundColor(context.getResources().getColor(R
                                            .color.colorPrimary));
                                    videoSelectedPosition = position;

                                    if (isEditable) {
                                        OwnerDataModel ownerDataModel = Session.getOwnerModel();
                                        ownerDataModel.setMedia(mGalleryModelsList.get(position).getPath());
                                        ownerDataModel.setMediaId("" + mGalleryModelsList.get(position).getId());
                                        ownerDataModel.setType(mType);
                                        Session.setOwnerModel(ownerDataModel);
                                    } else {
                                        FeedRequestModel feedModel = Utils.getFeed();
                                        feedModel.setMedia(mGalleryModelsList.get(position).getPath());
                                        feedModel.setMediaId("" + mGalleryModelsList.get(position).getId());
                                        feedModel.setType(mType);
                                        Utils.setFeed(feedModel);

                                    }

                                    Bundle bundle = new Bundle();
                                    bundle.putString("path", holder1.picturesView.getTag().toString());
                                    Intent intent;
                                    if (isEditable) {
                                        intent = new Intent(AddMediaActivity.this, EditPostActivity.class);
                                        intent.putExtra(Constants.KEY_IS_EDITABLE, true);
                                    } else {
                                        intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
                                    }
                                    intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
                                    intent.putExtra(Constants.KEY_GALLERY_TYPE, mType);
                                    intent.putExtra("come_from", "gallery");
                                    intent.putExtra("gallery_bundle", bundle);
                                    finish();
                                    startActivity(intent);
                                }
                                Utils.closeSweetProgressDialog(AddMediaActivity.this, mDialog);
                            } else if (mType.equalsIgnoreCase(Constants.KEY_TEXT)) {
                                ViewHolder holder1 = (ViewHolder) finalConvertView.getTag();
                                mDialog = Utils.showSweetProgressDialog(AddMediaActivity.this, getResources()
                                        .getString(R
                                                .string
                                                .progress_loading), SweetAlertDialog.PROGRESS_TYPE);
                                if (textSelectedPosition == position) {
                                    for (RelativeLayout linearLayout : linearLayoutArrayList) {
                                        linearLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                                    }
                                    textSelectedPosition = -1;
                                    mType = Constants.KEY_TEXT;
                                } else {
                                    holder1.linearLayout.setBackgroundColor(context.getResources().getColor(R
                                            .color.colorPrimary));
                                    textSelectedPosition = position;


                                    Bundle bundle = new Bundle();
                                    bundle.putString("path", holder1.picturesView.getTag().toString());
                                    Intent intent;
                                    if (isEditable) {
                                        intent = new Intent(AddMediaActivity.this, EditPostActivity.class);
                                        intent.putExtra(Constants.KEY_IS_EDITABLE, true);
                                    } else {
                                        intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);
                                    }
                                    intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
                                    if (mGalleryModelsList.get(position).isVideo()) {
                                        mType = Constants.KEY_VIDEOS;
                                    } else {
                                        mType = Constants.KEY_IMAGES;
                                    }
                                    if (isEditable) {
                                        OwnerDataModel ownerDataModel = Session.getOwnerModel();
                                        ownerDataModel.setMedia(mGalleryModelsList.get(position).getPath());
                                        ownerDataModel.setMediaId("" + mGalleryModelsList.get(position).getId());
                                        ownerDataModel.setType(mType);
                                        Session.setOwnerModel(ownerDataModel);

                                    } else {
                                        FeedRequestModel feedModel = Utils.getFeed();
                                        feedModel.setMedia(mGalleryModelsList.get(position).getPath());
                                        feedModel.setMediaId("" + mGalleryModelsList.get(position).getId());
                                        feedModel.setType(mType);
                                        Utils.setFeed(feedModel);

                                    }
                                    intent.putExtra(Constants.KEY_GALLERY_TYPE, mType);
                                    intent.putExtra("come_from", "gallery");
                                    intent.putExtra("gallery_bundle", bundle);
                                    intent.putExtra(Constants.IS_VIDEO, mGalleryModelsList.get(position).isVideo());
                                    finish();
                                    startActivity(intent);
                                }

                            }

                        } else {
                            AlertToastManager.showToast("Loading images", AddMediaActivity.this);
                        }
                        Utils.closeSweetProgressDialog(AddMediaActivity.this, mDialog);
                    }
                });
            }
            return convertView;


        }

        class ViewHolder {
            SquareImageView picturesView;
            ImageView iconView;
            RelativeLayout linearLayout;
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent;
        closeCursor();
        if (isEditable) {
            intent = new Intent(AddMediaActivity.this, EditPostActivity.class);
            intent.putExtra(Constants.KEY_IS_EDITABLE, true);

        } else {
            intent = new Intent(AddMediaActivity.this, CreatePostActivity.class);

        }
        intent.putExtra(Constants.KEY_GALLERY_TYPE, mType);
        intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeCursor();
    }

    private void closeCursor() {
        if (cursor != null) {
            cursor.close();
        }
        if (galleryTask != null) {
            galleryTask.cancel(true);
        }
    }
}
