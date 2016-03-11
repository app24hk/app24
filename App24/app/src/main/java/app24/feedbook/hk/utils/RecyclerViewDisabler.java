package app24.feedbook.hk.utils;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * Created by amritpal on 16/11/15.
 */
public class RecyclerViewDisabler implements RecyclerView.OnItemTouchListener {
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return true;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
