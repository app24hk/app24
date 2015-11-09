package com.capstone.app24.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.capstone.app24.R;
import com.capstone.app24.animations.AnimatorUtils;
import com.capstone.app24.fragments.HomeFragment;
import com.capstone.app24.fragments.UserProfileDetailsFragment;
import com.capstone.app24.utils.Utils;
import com.ogaclejapan.arclayout.ArcLayout;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amritpal on 3/11/15.
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener, View.OnTouchListener {
    private static final String TAG = MainActivity.class.getSimpleName();


    private ImageButton btn_app_24;
    private Toast toast = null;
    View menuLayout;
    private ArcLayout arcLayout;
    private LinearLayout layout_home, layout_profile;
    private ImageButton btn_home, btn_profile;
    HomeFragment homeFragment;
    private FrameLayout main_frame;
    UserProfileDetailsFragment userProfileDetailsFragment;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        setClickListeners();
        setHomeFragment();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Initialize the click listeners
     */
    private void setClickListeners() {
        btn_app_24.setOnClickListener(this);
        layout_home.setOnTouchListener(this);
        layout_profile.setOnTouchListener(this);
        btn_profile.setOnTouchListener(this);
        btn_home.setOnTouchListener(this);
    }

    /**
     * Initialize the View for the user Inaterface
     */
    private void initializeViews() {
        layout_home = (LinearLayout) findViewById(R.id.layout_home);
        layout_profile = (LinearLayout) findViewById(R.id.layout_profile);
        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_profile = (ImageButton) findViewById(R.id.btn_profile);
        main_frame = (FrameLayout) findViewById(R.id.main_frame);
        // Floating Action Button Initialization
        btn_app_24 = (ImageButton) findViewById(R.id.btn_app_24);
        menuLayout = findViewById(R.id.menu_layout);
        arcLayout = (ArcLayout) findViewById(R.id.arc_layout);
        for (int i = 0, size = arcLayout.getChildCount(); i < size; i++) {
            arcLayout.getChildAt(i).setOnClickListener(this);
        }

    }


    @Override
    public void onClick(View v) {

        Intent intent;
        if (v.getId() == R.id.btn_app_24) {
            onFabClick(v);

            if (v instanceof Button) {
                showToast((Button) v);
            }
            return;
        } else if (v.getId() == R.id.btn_add_post) {
            intent = new Intent(MainActivity.this, CreatePostActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_add_image_post) {
            intent = new Intent(MainActivity.this, CreateMediaPostActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_add_video_post) {
            intent = new Intent(MainActivity.this, CreateMediaPostActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.layout_home) {
            Utils.debug(TAG, "Inside OnClick home");
            btn_home.setImageResource(R.drawable.home_selected);
            btn_profile.setImageResource(R.drawable.user_unselected);
            setHomeFragment();
            //new loaderHome().execute();
        } else if (v.getId() == R.id.layout_profile) {
            Utils.debug(TAG, "Inside OnClick profile");
            btn_home.setImageResource(R.drawable.home_unselected);
            btn_profile.setImageResource(R.drawable.user_selected);
            setUserProfileFragment();
            // new loaderPrfile().execute();

        }

    }


    /**
     * handle the Floationg Action Button Click
     *
     * @param v
     */
    private void onFabClick(View v) {
        if (v.isSelected()) {
            hideMenu();
        } else {
            showMenu();
        }
        if (v.isSelected()) {
            btn_app_24.setImageResource(R.drawable.app_button);

        } else {
            btn_app_24.setImageResource(R.drawable.btn_app_24_close);

        }
        v.setSelected(!v.isSelected());

    }

    private void showToast(Button btn) {
        if (toast != null) {
            toast.cancel();
        }

        String text = "Clicked: " + btn.getText();
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();

    }

    @SuppressWarnings("NewApi")
    private void showMenu() {
        menuLayout.setVisibility(View.VISIBLE);

        List<Animator> animList = new ArrayList<>();

        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(100);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }

    @SuppressWarnings("NewApi")
    private void hideMenu() {

        List<Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(100);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();


    }

    /**
     * Create Animation for showing te Floation Action Menu
     *
     * @param item
     * @return
     */
    private Animator createShowItemAnimator(View item) {

        float dx = btn_app_24.getX() - item.getX();
        float dy = btn_app_24.getY() - item.getY();

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        return anim;
    }

    /**
     * Create Animation for hiding te Floation Action Menu
     *
     * @param item
     * @return
     */
    private Animator createHideItemAnimator(final View item) {
        float dx = btn_app_24.getX() - item.getX();
        float dy = btn_app_24.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }


    private void setUserProfileFragment() {
        userProfileDetailsFragment = UserProfileDetailsFragment.newInstance("UserProfileFragment", 1);
        manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.main_frame, userProfileDetailsFragment);
        ft.commit();
    }

    private void setHomeFragment() {
        homeFragment = new HomeFragment();
        manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.main_frame, homeFragment);
        ft.commit();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Utils.debug(TAG, "OnTouch ");

        if (v.getId() == R.id.layout_home) {
            Utils.debug(TAG, "Inside OnClick home");
            btn_home.setImageResource(R.drawable.home_selected);
            btn_profile.setImageResource(R.drawable.user_unselected);
            setHomeFragment();
        } else if (v.getId() == R.id.layout_profile) {
            Utils.debug(TAG, "Inside OnClick profile");
            btn_home.setImageResource(R.drawable.home_unselected);
            btn_profile.setImageResource(R.drawable.user_selected);
            setUserProfileFragment();
        } else if (v.getId() == R.id.layout_home) {
            Utils.debug(TAG, "Inside OnClick home");
            btn_home.setImageResource(R.drawable.home_selected);
            btn_profile.setImageResource(R.drawable.user_unselected);
            setHomeFragment();
        } else if (v.getId() == R.id.layout_profile) {
            Utils.debug(TAG, "Inside OnClick profile");
            btn_home.setImageResource(R.drawable.home_unselected);
            btn_profile.setImageResource(R.drawable.user_selected);
            setUserProfileFragment();
        }

        return false;
    }
}
