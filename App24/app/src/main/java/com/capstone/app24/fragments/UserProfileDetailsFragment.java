package com.capstone.app24.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.capstone.app24.R;
import com.capstone.app24.activities.SettingsActivity;
import com.capstone.app24.utils.AlertToastManager;

/**
 * Created by amritpal on 6/11/15.
 */
public class UserProfileDetailsFragment extends Fragment implements View.OnClickListener {
    View mView;
    private ImageButton ibtn_setting, ibtn_search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_user_profile_details, container, false);
        initializeViews();
        setClickListeners();
        updateUI();
        return mView;
    }

    private void updateUI() {

    }

    private void setClickListeners() {
        ibtn_search.setOnClickListener(this);
        ibtn_setting.setOnClickListener(this);
    }

    private void initializeViews() {
        ibtn_search = (ImageButton) mView.findViewById(R.id.ibtn_search);
        ibtn_setting = (ImageButton) mView.findViewById(R.id.ibtn_setting);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ibtn_search:

                break;
            case R.id.ibtn_setting:
                intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
