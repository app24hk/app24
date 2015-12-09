package com.capstone.app24.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;


import com.capstone.app24.bean.GalleryModel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import volley.Request;
import volley.RequestQueue;
import volley.toolbox.ImageLoader;
import volley.toolbox.Volley;

/**
 * Created by Amrit Pal on 28/10/15.
 */

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;

//    private static ArrayList<Bitmap> bitmapsListImages = new ArrayList<>();
//    private static ArrayList<String> bitmapsPathImages = new ArrayList<>();
//    private static ArrayList<Integer> bitmapsIdImages = new ArrayList<>();
//    private static ArrayList<Bitmap> bitmapsListVideos = new ArrayList<>();
//    private static ArrayList<String> bitmapsPathVideos = new ArrayList<>();
//    private static ArrayList<Integer> bitmapsIdVideos = new ArrayList<>();

    private static ArrayList<GalleryModel> galleryImageModelArrayList = new ArrayList<>();
    private static ArrayList<GalleryModel> galleryVideoModelArrayList = new ArrayList<>();
    //private static ArrayList<Integer> bitmapsIdVideos = new ArrayList<>();
    private static Cursor cursor;
    /**
     *
     */
    private int count;
    private Bitmap[] thumbnails;
    private boolean[] thumbnailsselection;
    private String[] arrPath;
    private int[] typeMedia;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        getKeyHash();
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    public void getKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.android.euro16", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }

    public void fetchGalleryImages() {


        String[] columns = {MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE,
        };
        String selection =
                MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
//                + " OR "
//          +
//                MediaStore.Files.FileColumns.MEDIA_TYPE + "="
//                        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        final String orderBy = MediaStore.Files.FileColumns.DATE_ADDED;
        Uri queryUri = MediaStore.Files.getContentUri("external");

        cursor = this.getContentResolver().query(queryUri,
                columns,
                selection,
                null, // Selection args (none).
                orderBy + " DESC" // Sort order.
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
            if (t == 1) {
                thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
                        getContentResolver(), id,
                        MediaStore.Images.Thumbnails.MINI_KIND, bmOptions);
                GalleryModel galleryModel = new GalleryModel(id, cursor.getString
                        (dataColumnIndex), MediaStore.Images.Thumbnails
                        .getThumbnail(
                                getContentResolver(), id,
                                MediaStore.Images.Thumbnails.MINI_KIND, bmOptions));

                if (!galleryImageModelArrayList.contains(galleryModel)) {
                    galleryImageModelArrayList.add(galleryModel);
                }

            }
        }
    }

    public void fetchGalleryVideos() {


        String[] columns = {MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE,
        };
        String selection =
                MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        final String orderBy = MediaStore.Files.FileColumns.DATE_ADDED;
        Uri queryUri = MediaStore.Files.getContentUri("external");

        cursor = this.getContentResolver().query(queryUri,
                columns,
                selection,
                null, // Selection args (none).
                orderBy + " DESC" // Sort order.
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
//            if (t == 1) {
//                thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
//                        getContentResolver(), id,
//                        MediaStore.Images.Thumbnails.MINI_KIND, bmOptions);
//                GalleryModel galleryModel = new GalleryModel(id, cursor.getString
//                        (dataColumnIndex), MediaStore.Images.Thumbnails
//                        .getThumbnail(
//                                getContentResolver(), id,
//                                MediaStore.Images.Thumbnails.MINI_KIND, bmOptions));
//
//                if (!galleryModelArrayList.contains(galleryModel)) {
//                    galleryModelArrayList.add(galleryModel);
//                }
//
//            } else if (t == 3) {
//                thumbnails[i] = MediaStore.Video.Thumbnails.getThumbnail(
//                        getContentResolver(), id,
//                        MediaStore.Video.Thumbnails.MINI_KIND, bmOptions);
//                GalleryModel galleryModel = new GalleryModel(id, cursor.getString
//                        (dataColumnIndex), MediaStore.Video.Thumbnails.getThumbnail(
//                        getContentResolver(), id,
//                        MediaStore.Video.Thumbnails.MINI_KIND, bmOptions));
//
//                if (!galleryModelArrayList.contains(galleryModel)) {
//                    galleryModelArrayList.add(galleryModel);
//                }
//            }
//            else
            if (t == 3) {
                thumbnails[i] = MediaStore.Video.Thumbnails.getThumbnail(
                        getContentResolver(), id,
                        MediaStore.Video.Thumbnails.MINI_KIND, bmOptions);
                GalleryModel galleryModel = new GalleryModel(id, cursor.getString
                        (dataColumnIndex), MediaStore.Video.Thumbnails.getThumbnail(
                        getContentResolver(), id,
                        MediaStore.Video.Thumbnails.MINI_KIND, bmOptions));

                if (!galleryVideoModelArrayList.contains(galleryModel)) {
                    galleryVideoModelArrayList.add(galleryModel);
                }
            }
        }
    }
//    public static ArrayList<Bitmap> getBitmapsListImages() {
//        return bitmapsListImages;
//    }
//
//    public static void setBitmapsListImages(ArrayList<Bitmap> bitmapsListImages) {
//        AppController.bitmapsListImages = bitmapsListImages;
//    }
//
//    public static ArrayList<String> getBitmapsPathImages() {
//        return bitmapsPathImages;
//    }
//
//    public static void setBitmapsPathImages(ArrayList<String> bitmapsPathImages) {
//        AppController.bitmapsPathImages = bitmapsPathImages;
//    }
//
//    public static ArrayList<Integer> getBitmapsIdImages() {
//        return bitmapsIdImages;
//    }
//
//    public static void setBitmapsIdImages(ArrayList<Integer> bitmapsIdImages) {
//        AppController.bitmapsIdImages = bitmapsIdImages;
//    }
//
//    public static ArrayList<Bitmap> getBitmapsListVideos() {
//        return bitmapsListVideos;
//    }
//
//    public static void setBitmapsListVideos(ArrayList<Bitmap> bitmapsListVideos) {
//        AppController.bitmapsListVideos = bitmapsListVideos;
//    }
//
//    public static ArrayList<String> getBitmapsPathVideos() {
//        return bitmapsPathVideos;
//    }
//
//    public static void setBitmapsPathVideos(ArrayList<String> bitmapsPathVideos) {
//        AppController.bitmapsPathVideos = bitmapsPathVideos;
//    }
//
//    public static ArrayList<Integer> getBitmapsIdVideos() {
//        return bitmapsIdVideos;
//    }
//
//    public static void setBitmapsIdVideos(ArrayList<Integer> bitmapsIdVideos) {
//        AppController.bitmapsIdVideos = bitmapsIdVideos;
//    }

//    public static ArrayList<GalleryModel> getGalleryModelArrayList() {
//        return galleryModelArrayList;
//    }
//
//    public static void setGalleryModelArrayList(ArrayList<GalleryModel> galleryModelArrayList) {
//        AppController.galleryModelArrayList = galleryModelArrayList;
//    }

    public static ArrayList<GalleryModel> getGalleryImageModelArrayList() {
        return galleryImageModelArrayList;
    }

    public static void setGalleryImageModelArrayList(ArrayList<GalleryModel> galleryImageModelArrayList) {
        AppController.galleryImageModelArrayList = galleryImageModelArrayList;
    }

    public static ArrayList<GalleryModel> getGalleryVideoModelArrayList() {
        return galleryVideoModelArrayList;
    }

    public static void setGalleryVideoModelArrayList(ArrayList<GalleryModel> galleryVideoModelArrayList) {
        AppController.galleryVideoModelArrayList = galleryVideoModelArrayList;
    }
}
