package com.capstone.app24.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.capstone.app24.R;
import com.capstone.app24.utils.AlertToastManager;
import com.capstone.app24.utils.Utils;

/**
 * Created by amritpal on 6/11/15.
 */
public class SettingsActivity extends BaseActivity {

    private RelativeLayout layout_paypal, layout_about, layout_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initializeViews();
        setClickListeners();
    }

    private void initializeViews() {
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        layout_paypal = (RelativeLayout) findViewById(R.id.layout_paypal);
        layout_about = (RelativeLayout) findViewById(R.id.layout_about);
        layout_logout = (RelativeLayout) findViewById(R.id.layout_logout);
    }

    private void setClickListeners() {
        ibtn_back.setOnClickListener(this);
        layout_paypal.setOnClickListener(this);
        layout_about.setOnClickListener(this);
        layout_logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.layout_paypal:
                AlertToastManager.showToast("Paypal ", this);
                break;
            case R.id.layout_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_logout:
                AlertToastManager.showToast("Logout ", this);
                break;

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
