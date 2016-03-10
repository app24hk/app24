package com.capstone.hk.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.capstone.hk.R;

/**
 * Created by amritpal on 6/11/15.
 */
public class GalleryFragment extends Fragment {

    private View mView;
    private ImageView gallery_image;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_gallery, container, false);
        gallery_image = (ImageView) mView.findViewById(R.id.gallery_image);
        gallery_image.setImageResource(R.drawable.logo);
        return mView;
    }
}
