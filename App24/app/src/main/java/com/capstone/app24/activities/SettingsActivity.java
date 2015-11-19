package com.capstone.app24.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.capstone.app24.R;
import com.capstone.app24.utils.AlertToastManager;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.Utils;
import com.facebook.login.widget.LoginButton;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

/**
 * Created by amritpal on 6/11/15.
 */
public class SettingsActivity extends BaseActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private RelativeLayout layout_paypal, layout_about, layout_logout;

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AcqYvFVkNOVGuh1hZVlQgzfavgG7UxLm22QBXXGXyiT-Gp7OK2kfT3bEOAirkc-ruokolbC34JDrCWsI";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID);

    private LoginButton fb_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initializeViews();
        setClickListeners();
    }

    private void initializeViews() {
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        layout_paypal = (RelativeLayout) findViewById(R.id.layout_paypal);
        layout_about = (RelativeLayout) findViewById(R.id.layout_about);
        layout_logout = (RelativeLayout) findViewById(R.id.layout_logout);
        fb_btn = (LoginButton) findViewById(R.id.login_button);
    }

    private void setClickListeners() {
        ibtn_back.setOnClickListener(this);
        layout_paypal.setOnClickListener(this);
        layout_about.setOnClickListener(this);
        layout_logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.layout_paypal:
                // AlertToastManager.showToast("Paypal ", this);

//                intent = new Intent(this, PayPalService.class);
//                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//                startService(intent);
//
//                onBuyPressed();
                intent = new Intent(this, PaypalActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_logout:
                AlertToastManager.showToast("Logout ", this);
                /*if (!new Utils(this).getSharedPreferences(this, Constants.KEY_IS_LOGGED_IN)) {
                    Utils.debug(TAG, "Inside If Condition");
                    fb_btn.performClick();
                    finish();
                } else {
                    AlertToastManager.showToast("You are not currently logged in", context);
                }*/

                break;

        }
    }

    public void onBuyPressed() {
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(SettingsActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {
        return new PayPalPayment(new BigDecimal("1.75"), "USD", "Sample item",
                paymentIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.e("Show", confirm.toJSONObject().toString(4));
                        Log.e("Show", confirm.getPayment().toJSONObject().toString(4));
                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         */
                        Toast.makeText(getApplicationContext(), "PaymentConfirmation info received" +
                                " from PayPal", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "an extremely unlikely failure" +
                                " occurred:", Toast.LENGTH_LONG).show();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "The user canceled.", Toast.LENGTH_LONG).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(getApplicationContext(), "An invalid Payment or PayPalConfiguration" +
                        " was submitted. Please see the docs.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
