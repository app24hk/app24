package com.capstone.app24.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.capstone.app24.R;
import com.capstone.app24.utils.AlertToastManager;

/**
 * Created by amritpal on 5/11/15.
 */
public class UserProfileActivity extends Activity {

    private EditText edit_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_profile_details);
        initializeViews();
    }

    private void initializeViews() {
     /*   edit_search = (EditText) findViewById(R.id.edit_search);
        edit_search = (EditText) findViewById(R.id.edit_search);
        edit_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    AlertToastManager.showToast("Has Focus", UserProfileActivity.this);
                } else {
                    AlertToastManager.showToast("Has No Focus", UserProfileActivity.this);
                }
            }
        });*/
/*        edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //  performSearch();
                    AlertToastManager.showToast("Edit", UserProfileActivity.this);
                    return true;
                }
                return false;
            }
        });*/
    }

}
