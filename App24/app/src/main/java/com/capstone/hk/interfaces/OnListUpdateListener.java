package com.capstone.hk.interfaces;

import com.capstone.hk.bean.UserFeedModel;

import java.util.ArrayList;

/**
 * Created by amritpal on 17/12/15.
 */
public interface OnListUpdateListener {

    void onUpdateList(ArrayList<UserFeedModel> arrayList);
}
