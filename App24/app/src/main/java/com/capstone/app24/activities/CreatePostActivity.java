package com.capstone.app24.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.app24.R;
import com.capstone.app24.utils.Utils;

import org.w3c.dom.Text;

/**
 * Created by amritpal on 5/11/15.
 */
public class CreatePostActivity extends BaseActivity {
    private static final String TAG = CreatePostActivity.class.getSimpleName();
    private LinearLayout save;
    private TextView txt_header, txt_post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        setHeader(null, true, false, false, false, false, "Post");
        initializeViews();
        setClickListeners();
        UpdateUI();


    }

    /**
     * Update UI with Data
     */
    private void UpdateUI() {
    }

    /**
     * Initialize Views for user interface
     */
    private void initializeViews() {
    }

    private void setClickListeners() {
        ibtn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                Toast.makeText(this, "Child Activity", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }
}
