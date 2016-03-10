package com.capstone.hk.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.capstone.hk.R;

/**
 * Created by amritpal on 5/11/15.
 */
public class UserProfileActivity extends Activity {

    private EditText edit_search;
    private ImageButton ibtn_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_profile_details);
        initializeViews();
    }

    private void initializeViews() {
        ibtn_search = (ImageButton) findViewById(R.id.edit_search);
        ibtn_search.setVisibility(View.GONE);
    }

}
