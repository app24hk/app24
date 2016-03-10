package com.capstone.hk.utils;

import com.capstone.hk.bean.GalleryModel;
import com.capstone.hk.bean.OwnerDataModel;
import com.capstone.hk.bean.UserFeedModel;
import com.capstone.hk.interfaces.OnDeleteListener;
import com.capstone.hk.interfaces.OnListUpdateListener;
import com.capstone.hk.interfaces.OnLoadMoreListener;
import com.capstone.hk.interfaces.OnNewMediaListener;

import java.util.ArrayList;

/**
 * Created by amritpal on 18/12/15.
 */
public class InterfaceListener {
    public static OnListUpdateListener mOnListUpdateListener;
    public static OnDeleteListener mOnDeleteListener;
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


}
