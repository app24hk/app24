package app24.feedbook.hk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import app24.feedbook.hk.R;
import app24.feedbook.hk.utils.APIsConstants;
import app24.feedbook.hk.utils.AlertToastManager;
import app24.feedbook.hk.utils.AppController;
import app24.feedbook.hk.utils.Constants;
import app24.feedbook.hk.utils.Session;
import app24.feedbook.hk.utils.Utils;

import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import volley.Request;
import volley.VolleyError;
import volley.VolleyLog;
import volley.toolbox.StringRequest;

/**
 * Created by amritpal on 6/11/15.
 */
public class SettingsActivity extends BaseActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private RelativeLayout layout_paypal, layout_about, layout_logout;

//    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
//    // note that these credentials will differ between live & sandbox environments.
//    private static final String CONFIG_CLIENT_ID = "AcqYvFVkNOVGuh1hZVlQgzfavgG7UxLm22QBXXGXyiT-Gp7OK2kfT3bEOAirkc-ruokolbC34JDrCWsI";
//    private static final int REQUEST_CODE_PAYMENT = 1;
//    private static PayPalConfiguration config = new PayPalConfiguration()
//            .environment(CONFIG_ENVIRONMENT)
//            .clientId(CONFIG_CLIENT_ID);

    /* Volley Request Tags */
    private String res = "";
    private String tag_string_req = "feeds_req";
    /* End of Volley Request Tags */

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
                AlertToastManager.showToast(getResources().getString(R.string.logout), this);
                logout();
                LoginManager.getInstance().logOut();

                break;

        }
    }

    private boolean logout() {
        final SweetAlertDialog pd = Utils.showSweetProgressDialog(SettingsActivity.this,
                getResources
                        ().getString(R.string.progress_loading), SweetAlertDialog.PROGRESS_TYPE);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_LOGOUT,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.debug(TAG, response.toString());
                        Utils.closeSweetProgressDialog(SettingsActivity.this, pd);
                        res = response.toString();
                        try {
                            logoutResponse(res);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }, new volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeSweetProgressDialog(SettingsActivity.this, pd);
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                res = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
//                        user_id(int)
                Map<String, String> params = new HashMap<String, String>();
                Utils.debug("acc", new Utils().getSharedPreferences(SettingsActivity.this, Constants
                        .KEY_FACEBOOK_ACCESS_TOKEN, Constants.EMPTY));
                new Utils().getSharedPreferences(SettingsActivity.this, Constants
                        .KEY_FACEBOOK_ACCESS_TOKEN, Constants.EMPTY);
                params.put("user_deviceToken", new Utils().getSharedPreferences(SettingsActivity.this, Constants
                        .KEY_FACEBOOK_ACCESS_TOKEN, Constants.EMPTY));
                Utils.info("params...", params.toString());
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }

    private void logoutResponse(String res) throws JSONException {
        JSONObject jsonObject = new JSONObject(res);
        if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
            Session.logout(SettingsActivity.this);
        } else {
            try {
                Utils.debug(Constants.API_TAG, jsonObject.getString(APIsConstants.KEY_MESSAGE));
                Utils.showSweetProgressDialog(SettingsActivity.this, jsonObject.getString(APIsConstants
                        .KEY_MESSAGE), SweetAlertDialog.ERROR_TYPE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

//    public void onBuyPressed() {
//        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
//        Intent intent = new Intent(SettingsActivity.this, PaymentActivity.class);
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
//        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
//    }

//    private PayPalPayment getThingToBuy(String paymentIntent) {
//        return new PayPalPayment(new BigDecimal("1.75"), "USD", "Sample item",
//                paymentIntent);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_CODE_PAYMENT) {
//            if (resultCode == Activity.RESULT_OK) {
//                PaymentConfirmation confirm =
//                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
//                if (confirm != null) {
//                    try {
//                        Log.e("Show", confirm.toJSONObject().toString(4));
//                        Log.e("Show", confirm.getPayment().toJSONObject().toString(4));
//                        /**
//                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
//                         */
//                        Toast.makeText(getApplicationContext(), "PaymentConfirmation info received" +
//                                " from PayPal", Toast.LENGTH_LONG).show();
//                    } catch (JSONException e) {
//                        Toast.makeText(getApplicationContext(), "an extremely unlikely failure" +
//                                " occurred:", Toast.LENGTH_LONG).show();
//                    }
//                }
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText(getApplicationContext(), "The user canceled.", Toast.LENGTH_LONG).show();
//            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
//                Toast.makeText(getApplicationContext(), "An invalid Payment or PayPalConfiguration" +
//                        " was submitted. Please see the docs.", Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    @Override
    public void onDestroy() {
        // Stop service when done
//        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
