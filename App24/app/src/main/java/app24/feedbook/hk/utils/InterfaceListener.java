package app24.feedbook.hk.utils;

import app24.feedbook.hk.bean.GalleryModel;
import app24.feedbook.hk.bean.OwnerDataModel;
import app24.feedbook.hk.bean.UserFeedModel;
import app24.feedbook.hk.interfaces.OnAdViewListener;
import app24.feedbook.hk.interfaces.OnDeleteListener;
import app24.feedbook.hk.interfaces.OnListUpdateListener;
import app24.feedbook.hk.interfaces.OnLoadMoreListener;
import app24.feedbook.hk.interfaces.OnNewMediaListener;

import java.util.ArrayList;

/**
 * Created by amritpal on 18/12/15.
 */
public class InterfaceListener {
    public static OnListUpdateListener mOnListUpdateListener;
    public static OnDeleteListener mOnDeleteListener;
    public static OnAdViewListener mOnAdViewListener;
    public static OwnerDataModel mOwnerDataModel;
    public static OnLoadMoreListener mOnLoadMoreListener;
    public static OnNewMediaListener mOnNewMediaListener;

    public static void onMediaUpdate(GalleryModel model) {
        mOnNewMediaListener.onMediaUpdate(model);
    }

    public static void setOnNewMediaListener(OnNewMediaListener listener) {
        try {
            InterfaceListener.mOnNewMediaListener = listener;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setOnListUpdateListener(OnListUpdateListener listener) {
        mOnListUpdateListener = listener;
    }

    public static void OnListUpdate(ArrayList<UserFeedModel> userFeedModelsList) {
        try {
            mOnListUpdateListener.onUpdateList(userFeedModelsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setOnDeleteListener(OnDeleteListener listener) {
        mOnDeleteListener = listener;
    }

    public static void OnDelete(String id, boolean isDelete) {
        try {
            mOnDeleteListener.onDelete(id, isDelete);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setOnAdViewListener(OnAdViewListener listener) {
        mOnAdViewListener = listener;
    }

    public static void onAdView() {
        try {
            mOnAdViewListener.onAdView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
