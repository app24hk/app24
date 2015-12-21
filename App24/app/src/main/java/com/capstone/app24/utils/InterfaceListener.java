package com.capstone.app24.utils;

import com.capstone.app24.bean.OwnerDataModel;
import com.capstone.app24.bean.UserFeedModel;
import com.capstone.app24.interfaces.OnDeleteListener;
import com.capstone.app24.interfaces.OnListUpdateListener;
import com.capstone.app24.interfaces.OnLoadMoreListener;

import java.util.ArrayList;

/**
 * Created by amritpal on 18/12/15.
 */
public class InterfaceListener {
    public static OnListUpdateListener mOnListUpdateListener;
    public static OnDeleteListener mOnDeleteListener;
    public static OwnerDataModel mOwnerDataModel;
    public static OnLoadMoreListener mOnLoadMoreListener;

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
