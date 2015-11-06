package com.capstone.app24.interfaces;

import android.view.View;

/**
 * Created by amritpal on 4/11/15.
 */
public interface ClickListener {
    void onLongClick(View child, int childPosition);

    void onClick(View child, int childPosition);
}
