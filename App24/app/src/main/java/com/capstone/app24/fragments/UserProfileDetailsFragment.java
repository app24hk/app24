package com.capstone.app24.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.capstone.app24.R;
import com.capstone.app24.activities.MainActivity;
import com.capstone.app24.activities.ProfileActivity;
import com.capstone.app24.activities.SettingsActivity;
import com.capstone.app24.adapters.LatestFeedsAdapter;
import com.capstone.app24.adapters.UserProfitAdapter;
import com.capstone.app24.animations.HidingScrollListener;
import com.capstone.app24.custom.CustomDrawablEditText;
import com.capstone.app24.sliding_tabs.SlidingTabLayout;
import com.capstone.app24.utils.Utils;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amritpal on 6/11/15.
 */
public class UserProfileDetailsFragment extends Fragment implements View.OnClickListener,
        TextWatcher, View.OnFocusChangeListener, CustomDrawablEditText.OnDrawableClickListener {
    private static final String TAG = UserProfileDetailsFragment.class.getSimpleName();
    View mView;
    private ImageButton ibtn_setting, ibtn_search;
    SweetAlertDialog dialog;
    private RecyclerView user_feeds_list;
    private UserProfitAdapter mLatestFeedsAdapter;
    private CustomDrawablEditText edit_search;
    private View layout_user_profle;

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
        mLatestFeedsAdapter = new UserProfitAdapter(getActivity());
        user_feeds_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        user_feeds_list.setAdapter(mLatestFeedsAdapter);
    }


    private void setClickListeners() {
        ibtn_setting.setOnClickListener(this);
        ibtn_search.setOnClickListener(this);






       /* edit_search = (EditText) mView.findViewById(R.id.edit_search);
        edit_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    AlertToastManager.showToast("Has Focus", getActivity());
                } else {
                    AlertToastManager.showToast("Has No Focus", getActivity());
                }
            }
        });*/
        /*edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //performSearch();
                    AlertToastManager.showToast("Has Focus", getActivity());

                    return true;
                }
                return false;
            }
        });*/
    }

    private void initializeViews() {
        ibtn_search = (ImageButton) mView.findViewById(R.id.ibtn_search);
        ibtn_setting = (ImageButton) mView.findViewById(R.id.ibtn_setting);
        user_feeds_list = (RecyclerView) mView.findViewById(R.id.user_feeds_list);
        //edit_search = (EditText) mView.findViewById(R.id.edit_search);
        edit_search = (CustomDrawablEditText) mView.findViewById(R.id.edit_search);
        edit_search.addTextChangedListener(this);
        edit_search.setOnDrawableClickListener(this);
        //edit_search.setOnDrawa
        edit_search.setOnFocusChangeListener(this);
        layout_user_profle = (View) mView.findViewById(R.id.layout_user_profle);
        initRecyclerView();

    }

    private void initRecyclerView() {
        user_feeds_list.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                Utils.debug(TAG, "Scrolling up");
                //Utils.setScrollDirection(Constants.SCROLL_UP);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) MainActivity.getBottomLayout().getLayoutParams();
                int fabBottomMargin = lp.bottomMargin;
                MainActivity.getBottomLayout().animate().translationY(MainActivity.getBottomLayout().getHeight() + fabBottomMargin + 100)
                        .setInterpolator(new AccelerateInterpolator(2)).start();

                // SlidingTabLayout slidingTabLayout = HomeFragment.getHeaderView();
                LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) layout_user_profle.getLayoutParams();
                int fabTopMargin = lp1.topMargin;
                layout_user_profle.animate().translationY(layout_user_profle.getHeight() + fabTopMargin - 200).setInterpolator(new
                        AccelerateInterpolator(2)).start();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(80);
                            layout_user_profle.setVisibility(View.GONE);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onShow() {
                Utils.debug(TAG, "Scrolling Down");

                MainActivity.getBottomLayout().animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                //SlidingTabLayout slidingTabLayout = HomeFragment.getHeaderView();
                layout_user_profle.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start(); // Utils.setScrollDirection(Constants.SCROLL_DOWN);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(80);
                            layout_user_profle.setVisibility(View.VISIBLE);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ibtn_search:
                intent = new Intent(getActivity(), ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(edit_search.getText().toString().trim())) {
            edit_search.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(
                    R.drawable.search), null, null, null);
        } else {
            edit_search.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(
                    R.drawable.search), null, null, null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public void onDrawableClick(CustomDrawablEditText.DrawablePosition position) {
        switch (position) {
            case BOTTOM:
                System.out.println("bottom click");
                break;
            case LEFT:
                System.out.println("left click");
                break;
            case RIGHT:
                edit_search.setText("");
                break;
            case TOP:
                System.out.println("top click");
                break;
        }
    }
}
