package app24.feedbook.hk.interfaces;

import app24.feedbook.hk.bean.UserFeedModel;

import java.util.ArrayList;

/**
 * Created by amritpal on 17/12/15.
 */
public interface OnListUpdateListener {

    void onUpdateList(ArrayList<UserFeedModel> arrayList);
}
