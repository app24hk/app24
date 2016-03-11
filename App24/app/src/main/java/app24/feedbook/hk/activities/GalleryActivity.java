package app24.feedbook.hk.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import app24.feedbook.hk.adapters.GalleryPagerAdapter;
import app24.feedbook.hk.R;

/**
 * Created by amritpal on 6/11/15.
 */
public class GalleryActivity extends FragmentActivity {
    private GalleryPagerAdapter mPagerAdapter;
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }
}
