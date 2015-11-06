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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.capstone.app24.R;
import com.capstone.app24.animations.AnimatorUtils;
import com.capstone.app24.fragments.HomeFragment;
import com.capstone.app24.fragments.ProfileFragment;
import com.capstone.app24.fragments.UserProfileDetailsFragment;
import com.capstone.app24.utils.AlertToastManager;
import com.ogaclejapan.arclayout.ArcLayout;

import java.util.ArrayList;
import java.util.List;

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
    //List<Fragment> mListFragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        setClickListeners();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new HomeFragment())
                .commit();

    }


    /**
     * Initialize the click listeners
     */
    private void setClickListeners() {
        btn_app_24.setOnClickListener(this);
        /*layout_home.setOnClickListener(this);
        layout_profile.setOnClickListener(this);
        btn_profile.setOnClickListener(this);
        btn_home.setOnClickListener(this);*/

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

    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment).commit();
    }
/*

    */
/**
 * Inflate the Profile section in the pager
 *//*

    private void getProfileSection() {
        Utils.debug(TAG, "Profile Clicked");
        btn_home.setImageResource(R.drawable.home_unselected);
        btn_profile.setImageResource(R.drawable.user_selected);
        pager.setAdapter(adapter_profile);
        adapter_profile.notifyDataSetChanged();
        tabs.setViewPager(pager);
    }

    */
/**
 * Inflate the Home section in the pager
 *//*

    private void getHomeSection() {
        btn_home.setImageResource(R.drawable.home_selected);
        btn_profile.setImageResource(R.drawable.user_unselected);
        pager.setAdapter(adapter_home);
        adapter_home.notifyDataSetChanged();
        tabs.setViewPager(pager);
    }
*/

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

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (v.getId() == R.id.layout_home) {
            setFragment(new HomeFragment());
            btn_home.setImageResource(R.drawable.home_selected);
            btn_profile.setImageResource(R.drawable.user_unselected);
        } else if (v.getId() == R.id.layout_profile) {
            setFragment(new UserProfileDetailsFragment());
            btn_home.setImageResource(R.drawable.home_unselected);
            btn_profile.setImageResource(R.drawable.user_selected);
        } else if (v.getId() == R.id.layout_home) {
            btn_home.setImageResource(R.drawable.home_selected);
            btn_profile.setImageResource(R.drawable.user_unselected);
        } else if (v.getId() == R.id.layout_profile) {
            setFragment(new UserProfileDetailsFragment());
            btn_home.setImageResource(R.drawable.home_unselected);
            btn_profile.setImageResource(R.drawable.user_selected);

        }
        return true;
    }
}
