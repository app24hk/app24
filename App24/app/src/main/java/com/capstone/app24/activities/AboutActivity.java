package com.capstone.app24.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.capstone.app24.R;

/**
 * Created by amritpal on 6/11/15.
 */
public class AboutActivity extends Activity implements View.OnClickListener {
    private ImageButton ibtn_back;
    private TextView txt_activity_header;

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
        txt_activity_header = (TextView) findViewById(R.id.txt_activity_header);
    }

    private void setClickListeners() {
        ibtn_back.setOnClickListener(this);
    }

    private void updateUI() {
        txt_activity_header.setText(getResources().getString(R.string.about));
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
