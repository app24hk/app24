package com.capstone.app24.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.capstone.app24.R;

/**
 * Created by amritpal on 6/11/15.
 */
public class AboutActivity extends Activity implements View.OnClickListener {
    private ImageButton ibtn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initializeViews();
        setClickListeners();
        updateUI();
    }

    private void initializeViews() {
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
    }

    private void setClickListeners() {
        ibtn_back.setOnClickListener(this);
    }

    private void updateUI() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
