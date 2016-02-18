package com.capstone.app24.activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.capstone.app24.R;
import com.capstone.app24.utils.APIsConstants;
import com.capstone.app24.utils.AppController;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.NetworkUtils;
import com.capstone.app24.utils.Utils;

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
 * Created by amritpal on 16/11/15.
 */
public class PaypalActivity extends BaseActivity {

    private WebView webview;
    //    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
//    // note that these credentials will differ between live & sandbox environments.
//    private static final String CONFIG_CLIENT_ID = "your paypal id";
//    private static final int REQUEST_CODE_PAYMENT = 1;
//    private static PayPalConfiguration config = new PayPalConfiguration()
//            .environment(CONFIG_ENVIRONMENT)
//            .clientId(CONFIG_CLIENT_ID);
    private Button payPal;
    private EditText editText;
    private String TAG = PaypalActivity.class.getSimpleName();
    private SweetAlertDialog mDialog;
    /* Volley Request Tags */
    private String res = "";
    private String tag_string_req = "feeds_req";
    private int mPageNo = 1;
    private ProgressBar progress_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);
        setHeader("帳戶資料", true, false, false, false, false, "完成");

        initializeViews();
        setClickListeners();
        updateUI();
    }

    private void updateUI() {
        if (NetworkUtils.isOnline(this)) {
            getPaypalDetails();
        } else {
            Utils.showSweetProgressDialog(this, this.getResources().getString(R
                    .string.check_your_internet_connection), SweetAlertDialog.WARNING_TYPE);
        }
    }

    private void setClickListeners() {
        txt_save.setOnClickListener(this);
        editText.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    setPaypalDetails();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.txt_save:
                if (Utils.isValidEmail(editText.getText().toString().trim())) {
                    if (NetworkUtils.isOnline(this)) {
                        setPaypalDetails();
                    } else {
                        Utils.showSweetProgressDialog(this, this.getResources().getString(R
                                .string.check_your_internet_connection), SweetAlertDialog.WARNING_TYPE);
                    }
                } else {
                    editText.setError(getResources().getString(R.string.please_enter_valid_email));
                }
                break;
        }


//        new Utils(this).setPreferences(this, Constants.PAYPAL_EMAIL, editText.getText().toString().trim());
//        finish();
    }

    private void initializeViews() {
        editText = (EditText) findViewById(R.id.editText);
        txt_save = (TextView) findViewById(R.id.txt_save);
        progress_dialog = (ProgressBar) findViewById(R.id.progress_dialog);
    }

//    public void onBuyPressed() {
//        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
//        Intent intent = new Intent(PaypalActivity.this, PaymentActivity.class);
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
//        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
//    }

//    private PayPalPayment getThingToBuy(String paymentIntent) {
//        return new PayPalPayment(new BigDecimal("1.75"), "USD", "sample item",
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

//    @Override
//    public void onDestroy() {
//        // Stop service when done
//        stopService(new Intent(this, PayPalService.class));
//        super.onDestroy();
//    }

    public boolean getPaypalDetails() {
        progress_dialog.setVisibility(View.VISIBLE);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_GET_PAYPAL_INFO,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Utils.debug(TAG, response.toString());

                        res = response.toString();
                        try {
                            updateEmailBlock(res);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }, new volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mPageNo == 1)
                    Utils.closeSweetProgressDialog(PaypalActivity.this, mDialog);
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progress_dialog.setVisibility(View.GONE);
                res = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
//                        user_id(int)
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIsConstants.KEY_USER_ID, new Utils(PaypalActivity.this)
                        .getSharedPreferences
                                (PaypalActivity.this, Constants.KEY_USER_DETAILS, ""));

                Utils.info("params...", params.toString());
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }

    public boolean setPaypalDetails() {
        mDialog = Utils.showSweetProgressDialog(PaypalActivity.this,
                getResources
                        ().getString(R.string.progress_loading), SweetAlertDialog.PROGRESS_TYPE);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_ADD_EDIT_PAYPAL_INFO,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Utils.debug(TAG, response.toString());

                        res = response.toString();
                        try {
                            handlePaypalDetails(res);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeSweetProgressDialog(PaypalActivity.this, mDialog);
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                res = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIsConstants.KEY_USER_ID, new Utils(PaypalActivity.this)
                        .getSharedPreferences
                                (PaypalActivity.this, Constants.KEY_USER_DETAILS, ""));
                params.put(APIsConstants.KEY_PAYPAL_EMAIL, editText.getText().toString().trim());
                Utils.info("params...", params.toString());
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }

    private void updateEmailBlock(String res) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            try {
                if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject(APIsConstants.PAYPAL_INFO);
                    if (!jsonObject1.getString(APIsConstants.PAYPAL_EMAIL).equalsIgnoreCase
                            (Constants.EMPTY))
                        editText.setText(jsonObject1.getString(APIsConstants.PAYPAL_EMAIL));
                    progress_dialog.setVisibility(View.GONE);

                } else {
                    progress_dialog.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void handlePaypalDetails(String res) {
        JSONObject jsonObject = null;
        Utils.closeSweetProgressDialog(PaypalActivity.this, mDialog);
        try {
            jsonObject = new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            try {
                if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
//                    if (jsonObject.getString(APIsConstants
//                            .KEY_MESSAGE).contains(Constants.UPDATED)) {
                    mDialog = Utils.showSweetProgressDialog(this, getResources().getString(R
                                    .string.paypal_info_updated_successfully),
                            SweetAlertDialog
                                    .SUCCESS_TYPE);
                    mDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Utils.closeSweetProgressDialog(PaypalActivity.this, mDialog);
                            finish();
                        }
                    });
//                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void loadWebViewLoad(WebView webview, String url) {
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setSupportMultipleWindows(true);
        webview.setWebViewClient(new WebViewClient());
        webview.setWebChromeClient(new WebChromeClient());
        webview.loadUrl(url);

    }
}
