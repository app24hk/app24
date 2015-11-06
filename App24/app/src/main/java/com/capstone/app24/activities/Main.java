package com.capstone.app24.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.capstone.app24.R;

/**
 * Created by amritpal on 6/11/15.
 */
public class Main extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

    }

    public void login(View view) {
        Intent intent = new Intent(Main.this, MainActivity.class);
        startActivity(intent);
    }
}
