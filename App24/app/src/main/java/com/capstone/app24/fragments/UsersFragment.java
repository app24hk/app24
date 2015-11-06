package com.capstone.app24.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.app24.R;

/**
 * Created by amritpal on 4/11/15.
 */
public class UsersFragment extends Fragment {
    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_users, container, false);

        return mView;
    }
}
