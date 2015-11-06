package com.capstone.app24.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.capstone.app24.R;

/**
 * Created by amritpal on 2/11/15.
 */
public class TermsNConditionsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_n_conditions);
        setHeader(getResources().getString(R.string.terms_of_use), true, false, false, false, false,
                null);
/*        WebView webview = (WebView) findViewById(R.id.webview);
        webview.loadUrl("http://www.example.com");*/
    }
}
