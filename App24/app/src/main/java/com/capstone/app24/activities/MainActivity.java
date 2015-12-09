package com.capstone.app24.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.capstone.app24.R;
import com.capstone.app24.animations.AnimatorUtils;
import com.capstone.app24.fragments.HomeFragment;
import com.capstone.app24.fragments.UserProfileDetailsFragment;
import com.capstone.app24.receiver.AlarmReceiver;
import com.capstone.app24.sliding_tabs.SlidingTabLayout;
import com.capstone.app24.utils.AppController;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.Utils;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.ogaclejapan.arclayout.ArcLayout;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.entities.Story;
import com.sromku.simple.fb.listeners.OnCreateStoryObject;
import com.sromku.simple.fb.listeners.OnPublishListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;

/**
 * Created by amritpal on 3/11/15.
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener, View
        .OnTouchListener {
    private static final String TAG = MainActivity.class.getSimpleName();


    private ImageButton btn_app_24;
    private Toast toast = null;
    View menuLayout;
    private ArcLayout arcLayout;
    private RelativeLayout layout_home, layout_profile;
    private ImageButton btn_home, btn_profile;
    HomeFragment homeFragment;
    private FrameLayout main_frame;
    UserProfileDetailsFragment userProfileDetailsFragment;
    private FragmentManager manager;
    boolean isFabOpened;
    private RelativeLayout layout_tab_buttons;
    private static RelativeLayout layout;
    // private RelativeLayout animated_layout;
    InterstitialAd mInterstitialAd;
    private LoginButton fb_btn;
    public static SlidingTabLayout tabs;
    public static RelativeLayout layout_user_profle;
    private ImageButton ibtn_setting, ibtn_search;

    //Setting Alarm
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private Bitmap mIcon_val;
    private SimpleFacebook mSimpleFacebook;
    OnPublishListener onPublishListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fb_btn = (LoginButton) findViewById(R.id.login_button);
        initializeViews();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                beginPlayingGame();
            }
        });
        requestNewInterstitial();
        setClickListeners();
        setHomeFragment();
        //mInterstitialAd.show();
        // registerReceiver(AlarmReceiver.getInstance(),)
        setAlarm();

//        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//        Date past = new Date();
//        try {
//            past = format.parse("01/10/2010");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        Date now = new Date();
//        System.out.println(TimeUnit.MILLISECONDS.toMillis(now.getTime() - past.getTime()) + " milliseconds ago");
//        System.out.println(TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + " minutes ago");
//        System.out.println(TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + " hours ago");
//        System.out.println(TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " days ago");

    }

    private void beginPlayingGame() {
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();
        //Test Device Id
        // "AFA12AE3A6981A0F0745048448E82F44"
        mInterstitialAd.loadAd(adRequest);

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
        ibtn_setting.setOnClickListener(this);
        ibtn_search.setOnClickListener(this);

    }

    /**
     * Initialize the View for the user Inaterface
     */
    private void initializeViews() {

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        layout_user_profle = (RelativeLayout) findViewById(R.id.layout_user_profle);
        MainActivity.layout_user_profle.setVisibility(View.VISIBLE);
        MainActivity.tabs.setVisibility(View.VISIBLE);

        layout_home = (RelativeLayout) findViewById(R.id.layout_home);
        layout_profile = (RelativeLayout) findViewById(R.id.layout_profile);
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
        layout = (RelativeLayout) findViewById(R.id.layout);

        ibtn_search = (ImageButton) findViewById(R.id.ibtn_search);
        ibtn_setting = (ImageButton) findViewById(R.id.ibtn_setting);


        onPublishListener = new OnPublishListener() {
            @Override
            public void onComplete(String id) {
                Utils.info(TAG, "Published successfully. id = " + id);
            }
        };
    }

    private void setAlarm() {
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 30, alarmIntent);

    }


    @Override
    public void onClick(View v) {
        if (mInterstitialAd.isLoaded()) {
            //   mInterstitialAd.show();
        }


        Intent intent;
        if (v.getId() == R.id.btn_app_24)

        {
            postStory();

            // setAlarm();
            onFabClick(v);

            if (v instanceof Button) {
                showToast((Button) v);
            }

            return;
        } else if (v.getId() == R.id.btn_add_post)

        {
            finish();
            intent = new Intent(MainActivity.this, CreatePostActivity.class);
            intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, false);
            intent.putExtra(Constants.KEY_GALLERY_TYPE, Constants.KEY_TEXT);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_add_image_post)

        {
            finish();
            intent = new Intent(MainActivity.this, AddMediaActivity.class);
            intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
            intent.putExtra(Constants.KEY_GALLERY_TYPE, Constants.KEY_IMAGES);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_add_video_post)

        {
            finish();
            intent = new Intent(MainActivity.this, AddMediaActivity.class);
            intent.putExtra(Constants.KEY_GALLERY_TYPE, Constants.KEY_VIDEOS);
            intent.putExtra(Constants.IS_FROM_MEDIA_ACTIVITY, true);
            startActivity(intent);
        }

        if (v.getId() == R.id.layout_home)

        {
            Utils.debug(TAG, "Inside OnClick home");
            btn_home.setImageResource(R.drawable.home_selected);
            btn_profile.setImageResource(R.drawable.user_unselected);
            setHomeFragment();
            //new loaderHome().execute();
        } else if (v.getId() == R.id.layout_profile)

        {
            Utils.debug(TAG, "Inside OnClick profile");
            btn_home.setImageResource(R.drawable.home_unselected);
            btn_profile.setImageResource(R.drawable.user_selected);
            setUserProfileFragment();
            // new loaderPrfile().execute();

        } else if (v.getId() == R.id.ibtn_search)

        {
            intent = new Intent(this, ProfileActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (v.getId() == R.id.ibtn_setting)

        {
            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

    }

    private void postStory() {

        // set object to be shared
        Story.StoryObject storyObject = new Story.StoryObject.Builder()
                .setUrl("http://romkuapps.com/github/simple-facebook/object-apple.html")
                .setNoun("food")
                .build();

// set action to be done
        Story.StoryAction storyAction = new Story.StoryAction.Builder()
                .setAction("eat")
                .addProperty("taste", "sweet")
                .build();

// build story
        Story story = new Story.Builder()
                .setObject(storyObject)
                .setAction(storyAction)
                .build();


//        mSimpleFacebook.create(storyObject, new OnCreateStoryObject() {
//            @Override
//            public void onComplete(String response) {
//                super.onComplete(response);
//                Utils.debug(TAG, "response : " + response);
//            }
//        });
// publish
        mSimpleFacebook.publish(story, onPublishListener);


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
            isFabOpened = false;
        } else {
            btn_app_24.setImageResource(R.drawable.btn_app_24_close);
            isFabOpened = true;
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
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        if (v.getId() == R.id.btn_home) {
            Utils.debug(TAG, "Inside OnClick home");
            btn_home.setImageResource(R.drawable.home_selected);
            btn_profile.setImageResource(R.drawable.user_unselected);
            setHomeFragment();
        } else if (v.getId() == R.id.btn_profile) {
            Utils.debug(TAG, "Inside OnClick profile");
            btn_home.setImageResource(R.drawable.home_unselected);
            btn_profile.setImageResource(R.drawable.user_selected);
            setUserProfileFragment();
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    public static RelativeLayout getBottomLayout() {
        return layout;
    }

    public View getAppMenu() {
        return menuLayout;
    }
}
