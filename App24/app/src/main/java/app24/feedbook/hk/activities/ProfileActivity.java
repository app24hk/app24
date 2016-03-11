package app24.feedbook.hk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import app24.feedbook.hk.R;
import app24.feedbook.hk.adapters.ViewPagerAdapterProfile;
import app24.feedbook.hk.bean.UserFeedModel;
import app24.feedbook.hk.custom.CustomDrawablEditText;
import app24.feedbook.hk.sliding_tabs.SlidingTabLayout;
import app24.feedbook.hk.utils.SearchType;
import app24.feedbook.hk.utils.Utils;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amritpal on 5/11/15.
 */
public class ProfileActivity extends FragmentActivity implements View.OnClickListener {
    private String TAG = ProfileActivity.class.getSimpleName();
    View mView;
    private ViewPager pager;
    private SlidingTabLayout tabs;
    ViewPagerAdapterProfile adapter_profile;
    CharSequence profile_titles[] = {"作者", "標題"};
    ImageButton ibtn_search, ibtn_settings;

    private CustomDrawablEditText editSearch;
    private TextView cancel;
    private TextView txt_profile_header;
    // public ArrayList<UserFeedModel> mMainList = new ArrayList<>();
    public static SearchType mSearchType = SearchType.USER;
    /* Volley Request Tags */
    //    private SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<UserFeedModel> latestFeedList = new ArrayList<>();
    /* End of Volley Request Tags */
    private SweetAlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        initializeViews();
        setClickListeners();
        updateUI();
    }

    private void updateUI() {
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter_profile = new ViewPagerAdapterProfile(getSupportFragmentManager(), profile_titles,
                profile_titles.length, latestFeedList, editSearch.getText().toString().trim());
        pager.setAdapter(adapter_profile);
        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setViewPager(pager);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSearchType(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private SearchType getSearchType(int id) {
        switch (id) {
            case 0:
                mSearchType = SearchType.USER;
                break;
            case 1:
                mSearchType = SearchType.POST;
                break;
        }
        return mSearchType;

    }

    private void initializeViews() {
        pager = (ViewPager) findViewById(R.id.pager);

        ibtn_settings = (ImageButton) findViewById(R.id.ibtn_setting);
        ibtn_settings.setVisibility(View.GONE);

        ibtn_search = (ImageButton) findViewById(R.id.ibtn_search);
        ibtn_search.setVisibility(View.GONE);

        editSearch = (CustomDrawablEditText) findViewById(R.id.edit_search);
        editSearch.setVisibility(View.VISIBLE);

        cancel = (TextView) findViewById(R.id.cancel);
        cancel.setVisibility(View.VISIBLE);

        txt_profile_header = (TextView) findViewById(R.id.txt_profile_header);
        txt_profile_header.setVisibility(View.GONE);

        ViewGroup.LayoutParams params = pager.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        pager.setLayoutParams(params);

    }


    private void setClickListeners() {
        cancel.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                Utils.hideKeyboard(this);
                finish();
                break;
        }
    }


}
