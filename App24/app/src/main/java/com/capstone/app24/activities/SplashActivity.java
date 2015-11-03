package com.capstone.app24.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.capstone.app24.R;

/**
 * Created by amritpal on 2/11/15.
 */
public class SplashActivity extends Activity implements View.OnClickListener {

    private TextView txt_terms_of_use;
    private Button btn_login_with_facebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initialize();
        setClickListeners();
        updateUI();
    }

    private void initialize() {
        txt_terms_of_use = (TextView) findViewById(R.id.txt_terms_of_use);
        btn_login_with_facebook = (Button) findViewById(R.id.btn_login_with_facebook);

    }

    private void setClickListeners() {
        txt_terms_of_use.setOnClickListener(this);
        btn_login_with_facebook.setOnClickListener(this);
    }

    private void updateUI() {
        SpannableString content = new SpannableString(getResources().getString(R.string.terms_of_use));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        txt_terms_of_use.append(content);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.txt_terms_of_use:
                intent = new Intent(SplashActivity.this, TermsNConditionsActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login_with_facebook:
                intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
