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
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.app24.R;
import com.capstone.app24.animations.AnimatorUtils;
import com.capstone.app24.bean.UserFeedModel;
import com.capstone.app24.fragments.HomeFragment;
import com.capstone.app24.fragments.UserProfileDetailsFragment;
import com.capstone.app24.interfaces.OnListUpdateListener;
import com.capstone.app24.receiver.AlarmReceiver;
import com.capstone.app24.sliding_tabs.SlidingTabLayout;
import com.capstone.app24.utils.APIsConstants;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.InterfaceListener;
import com.capstone.app24.utils.Utils;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.ogaclejapan.arclayout.ArcLayout;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.listeners.OnPublishListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

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
    private ArrayList<UserFeedModel> userFeedList = new ArrayList<>();
    private static TextView txt_profile_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fb_btn = (LoginButton) findViewById(R.id.login_button);
        initializeViews();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        Permission[] permissions = new Permission[]{
                Permission.USER_PHOTOS,
                Permission.EMAIL,
                Permission.PUBLISH_ACTION
        };

        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId("1673090976236867")
                .setNamespace("com_capstone_app")
                .setPermissions(permissions)
                .build();
        SimpleFacebook.setConfiguration(configuration);


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
        txt_profile_header = (TextView) findViewById(R.id.txt_profile_header);
        txt_profile_header.setSelected(true);  // Set focus to the textview
        txt_profile_header.setEllipsize(TextUtils.TruncateAt.MARQUEE);


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
            startActivity(intent);
        } else if (v.getId() == R.id.ibtn_setting)

        {
            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

    }

    private void postStory() {

     /*   ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type", "food.food")//613292//
                .putString("og:title", "A Game of Thrones")
                .putString("og:description", "In the frozen wastes to the north of Winterfell, sinister and supernatural forces are mustering.")
                .build();

        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("eat:food")
                .putObject("food", object)
                .build();*/

//        Feed feed = new Feed.Builder()
//                .setMessage("Clone it out...")
//                .setName("Simple Facebook SDK for Android")
//                .setCaption("Code less, do the same.")
//                .setDescription("Login, publish feeds and stories, invite friends and more...")
//                .setPicture("https://raw.github.com/sromku/android-simple-facebook/master/Refs/android_facebook_sdk_logo.png")
//                .setLink("https://github.com/sromku/android-simple-facebook")
//                .addAction("Clone", "https://github.com/sromku/android-simple-facebook")
//                .addProperty("Full documentation", "http://sromku.github.io/android-simple-facebook", "http://sromku.github.io/android-simple-facebook")
//                .addProperty("Stars", "14")
//                .build();
//        mSimpleFacebook.publish(feed, onPublishListener);
//

        /*ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("food")
                .setAction(action)
                .build();

        ShareDialog.show(this, content);

        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Utils.debug("fb", "facebook Result  : " + result);
                Utils.debug("fb", "facebook Post Id  : " + result.getPostId());

            }

            @Override
            public void onCancel() {
                Utils.debug("fb", "facebook Cancel  : ");
            }

            @Override
            public void onError(FacebookException error) {
                Utils.debug("fb", "facebook Error  : " + error);
            }
        });*/

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
        TimeZone tz = TimeZone.getDefault();
        Utils.debug("timezone", "TimeZone   " + tz.getDisplayName(false, TimeZone.SHORT) + " " +
                "Timezon id :: "
                + tz
                .getID());

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


        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_USER_NAME, txt_profile_header.getText().toString().trim());
        userProfileDetailsFragment = UserProfileDetailsFragment.newInstance();
        userProfileDetailsFragment.setArguments(bundle);
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

    public static void setUsername(String username) {
        txt_profile_header.setText(username);
    }

    public static RelativeLayout getBottomLayout() {
        return layout;
    }

    public View getAppMenu() {
        return menuLayout;
    }


}
