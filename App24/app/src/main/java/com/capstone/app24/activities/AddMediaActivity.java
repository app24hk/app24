package com.capstone.app24.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

import com.capstone.app24.R;

/**
 * Created by amritpal on 5/11/15.
 */
public class AddMediaActivity extends BaseActivity {

    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_media);
        setHeader("Add Media", true, false, true, true, false, null);
        initializeView();
        setClickListeners();
        updateUI();
    }

    private void updateUI() {

    }

    private void setClickListeners() {

    }

    private void initializeView() {
        gridView = (GridView) findViewById(R.id.grid_view);
    }
}
