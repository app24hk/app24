package com.capstone.app24.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.capstone.app24.R;
import com.capstone.app24.activities.SettingsActivity;
import com.capstone.app24.utils.AlertToastManager;
import com.capstone.app24.utils.Utils;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amritpal on 6/11/15.
 */
public class UserProfileDetailsFragment extends Fragment implements View.OnClickListener {
    View mView;
    private ImageButton ibtn_setting, ibtn_search;
    SweetAlertDialog dialog;

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

    class loaderPrfile extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            dialog = Utils.showSweetProgressDialog(getActivity(),
                    getResources()
                            .getString(R.string
                                    .progress_loading));
                    /*dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Please Wait");
            dialog.show();*/
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Utils.closeSweetProgressDialog(getActivity(), dialog);
        }

        @Override
        protected Void doInBackground(Void... params) {
           /* runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setUserProfileFragment();
                }
            });*/
            return null;
        }


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

    public static UserProfileDetailsFragment newInstance(String foo, int bar) {
        UserProfileDetailsFragment f = new UserProfileDetailsFragment();
        Bundle args = new Bundle();
        args.putString("a", foo);
        args.putInt("b", bar);
        f.setArguments(args);
        return f;
    }
}
